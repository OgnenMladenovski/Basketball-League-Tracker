package mk.ukim.finki.basketball.repository;

import mk.ukim.finki.basketball.model.domain.League;
import mk.ukim.finki.basketball.model.domain.Match;
import mk.ukim.finki.basketball.model.domain.Team;
import mk.ukim.finki.basketball.model.enums.MatchStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MatchRepository extends JpaRepository<Match, Long> {
    List<Match> findAllByOrderByPlayedAtDesc();

    List<Match> findByStatusOrderByPlayedAtDesc(MatchStatus status);

    List<Match> findByStatusOrderByPlayedAtAsc(MatchStatus status);

    List<Match> findByLeagueAndStatusOrderByPlayedAtDesc(League league, MatchStatus status);

    List<Match> findByLeagueAndStatusOrderByPlayedAtAsc(League league, MatchStatus status);

    long countByStatus(MatchStatus status);

    void deleteByHomeTeamOrAwayTeam(Team homeTeam, Team awayTeam);
}
