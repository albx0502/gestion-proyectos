package nebrija.gestionproyectos.controllers;

import lombok.RequiredArgsConstructor;
import nebrija.gestionproyectos.dto.AuthRequestDTO;
import nebrija.gestionproyectos.dto.AuthResponseDTO;
import nebrija.gestionproyectos.models.Usuario;
import nebrija.gestionproyectos.security.JwtUtil;
import nebrija.gestionproyectos.services.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> registrar(@RequestBody AuthRequestDTO request) {
        System.out.println("⏳ Intentando registrar usuario: " + request.getUsername());
        try {
            Usuario usuario = authService.registrarUsuario(request.getUsername(), request.getPassword(), Usuario.Rol.USER);
            String token = jwtUtil.generateToken(usuario.getUsername());
            System.out.println("✅ Usuario registrado correctamente: " + usuario.getUsername());
            return ResponseEntity.ok(new AuthResponseDTO(token, "Usuario registrado correctamente."));
        } catch (Exception e) {
            System.out.println("❌ Error registrando usuario: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new AuthResponseDTO(null, "Error: " + e.getMessage()));
        }
    }



    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody AuthRequestDTO request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );
        String token = jwtUtil.generateToken(request.getUsername());
        return ResponseEntity.ok(new AuthResponseDTO(token, "Login exitoso."));
    }

}
