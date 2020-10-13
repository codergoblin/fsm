package codegoblin.fsm


class InputMatcherTransition<S, I>(
    private val transformations: List<Pair<S, I>>,
    private val defaultTransition: (S) -> S = { it }
) : Transition<S, I> {

    override fun invoke(state: S, input: I): S {
        val transformation = transformations
            .filter {
                it.matcher(input, state)
            }
            .map { it.transformation }
            .firstOrNull()

        return (transformation ?: defaultTransition)(state)
    }

    class Pair<S, I>(
        val matcher: (I, S) -> Boolean,
        val transformation: (S) -> S
    ) {

        fun <T> wrapInput(transformer: (T) -> I): Pair<S, T> {
            return Pair(
                { s: T, t: S -> matcher(transformer(s), t) },
                transformation
            )
        }

    }

}
