package nebrija.gestionproyectos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.awt.*;
import java.net.URI;

@SpringBootApplication
public class GestionProyectosApplication {
    public static void main(String[] args) {
        SpringApplication.run(GestionProyectosApplication.class, args);

        // Abre la página web automáticamente
        abrirNavegador("http://localhost:8080/index.html");
    }

    private static void abrirNavegador(String url) {
        try {
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().browse(new URI(url));
            }
        } catch (Exception e) {
            System.out.println("No se pudo abrir el navegador: " + e.getMessage());
        }
    }
}
