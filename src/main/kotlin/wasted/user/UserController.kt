package wasted.user

import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("user")
class UserController(val userRepository: UserRepository) {

    @GetMapping("{id}/exists")
    fun existsUser(@PathVariable id: Int): Boolean {
        return userRepository.existsById(id)
    }

    @PostMapping("{id}")
    fun createUser(@PathVariable id: Int) {
        if (userRepository.findById(id).isPresent)
            throw IllegalArgumentException("User $id already exists")
        userRepository.save(User(id, arrayListOf("USD", "EUR", "RUB")))
    }

    @GetMapping("{id}/currencies")
    fun getUserCurrencies(@PathVariable id: Int): List<String> {
        return userRepository.findById(id)
                .map { it.currencies }
                .orElseThrow { NoSuchUserException(id) }
    }

    @PatchMapping("{id}/currency/{currency}")
    fun addUserCurrency(@PathVariable id: Int, @PathVariable currency: String): List<String> {
        val user = userRepository.findById(id)
                .orElseThrow { NoSuchUserException(id) }
        val currencyUpperCase = currency.toUpperCase()
        if (user.currencies.contains(currencyUpperCase))
            user.currencies.remove(currencyUpperCase)
        else
            user.currencies.add(currencyUpperCase)
        return user.currencies
    }
}