package com.unimag.edu.proyecto_final.domine.repository;

import com.unimag.edu.proyecto_final.domine.entities.Confi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ConfiRepository extends JpaRepository<Confi,String> {

    Optional<Confi> findByKey(String key);


    boolean existsByKey(String key);


    @Query("SELECT c FROM Confi c WHERE c.key IN :keys")
    List<Confi> findByKeys(@Param("keys") List<String> keys);


    @Query("SELECT c FROM Confi c WHERE c.key LIKE CONCAT(:prefix, '%')")
    List<Confi> findByPrefix(@Param("prefix") String prefix);

}
