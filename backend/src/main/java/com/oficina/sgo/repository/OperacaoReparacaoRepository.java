package com.oficina.sgo.repository;

import com.oficina.sgo.model.OperacaoReparacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface OperacaoReparacaoRepository extends JpaRepository<OperacaoReparacao, Long> {
    List<OperacaoReparacao> findByReparacaoId(Long reparacaoId);
}
