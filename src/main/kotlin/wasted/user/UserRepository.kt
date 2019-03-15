package wasted.user

import org.springframework.data.mongodb.repository.MongoRepository

interface UserRepository : MongoRepository<User, Int>