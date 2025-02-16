package nebrija.gestionproyectos.services;

import lombok.RequiredArgsConstructor;
import nebrija.gestionproyectos.models.Usuario;
import nebrija.gestionproyectos.repositories.UsuarioRepository;
import nebrija.gestionproyectos.security.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    /**
     * Registra un nuevo usuario en la base de datos
     */
    public Usuario registrarUsuario(String username, String password, Usuario.Rol rol) {
        if (usuarioRepository.existsByUsername(username)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El usuario ya existe");
        }

        Usuario usuario = new Usuario();
        usuario.setUsername(username);
        usuario.setPassword(passwordEncoder.encode(password));
        usuario.setRol(rol);

        return usuarioRepository.save(usuario);
    }


    /**
     * Autentica un usuario y genera un token JWT
     */
    public String autenticarUsuario(String username, String password) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );

        return jwtUtil.generateToken(username);
    }
}
