package DAS.Project.spring.services;

import DAS.Project.spring.model.AkcijaData;
import com.opencsv.*;
import org.springframework.stereotype.Service;

import java.io.*;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.*;

@Service
public class CSV_Processor {

    public static List<AkcijaData> loadCSV(String filePath) throws Exception {
        List<AkcijaData> stockData = new ArrayList<>();
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.getDefault());
        symbols.setDecimalSeparator(',');
        symbols.setGroupingSeparator('.');

        DecimalFormat decimalFormat = new DecimalFormat("#,##0.00", symbols);

        try (CSVReader reader = new CSVReader(new FileReader(filePath))) {
            String[] line;
            reader.readNext();

            while ((line = reader.readNext()) != null) {
                try {
                    String date = line[0];
                    double closePrice = parseValue(line[1], decimalFormat);
                    Double high = parseOptionalValue(line[2], decimalFormat);
                    Double low = parseOptionalValue(line[3], decimalFormat);
                    double avgPrice = parseValue(line[4], decimalFormat);
                    double volume = parseDouble(line[6]);

                    stockData.add(new AkcijaData(date, closePrice, high, low, avgPrice, volume));
                } catch (Exception e) {
                    System.err.println("Skipping invalid row: " + Arrays.toString(line));
                }
            }
        }
        return stockData;
    }

    private static double parseValue(String value, DecimalFormat decimalFormat) throws Exception {
        if (value == null || value.trim().isEmpty()) {
            throw new Exception("Missing required value");
        }
        return decimalFormat.parse(value.replace("\"", "")).doubleValue();
    }

    private static Double parseOptionalValue(String value, DecimalFormat decimalFormat) throws Exception {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        return decimalFormat.parse(value.replace("\"", "")).doubleValue();
    }

    private static double parseDouble(String value) {
        if (value == null || value.trim().isEmpty()) {
            return 0.0;
        }
        return Double.parseDouble(value);
    }
}