package mk.ukim.finki.basketball.model.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import mk.ukim.finki.basketball.model.domain.League;
import mk.ukim.finki.basketball.model.domain.Team;
import java.util.ArrayList;
import java.util.List;

@Getter
@RequiredArgsConstructor
public class StandingRow {

    private static final int FORM_LENGTH = 5;

    private final Team team;
    private int played;
    private int wins;
    private int losses;
    private int pointsFor;
    private int pointsAgainst;
    private int points;
    private final List<String> form = new ArrayList<>();

    public void record(int scored, int conceded) {
        played++;
        pointsFor += scored;
        pointsAgainst += conceded;

        if (scored > conceded) {
            wins++;
            form.add("W");
        }
        else {
            losses++;
            form.add("L");
        }
        if (form.size() > FORM_LENGTH) {
            form.remove(0);
        }
    }

    public void computePoints(League league) {
        points = wins * league.getWinPoints() + losses * league.getLossPoints();
    }

    public int getDiff() {
        return pointsFor - pointsAgainst;
    }

    public String getDiffLabel() {
        int diff = getDiff();
        if (diff > 0) {
            return "+" + diff;
        }
        else {
            return String.valueOf(diff);
        }
    }
}