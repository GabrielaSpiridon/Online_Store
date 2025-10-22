package repository;

import java.util.List;

/**
 * Interfata generica (sablon) pentru operatiunile CRUD.
 * @param <T> Tipul entitatii (ex: Product, Client).
 * @param <ID> Tipul cheii primare (ex: Integer).
 */

public interface IRepository<T, ID> {

    // Operatii CRUD standard
    /** Salveaza sau actualizeaza o entitate */
    void save(T entity);

    /** Cauta o entitate dupa ID.*/
    T findById(ID id);

    /** Returneaza toate entitatile*/
    List<T> findAll();

    /** Sterge o entitate dupa ID.*/
    void delete(ID id);

    /** Salveaza datele din memorie in fisier.*/
    void saveAllData();

    /** Incarca datele din fisier in memorie.*/
    void loadAllData();

}
