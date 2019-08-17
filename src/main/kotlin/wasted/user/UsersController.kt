package wasted.user

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("users")
class UsersController(private val userRepository: UserRepository) {

  @GetMapping("whats-new/ids")
  fun usersSubscribedToWhatsNewIds(): List<Int> {
    return userRepository.findAllByWhatsNewTrue().map { it.id }
  }
}
