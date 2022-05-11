@file:Suppress("SpellCheckingInspection")

import kotlinx.html.dom.append
import kotlinx.html.*
import kotlinx.html.form
import kotlinx.html.js.*
import kotlinx.html.tr
import org.w3c.dom.*

fun Node.addInputLayout() {
    append {
        div {
            id = Ids.InputContainers.INPUT_CONTAINERS_CONTAINER
            inputFormDiv()
        }
        div(classes = "${CssC.DEV} ${CssC.CENTER}") {
            id = Ids.Results.CALCULATE_ALL_DIV
        }
        div(classes = "${CssC.CENTER} ${CssC.DEV}") {
            id = Ids.Results.COMPARISON_CONTAINER
            div(classes = CssC.GRID) { id = Ids.Results.DATES_DIFFERENCE_TABLE }
        }
    }
    addReact()
}

fun TagConsumer<HTMLElement>.inputFormDiv(inputContainerToCopyFrom: HTMLElement? = null) {
    div(classes = Ids.InputContainers.INPUT_CONTAINER) {
        id = Ids.InputContainers.INPUT_CONTAINER
        if (inputContainerToCopyFrom != null) {
            removeInputsContainerButton()
        }
        addInputsContainerButton()
println("7")
        inputForm(inputContainerToCopyFrom)
println("8")
        content()
    }
}

//// MAIN PROGRAM DRAWN HERE
private fun TagConsumer<HTMLElement>.inputForm(inputContainerToCopyFrom: HTMLElement?) {
    form(action = "javascript:void(0);") {
        autoComplete = false
        ikhtilafiMasle()
        br()
        div(classes = CssC.LABEL_INPUT) { id = Ids.InputContainers.REACT_DIV }
        hr()
        questionInput()
        hr()
        haizDatesInputTable(inputContainerToCopyFrom)
println("f")
        haizDurationInputTable(inputContainerToCopyFrom)
println("g")
        calculateButton()
        hr()
        onSubmitFunction = { event -> parseEntries(findInputContainer(event)) }
    }
}

private fun FlowContent.ikhtilafiMasle() {
    div {
        details {
            summary(classes = CssC.IKHTILAF)
            b {
                span(classes = CssC.ENGLISH) { +StringsOfLanguages.ENGLISH.ikhtilafimasail }
                span(classes = CssC.URDU) { StringsOfLanguages.URDU.ikhtilafimasail }
            }
            makeIkhtilafiMasla(Ids.Ikhtilafat.IKHTILAF1,
                StringsOfLanguages.ENGLISH.considerTuhrInGhiarMustabeenIsqaatIkhtilaf,
                StringsOfLanguages.URDU.considerTuhrInGhiarMustabeenIsqaatIkhtilaf)
            if (devmode) {
                makeIkhtilafiMasla(
                    Ids.Ikhtilafat.IKHTILAF2,
                    StringsOfLanguages.ENGLISH.aadatIncreasingAtEndOfDaurIkhtilaf,
                    StringsOfLanguages.URDU.aadatIncreasingAtEndOfDaurIkhtilaf, extraClasses = CssC.DEV
                )
                makeIkhtilafiMasla(
                    Ids.Ikhtilafat.IKHTILAF3,
                    StringsOfLanguages.ENGLISH.ayyameqabliyyaikhtilaf,
                    StringsOfLanguages.URDU.ayyameqabliyyaikhtilaf, extraClasses = CssC.DEV
                )
                makeIkhtilafiMasla(
                    Ids.Ikhtilafat.IKHTILAF4,
                    StringsOfLanguages.ENGLISH.mubtadiaikhitilaf,
                    StringsOfLanguages.URDU.mubtadiaikhitilaf, extraClasses = CssC.DEV
                )
            }
        }
    }
}

private fun TagConsumer<HTMLElement>.questionInput() {
    details(classes = CssC.DEV) {
        summary {
            makeSpans(StringsOfLanguages.ENGLISH.titleTextFieldLabel, StringsOfLanguages.URDU.titleTextFieldLabel)
        }
        div(classes = CssC.ROW) {
            makeTextAreaInput(Ids.Inputs.INPUT_DESCRIPTION, "36px")
        }
    }
    details {
        summary {
            makeSpans(StringsOfLanguages.ENGLISH.questionTextFieldLabel, StringsOfLanguages.URDU.questionTextFieldLabel)
        }
        div(classes = CssC.ROW) {
            makeTextAreaInput(Ids.Inputs.INPUT_QUESTION)
        }
    }
}

private fun TagConsumer<HTMLElement>.haizDatesInputTable(inputContainerToCopyFrom: HTMLElement?) {
    val isDuration = inputContainerToCopyFrom?.isDuration ?: false
    table {
        id = Ids.InputTables.HAIZ_INPUT_TABLE
        classes = setOf( if (isDuration) CssC.INVIS else "" )
        thead {
            tr {
                th { makeSpans(StringsOfLanguages.ENGLISH.startTime, StringsOfLanguages.URDU.startTime) }
                th { makeSpans(StringsOfLanguages.ENGLISH.endTime, StringsOfLanguages.URDU.endTime) }
                th { addBeforeButton() }
            }
        }
        tbody {
            startInputRow(inputContainerToCopyFrom, isDuration)
        }
    }
}

private fun TagConsumer<HTMLElement>.haizDurationInputTable(inputContainerToCopyFrom: HTMLElement?) {
    val isDuration = inputContainerToCopyFrom?.isDuration ?: false
    table {
        id = Ids.InputTables.HAIZ_DURATION_INPUT_TABLE
        classes = setOf( if (!isDuration) CssC.INVIS else "" )
        thead {
            tr {
                th { makeSpans(StringsOfLanguages.ENGLISH.duration, StringsOfLanguages.URDU.duration) }
                th { makeSpans(StringsOfLanguages.ENGLISH.damOrTuhr, StringsOfLanguages.URDU.damOrTuhr) }
                th { addBeforeButton(true) }
            }
        }
        tbody {
println("9")
            startDurationInputRow(inputContainerToCopyFrom, isDuration)
println("10")
        }
    }
}

private fun FlowContent.calculateButton() {
    button(classes = "${CssC.ENGLISH} ${CssC.CALC_BTN}") {
        id = Ids.Results.CALCULATE_BUTTON
        +StringsOfLanguages.ENGLISH.calculate
    }
    button(classes = "${CssC.URDU} ${CssC.CALC_BTN}") {
        id = Ids.Results.CALCULATE_BUTTON
        +StringsOfLanguages.URDU.calculate
    }
}

private fun TagConsumer<HTMLElement>.content() {
    div(classes = CssC.INVIS) {
        id = Ids.Results.CONTENT_CONTAINER
        div(classes = CssC.URDU) {
            id = Ids.Results.CONTENT_WRAPPER
            copyBtn(CssC.LEFT, CssC.RTL)
            content { id = Ids.Results.CONTENT_URDU }
        }
        div(classes = CssC.ENGLISH) {
            id = Ids.Results.CONTENT_WRAPPER
            copyBtn(CssC.RIGHT)
            content { id = Ids.Results.CONTENT_ENGLISH }
        }
        content { id = Ids.Results.CONTENT_DATES }
        hr()
    }
}

// CLONING
private fun TagConsumer<HTMLElement>.addInputsContainerButton() {
    button(type = ButtonType.button) {
        +"Clone"
        style = "float: right"
        classes = setOf(CssC.PLUS, CssC.DEV)
        id = Ids.InputContainers.INPUTS_CONTAINER_CLONE_BUTTON
        onClickFunction = { event ->
            cloneInputsContainer(findInputContainer(event))
        }
    }
}

private fun TagConsumer<HTMLElement>.removeInputsContainerButton() {
    button(type = ButtonType.button) {
        +"\u274C"
        style = "float: right"
        classes = setOf(CssC.MINUS)
        id = Ids.InputContainers.INPUTS_CONTAINER_REMOVE_BUTTON
        onClickFunction = { event ->
            removeInputsContainer(findInputContainer(event))
        }
    }
}
