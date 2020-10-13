@file:JvmName("Transition")
package codegoblin.fsm


typealias Transition<S, I> = (S, I) -> S

fun <S> returnNew(): Transition<S, S> = { _, input -> input }
fun <S, I> returnOld(): Transition<S, I> = { state, _ -> state }

