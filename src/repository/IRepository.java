package repository;

import java.util.List;

/**
 * Interfata generica (sablon) pentru operatiunile CRUD (Create, Read, Update, Delete).
 * Aplica principiile Abstractizarii si Polimorfismului in stratul de Persistenta.
 * * @param <T> Tipul entitatii (ex: Product, Client).
 * @param <ID> Tipul cheii primare (ex: Integer).
 */

public interface IRepository<T, ID> {

    // Operatii CRUD standard

    /** * Salveaza sau actualizeaza o entitate in colectia din memorie.
     * @param entity Entitatea de salvat.
     */
    void save(T entity);

    /** * Cauta o entitate dupa ID.
     * @param id ID-ul entitatii.
     * @return Entitatea gasita sau null.
     */
    T findById(ID id);

    /** * Returneaza lista tuturor entitatilor din memorie.
     * @return Lista de entitati.
     */
    List<T> findAll();

    /** * Sterge o entitate dupa ID.
     * @param id ID-ul entitatii de sters.
     */
    void delete(ID id);

    // Operatii de Persistenta (Cerinta 2)

    /** * Salveaza datele din memorie in fisierul de persistenta (apelata la inchiderea aplicatiei).
     */
    void saveAllData();

    /** * Incarca datele din fisier in memoria aplicatiei (apelata la pornire).
     */
    void loadAllData();

}