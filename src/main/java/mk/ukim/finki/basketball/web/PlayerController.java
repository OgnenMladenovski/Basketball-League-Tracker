package mk.ukim.finki.basketball.web;

import jakarta.validation.Valid;
import mk.ukim.finki.basketball.model.domain.Player;
import mk.ukim.finki.basketball.model.enums.Position;
import mk.ukim.finki.basketball.service.PlayerService;
import mk.ukim.finki.basketball.service.TeamService;
import mk.ukim.finki.basketball.web.form.PlayerForm;
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
@RequestMapping("/players")
public class PlayerController {

    private final PlayerService playerService;
    private final TeamService teamService;

    public PlayerController(PlayerService playerService, TeamService teamService) {
        this.playerService = playerService;
        this.teamService = teamService;
    }

    @GetMapping
    public String list(@RequestParam(required = false) Long teamId,
                       @RequestParam(required = false) Position position,
                       Model model) {
        model.addAttribute("players", playerService.search(teamId, position));
        model.addAttribute("selectedTeamId", teamId);
        model.addAttribute("selectedPosition", position);
        populateOptions(model);
        return "players/list";
    }

    @GetMapping("/new")
    public String createForm(@RequestParam(required = false) Long teamId, Model model) {
        PlayerForm form = new PlayerForm();
        form.setTeamId(teamId);
        model.addAttribute("form", form);
        populateOptions(model);
        return "players/form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("form") PlayerForm form,
                         BindingResult binding,
                         Model model,
                         RedirectAttributes redirect) {
        if (binding.hasErrors()) {
            populateOptions(model);
            return "players/form";
        }

        Player player = new Player();
        form.applyTo(player);
        player.setTeam(teamService.findById(form.getTeamId()));
        playerService.save(player);
        redirect.addFlashAttribute("success", player.getFullName() + " signed for " + player.getTeam().getName() + ".");
        return "redirect:/players?teamId=" + player.getTeam().getId();
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("form", PlayerForm.from(playerService.findById(id)));
        model.addAttribute("playerId", id);
        populateOptions(model);
        return "players/form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("form") PlayerForm form,
                         BindingResult binding,
                         Model model,
                         RedirectAttributes redirect) {
        if (binding.hasErrors()) {
            model.addAttribute("playerId", id);
            populateOptions(model);
            return "players/form";
        }

        Player player = playerService.findById(id);
        form.applyTo(player);
        player.setTeam(teamService.findById(form.getTeamId()));
        playerService.save(player);
        redirect.addFlashAttribute("success", player.getFullName() + " was updated.");
        return "redirect:/players?teamId=" + player.getTeam().getId();
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirect) {
        Player player = playerService.findById(id);
        Long teamId = player.getTeam().getId();
        playerService.delete(id);
        redirect.addFlashAttribute("success", player.getFullName() + " was released.");
        return "redirect:/players?teamId=" + teamId;
    }

    private void populateOptions(Model model) {
        model.addAttribute("teams", teamService.search(null));
        model.addAttribute("positions", Position.values());
    }
}
