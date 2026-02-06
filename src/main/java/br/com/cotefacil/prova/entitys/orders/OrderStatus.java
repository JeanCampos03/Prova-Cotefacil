package br.com.cotefacil.prova.entitys.orders;

public enum OrderStatus {
    PENDING("PENDING"),
    CONFIRMED("CONFIRMED"),
    DELIVERED("DELIVERED"),
    CANCELLED("CANCELLED");

    private final String status;

    OrderStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
