package com.estoqueme.estoquemeweb.controller.api; // Ajuste o pacote conforme o seu projeto

import com.estoqueme.estoquemeweb.model.Produto;
import com.estoqueme.estoquemeweb.service.ProdutoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Controlador REST para operações com Produtos.
 * Expõe endpoints para o frontend interagir com o backend.
 */
@RestController // Indica que esta classe é um controlador REST
@RequestMapping("/api/produtos") // Define o caminho base para todos os endpoints deste controlador
public class ProdutoRestController {

    @Autowired // Injeta automaticamente uma instância de ProdutoService
    private ProdutoService produtoService;

    /**
     * Retorna todos os produtos.
     * GET /api/produtos
     * @return Lista de produtos.
     */
    @GetMapping
    public ResponseEntity<List<Produto>> getAllProdutos() {
        List<Produto> produtos = produtoService.findAll();
        return ResponseEntity.ok(produtos);
    }

    /**
     * Retorna um produto pelo ID.
     * GET /api/produtos/{id}
     * @param id ID do produto.
     * @return Produto encontrado ou 404 Not Found.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Produto> getProdutoById(@PathVariable Long id) {
        return produtoService.findById(id)
                .map(ResponseEntity::ok) // Se encontrado, retorna 200 OK com o produto
                .orElse(ResponseEntity.notFound().build()); // Se não encontrado, retorna 404 Not Found
    }

    /**
     * Busca produtos por termo (nome ou ID).
     * GET /api/produtos/search?term={searchTerm}
     * @param searchTerm Termo de busca.
     * @return Lista de produtos que correspondem.
     */
    @GetMapping("/search")
    public ResponseEntity<List<Produto>> searchProdutos(@RequestParam String term) {
        List<Produto> produtos = produtoService.searchProdutos(term);
        return ResponseEntity.ok(produtos);
    }

    /**
     * Cria um novo produto.
     * POST /api/produtos
     * @param produto O objeto Produto a ser criado.
     * @return Produto criado com status 201 Created.
     */
    @PostMapping
    public ResponseEntity<Produto> createProduto(@RequestBody Produto produto) {
        // Antes de salvar, verifique se o código já existe
        if (produtoService.findByCodigo(produto.getCodigo()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build(); // Retorna 409 Conflict se o código já existe
        }
        Produto savedProduto = produtoService.save(produto);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedProduto);
    }

    /**
     * Atualiza um produto existente.
     * PUT /api/produtos/{id}
     * @param id ID do produto a ser atualizado.
     * @param produto Objeto Produto com os dados atualizados.
     * @return Produto atualizado ou 404 Not Found.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Produto> updateProduto(@PathVariable Long id, @RequestBody Produto produto) {
        return produtoService.findById(id)
                .map(existingProduto -> {
                    // Atualiza apenas os campos permitidos ou necessários
                    existingProduto.setNome(produto.getNome());
                    existingProduto.setCodigo(produto.getCodigo()); // Cuidado ao permitir alteração de código único
                    existingProduto.setPrecoUltimaCompra(produto.getPrecoUltimaCompra());
                    existingProduto.setPorcentagemLucro(produto.getPorcentagemLucro());
                    existingProduto.setTipoProduto(produto.getTipoProduto());
                    existingProduto.setMarca(produto.getMarca());
                    existingProduto.setCategoria(produto.getCategoria());
                    existingProduto.setDescricao(produto.getDescricao());

                    // Recalcula preço médio e venda no backend para garantir consistência
                    existingProduto.setPrecoMedio(produtoService.calcularPrecoMedio(existingProduto.getPrecoUltimaCompra(), existingProduto.getQuantidade()));
                    existingProduto.setPrecoVenda(produtoService.calcularPrecoVenda(existingProduto.getPrecoMedio(), existingProduto.getPorcentagemLucro()));


                    return ResponseEntity.ok(produtoService.save(existingProduto));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Deleta um produto pelo ID.
     * DELETE /api/produtos/{id}
     * @param id ID do produto a ser deletado.
     * @return Resposta vazia com status 204 No Content.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduto(@PathVariable Long id) {
        if (!produtoService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build(); // Retorna 404 se o produto não existir
        }
        produtoService.deleteById(id);
        return ResponseEntity.noContent().build(); // Retorna 204 No Content
    }

    /**
     * Realiza um depósito de quantidade em um produto.
     * POST /api/produtos/{id}/depositar
     * Corpo da requisição: { "quantidade": 10, "precoCompra": 50.00 }
     * @param id ID do produto.
     * @param payload Map contendo "quantidade" e "precoCompra".
     * @return Produto atualizado ou 400 Bad Request/404 Not Found.
     */
    @PostMapping("/{id}/depositar")
    public ResponseEntity<Produto> depositarProduto(@PathVariable Long id, @RequestBody Map<String, Object> payload) {
        try {
            Integer quantidade = (Integer) payload.get("quantidade");
            BigDecimal precoCompra = new BigDecimal(payload.get("precoCompra").toString()); // Converte para BigDecimal

            Produto updatedProduto = produtoService.depositar(id, quantidade, precoCompra);
            return ResponseEntity.ok(updatedProduto);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null); // Retorna 400 Bad Request
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * Realiza uma retirada de quantidade de um produto.
     * POST /api/produtos/{id}/retirar
     * Corpo da requisição: { "quantidade": 5 }
     * @param id ID do produto.
     * @param payload Map contendo "quantidade".
     * @return Produto atualizado ou 400 Bad Request/404 Not Found.
     */
    @PostMapping("/{id}/retirar")
    public ResponseEntity<Produto> retirarProduto(@PathVariable Long id, @RequestBody Map<String, Object> payload) {
        try {
            Integer quantidade = (Integer) payload.get("quantidade");
            Produto updatedProduto = produtoService.retirar(id, quantidade);
            return ResponseEntity.ok(updatedProduto);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null); // Retorna 400 Bad Request
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}