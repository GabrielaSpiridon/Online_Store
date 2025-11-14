package model;

/**
 * Enum-ul OrderStatus defineste starile posibile prin care poate trece o comanda.
 * Este folosit pentru a urmari ciclul de viata al tranzactiei (Cerinta 1).
 */
public enum OrderStatus {
    /**
     * Comanda a fost plasata si asteapta confirmarea platii sau aprobarea.
     */
    PENDING,

    /**
     * Comanda este in curs de pregatire si procesare in depozit.
     */
    PROCESSING,

    /**
     * Comanda a fost impachetata si expediata catre client.
     */
    SHIPPED,

    /**
     * Comanda a ajuns la destinatie si a fost receptionata de client.
     */
    DELIVERED,

    /**
     * Comanda a fost anulata de client sau de administrator.
     */
    CANCELLED
}