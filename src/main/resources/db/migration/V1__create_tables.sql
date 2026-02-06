CREATE TABLE usuarios (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL
);


CREATE TABLE pedidos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_name VARCHAR(50) NOT NULL,
    customer_email VARCHAR(255) NOT NULL,
    order_date DATETIME NOT NULL,
    status VARCHAR(50) NOT NULL,
    total_amount DECIMAL(6,2) NOT NULL
);

CREATE TABLE pedido_itens (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_name VARCHAR(50) NOT NULL,
    quantity INTEGER NOT NULL,
    unit_price DECIMAL(6,2) NOT NULL,
    subtotal DECIMAL(6,2) NOT NULL,
    pedido_id BIGINT NOT NULL,

    CONSTRAINT fk_pedido
            FOREIGN KEY (pedido_id)
            REFERENCES pedidos(id)
            ON DELETE CASCADE
);