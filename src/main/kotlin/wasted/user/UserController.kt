package wasted.user

import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("user")
class UserController(val userRepository: UserRepository) {

    @GetMapping("{id}/currencies")
    fun getUserCurrencies(@PathVariable id: Int): Set<String> {
        return userRepository.findById(id)
                .map { it.currencies }
                .orElseThrow { NoSuchUserException(id) }
    }

    @PostMapping("{id}/currency/{currency}")
    fun addUserCurrency(@PathVariable id: Int, @PathVariable currency: String): Set<String> {
        val user = userRepository.findById(id)
                .orElseThrow { NoSuchUserException(id) }
        user.currencies.add(currency.toUpperCase())
        userRepository.save(user)
        return user.currencies
    }
}