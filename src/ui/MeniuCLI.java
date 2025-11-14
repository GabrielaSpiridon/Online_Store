package ui;

import model.Product;
import model.Client;
import model.Order;
import service.ServiceProduct;
import service.ServiceClient;
import service.ServiceOrder;
import service.InvalidDataException;
import java.util.Scanner;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;

/**
 * Interfata cu utilizatorul in linie de comanda (CLI - Command Line Interface).
 * Gestioneaza fluxul aplicatiei, citeste input-ul de la utilizator si apeleaza stratul Service (Cerinta 6).
 */
public class MeniuCLI {

    // Referinte la toate Service-urile (Injectie de Dependente)
    private final ServiceProduct serviceProduct;
    private final ServiceClient serviceClient;
    private final ServiceOrder serviceOrder;
    private final Scanner scanner;

    // Starea utilizatorului logat
    private Client currentUser = null;

    /**
     * Constructor care injecteaza toate dependentele Service necesare.
     * @param serviceProduct Service-ul pentru Produse.
     * @param serviceClient Service-ul pentru Clienti.
     * @param serviceOrder Service-ul pentru Comenzi.
     */
    public MeniuCLI(ServiceProduct serviceProduct, ServiceClient serviceClient, ServiceOrder serviceOrder) {
        this.serviceProduct = serviceProduct;
        this.serviceClient = serviceClient;
        this.serviceOrder = serviceOrder;
        this.scanner = new Scanner(System.in);
    }

    /**
     * Metoda principala care ruleaza bucla meniului aplicatiei.
     */
    public void start() {
        boolean running = true;
        while (running) {
            displayMenu();
            String choice = scanner.nextLine();

            try {
                switch (choice) {
                    case "1": handleProductMenu(); break;
                    case "2": handleClientMenu(); break;
                    case "3": handlePlaceOrder(); break;
                    case "4": handleReports(); break;
                    case "0": running = false; break;
                    default: System.out.println("Invalid choice. Please try again.");
                }
            } catch (InvalidDataException e) {
                // Prinderea exceptiei custom din Service (Cerinta 5)
                System.err.println("OPERATION FAILED: " + e.getMessage());
            } catch (Exception e) {
                // Gestiunea altor erori neasteptate
                System.err.println("A critical error occurred: " + e.getMessage());
            }
        }
        shutdown();
    }

    /**
     * Afiseaza optiunile meniului principal in consola.
     */
    private void displayMenu() {
        System.out.println("\n--- Main Menu ---");
        System.out.println("Current User: " + (currentUser != null ? currentUser.getName() : "Guest"));
        System.out.println("1. Manage Products (CRUD)");
        System.out.println("2. Manage Clients (CRUD & Login)");
        System.out.println("3. Place New Order (Requires Login)");
        System.out.println("4. Generate Reports/Statistics (Cerinta 1)");
        System.out.println("0. Exit Application & Save Data");
        System.out.print("Enter choice: ");
    }

    /**
     * Gestioneaza meniul CRUD pentru produse si citeste input-ul necesar.
     * @throws InvalidDataException Daca datele introduse nu sunt valide.
     */
    private void handleProductMenu() throws InvalidDataException {
        System.out.println("\n--- Product Management ---");
        serviceProduct.findAllProducts().forEach(System.out::println);

        System.out.print("Do you want to add a new product? (y/n): ");
        if (scanner.nextLine().trim().equalsIgnoreCase("y")) {
            System.out.print("Enter Product Name: ");
            String name = scanner.nextLine();
            System.out.print("Enter Price: ");
            float price = Float.parseFloat(scanner.nextLine());
            System.out.print("Enter Stock Quantity: ");
            int stock = Integer.parseInt(scanner.nextLine());

            // ID-ul 0 semnaleaza Service-ului ca trebuie sa atribuie un ID nou
            Product newP = new Product(0, name, price, model.ProductType.ELECTRONIC, stock, "CLI added product");

            serviceProduct.saveOrUpdateProduct(newP);
            System.out.println("Product saved/updated successfully!");
        }
    }

    /**
     * Gestioneaza meniul secundar pentru clienti (Login, Register, Listare).
     * @throws InvalidDataException Daca datele introduse nu sunt valide.
     */
    private void handleClientMenu() throws InvalidDataException {
        System.out.println("\n--- Client Management ---");
        System.out.println("1. List All Clients");
        System.out.println("2. Login");
        System.out.println("3. Register New Client");
        System.out.print("Enter client menu choice: ");
        String choice = scanner.nextLine();

        if (choice.equals("1")) {
            serviceClient.findAllClients().forEach(System.out::println);
        } else if (choice.equals("2")) {
            handleLogin();
        } else if (choice.equals("3")) {
            handleRegisterClient();
        }
    }

    /**
     * Gestioneaza procesul de autentificare.
     */
    private void handleLogin() {
        System.out.print("Enter Email: ");
        String email = scanner.nextLine();
        System.out.print("Enter Password: ");
        String password = scanner.nextLine();

        // Apeleaza ServiceClient.authenticate
        Optional<Client> clientOpt = serviceClient.authenticate(email, password);

        if (clientOpt.isPresent()) {
            currentUser = clientOpt.get();
            System.out.println("Login successful. Welcome, " + currentUser.getName() + "!");
        } else {
            System.out.println("Login failed. Invalid email or password.");
        }
    }

    /**
     * Gestioneaza procesul de inregistrare a unui client nou.
     * @throws InvalidDataException Daca datele introduse nu sunt valide.
     */
    private void handleRegisterClient() throws InvalidDataException {
        System.out.println("\n--- New Client Registration ---");

        System.out.print("Enter Full Name: ");
        String name = scanner.nextLine();
        System.out.print("Enter Email: ");
        String email = scanner.nextLine();
        System.out.print("Enter Password (min 6 chars): ");
        String password = scanner.nextLine();
        System.out.print("Enter Delivery Address: ");
        String address = scanner.nextLine();
        System.out.print("Enter Phone Number: ");
        String phone = scanner.nextLine();

        // ID-ul 0 semnaleaza Service-ului ca este un client nou
        Client newC = new Client(0, name, email, password, address, phone);

        // ServiceClient valideaza email-ul si parola, apoi salveaza
        serviceClient.saveOrUpdateClient(newC);
        System.out.println("Registration successful! Client ID: " + newC.getId());
    }

    /**
     * Gestioneaza plasarea unei comenzi noi.
     * @throws InvalidDataException Daca stocul este insuficient sau utilizatorul nu este logat.
     */
    private void handlePlaceOrder() throws InvalidDataException {
        if (currentUser == null) {
            System.out.println("ERROR: You must be logged in to place an order.");
            return;
        }

        System.out.println("\n--- Place Order for " + currentUser.getName() + " ---");

        // Logica de selectare a produselor (simplificată la un singur produs)
        Map<Product, Integer> cart = new HashMap<>();

        System.out.print("Enter Product ID to order (or 0 to cancel): ");
        int productId = Integer.parseInt(scanner.nextLine());

        if (productId == 0) return;

        Product p = serviceProduct.findProductById(productId);
        if (p == null) {
            System.out.println("Product not found.");
            return;
        }

        System.out.print("Enter Quantity for " + p.getName() + " (Max: " + p.getStockQuantity() + "): ");
        int quantity = Integer.parseInt(scanner.nextLine());

        cart.put(p, quantity); // Adauga in cos

        // Apelarea ServiceOrder pentru tranzactie (se ocupa de validarea stocului)
        if (!cart.isEmpty()) {
            serviceOrder.placeOrder(currentUser.getId(), cart);
            System.out.println("ORDER PLACED: Transaction successful!");
        } else {
            System.out.println("Cart is empty. Order cancelled.");
        }
    }

    /**
     * Afiseaza rapoartele si statisticile generate de Service.
     */
    private void handleReports() {
        System.out.println("\n--- Reports & Statistics ---");

        // Raport 1: Valoarea totala a stocului
        System.out.println("1. Total value of current stock: " + serviceProduct.calculateTotalStockValue() + " RON");

        // Raport 2: Numarul de comenzi plasate
        System.out.println("2. Total number of orders placed: " + serviceOrder.findAllOrders().size());

        // Raport 3: Unitati vandute per produs (detaliat)
        System.out.println("\n--- Detailed Sales Report ---");
        Map<String, Integer> salesData = serviceOrder.getUnitsSoldPerProduct();

        salesData.forEach((name, units) -> System.out.printf("%-25s | %d units\n", name, units));

    }

    /**
     * Opreste aplicatia si salveaza datele tuturor Repositories in fisiere.
     */
    private void shutdown() {
        // Cerința 2: Salvarea datelor la închiderea aplicației
        serviceProduct.shutdownApplicationAndSaveData();
        serviceClient.shutdownApplicationAndSaveData();
        serviceOrder.shutdownApplicationAndSaveData();
        System.out.println("Application closed. Data successfully saved.");
    }
}