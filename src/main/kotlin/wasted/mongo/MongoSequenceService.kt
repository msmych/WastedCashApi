package wasted.mongo

import org.springframework.data.mongodb.core.FindAndModifyOptions.options
import org.springframework.data.mongodb.core.MongoOperations
import org.springframework.data.mongodb.core.query.Criteria.where
import org.springframework.data.mongodb.core.query.Query.query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.stereotype.Service

@Service
class MongoSequenceService(val mongo: MongoOperations) {

    fun next(id: String): Long {
        return (mongo.findAndModify(
                query(where("_id").`is`(id)),
                Update().inc("sequence", 1),
                options().returnNew(true).upsert(true),
                MongoSequence::class.java)
                ?: MongoSequence(id, 1))
                .sequence
    }
}