import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.html.*
import kotlinx.html.dom.append
import kotlinx.html.form
import kotlinx.html.js.*
import org.w3c.dom.HTMLElement
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.HTMLSelectElement
import org.w3c.dom.Node

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
        if (isPersonalApper) {
            inputFormPersonalApper(inputContainerToCopyFrom)
        } else {
            inputForm(inputContainerToCopyFrom)
        }
        content()
    }
}

// MAIN PROGRAM DRAWN HERE
private fun TagConsumer<HTMLElement>.inputForm(inputContainerToCopyFrom: HTMLElement?) {
    form(action = "javascript:void(0);") {
        autoComplete = false
        inputFormPreMasla(inputContainerToCopyFrom)
        inputFormEntryTables(inputContainerToCopyFrom)
        onSubmitFunction = { event ->
            parseEntries(findInputContainer(event)) }
    }
}

private fun TagConsumer<HTMLElement>.inputFormPersonalApper(inputContainerToCopyFrom: HTMLElement?) {
    form(action = "javascript:void(0);") {
        autoComplete = false
        div {
            details {
                summary(classes = CssC.IKHTILAF)
                inputFormPreMasla(inputContainerToCopyFrom)
            }
        }
        inputFormEntryTables(inputContainerToCopyFrom)
        onSubmitFunction = { event -> parseEntries(findInputContainer(event)) }
    }
}

private fun TagConsumer<HTMLElement>.inputFormPreMasla(inputContainerToCopyFrom: HTMLElement?) {
    ikhtilafiMasle()
    br()
    div(classes = CssC.LABEL_INPUT) {
        maslaConfigurationSelectDropdown(inputContainerToCopyFrom)
        typeConfigurationSelectDropdown(inputContainerToCopyFrom)
        nifasInputs(inputContainerToCopyFrom)
        mutadaInputs(inputContainerToCopyFrom)
    }
    hr()
    questionInput(inputContainerToCopyFrom)
    hr()
}

private fun TagConsumer<HTMLElement>.inputFormEntryTables(inputContainerToCopyFrom: HTMLElement?) {
    haizDatesInputTable(inputContainerToCopyFrom)
    haizDurationInputTable(inputContainerToCopyFrom)
    calculateButton()
    hr()
}

private fun TagConsumer<HTMLElement>.ikhtilafiMasle() {
    val classes = if (isPersonalApper) CssC.INVIS else ""
    div(classes = classes) {
        details {
            summary(classes = CssC.IKHTILAF) {
                onClickFunction = { for (element in collapsingElements) element.classList.toggle(CssC.COLLAPSE) }
            }
            b {
                span(classes = CssC.ENGLISH) { +StringsOfLanguages.ENGLISH.ikhtilafimasail }
                span(classes = CssC.MMENGLISH) { +StringsOfLanguages.MMENGLISH.ikhtilafimasail }
                span(classes = CssC.URDU) { StringsOfLanguages.URDU.ikhtilafimasail }
            }
            makeIkhtilafiMasla(Ids.Ikhtilafat.IKHTILAF1, Strings::considerTuhrInGhiarMustabeenIsqaatIkhtilaf, true)
            makeIkhtilafiMasla(
                Ids.Ikhtilafat.IKHTILAF2,
                Strings::aadatIncreasingAtEndOfDaurIkhtilaf, false,
                extraClasses = CssC.DEV
            )
            makeIkhtilafiMasla(
                Ids.Ikhtilafat.IKHTILAF3,
                Strings::ayyameqabliyyaikhtilaf,
                false,
                extraClasses = CssC.DEV
            )
            makeIkhtilafiMasla(Ids.Ikhtilafat.IKHTILAF4, Strings::mubtadiaikhitilaf, false, extraClasses = CssC.DEV)
        }
    }
}

private fun TagConsumer<HTMLElement>.maslaConfigurationSelectDropdown(inputContainerToCopyFrom: HTMLElement?) {
    val isMutada = inputContainerToCopyFrom?.isMutada ?: IS_DEFAULT_INPUT_MODE_MUTADA
    val isNifas = inputContainerToCopyFrom?.isNifas ?: !IS_DEFAULT_INPUT_MODE_MUTADA
    val isMubtadia = inputContainerToCopyFrom?.isMubtadia ?: !IS_DEFAULT_INPUT_MODE_MUTADA

    div(classes = CssC.ROW) {
        makeLabel(Ids.Inputs.MASLA_TYPE_SELECT, Strings::typeOfMasla)
        select {
            id = Ids.Inputs.MASLA_TYPE_SELECT
            disabled = isPersonalApper
            onChangeFunction = { event -> maslaChanging((event.currentTarget as HTMLSelectElement).value) }
            makeDropdownOptions(isMutada, Vls.Maslas.MUTADA, Strings::mutada)
            makeDropdownOptions(isNifas, Vls.Maslas.NIFAS, Strings::nifas)
            makeDropdownOptions(isMubtadia, Vls.Maslas.MUBTADIA, Strings::mubtadia, CssC.DEV) {
                disabled = !devmode
            }
        }
        // Zaalla?
        div(classes = CssC.DEV) {
            makeLabel(Ids.Inputs.ZAALLA_CHECKBOX, Strings::zaalla, CssC.ZAALLA)
            checkBoxInput {
                id = Ids.Inputs.ZAALLA_CHECKBOX
                name = Ids.Inputs.ZAALLA_CHECKBOX
                checked = inputContainerToCopyFrom?.isZaalla == true
                disabled = isPersonalApper || !devmode
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
        makeLabel(Ids.Inputs.INPUT_TYPE_SELECT, Strings::typeOfInput)
        select {
            id = Ids.Inputs.INPUT_TYPE_SELECT
            disabled = isPersonalApper
            onChangeFunction = { event -> onClickTypeConfigurationSelectDropdown(event) }
            makeDropdownOptions(isDateOnly, Vls.Types.DATE_ONLY, Strings::dateOnly)
            makeDropdownOptions(isDateTime, Vls.Types.DATE_TIME, Strings::dateAndTime)
            makeDropdownOptions(isDuration, Vls.Types.DURATION, Strings::duration)
        }
        div {
            classes = setOf(CssC.DATETIME_ONLY, CssC.INVIS)

            makeLabel(Ids.Inputs.IS_DAYLIGHT_SAVINGS, Strings::isDaylightSavings)
            checkBoxInput {
                id = Ids.Inputs.IS_DAYLIGHT_SAVINGS
                disabled = isPersonalApper
                onChangeFunction = {
                    val tzs = findInputContainer(it).timezoneSelect
                    tzs.disabled = !tzs.disabled
                }
            }
        }
        select {
            id = Ids.Inputs.SELECT_LOCALE
            classes = setOf(CssC.DATETIME_ONLY, CssC.INVIS)
            disabled = true
            makeTzOptions()
            option {
                value = "UTC"
                id = "UTC"
                selected = true
                +"(GMT-00:00) UTC"
            }
        }
    }
}

private fun FlowContent.nifasInputs(inputContainerToCopyFrom: HTMLElement?) {
    val disable = isPersonalApper || inputContainerToCopyFrom?.isNifas ?: true
    // Pregnancy Start Time
    div(classes = "${CssC.ROW} ${CssC.NIFAS} ${CssC.INVIS} ${CssC.DATE_OR_TIME_AADAT}") {
        makeLabel(Ids.Inputs.PREG_START_TIME_INPUT, Strings::pregnancyStartTime)
        pregnancyTimeInput(inputContainerToCopyFrom, Ids.Inputs.PREG_START_TIME_INPUT) {
            value = inputContainerToCopyFrom?.pregStartTime?.value ?: ""
            disabled = disable
            onChangeFunction = { event ->
                findInputContainer(event).pregEndTime.min = (event.currentTarget as HTMLInputElement).value
            }
        }
    }
    // Pregnancy End Time
    div(classes = "${CssC.ROW} ${CssC.DATE_OR_TIME_AADAT} ${CssC.NIFAS} ${CssC.INVIS}") {
        makeLabel(Ids.Inputs.PREG_END_TIME_INPUT, Strings::birthMiscarrriageTime)
        pregnancyTimeInput(inputContainerToCopyFrom, Ids.Inputs.PREG_END_TIME_INPUT) {
            value = inputContainerToCopyFrom?.pregEndTime?.value ?: ""
            disabled = disable
            onChangeFunction = { event ->
                findInputContainer(event).pregStartTime.max = (event.currentTarget as HTMLInputElement).value
            }
        }
    }

    // Pregnancy Mustabeen ul Khilqa?
    div(classes = "${CssC.ROW} ${CssC.NIFAS} ${CssC.INVIS}") {
        div {
            makeLabel(Ids.Inputs.MUSTABEEN_CHECKBOX, Strings::mustabeenUlKhilqa)
            checkBoxInput {
                id = Ids.Inputs.MUSTABEEN_CHECKBOX
                name = Ids.Inputs.MUSTABEEN_CHECKBOX
                checked = inputContainerToCopyFrom?.isMustabeen != false
                disabled = disable
                onChangeFunction = { event -> switchWiladatIsqat(findInputContainer(event)) }
            }
        }
    }

    // Pregnancy Aadat
    div(classes = "${CssC.ROW} ${CssC.NIFAS} ${CssC.INVIS}") {
        makeLabel(Ids.Inputs.AADAT_NIFAS_INPUT, Strings::nifasAadat)
        makeNumberInput(Ids.Inputs.AADAT_NIFAS_INPUT, inputContainerToCopyFrom?.aadatNifas?.value.orEmpty(), (1..40)) {
            step = "any"
            required = false
            disabled = disable
        }
    }
}

private fun FlowContent.mutadaInputs(inputContainerToCopyFrom: HTMLElement?) {
    // Aadat of Haiz
    div(classes = CssC.ROW) {
        makeLabel(Ids.Inputs.AADAT_HAIZ_INPUT, Strings::haizAadat)
        makeNumberInput(Ids.Inputs.AADAT_HAIZ_INPUT, inputContainerToCopyFrom?.aadatHaz?.value.orEmpty(), (3..10)) {
            onChangeFunction = { event -> onlyTwo(event) }
            disabled = isPersonalApper
        }
    }
    // Aadat of Tuhr
    div(classes = "${CssC.ROW} ${CssC.MUTADA}") {
        makeLabel(Ids.Inputs.AADAT_TUHR_INPUT, Strings::tuhrAadat)
        makeNumberInput(
            Ids.Inputs.AADAT_TUHR_INPUT,
            inputContainerToCopyFrom?.aadatTuhr?.value.orEmpty(),
            (15..6 * 30)
        ) {
            onChangeFunction = { event -> onlyTwo(event) }
            disabled = isPersonalApper
        }
    }
    // Zaalla Cycle Length
    div(classes = "${CssC.ROW} ${CssC.ZAALLA} ${CssC.INVIS}") {
        makeLabel(Ids.Inputs.ZAALLA_CYCLE_LENGTH, Strings::zaallaCycleLength, "Cycle Length")
        makeNumberInput(
            Ids.Inputs.ZAALLA_CYCLE_LENGTH,
            inputContainerToCopyFrom?.cycleLength?.value.orEmpty(),
            (18..6 * 30 + 10)
        ) {
            onChangeFunction = { event -> onlyTwo(event) }
            disabled = isPersonalApper || !devmode || inputContainerToCopyFrom?.isZaalla ?: true
        }
    }
    // Mawjooda Tuhr
    div(classes = "${CssC.ROW} ${CssC.DATE_OR_TIME_AADAT}") {
        makeLabel(Ids.Inputs.MAWJOODA_TUHR_INPUT, Strings::mawjoodahTuhr)
        makeNumberInput(
            Ids.Inputs.MAWJOODA_TUHR_INPUT,
            inputContainerToCopyFrom?.mawjoodaTuhr?.value.orEmpty(),
            (15..10000)
        ) { disabled = isPersonalApper }
        // Fasid?
        div {
            makeLabel(Ids.Inputs.MAWJOODA_FAASID_CHECKBOX, Strings::faasid)
            checkBoxInput {
                id = Ids.Inputs.MAWJOODA_FAASID_CHECKBOX
                name = Ids.Inputs.MAWJOODA_FAASID_CHECKBOX
                checked = inputContainerToCopyFrom?.isMawjoodaFasid?.or(false) == true
                disabled = isPersonalApper
            }
        }
    }
}

@OptIn(DelicateCoroutinesApi::class)
private fun TagConsumer<HTMLElement>.questionInput(inputContainerToCopyFrom: HTMLElement?) {
    details {
        summary { makeSpans(Strings::loadMaslaFromID) }
        div(classes = CssC.ROW) {
            input {
                id = Ids.Inputs.INPUT_ID
                name = Ids.Inputs.INPUT_ID
                value = inputContainerToCopyFrom?.inputID.orEmpty()
                disabled = isPersonalApper
            }
            button(classes = CssC.CALC_BTN) {
                id = Ids.Inputs.SUBMIT
                name = Ids.Inputs.SUBMIT
                type = ButtonType.button
                +"Submit"
                onClickFunction = { event ->
                    val inputsContiner = findInputContainer(event)
                    val id = inputsContiner.inputID
                    GlobalScope.launch { loadData(id, inputsContiner) }
                }
            }
            content(Ids.ERROR_MESSAGE, CssC.INVIS)
        }
    }

    details {
        summary {
            makeSpans(Strings::saailaDetailsFieldLabel)
        }
        div(classes = CssC.ROW) {
            input {
                id = Ids.Inputs.INPUT_SAAILA
                name = Ids.Inputs.INPUT_SAAILA
                value = inputContainerToCopyFrom?.saailaDetails.orEmpty()
                disabled = isPersonalApper
            }
        }
    }
    details {
        summary { makeSpans(Strings::questionTextFieldLabel) }
        div(classes = CssC.ROW) { makeTextAreaInput(Ids.Inputs.INPUT_QUESTION) }
    }
    div(classes = CssC.ROW) {
        div() {
            makeLabel(Ids.Inputs.NOW_CHECKBOX, Strings::nowOpt)
            checkBoxInput {
            id = Ids.Inputs.NOW_CHECKBOX
            name = Ids.Inputs.NOW_CHECKBOX
            checked = inputContainerToCopyFrom?.addNow != false            }
        }
    }

}

private fun TagConsumer<HTMLElement>.haizDatesInputTable(inputContainerToCopyFrom: HTMLElement?) {
    val isDuration = inputContainerToCopyFrom?.isDuration ?: false
    table {
        id = Ids.InputTables.HAIZ_INPUT_TABLE
        classes = setOf(if (isDuration) CssC.INVIS else "")
        thead {
            tr {
                th { makeSpans(Strings::startTime) }
                th { makeSpans(Strings::endTime) }
                th { addBeforeButton() }
            }
        }
        tbody() {
            id = Ids.InputTables.HAIZ_INPUT_TABLE_BODY
            startInputRow(inputContainerToCopyFrom, isDuration)
        }
    }
}

private fun TagConsumer<HTMLElement>.haizDurationInputTable(inputContainerToCopyFrom: HTMLElement?) {
    val isDuration = inputContainerToCopyFrom?.isDuration ?: false
    table {
        id = Ids.InputTables.HAIZ_DURATION_INPUT_TABLE
        classes = setOf(if (!isDuration) CssC.INVIS else "")
        thead {
            tr {
                th { makeSpans(Strings::duration) }
                th { makeSpans(Strings::damOrTuhr) }
                th { addBeforeButton(true) }
            }
        }
        tbody {
            id = Ids.InputTables.HAIZ_DURATION_INPUT_TABLE_BODY
            startDurationInputRow(inputContainerToCopyFrom, isDuration)
        }
    }
    if (isPersonalApper) {
        br()
        div(classes = CssC.ROW) {
            button(classes = CssC.CALC_BTN) {
                type = ButtonType.button
                onClickFunction = { event -> addNowRow(event) }
                +"Add Now"
            }
        }
    }
}

private fun TagConsumer<HTMLElement>.calculateButton() {
    button(classes = "${CssC.ENGLISH} ${CssC.CALC_BTN}") {
        id = Ids.Results.CALCULATE_BUTTON
        +StringsOfLanguages.ENGLISH.calculate
        onClickFunction = { event ->
            setMaxToCurrentTimeForTimeInputs(findInputContainer(event))
        }
    }
    button(classes = "${CssC.MMENGLISH} ${CssC.CALC_BTN}") {
        id = Ids.Results.CALCULATE_BUTTON
        +StringsOfLanguages.MMENGLISH.calculate
        onClickFunction = { event ->
            setMaxToCurrentTimeForTimeInputs(findInputContainer(event))
        }
    }
    button(classes = "${CssC.URDU} ${CssC.CALC_BTN}") {
        id = Ids.Results.CALCULATE_BUTTON
        +StringsOfLanguages.URDU.calculate
        onClickFunction = { event ->
            setMaxToCurrentTimeForTimeInputs(findInputContainer(event))
        }
    }
    div { id = Ids.Results.MSG }
}

private fun TagConsumer<HTMLElement>.content() {
    div(classes = CssC.INVIS) {
        id = Ids.Results.CONTENT_CONTAINER
        div(classes = CssC.URDU) {
            id = Ids.Results.CONTENT_WRAPPER
            copyBtn(Ids.Results.CONTENT_URDU, CssC.LEFT)
            content(Ids.Results.CONTENT_URDU, Ids.Results.CONTENT_ANSWER)
        }
        div(classes = CssC.ENGLISH) {
            id = Ids.Results.CONTENT_WRAPPER
            copyBtn(Ids.Results.CONTENT_ENGLISH, CssC.RIGHT)
            content(Ids.Results.CONTENT_ENGLISH, Ids.Results.CONTENT_ANSWER)
        }
        div(classes = CssC.MMENGLISH) {
            id = Ids.Results.CONTENT_WRAPPER
            copyBtn(Ids.Results.CONTENT_MMENGLISH, CssC.RIGHT, true)
            content(Ids.Results.CONTENT_MMENGLISH, Ids.Results.CONTENT_ANSWER)
        }
        content(Ids.Results.CONTENT_DATES)
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
