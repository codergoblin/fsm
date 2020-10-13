package codegoblin.fsm

import com.nhaarman.mockitokotlin2.*
import org.assertj.core.api.Assertions.assertThat
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.gherkin.Feature

class InputMatcherSpec : Spek({

    Feature("Matchers") {


        Scenario("Should invoke matching transition with correct state") {
            val resultValue = "value2"
            val transformation: (String) -> String = mock()
            `when`(transformation.invoke(any())).thenReturn(resultValue)
            val inputState = "state"
            val matcherValue = "value2"
            val transition: Transition<String, String> = TransitionBuilder.match {
                this.match("value1")
                    .thenReturn("result1")
                    .match(matcherValue)
                    .then(transformation)
            }
            lateinit var actual: String

            When("Triggering the transition") {
                actual = transition(inputState, matcherValue)
            }
            Then("Result is returned") {
                assertThat(actual).isEqualTo(resultValue)
            }
            Then("Transformation has been invoked with correct value") {
                verify(transformation).invoke(inputState)
            }
        }

    }

    Feature("Default transformation") {

        lateinit var transition: Transition<String, String>
        val transformation: (String) -> String by memoized { spy { it } }
        val triggerState = "state"
        val triggerValue = "value"

        Scenario("Trigger default transformation given no matchers") {
            Given("Transition has no matchers") {
                transition = TransitionBuilder.match {
                    withDefaultTransformation(transformation)
                }
            }
            When("Triggering the transition") {
                transition(triggerState, triggerValue)
            }
            Then("Default transformation was triggered") {
                verify(transformation).invoke(triggerState)
            }
        }

        Scenario("Not trigger default transformation given a correct matcher") {
            Given("Transition has matcher") {
                transition = TransitionBuilder.match {
                    withDefaultTransformation(transformation)
                        .match(triggerValue)
                        .thenReturn("foo")
                }
            }
            When("Triggering the transition") {
                transition(triggerState, triggerValue)
            }
            Then("Default transformation was triggered") {
                verifyZeroInteractions(transformation)
            }
        }

        Scenario("Trigger default transformation given a no correct matcher") {
            Given("Transition has matcher") {
                transition = TransitionBuilder.match {
                    withDefaultTransformation(transformation)
                        .match("some value")
                        .thenReturn("foo")
                }
            }
            When("Triggering the transition") {
                transition(triggerState, triggerValue)
            }
            Then("Default transformation was triggered") {
                verify(transformation).invoke(triggerState)
            }
        }

    }

    Feature("Extract matcher for complex types") {

        val result = "newResult"
        val exitingQualifier = "foo"
        val transition: Transition<String, Foo> by memoized {
            TransitionBuilder.match {
                extracting { it.bar }
                    .match {
                        match(exitingQualifier).thenReturn(result)
                    }
            }
        }
        lateinit var actual: String

        Scenario("Should match complex types") {
            When("Triggering matching transition") {
                actual = transition("somestate", Foo(exitingQualifier))
            }
            Then("State is set to new one from transition") {
                assertThat(actual).isEqualTo(result)
            }
        }

        Scenario("Should return initial state given complex matched does not pass challenge") {
            val state = "somestate"
            When("Triggering non matching transition") {
                actual = transition(state, Foo("other"))
            }
            Then("State has not change") {
                assertThat(actual).isEqualTo(state)
            }
        }

    }

})

data class Foo(val bar: String)