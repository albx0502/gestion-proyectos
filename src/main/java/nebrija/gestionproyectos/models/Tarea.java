package nebrija.gestionproyectos.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;

@Entity
@Table(name = "tareas")
@Getter
@Setter
@NoArgsConstructor
public class Tarea {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String titulo;

    @Column(length = 255)
    private String descripcion;

    @Column
    private LocalDate fechaLimite;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoTarea estado;

    @ManyToOne(fetch = FetchType.LAZY) // Se carga solo cuando se accede a ella
    @JoinColumn(name = "proyecto_id", nullable = false)
    private Proyecto proyecto;

    public enum EstadoTarea {
        PENDIENTE, EN_CURSO, COMPLETADA
    }

    public Tarea(String titulo, String descripcion, LocalDate fechaLimite, EstadoTarea estado, Proyecto proyecto) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.fechaLimite = fechaLimite;
        this.estado = estado;
        this.proyecto = proyecto;
    }
}
