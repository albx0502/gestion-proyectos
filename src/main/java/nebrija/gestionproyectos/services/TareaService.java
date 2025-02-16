package nebrija.gestionproyectos.services;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import nebrija.gestionproyectos.dto.TareaDTO;
import nebrija.gestionproyectos.models.Proyecto;
import nebrija.gestionproyectos.models.Tarea;
import nebrija.gestionproyectos.repositories.ProyectoRepository;
import nebrija.gestionproyectos.repositories.TareaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TareaService {

    private final TareaRepository tareaRepository;
    private final ProyectoRepository proyectoRepository;

    /**
     * Obtiene todas las tareas almacenadas en la base de datos.
     */
    public List<TareaDTO> obtenerTodas() {
        return tareaRepository.findAll().stream()
                .map(this::convertirATareaDTO)
                .collect(Collectors.toList());
    }

    /**
     * Busca una tarea por su ID.
     */
    public TareaDTO obtenerPorId(Long id) {
        Tarea tarea = tareaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tarea no encontrada con ID: " + id));
        return convertirATareaDTO(tarea);
    }

    /**
     * Crea una nueva tarea asociada a un proyecto existente.
     */
    @Transactional
    public TareaDTO crearTarea(TareaDTO dto) {
        Proyecto proyecto = proyectoRepository.findById(dto.getProyectoId())
                .orElseThrow(() -> new EntityNotFoundException("Proyecto no encontrado con ID: " + dto.getProyectoId()));

        Tarea tarea = new Tarea(dto.getTitulo(), dto.getDescripcion(), dto.getFechaLimite(),
                Tarea.EstadoTarea.valueOf(dto.getEstado()), proyecto);

        return convertirATareaDTO(tareaRepository.save(tarea));
    }

    /**
     * Actualiza una tarea existente en la base de datos.
     */
    @Transactional
    public TareaDTO actualizarTarea(Long id, TareaDTO dto) {
        Tarea tarea = tareaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tarea no encontrada con ID: " + id));

        // Solo actualiza si el valor cambió
        if (dto.getTitulo() != null && !dto.getTitulo().equals(tarea.getTitulo())) {
            tarea.setTitulo(dto.getTitulo());
        }
        if (dto.getDescripcion() != null && !dto.getDescripcion().equals(tarea.getDescripcion())) {
            tarea.setDescripcion(dto.getDescripcion());
        }
        if (dto.getFechaLimite() != null && !dto.getFechaLimite().equals(tarea.getFechaLimite())) {
            tarea.setFechaLimite(dto.getFechaLimite());
        }
        if (dto.getEstado() != null && !dto.getEstado().equals(tarea.getEstado().name())) {
            tarea.setEstado(Tarea.EstadoTarea.valueOf(dto.getEstado()));
        }

        return convertirATareaDTO(tareaRepository.save(tarea));
    }

    /**
     * Elimina una tarea de la base de datos por su ID.
     */
    @Transactional
    public void eliminarTarea(Long id) {
        Tarea tarea = tareaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tarea no encontrada con ID: " + id));
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
