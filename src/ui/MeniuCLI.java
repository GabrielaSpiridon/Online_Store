package ui;

import model.Client;
import model.Product;
import model.Order;

import service.InvalidDataException;
import service.ServiceOrder;
import service.ServiceProduct;
import service.ServiceClient;

import java.util.Scanner;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class MeniuCLI {

    private final ServiceProduct serviceProduct;
    private final ServiceClient serviceClient;
    private final ServiceOrder serviceOrder;
    private final Scanner scanner;

    private Client currentUser = null;

    public MeniuCLI(ServiceProduct serviceProduct, ServiceClient serviceClient, ServiceOrder serviceOrder) {
        this.serviceProduct = serviceProduct;
        this.serviceClient = serviceClient;
        this.serviceOrder = serviceOrder;
        this.scanner = new Scanner(System.in);
    }

    public void start(){
        boolean running = true;
        while(running){
            displayMenu();
            String choice = scanner.nextLine();

            try{
                switch(choice){
                    case "1": handleProductMenu(); break;
                    case "2": handleClientMenu(); break;
                    case "3": handlePlaceOrder(); break;
                    case "4": handleReports(); break;
                    case "0": running = false; break;
                    default: System.out.println("Invalid choice. Please try again.");
                }
            }catch (InvalidDataException e) {
                System.err.println("OPERATION FAILED: " + e.getMessage());
            } catch (Exception e) {
                System.err.println("A critical error occurred: " + e.getMessage());
            }
        }
    }

    private void displayMenu() {
        System.out.println("\n--- Main Menu ---");
        System.out.println("Current User: " + (currentUser != null ? currentUser.getName() : "Guest"));
        System.out.println("1. Manage Products (CRUD)");
        System.out.println("2. Manage Clients (CRUD & Login)");
        System.out.println("3. Place New Order (Requires Login)");
        System.out.println("4. Generate Reports/Statistics (Cerința 1)");
        System.out.println("0. Exit Application & Save Data");
        System.out.print("Enter choice: ");
    }

    // --- 1. GESTIONARE PRODUSE (CRUD) ---

    private void handleProductMenu() throws InvalidDataException {
        System.out.println("\n--- Product Management ---");
        serviceProduct.findAllProducts().forEach(System.out::println);

        System.out.print("Do you want to add a new product? (y/n): ");
        if (scanner.nextLine().trim().equalsIgnoreCase("y")) {
            // Logica de citire de la utilizator
            System.out.print("Enter Product Name: ");
            String name = scanner.nextLine();
            System.out.print("Enter Price: ");
            float price = Float.parseFloat(scanner.nextLine());
            System.out.print("Enter Stock Quantity: ");
            int stock = Integer.parseInt(scanner.nextLine());

            // Trebuie să definim ID-ul ca 0 pentru a fi atribuit automat în ServiceProduct
            Product newP = new Product(0, name, price, model.ProductType.ELECTRONIC, stock, "CLI added product");

            // Apelează ServiceProduct pentru validare și salvare
            serviceProduct.saveOrUpdateProduct(newP);
            System.out.println("Product saved/updated successfully!");
        }
    }

    // --- 2. GESTIONARE CLIENȚI (LOGIN / CRUD) ---

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
            // Logica de înregistrare, apelează serviceClient.saveOrUpdateClient(new Client(0, ...))
            System.out.println("Client registration logic needed here...");
        }
    }

    private void handleLogin() {
        System.out.print("Enter Email: ");
        String email = scanner.nextLine();
        System.out.print("Enter Password: ");
        String password = scanner.nextLine();

        Client client = serviceClient.authenticate(email, password).orElse(null);

        if (client != null) {
            currentUser = client;
            System.out.println("Login successful. Welcome, " + currentUser.getName() + "!");
        } else {
            System.out.println("Login failed. Invalid email or password.");
        }
    }

    // --- 3. PLASARE COMANDA (Tranzacție) ---

    private void handlePlaceOrder() throws InvalidDataException {
        if (currentUser == null) {
            System.out.println("ERROR: You must be logged in to place an order.");
            return;
        }

        System.out.println("\n--- Place Order for " + currentUser.getName() + " ---");

        // 1. Logica de selectare a produselor (simplificată)
        Map<Product, Integer> cart = new HashMap<>();
        // Presupunem că utilizatorul știe ID-ul. În GUI ar fi un dropdown.
        System.out.print("Enter Product ID to order (0 to finish): ");
        int productId = Integer.parseInt(scanner.nextLine());

        Product p = serviceProduct.findProductById(productId);
        if (p == null) {
            System.out.println("Product not found.");
            return;
        }

        System.out.print("Enter Quantity for " + p.getName() + ": ");
        int quantity = Integer.parseInt(scanner.nextLine());

        cart.put(p, quantity); // Adaugă în coș

        // 2. Apelarea ServiceOrder pentru tranzacție
        if (!cart.isEmpty()) {
            serviceOrder.placeOrder(currentUser.getId(), cart);
        } else {
            System.out.println("Cart is empty. Order cancelled.");
        }
    }

    // --- 4. RAPOARTE (Cerința 1) ---

    private void handleReports() {
        System.out.println("\n--- Reports & Statistics ---");
        System.out.println("1. Total value of current stock: " + serviceProduct.calculateTotalStockValue() + " RON");

    }

    // --- 0. ÎNCHIDERE ȘI SALVARE (Cerința 2) ---

    private void shutdown() {
        // Cerința 2: Salvarea datelor la închiderea aplicației
        serviceProduct.shutdownApplicationAndSaveData();
        serviceClient.shutdownApplicationAndSaveData();
        serviceOrder.shutdownApplicationAndSaveData();
        System.out.println("Application closed. Data successfully saved.");
    }
}
