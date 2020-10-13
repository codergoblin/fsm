package codegoblin.fsm

import java.util.function.Consumer
import codegoblin.fsm.InputMatcherTransition.Pair as Pair


class TransitionBuilder<S, I> {

    private var defaultTransformation: (S) -> S = { it }
    private val transformations: MutableList<Pair<S, I>> = ArrayList()

    fun withDefaultTransformation(transformation: (S) -> S): TransitionBuilder<S, I> {
        defaultTransformation = transformation
        return this
    }

    fun match(qualifier: I) = match { challenge: I -> challenge == qualifier }

    fun match(predicate: (I) -> Boolean) = match { i: I, _: S -> predicate(i) }

    fun match(predicate: (I, S) -> Boolean) = TransitionBuilderCase(this, predicate)

    fun build() = InputMatcherTransition(transformations, defaultTransformation)

    fun <T> extracting(transformer: (I) -> T): HigherLayerMatcher<T> {
        return HigherLayerMatcher(transformer, this)
    }

    inner class TransitionBuilderCase(
        private val builder: TransitionBuilder<S, I>,
        private val predicate: (I, S) -> Boolean
    ) {

        fun thenReturn(result: S) = then { result }

        fun then(transformation: (S) -> S): TransitionBuilder<S, I> {
            transformations.add(
                Pair(
                    predicate,
                    transformation
                )
            )
            return builder
        }

    }

    inner class HigherLayerMatcher<T>(
        private val transformer: (I) -> T,
        private val builder: TransitionBuilder<S, I>
    ) {
        private val temporaryBuilder: TransitionBuilder<S, T> = TransitionBuilder()

        fun match(consumer: TransitionBuilder<S, T>.() -> Unit): TransitionBuilder<S, I> {

            consumer(temporaryBuilder)
            temporaryBuilder.transformations
                .map { it.wrapInput(transformer) }
                .forEach(builder.transformations::add)

            return builder
        }

    }

    companion object {

        @JvmStatic
        fun <T, I> match(
            consumer: Consumer<TransitionBuilder<T, I>>
        ): Transition<T, I> = match { consumer.accept(this) }

        @JvmSynthetic
        fun <T, I> match(
            consumer: TransitionBuilder<T, I>.() -> Unit
        ): Transition<T, I> {
            val builder: TransitionBuilder<T, I> = TransitionBuilder()
            consumer(builder)
            return builder.build()
        }
    }

}