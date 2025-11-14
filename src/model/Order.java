package model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Clasa Order reprezinta o tranzactie sau o comanda plasata de un client.
 * Contine detaliile comenzii, starea si o colectie de produse.
 * Implementeaza Serializable pentru a permite persistenta datelor in fisiere.
 */
public class Order implements Serializable {
    private int id;
    private int clientId;

    // Cerinta 3: Colectie de obiecte (Map) pentru a stoca Produsele si Cantitatea.
    private Map<Product,Integer> products;

    private LocalDateTime orderDate;
    private OrderStatus status;
    private float totalAmount;

    /**
     * Constructor fara parametri.
     * Initializeaza colectia de produse, data comenzii la momentul curent si statusul PENDING.
     */
    public Order(){
        this.products = new HashMap<>();
        this.orderDate = LocalDateTime.now();
        this.status = OrderStatus.PENDING;
    }

    /**
     * Constructor cu parametri pentru crearea unei noi comenzi.
     * @param id ID-ul unic al comenzii.
     * @param clientId ID-ul clientului care a plasat comanda.
     * @param products Colectia de produse si cantitatile comandate.
     * @param orderDate Data si ora comenzii.
     * @param status Statusul comenzii (PENDING, SHIPPED, etc.).
     * @param totalAmount Suma totala a comenzii.
     */
    public Order(int id, int clientId, Map<Product, Integer> products, LocalDateTime orderDate, OrderStatus status, float totalAmount) {
        this.id = id;
        this.clientId = clientId;
        this.products = products;
        this.orderDate = orderDate;
        this.status = status;
        this.totalAmount = totalAmount;
    }

    /**
     * Returneaza ID-ul comenzii.
     * @return ID-ul comenzii.
     */
    public int getId() {
        return id;
    }

    /**
     * Seteaza ID-ul comenzii. Se face o validare de baza (ID pozitiv).
     * @param id Noul ID.
     */
    public void setId(int id) {
        if(id > 0){
            this.id = id;
        }
    }

    /**
     * Returneaza ID-ul clientului care a plasat comanda.
     * @return ID-ul clientului.
     */
    public int getClientId() {
        return clientId;
    }

    /**
     * Seteaza ID-ul clientului, cu validare de baza.
     * @param clientId Noul ID al clientului.
     */
    public void setClientId(int clientId) {
        if(clientId > 0){
            this.clientId = clientId;
        }
    }

    /**
     * Returneaza colectia de produse si cantitati din comanda.
     * @return Map<Product, Integer> cu produsele si cantitatile.
     */
    public Map<Product, Integer> getProducts() {
        return products;
    }

    /**
     * Seteaza intregul Map de produse (folosit la deserializare).
     * @param products Noul Map de produse.
     */
    public void setProducts(Map<Product, Integer> products) {
        this.products = products;
    }

    /**
     * Returneaza data si ora comenzii.
     * @return Obiect LocalDateTime.
     */
    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    /**
     * Seteaza data comenzii.
     * @param orderDate Noua data si ora.
     */
    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    /**
     * Returneaza statusul comenzii.
     * @return Obiect OrderStatus (enum).
     */
    public OrderStatus getStatus() {
        return status;
    }

    /**
     * Seteaza statusul comenzii.
     * @param status Noul status.
     */
    public void setStatus(OrderStatus status) {
        if(status != null){
            this.status = status;
        }
    }

    /**
     * Returneaza suma totala a comenzii.
     * @return Suma totala.
     */
    public float getTotalAmount() {
        return totalAmount;
    }

    /**
     * Seteaza suma totala a comenzii, cu validare de baza (suma pozitiva).
     * @param totalAmount Noua suma totala.
     */
    public void setTotalAmount(float totalAmount) {
        if(totalAmount >= 0){
            this.totalAmount = totalAmount;
        }
    }

    /**
     * Metoda toString suprascrisa pentru afisarea starii comenzii.
     * @return String care reprezinta starea obiectului Order.
     */
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

    /**
     * Metoda equals suprascrisa. Doua comenzi sunt egale daca au acelasi ID.
     * @param o Obiectul de comparat.
     * @return true daca obiectele sunt egale, false altfel.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return id == order.id;
    }

    /**
     * Metoda hashCode suprascrisa, bazata pe ID.
     * @return Hash code-ul obiectului.
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    /**
     * Adauga un produs si o cantitate in Map-ul de produse.
     * @param product Produsul de adaugat.
     * @param quantity Cantitatea.
     */
    public void addProduct(Product product,int quantity){
        if (product != null && quantity > 0) {
            // Logica: Inlocuieste produsul existent sau adauga unul nou.
            this.products.put(product, quantity);
        }
    }
}