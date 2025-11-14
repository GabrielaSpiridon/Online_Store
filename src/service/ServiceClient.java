package service;

import model.Client;
import repository.IRepository;
import java.util.List;
import java.util.Optional; // Import nou pentru metoda authenticate
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Clasa ServiceClient contine logica de business pentru entitatea Client.
 * Gestioneaza validarea datelor, autentificarea si coordoneaza operatiunile CRUD.
 */
public class ServiceClient {

    private final IRepository<Client, Integer> clientRepository;
    private static AtomicInteger nextId = new AtomicInteger(1);

    /**
     * Constructor care injecteaza dependenta IRepository.
     * @param clientRepository Repository-ul de Clienti.
     */
    public ServiceClient(IRepository<Client, Integer> clientRepository) {
        this.clientRepository = clientRepository;
    }

    /**
     * Seteaza ID-ul de la care va incepe generarea (folosit la pornirea aplicatiei).
     * @param maxId ID-ul maxim gasit in fisier.
     */
    public static void setInitialId(int maxId) {
        if (maxId >= 0) {
            nextId.set(maxId + 1);
        }
    }


    /**
     * Valideaza datele unui obiect Client (Regula de Business).
     * @param client Obiectul Client de validat.
     * @throws InvalidDataException Daca datele nu sunt valide.
     */
    private void validateClient(Client client) throws InvalidDataException {
        if (client.getEmail() == null || !client.getEmail().contains("@") || !client.getEmail().contains(".")) {
            throw new InvalidDataException("Client email is invalid: must contain '@' and '.'.");
        }
        if (client.getPassword() == null || client.getPassword().length() < 6) {
            throw new InvalidDataException("Client password is invalid: must be at least 6 characters long.");
        }
        if (client.getName() == null || client.getName().trim().isEmpty()) {
            throw new InvalidDataException("Client name cannot be empty.");
        }
    }

    // --- Metode CRUD ---

    /**
     * Salveaza sau actualizeaza un client (Register).
     * Aplica validarea si atribuie un ID nou daca este o inregistrare noua.
     * @param client Clientul de salvat/actualizat.
     * @throws InvalidDataException Daca datele clientului nu sunt valide.
     */
    public void saveOrUpdateClient(Client client) throws InvalidDataException {
        validateClient(client);

        if (client.getId() <= 0) {
            // Salvare (Creare) - Atribuie un ID nou
            client.setId(nextId.getAndIncrement());
            clientRepository.save(client);
        } else {
            // Actualizare (Update)
            clientRepository.save(client);
        }
    }

    /**
     * Gaseste un client dupa ID.
     * @param id ID-ul clientului.
     * @return Clientul gasit sau null.
     */
    public Client findClientById(Integer id) {
        return clientRepository.findById(id);
    }

    /**
     * Returneaza toti clientii.
     * @return Lista tuturor clientilor.
     */
    public List<Client> findAllClients() {
        return clientRepository.findAll();
    }

    /**
     * Sterge un client dupa ID.
     * @param id ID-ul clientului de sters.
     * @throws InvalidDataException Daca ID-ul nu este valid sau clientul nu exista.
     */
    public void deleteClient(Integer id) throws InvalidDataException {
        if (clientRepository.findById(id) == null) {
            throw new InvalidDataException("Client with ID " + id + " was not found for deletion.");
        }
        clientRepository.delete(id);
    }


    /**
     * Autentifica un client pe baza email-ului si parolei (Login).
     * @param email Email-ul introdus.
     * @param password Parola introdusa.
     * @return Optional<Client> care contine clientul gasit sau este gol.
     */
    public Optional<Client> authenticate(String email, String password) {

        for (Client c : clientRepository.findAll()) {
            // Compara email-ul (insensibil la majuscule) si parola (sensibila)
            if (c.getEmail().equalsIgnoreCase(email) && c.getPassword().equals(password)) {
                return Optional.of(c);
            }
        }
        return Optional.empty();
    }

    // --- Persistenta ---

    /**
     * Salveaza toate datele clientilor in fisier inainte de oprirea aplicatiei.
     */
    public void shutdownApplicationAndSaveData() {
        clientRepository.saveAllData();
    }
}