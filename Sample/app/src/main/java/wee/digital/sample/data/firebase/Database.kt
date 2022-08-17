package wee.digital.sample.data.firebase

import android.annotation.SuppressLint
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

val dbRef: DatabaseReference get() = Firebase.database.reference

private val eventMap = mutableMapOf<DatabaseReference, EventListener?>()

abstract class EventListener(var eventId: String? = null) : ValueEventListener {
    final override fun onDataChange(snapshot: DataSnapshot) {
        try {
            onDataChanged(snapshot)
        } catch (e: Exception) {
            onError(null, e)
        }
    }

    final override fun onCancelled(error: DatabaseError) {
        onError(error, null)
    }

    abstract fun onDataChanged(snapshot: DataSnapshot)

    open fun onError(error: DatabaseError?, e: Exception?) {
        eventMap.entries.forEach {
            if (it.value == this) {
                eventMap[it.key] = null
            }
        }
        Firebase.d("EventListener onError: ${error?.message ?: e?.message ?: "none"}")
    }
}

@SuppressLint("RestrictedApi")
fun DatabaseReference?.setEventListener(listener: EventListener?) {

    this ?: return

    val entry = eventMap.entries.find { it.key.path == this.path }
    val existListener: EventListener? = entry?.value
    if (existListener != null && existListener.eventId == listener?.eventId) {
        return
    }
    eventMap[this] = listener
    if (listener == null) {
        eventMap[this]?.also { this.removeEventListener(it) }
        eventMap[this] = null
        return
    }
    if (listener.eventId == null){
        listener.eventId = this.path.toString()
    }
    this.addValueEventListener(listener)
}

fun removeFirebaseEventListeners() {
    eventMap.entries.forEach {
        it.value?.also { listener ->
            it.key.removeEventListener(listener)
            eventMap[it.key] = null
        }
    }
}