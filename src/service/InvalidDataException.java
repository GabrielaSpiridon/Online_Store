package service;

/**
 * Exceptie personalizata folosita pentru a semnala erori aparute in timpul
 * validarii datelor de business (ex: pret negativ, email invalid, stoc insuficient).
 * Extinde Exception pentru a fi o exceptie verificata (checked exception).
 */
public class InvalidDataException extends Exception{

    /**
     * Constructor care primeste un mesaj descriptiv al erorii.
     * @param message Mesajul descriptiv al erorii (in Engleză, conform standardului).
     */
    public InvalidDataException(String message){
        super(message);
    }
}