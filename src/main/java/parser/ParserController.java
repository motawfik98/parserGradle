package parser;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
public class ParserController {

    private Parser parser = null;

    @RequestMapping("/")
    public ModelAndView showPage() {
        TinyScanner tinyScanner = new TinyScanner();
        parser = new Parser(tinyScanner.getTokenList());

        return new ModelAndView("index");
    }

    @RequestMapping(value = "/getParseTree", method = RequestMethod.GET)
    public Node showTree() {
        return parser.getRoot();
    }
}
