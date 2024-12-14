package quiz.application;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Login extends JFrame implements ActionListener {

    JButton toggleAdmin, actionButton;
    JTextField tfName, tfUsername;
    JPasswordField tfPassword;
    JLabel nameLabel, usernameLabel, passwordLabel;
    boolean isAdmin = false;

    Login() {
        getContentPane().setBackground(Color.WHITE);
        setLayout(null);

        JLabel heading = new JLabel("Online Quizzes");
        heading.setBounds(100, 60, 300, 45);
        heading.setFont(new Font("Viner Hand ITC", Font.BOLD, 40));
        heading.setForeground(new Color(30, 144, 254));
        add(heading);

        // Label for Name Field
        nameLabel = new JLabel("Enter your name:");
        nameLabel.setBounds(100, 175, 300, 25);
        nameLabel.setFont(new Font("Times New Roman", Font.BOLD, 18));
        nameLabel.setVisible(true);
        add(nameLabel);

        // Name Field (default view for non-admin)
        tfName = new JTextField();
        tfName.setBounds(100, 200, 300, 25);
        tfName.setFont(new Font("Times New Roman", Font.BOLD, 20));
        add(tfName);

        // Label for Username Field
        usernameLabel = new JLabel("Username:");
        usernameLabel.setBounds(100, 125, 300, 25);
        usernameLabel.setFont(new Font("Times New Roman", Font.BOLD, 18));
        usernameLabel.setVisible(false);
        add(usernameLabel);

        // Username Field (hidden by default)
        tfUsername = new JTextField();
        tfUsername.setBounds(100, 150, 300, 25);
        tfUsername.setFont(new Font("Times New Roman", Font.BOLD, 20));
        tfUsername.setVisible(false);
        add(tfUsername);

        // Label for Password Field
        passwordLabel = new JLabel("Password:");
        passwordLabel.setBounds(100, 175, 300, 25);
        passwordLabel.setFont(new Font("Times New Roman", Font.BOLD, 18));
        passwordLabel.setVisible(false);
        add(passwordLabel);

        // Password Field (hidden by default)
        tfPassword = new JPasswordField();
        tfPassword.setBounds(100, 200, 300, 25);
        tfPassword.setFont(new Font("Times New Roman", Font.BOLD, 20));
        tfPassword.setVisible(false);
        add(tfPassword);

        actionButton = new JButton("Take a Quiz");
        actionButton.setBounds(100, 270, 120, 25);
        actionButton.setBackground(new Color(30, 144, 254));
        actionButton.setForeground(Color.WHITE);
        actionButton.addActionListener(this);
        add(actionButton);

        toggleAdmin = new JButton("Admin");
        toggleAdmin.setBounds(285, 270, 120, 25);
        toggleAdmin.setBackground(new Color(30, 144, 254));
        toggleAdmin.setForeground(Color.WHITE);
        toggleAdmin.addActionListener(this);
        add(toggleAdmin);

        setSize(500, 500);
        setResizable(false);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == actionButton) {
            if (!isAdmin) {
                String name = tfName.getText();
                if (name.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Please enter your name.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                setVisible(false);
                new Rules(name);
            } else {
                String username = tfUsername.getText();
                String password = new String(tfPassword.getPassword());
                if (username.equals("admin") && password.equals("123456789")) {
                    JOptionPane.showMessageDialog(this, "Login successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid credentials. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else if (ae.getSource() == toggleAdmin) {
            isAdmin = !isAdmin;
            toggleAdmin.setText(isAdmin ? "User" : "Admin");

            // Toggle visibility of components based on isAdmin
            tfName.setVisible(!isAdmin);
            nameLabel.setVisible(!isAdmin);
            tfUsername.setVisible(isAdmin);
            usernameLabel.setVisible(isAdmin);
            tfPassword.setVisible(isAdmin);
            passwordLabel.setVisible(isAdmin);
            actionButton.setText(isAdmin ? "Login" : "Rules");
        }
    }

    public static void main(String[] args) {
        new Login();
    }
}
