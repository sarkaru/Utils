package utp.utils.siteblocker;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.time.LocalDate;

public class SiteBlockerGUI extends JFrame {

    private JTextField siteNameField;
    private JPasswordField sudoPasswordField;
    private JTextField pinCodeField;
    private JRadioButton blockRadio;
    private JRadioButton unblockRadio;
    private JTextArea resultArea;

    public SiteBlockerGUI() {
        setTitle("Site Blocker");
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(8, 2));

        JLabel siteNameLabel = new JLabel("Site Name:");
        siteNameField = new JTextField();

        JLabel blockLabel = new JLabel("Block/Unblock:");
        blockRadio = new JRadioButton("Block");
        unblockRadio = new JRadioButton("Unblock");
        ButtonGroup blockGroup = new ButtonGroup();
        blockGroup.add(blockRadio);
        blockGroup.add(unblockRadio);

        JLabel pinCodeLabel = new JLabel("Pin Code:");
        pinCodeField = new JPasswordField();

        JLabel sudoPasswordLabel = new JLabel("Sudo Password:");
        sudoPasswordField = new JPasswordField();

        JButton submitButton = new JButton("Submit");
        submitButton.addActionListener(new SubmitButtonListener());

        resultArea = new JTextArea();
        resultArea.setEditable(false);

        add(siteNameLabel);
        add(siteNameField);
        add(blockLabel);
        add(new JPanel()); // Empty panel for spacing
        add(blockRadio);
        add(unblockRadio);
        add(pinCodeLabel);
        add(pinCodeField);
        add(sudoPasswordLabel);
        add(sudoPasswordField);
        add(new JPanel()); // Empty panel for spacing
        add(submitButton);
        add(new JScrollPane(resultArea));

        setVisible(true);
    }

    private class SubmitButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String siteName = siteNameField.getText().trim();
            char[] passwordChars = sudoPasswordField.getPassword();
            String sudoPassword = new String(passwordChars);
            String pinCode = pinCodeField.getText().trim();
            int blockFlag = blockRadio.isSelected() ? 1 : unblockRadio.isSelected() ? 0 : -1;

            if (siteName.isEmpty() || sudoPassword.isEmpty() || pinCode.isEmpty() || blockFlag == -1) {
                resultArea.setText("Please fill in all fields correctly.");
                return;
            }

            // Generate the expected pin code
            LocalDate today = LocalDate.now();
            int month = today.getMonthValue();
            int day = today.getDayOfMonth();
            int sumOfDayDigits = (day / 10) + (day % 10);
            int expectedPinCode = Integer.parseInt("" + month + day + sumOfDayDigits + blockFlag);

            // Validate the pin code
            if (!pinCode.equals(String.valueOf(expectedPinCode))) {
                resultArea.setText("Incorrect pin code.");
                return;
            }

            try {
                SiteBlocker.modifyHostsFile(siteName, blockFlag, sudoPassword);
                resultArea.setText("Updated successfully.");
            } catch (IOException | InterruptedException ex) {
                resultArea.setText("An error occurred: " + ex.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SiteBlockerGUI());
    }
}

