package wee.digital.sample.ui.model

fun requireNotEmpty(value: String?): String {
    if (value.isNullOrEmpty()) throw NullPointerException()
    return value
}

interface ToMap {

    fun toMap(): Map<String, Any?>
}

fun <T : ToMap> listMap(list: List<T>?): List<Map<String, Any?>> {
    val mapList = mutableListOf<Map<String, Any?>>()
    list?.forEach {
        mapList.add(it.toMap())
    }
    return mapList
}
