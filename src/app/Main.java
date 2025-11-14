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

/**
 * Clasa principala (main) a aplicatiei.
 * Se ocupa de initializarea tuturor componentelor (Repository, Service) si de pornirea interfetei grafice (GUI).
 * Aplica principiul Injectiei de Dependente.
 */
public class Main {

    /**
     * Metoda ajutatoare generica pentru a gasi cel mai mare ID existent in colectia de entitati.
     * Este folosita pentru a seta punctul de start al generatorului de ID-uri (AtomicInteger).
     * * @param <T> Tipul entitatii (Product, Client, Order).
     * @param entities Lista de entitati incarcate din fisier.
     * @param idExtractor Functie care extrage ID-ul din entitate (ex: Product::getId).
     * @return ID-ul maxim gasit sau 0 daca lista este goala.
     */
    private static <T> int findMaxId(List<T> entities, java.util.function.ToIntFunction<T> idExtractor) {
        return entities.stream()
                .mapToInt(idExtractor)
                .max().orElse(0);
    }

    /**
     * Metoda principala de executie a aplicatiei.
     * * @param args Argumente din linia de comanda (neutilizate).
     */

    public static void main(String[] args) {

        System.out.println("--- Online Store Application Starting GUI ---");

        // 1. INITIALIZAREA REPOSITORIES SI SERVICE-URILOR
        IRepository<Product, Integer> productRepo = new RepositoryProduct();
        IRepository<Client, Integer> clientRepo = new RepositoryClient();
        IRepository<Order, Integer> orderRepo = new RepositoryOrder();

        ServiceProduct productService = new ServiceProduct(productRepo);
        ServiceClient clientService = new ServiceClient(clientRepo);
        ServiceOrder orderService = new ServiceOrder(orderRepo, productService);

        // 2. GESTIUNEA ID-urilor la pornire

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