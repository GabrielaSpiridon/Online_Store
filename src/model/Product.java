package model;

import java.io.Serializable;
import java.util.Objects;

/**
 * Clasa Product reprezinta un produs fizic sau digital disponibil in magazinul online.
 * Implementeaza Serializable pentru a permite persistenta datelor in fisiere.
 * Aplica principiul Incapsularii (campuri private, acces prin Getters/Setters).
 */
public class Product implements Serializable {
    private int id;
    private String name;
    private float price;
    private ProductType productType;
    private int stockQuantity;
    private String description;

    /**
     * Constructor fara parametri.
     * Esential pentru mecanismele de I/O (citire din fisier).
     */
    public Product(){

    }

    /**
     * Constructor cu parametri pentru crearea unei noi instante.
     * @param id ID-ul unic al produsului.
     * @param name Numele produsului.
     * @param price Pretul unitar.
     * @param productType Tipul/categoria produsului (enum).
     * @param stockQuantity Stocul initial.
     * @param description Descrierea produsului.
     */
    public Product(int id, String name, float price, ProductType productType, int stockQuantity, String description) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.productType = productType;
        this.stockQuantity = stockQuantity;
        this.description = description;
    }

    /**
     * Returneaza ID-ul produsului.
     * @return ID-ul produsului.
     */
    public int getId() {
        return id;
    }

    /**
     * Seteaza ID-ul produsului. Se face o validare de baza (ID pozitiv).
     * @param id Noul ID.
     */
    public void setId(int id) {
        if(id > 0){
            this.id = id;
        }
    }

    /**
     * Returneaza numele produsului.
     * @return Numele.
     */
    public String getName() {
        return name;
    }

    /**
     * Seteaza numele produsului, cu validare de baza.
     * @param name Noul nume.
     */
    public void setName(String name) {
        if(name != null && name.length() >= 3){
            this.name = name;
        }
    }

    /**
     * Returneaza pretul produsului.
     * @return Pretul.
     */
    public float getPrice() {
        return price;
    }

    /**
     * Seteaza pretul produsului, cu validare de baza (pret pozitiv).
     * @param price Noul pret.
     */
    public void setPrice(float price) {
        if(price > 0.0){
            this.price = price;
        }
    }

    /**
     * Returneaza tipul produsului (categoria).
     * @return Tipul produsului (enum).
     */
    public ProductType getProductType() {
        return productType;
    }

    /**
     * Seteaza tipul produsului, cu validare de baza.
     * @param productType Noul tip.
     */
    public void setProductType(ProductType productType) {
        if (productType != null){
            this.productType = productType;
        }
    }

    /**
     * Returneaza cantitatea de stoc disponibila.
     * @return Cantitatea stoc.
     */
    public int getStockQuantity() {
        return stockQuantity;
    }

    /**
     * Seteaza cantitatea de stoc, cu validare de baza (stoc nenegativ).
     * @param stockQuantity Noua cantitate stoc.
     */
    public void setStockQuantity(int stockQuantity) {
        if(stockQuantity >= 0){
            this.stockQuantity = stockQuantity;
        }
    }

    /**
     * Returneaza descrierea produsului.
     * @return Descrierea.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Seteaza descrierea produsului.
     * @param description Noua descriere.
     */
    public void setDescription(String description) {
        if(description != null && description.length() > 3){
            this.description = description;
        }
    }

    /**
     * Metoda toString suprascrisa pentru afisarea starii produsului.
     * @return String care reprezinta starea obiectului Product.
     */
    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", productType=" + productType +
                ", stockQuantity=" + stockQuantity +
                ", description='" + description + '\'' +
                '}';
    }

    /**
     * Metoda equals suprascrisa.
     * Doua produse sunt egale daca au acelasi ID (cheie primara).
     * @param o Obiectul de comparat.
     * @return true daca obiectele sunt egale, false altfel.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return id == product.id;
    }

    /**
     * Metoda hashCode suprascrisa. Se bazeaza pe ID pentru a respecta contractul equals/hashCode.
     * @return Hash code-ul obiectului.
     */
    @Override
    public int hashCode() {
        return Objects.hash(id, name, price, productType, stockQuantity, description);
    }
}