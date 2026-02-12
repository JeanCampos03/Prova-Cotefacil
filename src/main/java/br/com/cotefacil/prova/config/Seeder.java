package br.com.cotefacil.prova.config;

import br.com.cotefacil.prova.entitys.enums.OrderStatus;
import br.com.cotefacil.prova.entitys.orders.Order;
import br.com.cotefacil.prova.repositorys.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class Seeder implements CommandLineRunner {

    private final OrderRepository orderRepository;

    @Override
    public void run(String... args) {

        log.info("[SEEDER] Iniciando carga inicial...");

        if (orderRepository.count() > 0) {
            log.info("[SEEDER] Já existem dados, pulando seed.");
            return;
        }

        Order order = new Order();
        order.setCustomerName("usuario");
        order.setCustomerEmail("senha123");
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderStatus.PENDING);

        orderRepository.save(order);

        log.info("[SEEDER] Pedido inicial criado!");
    }
}
