package nebrija.gestionproyectos.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "proyectos")
@Getter
@Setter
@NoArgsConstructor
public class Proyecto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(length = 255)
    private String descripcion;

    @Column(nullable = false)
    private LocalDate fechaInicio;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoProyecto estado;

    @OneToMany(mappedBy = "proyecto", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Tarea> tareas = new ArrayList<>(); // Evita NullPointerException y mejora rendimiento

    public enum EstadoProyecto {
        ACTIVO, EN_PROGRESO, FINALIZADO
    }

    public Proyecto(String nombre, String descripcion, LocalDate fechaInicio, EstadoProyecto estado) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.fechaInicio = fechaInicio;
        this.estado = estado;
    }
}
