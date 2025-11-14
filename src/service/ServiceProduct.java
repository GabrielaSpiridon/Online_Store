package service;

import model.Product;
import repository.IRepository;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Clasa ServiceProduct contine logica de business pentru entitatea Product.
 * Gestioneaza validarea datelor, calculeaza rapoarte si coordoneaza operatiunile CRUD.
 */
public class ServiceProduct {
    private final IRepository<Product,Integer> productRepository;

    private static AtomicInteger nextId = new AtomicInteger(1);

    /**
     * Constructor care injecteaza dependenta IRepository.
     * @param productRepository Repository-ul de Produse.
     */
    public ServiceProduct(IRepository<Product, Integer> productRepository) {
        this.productRepository = productRepository;
    }

    /**
     * Seteaza ID-ul de la care va incepe generarea (folosit la pornirea aplicatiei).
     * @param maxId ID-ul maxim gasit in fisier.
     */
    public static void setInitialId(int maxId) {
        if (maxId >= 0) {
            nextId.set(maxId + 1);
        }
    }


    /**
     * Adauga sau actualizeaza un produs, aplicand regulile de validare.
     * Implementeaza Cerinta 5 (Validare).
     * @param p Produsul de salvat.
     * @throws InvalidDataException Daca pretul sau alte campuri esentiale sunt invalide.
     */
    public void saveOrUpdateProduct(Product p) throws InvalidDataException{
        // 1. Validari de business
        if(p.getPrice()<=0){
            throw new InvalidDataException("The price for product '" + p.getName() + "' must be strictly positive.");
        }
        if (p.getName() == null || p.getName().trim().length() < 3) {
            throw new InvalidDataException("Product name is mandatory and must have at least 3 characters.");
        }
        if (p.getStockQuantity() < 0) {
            throw new InvalidDataException("Stock quantity cannot be negative for product '" + p.getName() + "'.");
        }

        // 2. Logica de atribuire a ID-ului
        if (p.getId() <= 0) {
            // Daca ID-ul este 0 (nou), atribuie un ID unic inainte de salvare.
            p.setId(nextId.getAndIncrement());
        }

        // 3. Salvare (Update sau Creare)
        productRepository.save(p);
    }

    /**
     * Scade stocul unui produs dupa procesarea unei comenzi.
     * Demonstreaza o logica de tranzactie complexa si coordonarea Service-urilor.
     * @param productId ID-ul produsului al carui stoc trebuie scazut.
     * @param quantity Cantitatea de scazut.
     * @throws InvalidDataException Daca stocul este insuficient sau produsul nu exista.
     */
    public void decreaseStock(int productId, int quantity) throws InvalidDataException {
        Product p = productRepository.findById(productId);

        if (p == null) {
            throw new InvalidDataException("Product with ID " + productId + " was not found.");
        }

        // Validare stoc insuficient (Cerinta 5)
        if (p.getStockQuantity() < quantity) {
            throw new InvalidDataException("Insufficient stock for product '" + p.getName() + "'. Current stock: " + p.getStockQuantity());
        }

        // Modificarea in memorie
        p.setStockQuantity(p.getStockQuantity() - quantity);

        // Salvarea modificarilor
        productRepository.save(p);
    }

    /**
     * Cauta un produs dupa ID.
     * @param id ID-ul produsului.
     * @return Produsul gasit sau null.
     */
    public Product findProductById(Integer id) {
        return productRepository.findById(id);
    }

    /**
     * Returneaza lista tuturor produselor.
     * @return Lista de obiecte Product.
     */
    public List<Product> findAllProducts() {
        return productRepository.findAll();
    }

    /**
     * Calculeaza valoarea monetara totala a intregului stoc de produse.
     * Implementeaza logica pentru Rapoarte (Cerinta 1).
     * @return Valoarea totala a stocului (Pret * Stoc).
     */
    public float calculateTotalStockValue() {
        float total = 0.0f;

        for (Product p : productRepository.findAll()) {
            total += p.getPrice() * p.getStockQuantity();
        }

        return total;
    }


    /**
     * Salveaza toate datele din memoria colectiei de produse in fisier.
     * Implementeaza Cerinta 2 (Salvare la inchidere).
     */
    public void shutdownApplicationAndSaveData() {
        productRepository.saveAllData();
    }
}