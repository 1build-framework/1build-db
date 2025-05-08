CREATE TABLE customer (
                          id INT PRIMARY KEY,
                          name VARCHAR(100),
                          status VARCHAR(20)
);

CREATE TABLE payment (
                         id INT PRIMARY KEY,
                         customer_id INT,
                         amount NUMERIC(10, 2)
);

CREATE TABLE product (
                         id INT PRIMARY KEY,
                         name VARCHAR(100),
                         quantity INT
);

CREATE TABLE "order" (
                         id INT PRIMARY KEY,
                         customer_id INT,
                         product_id INT,
                         quantity INT
);
