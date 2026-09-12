package mk.ukim.finki.basketball.model.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "leagues")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class League extends BaseEntity {

    @Column(nullable = false, unique = true, length = 80)
    private String name;

    @Column(nullable = false, length = 20)
    private String season;

    @Column(nullable = false)
    private Integer winPoints;

    @Column(nullable = false)
    private Integer lossPoints;

    @Column(length = 7)
    private String color;

    @Column(length = 7)
    private String accentColor;
}
