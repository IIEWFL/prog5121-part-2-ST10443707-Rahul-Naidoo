import java.util.*;
import java.util.regex.*;
import java.io.FileWriter;
import java.io.IOException;
import javax.swing.JOptionPane;
import org.json.simple.JSONObject;

class User {
    private String username;
    private String password;
    private String phoneNumber;
    private String firstName;
    private String lastName;

    public User(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public boolean setUsername(String username) {
        if (username.contains("_") && username.length() <= 5) {
            this.username = username;
            System.out.println("Username successfully captured.");
            return true;
        } else {
            System.out.println("Username is not correctly formatted. It must contain an underscore and be no more than 5 characters.");
            return false;
        }
    }

    public boolean setPassword(String password) {
        boolean hasUpper = password.matches(".*[A-Z].*");
        boolean hasDigit = password.matches(".*\d.*");
        boolean hasSpecial = password.matches(".*[!@#$%^&*()_+\-=[\]{};':"\\|,.<>/?].*");
        boolean isLongEnough = password.length() >= 8;

        if (hasUpper && hasDigit && hasSpecial && isLongEnough) {
            this.password = password;
            System.out.println("Password successfully captured.");
            return true;
        } else {
            System.out.println("Password must be at least 8 characters and include a capital letter, a number, and a special character.");
            return false;
        }
    }

    public boolean setPhoneNumber(String phoneNumber) {
        Pattern pattern = Pattern.compile("^\+27\d{9,10}$");
        Matcher matcher = pattern.matcher(phoneNumber);

        if (matcher.matches()) {
            this.phoneNumber = phoneNumber;
            System.out.println("Phone number successfully captured.");
            return true;
        } else {
            System.out.println("Phone number must start with +27 and be 10-11 digits long.");
            return false;
        }
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }
}

class Login {
    private String storedUsername;
    private String storedPassword;
    private String firstName;
    private String lastName;
    private boolean isLoggedIn;

    public Login(String firstName, String lastName, String username, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.storedUsername = username;
        this.storedPassword = password;
    }

    public boolean loginUser(String username, String password) {
        isLoggedIn = storedUsername.equals(username) && storedPassword.equals(password);
        return isLoggedIn;
    }

    public String returnLoginStatus() {
        if (isLoggedIn) {
            return "Welcome " + firstName + " " + lastName + ", it is great to see you again.";
        } else {
            return "Username or password incorrect, please try again.";
        }
    }

    public boolean isLoggedIn() {
        return isLoggedIn;
    }
}

class Message {
    private static int messageCount = 0;
    private final String messageID;
    private final String recipient;
    private final String content;
    private final String messageHash;

    public Message(String recipient, String content) {
        messageCount++;
        this.messageID = generateMessageID();
        this.recipient = recipient;
        this.content = content;
        this.messageHash = createMessageHash();
    }

    public boolean checkMessageID() {
        return messageID.length() <= 10;
    }

    public int checkRecipientCell() {
        if (!recipient.startsWith("+")) return 0;
        String digitsOnly = recipient.replaceAll("\D", "");
        return digitsOnly.length() <= 10 ? 1 : 0;
    }

    public String createMessageHash() {
        String[] words = content.trim().split("\s+");
        String first = words.length > 0 ? words[0].toUpperCase() : "";
        String last = words.length > 1 ? words[words.length - 1].toUpperCase() : first;
        return messageID.substring(0, 2) + ":" + messageCount + ":" + first + last;
    }

    public String SentMessage(Scanner scanner) {
        System.out.println("1) Send Message (2) Disregard Message (3) Store Message");
        System.out.print("Choose an option: ");
        String input = scanner.nextLine();

        if (input.equals("1")) return "send";
        else if (input.equals("2")) return "discard";
        else return "store";
    }

    public String printMessages() {
        return "Message ID: " + messageID + "Message Hash: " + messageHash + "Recipient: " + recipient + "Message: " + content;
    }

    public static int returnTotalMessages() {
        return messageCount;
    }

    public void storeMessage() {
        JSONObject json = new JSONObject();
        json.put("MessageID", messageID);
        json.put("Recipient", recipient);
        json.put("Message", content);
        json.put("MessageHash", messageHash);

        try (FileWriter file = new FileWriter("messages.json", true)) {
            file.write(json.toJSONString() + "");
        } catch (IOException e) {
            System.out.println("An error occurred while storing the message.");
        }
    }

    private String generateMessageID() {
        Random rand = new Random();
        StringBuilder id = new StringBuilder();
        for (int i = 0; i < 10; i++) id.append(rand.nextInt(10));
        return id.toString();
    }
}

public class ChatAppPart1And2_Updated {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("=== Chat App Registration ===");

        System.out.print("Enter first name: ");
        String firstName = scanner.nextLine();

        System.out.print("Enter last name: ");
        String lastName = scanner.nextLine();

        User newUser = new User(firstName, lastName);
        while (!newUser.setUsername(prompt(scanner, "Enter username (must contain _ and be <= 5 chars): "))) {}
        while (!newUser.setPassword(prompt(scanner, "Enter password (8+ chars, 1 uppercase, 1 number, 1 special): "))) {}
        while (!newUser.setPhoneNumber(prompt(scanner, "Enter phone number (+27XXXXXXXXX): "))) {}

        System.out.println("Registration successful!");
        Login login = new Login(firstName, lastName, newUser.getUsername(), newUser.getPassword());

        System.out.println("Please log in to continue:");
        String loginUsername = prompt(scanner, "Username: ");
        String loginPassword = prompt(scanner, "Password: ");
        login.loginUser(loginUsername, loginPassword);
        System.out.println(login.returnLoginStatus());

        if (!login.isLoggedIn()) {
            System.out.println("Exiting program.");
            return;
        }

        System.out.println("Welcome to QuickChat.");
        ArrayList<Message> sentMessages = new ArrayList<>();
        boolean running = true;

        while (running) {
            System.out.println("Select an option:");
            System.out.println("1) Send Messages");
            System.out.println("2) Show recently sent messages");
            System.out.println("3) Quit");

            String option = prompt(scanner, "Enter option: ");
            switch (option) {
                case "1":
                    int numMessages = Integer.parseInt(prompt(scanner, "How many messages would you like to send? "));
                    for (int i = 0; i < numMessages; i++) {
                        System.out.println("Message " + (i + 1));
                        String recipient = prompt(scanner, "Enter recipient phone number (+27XXXXXXXXX): ");
                        String content = prompt(scanner, "Enter your message (<= 250 characters): ");
                        if (content.length() > 250) {
                            System.out.println("Please enter a message of less than 250 characters.");
                            continue;
                        }

                        Message msg = new Message(recipient, content);
                        if (!msg.checkMessageID()) continue;
                        if (msg.checkRecipientCell() == 0) continue;

                        String decision = msg.SentMessage(scanner);
                        if (decision.equals("send")) {
                            sentMessages.add(msg);
                            JOptionPane.showMessageDialog(null, msg.printMessages(), "Message Sent", JOptionPane.INFORMATION_MESSAGE);
                        } else if (decision.equals("discard")) {
                            System.out.println("Message disregarded.");
                        } else {
                            msg.storeMessage();
                            System.out.println("Message stored.");
                        }
                    }
                    break;
                case "2":
                    System.out.println("Coming Soon.");
                    break;
                case "3":
                    running = false;
                    break;
                default:
                    System.out.println("Invalid option.");
                    break;
            }
        }

        System.out.println("Total messages sent: " + Message.returnTotalMessages());
        System.out.println("Goodbye!");
        scanner.close();
    }

    private static String prompt(Scanner scanner, String message) {
        System.out.print(message);
        return scanner.nextLine();
    }
}