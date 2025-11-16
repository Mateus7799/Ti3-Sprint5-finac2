CREATE TABLE Servicos (
    id INT IDENTITY(1,1) PRIMARY KEY,
    id_produto INT NOT NULL,
    tipo INT NOT NULL,
    valor DECIMAL(10,2) NOT NULL,
    id_pessoa INT NULL,
    id_status INT NULL,
    id_metodo_pagamento INT NULL,
    data_inicio DATE NULL,
    data_fim DATE NULL,
    observacoes VARCHAR(MAX) NULL,

CONSTRAINT fk_Servicos_Produtos FOREIGN KEY (id_produto)
    REFERENCES Produtos(id)
    ON UPDATE CASCADE
    ON DELETE NO ACTION,

CONSTRAINT fk_Servicos_Pessoas FOREIGN KEY (id_pessoa)
    REFERENCES Pessoas(id)
    ON UPDATE CASCADE
    ON DELETE NO ACTION,

CONSTRAINT fk_Servicos_StatusServico FOREIGN KEY (id_status)
    REFERENCES StatusServico(id)
    ON UPDATE CASCADE
    ON DELETE NO ACTION,

CONSTRAINT fk_Servicos_MetodosPagamento FOREIGN KEY (id_metodo_pagamento)
    REFERENCES MetodosPagamento(id)
    ON UPDATE CASCADE
    ON DELETE NO ACTION
);