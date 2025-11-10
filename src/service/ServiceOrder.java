package service;

import model.Order;
import model.OrderStatus;
import model.Product;
import repository.IRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class ServiceOrder {
    private final IRepository<Order, Integer> orderRepository;
    private final ServiceProduct serviceProduct;

    private static AtomicInteger nextId = new AtomicInteger(1);

    public ServiceOrder(IRepository<Order, Integer> orderRepository, ServiceProduct serviceProduct) {
        this.orderRepository = orderRepository;
        this.serviceProduct = serviceProduct;
    }

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

    /**
     * Finalizeaza o comanda: calculeaza totalul, scade stocul si salveaza.
     * @param clientId ID-ul clientului care plaseaza comanda.
     * @param productsInCart Map<Product, Integer> cu produsele si cantitatea dorita.
     * @return Obiectul Order nou creat.
     * @throws InvalidDataException Daca stocul este insuficient.
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
