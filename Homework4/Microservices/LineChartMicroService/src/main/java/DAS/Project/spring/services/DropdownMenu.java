package DAS.Project.spring.services;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class DropdownMenu {

    private static DropdownMenu instance;

    private DropdownMenu() {

    }

    public static DropdownMenu getInstance() {
        if (instance == null) {
            synchronized (DropdownMenu.class) {
                if (instance == null) {
                    instance = new DropdownMenu();
                }
            }
        }
        return instance;
    }

    public static List<String> extractDropdownOptions(String url) {
        List<String> dates = new ArrayList<>();

        try {
            Document document = Jsoup.connect(url).get();

            Element dropdown = document.selectFirst("#Code");

            if (dropdown != null) {
                Elements options = dropdown.select("option");

                for (Element option : options) {
                    String optionText = option.text();
                    if (!optionText.matches(".*\\d.*")) {
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