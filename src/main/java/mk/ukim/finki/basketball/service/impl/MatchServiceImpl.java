package mk.ukim.finki.basketball.service.impl;

import mk.ukim.finki.basketball.model.domain.League;
import mk.ukim.finki.basketball.model.domain.Match;
import mk.ukim.finki.basketball.model.domain.Team;
import mk.ukim.finki.basketball.model.enums.MatchStatus;
import mk.ukim.finki.basketball.model.exception.LeagueRuleViolationException;
import mk.ukim.finki.basketball.model.exception.ResourceNotFoundException;
import mk.ukim.finki.basketball.repository.MatchRepository;
import mk.ukim.finki.basketball.service.LeagueService;
import mk.ukim.finki.basketball.service.MatchService;
import mk.ukim.finki.basketball.service.TeamService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class MatchServiceImpl implements MatchService {

    private final MatchRepository matchRepository;
    private final TeamService teamService;
    private final LeagueService leagueService;

    public MatchServiceImpl(MatchRepository matchRepository, TeamService teamService, LeagueService leagueService) {
        this.matchRepository = matchRepository;
        this.teamService = teamService;
        this.leagueService = leagueService;
    }

    @Override
    public List<Match> search(Long leagueId, MatchStatus status) {
        return matchRepository.findAllByOrderByPlayedAtDesc().stream()
                .filter(m -> leagueId == null || m.getLeague().getId().equals(leagueId))
                .filter(m -> status == null || m.getStatus() == status)
                .toList();
    }

    @Override
    public List<Match> latestResults(int limit) {
        return matchRepository.findByStatusOrderByPlayedAtDesc(MatchStatus.PLAYED).stream()
                .limit(limit)
                .toList();
    }

    @Override
    public List<Match> upcoming(int limit) {
        return matchRepository.findByStatusOrderByPlayedAtAsc(MatchStatus.SCHEDULED).stream()
                .limit(limit)
                .toList();
    }

    @Override
    public Match findById(Long id) {
        return matchRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Match", id));
    }

    @Override
    public long countByStatus(MatchStatus status) {
        return matchRepository.countByStatus(status);
    }

    @Override
    @Transactional
    public Match schedule(Long leagueId, Long homeTeamId, Long awayTeamId, LocalDateTime playedAt) {
        if (homeTeamId.equals(awayTeamId)) {
            throw new LeagueRuleViolationException("A team cannot play against itself.");
        }

        League league = leagueService.findById(leagueId);
        Team home = teamService.findById(homeTeamId);
        Team away = teamService.findById(awayTeamId);

        if (!playsIn(home, league) || !playsIn(away, league)) {
            throw new LeagueRuleViolationException(home.getName() + " and " + away.getName() + " must both play in " + league.getName() + ".");
        }

        return matchRepository.save(new Match(league, home, away, playedAt));
    }

    private boolean playsIn(Team team, League league) {
        return team.getLeagues().stream().anyMatch(l -> l.getId().equals(league.getId()));
    }

    @Override
    @Transactional
    public Match recordScore(Long id, int homeScore, int awayScore) {
        if (homeScore == awayScore) {
            throw new LeagueRuleViolationException("Basketball games cannot end in a tie. Enter the score after overtime.");
        }

        Match match = findById(id);
        match.setHomeScore(homeScore);
        match.setAwayScore(awayScore);
        match.setStatus(MatchStatus.PLAYED);
        return matchRepository.save(match);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        matchRepository.delete(findById(id));
    }
}
