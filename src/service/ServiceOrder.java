package service;

import model.Order;
import model.OrderStatus;
import model.Product;
import repository.IRepository;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Service Class pentru entitatea Order.
 * Contine logica de tranzactie (plasare comanda) si raportare.
 */
public class ServiceOrder {
    private final IRepository<Order, Integer> orderRepository;
    private final ServiceProduct serviceProduct;

    private static AtomicInteger nextId = new AtomicInteger(1);

    /**
     * Constructor cu Injectie de Dependenta.
     */
    public ServiceOrder(IRepository<Order, Integer> orderRepository, ServiceProduct serviceProduct) {
        this.orderRepository = orderRepository;
        this.serviceProduct = serviceProduct;
    }

    /**
     * Seteaza ID-ul de la care va incepe generarea (folosit la pornirea aplicatiei).
     */
    public static void setInitialId(int maxId) {
        if (maxId >= nextId.get()) {
            nextId.set(maxId + 1);
            System.out.println("SERVICE: Next Order ID set to " + nextId.get());
        }
    }

    public List<Order> findAllOrders() {
        return orderRepository.findAll();
    }

    public Order findOrderById(int id) {
        return orderRepository.findById(id);
    }

    public void deleteOrder(int id) {
        orderRepository.delete(id);
        System.out.println("SERVICE: Order " + id + " deleted successfully.");
    }

    // Metoda ajutatoare pentru a gasi detaliile Produsului REAL folosind ServiceProduct.
    private Product findRealProductDetails(int productId) {
        return serviceProduct.findProductById(productId);
    }

    /**
     * Calculeaza numarul total de unitati comandate pentru fiecare produs.
     * Aceasta functie foloseste ProductService pentru a gasi numele corect al produsului.
     * @return Map<String, Integer> unde cheia este numele produsului, iar valoarea este totalul unitatilor vandute.
     */
    public Map<String, Integer> getUnitsSoldPerProduct() {
        Map<String, Integer> salesReport = new HashMap<>();
        List<Order> allOrders = orderRepository.findAll();

        for (Order order : allOrders) {
            for (Map.Entry<Product, Integer> item : order.getProducts().entrySet()) {

                int productId = item.getKey().getId();
                Product realProduct = findRealProductDetails(productId);

                if (realProduct != null) {
                    String productName = realProduct.getName();
                    int quantitySold = item.getValue();
                    salesReport.put(productName, salesReport.getOrDefault(productName, 0) + quantitySold);
                }

            }
        }
        return salesReport;
    }


    /**
     * Finalizeaza o comanda: calculeaza totalul, scade stocul si salveaza.
     */
    public Order placeOrder(int clientId, Map<Product, Integer> productsInCart) throws InvalidDataException{
        for(Map.Entry<Product, Integer> entry : productsInCart.entrySet()){
            Product product = entry.getKey();
            int requestedQuantity = entry.getValue();

            if(product.getStockQuantity() < requestedQuantity){
                throw new InvalidDataException("Insufficient stock for product: " + product.getName() +
                        ". Available: " + product.getStockQuantity() + ", Requested: " + requestedQuantity);
            }
        }

        float totalAmount = calculateTotal(productsInCart);

        int orderId = nextId.getAndIncrement();

        Order newOrder = new Order(
                orderId,
                clientId,
                productsInCart,
                LocalDateTime.now(),
                OrderStatus.PENDING,
                totalAmount
        );

        orderRepository.save(newOrder);

        for(Map.Entry<Product, Integer> entry: productsInCart.entrySet()){
            serviceProduct.decreaseStock(entry.getKey().getId(),entry.getValue());
        }

        System.out.println("SERVICE: Order " + orderId + " placed successfully. Total: " + totalAmount);
        return newOrder;
    }

    private float calculateTotal(Map<Product, Integer> products) {
        float total = 0;
        for (Map.Entry<Product, Integer> entry : products.entrySet()) {
            total += entry.getKey().getPrice() * entry.getValue();
        }
        return total;
    }

    public void shutdownApplicationAndSaveData() {
        orderRepository.saveAllData();
        System.out.println("SERVICE: Order data saved successfully.");
    }
}