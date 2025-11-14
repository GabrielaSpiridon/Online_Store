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
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import java.util.Optional; // Necesar pentru Login

/**
 * Interfata Grafica (GUI) a aplicatiei, folosind Java Swing (Cerința 7).
 * Include logica de Coș de Cumpărături și Autentificare.
 */
public class StoreGUI extends JFrame {

    private final ServiceProduct serviceProduct;
    private final ServiceClient serviceClient;
    private final ServiceOrder serviceOrder;
    private DefaultTableModel productTableModel;
    private JTable productTable;

    // Câmpuri UI pentru comandă
    private JTextField selectedProductIdField;
    private JTextField selectedProductNameField;
    private JLabel cartStatusLabel;
    private JTextArea cartDisplayArea;
    private JLabel clientStatusLabel; // NOU: Etichetă pentru statusul clientului

    // Coșul de cumpărături temporar
    private final Map<Product, Integer> temporaryCart = new HashMap<>();

    // NOU: Utilizează Optional pentru a gestiona starea de Login (evită null)
    private Optional<Client> loggedInUser = Optional.empty();

    public StoreGUI(ServiceProduct sp, ServiceClient sc, ServiceOrder so) {
        this.serviceProduct = sp;
        this.serviceClient = sc;
        this.serviceOrder = so;

        setTitle("Online Store Management (GUI)");
        setSize(800, 550);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                shutdownAndSave();
            }
        });

        // Configurare Layout principal (Tab-uri)
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Product Management", createProductPanel());
        tabbedPane.addTab("Place Order (Tranzacție)", createOrderPanel());
        tabbedPane.addTab("Reports", createReportsPanel());
        tabbedPane.addTab("Client Access", createClientAccessPanel()); // NOU Tab

        add(tabbedPane, BorderLayout.CENTER);
        setVisible(true);
        loadProductData();
    }

    // --- Panou Nou: GESTIONARE ACCES CLIENT (Login/Register) ---

    private JPanel createClientAccessPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Status
        clientStatusLabel = new JLabel("Status: Logged out.", SwingConstants.CENTER);

        // Butoane
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        JButton loginButton = new JButton("Login");
        JButton registerButton = new JButton("Register New Account");

        loginButton.addActionListener(e -> showLoginDialog());
        registerButton.addActionListener(e -> showRegisterDialog());

        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);

        panel.add(clientStatusLabel, BorderLayout.NORTH);
        panel.add(buttonPanel, BorderLayout.CENTER);

        return panel;
    }

    // --- Metoda de LOGIN ---

    private void showLoginDialog() {
        JTextField emailField = new JTextField(15);
        JPasswordField passwordField = new JPasswordField(15);

        JPanel loginPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        loginPanel.add(new JLabel("Email:"));
        loginPanel.add(emailField);
        loginPanel.add(new JLabel("Password:"));
        loginPanel.add(passwordField);

        int result = JOptionPane.showConfirmDialog(this, loginPanel, "Client Login", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String email = emailField.getText();
            String password = new String(passwordField.getPassword());

            // Apelează Service-ul pentru autentificare
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

    // --- Metoda de REGISTER ---

    private void showRegisterDialog() {
        JTextField nameField = new JTextField(15);
        JTextField emailField = new JTextField(15);
        JPasswordField passwordField = new JPasswordField(15);
        JTextField addressField = new JTextField(15);
        JTextField phoneField = new JTextField(15);

        JPanel registerPanel = new JPanel(new GridLayout(5, 2, 5, 5));
        registerPanel.add(new JLabel("Name:"));
        registerPanel.add(nameField);
        registerPanel.add(new JLabel("Email:"));
        registerPanel.add(emailField);
        registerPanel.add(new JLabel("Password (min 6):"));
        registerPanel.add(passwordField);
        registerPanel.add(new JLabel("Address:"));
        registerPanel.add(addressField);
        registerPanel.add(new JLabel("Phone:"));
        registerPanel.add(phoneField);

        int result = JOptionPane.showConfirmDialog(this, registerPanel, "Register New Client", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                // Creează obiectul Client cu ID=0 (Service va atribui ID-ul)
                Client newClient = new Client(0, nameField.getText(), emailField.getText(),
                        new String(passwordField.getPassword()),
                        addressField.getText(), phoneField.getText());

                serviceClient.saveOrUpdateClient(newClient); // Validează și salvează
                JOptionPane.showMessageDialog(this, "Registration successful! You can now log in.");

            } catch (InvalidDataException ex) {
                JOptionPane.showMessageDialog(this, "Registration failed: " + ex.getMessage(), "Validation Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }


    // --- Panou 1: GESTIONARE PRODUSE (CRUD) ---

    private JPanel createProductPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        String[] columnNames = {"ID", "Name", "Price", "Stock", "Type"};
        productTableModel = new DefaultTableModel(columnNames, 0);
        productTable = new JTable(productTableModel);
        panel.add(new JScrollPane(productTable), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton refreshButton = new JButton("Refresh Stock");
        JButton addButton = new JButton("Add New Product");

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

        Object[] message = {
                "Product Name:", nameField,
                "Price:", priceField,
                "Stock Quantity:", stockField,
                "Product Type:", typeDropdown
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Add New Product", JOptionPane.OK_CANCEL_OPTION);

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

    // --- Panou 2: PLASARE COMANDĂ (Tranzacție C.7) ---

    private JPanel createOrderPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // A. Panou Central (Tabel Produse stanga, Sumar Cos dreapta)
        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        JTable orderProductTable = new JTable(productTableModel); // Tabel produse stânga
        centerPanel.add(new JScrollPane(orderProductTable));

        // B. Sumar Coș (Dreapta)
        JPanel cartSummaryPanel = new JPanel(new BorderLayout(5, 5));
        cartSummaryPanel.setBorder(BorderFactory.createTitledBorder("Shopping Cart Summary"));

        cartDisplayArea = new JTextArea(8, 20); // Zonă de text pentru conținut
        cartDisplayArea.setEditable(false);
        cartStatusLabel = new JLabel("Cart: 0 unique items | Total: 0.00 RON");

        cartSummaryPanel.add(new JScrollPane(cartDisplayArea), BorderLayout.CENTER);
        cartSummaryPanel.add(cartStatusLabel, BorderLayout.SOUTH);
        centerPanel.add(cartSummaryPanel);

        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // C. Panoul de Input/Acțiune (JOS - SUD)
        JPanel inputPanel = new JPanel(new GridLayout(5, 2, 10, 5));

        selectedProductIdField = new JTextField();
        selectedProductIdField.setEditable(false);
        selectedProductNameField = new JTextField();
        selectedProductNameField.setEditable(false);

        JTextField quantityField = new JTextField();

        JButton addToCartButton = new JButton("Add to Cart");
        JButton placeOrderButton = new JButton("Place Order");

        // Logica de Selecție pe Click
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

        // Logica Adăugare în Coș
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

                // Validează stocul
                int currentCartQuantity = temporaryCart.getOrDefault(productToAdd, 0);
                if (productToAdd.getStockQuantity() < (currentCartQuantity + quantity)) {
                    throw new InvalidDataException("Insufficient stock available for this quantity.");
                }

                // Adaugă/Acumulează în coș
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

        // Logica Plasare Comandă
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
                // Apelează ServiceOrder cu produsele din coșul temporar
                serviceOrder.placeOrder(loggedInUser.get().getId(), temporaryCart);

                JOptionPane.showMessageDialog(this, "Order placed successfully! Stock updated.", "Success", JOptionPane.INFORMATION_MESSAGE);
                temporaryCart.clear();
                updateCartDisplay();
                loadProductData(); // Reîmprospătează stocul în tabel

            } catch (InvalidDataException ex) {
                JOptionPane.showMessageDialog(this, "Order Failed: " + ex.getMessage(), "Transaction Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Adăugarea componentelor la Panoul de Input
        inputPanel.add(new JLabel("Selected Product Name:"));
        inputPanel.add(selectedProductNameField);
        inputPanel.add(new JLabel("Product ID (Auto-Selected):"));
        inputPanel.add(selectedProductIdField);
        inputPanel.add(new JLabel("Quantity to Add:"));
        inputPanel.add(quantityField);

        inputPanel.add(addToCartButton);
        inputPanel.add(placeOrderButton);

        mainPanel.add(inputPanel, BorderLayout.SOUTH);
        return mainPanel;
    }

    // NOU: Metodă pentru a calcula și afișa conținutul coșului
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


    // --- Panou 3: RAPOARTE (Cerința 1) ---

    private JPanel createReportsPanel() {
        JPanel panel = new JPanel(new GridLayout(0, 1, 10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Statistics (Cerința 1)"));

        float totalValue = serviceProduct.calculateTotalStockValue();
        int totalOrders = serviceOrder.findAllOrders().size();

        JLabel valueLabel = new JLabel("Total Stock Value: " + String.format("%.2f RON", totalValue));
        JLabel ordersLabel = new JLabel("Total Orders Placed (Since Launch): " + totalOrders);

        panel.add(valueLabel);
        panel.add(ordersLabel);

        return panel;
    }

    // --- 0. ÎNCHIDERE ȘI SALVARE (Cerința 2) ---

    private void shutdownAndSave() {
        // Salvarea datelor la închiderea aplicației
        serviceProduct.shutdownApplicationAndSaveData();
        serviceClient.shutdownApplicationAndSaveData();
        serviceOrder.shutdownApplicationAndSaveData();
        System.out.println("GUI closed. Data saved successfully.");
        System.exit(0);
    }
}