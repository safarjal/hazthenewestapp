import redux.RAction
import redux.combineReducers

//fun reducer(state: Int, action: Action) =
//    when (action) {
//        is Increment -> state + 1
//        is Decrement -> state - 1
//        else -> state
//    }

/**
 * Actions are plain objects that represent an action in the app. These can be
 * plain objects or data classes and have fields that hold data necessary for
 * the reducer to update the state.
 */
//class Increment
//class Decrement

// Create a Redux store holding the state of your app.
// 0 is the initial state
//val store = createThreadSafeStore(reducer, 0)

data class Store(
    var masla: String = Vls.Maslas.MUTADA,
    var type: String = Vls.Types.DATE_ONLY,
//    var isDateOnly: Boolean = false,
//    var isDuration: Boolean = false,
//    var isNifas: Boolean = false,
//    var isMubtadia: Boolean = false,
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