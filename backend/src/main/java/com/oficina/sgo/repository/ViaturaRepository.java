package com.oficina.sgo.repository;

import com.oficina.sgo.model.Viatura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ViaturaRepository extends JpaRepository<Viatura, Long> {
    Optional<Viatura> findByMatricula(String matricula);
    boolean existsByMatricula(String matricula);
    List<Viatura> findByClienteId(Long clienteId);
}
