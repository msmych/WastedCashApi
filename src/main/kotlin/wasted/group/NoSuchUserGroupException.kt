package wasted.group

class NoSuchUserGroupException(private val id: Long) : IllegalArgumentException() {

  override val message: String?
    get() = "User group $id not found"
}
