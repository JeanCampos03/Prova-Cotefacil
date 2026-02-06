package br.com.cotefacil.prova.entitys.orders;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "pedido_itens")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String productName;

    @Column(nullable = false)
    private Integer quantity;

    @Column(
            nullable = false,
            precision = 8,
            scale = 2
    )
    private BigDecimal unitPrice;

    @Column(
            nullable = false,
            precision = 8,
            scale = 2
    )
    private BigDecimal subtotal;

    @ManyToOne
    @JoinColumn(name = "pedido_id")
    private Order order;
}
