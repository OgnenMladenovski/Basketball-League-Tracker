package mk.ukim.finki.basketball.service;

import mk.ukim.finki.basketball.model.domain.League;
import mk.ukim.finki.basketball.model.dto.StandingRow;
import java.util.List;
import java.util.Map;

public interface StandingsService {
    List<StandingRow> standings(League league);

    Map<Long, StandingRow> leaders(List<League> leagues);
}
