package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Clasa Client reprezinta un utilizator care plaseaza comenzi.
 * Extinde clasa User pentru a mosteni detaliile de autentificare (Moștenire).
 * Implementeaza Serializable pentru a permite persistenta datelor in fisiere.
 */
public class Client extends User implements Serializable {

    // Atribute specifice clientului (pe langa cele mostenite)
    private String deliveryAddress;
    private String phoneNumber;

    // Cerinta 3: Colectie de obiecte pentru istoricul comenzilor.
    private List<Order> orderHistory;

    /**
     * Constructor fara parametri.
     * Esential pentru mecanismele de I/O (citire din fisier).
     */
    public Client(){
        super();
        this.orderHistory = new ArrayList<>();
    }

    /**
     * Constructor cu parametri pentru crearea unei noi instante.
     * @param id ID-ul unic al clientului (mostenit de la User).
     * @param name Numele complet al clientului.
     * @param email Adresa de email (pentru autentificare).
     * @param password Parola.
     * @param deliveryAddress Adresa fizica de livrare.
     * @param phoneNumber Numarul de telefon.
     */
    public Client(int id, String name, String email, String password, String deliveryAddress, String phoneNumber) {
        super(id, name, email, password);
        this.deliveryAddress = deliveryAddress;
        this.phoneNumber = phoneNumber;
        this.orderHistory = new ArrayList<>();
    }

    /**
     * Returneaza adresa de livrare a clientului.
     * @return Adresa de livrare.
     */
    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    /**
     * Seteaza adresa de livrare, cu validare de baza.
     * @param deliveryAddress Noua adresa.
     */
    public void setDeliveryAddress(String deliveryAddress) {
        if(deliveryAddress != null && deliveryAddress.length() > 3){
            this.deliveryAddress = deliveryAddress;
        }
    }

    /**
     * Returneaza numarul de telefon al clientului.
     * @return Numarul de telefon.
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * Seteaza numarul de telefon, cu validare Regex de baza (minim 10 cifre).
     * @param phoneNumber Noul numar de telefon.
     */
    public void setPhoneNumber(String phoneNumber) {
        if(phoneNumber != null && phoneNumber.matches("\\d{10,}")){
            this.phoneNumber = phoneNumber;
        }
    }

    /**
     * Returneaza istoricul comenzilor plasate de client.
     * @return Lista de obiecte Order.
     */
    public List<Order> getOrderHistory() {
        return orderHistory;
    }

    /**
     * Seteaza intregul istoric de comenzi (folosit la deserializarea datelor).
     * @param orderHistory Lista de comenzi.
     */
    public void setOrderHistory(List<Order> orderHistory) {
        this.orderHistory = orderHistory;
    }

    /**
     * Adauga o noua comanda in istoricul clientului.
     * @param order Obiectul Order de adaugat.
     */
    public void addOrder(Order order){
        if(order != null){
            this.orderHistory.add(order);
        }
    }

    /**
     * Metoda toString suprascrisa pentru afisare.
     * Include detaliile mostenite de la User.
     * @return String care reprezinta starea obiectului Client.
     */
    @Override
    public String toString() {
        return "Client{" + super.toString() +
                "deliveryAddress='" + deliveryAddress + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", orderHistory=" + orderHistory +
                "} ";
    }
}