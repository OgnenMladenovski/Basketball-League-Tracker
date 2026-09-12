package mk.ukim.finki.basketball.web;

import jakarta.validation.Valid;
import mk.ukim.finki.basketball.model.domain.League;
import mk.ukim.finki.basketball.model.domain.Team;
import mk.ukim.finki.basketball.service.LeagueService;
import mk.ukim.finki.basketball.service.TeamService;
import mk.ukim.finki.basketball.web.form.TeamForm;
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
import java.util.HashSet;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/teams")
public class TeamController {

    private final TeamService teamService;
    private final LeagueService leagueService;

    public TeamController(TeamService teamService, LeagueService leagueService) {
        this.teamService = teamService;
        this.leagueService = leagueService;
    }

    @GetMapping
    public String list(@RequestParam(required = false) Long leagueId, Model model) {
        model.addAttribute("teams", teamService.search(leagueId));
        model.addAttribute("selectedLeagueId", leagueId);
        model.addAttribute("leagues", leagueService.findAll());
        return "teams/list";
    }

    @GetMapping("/new")
    public String createForm(@RequestParam(required = false) Long leagueId, Model model) {
        TeamForm form = new TeamForm();
        if (leagueId != null) {
            form.getLeagueIds().add(leagueId);
        }
        model.addAttribute("form", form);
        populateOptions(model);
        return "teams/form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("form") TeamForm form,
                         BindingResult binding,
                         Model model,
                         RedirectAttributes redirect) {
        if (binding.hasErrors()) {
            populateOptions(model);
            return "teams/form";
        }
        Team team = new Team();
        form.applyTo(team);
        team.setLeagues(new HashSet<>(leagueService.findAllById(form.getLeagueIds())));
        teamService.save(team);
        redirect.addFlashAttribute("success", team.getName() + " joined " + leagueNames(team) + ".");
        return "redirect:/teams";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("form", TeamForm.from(teamService.findById(id)));
        model.addAttribute("teamId", id);
        populateOptions(model);
        return "teams/form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("form") TeamForm form,
                         BindingResult binding,
                         Model model,
                         RedirectAttributes redirect) {
        if (binding.hasErrors()) {
            model.addAttribute("teamId", id);
            populateOptions(model);
            return "teams/form";
        }
        Team team = teamService.findById(id);
        form.applyTo(team);
        team.setLeagues(new HashSet<>(leagueService.findAllById(form.getLeagueIds())));
        teamService.save(team);
        redirect.addFlashAttribute("success", team.getName() + " was updated.");
        return "redirect:/teams";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirect) {
        String name = teamService.findById(id).getName();
        teamService.delete(id);
        redirect.addFlashAttribute("success", name + ", its roster and its matches were removed.");
        return "redirect:/teams";
    }

    private void populateOptions(Model model) {
        model.addAttribute("leagues", leagueService.findAll());
    }

    private String leagueNames(Team team) {
        return team.getLeagues().stream().map(League::getName).sorted().collect(Collectors.joining(", "));
    }
}
