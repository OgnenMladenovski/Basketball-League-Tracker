package mk.ukim.finki.basketball.repository;

import mk.ukim.finki.basketball.model.domain.Player;
import mk.ukim.finki.basketball.model.domain.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PlayerRepository extends JpaRepository<Player, Long> {
    List<Player> findAllByOrderByLastNameAscFirstNameAsc();

    List<Player> findByTeamOrderByJerseyNumberAsc(Team team);

    void deleteByTeam(Team team);
}
