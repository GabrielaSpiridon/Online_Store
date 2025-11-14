package app;

import model.Client;
import model.Order;
import model.Product;
import repository.*;
import service.*;
import ui.StoreGUI;
import java.util.Comparator;
import java.util.List;
import javax.swing.SwingUtilities;

public class Main {

    // Metodă ajutătoare pentru a găsi ID-ul maxim (simplificat)
    // Folosește un extractor de funcții pentru a obține ID-ul din orice entitate
    private static <T> int findMaxId(List<T> entities, java.util.function.ToIntFunction<T> idExtractor) {
        return entities.stream()
                .mapToInt(idExtractor)
                .max().orElse(0);
    }

    public static void main(String[] args) {

        System.out.println("--- Online Store Application Starting GUI ---");

        // 1. INIȚIALIZAREA REPOSITORIES ȘI SERVICE-URILOR
        IRepository<Product, Integer> productRepo = new RepositoryProduct();
        IRepository<Client, Integer> clientRepo = new RepositoryClient();
        IRepository<Order, Integer> orderRepo = new RepositoryOrder();

        ServiceProduct productService = new ServiceProduct(productRepo);
        ServiceClient clientService = new ServiceClient(clientRepo);
        ServiceOrder orderService = new ServiceOrder(orderRepo, productService);

        // 2. GESTIUNEA ID-urilor la pornire (Cerința 2: Prevenirea Coliziunilor)

        // Product::getId este o referință la metoda, folosită de idExtractor
        ServiceProduct.setInitialId(findMaxId(productService.findAllProducts(), Product::getId));
        ServiceClient.setInitialId(findMaxId(clientService.findAllClients(), Client::getId));
        ServiceOrder.setInitialId(findMaxId(orderService.findAllOrders(), Order::getId));

        System.out.println("Initialization complete. Data loaded and IDs set.");

        // 3. PORNIREA INTERFEȚEI GRAFICE (Cerința 7)
        SwingUtilities.invokeLater(() -> {
            new StoreGUI(productService, clientService, orderService);
        });
    }
}