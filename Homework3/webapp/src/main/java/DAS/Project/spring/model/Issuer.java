package DAS.Project.spring.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Issuer {

    private String name;
    private String code;
    private List<Akcija> akcii;

    public Issuer(String name, String code) {
        this.name = name;
        this.code = code;
        this.akcii = new ArrayList<>();
    }
}