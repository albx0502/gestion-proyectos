package nebrija.gestionproyectos.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nebrija.gestionproyectos.dto.TareaDTO;
import nebrija.gestionproyectos.models.Usuario;
import nebrija.gestionproyectos.repositories.UsuarioRepository;
import nebrija.gestionproyectos.services.TareaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/tareas")
@RequiredArgsConstructor
@Slf4j
public class TareaController {

    private final TareaService tareaService;
    private final UsuarioRepository usuarioRepository;

    @GetMapping
    public ResponseEntity<List<TareaDTO>> obtenerTodas(Authentication authentication) {
        Usuario usuario = obtenerUsuarioAutenticado(authentication);
        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        List<TareaDTO> tareas = tareaService.obtenerTodas(usuario);  // 🔥 Ajuste aquí
        return ResponseEntity.ok(tareas);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TareaDTO> obtenerPorId(@PathVariable Long id, Authentication authentication) {
        Usuario usuario = obtenerUsuarioAutenticado(authentication);
        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            TareaDTO tarea = tareaService.obtenerPorId(id, usuario);  // 🔥 Ajuste aquí
            return ResponseEntity.ok(tarea);
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
    }

    @PostMapping
    public ResponseEntity<?> crearTarea(@Valid @RequestBody TareaDTO tareaDTO, Authentication authentication) {
        Usuario usuario = obtenerUsuarioAutenticado(authentication);
        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no autenticado");
        }

        TareaDTO nuevaTarea = tareaService.crearTarea(tareaDTO, usuario);  // 🔥 Ajuste aquí
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevaTarea);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarTarea(@PathVariable Long id, @Valid @RequestBody TareaDTO tareaDTO, Authentication authentication) {
        Usuario usuario = obtenerUsuarioAutenticado(authentication);
        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no autenticado");
        }

        TareaDTO tareaActualizada = tareaService.actualizarTarea(id, tareaDTO, usuario);  // 🔥 Ajuste aquí
        return ResponseEntity.ok(tareaActualizada);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarTarea(@PathVariable Long id, Authentication authentication) {
        Usuario usuario = obtenerUsuarioAutenticado(authentication);
        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no autenticado");
        }

        tareaService.eliminarTarea(id, usuario);  // 🔥 Ajuste aquí
        return ResponseEntity.noContent().build();
    }

    private Usuario obtenerUsuarioAutenticado(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) return null;
        return usuarioRepository.findByUsername(authentication.getName()).orElse(null);
    }
}
