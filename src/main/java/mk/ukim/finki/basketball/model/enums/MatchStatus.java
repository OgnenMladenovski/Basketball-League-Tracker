package mk.ukim.finki.basketball.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MatchStatus {

    SCHEDULED("Scheduled"),
    PLAYED("Played");

    private final String displayName;
}
