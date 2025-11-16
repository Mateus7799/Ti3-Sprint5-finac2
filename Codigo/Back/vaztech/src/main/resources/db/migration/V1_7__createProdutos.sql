CREATE TABLE Produtos (
    id INT IDENTITY(1,1) PRIMARY KEY,
    numero_serie VARCHAR(50) UNIQUE NOT NULL,
    aparelho VARCHAR(50) NOT NULL,
    modelo VARCHAR(100) NULL,
    cor VARCHAR(30) NULL,
    id_status INT NULL,
    observacoes VARCHAR(MAX) NULL

CONSTRAINT fk_Produtos_StatusProduto FOREIGN KEY (id_status)
   REFERENCES StatusProduto(id)
   ON UPDATE CASCADE
   ON DELETE SET NULL
);