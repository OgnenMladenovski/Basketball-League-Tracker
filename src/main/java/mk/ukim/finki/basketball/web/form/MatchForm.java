package mk.ukim.finki.basketball.web.form;

import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

@Getter
@Setter
public class MatchForm {

    @NotNull(message = "Pick a league")
    private Long leagueId;

    @NotNull(message = "Pick the home team")
    private Long homeTeamId;

    @NotNull(message = "Pick the away team")
    private Long awayTeamId;

    @NotNull(message = "Tip-off time is required")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime playedAt;
}
