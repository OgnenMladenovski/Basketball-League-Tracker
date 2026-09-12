package mk.ukim.finki.basketball.service;

import mk.ukim.finki.basketball.model.domain.Player;
import mk.ukim.finki.basketball.model.enums.Position;
import java.util.List;

public interface PlayerService {
    List<Player> search(Long teamId, Position position);

    Player findById(Long id);

    Player save(Player player);

    void delete(Long id);

    long count();
}
