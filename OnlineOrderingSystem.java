import java.util.*;

class User {
    String username;
    String password;

    User(String username, String password) {
        this.username = username;
        this.password = password;
    }
}

class MenuItem {
    String name;
    String description;
    double price;

    MenuItem(String name, String description, double price) {
        this.name = name;
        this.description = description;
        this.price = price;
    }
}

class Order {
    User user;
    List<MenuItem> items;

    Order(User user) {
        this.user = user;
        this.items = new ArrayList<>();
    }

    void addItem(MenuItem item) {
        items.add(item);
    }

    double calculateTotal() {
        double total = 0;
        for (MenuItem item : items) {
            total += item.price;
        }
        return total;
    }

    void generateBill() {
        System.out.println("┌────────────────────── Order Details and Bill ──────────────────────┐");
        System.out.printf(" User: %-57s\n", user.username);
        System.out.println(" Items Ordered:                                 ");
        for (MenuItem item : items) {
            System.out.printf("   %-38s $%-10.2f \n", item.name, item.price);
        }
        System.out.println("                                               ");
        System.out.printf(" Total: $%-38.2f \n", calculateTotal());
        System.out.println("└───────────────────────────────────────────────────────────────────┘");
    }
}

public class OnlineOrderingSystem {
    static Map<String, User> usersDB = new HashMap<>();
    static Map<String, List<Order>> userOrders = new HashMap<>();
    static Map<String, List<MenuItem>> menu = new HashMap<>();

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("");
            System.out.println("----- Online Ordering System -----");
            System.out.println("1. Login");
            System.out.println("2. Register");
            System.out.println("3. Exit");
            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume the newline character

            switch (choice) {
                case 1:
                    login(scanner);
                    break;
                case 2:
                    register(scanner);
                    break;
                case 3:
                    System.out.println("Exiting the system. Goodbye!");
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    static void register(Scanner scanner) {
        System.out.print("Enter a username: ");
        String username = scanner.nextLine();

        if (usersDB.containsKey(username)) {
            System.out.println("Username already exists. Please choose another username.");
            return;
        }

        System.out.print("Enter a password: ");
        String password = scanner.nextLine();

        usersDB.put(username, new User(username, password));
        System.out.println("Registration successful. You can now log in.");
    }

    static void login(Scanner scanner) {
        System.out.print("Enter your username: ");
        String username = scanner.nextLine();

        System.out.print("Enter your password: ");
        String password = scanner.nextLine();

        if (usersDB.containsKey(username) && usersDB.get(username).password.equals(password)) {
            System.out.println("Login successful. Welcome, " + username + "!");
            processOrder(username, scanner);
        } else {
            System.out.println("Login failed. Invalid username or password.");
        }
    }

    static void processOrder(String username, Scanner scanner) {
        displayMenu();

        List<MenuItem> selectedItems = selectItems(scanner);
        if (!selectedItems.isEmpty()) {
            double total = placeOrder(username, selectedItems);
            System.out.println("\nOrder placed successfully!");
            viewOrders(username);
        } else {
            System.out.println("\nNo items selected. Order not placed.");
        }
    }

    static void displayMenu() {
        menu.put("Coffee", Arrays.asList(
            new MenuItem("Cappuccino", "Coffee with steamed milk foam", 3.99),
            new MenuItem("Espresso", "Strong black coffee", 2.49),
            new MenuItem("Latte", "Coffee with milk", 4.29),
            new MenuItem("Mocha", "Coffee with chocolate", 4.49)
        ));

        menu.put("Shakes", Arrays.asList(
            new MenuItem("Chocolate Shake", "Creamy chocolate shake", 3.99),
            new MenuItem("Vanilla Shake", "Smooth vanilla shake", 3.99),
            new MenuItem("Strawberry Shake", "Refreshing strawberry shake", 3.99)
        ));

        menu.put("Tea", Arrays.asList(
            new MenuItem("Green Tea", "Healthy green tea", 2.49),
            new MenuItem("Chai Tea", "Spiced tea", 3.49),
            new MenuItem("Earl Grey", "Black tea with bergamot", 3.29)
        ));

        System.out.println("----- Menu Categories -----");
        int index = 1;
        for (String category : menu.keySet()) {
            System.out.println(index + ". " + category);
            index++;
        }
    }

    static List<MenuItem> selectItems(Scanner scanner) {
        List<MenuItem> selectedItems = new ArrayList<>();

        while (true) {
            System.out.println("Enter the category number (or 'done' to finish):");
            String input = scanner.nextLine();
            if (input.equalsIgnoreCase("done")) {
                break;
            }

            try {
                int categoryNumber = Integer.parseInt(input);
                if (categoryNumber >= 1 && categoryNumber <= menu.size()) {
                    List<MenuItem> items = new ArrayList<>(menu.values()).get(categoryNumber - 1);
                    System.out.println("\nAvailable items in " + items.get(0).name + " category:");
                    for (int i = 0; i < items.size(); i++) {
                        MenuItem item = items.get(i);
                        System.out.printf("    %d. %-25s $%.2f\n", i + 1, item.name, item.price);
                    }

                    System.out.print("Enter the item number: ");
                    int itemNumber = scanner.nextInt();
                    scanner.nextLine(); // Consume the newline character

                    if (itemNumber >= 1 && itemNumber <= items.size()) {
                        System.out.println("Added " + items.get(itemNumber - 1).name + " to your order.");
                        selectedItems.add(items.get(itemNumber - 1));
                    } else {
                        System.out.println("Invalid item number.");
                    }
                } else {
                    System.out.println("Invalid category number.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number.");
            }
        }

        return selectedItems;
    }

    static double placeOrder(String username, List<MenuItem> selectedItems) {
        if (usersDB.containsKey(username)) {
            User user = usersDB.get(username);
            Order order = new Order(user);
            for (MenuItem item : selectedItems) {
                order.addItem(item);
            }
            userOrders.computeIfAbsent(username, k -> new ArrayList<>()).add(order);
            return order.calculateTotal();
        }
        return 0;
    }

    static void viewOrders(String username) {
        if (userOrders.containsKey(username)) {
            List<Order> orders = userOrders.get(username);
            for (Order order : orders) {
                order.generateBill();
            }
        } else {
            System.out.println("No orders found.");
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("----- Online Ordering System -----");
            System.out.println("1. Login");
            System.out.println("2. Register");
            System.out.println("3. Exit");
            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume the newline character

            switch (choice) {
                case 1:
                    login(scanner);
                    break;
                case 2:
                    register(scanner);
                    break;
                case 3:
                    System.out.println("Exiting the system. Goodbye!");
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }
}
