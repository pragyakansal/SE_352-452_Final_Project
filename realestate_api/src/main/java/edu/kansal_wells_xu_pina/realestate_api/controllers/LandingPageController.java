package edu.kansal_wells_xu_pina.realestate_api.controllers;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.Controller;

@Controller
public class LandingPageController {
    @GetMapping("/")
    public String landingPage() {
        return "landing-page";
    }
}
