# Finite state machine

A simple finite state machine implementation written in Kotlin.
Added jvm compiler hooks for better semantics when using this library in java.


### Add it to your project

Library can obtained in gradle using jitpack 

```groovy
repositories {
    // ...
    maven { url 'https://jitpack.io' }
    // ...
}


dependencies {
    // ...
    implementation 'com.github.codergoblin:fsm:0.0.1-SNAPSHOT'
    // ...
}
```

### Simple state machine example
Implementation for the following diagram

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

## Semantics

```kotlin

fun main() {

    val walkingSimulation = FiniteStateMachine.create<String, Request> {
        put("running",
            match {
                // Override default behavior of returning existing state in case no transitions are found 
                withDefaultTransformation { "stand" }
                    // Match exact value
                    .match(Request("stop")).thenReturn("standing")
                    // Match value with a qualifier
                    .match { it -> it.action == "duck" }.thenReturn("ducking")
                    // Apply matchers to namespaced properties for simpler semantcs
                    .extracting { it.action }
                    .match {
                        match("jump").thenReturn("in the air")
                    }
            })
        //...
    }

}
data class Request(val action: String)

```


## Build it yourself
```bash

git clone https://github.com/codergoblin/fsm.git
cd fsm
./gradlew jar

```
Executable is located under `build/libs`
