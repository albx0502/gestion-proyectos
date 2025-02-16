package nebrija.gestionproyectos.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import nebrija.gestionproyectos.dto.TareaDTO;
import nebrija.gestionproyectos.services.TareaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tareas")
@RequiredArgsConstructor
public class TareaController {

    private final TareaService tareaService;

    @GetMapping
    public ResponseEntity<List<TareaDTO>> obtenerTodas() {
        List<TareaDTO> tareas = tareaService.obtenerTodas();
        return ResponseEntity.ok(tareas);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TareaDTO> obtenerPorId(@PathVariable Long id) {
        TareaDTO tarea = tareaService.obtenerPorId(id);
        return ResponseEntity.ok(tarea);
    }

    @PostMapping
    public ResponseEntity<TareaDTO> crearTarea(@Valid @RequestBody TareaDTO tareaDTO) {
        TareaDTO nuevaTarea = tareaService.crearTarea(tareaDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevaTarea);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TareaDTO> actualizarTarea(@PathVariable Long id, @Valid @RequestBody TareaDTO tareaDTO) {
        TareaDTO tareaActualizada = tareaService.actualizarTarea(id, tareaDTO);
        return ResponseEntity.ok(tareaActualizada);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarTarea(@PathVariable Long id) {
        tareaService.eliminarTarea(id);
        return ResponseEntity.noContent().build();
    }
}
