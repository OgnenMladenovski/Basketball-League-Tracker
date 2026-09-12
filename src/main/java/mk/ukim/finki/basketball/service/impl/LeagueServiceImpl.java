package mk.ukim.finki.basketball.service.impl;

import mk.ukim.finki.basketball.model.domain.League;
import mk.ukim.finki.basketball.model.exception.ResourceNotFoundException;
import mk.ukim.finki.basketball.repository.LeagueRepository;
import mk.ukim.finki.basketball.repository.TeamRepository;
import mk.ukim.finki.basketball.service.LeagueService;
import org.springframework.stereotype.Service;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class LeagueServiceImpl implements LeagueService {

    private final LeagueRepository leagueRepository;
    private final TeamRepository teamRepository;

    public LeagueServiceImpl(LeagueRepository leagueRepository, TeamRepository teamRepository) {
        this.leagueRepository = leagueRepository;
        this.teamRepository = teamRepository;
    }

    @Override
    public List<League> findAll() {
        return leagueRepository.findAllByOrderByNameAsc();
    }

    @Override
    public League findById(Long id) {
        return leagueRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("League", id));
    }

    @Override
    public List<League> findAllById(Collection<Long> ids) {
        return leagueRepository.findAllById(ids);
    }

    @Override
    public Map<Long, Long> teamCounts(List<League> leagues) {
        Map<Long, Long> counts = new LinkedHashMap<>();

        for (League league : leagues) {
            counts.put(league.getId(), teamRepository.countByLeaguesContaining(league));
        }

        return counts;
    }
}
