package com.estoqueme.estoquemeweb.service; // Ajuste o pacote conforme o seu projeto

import com.estoqueme.estoquemeweb.model.Produto;
import com.estoqueme.estoquemeweb.repository.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Para controle de transação

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Serviço para gerenciar operações de negócio relacionadas a Produtos.
 */
@Service // Indica que esta classe é um componente de serviço Spring
public class ProdutoService {

    @Autowired // Injeta automaticamente uma instância de ProdutoRepository
    private ProdutoRepository produtoRepository;

    /**
     * Retorna todos os produtos cadastrados.
     * @return Lista de produtos.
     */
    public List<Produto> findAll() {
        return produtoRepository.findAll();
    }

    /**
     * Retorna um produto pelo seu ID.
     * @param id ID do produto.
     * @return Um Optional contendo o produto, se encontrado.
     */
    public Optional<Produto> findById(Long id) {
        return produtoRepository.findById(id);
    }

    /**
     * Retorna um produto pelo seu código.
     * @param codigo Código do produto.
     * @return Um Optional contendo o produto, se encontrado.
     */
    public Optional<Produto> findByCodigo(String codigo) {
        return produtoRepository.findByCodigo(codigo);
    }

    /**
     * Salva um novo produto ou atualiza um existente.
     * @param produto O objeto Produto a ser salvo.
     * @return O produto salvo/atualizado.
     */
    @Transactional // Garante que a operação seja transacional
    public Produto save(Produto produto) {
        // Lógica de cálculo de preço médio e venda pode ser feita aqui ou no frontend
        // Para consistência, vamos garantir que o backend também calcule
        produto.setPrecoMedio(calcularPrecoMedio(produto.getPrecoUltimaCompra(), produto.getQuantidade()));
        produto.setPrecoVenda(calcularPrecoVenda(produto.getPrecoMedio(), produto.getPorcentagemLucro()));
        return produtoRepository.save(produto);
    }

    /**
     * Deleta um produto pelo seu ID.
     * @param id ID do produto a ser deletado.
     */
    @Transactional
    public void deleteById(Long id) {
        produtoRepository.deleteById(id);
    }

    /**
     * Busca produtos por nome ou código.
     * @param searchTerm Termo de busca (nome ou ID).
     * @return Lista de produtos que correspondem ao termo.
     */
    public List<Produto> searchProdutos(String searchTerm) {
        try {
            // Tenta buscar por ID se o termo for um número
            Long id = Long.parseLong(searchTerm);
            return produtoRepository.findById(id).map(List::of).orElse(List.of());
        } catch (NumberFormatException e) {
            // Se não for um número, busca por nome
            return produtoRepository.findByNomeContainingIgnoreCase(searchTerm);
        }
    }

    /**
     * Realiza um depósito de quantidade em um produto.
     * @param produtoId ID do produto.
     * @param quantidade Quantidade a ser depositada.
     * @param precoCompra Preço unitário da compra para cálculo do novo preço médio.
     * @return O produto atualizado.
     * @throws IllegalArgumentException se o produto não for encontrado ou quantidade/preço inválidos.
     */
    @Transactional
    public Produto depositar(Long produtoId, Integer quantidade, BigDecimal precoCompra) {
        if (quantidade == null || quantidade <= 0) {
            throw new IllegalArgumentException("Quantidade para depósito deve ser um número positivo.");
        }
        if (precoCompra == null || precoCompra.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Preço de compra deve ser um valor positivo ou zero.");
        }

        Produto produto = produtoRepository.findById(produtoId)
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado com ID: " + produtoId));

        // Cálculo do novo preço médio ponderado
        BigDecimal valorTotalAntigo = produto.getPrecoMedio().multiply(BigDecimal.valueOf(produto.getQuantidade()));
        BigDecimal valorTotalNovoDeposito = precoCompra.multiply(BigDecimal.valueOf(quantidade));
        Integer novaQuantidadeTotal = produto.getQuantidade() + quantidade;

        if (novaQuantidadeTotal <= 0) { // Evita divisão por zero se quantidade for 0 após operação
            produto.setPrecoMedio(BigDecimal.ZERO);
        } else {
            BigDecimal novoPrecoMedio = (valorTotalAntigo.add(valorTotalNovoDeposito))
                                        .divide(BigDecimal.valueOf(novaQuantidadeTotal), 2, BigDecimal.ROUND_HALF_UP);
            produto.setPrecoMedio(novoPrecoMedio);
        }


        produto.setQuantidade(novaQuantidadeTotal);
        produto.setPrecoUltimaCompra(precoCompra); // Atualiza o preço da última compra
        produto.setPrecoVenda(calcularPrecoVenda(produto.getPrecoMedio(), produto.getPorcentagemLucro())); // Recalcula o preço de venda

        return produtoRepository.save(produto);
    }

    /**
     * Realiza uma retirada de quantidade de um produto.
     * @param produtoId ID do produto.
     * @param quantidade Quantidade a ser retirada.
     * @return O produto atualizado.
     * @throws IllegalArgumentException se o produto não for encontrado ou quantidade inválida.
     */
    @Transactional
    public Produto retirar(Long produtoId, Integer quantidade) {
        if (quantidade == null || quantidade <= 0) {
            throw new IllegalArgumentException("Quantidade para retirada deve ser um número positivo.");
        }

        Produto produto = produtoRepository.findById(produtoId)
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado com ID: " + produtoId));

        if (produto.getQuantidade() < quantidade) {
            throw new IllegalArgumentException("Quantidade em estoque insuficiente para retirada. Estoque atual: " + produto.getQuantidade());
        }

        produto.setQuantidade(produto.getQuantidade() - quantidade);
        return produtoRepository.save(produto);
    }

    // Métodos auxiliares para cálculo (agora públicos para serem acessados pelo RestController)
    public BigDecimal calcularPrecoMedio(BigDecimal precoUltimaCompra, Integer quantidade) { // Alterado para public
        // Para um novo produto, o preço médio é o preço da última compra.
        // Em um depósito, a lógica seria mais complexa (média ponderada).
        // Por enquanto, para o cadastro inicial, é simples.
        if (precoUltimaCompra == null) return BigDecimal.ZERO;
        return precoUltimaCompra;
    }

    public BigDecimal calcularPrecoVenda(BigDecimal precoBase, BigDecimal porcentagemLucro) { // Alterado para public
        if (precoBase == null || porcentagemLucro == null) return BigDecimal.ZERO;
        BigDecimal fatorLucro = porcentagemLucro.divide(BigDecimal.valueOf(100), 2, BigDecimal.ROUND_HALF_UP);
        return precoBase.add(precoBase.multiply(fatorLucro)).setScale(2, BigDecimal.ROUND_HALF_UP);
    }
}
