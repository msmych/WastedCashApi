package wasted.user

import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("user")
class UserController(val userRepository: UserRepository) {

    @PostMapping("{id}")
    fun createUser(@PathVariable id: Int) {
        if (userRepository.findById(id).isPresent)
            throw IllegalArgumentException("User $id already exists")
        userRepository.save(User(id, mutableListOf("USD", "EUR", "RUB")))
    }

    @GetMapping("{id}/currencies")
    fun getUserCurrencies(@PathVariable id: Int): List<String> {
        return userRepository.findById(id)
                .map { it.currencies }
                .orElseThrow { NoSuchUserException(id) }
    }

    @PostMapping("{id}/currency/{currency}")
    fun addUserCurrency(@PathVariable id: Int, @PathVariable currency: String): List<String> {
        val user = userRepository.findById(id)
                .orElseThrow { NoSuchUserException(id) }
        user.currencies.add(currency.toUpperCase())
        userRepository.save(user)
        return user.currencies
    }
}