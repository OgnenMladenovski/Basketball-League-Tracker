package mk.ukim.finki.basketball.web.form;

import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import mk.ukim.finki.basketball.model.domain.League;
import mk.ukim.finki.basketball.model.domain.Team;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
public class TeamForm {

    @NotBlank(message = "Team name is required")
    @Size(max = 80)
    private String name;

    @NotBlank(message = "City is required")
    @Size(max = 60)
    private String city;

    @Size(max = 80)
    private String homeArena;

    @Min(value = 1891, message = "Basketball was invented in 1891")
    @Max(value = 2100)
    private Integer foundedYear;

    @NotEmpty(message = "Pick at least one league")
    private Set<Long> leagueIds = new HashSet<>();

    public static TeamForm from(Team team) {
        TeamForm form = new TeamForm();
        form.setName(team.getName());
        form.setCity(team.getCity());
        form.setHomeArena(team.getHomeArena());
        form.setFoundedYear(team.getFoundedYear());
        form.setLeagueIds(team.getLeagues().stream().map(League::getId).collect(Collectors.toSet()));
        return form;
    }

    public void applyTo(Team team) {
        team.setName(name);
        team.setCity(city);
        team.setHomeArena(homeArena);
        team.setFoundedYear(foundedYear);
    }
}
