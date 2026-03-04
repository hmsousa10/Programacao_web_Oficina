package com.oficina.sgo.repository;

import com.oficina.sgo.model.MovimentoStock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MovimentoStockRepository extends JpaRepository<MovimentoStock, Long> {
    List<MovimentoStock> findByPecaId(Long pecaId);
    List<MovimentoStock> findByReparacaoId(Long reparacaoId);
}
