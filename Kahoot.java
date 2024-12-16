import java.io.*;
import java.util.*;

public class Kahoot {
    static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) throws IOException {
        System.out.println("Welcome to the Quiz Game!");


        String playerName = authenticateUser();
        if (playerName == null) {
            System.out.println("Exiting the game. Goodbye!");
            return;
        }

        System.out.println("Hello, " + playerName + "! Let's start the quiz.");
        List<Question> questions = loadQuestions("questions.txt");
        if (questions.isEmpty()) {
            System.out.println("No questions available. Exiting...");
            return;
        }

        Collections.shuffle(questions);
        List<Question> selectedQuestions = questions.subList(0, Math.min(5, questions.size()));

        int score = 0;


        for (Question question : selectedQuestions) {
            System.out.println("\n" + question.getQuestion());
            for (int i = 0; i < question.getOptions().length; i++) {
                System.out.println((i + 1) + ". " + question.getOptions()[i]);
            }

            int answer = getValidAnswer();
            if (answer == question.getCorrectOption()) {
                System.out.println("Correct!");
                score++;
            } else {
                System.out.println("Wrong! The correct answer was: " + question.getOptions()[question.getCorrectOption() - 1]);
            }
        }


        System.out.println("\nQuiz Over! Your score: " + score + "/" + selectedQuestions.size());
    }

    private static String authenticateUser() throws IOException {
        System.out.println("1. Login");
        System.out.println("2. Register");
        System.out.print("Choose an option: ");
        int choice = scanner.nextInt();
        scanner.nextLine();

        if (choice == 1) {
            return loginUser();
        } else if (choice == 2) {
            return registerUser();
        } else {
            System.out.println("Invalid choice. Please try again.");
            return authenticateUser();
        }
    }
    private static String loginUser() throws IOException {
        System.out.print("Enter your username: ");
        String username = scanner.nextLine();
        System.out.print("Enter your password: ");
        String password = scanner.nextLine();

        try (BufferedReader reader = new BufferedReader(new FileReader("users.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":"); // username:password
                if (parts.length == 2 && parts[0].equals(username) && parts[1].equals(password)) {
                    System.out.println("Login successful!");
                    return username;
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("User database not found. Please register first.");
        }

        System.out.println("Invalid username or password. Try again.");
        return authenticateUser();
    }


    private static String registerUser() throws IOException {
        System.out.print("Enter your username: ");
        String username = scanner.nextLine();
        System.out.print("Enter your password: ");
        String password = scanner.nextLine();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("users.txt", true))) {
            writer.write(username + ":" + password);
            writer.newLine();
        }

        System.out.println("Registration successful! You can now log in.");
        return username;
    }


    private static List<Question> loadQuestions(String fileName) throws IOException {
        List<Question> questions = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(";"); // Question;Option1;Option2;Option3;Option4;CorrectOption
                if (parts.length == 6) {
                    String questionText = parts[0];
                    String[] options = Arrays.copyOfRange(parts, 1, 5);
                    int correctOption = Integer.parseInt(parts[5]);
                    questions.add(new Question(questionText, options, correctOption));
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("Questions file not found: " + fileName);
        }
        return questions;
    }

    private static int getValidAnswer() {
        int answer;
        while (true) {
            System.out.print("Your answer (1-4): ");
            try {
                answer = scanner.nextInt();
                if (answer >= 1 && answer <= 4) {
                    break;
                } else {
                    System.out.println("Please enter a number between 1 and 4.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number between 1 and 4.");
                scanner.nextLine();
            }
        }
        return answer;
    }
}


class Question {
    private final String question;
    private final String[] options;
    private final int correctOption;

    public Question(String question, String[] options, int correctOption) {
        this.question = question;
        this.options = options;
        this.correctOption = correctOption;
    }

    public String getQuestion() {
        return question;
    }

    public String[] getOptions() {
        return options;
    }

    public int getCorrectOption() {
        return correctOption;
    }
}
