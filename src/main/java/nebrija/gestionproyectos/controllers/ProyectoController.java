package nebrija.gestionproyectos.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import nebrija.gestionproyectos.dto.ProyectoDTO;
import nebrija.gestionproyectos.services.ProyectoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/proyectos")
@RequiredArgsConstructor
public class ProyectoController {

    private final ProyectoService proyectoService;

    @GetMapping
    public ResponseEntity<List<ProyectoDTO>> obtenerTodos() {
        List<ProyectoDTO> proyectos = proyectoService.obtenerTodos();
        return ResponseEntity.ok(proyectos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProyectoDTO> obtenerPorId(@PathVariable Long id) {
        ProyectoDTO proyecto = proyectoService.obtenerPorId(id);
        return ResponseEntity.ok(proyecto);
    }

    @PostMapping
    public ResponseEntity<ProyectoDTO> crearProyecto(@Valid @RequestBody ProyectoDTO proyectoDTO) {
        ProyectoDTO nuevoProyecto = proyectoService.crearProyecto(proyectoDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoProyecto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProyectoDTO> actualizarProyecto(@PathVariable Long id, @Valid @RequestBody ProyectoDTO proyectoDTO) {
        ProyectoDTO proyectoActualizado = proyectoService.actualizarProyecto(id, proyectoDTO);
        return ResponseEntity.ok(proyectoActualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarProyecto(@PathVariable Long id) {
        proyectoService.eliminarProyecto(id);
        return ResponseEntity.noContent().build();
    }
}
