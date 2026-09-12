package mk.ukim.finki.basketball.service;

import mk.ukim.finki.basketball.model.domain.League;
import mk.ukim.finki.basketball.model.domain.Match;
import mk.ukim.finki.basketball.model.domain.Team;
import mk.ukim.finki.basketball.model.dto.StandingRow;
import mk.ukim.finki.basketball.model.enums.MatchStatus;
import mk.ukim.finki.basketball.model.exception.LeagueRuleViolationException;
import mk.ukim.finki.basketball.repository.LeagueRepository;
import mk.ukim.finki.basketball.repository.MatchRepository;
import mk.ukim.finki.basketball.repository.TeamRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class StandingsRulesTest {

    @Autowired
    private MatchService matchService;
    @Autowired
    private StandingsService standingsService;
    @Autowired
    private LeagueRepository leagueRepository;
    @Autowired
    private TeamRepository teamRepository;
    @Autowired
    private MatchRepository matchRepository;

    private final LocalDateTime now = LocalDateTime.now();

    private League league(String name, int winPoints, int lossPoints) {
        return leagueRepository.save(new League(name, "2026/27", winPoints, lossPoints, null, null));
    }

    private Team team(String name, League league) {
        return teamRepository.save(new Team(name, "Skopje", null, 2000, new HashSet<>(Set.of(league))));
    }

    private Long leagueOf(Team team) {
        return team.getLeagues().iterator().next().getId();
    }

    private void play(Team home, Team away, int daysAgo, int homeScore, int awayScore) {
        Match match = matchService.schedule(leagueOf(home), home.getId(), away.getId(), now.minusDays(daysAgo));
        matchService.recordScore(match.getId(), homeScore, awayScore);
    }

    @Test
    void enteringScoresBuildsTheStandings() {
        League league = league("Rules League A", 2, 1);
        Team alpha = team("Alpha", league);
        Team beta = team("Beta", league);

        play(alpha, beta, 3, 90, 80);
        play(beta, alpha, 2, 85, 70);
        play(alpha, beta, 1, 100, 90);

        List<StandingRow> table = standingsService.standings(league);
        StandingRow first = table.get(0);

        assertEquals("Alpha", first.getTeam().getName());
        assertEquals(3, first.getPlayed());
        assertEquals(2, first.getWins());
        assertEquals(1, first.getLosses());
        assertEquals(5, first.getPoints());
        assertEquals(List.of("W", "L", "W"), first.getForm());
        assertEquals(4, table.get(1).getPoints());
    }

    @Test
    void pointDifferenceBreaksTiesOnPoints() {
        League league = league("Rules League B", 2, 1);
        Team alpha = team("Alpha", league);
        Team beta = team("Beta", league);
        Team gamma = team("Gamma", league);

        play(alpha, beta, 3, 80, 60);
        play(beta, gamma, 2, 70, 65);
        play(gamma, alpha, 1, 75, 70);

        List<StandingRow> table = standingsService.standings(league);

        assertEquals("Alpha", table.get(0).getTeam().getName());
        assertEquals("Gamma", table.get(1).getTeam().getName());
        assertEquals("Beta", table.get(2).getTeam().getName());
        assertEquals("+15", table.get(0).getDiffLabel());
        assertEquals("-15", table.get(2).getDiffLabel());
    }

    @Test
    void tiedScoresAreRejected() {
        League league = league("Rules League C", 2, 1);
        Team alpha = team("Alpha", league);
        Team beta = team("Beta", league);
        Match match = matchService.schedule(league.getId(), alpha.getId(), beta.getId(), now.minusDays(1));

        assertThrows(LeagueRuleViolationException.class,
                () -> matchService.recordScore(match.getId(), 80, 80));
        assertEquals(MatchStatus.SCHEDULED, matchRepository.findById(match.getId()).orElseThrow().getStatus());
    }

    @Test
    void aTeamCannotPlayItself() {
        League league = league("Rules League D", 2, 1);
        Team alpha = team("Alpha", league);

        assertThrows(LeagueRuleViolationException.class,
                () -> matchService.schedule(league.getId(), alpha.getId(), alpha.getId(), now.plusDays(1)));
    }

    @Test
    void teamsFromDifferentLeaguesCannotMeet() {
        League leagueE = league("Rules League E", 2, 1);
        Team alpha = team("Alpha", leagueE);
        Team beta = team("Beta", league("Rules League F", 2, 0));

        assertThrows(LeagueRuleViolationException.class,
                () -> matchService.schedule(leagueE.getId(), alpha.getId(), beta.getId(), now.plusDays(1)));
    }
}
