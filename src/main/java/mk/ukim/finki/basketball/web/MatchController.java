package mk.ukim.finki.basketball.web;

import jakarta.validation.Valid;
import mk.ukim.finki.basketball.model.domain.Match;
import mk.ukim.finki.basketball.model.enums.MatchStatus;
import mk.ukim.finki.basketball.model.exception.LeagueRuleViolationException;
import mk.ukim.finki.basketball.service.LeagueService;
import mk.ukim.finki.basketball.service.MatchService;
import mk.ukim.finki.basketball.service.TeamService;
import mk.ukim.finki.basketball.web.form.MatchForm;
import mk.ukim.finki.basketball.web.form.ScoreForm;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/matches")
public class MatchController {

    private final MatchService matchService;
    private final TeamService teamService;
    private final LeagueService leagueService;

    public MatchController(MatchService matchService, TeamService teamService, LeagueService leagueService) {
        this.matchService = matchService;
        this.teamService = teamService;
        this.leagueService = leagueService;
    }

    @GetMapping
    public String list(@RequestParam(required = false) Long leagueId,
                       @RequestParam(required = false) MatchStatus status,
                       Model model) {
        model.addAttribute("matches", matchService.search(leagueId, status));
        model.addAttribute("selectedLeagueId", leagueId);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("leagues", leagueService.findAll());
        model.addAttribute("statuses", MatchStatus.values());
        return "matches/list";
    }

    @GetMapping("/new")
    public String createForm(@RequestParam(required = false) Long leagueId, Model model) {
        MatchForm form = new MatchForm();
        form.setLeagueId(leagueId);
        model.addAttribute("form", form);
        populateOptions(model);
        return "matches/form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("form") MatchForm form,
                         BindingResult binding,
                         Model model,
                         RedirectAttributes redirect) {
        if (binding.hasErrors()) {
            populateOptions(model);
            return "matches/form";
        }

        Match match;
        try {
            match = matchService.schedule(form.getLeagueId(), form.getHomeTeamId(), form.getAwayTeamId(), form.getPlayedAt());
        } catch (LeagueRuleViolationException e) {
            model.addAttribute("error", e.getMessage());
            populateOptions(model);
            return "matches/form";
        }

        redirect.addFlashAttribute("success",
                match.getHomeTeam().getName() + " vs " + match.getAwayTeam().getName() + " was scheduled.");
        return "redirect:/matches";
    }

    @GetMapping("/{id}/score")
    public String scoreForm(@PathVariable Long id, Model model) {
        Match match = matchService.findById(id);
        model.addAttribute("match", match);
        model.addAttribute("form", ScoreForm.from(match));
        return "matches/score";
    }

    @PostMapping("/{id}/score")
    public String recordScore(@PathVariable Long id,
                              @Valid @ModelAttribute("form") ScoreForm form,
                              BindingResult binding,
                              Model model,
                              RedirectAttributes redirect) {
        Match match = matchService.findById(id);
        if (binding.hasErrors()) {
            model.addAttribute("match", match);
            return "matches/score";
        }

        try {
            match = matchService.recordScore(id, form.getHomeScore(), form.getAwayScore());
        } catch (LeagueRuleViolationException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("match", match);
            return "matches/score";
        }

        redirect.addFlashAttribute("success",
                "Final: " + match.getHomeTeam().getName() + " " + match.getHomeScore() + " – "
                        + match.getAwayScore() + " " + match.getAwayTeam().getName() + ". Standings updated.");
        return "redirect:/leagues/" + match.getLeague().getId();
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirect) {
        matchService.delete(id);
        redirect.addFlashAttribute("success", "Match removed. Standings updated.");
        return "redirect:/matches";
    }

    private void populateOptions(Model model) {
        model.addAttribute("leagues", leagueService.findAll());
        model.addAttribute("teams", teamService.search(null));
    }
}
