package repository;

import model.Product;
import model.ProductType;

import java.io.*;
import java.util.*;

/**
 * Implementarea concreta a interfetei IRepository pentru entitatea Product.
 * Gestionează operațiunile CRUD pe o colectie Map si simuleaza lucrul cu fisiere.
 */
public class RepositoryProduct implements IRepository<Product, Integer> {

    private final Map<Integer,Product> products;
    private final String FILE_NAME = "data/products.txt";
    private static final String separator = ";";

    public RepositoryProduct(){
        this.products = new HashMap<>();
        new File("data").mkdirs();
        loadAllData();

    }

    @Override
    public void save(Product product) {
        products.put(product.getId(),product);
    }

    @Override
    public Product findById(Integer id) {
        return products.get(id);
    }

    @Override
    public List<Product> findAll() {
        return new ArrayList<>(products.values());
    }

    @Override
    public void delete(Integer id) {
        products.remove(id);
    }

    /**
     * Salveaza datele din colectia din memorie in fisierul text.
     * Serializeaza obiectul intr-un format CSV (separat prin ;)
     */
    @Override
    public void saveAllData() {
        try(PrintWriter writer = new PrintWriter(new FileWriter(FILE_NAME))){
            for(Product product:products.values()){
                String line = product.getId() + separator +
                              product.getName() + separator +
                              product.getPrice() + separator +
                              product.getStockQuantity() + separator +
                              product.getProductType() + separator +
                              product.getDescription();
                writer.println(line);
            }
        }catch(IOException e){
            throw  new DataProcessingException("Eroare I/O la salvarea datelor produselor.",e);
        }
    }

    /**
     * Incarca datele din fisierul text in colectia din memorie.
     * Deserializarea (citirea) liniilor si reconstruirea obiectelor.
     */

    @Override
    public void loadAllData() {
        File file = new File(FILE_NAME);
        if(file.exists() && file.length()>0){
            try (Scanner scanner = new Scanner(file)) {
                int lineNumber = 0;
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    lineNumber++;
                    String[] parts = line.split(separator);

                    // Validare de baza a numarului de campuri
                    if (parts.length != 6) {
                        throw new DataProcessingException("Linie invalida la randul " + lineNumber + ": numar de campuri incorect.");
                    }

                    // Parsarea obiectului
                    try {
                        int id = Integer.parseInt(parts[0].trim());
                        float price = Float.parseFloat(parts[2].trim());
                        int stock = Integer.parseInt(parts[3].trim());
                        ProductType type = ProductType.valueOf(parts[4].trim());

                        // Crearea obiectului Product
                        Product p = new Product(id, parts[1].trim(), price, type, stock, parts[5].trim());
                        products.put(id, p);
                    } catch (IllegalArgumentException e) {
                        throw new DataProcessingException("Eroare de parsare la randul " + lineNumber + ": tip de date invalid.", e);
                    }
                }
            } catch (FileNotFoundException e) {
                throw new DataProcessingException("Fișierul de produse nu a fost gasit.", e);
            } catch (DataProcessingException e) {
                System.err.println(e.getMessage());
            }
        }
        else{
            loadInitialData();
        }
    }
    private void loadInitialData() {
        if (products.isEmpty()) {
            products.put(1, new Product(1, "Laptop Basic", 3200.0f, ProductType.ELECTRONIC, 10, "Ideal pentru munca."));
            products.put(2, new Product(2, "Carte POO", 65.50f, ProductType.BOOKS, 25, "Manual academic."));
            products.put(3, new Product(3, "Bluza Casual", 150.0f, ProductType.CLOTHING, 20, "100% bumbac."));
            System.out.println("INFO: Fisierul nu a fost gasit. Colectia inițializata cu date de test.");
        }
    }
}
