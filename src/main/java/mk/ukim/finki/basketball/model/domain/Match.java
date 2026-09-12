package mk.ukim.finki.basketball.model.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mk.ukim.finki.basketball.model.enums.MatchStatus;
import java.time.LocalDateTime;

@Entity
@Table(name = "matches")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Match extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "league_id", nullable = false)
    private League league;

    @ManyToOne
    @JoinColumn(name = "home_team_id", nullable = false)
    private Team homeTeam;

    @ManyToOne
    @JoinColumn(name = "away_team_id", nullable = false)
    private Team awayTeam;

    @Column(nullable = false)
    private LocalDateTime playedAt;

    private Integer homeScore;

    private Integer awayScore;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private MatchStatus status = MatchStatus.SCHEDULED;

    public Match(League league, Team homeTeam, Team awayTeam, LocalDateTime playedAt) {
        this.league = league;
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.playedAt = playedAt;
    }

    public boolean isPlayed() {
        return status == MatchStatus.PLAYED;
    }

    public boolean isHomeWin() {
        return isPlayed() && homeScore > awayScore;
    }

    public boolean isAwayWin() {
        return isPlayed() && awayScore > homeScore;
    }
}
