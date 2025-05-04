package fpt.g36.gapms.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RootController {

    @GetMapping("/")
    public String redirectToHomePage() {
        return "home-page/home-page";
    }
}