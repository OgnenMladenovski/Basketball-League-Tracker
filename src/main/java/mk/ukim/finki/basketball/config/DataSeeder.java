package mk.ukim.finki.basketball.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mk.ukim.finki.basketball.model.domain.League;
import mk.ukim.finki.basketball.model.domain.Match;
import mk.ukim.finki.basketball.model.domain.Player;
import mk.ukim.finki.basketball.model.domain.Team;
import mk.ukim.finki.basketball.model.enums.MatchStatus;
import mk.ukim.finki.basketball.model.enums.Position;
import mk.ukim.finki.basketball.repository.LeagueRepository;
import mk.ukim.finki.basketball.repository.MatchRepository;
import mk.ukim.finki.basketball.repository.PlayerRepository;
import mk.ukim.finki.basketball.repository.TeamRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "league.seed-data", havingValue = "true")
public class DataSeeder implements CommandLineRunner {

    private static final int PLAYED_ROUNDS = 3;
    private static final String[] FIRST_NAMES = {"Aleksandar", "Bojan", "Damjan", "Filip", "Goran", "Igor", "Jovan", "Kristijan", "Luka", "Marko", "Nikola", "Petar", "Stefan", "Tomislav", "Vladimir"};
    private static final String[] LAST_NAMES = {"Petrovski", "Stojanovski", "Nikolovski", "Trajkovski", "Ristovski", "Kostovski", "Jovanović", "Petrović", "Marković", "Kovačević", "Popović", "Nikolić", "Ilić", "Pavlović"};
    private static final int[] JERSEYS = {4, 7, 11, 23, 33};
    private static final int[] HEIGHTS = {186, 193, 200, 206, 211};

    private final LeagueRepository leagueRepository;
    private final TeamRepository teamRepository;
    private final PlayerRepository playerRepository;
    private final MatchRepository matchRepository;
    private final Random random = new Random(2026);
    private final LocalDate nextRound = LocalDate.now().plusDays(3);

    @Override
    @Transactional
    public void run(String... args) {
        if (leagueRepository.count() > 0) {
            log.info("Database already contains data, skipping seed.");
            return;
        }
        log.info("Seeding leagues, teams, players and matches.");

        League kfm = leagueRepository.save(new League("KFM Super League", "2026/27", 2, 1, "#D20000", "#FFE600"));
        League aba = leagueRepository.save(new League("ABA League", "2026/27", 2, 1, "#215BA6", "#F26E22"));
        Team mzt = team("MZT Skopje Aerodrom", "Skopje", "Jane Sandanski Arena", 1966, kfm, aba);

        season(kfm, List.of(
                mzt,
                team("Skopje 2023", "Skopje", "Jane Sandanski Arena", 2023, kfm),
                team("Rabotnički", "Skopje", "Kale Sports Hall", 1945, kfm),
                team("TFT Skopje", "Skopje", "T Arena", 1991, kfm),
                team("Pelister", "Bitola", "Mladost Sports Hall", 1955, kfm),
                team("MKK Kumanovo", "Kumanovo", "Kumanovo Sports Hall", 2009, kfm),
                team("Kožuv", "Gevgelija", "26 April Sports Hall", 1955, kfm),
                team("Tikveš Golden Eagle", "Kavadarci", "Jasmin Sports Hall", 1970, kfm),
                team("Strumica 2001", "Strumica", "Park Sports Hall", 2001, kfm),
                team("Sokoli 1933", "Štip", "Jackson Sports Hall", 1933, kfm)));

        season(aba, List.of(
                mzt,
                team("Crvena zvezda", "Belgrade", "Aleksandar Nikolić Hall", 1945, aba),
                team("Partizan", "Belgrade", "Belgrade Arena", 1945, aba),
                team("FMP", "Belgrade", "Železnik Hall", 1975, aba),
                team("Mega", "Belgrade", "Ranko Žeravica Sports Hall", 1998, aba),
                team("Borac Čačak", "Čačak", "Borac Hall", 1945, aba),
                team("Spartak Subotica", "Subotica", "Dudova Šuma Hall", 1945, aba),
                team("Budućnost", "Podgorica", "Morača Sports Center", 1949, aba),
                team("SC Derby", "Podgorica", "Morača Sports Center", 1997, aba),
                team("Cedevita Olimpija", "Ljubljana", "Arena Stožice", 2019, aba),
                team("Ilirija", "Ljubljana", "Tivoli Hall", 1957, aba),
                team("Krka", "Novo Mesto", "Leon Štukelj Hall", 1948, aba),
                team("Cibona", "Zagreb", "Dražen Petrović Hall", 1946, aba),
                team("Zadar", "Zadar", "Krešimir Ćosić Hall", 1945, aba),
                team("Igokea", "Laktaši", "Laktaši Sports Hall", 1976, aba),
                team("Bosna", "Sarajevo", "Zetra Olympic Hall", 1951, aba),
                team("Široki", "Široki Brijeg", "Pecara Sports Hall", 1974, aba),
                team("Dubai Basketball", "Dubai", "Coca-Cola Arena", 2023, aba),
                team("U-BT Cluj-Napoca", "Cluj-Napoca", "BTarena", 1947, aba),
                team("Slovan Bratislava", "Bratislava", "Gopass Arena", 1940, aba)));

        log.info("Seed data loaded.");
    }

    private Team team(String name, String city, String arena, int founded, League... leagues) {
        Team team = teamRepository.save(new Team(name, city, arena, founded, new HashSet<>(List.of(leagues))));
        for (Position position : Position.values()) {
            int slot = position.ordinal();
            playerRepository.save(new Player(
                    FIRST_NAMES[random.nextInt(FIRST_NAMES.length)],
                    LAST_NAMES[random.nextInt(LAST_NAMES.length)],
                    JERSEYS[slot] + random.nextInt(3),
                    position,
                    HEIGHTS[slot] + random.nextInt(-3, 4),
                    LocalDate.of(random.nextInt(1994, 2006), random.nextInt(1, 13), random.nextInt(1, 29)),
                    team));
        }
        return team;
    }

    private void season(League league, List<Team> teams) {
        List<Team> order = new ArrayList<>(teams);
        for (int round = 0; round <= PLAYED_ROUNDS; round++) {
            LocalDateTime tipOff = nextRound.minusWeeks(PLAYED_ROUNDS - round).atTime(20, 0);
            for (int i = 0; i < order.size() / 2; i++) {
                match(league, order.get(i), order.get(order.size() - 1 - i), tipOff, round < PLAYED_ROUNDS);
            }
            Collections.rotate(order.subList(1, order.size()), 1);
        }
    }

    private void match(League league, Team home, Team away, LocalDateTime tipOff, boolean played) {
        Match match = new Match(league, home, away, tipOff);
        if (played) {
            int homeScore = random.nextInt(70, 100);
            int awayScore = random.nextInt(70, 100);
            if (awayScore == homeScore) {
                awayScore++;
            }
            match.setHomeScore(homeScore);
            match.setAwayScore(awayScore);
            match.setStatus(MatchStatus.PLAYED);
        }
        matchRepository.save(match);
    }
}
