package repository;

import model.Order;
import model.OrderStatus;
import model.Product;
import model.ProductType;

import java.io.*;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Implementarea concreta a IRepository pentru entitatea Order.
 * Gestioneaza operatiunile CRUD pe o colectie Map si persistenta in fisier text (orders.txt).
 */

public class RepositoryOrder implements IRepository<Order,Integer>{
    private final Map<Integer, Order> orders;
    private final String FILE_NAME = "data/orders.txt";
    private static final String separator = ";";
    // Formatter necesar pentru a converti LocalDateTime in String si invers (Cerinta 1)
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public RepositoryOrder(){
        this.orders = new HashMap<>();
        loadAllData();
    }

    // ----------------------------------------------------------------------
    // Implementarea Operatiunilor CRUD (din IRepository)
    // ----------------------------------------------------------------------

    @Override
    public void save(Order order) {
        orders.put(order.getId(),order);
    }

    @Override
    public Order findById(Integer id) {
        return orders.get(id);
    }

    @Override
    public List<Order> findAll() {
        return new ArrayList<>(orders.values());
    }

    @Override
    public void delete(Integer id) {
        orders.remove(id);
    }

    @Override
    public void saveAllData() {
        try(PrintWriter writer = new PrintWriter(new FileWriter(FILE_NAME))){
            for(Order order: orders.values()){
                String productsString = serializeProducts(order.getProducts());

                String line = order.getId() + separator +
                              order.getClientId() + separator +
                              productsString + separator +
                              order.getOrderDate().format(DATE_FORMATTER) + separator +
                              order.getStatus() + separator +
                              order.getTotalAmount();
                writer.println(line);
            }
        } catch (IOException e){
            throw new DataProcessingException("I/O Error while saving order data.", e);
        }
    }

    @Override
    public void loadAllData() {
        File file = new File(FILE_NAME);
        if(file.exists() && file.length()>0){
            try(Scanner scanner = new Scanner(file)){
                int lineNumber = 0;
                while(scanner.hasNextLine()){
                    String line = scanner.nextLine();
                    lineNumber++;
                    String[] parts = line.split(separator);

                    if(parts.length!= 6){
                        throw new DataProcessingException("Invalid line at row " + lineNumber + ": incorrect number of fields (expected 6).");
                    }

                    try{
                        int id = Integer.parseInt(parts[0].trim());
                        int clientId = Integer.parseInt(parts[1].trim());

                        // NOU: Deserializarea folosește o metodă care nu mai creează un mock
                        Map<Product,Integer> productsMap = deserializeProducts(parts[2].trim());

                        LocalDateTime orderDate = LocalDateTime.parse(parts[3].trim(),DATE_FORMATTER);
                        OrderStatus status = OrderStatus.valueOf(parts[4].trim());
                        float totalAmount = Float.parseFloat(parts[5].trim());

                        Order o = new Order(id, clientId, productsMap, orderDate,status, totalAmount);
                        orders.put(id,o);
                    }catch (IllegalArgumentException e){
                        throw new DataProcessingException("Parsing error at row " + lineNumber + ": invalid data type for order field.", e);
                    }
                }
            } catch (FileNotFoundException e) {
                throw new DataProcessingException("Order data file not found.", e);
            } catch (DataProcessingException e){
                System.err.println("PERSISTENCE ERROR: " + e.getMessage());
            }
        }
    }


    private String serializeProducts(Map<Product, Integer> products){
        if (products.isEmpty()){
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for(Map.Entry<Product, Integer> entry : products.entrySet()){
            sb.append(entry.getKey().getId())
                    .append(":")
                    .append(entry.getValue())
                    .append("|");
        }
        sb.deleteCharAt(sb.length()-1);
        return sb.toString();
    }

    private Map<Product, Integer> deserializeProducts(String productsString){
        Map<Product, Integer> productsMap = new HashMap<>();
        if(productsString.isEmpty()){
            return productsMap;
        }

        String[] productPairs = productsString.split("\\|");
        for( String pair: productPairs){
            String[] parts = pair.split(":");
            if(parts.length == 2){
                try{
                    int productId = Integer.parseInt(parts[0].trim());
                    int quantity = Integer.parseInt(parts[1].trim());

                    Product minimalProduct = new Product(productId, "N/A", 0.0f, ProductType.ELECTRONIC, 0, "Minimal");

                    productsMap.put(minimalProduct,quantity);
                }catch(NumberFormatException e){
                    // Ignorăm
                }
            }
        }
        return productsMap;
    }
}
