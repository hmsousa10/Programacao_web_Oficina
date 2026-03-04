package com.oficina.sgo.repository;

import com.oficina.sgo.model.Peca;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface PecaRepository extends JpaRepository<Peca, Long> {
    Optional<Peca> findByReferencia(String referencia);
    boolean existsByReferencia(String referencia);

    @Query("SELECT p FROM Peca p WHERE p.quantidadeStock <= p.stockMinimo")
    List<Peca> findPecasComStockBaixo();
}
