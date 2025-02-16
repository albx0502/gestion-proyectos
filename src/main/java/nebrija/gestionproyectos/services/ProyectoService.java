package nebrija.gestionproyectos.services;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import nebrija.gestionproyectos.dto.ProyectoDTO;
import nebrija.gestionproyectos.dto.TareaDTO;
import nebrija.gestionproyectos.models.Proyecto;
import nebrija.gestionproyectos.models.Tarea;
import nebrija.gestionproyectos.repositories.ProyectoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProyectoService {

    private final ProyectoRepository proyectoRepository;

    /**
     * Obtiene todos los proyectos en la base de datos.
     */
    public List<ProyectoDTO> obtenerTodos() {
        return proyectoRepository.findAll()
                .stream()
                .map(this::convertirAProyectoDTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene un proyecto por su ID.
     */
    public ProyectoDTO obtenerPorId(Long id) {
        Proyecto proyecto = proyectoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Proyecto no encontrado con ID: " + id));
        return convertirAProyectoDTO(proyecto);
    }

    /**
     * Crea un nuevo proyecto.
     */
    @Transactional
    public ProyectoDTO crearProyecto(ProyectoDTO dto) {
        Proyecto proyecto = new Proyecto();
        proyecto.setNombre(dto.getNombre());
        proyecto.setDescripcion(dto.getDescripcion());
        proyecto.setFechaInicio(dto.getFechaInicio());
        proyecto.setEstado(Proyecto.EstadoProyecto.valueOf(dto.getEstado()));

        return convertirAProyectoDTO(proyectoRepository.save(proyecto));
    }

    /**
     * Actualiza un proyecto existente.
     */
    @Transactional
    public ProyectoDTO actualizarProyecto(Long id, ProyectoDTO dto) {
        Proyecto proyecto = proyectoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Proyecto no encontrado con ID: " + id));

        proyecto.setNombre(dto.getNombre());
        proyecto.setDescripcion(dto.getDescripcion());
        proyecto.setFechaInicio(dto.getFechaInicio());
        proyecto.setEstado(Proyecto.EstadoProyecto.valueOf(dto.getEstado()));

        return convertirAProyectoDTO(proyectoRepository.save(proyecto));
    }

    /**
     * Elimina un proyecto por su ID.
     */
    @Transactional
    public void eliminarProyecto(Long id) {
        Proyecto proyecto = proyectoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Proyecto no encontrado con ID: " + id));

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
        dto.setTareas(proyecto.getTareas().stream()
                .map(this::convertirATareaDTO)
                .collect(Collectors.toList()));
        return dto;
    }

    /**
     * Convierte una entidad Tarea en un DTO.
     */
    private TareaDTO convertirATareaDTO(Tarea tarea) {
        TareaDTO tareaDto = new TareaDTO();
        tareaDto.setId(tarea.getId());
        tareaDto.setTitulo(tarea.getTitulo());
        tareaDto.setDescripcion(tarea.getDescripcion());
        tareaDto.setFechaLimite(tarea.getFechaLimite());
        tareaDto.setEstado(tarea.getEstado().name());
        tareaDto.setProyectoId(tarea.getProyecto().getId());
        return tareaDto;
    }
}
