package model;

import java.io.Serializable;
import java.util.Objects;

/**
 * Clasa User reprezinta baza pentru toti utilizatorii aplicatiei (ex: Clienti, Admini).
 * Stabileste atributele necesare pentru autentificare (ID, email, parola).
 * Implementeaza Serializable si aplica principiul Incapsularii.
 */
public class User implements Serializable {
    private int id;
    private String name;
    private String email;
    private String password;

    /**
     * Constructor fara parametri.
     * Esential pentru mecanismele de I/O si mostenire.
     */
    public User(){

    }

    /**
     * Constructor cu parametri.
     * @param id ID-ul unic al utilizatorului.
     * @param name Numele complet.
     * @param email Email-ul (pentru login).
     * @param password Parola.
     */
    public User(int id, String name, String email, String password) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
    }

    /**
     * Returneaza ID-ul utilizatorului.
     * @return ID-ul.
     */
    public int getId() {
        return id;
    }

    /**
     * Seteaza ID-ul utilizatorului.
     * @param id Noul ID.
     */
    public void setId(int id) {
        if(id > 0){
            this.id = id;
        }
    }

    /**
     * Returneaza numele utilizatorului.
     * @return Numele.
     */
    public String getName() {
        return name;
    }

    /**
     * Seteaza numele utilizatorului, cu validare de baza.
     * @param name Noul nume.
     */
    public void setName(String name) {
        if(name != null && name.length() >= 3){
            this.name = name;
        }
    }

    /**
     * Returneaza email-ul utilizatorului.
     * @return Email-ul.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Seteaza email-ul utilizatorului, cu validare de baza.
     * @param email Noul email.
     */
    public void setEmail(String email) {
        if(email != null && email.length() >= 3){
            this.email = email;
        }
    }

    /**
     * Returneaza parola (de evitat in log-uri).
     * @return Parola.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Seteaza parola, cu validare de baza.
     * @param password Noua parola.
     */
    public void setPassword(String password) {
        if(password != null && password.length() >= 3){
            this.password = password;
        }
    }

    /**
     * Metoda toString suprascrisa.
     * @return String care reprezinta starea obiectului User.
     */
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                '}';
    }

    /**
     * Metoda equals suprascrisa. Se bazeaza pe ID si alte campuri unice (optional) pentru comparare.
     * @param o Obiectul de comparat.
     * @return true daca obiectele sunt egale.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id == user.id && Objects.equals(name, user.name) && Objects.equals(email, user.email) && Objects.equals(password, user.password);
    }

    /**
     * Metoda hashCode suprascrisa, bazata pe campurile cheie.
     * @return Hash code-ul obiectului.
     */
    @Override
    public int hashCode() {
        return Objects.hash(id, name, email, password);
    }
}