CREATE DATABASE IF NOT EXISTS estoqueme_db;

-- Usa o banco de dados recém-criado/existente
USE estoqueme_db;

-- Remove a tabela 'produtos' se ela já existir para garantir um estado limpo
DROP TABLE IF EXISTS produtos;

-- Cria a tabela 'produtos'
CREATE TABLE produtos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    codigo VARCHAR(255) NOT NULL UNIQUE, -- Código deve ser único
    quantidade INT NOT NULL,
    preco_ultima_compra DECIMAL(10, 2) NOT NULL,
    preco_medio DECIMAL(10, 2) NOT NULL,
    porcentagem_lucro DECIMAL(5, 2) NOT NULL,
    preco_venda DECIMAL(10, 2) NOT NULL,
    tipo_produto VARCHAR(100) NOT NULL,
    marca VARCHAR(100) NOT NULL,
    categoria VARCHAR(100) NOT NULL,
    descricao TEXT -- Usar TEXT para descrições mais longas
);

-- Opcional: Inserir alguns dados de exemplo para teste
INSERT INTO produtos (nome, codigo, quantidade, preco_ultima_compra, preco_medio, porcentagem_lucro, preco_venda, tipo_produto, marca, categoria, descricao) VALUES
('Teclado Mecânico RGB', 'KM001', 50, 150.00, 150.00, 25.00, 187.50, 'Eletrônico', 'HyperX', 'Periféricos', 'Teclado mecânico com iluminação RGB.'),
('Mouse Gamer Wireless', 'MGW002', 120, 80.00, 80.00, 30.00, 104.00, 'Eletrônico', 'Logitech', 'Periféricos', 'Mouse sem fio para jogos.'),
('Monitor Ultrawide 29"', 'MU2903', 30, 700.00, 700.00, 40.00, 980.00, 'Eletrônico', 'LG', 'Monitores', 'Monitor ultrawide para produtividade e jogos.'),
('Camiseta Algodão P', 'CA004P', 200, 25.00, 25.00, 50.00, 37.50, 'Vestuário', 'MarcaX', 'Roupas', 'Camiseta de algodão tamanho P.'),
('Fone de Ouvido Bluetooth', 'FOB005', 80, 60.00, 60.00, 35.00, 81.00, 'Eletrônico', 'JBL', 'Áudio', 'Fone de ouvido sem fio com cancelamento de ruído.');