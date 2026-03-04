package com.oficina.sgo.repository;

import com.oficina.sgo.model.Reparacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReparacaoRepository extends JpaRepository<Reparacao, Long> {

    List<Reparacao> findByMecanicoId(Long mecanicoId);

    @Query("SELECT COUNT(r) FROM Reparacao r WHERE r.estado = 'EM_EXECUCAO'")
    long countAtivas();

    @Query("SELECT r FROM Reparacao r WHERE r.estado = 'EM_EXECUCAO'")
    List<Reparacao> findAllAtivas();

    @Query("SELECT r FROM Reparacao r WHERE r.dataFim >= :inicio AND r.dataFim <= :fim AND r.estado = 'CONCLUIDA'")
    List<Reparacao> findConcluidasNoPeriodo(@Param("inicio") LocalDateTime inicio, @Param("fim") LocalDateTime fim);

    @Query("SELECT COUNT(r) FROM Reparacao r WHERE r.dataFim >= :inicio AND r.dataFim <= :fim AND r.estado = 'CONCLUIDA'")
    long countConcluidasNoPeriodo(@Param("inicio") LocalDateTime inicio, @Param("fim") LocalDateTime fim);
}
