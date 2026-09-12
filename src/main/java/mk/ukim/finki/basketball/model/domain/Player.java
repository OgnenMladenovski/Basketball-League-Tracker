package mk.ukim.finki.basketball.model.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mk.ukim.finki.basketball.model.enums.Position;
import java.time.LocalDate;
import java.time.Period;

@Entity
@Table(name = "players")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Player extends BaseEntity {

    @Column(nullable = false, length = 60)
    private String firstName;

    @Column(nullable = false, length = 60)
    private String lastName;

    @Column(nullable = false)
    private Integer jerseyNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 5)
    private Position position;

    private Integer heightCm;

    @Column(nullable = false)
    private LocalDate birthDate;

    @ManyToOne
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public int getAge() {
        if (birthDate == null) {
            return 0;
        } else {
            return Period.between(birthDate, LocalDate.now()).getYears();
        }
    }
}
