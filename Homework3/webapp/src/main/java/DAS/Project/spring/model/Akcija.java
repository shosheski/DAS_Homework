package DAS.Project.spring.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class Akcija {

    private LocalDateTime date;
    private long priceOfLastTransaction;
    private long maxPrice;
    private long minPrice;
    private long averagePrice;
    private long prom;
    private long quantity;
    private long BESTturnoverInDenars;
    private long totalTurnoverInDenars;
}