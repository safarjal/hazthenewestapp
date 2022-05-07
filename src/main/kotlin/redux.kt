import redux.RAction
import redux.combineReducers

data class MyStore(
    var lang: String = languageSelector.value,
    var masla: String = Vls.Maslas.MUTADA,
    var type: String = Vls.Types.DATE_ONLY
)

object InputState {
    var lang: String = languageSelector.value
    var masla: String = Vls.Maslas.MUTADA
    var type: String = Vls.Types.DATE_ONLY
}

class MaslaChange(val value: String): RAction
fun updateMasla(state: String, action: RAction): String = when (action) {
    is MaslaChange -> action.value
    else -> state
}

class TypeChange(val value: String): RAction
fun updateType(state: String, action: RAction): String = when (action) {
    is TypeChange -> action.value
    else -> state
}

fun combinedReducers() = combineReducers<InputState, RAction>(
    mapOf(
        "updateMasla" to ::updateMasla,
        "updateType" to ::updateType
    )
)

// You can use subscribe() to update the UI in response to state changes.
// Normally you'd use an additional layer or view binding library rather than subscribe() directly.

//val unsubscribe = store.subscribe { logger.debug(store.state)}

// The only way to mutate the internal state is to dispatch an action.
// The actions can be serialized, logged or stored.
//store.dispatch(Increment())
// Current State: 1
//store.dispatch(Increment())
// Current State: 2
//store.dispatch(Decrement())
// Current State: 1

//Removes the reference to the subscription functions.
//Must be called when subscription is no longer needed to avoid a
//memory leak.
//unsubscribe()