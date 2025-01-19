package DAS.Project.spring.services;

import DAS.Project.spring.model.AkcijaData;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.*;

@Service
public class AkcijaAnalyzer {

    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    public static List<Double> calculateSMA(List<Double> prices, int period) {
        List<Double> smaValues = new ArrayList<>();
        for (int i = 0; i < prices.size(); i++) {
            if (i < period - 1) {
                smaValues.add(Double.NaN);
            } else {
                double sum = 0.0;
                for (int j = 0; j < period; j++) {
                    sum += prices.get(i - j);
                }
                smaValues.add(sum / period);
            }
        }
        return smaValues;
    }


    public static List<Double> calculateRSI(List<Double> prices, int period) {
        List<Double> rsiValues = new ArrayList<>();
        for (int i = 0; i < prices.size(); i++) {
            if (i < period) {
                rsiValues.add(Double.NaN);
            } else {
                double gain = 0.0, loss = 0.0;
                for (int j = 0; j < period; j++) {
                    double change = prices.get(i - j) - prices.get(i - j - 1);
                    if (change > 0) gain += change;
                    else loss -= change;
                }
                double rs = gain / loss;
                rsiValues.add(100 - (100 / (1 + rs)));
            }
        }
        return rsiValues;
    }

    public static String generateSignal(double rsi, double sma, double price) {
        if (rsi < 30 && price < sma) {
            return "Buy";
        } else if (rsi > 70 && price > sma) {
            return "Sell";
        } else {
            return "Hold";
        }
    }

    public static List<Double> calculateWMA(List<Double> prices, int period) {
        List<Double> wma = new ArrayList<>();

        for (int i = 0; i < prices.size(); i++) {
            if (i < period - 1) {
                wma.add(null);
            } else {
                double sum = 0;
                int weightSum = 0;
                for (int j = 0; j < period; j++) {
                    sum += prices.get(i - j) * (period - j);
                    weightSum += (period - j);
                }
                wma.add(sum / weightSum);
            }
        }
        return wma;
    }


    public static List<Double> calculateEMA(List<Double> prices, int period) {
        List<Double> ema = new ArrayList<>();
        double multiplier = 2.0 / (period + 1);
        double previousEma = 0;

        for (int i = 0; i < prices.size(); i++) {
            if (i < period - 1) {
                ema.add(null);
            } else if (i == period - 1) {
                double sum = 0;
                for (int j = 0; j < period; j++) {
                    sum += prices.get(j);
                }
                previousEma = sum / period;
                ema.add(previousEma);
            } else {
                double currentEma = (prices.get(i) - previousEma) * multiplier + previousEma;
                ema.add(currentEma);
                previousEma = currentEma;
            }
        }
        return ema;
    }

    public static List<Double> calculateADX(List<AkcijaData> stockData) {
        int period = 14;
        List<Double> adx = new ArrayList<>();
        List<Double> trueRange = new ArrayList<>();
        List<Double> plusDM = new ArrayList<>();
        List<Double> minusDM = new ArrayList<>();
        List<Double> diPlus = new ArrayList<>();
        List<Double> diMinus = new ArrayList<>();
        List<Double> dx = new ArrayList<>();

        for (int i = 1; i < stockData.size(); i++) {
            double highDiff = stockData.get(i).high - stockData.get(i - 1).high;
            double lowDiff = stockData.get(i - 1).low - stockData.get(i).low;
            trueRange.add(Math.max(stockData.get(i).high - stockData.get(i).low,
                    Math.max(Math.abs(stockData.get(i).high - stockData.get(i - 1).closePrice),
                            Math.abs(stockData.get(i).low - stockData.get(i - 1).closePrice))));
            plusDM.add(highDiff > lowDiff && highDiff > 0 ? highDiff : 0);
            minusDM.add(lowDiff > highDiff && lowDiff > 0 ? lowDiff : 0);
        }

        for (int i = 0; i < trueRange.size(); i++) {
            if (i < period - 1) {
                diPlus.add(null);
                diMinus.add(null);
                dx.add(0.0);
            } else {
                double trSum = trueRange.subList(i - period + 1, i + 1).stream().mapToDouble(Double::doubleValue).sum();
                double plusDMSum = plusDM.subList(i - period + 1, i + 1).stream().mapToDouble(Double::doubleValue).sum();
                double minusDMSum = minusDM.subList(i - period + 1, i + 1).stream().mapToDouble(Double::doubleValue).sum();

                diPlus.add((plusDMSum / trSum) * 100);
                diMinus.add((minusDMSum / trSum) * 100);

                double diff = Math.abs(diPlus.get(i) - diMinus.get(i));
                double sum = diPlus.get(i) + diMinus.get(i);
                dx.add((diff / sum) * 100);
            }
        }

        for (int i = 0; i < dx.size(); i++) {
            if (i < period - 1) {
                adx.add(0.0);
            } else {
                adx.add(dx.subList(i - period + 1, i + 1).stream().mapToDouble(Double::doubleValue).average().orElse(0.0));
            }
        }
        return adx;
    }

    public static List<Double> calculateCCI(List<AkcijaData> stockData) {
        int period = 20;
        List<Double> cci = new ArrayList<>();
        List<Double> typicalPrices = new ArrayList<>();

        for (AkcijaData data : stockData) {
            double typicalPrice = (data.high + data.low + data.closePrice) / 3;
            typicalPrices.add(typicalPrice);
        }

        for (int i = 0; i < typicalPrices.size(); i++) {
            if (i < period - 1) {
                cci.add(null);
            } else {
                double avgTypicalPrice = typicalPrices.subList(i - period + 1, i + 1).stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
                double meanDeviation = typicalPrices.subList(i - period + 1, i + 1).stream()
                        .mapToDouble(tp -> Math.abs(tp - avgTypicalPrice)).sum() / period;
                double currentCci = (typicalPrices.get(i) - avgTypicalPrice) / (0.015 * meanDeviation);
                cci.add(currentCci);
            }
        }
        return cci;
    }


    public static List<Double> calculateMACD(List<Double> prices) {
        List<Double> ema12 = calculateEMA(prices, 12);
        List<Double> ema26 = calculateEMA(prices, 26);
        List<Double> macd = new ArrayList<>();

        for (int i = 0; i < prices.size(); i++) {
            if (ema12.get(i) == null || ema26.get(i) == null) {
                macd.add(null);
            } else {
                macd.add(ema12.get(i) - ema26.get(i));
            }
        }
        return macd;
    }


    public static List<Double> calculateStochasticOscillator(List<Double> prices) {
        int period = 14;
        List<Double> stochastic = new ArrayList<>();

        for (int i = 0; i < prices.size(); i++) {
            if (i < period - 1) {
                stochastic.add(null);
            } else {
                double high = Collections.max(prices.subList(i - period + 1, i + 1));
                double low = Collections.min(prices.subList(i - period + 1, i + 1));
                double k = ((prices.get(i) - low) / (high - low)) * 100;
                stochastic.add(k);
            }
        }
        return stochastic;
    }


    public static List<AkcijaData> aggregateByWeek(List<AkcijaData> dailyData) {
        Map<String, List<AkcijaData>> weeklyData = new HashMap<>();

        for (AkcijaData data : dailyData) {
            LocalDate date = LocalDate.parse(data.date, dateFormatter);
            String weekKey = date.getYear() + "-" + date.get(WeekFields.ISO.weekOfYear());
            weeklyData.computeIfAbsent(weekKey, k -> new ArrayList<>()).add(data);
        }

        return getAkcijaData(weeklyData);
    }

    public static List<AkcijaData> aggregateByMonth(List<AkcijaData> dailyData) {
        Map<String, List<AkcijaData>> monthlyData = new HashMap<>();

        for (AkcijaData data : dailyData) {
            LocalDate date = LocalDate.parse(data.date, dateFormatter);
            String monthKey = date.getYear() + "-" + date.getMonthValue();
            monthlyData.computeIfAbsent(monthKey, k -> new ArrayList<>()).add(data);
        }

        return getAkcijaData(monthlyData);
    }

    private static List<AkcijaData> getAkcijaData(Map<String, List<AkcijaData>> timeFrameData) {
        List<AkcijaData> aggregatedData = new ArrayList<>();

        for (Map.Entry<String, List<AkcijaData>> entry : timeFrameData.entrySet()) {
            List<AkcijaData> periodData = entry.getValue();

            if (!periodData.isEmpty()) {
                double avgClosePrice = periodData.stream().mapToDouble(sd -> sd.closePrice).average().orElse(0.0);
                double avgHigh = periodData.stream().mapToDouble(sd -> sd.high).average().orElse(0.0);
                double avgLow = periodData.stream().mapToDouble(sd -> sd.low).average().orElse(0.0);
                double avgPrice = periodData.stream().mapToDouble(sd -> sd.avgPrice).average().orElse(0.0);
                double avgVolume = periodData.stream().mapToDouble(sd -> sd.volume).average().orElse(0.0);

                String date = periodData.get(0).date;

                AkcijaData aggregate = new AkcijaData(date, avgClosePrice, avgHigh, avgLow, avgPrice, avgVolume);
                aggregatedData.add(aggregate);
            }
        }

        return aggregatedData;
    }

}