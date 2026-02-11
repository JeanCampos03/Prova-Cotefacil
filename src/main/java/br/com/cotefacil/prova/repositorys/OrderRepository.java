package br.com.cotefacil.prova.repositorys;

import br.com.cotefacil.prova.entitys.enums.OrderStatus;
import br.com.cotefacil.prova.entitys.orders.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    Page<Order> findAllByStatusNot(OrderStatus status, Pageable pageable);


    @Override
    Optional<Order> findById(Long id);
}
