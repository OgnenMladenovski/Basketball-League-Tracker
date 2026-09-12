package mk.ukim.finki.basketball.service.impl;

import mk.ukim.finki.basketball.model.domain.League;
import mk.ukim.finki.basketball.model.domain.Match;
import mk.ukim.finki.basketball.model.domain.Team;
import mk.ukim.finki.basketball.model.enums.MatchStatus;
import mk.ukim.finki.basketball.repository.MatchRepository;
import mk.ukim.finki.basketball.repository.TeamRepository;
import mk.ukim.finki.basketball.model.dto.StandingRow;
import mk.ukim.finki.basketball.service.StandingsService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class StandingsServiceImpl implements StandingsService {

    private static final Comparator<StandingRow> TABLE_ORDER = Comparator
            .comparingInt((StandingRow row) -> -row.getPoints())
            .thenComparingInt(row -> -row.getDiff())
            .thenComparingInt(row -> -row.getPointsFor())
            .thenComparing(row -> row.getTeam().getName());

    private final TeamRepository teamRepository;
    private final MatchRepository matchRepository;

    public StandingsServiceImpl(TeamRepository teamRepository, MatchRepository matchRepository) {
        this.teamRepository = teamRepository;
        this.matchRepository = matchRepository;
    }

    @Override
    public List<StandingRow> standings(League league) {
        Map<Long, StandingRow> rows = new LinkedHashMap<>();

        for (Team team : teamRepository.findByLeaguesContainingOrderByNameAsc(league)) {
            rows.put(team.getId(), new StandingRow(team));
        }

        for (Match match : matchRepository.findByLeagueAndStatusOrderByPlayedAtAsc(league, MatchStatus.PLAYED)) {
            StandingRow home = rows.get(match.getHomeTeam().getId());
            StandingRow away = rows.get(match.getAwayTeam().getId());

            if (home == null || away == null) {
                continue;
            }

            home.record(match.getHomeScore(), match.getAwayScore());
            away.record(match.getAwayScore(), match.getHomeScore());
        }

        List<StandingRow> table = new ArrayList<>(rows.values());
        table.forEach(row -> row.computePoints(league));
        table.sort(TABLE_ORDER);
        return table;
    }

    @Override
    public Map<Long, StandingRow> leaders(List<League> leagues) {
        Map<Long, StandingRow> leaders = new LinkedHashMap<>();

        for (League league : leagues) {
            List<StandingRow> table = standings(league);
            if (!table.isEmpty()) {
                leaders.put(league.getId(), table.get(0));
            }
        }

        return leaders;
    }
}
