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
}

fun TagConsumer<HTMLElement>.inputFormDiv(inputContainerToCopyFrom: HTMLElement? = null) {
    div(classes = Ids.InputContainers.INPUT_CONTAINER) {
        id = Ids.InputContainers.INPUT_CONTAINER
        if (inputContainerToCopyFrom != null) {
            removeInputsContainerButton()
        }
        addInputsContainerButton()
        inputForm(inputContainerToCopyFrom)
        content()
    }
}

// MAIN PROGRAM DRAWN HERE
private fun TagConsumer<HTMLElement>.inputForm(inputContainerToCopyFrom: HTMLElement?) {
    form(action = "javascript:void(0);") {
        autoComplete = false
        ikhtilafiMasle()
        br()
        div(classes = CssC.LABEL_INPUT) {
            maslaConfigurationSelectDropdown(inputContainerToCopyFrom)
            typeConfigurationSelectDropdown(inputContainerToCopyFrom)
            nifasInputs(inputContainerToCopyFrom)
            mutadaInputs(inputContainerToCopyFrom)
        }
        hr()
        questionInput()
        hr()
        haizDatesInputTable(inputContainerToCopyFrom)
        haizDurationInputTable(inputContainerToCopyFrom)
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
            makeIkhtilafiMasla(Ids.Ikhtilafat.IKHTILAF2,
                StringsOfLanguages.ENGLISH.aadatIncreasingAtEndOfDaurIkhtilaf,
                StringsOfLanguages.URDU.aadatIncreasingAtEndOfDaurIkhtilaf, extraClasses = CssC.DEV)
            makeIkhtilafiMasla(Ids.Ikhtilafat.IKHTILAF3,
                StringsOfLanguages.ENGLISH.ayyameqabliyyaikhtilaf,
                StringsOfLanguages.URDU.ayyameqabliyyaikhtilaf, extraClasses = CssC.DEV)
            makeIkhtilafiMasla(Ids.Ikhtilafat.IKHTILAF4,
                StringsOfLanguages.ENGLISH.mubtadiaikhitilaf,
                StringsOfLanguages.URDU.mubtadiaikhitilaf, extraClasses = CssC.DEV)
        }
    }
}

private fun TagConsumer<HTMLElement>.maslaConfigurationSelectDropdown(inputContainerToCopyFrom: HTMLElement?) {
    val isMutada = inputContainerToCopyFrom?.isMutada ?: IS_DEFAULT_INPUT_MODE_MUTADA
    val isNifas = inputContainerToCopyFrom?.isNifas ?: !IS_DEFAULT_INPUT_MODE_MUTADA
    val isMubtadia = inputContainerToCopyFrom?.isMubtadia ?: !IS_DEFAULT_INPUT_MODE_MUTADA
    div(classes = CssC.ROW) {
        makeLabel(Ids.Inputs.MASLA_TYPE_SELECT, StringsOfLanguages.ENGLISH.typeOfMasla, StringsOfLanguages.URDU.typeOfMasla)
        select {
            id = Ids.Inputs.MASLA_TYPE_SELECT
            onChangeFunction = { event -> disableTree(findInputContainer(event)) }
            makeDropdownOptions(isMutada, Vls.Maslas.MUTADA, StringsOfLanguages.ENGLISH.mutada, StringsOfLanguages.URDU.mutada)
            makeDropdownOptions(isNifas, Vls.Maslas.NIFAS, StringsOfLanguages.ENGLISH.nifas, StringsOfLanguages.URDU.nifas)
            makeDropdownOptions(isMubtadia, Vls.Maslas.MUBTADIA, StringsOfLanguages.ENGLISH.mubtadia, StringsOfLanguages.URDU.mubtadia, "dev")
        }
        // Zaalla?
        div {
            makeLabel(Ids.Inputs.ZAALLA_CHECKBOX, "Zaalla", "Zaalla")
            checkBoxInput {
                id = Ids.Inputs.ZAALLA_CHECKBOX
                name = Ids.Inputs.ZAALLA_CHECKBOX
                checked = inputContainerToCopyFrom?.isZaalla == true
                onChangeFunction = { event -> disableTree(findInputContainer(event)) }
            }
        }
    }

}

private fun TagConsumer<HTMLElement>.typeConfigurationSelectDropdown(inputContainerToCopyFrom: HTMLElement?) {
    val isDateTime = inputContainerToCopyFrom?.isDateTime ?: !IS_DEFAULT_INPUT_MODE_DATE_ONLY
    val isDateOnly = inputContainerToCopyFrom?.isDateOnly ?: IS_DEFAULT_INPUT_MODE_DATE_ONLY
    val isDuration = inputContainerToCopyFrom?.isDuration ?: !IS_DEFAULT_INPUT_MODE_DATE_ONLY
    div(classes = CssC.ROW) {
        makeLabel(Ids.Inputs.INPUT_TYPE_SELECT, StringsOfLanguages.ENGLISH.typeOfInput, StringsOfLanguages.URDU.typeOfInput)
        select {
            id = Ids.Inputs.INPUT_TYPE_SELECT
            onChangeFunction = { event ->
                onClickTypeConfigurationSelectDropdown(event)
            }
            makeDropdownOptions(isDateOnly, Vls.Types.DATE_ONLY, StringsOfLanguages.ENGLISH.dateOnly, StringsOfLanguages.URDU.dateOnly)
            makeDropdownOptions(isDateTime, Vls.Types.DATE_TIME, StringsOfLanguages.ENGLISH.dateAndTime, StringsOfLanguages.URDU.dateAndTime)
            makeDropdownOptions(isDuration, Vls.Types.DURATION, StringsOfLanguages.ENGLISH.duration, StringsOfLanguages.URDU.duration)
        }
    }
}

private fun FlowContent.nifasInputs(inputContainerToCopyFrom: HTMLElement?) {
    // Pregnancy Start Time
    div(classes = "${CssC.ROW} ${CssC.NIFAS} ${CssC.INVIS} ${CssC.DATETIME_AADAT}") {
        makeLabel(Ids.Inputs.PREG_START_TIME_INPUT, StringsOfLanguages.ENGLISH.pregnancyStartTime, StringsOfLanguages.URDU.pregnancyStartTime)
        pregnancyTimeInput(inputContainerToCopyFrom, Ids.Inputs.PREG_START_TIME_INPUT) {
            onChangeFunction = { event ->
                findInputContainer(event).pregEndTime.min = (event.currentTarget as HTMLInputElement).value
            }
        }
    }
    // Pregnancy End Time
    div(classes = "${CssC.ROW} ${CssC.DATETIME_AADAT} ${CssC.NIFAS} ${CssC.INVIS}") {
        makeLabel(Ids.Inputs.PREG_END_TIME_INPUT, StringsOfLanguages.ENGLISH.birthMiscarrriageTime, StringsOfLanguages.URDU.birthMiscarrriageTime)
        pregnancyTimeInput(inputContainerToCopyFrom, Ids.Inputs.PREG_END_TIME_INPUT) {
            onChangeFunction = { event ->
                findInputContainer(event).pregStartTime.max = (event.currentTarget as HTMLInputElement).value
            }
        }
    }

    // Pregnancy Mustabeen ul Khilqa?
    div(classes = "${CssC.ROW} ${CssC.NIFAS} ${CssC.INVIS}"){
        div {
            makeLabel(Ids.Inputs.MUSTABEEN_CHECKBOX, StringsOfLanguages.ENGLISH.mustabeenUlKhilqa, StringsOfLanguages.URDU.mustabeenUlKhilqa)
            checkBoxInput {
                id = Ids.Inputs.MUSTABEEN_CHECKBOX
                name = Ids.Inputs.MUSTABEEN_CHECKBOX
                checked = inputContainerToCopyFrom?.isMustabeen != false
                onChangeFunction = { event -> switchWiladatIsqat(findInputContainer(event)) }
            }
        }
    }

    // Pregnancy Aadat
    div(classes = "${CssC.ROW} ${CssC.NIFAS} ${CssC.INVIS}") {
        makeLabel(Ids.Inputs.AADAT_NIFAS_INPUT, StringsOfLanguages.ENGLISH.nifasAadat, StringsOfLanguages.URDU.nifasAadat)
        makeNumberInput(Ids.Inputs.AADAT_NIFAS_INPUT, inputContainerToCopyFrom?.aadatNifas?.value.orEmpty(), (1..40)) {
            step = "any"
            required = false
            disabled = inputContainerToCopyFrom?.isNifas != true
        }
    }
}

private fun FlowContent.mutadaInputs(inputContainerToCopyFrom: HTMLElement?) {
    // Aadat of Haiz
    div(classes = CssC.ROW) {
        makeLabel(Ids.Inputs.AADAT_HAIZ_INPUT, StringsOfLanguages.ENGLISH.haizAadat, StringsOfLanguages.URDU.haizAadat)
        makeNumberInput(Ids.Inputs.AADAT_HAIZ_INPUT, inputContainerToCopyFrom?.aadatHaz?.value.orEmpty(), (3..10)) {
            onChangeFunction = { event -> onlyTwo(event) }
        }
    }
    // Aadat of Tuhr
    div(classes = "${CssC.ROW} ${CssC.MUTADA}") {
        makeLabel(Ids.Inputs.AADAT_TUHR_INPUT, StringsOfLanguages.ENGLISH.tuhrAadat, StringsOfLanguages.URDU.tuhrAadat)
        makeNumberInput(Ids.Inputs.AADAT_TUHR_INPUT, inputContainerToCopyFrom?.aadatTuhr?.value.orEmpty(), (15..6 * 30)) {
            onChangeFunction = { event -> onlyTwo(event) }
        }
    }
    // Zaalla Cycle Length
    div(classes = "${CssC.ROW} ${CssC.ZAALLA} ${CssC.INVIS}") {
        makeLabel(Ids.Inputs.ZAALLA_CYCLE_LENGTH, "Cycle Length", "Cycle Length")
        makeNumberInput(Ids.Inputs.ZAALLA_CYCLE_LENGTH, inputContainerToCopyFrom?.cycleLength?.value.orEmpty(), (18..6 * 30 + 10)) {
            onChangeFunction = { event -> onlyTwo(event) }
        }
    }
    // Mawjooda Tuhr
    div(classes = "${CssC.ROW} ${CssC.DATETIME_AADAT}") {
        makeLabel(Ids.Inputs.MAWJOODA_TUHR_INPUT, StringsOfLanguages.ENGLISH.mawjoodahTuhr, StringsOfLanguages.URDU.mawjoodahTuhr)
        makeNumberInput(Ids.Inputs.MAWJOODA_TUHR_INPUT, inputContainerToCopyFrom?.mawjoodaTuhr?.value.orEmpty(), (15..10000))
        // Fasid?
        div {
            makeLabel(Ids.Inputs.MAWJOODA_FASID_CHECKBOX, StringsOfLanguages.ENGLISH.faasid, StringsOfLanguages.URDU.faasid)
            checkBoxInput {
                id = Ids.Inputs.MAWJOODA_FASID_CHECKBOX
                name = Ids.Inputs.MAWJOODA_FASID_CHECKBOX
                checked = inputContainerToCopyFrom?.isMawjoodaFasid?.or(false) == true
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
            startDurationInputRow(inputContainerToCopyFrom, isDuration)
        }
    }
}

private fun FlowContent.calculateButton() {
    button(classes = "${CssC.ENGLISH} ${CssC.CALC_BTN}") {
        id = Ids.Results.CALCULATE_BUTTON
        +StringsOfLanguages.ENGLISH.calculate
        onClickFunction = { event -> setMaxToCurrentTimeForTimeInputs(findInputContainer(event)) }
    }
    button(classes = "${CssC.URDU} ${CssC.CALC_BTN}") {
        id = Ids.Results.CALCULATE_BUTTON
        +StringsOfLanguages.URDU.calculate
        onClickFunction = { event -> setMaxToCurrentTimeForTimeInputs(findInputContainer(event)) }
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
