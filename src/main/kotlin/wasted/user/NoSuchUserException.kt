package wasted.user

class NoSuchUserException(private val id: Int) : IllegalArgumentException() {

    override val message: String?
        get() = "User $id not found"
}
