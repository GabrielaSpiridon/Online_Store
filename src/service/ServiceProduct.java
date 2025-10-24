package service;

import model.Product;
import repository.IRepository;

import java.util.List;

public class ServiceProduct {
    private final IRepository<Product,Integer> productRepository;

    public ServiceProduct(IRepository<Product, Integer> productRepository) {
        this.productRepository = productRepository;
    }

    /**
     * Adauga sau actualizeaza un produs, aplicand regulile de validare.
     * @param p Produsul de salvat.
     * @throws InvalidDataException Daca pretul sau alte campuri esentiale sunt invalide.
     */

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
    /**
     * Returneaza lista tuturor produselor.
     */
    public List<Product> findAllProducts() {
        return productRepository.findAll();
    }

    /**
     * Salveaza toate datele in fisier
     */
    public void shutdownApplicationAndSaveData() {
        productRepository.saveAllData();
    }

}
