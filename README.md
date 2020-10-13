

### Simple state machine example
Implementation for the follwing diagram

<pre>

+------- +------+ -----COIN-----> +--------+ <-COIN-+
|        |LOCKED|                 |UNLOCKED|        |
+-PUSH-> +------+ <---PUSH------- +--------+ -------+

</pre>


<table border="0">
<tr>
<td><b style="font-size:30px">Kotlin</b></td>
<td><b style="font-size:30px">Java</b></td>
</tr>
<tr>
<td>

```kotlin
import codegoblin.fsm.TransitionBuilder.Companion.match


fun main() {

    val coinMachine: FiniteStateMachine<State, Action> = FiniteStateMachine.create {
        put(LOCKED, match {
            match(COIN).thenReturn(UNLOCKED)
        })
        put(UNLOCKED, match {
            match(PUSH).thenReturn(LOCKED)
        })
    }

    val newState = coinMachine(LOCKED, PUSH) // Still locked

}

enum class Action {
    PUSH, COIN
}

enum class State {
    LOCKED, UNLOCKED
}
```

</td>
<td>

```java
import static codegoblin.fsm.TransitionBuilder.match;

public class CoinMachine {

    public static void main(String[] args) {
        FiniteStateMachine<State, Action> coinMachine = FiniteStateMachine.create(
                transitions -> {
                    transitions.put(
                            LOCKED,
                            match(matcher -> matcher.match(COIN).thenReturn(UNLOCKED))
                    );
                    transitions.put(
                            UNLOCKED,
                            match(matcher -> matcher.match(PUSH).thenReturn(LOCKED))
                    );
                }
        );

        State newState = coinMachine.invoke(LOCKED, PUSH); // Still locked

    }

    enum Action {
        PUSH, COIN
    }

    enum State {
        LOCKED, UNLOCKED
    }

}
```

</td>
</tr>
</table>