package wasted.help

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("help")
class HelpController {

    @GetMapping("")
    fun help(): String {
        return "Wasted cash help"
    }
}