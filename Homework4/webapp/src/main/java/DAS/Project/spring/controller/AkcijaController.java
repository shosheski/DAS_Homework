package DAS.Project.spring.controller;

import DAS.Project.spring.model.*;
import DAS.Project.spring.services.DropdownMenu;
import DAS.Project.spring.services.AkcijaAnalyzer;
import DAS.Project.spring.services.CSV_Processor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/stocks")
@CrossOrigin(origins = "http://localhost:8080")
public class AkcijaController {

    private final DropdownMenu dropdownMenu = DropdownMenu.getInstance();

    public int getYear(String date){
        return Integer.parseInt(date.split("\\.")[2]);
    }

    @GetMapping("/issuers")
    public List<String> getCompanies() {
        String url = "https://www.mse.mk/mk/stats/symbolhistory/kmb";
        return dropdownMenu.extractDropdownOptions(url);
    }

    @GetMapping("/table/stats")
    public Map<String, Object> getStats(@RequestParam(defaultValue = "ADIN") String companySelected,
                                        @RequestParam(defaultValue = "2014") Integer fromYear,
                                        @RequestParam(defaultValue = "2024") Integer toYear) {
        try {
            List<AkcijaData> stockData = CSV_Processor.loadCSV("./database/" + companySelected + ".csv");
            Map<String, Object> response = new HashMap<>();

            response.put("data", stockData.get(0));

            return response;
        } catch (Exception e) {
            throw new RuntimeException("Error processing CSV file", e);
        }
    }

    @GetMapping("/table/lineChart")
    public Map<String, Object> getCompanyData(@RequestParam(defaultValue = "ADIN") String companySelected,
                                              @RequestParam(defaultValue = "2014") Integer fromYear,
                                              @RequestParam(defaultValue = "2024") Integer toYear) {
        try {
            List<AkcijaData> stockData = CSV_Processor.loadCSV("./database/" + companySelected + ".csv");

            List<Double> prices = stockData.stream().filter(sd -> getYear(sd.date) >= fromYear && getYear(sd.date) <= toYear).map(d -> d.closePrice).toList();
            List<Double> sma10 = AkcijaAnalyzer.calculateSMA(prices, 10);
            List<Double> rsi14 = AkcijaAnalyzer.calculateRSI(prices, 14);

            List<String> signals = new ArrayList<>();
            for (int i = 0; i < prices.size(); i++) {
                double sma = sma10.get(i);
                double rsi = rsi14.get(i);
                signals.add(AkcijaAnalyzer.generateSignal(rsi, sma, prices.get(i)));
            }

            Map<String, Object> response = new HashMap<>();
            response.put("dates", stockData.stream().filter(sd -> getYear(sd.date) >= fromYear && getYear(sd.date) <= toYear).map(d -> d.date).toList());
            response.put("prices", prices);
            response.put("sma", sma10);
            response.put("rsi", rsi14);
            response.put("signals", signals);

            return response;
        } catch (Exception e) {
            throw new RuntimeException("Error processing CSV file", e);
        }
    }

    @GetMapping("/table/statistics")
    public Map<String, Object> getStatsData(@RequestParam(defaultValue = "ADIN") String companySelected,
                                            @RequestParam(defaultValue = "2014") Integer fromYear,
                                            @RequestParam(defaultValue = "2024") Integer toYear) {
        try {
            List<AkcijaData> stockData = CSV_Processor.loadCSV("./database/" + companySelected + ".csv");
            List<AkcijaData> filteredData = stockData.stream()
                    .filter(sd -> getYear(sd.date) >= fromYear && getYear(sd.date) <= toYear)
                    .filter(sd -> sd.high != null)
                    .collect(Collectors.toList());

            List<Double> prices = filteredData.stream().map(d -> d.closePrice).toList();

            List<Double> sma10 = AkcijaAnalyzer.calculateSMA(prices, 10);
            List<Double> sma20 = AkcijaAnalyzer.calculateSMA(prices, 20);
            List<Double> ema10 = AkcijaAnalyzer.calculateEMA(prices, 10);
            List<Double> ema20 = AkcijaAnalyzer.calculateEMA(prices, 20);
            List<Double> wma = AkcijaAnalyzer.calculateWMA(prices, 14);

            List<Double> rsiDaily = AkcijaAnalyzer.calculateRSI(prices, 14);

            List<AkcijaData> weeklyData = AkcijaAnalyzer.aggregateByWeek(filteredData);
            List<Double> rsiWeekly = AkcijaAnalyzer.calculateRSI(weeklyData.stream().map(d -> d.closePrice).collect(Collectors.toList()), 14);

            List<AkcijaData> monthlyData = AkcijaAnalyzer.aggregateByMonth(filteredData);
            List<Double> rsiMonthly = AkcijaAnalyzer.calculateRSI(monthlyData.stream().map(d -> d.closePrice).collect(Collectors.toList()), 14);

            List<Double> stochastic = AkcijaAnalyzer.calculateStochasticOscillator(prices);
            List<Double> macd = AkcijaAnalyzer.calculateMACD(prices);
            List<Double> adx = AkcijaAnalyzer.calculateADX(filteredData);
            List<Double> cci = AkcijaAnalyzer.calculateCCI(filteredData);

            List<String> signals = new ArrayList<>();
            for (int i = 0; i < prices.size(); i++) {
                signals.add(AkcijaAnalyzer.generateSignal(rsiDaily.get(i), sma10.get(i), prices.get(i)));
            }

            Map<String, Object> response = new HashMap<>();
            response.put("dates", filteredData.stream().map(d -> d.date).toList());
            response.put("prices", prices);
            response.put("sma", Map.of("sma10", sma10, "sma20", sma20, "ema10", ema10, "ema20", ema20, "wma", wma));
            response.put("oscillators", Map.of("rsi", rsiDaily, "stochastic", stochastic, "macd", macd, "adx", adx, "cci", cci, "rsiDaily", rsiDaily, "rsiWeekly", rsiWeekly, "rsiMonthly", rsiMonthly));
            response.put("signals", signals);

            return response;
        } catch (Exception e) {
            throw new RuntimeException("Error processing CSV file", e);
        }
    }


}