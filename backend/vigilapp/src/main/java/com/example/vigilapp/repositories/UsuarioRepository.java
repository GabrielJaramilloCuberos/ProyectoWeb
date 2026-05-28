package com.example.vigilapp.repositories;

import com.example.vigilapp.entities.Usuario;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByEmail(String email);

    @Query("SELECT u FROM Usuario u JOIN FETCH u.rol WHERE u.email = :email")
    Optional<Usuario> findByEmailWithRol(String email);

    @Query("SELECT u FROM Usuario u JOIN FETCH u.rol")
    List<Usuario> findAllWithRelations();

    @Query("SELECT u FROM Usuario u JOIN FETCH u.rol WHERE u.id_usuario = :id")
    Optional<Usuario> findByIdWithRelations(Long id);
}
