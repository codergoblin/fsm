package codegoblin.fsm

import java.util.function.Consumer

class FiniteStateMachine<S, I>(
    private val transitions: Map<S, Transition<S, I>>,
    private val defaultTransformation: Transition<S, I> = returnOld()
) : (S, I) -> S {

    override fun invoke(state: S, input: I) = (transitions.getOrDefault(state, defaultTransformation))(state, input)

    companion object {

        @JvmStatic
        @JvmOverloads
        fun <S, I> create(
            defaultTransformation: Transition<S, I> = returnOld(),
            transitionCollector: Consumer<MutableMap<S, Transition<S, I>>>
        ) = create(defaultTransformation) {
            transitionCollector.accept(this)
        }

        @JvmSynthetic
        fun <S, I> create(
            defaultTransformation: Transition<S, I> = returnOld(),
            transitionCollector: MutableMap<S, Transition<S, I>>.() -> Unit
        ): FiniteStateMachine<S, I> {

            val transitions = HashMap<S, Transition<S, I>>()
            transitionCollector(transitions)

            return FiniteStateMachine(transitions, defaultTransformation)
        }

    }

}
