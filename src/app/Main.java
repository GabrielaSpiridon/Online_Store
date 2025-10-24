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

        System.out.println("--- Online Store Application Starting ---");

        // 1. INJECȚIA DE DEPENDENȚĂ: Inițializăm Repository și Service.
        // Service-ul depinde de contractul (IRepository), nu de clasa concretă.
        IRepository<Product, Integer> productRepo = new RepositoryProduct();
        ServiceProduct productService = new ServiceProduct(productRepo);

        // La acest punct, datele au fost încărcate în memorie de către RepositoryProduct (loadAllData).

        // 2. TEST CRUD (Citire Inițială)
        System.out.println("\n--- 2. Initial Product Data ---");
        productService.findAllProducts().forEach(p -> System.out.println("Loaded: " + p.getName()));

        // 3. TEST VALIDARE (Cerința 5) și Salvare

        // A. Produs Valid
        try {
            Product pNew = new Product(10, "Smart Watch V2", 1200.0f, ProductType.ELECTRONIC, 20, "Latest model smartwatch.");
            productService.saveOrUpdateProduct(pNew);
        } catch (InvalidDataException e) {
            System.err.println("Validation Error Caught (Valid Product Test): " + e.getMessage());
        }

        // B. Produs INVALID (Preț <= 0) - Test Cerința 5
        try {
            Product pInvalid = new Product(11, "Free eBook", 0.0f, ProductType.BOOKS, 100, "Book with zero price.");
            productService.saveOrUpdateProduct(pInvalid); // Aceasta va ARUNCA excepția
        } catch (InvalidDataException e) {
            // Prindem excepția custom aruncată de ServiceProduct
            System.err.println("\nValidation Error Caught (Invalid Product Test): " + e.getMessage());
        }

        // 4. TEST LOGICĂ DE BUSINESS (Scădere Stoc)
        try {
            productService.decreaseStock(10, 5);
        } catch (InvalidDataException e) {
            System.err.println("\nStock Error: " + e.getMessage());
        }


        // 5. TEST PERSISTENȚĂ (Cerința 2: Salvare la Ieșire)
        System.out.println("\n--- 5. Saving Data to File ---");
        productService.shutdownApplicationAndSaveData();

        System.out.println("--- Application Shutting Down ---");

        // NOTA: Dacă rulezi Main a doua oară, ar trebui să vezi produsele salvate (inclusiv 'Smart Watch V2')
        // încarcate la început (Pasul 2), demonstrând persistența!
    }
}