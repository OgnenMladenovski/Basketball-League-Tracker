package mk.ukim.finki.basketball.web.form;

import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import mk.ukim.finki.basketball.model.domain.Player;
import mk.ukim.finki.basketball.model.enums.Position;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;

@Getter
@Setter
public class PlayerForm {

    @NotBlank(message = "First name is required")
    @Size(max = 60)
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(max = 60)
    private String lastName;

    @NotNull(message = "Jersey number is required")
    @Min(value = 0, message = "Jersey number must be between 0 and 99")
    @Max(value = 99, message = "Jersey number must be between 0 and 99")
    private Integer jerseyNumber;

    @NotNull(message = "Pick a position")
    private Position position;

    @Min(value = 150, message = "Height must be between 150 and 250 cm")
    @Max(value = 250, message = "Height must be between 150 and 250 cm")
    private Integer heightCm;

    @NotNull(message = "Birth date is required")
    @Past(message = "Birth date must be in the past")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate birthDate;

    @NotNull(message = "Pick a team")
    private Long teamId;

    public static PlayerForm from(Player player) {
        PlayerForm form = new PlayerForm();
        form.setFirstName(player.getFirstName());
        form.setLastName(player.getLastName());
        form.setJerseyNumber(player.getJerseyNumber());
        form.setPosition(player.getPosition());
        form.setHeightCm(player.getHeightCm());
        form.setBirthDate(player.getBirthDate());
        form.setTeamId(player.getTeam().getId());
        return form;
    }

    public void applyTo(Player player) {
        player.setFirstName(firstName);
        player.setLastName(lastName);
        player.setJerseyNumber(jerseyNumber);
        player.setPosition(position);
        player.setHeightCm(heightCm);
        player.setBirthDate(birthDate);
    }
}
