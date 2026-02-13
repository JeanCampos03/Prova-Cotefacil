package br.com.prova.cotefacil.apipedidos.repositorys;

import br.com.prova.cotefacil.apigateway.entity.Usuario;
import br.com.prova.cotefacil.apipedidos.entitys.enums.OrderStatus;
import br.com.prova.cotefacil.apipedidos.entitys.orders.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    Page<Order> findAllByStatusNotAndCreatedBy(OrderStatus status, Pageable pageable, String username);

    Optional<Order> findByIdAndCreatedBy(Long id, String username);

    boolean existsByIdAndCreatedBy(Long id, String createdBy);
}
