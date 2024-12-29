package DAS.Project.spring.model;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DropdownMenu {

    public static List<String> extractDropdownOptions(String url) {
        List<String> dates = new ArrayList<>();

        try {
            // Fetch the HTML content of the webpage
            Document document = Jsoup.connect(url).get();

            // Select the dropdown element by its ID
            Element dropdown = document.selectFirst("#Code");

            if (dropdown != null) {
                // Get all options within the dropdown
                Elements options = dropdown.select("option");

                // Filter out options containing digits
                for (Element option : options) {
                    String optionText = option.text();
                    if (!optionText.matches(".*\\d.*")) { // Regex to check if text contains digits
                        dates.add(optionText);
                    }
                }
            } else {
                System.out.println("Dropdown with ID 'Code' not found.");
            }

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Failed to fetch the webpage.");
        }

        return dates;
    }
}