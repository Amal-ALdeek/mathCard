import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Random;

public class MathCardsApplication extends JFrame {
    private JTextField answerTextField, pointsTextField;
    private JTextArea mainTextArea, timerTextArea;
    private JCheckBox additionCheckBox, subtractionCheckBox, multiplicationCheckBox, divisionCheckBox;
    private JRadioButton randomRadioButton, offRadioButton, countUpRadioButton, countDownRadioButton;
    private JButton startPracticeButton, stopPracticeButton, exitButton;
    private ButtonGroup factorGroup, timerGroup;

    private int points = 0, timeCount = 0, numberOfTriedQuestions = 0, numberOfCorrectAnswers = 0;
    private final int MAX_TIME = 20 * 60; // 20 minutes in seconds
    private Timer timer;
    private Random random = new Random();
    private int currentQuestionAnswer;

    public MathCardsApplication() {
        setTitle("Math Cards");
        setSize(700, 400);
        initComponents();
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        getContentPane().setLayout(null);

        // Answer and Points Section
        setupAnswerAndPoints();

        // Main Text Area for displaying the math question
        setupMainTextArea();

        // Control Panels: Type, Factor, Timer
        setupControlPanels();

        // Start, Stop, and Exit Buttons
        setupActionButtons();

        // Add ActionListeners to components
        addActionListeners();
    }

    private void setupAnswerAndPoints() {
        JLabel answerLabel = new JLabel("Answer:");
        answerLabel.setBounds(10, 10, 60, 25);
        getContentPane().add(answerLabel);
        getContentPane().setBackground(new Color(173, 216, 230));

        answerTextField = new JTextField();
        answerTextField.setBounds(70, 10, 120, 25);
        getContentPane().add(answerTextField);

        JLabel pointsLabel = new JLabel("Points:");
        pointsLabel.setBounds(200, 10, 60, 25);
        getContentPane().add(pointsLabel);

        pointsTextField = new JTextField("0");
        pointsTextField.setBounds(260, 10, 120, 25);
        pointsTextField.setEditable(false);
        getContentPane().add(pointsTextField);
    }

    private void setupMainTextArea() {
        mainTextArea = new JTextArea();
        mainTextArea.setBounds(10, 40, 670, 50);
        mainTextArea.setEditable(false);
        getContentPane().add(mainTextArea);
    }

    private void setupControlPanels() {
        // Type Panel
        JPanel typePanel = new JPanel();
        typePanel.setBounds(10, 100, 200, 120);
        typePanel.setBorder(BorderFactory.createTitledBorder("Type"));
        typePanel.setLayout(new GridLayout(4, 1));
        getContentPane().add(typePanel);

        additionCheckBox = new JCheckBox("Addition");
        subtractionCheckBox = new JCheckBox("Subtraction");
        multiplicationCheckBox = new JCheckBox("Multiplication");
        divisionCheckBox = new JCheckBox("Division");

        typePanel.add(additionCheckBox);
        typePanel.add(subtractionCheckBox);
        typePanel.add(multiplicationCheckBox);
        typePanel.add(divisionCheckBox);

        // Factor Panel
        JPanel factorPanel = new JPanel();
        factorPanel.setBounds(220, 100, 200, 120);
        factorPanel.setBorder(BorderFactory.createTitledBorder("Factor"));
        factorPanel.setLayout(new GridLayout(4, 3));
        getContentPane().add(factorPanel);

        factorGroup = new ButtonGroup();
        randomRadioButton = new JRadioButton("Random");
        factorGroup.add(randomRadioButton);
        factorPanel.add(randomRadioButton);
        for (int i = 0; i <= 9; i++) {
            JRadioButton radioButton = new JRadioButton(String.valueOf(i));
            factorGroup.add(radioButton);
            factorPanel.add(radioButton);
        }

        // Timer Panel
        JPanel timerPanel = new JPanel();
        timerPanel.setBounds(430, 100, 250, 120);
        timerPanel.setBorder(BorderFactory.createTitledBorder("Timer"));
        timerPanel.setLayout(null);
        getContentPane().add(timerPanel);

        offRadioButton = new JRadioButton("Off" );
        countUpRadioButton = new JRadioButton("On Count-Up");
        countDownRadioButton = new JRadioButton("On Count-Down");
        timerGroup = new ButtonGroup();
        timerGroup.add(offRadioButton);
        timerGroup.add(countUpRadioButton);
        timerGroup.add(countDownRadioButton);

        offRadioButton.setBounds(10, 20, 230, 25);
        countUpRadioButton.setBounds(10, 45, 230, 25);
        countDownRadioButton.setBounds(10, 70, 230, 25);

        timerPanel.add(offRadioButton);
        timerPanel.add(countUpRadioButton);
        timerPanel.add(countDownRadioButton);

        timerTextArea = new JTextArea();
        timerTextArea.setBounds(10, 95, 230, 25);
        timerTextArea.setEditable(false);
        timerPanel.add(timerTextArea);
    }

    private void setupActionButtons() {
        startPracticeButton = new JButton("Start Practice");
        startPracticeButton.setBounds(10, 230, 150, 25);
        startPracticeButton.setBackground(new Color(30, 144, 255));
        startPracticeButton.setForeground(Color.WHITE);
        startPracticeButton.setFocusPainted(false);
        getContentPane().add(startPracticeButton);// Set a blue color

        stopPracticeButton = new JButton("Stop Practice");
        stopPracticeButton.setBounds(170, 230, 150, 25);
        stopPracticeButton.setBackground(new Color(30, 144, 255));
        stopPracticeButton.setForeground(Color.WHITE);
        stopPracticeButton.setFocusPainted(false);
        getContentPane().add(stopPracticeButton);

        exitButton = new JButton("Exit");
        exitButton.setBounds(330, 230, 150, 25);
        exitButton.setBackground(new Color(30, 144, 255));
        exitButton.setForeground(Color.WHITE);
        exitButton.setFocusPainted(false);
        getContentPane().add(exitButton);
    }

    private void addActionListeners() {
        startPracticeButton.addActionListener(e -> startPractice());
        stopPracticeButton.addActionListener(e -> stopPractice());
        exitButton.addActionListener(e -> System.exit(0));
        answerTextField.addActionListener(e -> checkAnswer());
    }

    private void startPractice() {
        numberOfTriedQuestions = 0;
        numberOfCorrectAnswers = 0;
        points = 0;
        pointsTextField.setText("0");
        enableTimerOptions(false);
        generateQuestion();
        startTimer();
    }

    private void stopPractice() {
        stopTimer();
        enableTimerOptions(true);
        showResults();
    }

    private void generateQuestion() {
        int number1 = random.nextInt(10);
        int number2 = randomRadioButton.isSelected() ? random.nextInt(10) : getSelectedFactor();
        String operation = getRandomOperation();

        // Avoid division by zero
        if (operation.equals("/") && number2 == 0) number2 = 1;

        currentQuestionAnswer = calculateAnswer(number1, number2, operation);
        mainTextArea.setText(number1 + " " + operation + " " + number2);
    }

    private String getRandomOperation() {
        ArrayList<String> operations = new ArrayList<>();
        if (additionCheckBox.isSelected()) operations.add("+");
        if (subtractionCheckBox.isSelected()) operations.add("-");
        if (multiplicationCheckBox.isSelected()) operations.add("*");
        if (divisionCheckBox.isSelected()) operations.add("/");

        if (operations.isEmpty()) return "+";
        return operations.get(random.nextInt(operations.size()));
    }

    private int calculateAnswer(int number1, int number2, String operation) {
        return switch (operation) {
            case "+" -> number1 + number2;
            case "-" -> number1 - number2;
            case "*" -> number1 * number2;
            case "/" -> number2 != 0 ? number1 / number2 : 0;
            default -> 0;
        };
    }

    private void checkAnswer() {
        try {
            int userAnswer = Integer.parseInt(answerTextField.getText());
            if (userAnswer == currentQuestionAnswer) {
                points++;
                numberOfCorrectAnswers++;
                pointsTextField.setText(String.valueOf(points));
            }
            numberOfTriedQuestions++;
            generateQuestion();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid number.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
        } finally {
            answerTextField.setText("");
        }
    }

    private int getSelectedFactor() {
        Enumeration<AbstractButton> buttons = factorGroup.getElements();
        while (buttons.hasMoreElements()) {
            AbstractButton button = buttons.nextElement();
            if (button.isSelected()) {
                return "Random".equals(button.getText()) ? random.nextInt(10) : Integer.parseInt(button.getText());
            }
        }
        return random.nextInt(10);
    }

    private void startTimer() {
        if (offRadioButton.isSelected()) {
            timerTextArea.setText("Timer Off");
            return; // Don't start the timer if the option is "Off"
        }

        timeCount = countUpRadioButton.isSelected() ? 0 : MAX_TIME; // Reset time count based on timer mode
        timer = new Timer(1000, e -> updateTimer());
        timer.start();
    }

    private void updateTimer() {
        if (countDownRadioButton.isSelected() && timeCount <= 0) {
            stopTimer();
            showResults();
            return;
        }
        timeCount += countUpRadioButton.isSelected() ? 1 : -1;
        timerTextArea.setText(formatTime(timeCount));

        // Stop timer if count-up reaches MAX_TIME
        if (countUpRadioButton.isSelected() && timeCount >= MAX_TIME) {
            stopTimer();
            showResults();
        }
    }

    private void stopTimer() {
        if (timer != null) {
            timer.stop();
            timer = null;
        }
    }

    private void showResults() {
        double accuracy = numberOfTriedQuestions > 0 ? ((double) numberOfCorrectAnswers / numberOfTriedQuestions) * 100 : 0;
        int elapsedTime = countUpRadioButton.isSelected() ? timeCount : (MAX_TIME - timeCount);
        double timePerCard = numberOfTriedQuestions > 0 ? (double) elapsedTime / numberOfTriedQuestions : 0;

        String message = String.format(
                "Tried Math Cards: %d card\n" +
                        "Correct Answers: %d (%.0f%%)\n" +
                        "Elapsed Time: %s\n" +
                        "Time Per Card: %s seconds",
                numberOfTriedQuestions,
                numberOfCorrectAnswers,
                accuracy,
                formatTime(elapsedTime),
                formatTime((int)timePerCard)
        );

        JOptionPane.showMessageDialog(this, message, "Results", JOptionPane.INFORMATION_MESSAGE);
    }

    private String formatTime(int timeInSeconds) {
        if (timeInSeconds < 0) timeInSeconds = 0; // If time is negative, adjust to 0
        int minutes = timeInSeconds / 60;
        int seconds = timeInSeconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    private void enableTimerOptions(boolean enable) {
        offRadioButton.setEnabled(enable);
        countUpRadioButton.setEnabled(enable);
        countDownRadioButton.setEnabled(enable);
    }
}