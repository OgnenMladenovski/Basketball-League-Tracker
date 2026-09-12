package mk.ukim.finki.basketball.repository;

import mk.ukim.finki.basketball.model.domain.League;
import mk.ukim.finki.basketball.model.domain.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TeamRepository extends JpaRepository<Team, Long> {
    List<Team> findAllByOrderByNameAsc();

    List<Team> findByLeaguesContainingOrderByNameAsc(League league);

    long countByLeaguesContaining(League league);
}
