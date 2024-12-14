package quiz.application;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Login extends JFrame implements ActionListener {

    JButton actionButton;
    JTextField tfUsername;
    JPasswordField tfPassword;
    JLabel usernameLabel, passwordLabel;

    Login() {
        getContentPane().setBackground(Color.WHITE);
        setLayout(null);

        JLabel heading = new JLabel("Admin Login");
        heading.setBounds(120, 60, 300, 45);
        heading.setFont(new Font("", Font.BOLD, 40));
        heading.setForeground(new Color(30, 144, 254));
        add(heading);

        // Label for Username Field
        usernameLabel = new JLabel("Username:");
        usernameLabel.setBounds(100, 125, 300, 25);
        usernameLabel.setFont(new Font("Times New Roman", Font.BOLD, 18));
        add(usernameLabel);

        // Username Field
        tfUsername = new JTextField();
        tfUsername.setBounds(100, 150, 300, 25);
        tfUsername.setFont(new Font("Times New Roman", Font.BOLD, 20));
        add(tfUsername);

        // Label for Password Field
        passwordLabel = new JLabel("Password:");
        passwordLabel.setBounds(100, 200, 300, 25);
        passwordLabel.setFont(new Font("Times New Roman", Font.BOLD, 18));
        add(passwordLabel);

        // Password Field
        tfPassword = new JPasswordField();
        tfPassword.setBounds(100, 225, 300, 25);
        tfPassword.setFont(new Font("Times New Roman", Font.BOLD, 20));
        add(tfPassword);

        actionButton = new JButton("Login");
        actionButton.setBounds(185, 300, 120, 25);
        actionButton.setBackground(new Color(30, 144, 254));
        actionButton.setForeground(Color.WHITE);
        actionButton.addActionListener(this);
        add(actionButton);

        setSize(500, 500);
        setResizable(false);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == actionButton) {
            String username = tfUsername.getText();
            String password = new String(tfPassword.getPassword());

            if (username.equals("admin") && password.equals("123456789")) {
                JOptionPane.showMessageDialog(this, "Login successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                // Close the Login screen
                this.setVisible(false);  // Hide the Login screen

                // Open the Admin GUI
                new Admin();  // Display the Admin screen
            } else {
                JOptionPane.showMessageDialog(this, "Invalid credentials. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }


    public static void main(String[] args) {
        new Login();
    }
}
