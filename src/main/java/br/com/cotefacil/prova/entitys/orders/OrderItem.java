package br.com.cotefacil.prova.entitys.orders;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "pedido_itens")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_name", nullable = false)
    private String productName;

    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "unit_price", nullable = false, precision = 8, scale = 2)
    private BigDecimal unitPrice;

    @Column(nullable = false, precision = 8, scale = 2)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private BigDecimal subtotal;

    @ManyToOne
    @JoinColumn(name = "pedido_id")
    private Order order;
}
