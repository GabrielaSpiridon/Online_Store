package repository;

/**
 * Exceptie personalizata folosita pentru a semnala erori aparute in timpul
 * procesarii datelor (citire/scriere/parsare) din fisierele de persistenta.
 * Extinde RuntimeException pentru a fi o exceptie netratata (unchecked).
 */
public class DataProcessingException extends RuntimeException {

    /**
     * Constructor care primeste un mesaj de eroare si o exceptie care a cauzat problema.
     * @param message Mesajul descriptiv al erorii.
     * @param cause Exceptia originala (de baza) care a declansat problema.
     */
    public DataProcessingException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor care primeste doar un mesaj de eroare.
     * @param message Mesajul descriptiv al erorii.
     */
    public DataProcessingException(String message) {
        super(message);
    }
}