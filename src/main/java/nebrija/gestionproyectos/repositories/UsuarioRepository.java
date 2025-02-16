package nebrija.gestionproyectos.repositories;

import nebrija.gestionproyectos.models.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository // Se recomienda explícitamente marcar los repositorios
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByUsername(String username);

    boolean existsByUsername(String username); // Para validar si un usuario ya existe antes de registrarlo
}
