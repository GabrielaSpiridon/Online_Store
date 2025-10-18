package model;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Order {
    private int id;
    private int clientId;

    private Map<Product,Integer> products;

    private LocalDateTime orderDate;
    private OrderStatus status;
    private float totalAmount;

    public Order(){
        this.products = new HashMap<>();
        this.orderDate = LocalDateTime.now();
        this.status = OrderStatus.PENDING;
    }

    public Order(int id, int clientId, Map<Product, Integer> products, LocalDateTime orderDate, OrderStatus status, float totalAmount) {
        this.id = id;
        this.clientId = clientId;
        this.products = products;
        this.orderDate = orderDate;
        this.status = status;
        this.totalAmount = totalAmount;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        if(id>0){
            this.id = id;
        }
    }

    public int getClientId() {
        return clientId;
    }

    public void setClientId(int clientId) {
        if(clientId>0){
            this.clientId = clientId;
        }
    }

    public Map<Product, Integer> getProducts() {
        return products;
    }

    public void setProducts(Map<Product, Integer> products) {
        this.products = products;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
       if(status!=null){
           this.status = status;
       }
    }

    public float getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(float totalAmount) {
        if(totalAmount>=0){
            this.totalAmount = totalAmount;
        }
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", clientId=" + clientId +
                ", products=" + products +
                ", orderDate=" + orderDate +
                ", status=" + status +
                ", totalAmount=" + totalAmount +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return id == order.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public void addProduct(Product product,int quantity){
        if (product != null && quantity > 0) {
            this.products.put(product, quantity);
        }
    }
}
