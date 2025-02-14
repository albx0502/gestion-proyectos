package nebrija.gestionproyectos.services;

import lombok.RequiredArgsConstructor;
import nebrija.gestionproyectos.dto.ProyectoDTO;
import nebrija.gestionproyectos.models.Proyecto;
import nebrija.gestionproyectos.models.Tarea;
import nebrija.gestionproyectos.repositories.ProyectoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProyectoService {

    private final ProyectoRepository proyectoRepository;

    public List<ProyectoDTO> obtenerTodos() {
        List<Proyecto> proyectos = proyectoRepository.findAll();
        return proyectos.stream().map(this::convertirAProyectoDTO).collect(Collectors.toList());
    }

    public ProyectoDTO obtenerPorId(Long id) {
        Proyecto proyecto = proyectoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Proyecto no encontrado"));
        return convertirAProyectoDTO(proyecto);
    }

    @Transactional
    public ProyectoDTO crearProyecto(ProyectoDTO dto) {
        Proyecto proyecto = new Proyecto();
        proyecto.setNombre(dto.getNombre());
        proyecto.setDescripcion(dto.getDescripcion());
        proyecto.setFechaInicio(dto.getFechaInicio());
        proyecto.setEstado(Proyecto.EstadoProyecto.valueOf(dto.getEstado()));
        Proyecto proyectoGuardado = proyectoRepository.save(proyecto);
        return convertirAProyectoDTO(proyectoGuardado);
    }

    @Transactional
    public ProyectoDTO actualizarProyecto(Long id, ProyectoDTO dto) {
        Proyecto proyecto = proyectoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Proyecto no encontrado"));
        proyecto.setNombre(dto.getNombre());
        proyecto.setDescripcion(dto.getDescripcion());
        proyecto.setFechaInicio(dto.getFechaInicio());
        proyecto.setEstado(Proyecto.EstadoProyecto.valueOf(dto.getEstado()));
        return convertirAProyectoDTO(proyectoRepository.save(proyecto));
    }

    @Transactional
    public void eliminarProyecto(Long id) {
        Proyecto proyecto = proyectoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Proyecto no encontrado"));
        proyectoRepository.delete(proyecto);
    }

    private ProyectoDTO convertirAProyectoDTO(Proyecto proyecto) {
        ProyectoDTO dto = new ProyectoDTO();
        dto.setId(proyecto.getId());
        dto.setNombre(proyecto.getNombre());
        dto.setDescripcion(proyecto.getDescripcion());
        dto.setFechaInicio(proyecto.getFechaInicio());
        dto.setEstado(proyecto.getEstado().name());
        dto.setTareas(proyecto.getTareas().stream().map(t -> {
            var tareaDto = new nebrija.gestionproyectos.dto.TareaDTO();
            tareaDto.setId(t.getId());
            tareaDto.setTitulo(t.getTitulo());
            tareaDto.setDescripcion(t.getDescripcion());
            tareaDto.setFechaLimite(t.getFechaLimite());
            tareaDto.setEstado(t.getEstado().name());
            tareaDto.setProyectoId(proyecto.getId());
            return tareaDto;
        }).collect(Collectors.toList()));
        return dto;
    }
}
