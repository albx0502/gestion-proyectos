package nebrija.gestionproyectos.controllers;

import lombok.RequiredArgsConstructor;
import nebrija.gestionproyectos.models.Usuario;
import nebrija.gestionproyectos.security.JwtUtil;
import nebrija.gestionproyectos.services.UsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UsuarioService usuarioService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<String> registrar(@RequestParam String username, @RequestParam String password) {
        usuarioService.registrarUsuario(username, password, Usuario.Rol.USER);
        return ResponseEntity.ok("Usuario registrado correctamente.");
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestParam String username, @RequestParam String password) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        String token = jwtUtil.generateToken(username);
        return ResponseEntity.ok(token);
    }
}
