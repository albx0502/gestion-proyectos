package nebrija.gestionproyectos.repositories;

import nebrija.gestionproyectos.models.Tarea;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TareaRepository extends JpaRepository<Tarea, Long> {

    // Buscar todas las tareas asociadas a un proyecto específico
    List<Tarea> findByProyectoId(Long proyectoId);

    // Buscar todas las tareas por estado (PENDIENTE, EN_CURSO, COMPLETADA)
    List<Tarea> findByEstado(Tarea.EstadoTarea estado);

    // Buscar tareas cuyo título contenga una palabra clave (ignorando mayúsculas/minúsculas)
    List<Tarea> findByTituloContainingIgnoreCase(String titulo);
}
