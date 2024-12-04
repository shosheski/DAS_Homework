package DAS.Project.spring.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Data
@Entity
@Table(name = "objects")
@NoArgsConstructor
@AllArgsConstructor
public class Object implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty("id")
    private Long id;

    @Column(nullable = false)
    @JsonProperty("name")
    private String name;

    @JsonProperty("promet")
    private double promet;

    public void setId(Long id) {
        this.id = id;
    }
}
