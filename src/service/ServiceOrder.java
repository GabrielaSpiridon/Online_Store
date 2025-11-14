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
 * Contine logica de tranzactie (plasare comanda), raportare si coordonarea stocurilor.
 */
public class ServiceOrder {
    private final IRepository<Order, Integer> orderRepository;
    private final ServiceProduct serviceProduct;

    private static AtomicInteger nextId = new AtomicInteger(1);

    /**
     * Constructor cu Injectie de Dependenta.
     * @param orderRepository Repository-ul de Comenzi.
     * @param serviceProduct Service-ul de Produse (pentru modificarea stocului).
     */
    public ServiceOrder(IRepository<Order, Integer> orderRepository, ServiceProduct serviceProduct) {
        this.orderRepository = orderRepository;
        this.serviceProduct = serviceProduct;
    }

    /**
     * Seteaza ID-ul de la care va incepe generarea (folosit la pornirea aplicatiei).
     * @param maxId ID-ul maxim gasit in fisier.
     */
    public static void setInitialId(int maxId) {
        if (maxId >= nextId.get()) {
            nextId.set(maxId + 1);
            System.out.println("SERVICE: Next Order ID set to " + nextId.get());
        }
    }

    /**
     * Returneaza lista tuturor comenzilor din memorie.
     * @return Lista de obiecte Order.
     */
    public List<Order> findAllOrders() {
        return orderRepository.findAll();
    }

    /**
     * Cauta o comanda dupa ID.
     * @param id ID-ul comenzii.
     * @return Comanda gasita sau null.
     */
    public Order findOrderById(int id) {
        return orderRepository.findById(id);
    }

    /**
     * Sterge o comanda dupa ID.
     * @param id ID-ul comenzii de sters.
     */
    public void deleteOrder(int id) {
        orderRepository.delete(id);
        System.out.println("SERVICE: Order " + id + " deleted successfully.");
    }

    // Metoda ajutatoare pentru a gasi detaliile Produsului REAL folosind ServiceProduct.
    private Product findRealProductDetails(int productId) {
        return serviceProduct.findProductById(productId);
    }

    /**
     * Calculeaza numarul total de unitati comandate pentru fiecare produs (Cerinta 1).
     * @return Map<String, Integer> unde cheia este numele produsului, iar valoarea este totalul unitatilor vandute.
     */
    public Map<String, Integer> getUnitsSoldPerProduct() {
        Map<String, Integer> salesReport = new HashMap<>();
        List<Order> allOrders = orderRepository.findAll();

        for (Order order : allOrders) {
            for (Map.Entry<Product, Integer> item : order.getProducts().entrySet()) {

                int productId = item.getKey().getId();
                // Folosim ServiceProduct pentru a gasi numele corect din stocul activ
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
     * Implementeaza logica de tranzactie.
     * @param clientId ID-ul clientului care plaseaza comanda.
     * @param productsInCart Map<Product, Integer> cu produsele si cantitatea dorita.
     * @return Obiectul Order nou creat.
     * @throws InvalidDataException Daca stocul este insuficient.
     */
    public Order placeOrder(int clientId, Map<Product, Integer> productsInCart) throws InvalidDataException{
        // 1. Pre-validarea stocului
        for(Map.Entry<Product, Integer> entry : productsInCart.entrySet()){
            Product product = entry.getKey();
            int requestedQuantity = entry.getValue();

            if(product.getStockQuantity() < requestedQuantity){
                throw new InvalidDataException("Insufficient stock for product: " + product.getName() +
                        ". Available: " + product.getStockQuantity() + ", Requested: " + requestedQuantity);
            }
        }

        // 2. Calcul
        float totalAmount = calculateTotal(productsInCart);

        // 3. Creare Order
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

        // 4. Scadere Stoc (Coordonarea cu ServiceProduct)
        for(Map.Entry<Product, Integer> entry: productsInCart.entrySet()){
            serviceProduct.decreaseStock(entry.getKey().getId(),entry.getValue());
        }

        System.out.println("SERVICE: Order " + orderId + " placed successfully. Total: " + totalAmount);
        return newOrder;
    }

    /**
     * Metoda privata pentru a calcula valoarea totala a produselor din cos.
     */
    private float calculateTotal(Map<Product, Integer> products) {
        float total = 0;
        for (Map.Entry<Product, Integer> entry : products.entrySet()) {
            total += entry.getKey().getPrice() * entry.getValue();
        }
        return total;
    }

    /**
     * Salveaza toate datele comenzilor in fisier inainte de oprirea aplicatiei.
     */
    public void shutdownApplicationAndSaveData() {
        orderRepository.saveAllData();
        System.out.println("SERVICE: Order data saved successfully.");
    }
}