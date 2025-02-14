package nebrija.gestionproyectos.services;

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

    public List<TareaDTO> obtenerTodas() {
        return tareaRepository.findAll().stream().map(this::convertirATareaDTO).collect(Collectors.toList());
    }

    public TareaDTO obtenerPorId(Long id) {
        Tarea tarea = tareaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tarea no encontrada"));
        return convertirATareaDTO(tarea);
    }

    @Transactional
    public TareaDTO crearTarea(TareaDTO dto) {
        Proyecto proyecto = proyectoRepository.findById(dto.getProyectoId())
                .orElseThrow(() -> new RuntimeException("Proyecto no encontrado"));

        Tarea tarea = new Tarea();
        tarea.setTitulo(dto.getTitulo());
        tarea.setDescripcion(dto.getDescripcion());
        tarea.setFechaLimite(dto.getFechaLimite());
        tarea.setEstado(Tarea.EstadoTarea.valueOf(dto.getEstado()));
        tarea.setProyecto(proyecto);

        return convertirATareaDTO(tareaRepository.save(tarea));
    }

    @Transactional
    public TareaDTO actualizarTarea(Long id, TareaDTO dto) {
        Tarea tarea = tareaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tarea no encontrada"));

        tarea.setTitulo(dto.getTitulo());
        tarea.setDescripcion(dto.getDescripcion());
        tarea.setFechaLimite(dto.getFechaLimite());
        tarea.setEstado(Tarea.EstadoTarea.valueOf(dto.getEstado()));

        return convertirATareaDTO(tareaRepository.save(tarea));
    }

    @Transactional
    public void eliminarTarea(Long id) {
        Tarea tarea = tareaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tarea no encontrada"));
        tareaRepository.delete(tarea);
    }

    private TareaDTO convertirATareaDTO(Tarea tarea) {
        TareaDTO dto = new TareaDTO();
        dto.setId(tarea.getId());
        dto.setTitulo(tarea.getTitulo());
        dto.setDescripcion(tarea.getDescripcion());
        dto.setFechaLimite(tarea.getFechaLimite());
        dto.setEstado(tarea.getEstado().name());
        dto.setProyectoId(tarea.getProyecto().getId());
        return dto;
    }
}
