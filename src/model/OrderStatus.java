package model;

public enum OrderStatus {
    PENDING,    // Comanda asteapta confirmare
    PROCESSING, // Comanda in curs de pregatire
    SHIPPED,    // Comanda expediata
    DELIVERED,  // Comanda a ajuns la client
    CANCELLED   // Comanda anulata
}
