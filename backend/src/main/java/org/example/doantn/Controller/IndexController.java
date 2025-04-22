package org.example.doantn.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {

    @GetMapping("/index")
    public String showIndex() {
        return "index"; // Trả về file students.html trong src/main/resources/templates
    }
}
