package br.com.prova.cotefacil.apipedidos.repository;

import br.com.prova.cotefacil.apipedidos.entities.enums.OrderStatus;
import br.com.prova.cotefacil.apipedidos.entities.orders.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    Page<Order> findAllByStatusNotAndCreatedBy(OrderStatus status, Pageable pageable, String username);

    Optional<Order> findByIdAndCreatedBy(Long id, String username);

    boolean existsByIdAndCreatedBy(Long id, String createdBy);
}
