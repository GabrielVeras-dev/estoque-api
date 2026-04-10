package com.gabrielveras.estoque_api.repository;

import com.gabrielveras.estoque_api.model.Fornecedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FornecedorRepository extends JpaRepository<Fornecedor, Long> {
    Optional<Fornecedor> findByCnpj(String cnpj);
    boolean existsByCnpj(String cnpj);
    List<Fornecedor> findByAtivoTrue();
}