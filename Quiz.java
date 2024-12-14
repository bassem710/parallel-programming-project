package quiz.application;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.ArrayList;

public class Quiz extends JFrame implements ActionListener, Runnable {

    ArrayList<ArrayList<String>> questions = new ArrayList<>();
    ArrayList<String> answers = new ArrayList<>();
    String useranswers[][] = new String[10][1];
    JLabel qno, question, timeLabel, scoreLabel;
    JRadioButton opt1, opt2, opt3, opt4;
    ButtonGroup groupoptions;
    JButton next, submit, lifeline;
    Connection conn;

    public int timer = 10; // Total quiz time in seconds
    public int ans_given = 0;
    public int count = 0;
    public int score = 0;
    Object obj = new Object();
    boolean endFlag = false;
    boolean quizEnded = false; // Prevent duplicate execution of endQuiz()

    String name;

    private void connectToDatabase() {
        try {
            conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/quizzes", "postgres", "123456");
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
                ArrayList<String> stringList = new ArrayList<>();
                stringList.add(rs.getString("questions"));
                stringList.add(rs.getString("option0"));
                stringList.add(rs.getString("option1"));
                stringList.add(rs.getString("option2"));
                stringList.add(rs.getString("option3"));

                // Store the correct option text
                answers.add(rs.getString("option" + rs.getInt("answer")));
                questions.add(stringList);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to retrieve questions: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean timerStarted = false; // Ensure only one timer thread runs

    // Constructor
    public Quiz(String name) {
        this.name = name;
        setBounds(50, 0, 400, 400);
        getContentPane().setBackground(Color.WHITE);
        setLayout(null);

        connectToDatabase();
        populateTable();

        // Add all components
        qno = new JLabel();
        qno.setBounds(20, 20, 50, 30);
        qno.setFont(new Font("Tahoma", Font.PLAIN, 14));
        add(qno);

        question = new JLabel();
        question.setBounds(80, 20, 300, 30);
        question.setFont(new Font("Tahoma", Font.PLAIN, 14));
        add(question);

        opt1 = new JRadioButton();
        opt1.setBounds(20, 60, 350, 30);
        opt1.setBackground(Color.WHITE);
        opt1.setFont(new Font("Dialog", Font.PLAIN, 12));
        add(opt1);

        opt2 = new JRadioButton();
        opt2.setBounds(20, 100, 350, 30);
        opt2.setBackground(Color.WHITE);
        opt2.setFont(new Font("Dialog", Font.PLAIN, 12));
        add(opt2);

        opt3 = new JRadioButton();
        opt3.setBounds(20, 140, 350, 30);
        opt3.setBackground(Color.WHITE);
        opt3.setFont(new Font("Dialog", Font.PLAIN, 12));
        add(opt3);

        opt4 = new JRadioButton();
        opt4.setBounds(20, 180, 350, 30);
        opt4.setBackground(Color.WHITE);
        opt4.setFont(new Font("Dialog", Font.PLAIN, 12));
        add(opt4);

        groupoptions = new ButtonGroup();
        groupoptions.add(opt1);
        groupoptions.add(opt2);
        groupoptions.add(opt3);
        groupoptions.add(opt4);

        next = new JButton("Next");
        next.setBounds(20, 220, 100, 30);
        next.setFont(new Font("Tahoma", Font.PLAIN, 12));
        next.setBackground(new Color(30, 144, 255));
        next.setForeground(Color.WHITE);
        next.addActionListener(this);
        add(next);

        lifeline = new JButton("50-50");
        lifeline.setBounds(130, 220, 100, 30);
        lifeline.setFont(new Font("Tahoma", Font.PLAIN, 12));
        lifeline.setBackground(new Color(30, 144, 255));
        lifeline.setForeground(Color.WHITE);
        lifeline.addActionListener(this);
        add(lifeline);

        submit = new JButton("Submit");
        submit.setBounds(240, 220, 100, 30);
        submit.setFont(new Font("Tahoma", Font.PLAIN, 12));
        submit.setBackground(new Color(30, 144, 255));
        submit.setForeground(Color.WHITE);
        submit.addActionListener(this);
        submit.setEnabled(false);
        add(submit);

        timeLabel = new JLabel("Time left: " + timer + " seconds");
        timeLabel.setBounds(20, 290, 200, 30);
        timeLabel.setFont(new Font("Tahoma", Font.BOLD, 14));
        add(timeLabel);

        scoreLabel = new JLabel("Score: " + score);
        scoreLabel.setBounds(20, 270, 200, 30);
        scoreLabel.setFont(new Font("Tahoma", Font.BOLD, 14));
        add(scoreLabel);

        start(count);

        setVisible(true);

        // Start the timer safely
        SwingUtilities.invokeLater(() -> startTimer());
    }

    private void startTimer() {
        if (!timerStarted) { // Ensure only one thread is created
            timerStarted = true;
            new Thread(this).start();
        }
    }

    @Override
    public void run() {
        while (!endFlag) {
            while (timer > 0) {
                try {
                    synchronized (obj) {
                        Thread.sleep(1000); // Wait for 1 second
                        timer--; // Decrease the timer by 1 second

                        // Update time and score safely on the Event Dispatch Thread (EDT)
                        SwingUtilities.invokeLater(() -> {
                            timeLabel.setText("Time left: " + timer + " seconds");
                            scoreLabel.setText("Score: " + score);
                        });
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            endQuiz(); // End quiz when the timer reaches 0
        }
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == next) {
            ans_given = 1;
            if (groupoptions.getSelection() == null) {
                useranswers[count][0] = "";
            } else {
                useranswers[count][0] = groupoptions.getSelection().getActionCommand();
            }

            if (useranswers[count][0].equals(answers.get(count))) {
                score += 1;
            }

            count++;

            if (count == 9) {
                next.setEnabled(false);
                submit.setEnabled(true);
            }

            start(count); // Show the next question
        } else if (ae.getSource() == lifeline) {
            lifeline.setEnabled(false);
        } else if (ae.getSource() == submit) {
            if (groupoptions.getSelection() == null) {
                useranswers[count][0] = "";
            } else {
                useranswers[count][0] = groupoptions.getSelection().getActionCommand();
            }

            if (useranswers[count][0].equals(answers.get(count))) {
                score += 1;
            }
            endQuiz();
        }
    }

    private void endQuiz() {
        if (quizEnded) return; // Prevent duplicate execution
        quizEnded = true;

        next.setEnabled(false);
        submit.setEnabled(false);
        endFlag = true;

        JOptionPane.showMessageDialog(this,
                "Quiz completed!\nFinal Score: " + score,
                "Quiz Result",
                JOptionPane.INFORMATION_MESSAGE);

        dispose();
    }

    public void start(int count) {
        qno.setText("" + (count + 1) + ". ");
        question.setText(questions.get(count).get(0));
        opt1.setText(questions.get(count).get(1));
        opt1.setActionCommand(questions.get(count).get(1));

        opt2.setText(questions.get(count).get(2));
        opt2.setActionCommand(questions.get(count).get(2));

        opt3.setText(questions.get(count).get(3));
        opt3.setActionCommand(questions.get(count).get(3));

        opt4.setText(questions.get(count).get(4));
        opt4.setActionCommand(questions.get(count).get(4));

        groupoptions.clearSelection();
    }

    public static void main(String[] args) {
        new Quiz("User");
    }
}