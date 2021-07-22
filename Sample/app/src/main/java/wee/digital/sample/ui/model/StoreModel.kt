package wee.digital.sample.ui.model

import com.google.firebase.firestore.DocumentSnapshot

fun requireNotEmpty(value: String?): String {
    if (value.isNullOrEmpty()) throw NullPointerException()
    return value
}

interface ObjectMapper {

    fun toMap(): Map<String, Any?>

}

fun <T> List<DocumentSnapshot>?.toDataList(block: (Map<String, Any>) -> T?): List<T> {
    this ?: return listOf()
    val list = mutableListOf<T>()
    this.forEach { doc: DocumentSnapshot ->
        doc.data?.also { map: Map<String, Any> ->
            block(map)?.also { t: T ->
                list.add(t)
            }

        }
    }
    return list
}

fun <T : ObjectMapper> List<T>?.toMapList(): List<Map<String, Any?>> {
    val mapList = mutableListOf<Map<String, Any?>>()
    this?.forEach {
        mapList.add(it.toMap())
    }
    return mapList
}
