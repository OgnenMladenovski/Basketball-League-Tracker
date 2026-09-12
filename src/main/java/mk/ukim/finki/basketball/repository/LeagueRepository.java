package mk.ukim.finki.basketball.repository;

import mk.ukim.finki.basketball.model.domain.League;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface LeagueRepository extends JpaRepository<League, Long> {
    List<League> findAllByOrderByNameAsc();
}
