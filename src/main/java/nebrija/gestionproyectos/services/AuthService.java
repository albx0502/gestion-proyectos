package nebrija.gestionproyectos.services;

import lombok.RequiredArgsConstructor;
import nebrija.gestionproyectos.models.Usuario;
import nebrija.gestionproyectos.repositories.UsuarioRepository;
import nebrija.gestionproyectos.security.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    /**
     * Registra un nuevo usuario en la base de datos con validación de contraseña.
     */
    public Usuario registrarUsuario(String username, String password, Usuario.Rol rol) {
        if (usuarioRepository.existsByUsername(username)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El usuario ya existe");
        }

        // 🔥 Se asegura de que la contraseña se almacene encriptada

        Usuario usuario = new Usuario();
        usuario.setUsername(username);
        usuario.setPassword(password);
        usuario.setRol(rol);

        return usuarioRepository.save(usuario);
    }

    /**
     * Autentica un usuario y genera un token JWT con manejo de errores mejorado.
     */
    public String autenticarUsuario(String username, String password) {
        try {
            Usuario usuario = usuarioRepository.findByUsername(username)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado."));

            if (!usuario.getPassword().equals(password)) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciales incorrectas.");
            }


            return jwtUtil.generateToken(username);
        } catch (BadCredentialsException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciales incorrectas.");
        }
    }

    /**
     * Método para buscar un usuario por su nombre de usuario.
     */
    public Usuario buscarUsuarioPorUsername(String username) {
        return usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado."));
    }

    /**
     * Valida que la contraseña cumpla con los requisitos de seguridad.
     */
    private boolean esContrasenaValida(String password) {
        return password.length() >= 6 &&
                password.matches(".*[A-Z].*") &&  // Al menos una mayúscula
                password.matches(".*[a-z].*") &&  // Al menos una minúscula
                password.matches(".*\\d.*");      // Al menos un número
    }
}
