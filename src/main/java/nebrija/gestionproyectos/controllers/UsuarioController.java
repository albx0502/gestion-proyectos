package nebrija.gestionproyectos.controllers;

import lombok.RequiredArgsConstructor;
import nebrija.gestionproyectos.models.Usuario;
import nebrija.gestionproyectos.repositories.UsuarioRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioRepository usuarioRepository;

    @GetMapping("/me")
    public ResponseEntity<Usuario> obtenerUsuarioAutenticado(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            return ResponseEntity.status(401).build();
        }

        Optional<Usuario> usuario = usuarioRepository.findByUsername(authentication.getName());
        return usuario.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.status(404).build());
    }
}
