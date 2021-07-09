package wee.digital.sample.repository.model

data class RegisterData(
    var name: String = "",
    var email: String = "",
    var password: String = "",
    var urlAvatar : String = "",
    var face: ByteArray? = null
)

data class UserData(var name: String, var url: String)