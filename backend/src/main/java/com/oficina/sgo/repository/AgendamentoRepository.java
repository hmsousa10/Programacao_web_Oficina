package com.oficina.sgo.repository;

import com.oficina.sgo.model.Agendamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AgendamentoRepository extends JpaRepository<Agendamento, Long> {

    @Query("SELECT a FROM Agendamento a WHERE a.dataHoraInicio >= :inicio AND a.dataHoraFim <= :fim")
    List<Agendamento> findByPeriod(@Param("inicio") LocalDateTime inicio, @Param("fim") LocalDateTime fim);

    @Query("SELECT COUNT(a) FROM Agendamento a WHERE a.dataHoraInicio = :inicio AND a.dataHoraFim = :fim AND a.estado != 'CANCELADO'")
    long countBySlot(@Param("inicio") LocalDateTime inicio, @Param("fim") LocalDateTime fim);

    @Query("SELECT COUNT(a) FROM Agendamento a WHERE a.dataHoraInicio = :inicio AND a.dataHoraFim = :fim AND a.estado != 'CANCELADO' AND a.id != :excludeId")
    long countBySlotExcluding(@Param("inicio") LocalDateTime inicio, @Param("fim") LocalDateTime fim, @Param("excludeId") Long excludeId);

    List<Agendamento> findByMecanicoId(Long mecanicoId);

    @Query("SELECT a FROM Agendamento a WHERE a.dataHoraInicio >= :inicio AND a.dataHoraInicio < :fim ORDER BY a.dataHoraInicio")
    List<Agendamento> findBySemana(@Param("inicio") LocalDateTime inicio, @Param("fim") LocalDateTime fim);

    @Query("SELECT COUNT(a) FROM Agendamento a WHERE a.estado IN ('PENDENTE', 'CONFIRMADO') AND a.dataHoraInicio > :now")
    long countMarcacoesFuturas(@Param("now") LocalDateTime now);
}
