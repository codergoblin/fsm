package codegoblin.fsm

import com.nhaarman.mockitokotlin2.spy
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyZeroInteractions
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.mockito.ArgumentMatchers
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.Mockito
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.gherkin.Feature

const val OTHER_STATE = "otherState"
const val NEW_STATE = "newState"
const val DEFAULT_STATE = "state"

class FiniteStateMachineSpec : Spek({

    Feature("Transitions") {

        lateinit var fsm: FiniteStateMachine<String, String>

        Scenario("Return old state") {
            lateinit var newState: String
            Given("No transitions") {
                fsm = FiniteStateMachine(emptyMap())
            }
            When("Invoking state machine") {
                newState = fsm(DEFAULT_STATE, "")
            }
            Then("State hasn't change") {
                assertThat(newState).isEqualTo(DEFAULT_STATE)
            }
        }

        Scenario("Return new state given matching transition") {
            lateinit var newState: String
            Given("Transition") {
                fsm = FiniteStateMachine.create { put(DEFAULT_STATE, returnNew()) }
            }
            When("Invoking state machine for matching status") {
                newState = fsm(DEFAULT_STATE, OTHER_STATE)
            }
            Then("State has changed") {
                assertThat(newState).isEqualTo(OTHER_STATE)
            }
        }

        Scenario("Return old state given no matching transition") {
            lateinit var newState: String
            Given("Transition") {
                fsm = FiniteStateMachine.create { put(DEFAULT_STATE, returnNew()) }
            }
            When("Invoking state machine for no matching status") {
                newState = fsm(NEW_STATE, OTHER_STATE)
            }
            Then("State hash not changed") {
                assertThat(newState).isEqualTo(NEW_STATE)
            }
        }

        Scenario("Invoking state machine should send old state and input to transition") {
            val transition: Transition<String, String> = spy { it }
            Given("Transition") {
                fsm = FiniteStateMachine.create { put(DEFAULT_STATE, transition) }
            }
            When("Invoking state machine for matching status") {
                fsm(DEFAULT_STATE, OTHER_STATE)
            }
            Then("Correct transition was invoked with old status and input") {
                verify(transition).invoke(DEFAULT_STATE, OTHER_STATE)
            }
        }

    }

    Feature("Default transition") {

        lateinit var defaultTransformation: Transition<String, String>
        beforeEachScenario {
            defaultTransformation = spy { it }
        }
        val fsm by memoized {
            FiniteStateMachine.create(defaultTransformation){
                put(DEFAULT_STATE) { _, _ -> NEW_STATE }
            }
        }

        Scenario("Should call default transition given no custom transitions found") {
            When("Invoking state machine for status does not have transitions") {
                fsm(NEW_STATE, "")
            }
            Then("Default transition was invoked") {
                verify(defaultTransformation).invoke(anyString(), anyString())
            }
        }

        Scenario("Should not call default transition given custom transition found") {
            When("Invoking state machine for status that has transitions") {
                fsm(DEFAULT_STATE, "")
            }
            Then("Default transition was not invoked") {
                verifyZeroInteractions(defaultTransformation)
            }
        }

    }

})