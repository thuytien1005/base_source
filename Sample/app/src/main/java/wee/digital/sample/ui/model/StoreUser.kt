package wee.digital.sample.ui.model

import androidx.recyclerview.widget.DiffUtil
import wee.digital.library.extension.str
import wee.digital.widget.extension.normalizer

class StoreUser : ObjectMapper {

    var uid: String = ""

    var firstName: String = ""

    var lastName: String? = null

    var email: String? = null

    var photoDisplay: String? = null

    var searchKey: String = ""

    fun fullName(): String = "%s %s".format(firstName, lastName.toString()).trim()

    override fun equals(other: Any?): Boolean {
        return uid === (other as? StoreUser)?.uid
    }

    override fun toMap(): Map<String, Any?> {
        return mapOf(
            "uid" to uid,
            "firstName" to firstName,
            "lastName" to lastName,
            "email" to email,
            "photoDisplay" to photoDisplay,
            "searchKey" to ("%s %s".format(firstName, lastName.toString()).normalizer() ?: ""),
        )
    }

    companion object {

        val itemDiffer
            get() = object : DiffUtil.ItemCallback<StoreUser>() {
                override fun areItemsTheSame(oldItem: StoreUser, newItem: StoreUser): Boolean {
                    return oldItem.uid === newItem.uid
                }

                override fun areContentsTheSame(oldItem: StoreUser, newItem: StoreUser): Boolean {
                    return oldItem.uid == newItem.uid
                }

            }

        fun fromMap(m: Map<String, Any>): StoreUser {
            return StoreUser().also {
                it.uid = m.str("uid")
                it.firstName = m.str("firstName")
                it.lastName = m.str("lastName")
                it.email = m.str("email")
                it.photoDisplay = m.str("photoDisplay")
                it.searchKey = m.str("searchKey")
            }
        }

    }
}


