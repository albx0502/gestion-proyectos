package nebrija.gestionproyectos.services;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import nebrija.gestionproyectos.dto.TareaDTO;
import nebrija.gestionproyectos.models.Proyecto;
import nebrija.gestionproyectos.models.Tarea;
import nebrija.gestionproyectos.models.Usuario;
import nebrija.gestionproyectos.repositories.ProyectoRepository;
import nebrija.gestionproyectos.repositories.TareaRepository;
import nebrija.gestionproyectos.repositories.UsuarioRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TareaService {

    private final TareaRepository tareaRepository;
    private final ProyectoRepository proyectoRepository;
    private final UsuarioRepository usuarioRepository;

    /**
     * Obtiene todas las tareas del usuario autenticado.
     */
    public List<TareaDTO> obtenerTodas(Usuario usuario) {  // 🔥 Ajuste aquí
        return tareaRepository.findByProyectoUsuario(usuario)
                .stream()
                .map(this::convertirATareaDTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene una tarea solo si pertenece a un proyecto del usuario autenticado.
     */
    public TareaDTO obtenerPorId(Long id, Usuario usuario) {  // 🔥 Ajuste aquí
        Tarea tarea = tareaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tarea no encontrada con ID: " + id));

        if (!tarea.getProyecto().getUsuario().getId().equals(usuario.getId())) {
            throw new SecurityException("No tienes acceso a esta tarea.");
        }

        return convertirATareaDTO(tarea);
    }

    /**
     * Crea una nueva tarea en un proyecto del usuario autenticado.
     */
    @Transactional
    public TareaDTO crearTarea(TareaDTO dto, Usuario usuario) {  // 🔥 Ajuste aquí
        Proyecto proyecto = proyectoRepository.findById(dto.getProyectoId())
                .orElseThrow(() -> new EntityNotFoundException("Proyecto no encontrado con ID: " + dto.getProyectoId()));

        if (!proyecto.getUsuario().getId().equals(usuario.getId())) {
            throw new SecurityException("No puedes crear tareas en un proyecto ajeno.");
        }

        Tarea tarea = new Tarea(dto.getTitulo(), dto.getDescripcion(), dto.getFechaLimite(),
                Tarea.EstadoTarea.valueOf(dto.getEstado()), proyecto);

        return convertirATareaDTO(tareaRepository.save(tarea));
    }

    /**
     * Actualiza una tarea solo si pertenece a un proyecto del usuario autenticado.
     */
    @Transactional
    public TareaDTO actualizarTarea(Long id, TareaDTO dto, Usuario usuario) {  // 🔥 Ajuste aquí
        Tarea tarea = tareaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tarea no encontrada con ID: " + id));

        if (!tarea.getProyecto().getUsuario().getId().equals(usuario.getId())) {
            throw new SecurityException("No puedes modificar una tarea en un proyecto ajeno.");
        }

        tarea.setTitulo(dto.getTitulo());
        tarea.setDescripcion(dto.getDescripcion());
        tarea.setFechaLimite(dto.getFechaLimite());
        tarea.setEstado(Tarea.EstadoTarea.valueOf(dto.getEstado()));

        return convertirATareaDTO(tareaRepository.save(tarea));
    }

    /**
     * Elimina una tarea solo si pertenece a un proyecto del usuario autenticado.
     */
    @Transactional
    public void eliminarTarea(Long id, Usuario usuario) {  // 🔥 Ajuste aquí
        Tarea tarea = tareaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tarea no encontrada con ID: " + id));

        if (!tarea.getProyecto().getUsuario().getId().equals(usuario.getId())) {
            throw new SecurityException("No puedes eliminar una tarea de un proyecto ajeno.");
        }

        tareaRepository.delete(tarea);
    }

    /**
     * Convierte una entidad `Tarea` a su representación `TareaDTO`.
     */
    private TareaDTO convertirATareaDTO(Tarea tarea) {
        return new TareaDTO(
                tarea.getId(),
                tarea.getTitulo(),
                tarea.getDescripcion(),
                tarea.getFechaLimite(),
                tarea.getEstado().name(),
                tarea.getProyecto().getId()
        );
    }
}
