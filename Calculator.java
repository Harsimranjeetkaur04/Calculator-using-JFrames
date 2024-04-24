import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Calculator extends JFrame implements ActionListener {
    private JTextField display;

    public Calculator() {
        setTitle("Calculator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(300, 400);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBackground(Color.BLUE);

        display = new JTextField();
        display.setEditable(false);
        display.setBackground(Color.WHITE);
        display.setFont(new Font("Arial", Font.PLAIN, 24));
        panel.add(display, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(5, 4, 5, 5));

        String[] buttonLabels = {
            "7", "8", "9", "/",
            "4", "5", "6", "*",
            "1", "2", "3", "-",
            "0", ".", "=", "+",
            "Del", "C"
        };

        for (String label : buttonLabels) {
            JButton button = new JButton(label);
            button.addActionListener(this);
            buttonPanel.add(button);

            if (label.equals("=") || label.equals("Del") || label.equals("C")) {
                button.setFont(new Font("Arial", Font.PLAIN, 20));
                button.setForeground(Color.RED);
            } else {
                button.setFont(new Font("Arial", Font.PLAIN, 24));
            }
        }

        panel.add(buttonPanel, BorderLayout.CENTER);

        add(panel);
        setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        if ("=".equals(command)) {
            calculate();
        } else if ("Del".equals(command)) {
            deleteCharacter();
        } else if ("C".equals(command)){
            clearDisplay();
        }
         else {
            display.setText(display.getText() + command);
        }
    }

    private void calculate() {
        try {
            String expression = display.getText();
            double result = eval(expression);
            display.setText(Double.toString(result));
        } catch (Exception e) {
            display.setText("Error");
        }
    }

    private void deleteCharacter() {
        String currentText = display.getText();
        if (currentText.length() > 0) {
            display.setText(currentText.substring(0, currentText.length() - 1));
        }
    }
    private void clearDisplay() {
        display.setText("");
    }

    private double eval(String expression) {
        return new Object() {
            int pos = -1, ch;

            void nextChar() {
                ch = (++pos < expression.length()) ? expression.charAt(pos) : -1;
            }

            boolean eat(int charToEat) {
                while (ch == ' ') nextChar();
                if (ch == charToEat) {
                    nextChar();
                    return true;
                }
                return false;
            }

            double parse() {
                nextChar();
                double x = parseExpression();
                if (pos < expression.length()) throw new RuntimeException("Unexpected: " + (char)ch);
                return x;
            }

            double parseExpression() {
                double x = parseTerm();
                for (;;) {
                    if (eat('+')) x += parseTerm();
                    else if (eat('-')) x -= parseTerm();
                    else return x;
                }
            }

            double parseTerm() {
                double x = parseFactor();
                for (;;) {
                    if (eat('*')) x *= parseFactor();
                    else if (eat('/')) x /= parseFactor();
                    else return x;
                }
            }

            double parseFactor() {
                if (eat('+')) return parseFactor();
                if (eat('-')) return -parseFactor();

                double x;
                int startPos = this.pos;
                if (eat('(')) {
                    x = parseExpression();
                    eat(')');
                } else if ((ch >= '0' && ch <= '9') || ch == '.') {
                    while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
                    x = Double.parseDouble(expression.substring(startPos, this.pos));
                } else {
                    throw new RuntimeException("Unexpected: " + (char)ch);
                }

                return x;
            }
        }.parse();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Calculator());
    }
}
