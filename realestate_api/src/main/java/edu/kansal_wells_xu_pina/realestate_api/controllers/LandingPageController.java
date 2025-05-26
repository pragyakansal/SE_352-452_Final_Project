package edu.kansal_wells_xu_pina.realestate_api.controllers;

@Controller
public class LandingPageController {
    @GetMapping("/")
    public String landingPage() {
        return "landing-page";
    }
}
