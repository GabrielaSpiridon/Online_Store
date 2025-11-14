package service;

import model.Product;
import repository.IRepository;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class ServiceProduct {
    private final IRepository<Product,Integer> productRepository;

    private static AtomicInteger nextId = new AtomicInteger(1);

    public ServiceProduct(IRepository<Product, Integer> productRepository) {
        this.productRepository = productRepository;
    }

    /**
     * Adauga sau actualizeaza un produs, aplicand regulile de validare.
     * @param p Produsul de salvat.
     * @throws InvalidDataException Daca pretul sau alte campuri esentiale sunt invalide.
     */


    /**
     * Setează ID-ul de la care va începe generarea (folosit la pornirea aplicației).
     * @param maxId ID-ul maxim găsit în fișier.
     */
    public static void setInitialId(int maxId) { // ADĂUGAT
        if (maxId >= 0) {
            nextId.set(maxId + 1);
        }
    }


    public void saveOrUpdateProduct(Product p) throws InvalidDataException{
        if(p.getPrice()<=0){
            throw new InvalidDataException("The price for product '" + p.getName() + "' must be strictly positive.");
        }
        if (p.getName() == null || p.getName().trim().length() < 3) {
            throw new InvalidDataException("Product name is mandatory and must have at least 3 characters.");
        }
        if (p.getStockQuantity() < 0) {
            throw new InvalidDataException("Stock quantity cannot be negative for product '" + p.getName() + "'.");
        }

        if (p.getId() <= 0) {
            p.setId(nextId.getAndIncrement());
        }

        productRepository.save(p);
    }

    /**
     * Scade stocul unui produs dupa procesarea unei comenzi.
     * Demonstreaza o logica de tranzactie complexa.
     */
    public void decreaseStock(int productId, int quantity) throws InvalidDataException {
        Product p = productRepository.findById(productId);

        if (p == null) {
            throw new InvalidDataException("Product with ID " + productId + " was not found.");
        }

        if (p.getStockQuantity() < quantity) {
            throw new InvalidDataException("Insufficient stock for product '" + p.getName() + "'. Current stock: " + p.getStockQuantity());
        }

        p.setStockQuantity(p.getStockQuantity() - quantity);
        productRepository.save(p);
    }

    public Product findProductById(Integer id) {
        return productRepository.findById(id);
    }

    /**
     * Returneaza lista tuturor produselor.
     */
    public List<Product> findAllProducts() {
        return productRepository.findAll();
    }

    /**
     * Calculeaza valoarea monetara totala a intregului stoc de produse.
     * Implementeaza logica pentru Rapoarte.
     */
    public float calculateTotalStockValue() {
        float total = 0.0f;

        for (Product p : productRepository.findAll()) {
            total += p.getPrice() * p.getStockQuantity();
        }

        return total;
    }



    /**
     * Salveaza toate datele in fisier
     */
    public void shutdownApplicationAndSaveData() {
        productRepository.saveAllData();
    }

}
