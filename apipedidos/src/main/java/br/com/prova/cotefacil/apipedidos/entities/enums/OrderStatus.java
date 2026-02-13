package br.com.prova.cotefacil.apipedidos.entities.enums;

import java.util.List;

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

    public boolean canTransitionTo(OrderStatus newStatus) {
        if (newStatus == null) {
            return false;
        }

        if (this == DELIVERED || this == CANCELLED) {
            return false;
        }

        if (newStatus == CANCELLED) {
            return this != DELIVERED;
        }

        return newStatus.ordinal() == this.ordinal() + 1;
    }

    public List<OrderStatus> getNextPossibleStatuses() {
        return switch (this) {
            case PENDING -> List.of(CONFIRMED, CANCELLED, DELIVERED);
            case CONFIRMED -> List.of(CANCELLED, DELIVERED);
            case DELIVERED, CANCELLED -> List.of(); // Status final
        };
    }
}
