package mk.ukim.finki.basketball.service;

import mk.ukim.finki.basketball.model.domain.Team;
import java.util.List;

public interface TeamService {
    List<Team> search(Long leagueId);

    Team findById(Long id);

    Team save(Team team);

    void delete(Long id);
    
    long count();
}
