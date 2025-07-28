package com.estoqueme.estoquemeweb.repository; // Ajuste o pacote conforme o seu projeto

import com.estoqueme.estoquemeweb.model.Produto; // Importe a entidade Produto
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositório para a entidade Produto.
 * Estende JpaRepository para fornecer operações CRUD prontas.
 */
@Repository // Indica que esta interface é um componente de repositório Spring
public interface ProdutoRepository extends JpaRepository<Produto, Long> {

    // Método para buscar produtos por nome (ignorando maiúsculas/minúsculas)
    List<Produto> findByNomeContainingIgnoreCase(String nome);

    // Método para buscar produtos por código
    Optional<Produto> findByCodigo(String codigo);
}