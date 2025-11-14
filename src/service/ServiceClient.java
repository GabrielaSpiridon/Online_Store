package service;

import model.Client;
import repository.IRepository;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.Optional;

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
     * @param maxId ID-ul maxim gasit în fisier.
     */
    public static void setInitialId(int maxId) {
        if (maxId >= 0) {
            nextId.set(maxId + 1);
        }
    }


    /**
     * Valideaza datele unui obiect Client.
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
     * Salvează sau actualizează un client.
     * @param client Clientul de salvat/actualizat.
     * @throws InvalidDataException Dacă datele clientului nu sunt valide.
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
     * Găsește un client după ID.
     * @param id ID-ul clientului.
     * @return Clientul găsit sau null.
     */
    public Client findClientById(Integer id) {
        return clientRepository.findById(id);
    }

    /**
     * Returnează toți clienții.
     * @return Lista tuturor clienților.
     */
    public List<Client> findAllClients() {
        return clientRepository.findAll();
    }

    /**
     * Șterge un client după ID.
     * @param id ID-ul clientului de șters.
     * @throws InvalidDataException Dacă ID-ul nu este valid sau clientul nu există.
     */
    public void deleteClient(Integer id) throws InvalidDataException {
        if (clientRepository.findById(id) == null) {
            throw new InvalidDataException("Client with ID " + id + " was not found for deletion.");
        }
        clientRepository.delete(id);
    }


    /**
     * Autentifica un client pe baza email-ului si parolei
     * @param email Email-ul introdus de utilizator.
     * @param password Parola introdusa de utilizator.
     * @return Un Optional ce contine clientul, daca autentificarea reuseste
     */
    public Optional<Client> authenticate(String email, String password) {
        List<Client> allClients = clientRepository.findAll();

        for (Client client : allClients) {
            if (client.getEmail().equalsIgnoreCase(email) && client.getPassword().equals(password)) {
                return Optional.of(client);
            }
        }
        return Optional.empty();
    }

    /**
     * Salvează toate datele în fișier înainte de oprirea aplicației.
     */
    public void shutdownApplicationAndSaveData() {
        clientRepository.saveAllData();
    }
}