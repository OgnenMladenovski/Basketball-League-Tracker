package mk.ukim.finki.basketball.service;

import mk.ukim.finki.basketball.model.domain.League;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface LeagueService {
    List<League> findAll();

    League findById(Long id);

    List<League> findAllById(Collection<Long> ids);

    Map<Long, Long> teamCounts(List<League> leagues);
}
