package mk.ukim.finki.basketball.service.impl;

import mk.ukim.finki.basketball.model.domain.Team;
import mk.ukim.finki.basketball.model.exception.ResourceNotFoundException;
import mk.ukim.finki.basketball.repository.MatchRepository;
import mk.ukim.finki.basketball.repository.PlayerRepository;
import mk.ukim.finki.basketball.repository.TeamRepository;
import mk.ukim.finki.basketball.service.LeagueService;
import mk.ukim.finki.basketball.service.TeamService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class TeamServiceImpl implements TeamService {

    private final TeamRepository teamRepository;
    private final PlayerRepository playerRepository;
    private final MatchRepository matchRepository;
    private final LeagueService leagueService;

    public TeamServiceImpl(TeamRepository teamRepository, PlayerRepository playerRepository, MatchRepository matchRepository, LeagueService leagueService) {
        this.teamRepository = teamRepository;
        this.playerRepository = playerRepository;
        this.matchRepository = matchRepository;
        this.leagueService = leagueService;
    }

    @Override
    public List<Team> search(Long leagueId) {
        if (leagueId == null) {
            return teamRepository.findAllByOrderByNameAsc();
        }
        else {
            return teamRepository.findByLeaguesContainingOrderByNameAsc(leagueService.findById(leagueId));
        }
    }

    @Override
    public Team findById(Long id) {
        return teamRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Team", id));
    }

    @Override
    @Transactional
    public Team save(Team team) {
        return teamRepository.save(team);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Team team = findById(id);
        playerRepository.deleteByTeam(team);
        matchRepository.deleteByHomeTeamOrAwayTeam(team, team);
        teamRepository.delete(team);
    }

    @Override
    public long count() {
        return teamRepository.count();
    }
}
