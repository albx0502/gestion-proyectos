package nebrija.gestionproyectos.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDate;

@Data
public class TareaDTO {
    private Long id;

    @NotBlank(message = "El título de la tarea no puede estar vacío")
    @Size(max = 100, message = "El título no puede tener más de 100 caracteres")
    private String titulo;

    @Size(max = 255, message = "La descripción no puede tener más de 255 caracteres")
    private String descripcion;

    @FutureOrPresent(message = "La fecha límite no puede estar en el pasado")
    private LocalDate fechaLimite;

    @NotNull(message = "El estado de la tarea es obligatorio")
    @Pattern(regexp = "PENDIENTE|EN_CURSO|COMPLETADA", message = "El estado debe ser PENDIENTE, EN_CURSO o COMPLETADA")
    private String estado;

    @NotNull(message = "Debe estar asociada a un proyecto")
    private Long proyectoId;
}
