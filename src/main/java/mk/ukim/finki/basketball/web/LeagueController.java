package mk.ukim.finki.basketball.web;

import mk.ukim.finki.basketball.model.domain.League;
import mk.ukim.finki.basketball.model.enums.MatchStatus;
import mk.ukim.finki.basketball.repository.MatchRepository;
import mk.ukim.finki.basketball.service.LeagueService;
import mk.ukim.finki.basketball.service.StandingsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/leagues")
public class LeagueController {

    private final LeagueService leagueService;
    private final StandingsService standingsService;
    private final MatchRepository matchRepository;

    public LeagueController(LeagueService leagueService, StandingsService standingsService, MatchRepository matchRepository) {
        this.leagueService = leagueService;
        this.standingsService = standingsService;
        this.matchRepository = matchRepository;
    }

    @GetMapping
    public String list(Model model) {
        List<League> leagues = leagueService.findAll();
        model.addAttribute("leagues", leagues);
        model.addAttribute("teamCounts", leagueService.teamCounts(leagues));
        model.addAttribute("leaders", standingsService.leaders(leagues));
        return "leagues/list";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        League league = leagueService.findById(id);
        model.addAttribute("league", league);
        model.addAttribute("standings", standingsService.standings(league));
        model.addAttribute("results", matchRepository.findByLeagueAndStatusOrderByPlayedAtDesc(league, MatchStatus.PLAYED));
        model.addAttribute("fixtures", matchRepository.findByLeagueAndStatusOrderByPlayedAtAsc(league, MatchStatus.SCHEDULED));
        return "leagues/detail";
    }
}
