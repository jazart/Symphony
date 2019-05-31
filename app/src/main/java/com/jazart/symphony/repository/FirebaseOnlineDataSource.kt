import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
import com.jazart.symphony.Constants
import com.jazart.symphony.model.User
import com.jazart.symphony.repository.await

class FirebaseOnlineDataSource : UserRepository {

    private val db: FirebaseFirestore
        @Synchronized
        get() {
            FirebaseFirestore.getInstance().enableNetwork()
            return FirebaseFirestore.getInstance()
        }

    override suspend fun getUserById(id: String): User? {
        return db.collection(Constants.USERS).document(id).get(Source.SERVER).await().toObject(User::class.java)
    }

    override suspend fun getUserFriends(id: String): List<User> {
        return getUserById(id)?.friends?.mapNotNull { friendId -> getUserById(friendId) } ?: emptyList()
    }
}
