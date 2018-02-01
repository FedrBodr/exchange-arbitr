package ru.fedrbodr.exchangearbitr.view;

import org.springframework.stereotype.Controller;

@Controller
public class GreetingController {

    public String getMessage() {
        return "Hello Crypto World!!!";
    }
}
