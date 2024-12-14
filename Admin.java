package quiz.application;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class Admin extends JFrame implements ActionListener {

    JTable table;
    DefaultTableModel tableModel;
    JButton addQuestionButton, startButton;
    JTextField numQuizzesTextField;
    Connection conn;

    Admin() {
        getContentPane().setBackground(Color.WHITE);
        setLayout(new GridBagLayout()); // Use GridBagLayout for flexible control over the layout

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel heading = new JLabel("Question Bank", JLabel.CENTER);
        heading.setFont(new Font("", Font.BOLD, 40));
        heading.setForeground(new Color(30, 144, 254));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 20;
        add(heading, gbc);

        // Table setup
        tableModel = new DefaultTableModel(new String[]{"ID", "Question", "Option 1", "Option 2", "Option 3", "Option 4", "Answer", "Actions"}, 0);
        table = new JTable(tableModel) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 7; // Only the "Actions" column is editable for the button
            }
        };
        JScrollPane scrollPane = new JScrollPane(table);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.weighty = 0.9; // 90% of the height for the table
        gbc.fill = GridBagConstraints.BOTH;
        add(scrollPane, gbc);

        // Number of quizzes section
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.add(new JLabel("Number of quizzes:"));
        numQuizzesTextField = new JTextField(5); // Set width to accommodate the number
        numQuizzesTextField.setHorizontalAlignment(JTextField.RIGHT);
        numQuizzesTextField.setInputVerifier(new InputVerifier() { // Allow only numbers
            @Override
            public boolean verify(JComponent input) {
                String text = ((JTextField) input).getText();
                try {
                    Integer.parseInt(text); // Only numbers are allowed
                    return true;
                } catch (NumberFormatException e) {
                    return false;
                }
            }
        });
        bottomPanel.add(numQuizzesTextField);

        startButton = new JButton("Start");
        startButton.addActionListener(this);
        bottomPanel.add(startButton);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.weighty = 0.1; // 10% of the height for the bottom section
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(bottomPanel, gbc);

        // Add Question Button
        addQuestionButton = new JButton("Add Question");
        addQuestionButton.setBackground(new Color(30, 144, 254));
        addQuestionButton.setForeground(Color.WHITE);
        addQuestionButton.addActionListener(this);
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        add(addQuestionButton, gbc);

        setSize(1200, 600);
        setResizable(false);
        setLocationRelativeTo(null);
        setVisible(true);

        // Initialize database connection and populate table
        connectToDatabase();
        populateTable();
    }

    private void connectToDatabase() {
        try {
            conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/quizzes", "postgres", "123");
        } catch (SQLException e) {
            System.out.println(e);
            JOptionPane.showMessageDialog(this, "Database connection failed: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }

    private void populateTable() {
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM qb")) {
            while (rs.next()) {
                int id = rs.getInt("id");
                String question = rs.getString("questions");
                String option0 = rs.getString("option0");
                String option1 = rs.getString("option1");
                String option2 = rs.getString("option2");
                String option3 = rs.getString("option3");
                int answer = rs.getInt("answer");

                JButton deleteButton = new JButton("Delete");
                deleteButton.setBackground(Color.RED);
                deleteButton.setForeground(Color.WHITE);
                deleteButton.addActionListener(e -> deleteQuestion(id));

                tableModel.addRow(new Object[]{id, question, option0, option1, option2, option3, answer, deleteButton});
            }
        }catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to retrieve questions: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteQuestion(int id) {
        try (PreparedStatement pstmt = conn.prepareStatement("DELETE FROM qb WHERE id = ?")) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Question deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            refreshTable();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to delete question: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void refreshTable() {
        tableModel.setRowCount(0);
        populateTable();
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == addQuestionButton) {
            new AddQuestionDialog(this, conn);
        } else if (ae.getSource() == startButton) {
            String numQuizzes = numQuizzesTextField.getText();
            JOptionPane.showMessageDialog(this, "Starting quizzes: " + numQuizzes, "Info", JOptionPane.INFORMATION_MESSAGE);

            for(int i=1 ; i<=Integer.parseInt(numQuizzes);i++){
                String name = "user_" + i;
                Thread thread = new Thread(new Quiz("name"),name);
                thread.start();
            }
        }
    }

    public static void main(String[] args) {
        new Admin();
    }
}

class AddQuestionDialog extends JDialog implements ActionListener {

    JTextField tfQuestion, tfOption0, tfOption1, tfOption2, tfOption3;
    JComboBox<String> cbAnswer;
    JButton submitButton;
    Connection conn;

    AddQuestionDialog(JFrame parent, Connection conn) {
        super(parent, "Add Question", true);
        this.conn = conn;
        setLayout(new GridLayout(7, 1, 10, 10)); // Stack inputs vertically

        add(new JLabel("Question:"));
        tfQuestion = new JTextField();
        add(tfQuestion);

        add(new JLabel("Option 1:"));
        tfOption0 = new JTextField();
        add(tfOption0);

        add(new JLabel("Option 2:"));
        tfOption1 = new JTextField();
        add(tfOption1);

        add(new JLabel("Option 3:"));
        tfOption2 = new JTextField();
        add(tfOption2);

        add(new JLabel("Option 4:"));
        tfOption3 = new JTextField();
        add(tfOption3);

        add(new JLabel("Correct Answer (0-3):"));
        cbAnswer = new JComboBox<>(new String[]{"0", "1", "2", "3"});
        add(cbAnswer);

        submitButton = new JButton("Submit");
        submitButton.addActionListener(this);
        add(submitButton);

        setSize(600, 300);
        setLocationRelativeTo(parent);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == submitButton) {
            String question = tfQuestion.getText();
            String option0 = tfOption0.getText();
            String option1 = tfOption1.getText();
            String option2 = tfOption2.getText();
            String option3 = tfOption3.getText();
            int answer = Integer.parseInt((String) cbAnswer.getSelectedItem());

            try (PreparedStatement pstmt = conn.prepareStatement("INSERT INTO qb (questions, option0, option1, option2, option3, answer) VALUES (?, ?, ?, ?, ?, ?)")) {
                pstmt.setString(1, question);
                pstmt.setString(2, option0);
                pstmt.setString(3, option1);
                pstmt.setString(4, option2);
                pstmt.setString(5, option3);
                pstmt.setInt(6, answer);
                pstmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "Question added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);

                // Notify the parent to refresh the table content
                ((Admin) getParent()).refreshTable();

                dispose(); // Close the dialog
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Failed to add question: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
