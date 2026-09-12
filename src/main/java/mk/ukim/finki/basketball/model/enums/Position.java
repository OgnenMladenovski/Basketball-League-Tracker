package mk.ukim.finki.basketball.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Position {

    PG("PG", "Point guard"),
    SG("SG", "Shooting guard"),
    SF("SF", "Small forward"),
    PF("PF", "Power forward"),
    C("C", "Center");

    private final String label;
    private final String displayName;
}
