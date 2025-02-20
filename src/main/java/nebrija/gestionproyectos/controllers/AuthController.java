package nebrija.gestionproyectos.controllers;

import jakarta.validation.Valid;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder; // 🔥 Agregamos el codificador de contraseñas

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> registrar(@Valid @RequestBody AuthRequestDTO request) {
        try {
            // 🔥 Se encripta la contraseña antes de guardarla en la base de datos
            String encryptedPassword = passwordEncoder.encode(request.getPassword());
            Usuario usuario = authService.registrarUsuario(request.getUsername(), encryptedPassword, Usuario.Rol.USER);

            String token = jwtUtil.generateToken(usuario.getUsername());
            return ResponseEntity.ok(new AuthResponseDTO(token, "Usuario registrado correctamente."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new AuthResponseDTO(null, "Error: " + e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody AuthRequestDTO request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );

            // 🔥 Verificar si el usuario existe antes de generar el token
            Usuario usuario = authService.buscarUsuarioPorUsername(request.getUsername());

            if (!passwordEncoder.matches(request.getPassword(), usuario.getPassword())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new AuthResponseDTO(null, "Contraseña incorrecta."));
            }

            String token = jwtUtil.generateToken(request.getUsername());
            return ResponseEntity.ok(new AuthResponseDTO(token, "Login exitoso."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new AuthResponseDTO(null, "Error de autenticación."));
        }
    }

}
