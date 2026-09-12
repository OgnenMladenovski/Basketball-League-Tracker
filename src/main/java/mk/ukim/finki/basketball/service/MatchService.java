package mk.ukim.finki.basketball.service;

import mk.ukim.finki.basketball.model.domain.Match;
import mk.ukim.finki.basketball.model.enums.MatchStatus;
import java.time.LocalDateTime;
import java.util.List;

public interface MatchService {
    List<Match> search(Long leagueId, MatchStatus status);

    List<Match> latestResults(int limit);

    List<Match> upcoming(int limit);

    Match findById(Long id);

    long countByStatus(MatchStatus status);

    Match schedule(Long leagueId, Long homeTeamId, Long awayTeamId, LocalDateTime playedAt);

    Match recordScore(Long id, int homeScore, int awayScore);

    void delete(Long id);
}
