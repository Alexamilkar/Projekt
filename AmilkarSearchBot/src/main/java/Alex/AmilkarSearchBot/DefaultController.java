package Alex.AmilkarSearchBot;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class DefaultController {
    @RequestMapping("/admin")
    public String index() {
        return "index";
    }

}
