package app;

import model.Product;
import model.ProductType;
import repository.IRepository;
import repository.RepositoryProduct;
import service.InvalidDataException;
import service.ServiceProduct;
import java.util.List;

public class Main {
    public static void main(String[] args) {

        System.out.println("--- Online Store ---");

        IRepository<Product, Integer> productRepo = new RepositoryProduct();
        ServiceProduct productService = new ServiceProduct(productRepo);

        System.out.println("\n--- 2. Initial Product Data ---");
        productService.findAllProducts().forEach(p -> System.out.println("Loaded: " + p.getName()));

        try {
            Product pNew = new Product(10, "Smart Watch V2", 1200.0f, ProductType.ELECTRONIC, 20, "Latest model smartwatch.");
            productService.saveOrUpdateProduct(pNew);
        } catch (InvalidDataException e) {
            System.err.println("Validation Error Caught (Valid Product Test): " + e.getMessage());
        }

        try {
            Product pInvalid = new Product(11, "Free eBook", 0.0f, ProductType.BOOKS, 100, "Book with zero price.");
            productService.saveOrUpdateProduct(pInvalid);
        } catch (InvalidDataException e) {
            System.err.println("\nValidation Error Caught (Invalid Product Test): " + e.getMessage());
        }

        try {
            productService.decreaseStock(10, 5);
        } catch (InvalidDataException e) {
            System.err.println("\nStock Error: " + e.getMessage());
        }

        productService.shutdownApplicationAndSaveData();


    }
}