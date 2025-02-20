package nebrija.gestionproyectos.services;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import nebrija.gestionproyectos.dto.ProyectoDTO;
import nebrija.gestionproyectos.dto.TareaDTO;
import nebrija.gestionproyectos.models.Proyecto;
import nebrija.gestionproyectos.models.Tarea;
import nebrija.gestionproyectos.models.Usuario;
import nebrija.gestionproyectos.repositories.ProyectoRepository;
import nebrija.gestionproyectos.repositories.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProyectoService {

    private final ProyectoRepository proyectoRepository;
    private final UsuarioRepository usuarioRepository;

    /**
     * Obtiene todos los proyectos del usuario autenticado.
     */
    public List<ProyectoDTO> obtenerTodos(Usuario usuario) {  // 🔥 Ajuste aquí
        return proyectoRepository.findByUsuario(usuario)
                .stream()
                .map(this::convertirAProyectoDTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene un proyecto solo si pertenece al usuario autenticado.
     */
    public ProyectoDTO obtenerPorId(Long id, Usuario usuario) {  // 🔥 Ajuste aquí
        Proyecto proyecto = proyectoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Proyecto no encontrado con ID: " + id));

        if (!proyecto.getUsuario().getId().equals(usuario.getId())) {
            throw new SecurityException("No tienes acceso a este proyecto.");
        }

        return convertirAProyectoDTO(proyecto);
    }

    /**
     * Crea un nuevo proyecto y lo asocia al usuario autenticado.
     */
    @Transactional
    public ProyectoDTO crearProyecto(ProyectoDTO dto, Usuario usuario) {  // 🔥 Ajuste aquí
        Proyecto proyecto = new Proyecto();
        proyecto.setNombre(dto.getNombre());
        proyecto.setDescripcion(dto.getDescripcion());
        proyecto.setFechaInicio(dto.getFechaInicio());
        proyecto.setEstado(Proyecto.EstadoProyecto.valueOf(dto.getEstado()));
        proyecto.setUsuario(usuario);

        return convertirAProyectoDTO(proyectoRepository.save(proyecto));
    }

    /**
     * Actualiza un proyecto solo si pertenece al usuario autenticado.
     */
    @Transactional
    public ProyectoDTO actualizarProyecto(Long id, ProyectoDTO dto, Usuario usuario) {  // 🔥 Ajuste aquí
        Proyecto proyecto = proyectoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Proyecto no encontrado con ID: " + id));

        if (!proyecto.getUsuario().getId().equals(usuario.getId())) {
            throw new SecurityException("No tienes permiso para modificar este proyecto.");
        }

        proyecto.setNombre(dto.getNombre());
        proyecto.setDescripcion(dto.getDescripcion());
        proyecto.setFechaInicio(dto.getFechaInicio());
        proyecto.setEstado(Proyecto.EstadoProyecto.valueOf(dto.getEstado()));

        return convertirAProyectoDTO(proyectoRepository.save(proyecto));
    }

    /**
     * Elimina un proyecto solo si pertenece al usuario autenticado.
     */
    @Transactional
    public void eliminarProyecto(Long id, Usuario usuario) {  // 🔥 Ajuste aquí
        Proyecto proyecto = proyectoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Proyecto no encontrado con ID: " + id));

        if (!proyecto.getUsuario().getId().equals(usuario.getId())) {
            throw new SecurityException("No tienes permiso para eliminar este proyecto.");
        }

        if (!proyecto.getTareas().isEmpty()) {
            throw new IllegalStateException("No se puede eliminar un proyecto con tareas asociadas.");
        }

        proyectoRepository.delete(proyecto);
    }

    /**
     * Convierte una entidad Proyecto en un DTO.
     */
    private ProyectoDTO convertirAProyectoDTO(Proyecto proyecto) {
        ProyectoDTO dto = new ProyectoDTO();
        dto.setId(proyecto.getId());
        dto.setNombre(proyecto.getNombre());
        dto.setDescripcion(proyecto.getDescripcion());
        dto.setFechaInicio(proyecto.getFechaInicio());
        dto.setEstado(proyecto.getEstado().name());
        dto.setUsuarioId(proyecto.getUsuario().getId());
        dto.setTareas(proyecto.getTareas().stream()
                .map(this::convertirATareaDTO)
                .collect(Collectors.toList()));
        return dto;
    }

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
