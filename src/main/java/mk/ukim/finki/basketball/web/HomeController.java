package mk.ukim.finki.basketball.web;

import mk.ukim.finki.basketball.model.domain.League;
import mk.ukim.finki.basketball.model.enums.MatchStatus;
import mk.ukim.finki.basketball.service.LeagueService;
import mk.ukim.finki.basketball.service.MatchService;
import mk.ukim.finki.basketball.service.PlayerService;
import mk.ukim.finki.basketball.service.StandingsService;
import mk.ukim.finki.basketball.service.TeamService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {

    private final LeagueService leagueService;
    private final TeamService teamService;
    private final PlayerService playerService;
    private final MatchService matchService;
    private final StandingsService standingsService;

    public HomeController(LeagueService leagueService, TeamService teamService, PlayerService playerService, MatchService matchService, StandingsService standingsService) {
        this.leagueService = leagueService;
        this.teamService = teamService;
        this.playerService = playerService;
        this.matchService = matchService;
        this.standingsService = standingsService;
    }

    @GetMapping("/")
    public String home(Model model) {
        List<League> leagues = leagueService.findAll();
        model.addAttribute("leagueCount", leagues.size());
        model.addAttribute("teamCount", teamService.count());
        model.addAttribute("playerCount", playerService.count());
        model.addAttribute("playedCount", matchService.countByStatus(MatchStatus.PLAYED));
        model.addAttribute("scheduledCount", matchService.countByStatus(MatchStatus.SCHEDULED));
        model.addAttribute("leagues", leagues);
        model.addAttribute("teamCounts", leagueService.teamCounts(leagues));
        model.addAttribute("leaders", standingsService.leaders(leagues));
        model.addAttribute("latestResults", matchService.latestResults(5));
        model.addAttribute("upcoming", matchService.upcoming(5));
        return "index";
    }
}
