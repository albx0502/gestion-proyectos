package nebrija.gestionproyectos.repositories;

import nebrija.gestionproyectos.models.Proyecto;
import nebrija.gestionproyectos.models.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProyectoRepository extends JpaRepository<Proyecto, Long> {

    // 🔥 Buscar proyectos por usuario (SOLUCIÓN AL ERROR)
    List<Proyecto> findByUsuario(Usuario usuario);

    // Buscar proyectos por estado (ACTIVO, EN_PROGRESO, FINALIZADO)
    List<Proyecto> findByEstado(Proyecto.EstadoProyecto estado);

    // Buscar proyectos cuyo nombre contenga un texto específico (ignorando mayúsculas/minúsculas)
    List<Proyecto> findByNombreContainingIgnoreCase(String nombre);
}
