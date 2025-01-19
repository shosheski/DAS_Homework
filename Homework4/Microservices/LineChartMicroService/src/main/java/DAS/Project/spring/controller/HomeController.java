package DAS.Project.spring.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class HomeController {

    @GetMapping
    String home(Model model) {
        model.addAttribute("title", "DAS Home Page");
        model.addAttribute("card1Url", "/table");
        return "home";
    }
}