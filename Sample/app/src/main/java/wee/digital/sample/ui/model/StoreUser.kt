package wee.digital.sample.ui.model

import androidx.recyclerview.widget.DiffUtil
import com.google.firebase.firestore.DocumentSnapshot
import wee.digital.library.extension.MapValueNullException
import wee.digital.library.extension.strOrNull
import wee.digital.library.extension.strOrThrow

class StoreUser {

    var uid: String = ""
        get() = requireNotEmpty(field)

    var firstName: String = ""
        get() = requireNotEmpty(field)

    var lastName: String? = null

    var email: String = ""
        get() = requireNotEmpty(field)

    var photoDisplay: String? = null

    var searchKey: String = ""

    override fun equals(other: Any?): Boolean {
        return uid === (other as? StoreUser)?.uid
    }

    companion object {

        private fun requireNotEmpty(value: String?): String {
            if (value.isNullOrEmpty()) throw NullPointerException()
            return value
        }

        val itemDiffer
            get() = object : DiffUtil.ItemCallback<StoreUser>() {
                override fun areItemsTheSame(oldItem: StoreUser, newItem: StoreUser): Boolean {
                    return oldItem.uid === newItem.uid
                }

                override fun areContentsTheSame(oldItem: StoreUser, newItem: StoreUser): Boolean {
                    return oldItem.uid == newItem.uid
                }

            }

        fun from(map: Map<String, Any>): StoreUser {
            return StoreUser().apply {
                uid = map.strOrThrow("uid")
                email = map.strOrThrow("email")
                firstName = map.strOrThrow("firstName")
                lastName = map.strOrNull("lastName")
                photoDisplay = map.strOrNull("photoDisplay")
            }
        }

        fun from(map: DocumentSnapshot): StoreUser {
            return StoreUser().apply {
                uid = map.get("uid")?.toString() ?: throw MapValueNullException("uid")
                email = map.get("email")?.toString()?: throw MapValueNullException("email")
                firstName = map.get("firstName")?.toString()?: throw MapValueNullException("firstName")
                lastName = map.get("lastName")?.toString()
                photoDisplay = map.get("photoDisplay")?.toString()
            }
        }

    }
}

val StoreUser.fullName: String get() = "%s %s".format(firstName, lastName.toString()).trim()
