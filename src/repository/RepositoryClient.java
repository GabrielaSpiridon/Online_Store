package repository;

import model.Client;
import model.Product;
import model.ProductType;

import java.io.*;
import java.util.*;

/**
 * Implementarea concreta a IRepository pentru entitatea Client.
 * Gestioneaza operatiunile CRUD pe o colectie Map si persista datele in fisier text (clients.txt).
 * Implementeaza Cerintele 1, 2, 3, 4 (Persistenta si Colectii).
 */
public class RepositoryClient implements IRepository<Client,Integer>{

    // Cerinta 3 & 4: Colectia interna pentru stocarea in memorie
    private final Map<Integer, Client> clients;
    private final String FILE_NAME = "data/clients.txt";
    private static final String separator = ";";

    /**
     * Constructor. Initializeaza colectia si incarca datele la pornirea aplicatiei.
     */
    public RepositoryClient() {
        this.clients = new HashMap<>();
        loadAllData();
    }

    /**
     * Salveaza sau actualizeaza un client in colectia din memorie.
     * @param client Clientul de salvat/actualizat.
     */
    @Override
    public void save(Client client) {
        clients.put(client.getId(),client);
    }

    /**
     * Cauta un client dupa ID.
     * @param id ID-ul clientului (Integer - clasa wrapper).
     * @return Clientul gasit sau null.
     */
    @Override
    public Client findById(Integer id) {
        return clients.get(id);
    }

    /**
     * Returneaza lista tuturor clientilor din memorie.
     * @return Lista de obiecte Client.
     */
    @Override
    public List<Client> findAll() {
        return new ArrayList<>(clients.values());
    }

    /**
     * Sterge un client dupa ID.
     * @param id ID-ul clientului de sters (Integer - clasa wrapper).
     */
    @Override
    public void delete(Integer id) {
        clients.remove(id);
    }

    /**
     * Salveaza toate datele din memorie in fisierul text (clients.txt).
     * Implementeaza Cerinta 2 (Salvare la inchidere).
     * @throws DataProcessingException Daca apare o eroare de I/O.
     */
    @Override
    public void saveAllData() {
        try(PrintWriter writer = new PrintWriter(new FileWriter((FILE_NAME)))){
            for(Client client:clients.values()){
                // Serializarea datelor (ID, Nume, Email, Parola, Adresa, Telefon)
                String line = client.getId() + separator +
                        client.getName() + separator +
                        client.getEmail() + separator +
                        client.getPassword() + separator +
                        client.getDeliveryAddress() + separator +
                        client.getPhoneNumber() ;
                writer.println(line);
            }
        }catch(IOException e){
            throw new DataProcessingException("Error I/O in client file",e);
        }
    }

    /**
     * Incarca datele din clients.txt in colectia din memorie la pornirea aplicatiei.
     * Implementeaza Cerinta 2 (Restaurare).
     */
    @Override
    public void loadAllData() {
        File file = new File(FILE_NAME);
        if(file.exists() && file.length()>0){
            try (Scanner scanner = new Scanner(file)) {
                int lineNumber = 0;
                while(scanner.hasNextLine()){
                    String line = scanner.nextLine();
                    lineNumber++;
                    String[] parts = line.split(separator);

                    // Validare lungime (6 campuri asteptate)
                    if(parts.length != 6){
                        throw new DataProcessingException("Invalid line at row " + lineNumber + ": incorrect number of fields (expected 6).");
                    }

                    try{
                        // Parsare si reconstructie obiect Client
                        int id =Integer.parseInt(parts[0].trim());
                        Client c = new Client(id, parts[1].trim(), parts[2].trim(), parts[3].trim(), parts[4].trim(), parts[5].trim());
                        clients.put(id,c);
                    }catch (IllegalArgumentException e) {
                        // Prinde erorile de parsare (NumberFormatException, etc.)
                        throw new DataProcessingException("Parsing error at row " + lineNumber + ": invalid data type for client field.", e);
                    }
                }

            }catch(FileNotFoundException e){
                throw new DataProcessingException("Client data file not found.", e);
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
        if (clients.isEmpty()) {
            Client c1 = new Client(1, "John Smith", "john.s@example.com", "pass123", "123 Main St, NY", "0721234567");
            Client c2 = new Client(2, "Jane Doe", "jane.d@example.com", "pass456", "45 Oak Ave, CA", "0739876543");
            clients.put(c1.getId(), c1);
            clients.put(c2.getId(), c2);
            System.out.println("INFO: Client collection initialized with test data.");
        }
    }
}