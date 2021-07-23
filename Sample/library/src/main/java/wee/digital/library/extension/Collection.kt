package wee.digital.library.extension

import java.text.Normalizer
import java.util.regex.Pattern

fun <T> Collection<T>?.notNullOrEmpty(): Boolean {
    return !this.isNullOrEmpty()
}

fun <T> List<T>?.join(collection: Collection<T>?): List<T>? {
    val list = mutableListOf<T>()
    if (this.notNullOrEmpty()) {
        list.addAll(this!!)
    }
    if (collection.notNullOrEmpty()) {
        list.addAll(collection!!)
    }
    return if (list.isEmpty()) return null else list
}

fun <T,  R> Collection<T>.transform(block: (T) -> R?): List<R> {
    val list = mutableListOf<R>()
    for (item in this) {
        block(item)?.also {
            list.add(it)
        }
    }
    return list
}

fun <T> Collection<T?>?.filters(block: (T) -> T?): MutableList<T>? {
    this ?: return null
    val list = mutableListOf<T>()
    for (item in this) {
        item ?: continue
        val filterItem = block(item) ?: continue
        list.add(filterItem)
    }
    return list
}

fun <T> Collection<T>?.search(s: String?): Collection<T>? {
    if (s.isNullOrEmpty() || this.isNullOrEmpty()) return this
    val s1 = s.normalizer() ?: return this
    val searchResults = mutableListOf<T>()
    for (model in this) {
        val s2 = model?.toString()?.normalizer() ?: continue
        if (s2.contains(s1, true)) searchResults.add(model)
    }
    return searchResults
}

private fun String?.normalizer(): String? {
    if (this.isNullOrEmpty()) return null
    return try {
        val temp = Normalizer.normalize(this, Normalizer.Form.NFD)
        val pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+")
        pattern.matcher(temp)
                .replaceAll("")
                .toLowerCase()
                .replace(" ", "-")
                .replace("đ", "d", true)

    } catch (e: IllegalStateException) {
        null
    } catch (e: IllegalArgumentException) {
        null
    }
}

