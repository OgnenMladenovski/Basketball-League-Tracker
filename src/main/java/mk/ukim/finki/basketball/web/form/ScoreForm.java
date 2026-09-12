package mk.ukim.finki.basketball.web.form;

import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import mk.ukim.finki.basketball.model.domain.Match;

@Getter
@Setter
public class ScoreForm {

    @NotNull(message = "Home score is required")
    @Min(value = 0, message = "Score cannot be negative")
    @Max(value = 300, message = "Score must be at most 300")
    private Integer homeScore;

    @NotNull(message = "Away score is required")
    @Min(value = 0, message = "Score cannot be negative")
    @Max(value = 300, message = "Score must be at most 300")
    private Integer awayScore;

    public static ScoreForm from(Match match) {
        ScoreForm form = new ScoreForm();
        form.setHomeScore(match.getHomeScore());
        form.setAwayScore(match.getAwayScore());
        return form;
    }
}
