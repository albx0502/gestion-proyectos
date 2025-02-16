package nebrija.gestionproyectos.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
public class ProyectoDTO {

    private Long id;

    @NotBlank(message = "El nombre del proyecto no puede estar vacío")
    @Size(max = 100, message = "El nombre no puede tener más de 100 caracteres")
    private String nombre;

    @Size(max = 255, message = "La descripción no puede tener más de 255 caracteres")
    private String descripcion;

    @NotNull(message = "La fecha de inicio es obligatoria")
    @PastOrPresent(message = "La fecha de inicio no puede estar en el futuro")
    private LocalDate fechaInicio;

    @NotNull(message = "El estado del proyecto es obligatorio")
    @Pattern(regexp = "ACTIVO|EN_PROGRESO|FINALIZADO", message = "El estado debe ser ACTIVO, EN_PROGRESO o FINALIZADO")
    private String estado;

    private List<TareaDTO> tareas;
}
