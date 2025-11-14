package ui;

import service.ServiceProduct;
import service.ServiceClient;
import service.ServiceOrder;
import service.InvalidDataException;
import model.Product;
import model.ProductType;
import model.Client;
import model.Order;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.*;
import java.util.List;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;

/**
 * Interfata Grafica (GUI) a aplicatiei, folosind Java Swing (Cerința 7).
 * Include logica de Coș de Cumpărături și Autentificare cu stil îmbunătățit.
 */
public class StoreGUI extends JFrame {

    // --- CONSTANTE VIZUALE ---
    private static final Color BACKGROUND_COLOR = new Color(248, 248, 255);
    private static final Color PRIMARY_COLOR = new Color(28, 102, 180);
    private static final Color SECONDARY_COLOR = new Color(230, 230, 230);
    private static final Font HEADER_FONT = new Font("Segoe UI", Font.BOLD, 15);
    private static final Font UI_FONT = new Font("Segoe UI", Font.PLAIN, 12);
    private static final Font STATUS_FONT = new Font("Arial", Font.ITALIC, 12);

    private final ServiceProduct serviceProduct;
    private final ServiceClient serviceClient;
    private final ServiceOrder serviceOrder;
    private DefaultTableModel productTableModel;
    private JTable productTable;

    private JTextField selectedProductIdField;
    private JTextField selectedProductNameField;
    private JLabel cartStatusLabel;
    private JTextArea cartDisplayArea;
    private JLabel clientStatusLabel;

    // Cosul de cumparaturi temporar
    private final Map<Product, Integer> temporaryCart = new HashMap<>();

    // Starea clientului logat
    private Optional<Client> loggedInUser = Optional.empty();

    public StoreGUI(ServiceProduct sp, ServiceClient sc, ServiceOrder so) {
        this.serviceProduct = sp;
        this.serviceClient = sc;
        this.serviceOrder = so;

        setTitle("Online Store");
        setSize(850, 600);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                shutdownAndSave();
            }
        });

        getContentPane().setBackground(BACKGROUND_COLOR);

        // Configurare Layout principal (Tab-uri)
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(HEADER_FONT);

        tabbedPane.addTab("Client Access", createClientAccessPanel());
        tabbedPane.addTab("Product Management", createProductPanel());
        tabbedPane.addTab("Place Order", createOrderPanel());
        tabbedPane.addTab("Reports", createReportsPanel());

        add(tabbedPane, BorderLayout.CENTER);
        setVisible(true);
        loadProductData();
    }

    private JPanel createClientAccessPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(new EmptyBorder(30, 30, 30, 30));

        // Status
        clientStatusLabel = new JLabel("Status: Logged out.", SwingConstants.CENTER);
        clientStatusLabel.setFont(HEADER_FONT.deriveFont(Font.BOLD, 18));

        // Butoane
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 30));
        buttonPanel.setBackground(BACKGROUND_COLOR);

        JButton loginButton = new JButton("Login");
        styleButton(loginButton, PRIMARY_COLOR, Color.WHITE);

        JButton registerButton = new JButton("Register New Account");
        styleButton(registerButton, Color.GRAY, Color.WHITE);

        loginButton.addActionListener(e -> showLoginDialog());
        registerButton.addActionListener(e -> showRegisterDialog());

        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);

        panel.add(clientStatusLabel, BorderLayout.NORTH);
        panel.add(buttonPanel, BorderLayout.CENTER);

        return panel;
    }

    private void showLoginDialog() {
        JTextField emailField = new JTextField(15);
        JPasswordField passwordField = new JPasswordField(15);

        emailField.setFont(UI_FONT);
        passwordField.setFont(UI_FONT);

        JPanel loginPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        loginPanel.add(new JLabel("Email:", SwingConstants.RIGHT));
        loginPanel.add(emailField);
        loginPanel.add(new JLabel("Password:", SwingConstants.RIGHT));
        loginPanel.add(passwordField);

        int result = JOptionPane.showConfirmDialog(this, loginPanel, "Client Login", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String email = emailField.getText();
            String password = new String(passwordField.getPassword());

            loggedInUser = serviceClient.authenticate(email, password);

            if (loggedInUser.isPresent()) {
                clientStatusLabel.setText("Status: Logged in as " + loggedInUser.get().getName() + " (ID: " + loggedInUser.get().getId() + ")");
                JOptionPane.showMessageDialog(this, "Login successful!");
            } else {
                clientStatusLabel.setText("Status: Logged out.");
                JOptionPane.showMessageDialog(this, "Login failed. Invalid credentials.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showRegisterDialog() {
        JTextField nameField = new JTextField(15);
        JTextField emailField = new JTextField(15);
        JPasswordField passwordField = new JPasswordField(15);
        JTextField addressField = new JTextField(15);
        JTextField phoneField = new JTextField(15);

        nameField.setFont(UI_FONT); emailField.setFont(UI_FONT); passwordField.setFont(UI_FONT); addressField.setFont(UI_FONT); phoneField.setFont(UI_FONT);

        JPanel registerPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        registerPanel.add(new JLabel("Name:", SwingConstants.RIGHT));
        registerPanel.add(nameField);
        registerPanel.add(new JLabel("Email:", SwingConstants.RIGHT));
        registerPanel.add(emailField);
        registerPanel.add(new JLabel("Password (min 6):", SwingConstants.RIGHT));
        registerPanel.add(passwordField);
        registerPanel.add(new JLabel("Address:", SwingConstants.RIGHT));
        registerPanel.add(addressField);
        registerPanel.add(new JLabel("Phone:", SwingConstants.RIGHT));
        registerPanel.add(phoneField);

        int result = JOptionPane.showConfirmDialog(this, registerPanel, "Register New Client", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                Client newClient = new Client(0, nameField.getText(), emailField.getText(),
                        new String(passwordField.getPassword()),
                        addressField.getText(), phoneField.getText());

                serviceClient.saveOrUpdateClient(newClient);
                serviceClient.shutdownApplicationAndSaveData();
                JOptionPane.showMessageDialog(this, "Registration successful! You can now log in.");

            } catch (InvalidDataException ex) {
                JOptionPane.showMessageDialog(this, "Registration failed: " + ex.getMessage(), "Validation Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private JPanel createProductPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        String[] columnNames = {"ID", "Name", "Price", "Stock", "Type"};
        productTableModel = new DefaultTableModel(columnNames, 0);
        productTable = new JTable(productTableModel);
        productTable.getTableHeader().setFont(HEADER_FONT);
        productTable.setFont(UI_FONT);

        panel.add(new JScrollPane(productTable), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setBackground(BACKGROUND_COLOR);

        JButton refreshButton = new JButton("Refresh Stock");
        styleButton(refreshButton);
        JButton addButton = new JButton("Add New Product");
        styleButton(addButton);

        refreshButton.addActionListener(e -> loadProductData());
        addButton.addActionListener(e -> showAddProductDialog());

        buttonPanel.add(refreshButton);
        buttonPanel.add(addButton);

        panel.add(buttonPanel, BorderLayout.SOUTH);
        return panel;
    }

    private void loadProductData() {
        productTableModel.setRowCount(0);
        List<Product> products = serviceProduct.findAllProducts();

        for (Product p : products) {
            Object[] row = new Object[]{
                    p.getId(), p.getName(), p.getPrice(), p.getStockQuantity(), p.getProductType()
            };
            productTableModel.addRow(row);
        }
    }

    private void showAddProductDialog() {
        ProductType[] types = ProductType.values();
        JComboBox<ProductType> typeDropdown = new JComboBox<>(types);

        JTextField nameField = new JTextField();
        JTextField priceField = new JTextField();
        JTextField stockField = new JTextField();

        nameField.setFont(UI_FONT); priceField.setFont(UI_FONT); stockField.setFont(UI_FONT); typeDropdown.setFont(UI_FONT);

        JPanel inputPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        inputPanel.add(new JLabel("Product Name:", SwingConstants.RIGHT)); inputPanel.add(nameField);
        inputPanel.add(new JLabel("Price:", SwingConstants.RIGHT)); inputPanel.add(priceField);
        inputPanel.add(new JLabel("Stock Quantity:", SwingConstants.RIGHT)); inputPanel.add(stockField);
        inputPanel.add(new JLabel("Product Type:", SwingConstants.RIGHT)); inputPanel.add(typeDropdown);

        int option = JOptionPane.showConfirmDialog(this, inputPanel, "Add New Product", JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            try {
                String name = nameField.getText();
                float price = Float.parseFloat(priceField.getText());
                int stock = Integer.parseInt(stockField.getText());
                ProductType type = (ProductType) typeDropdown.getSelectedItem();

                Product newP = new Product(0, name, price, type, stock, "Added via GUI");

                serviceProduct.saveOrUpdateProduct(newP);
                loadProductData();
                JOptionPane.showMessageDialog(this, "Product added successfully! ID assigned by system.");

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Error: Price and Stock must be valid numbers.", "Input Error", JOptionPane.ERROR_MESSAGE);
            } catch (InvalidDataException ex) {
                JOptionPane.showMessageDialog(this, "Validation Failed: " + ex.getMessage(), "Validation Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private JPanel createOrderPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // A. Panou Central
        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        JTable orderProductTable = new JTable(productTableModel); // Tabel produse stânga
        orderProductTable.getTableHeader().setFont(HEADER_FONT);
        centerPanel.add(new JScrollPane(orderProductTable));

        // B. Sumar Cos
        JPanel cartSummaryPanel = new JPanel(new BorderLayout(5, 5));
        cartSummaryPanel.setBackground(SECONDARY_COLOR);
        cartSummaryPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY), "Shopping Cart Summary", 0, 0, HEADER_FONT));

        cartDisplayArea = new JTextArea(8, 20);
        cartDisplayArea.setEditable(false);
        cartDisplayArea.setFont(UI_FONT);
        cartStatusLabel = new JLabel("Cart: 0 unique items | Total: 0.00 RON");
        cartStatusLabel.setFont(HEADER_FONT);

        cartSummaryPanel.add(new JScrollPane(cartDisplayArea), BorderLayout.CENTER);
        cartSummaryPanel.add(cartStatusLabel, BorderLayout.SOUTH);
        centerPanel.add(cartSummaryPanel);

        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // C. Panoul de Input/Actiune
        JPanel inputPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        inputPanel.setBackground(BACKGROUND_COLOR);

        selectedProductIdField = new JTextField();
        selectedProductIdField.setEditable(false);
        selectedProductNameField = new JTextField();
        selectedProductNameField.setEditable(false);

        JTextField quantityField = new JTextField();
        quantityField.setFont(UI_FONT);

        JButton addToCartButton = new JButton("Add to Cart");
        styleButton(addToCartButton);
        JButton placeOrderButton = new JButton("Place Order");
        styleButton(placeOrderButton, PRIMARY_COLOR, Color.WHITE);

        // Logica de Selectie pe Click
        orderProductTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting() && orderProductTable.getSelectedRow() != -1) {
                    int selectedRow = orderProductTable.getSelectedRow();
                    String selectedId = productTableModel.getValueAt(selectedRow, 0).toString();
                    String selectedName = productTableModel.getValueAt(selectedRow, 1).toString();

                    selectedProductIdField.setText(selectedId);
                    selectedProductNameField.setText(selectedName);
                }
            }
        });

        addToCartButton.addActionListener(e -> {
            try {
                if (selectedProductIdField.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Please select a product first.", "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                int productId = Integer.parseInt(selectedProductIdField.getText());
                int quantity = Integer.parseInt(quantityField.getText());

                Product productToAdd = serviceProduct.findProductById(productId);

                if (productToAdd == null) {
                    throw new InvalidDataException("Product not found in system.");
                }

                int currentCartQuantity = temporaryCart.getOrDefault(productToAdd, 0);
                if (productToAdd.getStockQuantity() < (currentCartQuantity + quantity)) {
                    throw new InvalidDataException("Insufficient stock available for this quantity. Current stock: " + productToAdd.getStockQuantity());
                }

                temporaryCart.put(productToAdd, currentCartQuantity + quantity);

                updateCartDisplay();
                quantityField.setText("");
                JOptionPane.showMessageDialog(this, productToAdd.getName() + " added to cart!");

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Error: Quantity must be a valid number.", "Input Error", JOptionPane.ERROR_MESSAGE);
            } catch (InvalidDataException ex) {
                JOptionPane.showMessageDialog(this, "Add to Cart Failed: " + ex.getMessage(), "Transaction Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Logica Plasare Comanda
        placeOrderButton.addActionListener(e -> {
            if (loggedInUser.isEmpty()) {
                JOptionPane.showMessageDialog(this, "ERROR: Please log in before placing an order.", "Order Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (temporaryCart.isEmpty()) {
                JOptionPane.showMessageDialog(this, "The cart is empty. Add items first.", "Order Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                // Apeleaza ServiceOrder cu produsele din cosul temporar
                serviceOrder.placeOrder(loggedInUser.get().getId(), temporaryCart);

                JOptionPane.showMessageDialog(this, "Order placed successfully! Stock updated.", "Success", JOptionPane.INFORMATION_MESSAGE);
                temporaryCart.clear();
                updateCartDisplay();
                loadProductData(); // Reimprospateaza stocul în tabel

            } catch (InvalidDataException ex) {
                JOptionPane.showMessageDialog(this, "Order Failed: " + ex.getMessage(), "Transaction Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Adaugarea componentelor la Panoul de Input
        inputPanel.add(new JLabel("Selected Product Name:", SwingConstants.RIGHT));
        inputPanel.add(selectedProductNameField);
        inputPanel.add(new JLabel("Product ID (Auto-Selected):", SwingConstants.RIGHT));
        inputPanel.add(selectedProductIdField);
        inputPanel.add(new JLabel("Quantity to Add:", SwingConstants.RIGHT));
        inputPanel.add(quantityField);

        inputPanel.add(addToCartButton);
        inputPanel.add(placeOrderButton);

        mainPanel.add(inputPanel, BorderLayout.SOUTH);
        return mainPanel;
    }

    private void updateCartDisplay() {
        cartDisplayArea.setText("");
        float total = 0.0f;
        int uniqueItems = 0;

        StringBuilder sb = new StringBuilder("Items in Cart:\n");

        for (Map.Entry<Product, Integer> entry : temporaryCart.entrySet()) {
            Product p = entry.getKey();
            int qty = entry.getValue();
            float subtotal = p.getPrice() * qty;

            sb.append(String.format("  [%d x %s] - %.2f RON\n", qty, p.getName(), subtotal));
            total += subtotal;
            uniqueItems++;
        }

        cartDisplayArea.setText(sb.toString());
        cartStatusLabel.setText(String.format("Cart: %d unique items | Total: %.2f RON", uniqueItems, total));
    }

    private JPanel createReportsPanel() {

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15)); // Padding consistent

        // 1. Calcul Statistici Agregate
        float totalValue = serviceProduct.calculateTotalStockValue();
        int totalOrders = serviceOrder.findAllOrders().size();

        // 2. Obtine raportul detaliat pe produse
        Map<String, Integer> salesData = serviceOrder.getUnitsSoldPerProduct();

        // 3. Crearea zonei de text pentru raportul detaliat
        JTextArea salesReportArea = new JTextArea(15, 60);
        salesReportArea.setEditable(false);
        salesReportArea.setFont(new Font(Font.MONOSPACED, UI_FONT.getStyle(), UI_FONT.getSize()));
        // Construieste continutul raportului detaliat
        StringBuilder sb = new StringBuilder("\n--- Units Sold Per Product ---\n");
        sb.append(String.format("%-30s | %s\n", "Product Name", "Units Sold"));
        sb.append("----------------------------------------------------\n");

        // Sorteaza raportul dupa cantitatea vanduta
        salesData.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue(Comparator.reverseOrder()))
                .forEach(entry -> {
                    sb.append(String.format("%-30s | %d\n", entry.getKey(), entry.getValue()));
                });

        salesReportArea.setText(sb.toString());

        // Etichete pentru sumare
        JLabel titleLabel = new JLabel("Application Statistics Summary", SwingConstants.LEFT);
        titleLabel.setFont(HEADER_FONT.deriveFont(Font.BOLD, 18));

        JLabel valueLabel = new JLabel("Total Stock Value: " + String.format("%.2f RON", totalValue));
        valueLabel.setFont(HEADER_FONT);

        JLabel ordersLabel = new JLabel("Total Orders Placed: " + totalOrders);
        ordersLabel.setFont(HEADER_FONT);

        // 4. Adauga componentele la panou
        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(20));
        panel.add(valueLabel);
        panel.add(ordersLabel);
        panel.add(Box.createVerticalStrut(20));

        panel.add(new JLabel("Detailed Sales Report (Sorted by Units Sold):"));
        panel.add(new JScrollPane(salesReportArea));

        return panel;
    }
    // --- Metodă Utilitara pentru Stil ---
    private void styleButton(JButton button) {
        button.setFont(HEADER_FONT);
        button.setBackground(Color.LIGHT_GRAY);
        button.setForeground(Color.BLACK);
        button.setFocusPainted(false);
    }

    private void styleButton(JButton button, Color background, Color foreground) {
        button.setFont(HEADER_FONT);
        button.setBackground(background);
        button.setForeground(foreground);
        button.setFocusPainted(false);
    }

    private void shutdownAndSave() {
        // Salvarea datelor la inchiderea aplicatiei
        serviceProduct.shutdownApplicationAndSaveData();
        serviceClient.shutdownApplicationAndSaveData();
        serviceOrder.shutdownApplicationAndSaveData();
        System.out.println("GUI closed. Data saved successfully.");
        System.exit(0);
    }
}