package nebrija.gestionproyectos.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nebrija.gestionproyectos.dto.ProyectoDTO;
import nebrija.gestionproyectos.models.Usuario;
import nebrija.gestionproyectos.repositories.UsuarioRepository;
import nebrija.gestionproyectos.services.ProyectoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/proyectos")
@RequiredArgsConstructor
@Slf4j
public class ProyectoController {

    private final ProyectoService proyectoService;
    private final UsuarioRepository usuarioRepository;

    @GetMapping
    public ResponseEntity<List<ProyectoDTO>> obtenerTodos(Authentication authentication) {
        Usuario usuario = obtenerUsuarioAutenticado(authentication);
        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        List<ProyectoDTO> proyectos = proyectoService.obtenerTodos(usuario);  // 🔥 Ajuste aquí
        return ResponseEntity.ok(proyectos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProyectoDTO> obtenerPorId(@PathVariable Long id, Authentication authentication) {
        Usuario usuario = obtenerUsuarioAutenticado(authentication);
        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            ProyectoDTO proyecto = proyectoService.obtenerPorId(id, usuario);  // 🔥 Ajuste aquí
            return ResponseEntity.ok(proyecto);
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
    }

    @PostMapping
    public ResponseEntity<?> crearProyecto(@Valid @RequestBody ProyectoDTO proyectoDTO, Authentication authentication) {
        Usuario usuario = obtenerUsuarioAutenticado(authentication);

        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no autenticado");
        }

        log.info("Usuario autenticado: " + usuario.getId()); // ✅ Agregar log
        log.info("Datos del proyecto: " + proyectoDTO); // ✅ Ver qué datos llegan

        ProyectoDTO nuevoProyecto = proyectoService.crearProyecto(proyectoDTO, usuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoProyecto);
    }


    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarProyecto(@PathVariable Long id, @Valid @RequestBody ProyectoDTO proyectoDTO, Authentication authentication) {
        Usuario usuario = obtenerUsuarioAutenticado(authentication);
        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no autenticado");
        }

        ProyectoDTO proyectoActualizado = proyectoService.actualizarProyecto(id, proyectoDTO, usuario);  // 🔥 Ajuste aquí
        return ResponseEntity.ok(proyectoActualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarProyecto(@PathVariable Long id, Authentication authentication) {
        Usuario usuario = obtenerUsuarioAutenticado(authentication);
        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no autenticado");
        }

        proyectoService.eliminarProyecto(id, usuario);  // 🔥 Ajuste aquí
        return ResponseEntity.noContent().build();
    }

    private Usuario obtenerUsuarioAutenticado(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) return null;
        return usuarioRepository.findByUsername(authentication.getName()).orElse(null);
    }
}
