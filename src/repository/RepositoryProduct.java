package repository;

import model.Product;
import model.ProductType;

import java.io.*;
import java.util.*;

/**
 * Implementarea concreta a interfetei IRepository pentru entitatea Product.
 * Gestioneaza operatiunile CRUD pe o colectie Map si persista datele in fisier text (products.txt).
 * Implementeaza Cerintele 1, 2, 3, 4 (Persistenta, Colectii).
 */
public class RepositoryProduct implements IRepository<Product, Integer> {

    // Cerinta 3 & 4: Colectia interna pentru stocarea in memorie
    private final Map<Integer,Product> products;
    private final String FILE_NAME = "data/products.txt";
    private static final String separator = ";";

    /**
     * Constructor. Initializeaza colectia si incarca datele la pornirea aplicatiei.
     */
    public RepositoryProduct(){
        this.products = new HashMap<>();
        // Creeaza directorul 'data' daca nu exista
        new File("data").mkdirs();
        loadAllData();
    }

    /**
     * Salveaza sau actualizeaza un produs in colectia din memorie.
     * @param product Produsul de salvat/actualizat.
     */
    @Override
    public void save(Product product) {
        products.put(product.getId(),product);
    }

    /**
     * Cauta un produs dupa ID.
     * @param id ID-ul produsului (Integer - clasa wrapper).
     * @return Produsul gasit sau null.
     */
    @Override
    public Product findById(Integer id) {
        return products.get(id);
    }

    /**
     * Returneaza lista tuturor produselor din memorie.
     * @return Lista de obiecte Product.
     */
    @Override
    public List<Product> findAll() {
        return new ArrayList<>(products.values());
    }

    /**
     * Sterge un produs dupa ID.
     * @param id ID-ul produsului de sters (Integer - clasa wrapper).
     */
    @Override
    public void delete(Integer id) {
        products.remove(id);
    }

    /**
     * Salveaza datele din colectia din memorie in fisierul text (products.txt).
     * Serializeaza obiectul intr-un format CSV (separat prin ;). Implementeaza Cerinta 2.
     * @throws DataProcessingException Daca apare o eroare de I/O.
     */
    @Override
    public void saveAllData() {
        try(PrintWriter writer = new PrintWriter(new FileWriter(FILE_NAME))){
            for(Product product:products.values()){
                // Serializarea datelor in format text (ID;Nume;Pret;Stoc;Tip;Descriere)
                String line = product.getId() + separator +
                        product.getName() + separator +
                        product.getPrice() + separator +
                        product.getStockQuantity() + separator +
                        product.getProductType() + separator +
                        product.getDescription();
                writer.println(line);
            }
        }catch(IOException e){
            throw  new DataProcessingException("Error I/O in product file",e);
        }
    }

    /**
     * Incarca datele din fisierul text in colectia din memorie.
     * Deserializarea (citirea) liniilor si reconstruirea obiectelor. Implementeaza Cerinta 2.
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
                        throw new DataProcessingException("Invalid line at row " + lineNumber + ": incorrect number of fields (expected 6).");
                    }

                    // Parsarea obiectului
                    try {
                        int id = Integer.parseInt(parts[0].trim());
                        float price = Float.parseFloat(parts[2].trim());
                        int stock = Integer.parseInt(parts[3].trim());
                        ProductType type = ProductType.valueOf(parts[4].trim()); // Convertire String -> Enum

                        // Crearea obiectului Product
                        Product p = new Product(id, parts[1].trim(), price, type, stock, parts[5].trim());
                        products.put(id, p);
                    } catch (IllegalArgumentException e) {
                        // Prinde erorile de parsare (NumberFormatException, erori Enum.valueOf)
                        throw new DataProcessingException("Parsing error at row " + lineNumber + ": invalid data type for product field.", e);
                    }
                }
            } catch (FileNotFoundException e) {
                throw new DataProcessingException("Product data file not found.", e);
            } catch (DataProcessingException e) {
                System.err.println(e.getMessage());
            }
        }
        else{
            loadInitialData();
        }
    }

    /**
     * Incarca date initiale de test in colectia din memorie daca fisierul este gol.
     */
    private void loadInitialData() {
        if (products.isEmpty()) {
            products.put(1, new Product(1, "Laptop Basic", 3200.0f, ProductType.ELECTRONIC, 10, "Ideal pentru munca."));
            products.put(2, new Product(2, "Carte POO", 65.50f, ProductType.BOOKS, 25, "Manual academic."));
            products.put(3, new Product(3, "Bluza Casual", 150.0f, ProductType.CLOTHING, 20, "100% bumbac."));
            System.out.println("INFO: Fisierul nu a fost gasit. Colectia initializata cu date de test.");
        }
    }
}