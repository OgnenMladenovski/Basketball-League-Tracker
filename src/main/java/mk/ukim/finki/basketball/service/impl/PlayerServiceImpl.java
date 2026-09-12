package mk.ukim.finki.basketball.service.impl;

import mk.ukim.finki.basketball.model.domain.Player;
import mk.ukim.finki.basketball.model.enums.Position;
import mk.ukim.finki.basketball.model.exception.ResourceNotFoundException;
import mk.ukim.finki.basketball.repository.PlayerRepository;
import mk.ukim.finki.basketball.service.PlayerService;
import mk.ukim.finki.basketball.service.TeamService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class PlayerServiceImpl implements PlayerService {

    private final PlayerRepository playerRepository;
    private final TeamService teamService;

    public PlayerServiceImpl(PlayerRepository playerRepository, TeamService teamService) {
        this.playerRepository = playerRepository;
        this.teamService = teamService;
    }

    @Override
    public List<Player> search(Long teamId, Position position) {
        List<Player> players;
        if (teamId == null) {
            players = playerRepository.findAllByOrderByLastNameAscFirstNameAsc();
        }
        else {
            players = playerRepository.findByTeamOrderByJerseyNumberAsc(teamService.findById(teamId));
        }

        if (position == null) {
            return players;
        }
        else {
            return players.stream().filter(p -> p.getPosition() == position).toList();
        }
    }

    @Override
    public Player findById(Long id) {
        return playerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Player", id));
    }

    @Override
    @Transactional
    public Player save(Player player) {
        return playerRepository.save(player);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        playerRepository.delete(findById(id));
    }

    @Override
    public long count() {
        return playerRepository.count();
    }
}
