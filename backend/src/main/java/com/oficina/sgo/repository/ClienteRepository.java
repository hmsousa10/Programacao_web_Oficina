package com.oficina.sgo.repository;

import com.oficina.sgo.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    Optional<Cliente> findByNif(String nif);
    boolean existsByNif(String nif);
}
