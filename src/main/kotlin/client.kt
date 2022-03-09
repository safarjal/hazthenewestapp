import kotlinx.html.dom.append
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.html.*
import kotlinx.html.dom.prepend
import kotlinx.html.form
import kotlinx.html.js.*
import kotlinx.html.tr
import org.w3c.dom.*
import org.w3c.dom.events.Event
import kotlin.js.Date

private const val IS_DEFAULT_INPUT_MODE_DATE_ONLY = true

object Ids {
    const val HAIZ_INPUT_TABLE = "haiz_input_table"
    const val HAIZ_DURATION_INPUT_TABLE = "haiz_duration_input_table"

    object Row {
        const val INPUT_START_TIME = "input_start_time"
        const val INPUT_END_TIME = "input_end_time"
        const val BUTTONS_CONTAINER = "button_add_before_container"
        const val BUTTON_REMOVE = "button_remove"
        const val BUTTON_ADD_BEFORE = "button_add_before"
    }

    const val CONTENT_CONTAINER = "content_container"
    const val CONTENT = "content"
    const val CONTENT_DATES = "content_dates"
    const val CONTENT_DATES_DIFFERENCE = "content_dates_difference"
    const val DATES_DIFFERENCE_TABLE = "dates_difference_table"
    const val INPUT_CONTAINERS_CONTAINER = "input_containers_container"
    const val INPUT_CONTAINER = "input_container"
    const val COMPARISON_CONTAINER = "comparison_container"
    const val PREGNANCY_CHECKBOX = "pregnancy_checkbox"
    const val MUBTADIA_CHECKBOX = "mubtadia_checkbox"
    const val MUSTABEEN_CHECKBOX = "mustabeen_checkbox"
    const val PREG_START_TIME_INPUT = "preg_start_time_input"
    const val PREG_END_TIME_INPUT = "preg_end_time_input"
    const val AADAT_HAIZ_INPUT = "aadat_haiz_input"
    const val AADAT_TUHR_INPUT = "aadat_tuhr_input"
    const val MAWJOODA_TUHR_INPUT = "mawjooda_tuhr_input"
    const val MAWJOODA_FASID_CHECKBOX = "mawjooda_fasid_checkbox"
    const val AADAT_NIFAS_INPUT = "aadat_nifas_input"
    const val DATE_ONLY_RADIO = "date_only_radio"
    const val DATE_TIME_RADIO = "date_time_radio"
    const val DATE_AND_OR_RADIO = "date_and_or_time"
//    const val INPUTS_CONTAINER_CLONE_BUTTON = "inputs_container_clone_button"
    const val INPUTS_CONTAINER_REMOVE_BUTTON = "inputs_container_remove_button"

    val pregnancyElementIds = listOf(
        PREG_START_TIME_INPUT,
        PREG_END_TIME_INPUT,
        MUSTABEEN_CHECKBOX,
        AADAT_NIFAS_INPUT
    )
}

private val inputsContainersContainer get() = document.getElementById(Ids.INPUT_CONTAINERS_CONTAINER) as HTMLElement
@Suppress("UNCHECKED_CAST")
private val inputsContainers get() = inputsContainersContainer.children.asList() as List<HTMLElement>
private val comparisonContainer get() = document.getElementById(Ids.COMPARISON_CONTAINER) as HTMLElement?

private val contentDatesDifferenceElement get() = document.getElementById(Ids.CONTENT_DATES_DIFFERENCE) as HTMLParagraphElement?
private val datesDifferenceTableElement get() = document.getElementById(Ids.DATES_DIFFERENCE_TABLE) as HTMLElement?
private val languageSelecter get() = document.getElementById("language") as HTMLSelectElement

private val HTMLElement.haizInputTable get() = getChildById(Ids.HAIZ_INPUT_TABLE) as HTMLTableElement

//private val HTMLElement.isDateOnly get() = (getChildById(Ids.DATE_ONLY_RADIO) as HTMLInputElement).checked
private val HTMLElement.isDateTime get() = (getChildById("typePicker") as HTMLSelectElement).value == "dateTime"
private val HTMLElement.isDateOnly get() = (getChildById("typePicker") as HTMLSelectElement).value == "dateOnly"
private val HTMLElement.isDuration get() = (getChildById("typePicker") as HTMLSelectElement).value == "duration"
//private val HTMLElement.isIstimrar get() = (getChildById(Ids.ISTIMRAR_CHECKBOX) as HTMLInputElement).checked
private val HTMLElement.isPregnancy get() = (getChildById(Ids.PREGNANCY_CHECKBOX) as HTMLInputElement).checked
private val HTMLElement.mustabeen get() = (getChildById(Ids.MUSTABEEN_CHECKBOX) as HTMLInputElement).checked
private val HTMLElement.pregStartTime get() = getChildById(Ids.PREG_START_TIME_INPUT) as HTMLInputElement
private val HTMLElement.pregEndTime get() = getChildById(Ids.PREG_END_TIME_INPUT) as HTMLInputElement
private val HTMLElement.aadatHaz get() = getChildById(Ids.AADAT_HAIZ_INPUT) as HTMLInputElement
private val HTMLElement.aadatTuhr get() = getChildById(Ids.AADAT_TUHR_INPUT) as HTMLInputElement
private val HTMLElement.mawjoodaTuhr get() = getChildById(Ids.MAWJOODA_TUHR_INPUT) as HTMLInputElement
private val HTMLElement.isMawjoodaFasid get() = (getChildById(Ids.MAWJOODA_FASID_CHECKBOX) as HTMLInputElement).checked
private val HTMLElement.aadatNifas get() = getChildById(Ids.AADAT_NIFAS_INPUT) as HTMLInputElement
private val HTMLElement.contentContainer get() = getChildById(Ids.CONTENT_CONTAINER)!!
private val HTMLElement.contentElement get() = getChildById(Ids.CONTENT) as HTMLParagraphElement
private val HTMLElement.contentDatesElement get() = getChildById(Ids.CONTENT_DATES) as HTMLParagraphElement
//private val HTMLElement.inputsContainerCloneButton get() =
//    getChildById(Ids.INPUTS_CONTAINER_CLONE_BUTTON) as HTMLButtonElement
private val HTMLElement.inputsContainerRemoveButton get() =
    getChildById(Ids.INPUTS_CONTAINER_REMOVE_BUTTON) as HTMLButtonElement

private var HTMLElement.haizDatesList: List<Entry>?
    get() = (contentDatesElement.asDynamic().haizDatesList as List<Entry>?)?.takeIf { it != undefined }
    set(value) { contentDatesElement.asDynamic().haizDatesList = value }

private val HTMLElement.pregnancyInputs get() = Ids.pregnancyElementIds.map { id ->
    getChildById(id) as HTMLInputElement
}
private val HTMLElement.pregnancyElements get() = getElementsByClassName("preg-checked").asList()
private val englishElements get() = document.getElementsByClassName("english").asList()
private val urduElements get() = document.getElementsByClassName("urdu").asList()

private val HTMLElement.hazInputTableBody: HTMLTableSectionElement
    get() {
        val inputDatesTable = getChildById(Ids.HAIZ_INPUT_TABLE) as HTMLTableElement
        return inputDatesTable.tBodies[0] as HTMLTableSectionElement
    }
private val HTMLElement.haizInputDatesRows: List<HTMLTableRowElement>
    get() {
        @Suppress("UNCHECKED_CAST")
        return hazInputTableBody.rows.asList() as List<HTMLTableRowElement>
    }

private val HTMLTableRowElement.startTimeInput get() = getChildById(Ids.Row.INPUT_START_TIME) as HTMLInputElement
private val HTMLTableRowElement.endTimeInput get() = getChildById(Ids.Row.INPUT_END_TIME) as HTMLInputElement
private val HTMLTableRowElement.buttonsContainer get() = getChildById(Ids.Row.BUTTONS_CONTAINER)!!
private val HTMLTableRowElement.removeButton get() = getChildById(Ids.Row.BUTTON_REMOVE) as HTMLButtonElement
private val HTMLTableRowElement.addBeforeButton get() = getChildById(Ids.Row.BUTTON_ADD_BEFORE) as HTMLButtonElement?

private val HTMLElement.haizTimeInputs get() = haizInputDatesRows.flatMap { row ->
    listOf(row.startTimeInput, row.endTimeInput)
}

private val HTMLElement.timeInputsGroups get() = listOf(listOf(pregStartTime, pregEndTime), haizTimeInputs)

fun main() {
    window.onload = {
        if(askPassword()){
            document.body!!.addInputLayout()
            setupRows(inputsContainers.first())
            document.addEventListener(Events.VISIBILITY_CHANGE, {
                if (!document.isHidden) {
                    setMaxToCurrentTimeForTimeInputs(inputsContainers.first())
                }
            })
            languageSelecter.onchange = {
                for (element in englishElements) element.classList.toggle("lang-invisible", languageSelecter.value == "urdu")
                for (element in urduElements) element.classList.toggle("lang-invisible", languageSelecter.value == "english")
                document.body!!.classList.toggle("rtl", languageSelecter.value == "urdu")
            }
        }else{
            askPassword()
        }
    }
}

fun askPassword():Boolean{
    val pass1 = "786"
    val password = window.prompt("${StringsOfLanguages.ENGLISH.warningOnlyAuthorizedPersonnel}\n\n" +
            "${StringsOfLanguages.URDU.warningOnlyAuthorizedPersonnel}\n\n" +
            "${StringsOfLanguages.URDU.passwordRequired}\n\n", "")
    if (pass1 == password) {
        return true
    }
    else return askPassword()
}

fun Node.addInputLayout() {
    append {
        div {
            id = Ids.INPUT_CONTAINERS_CONTAINER
            inputFormDiv()
        }
        div {
            style = Styles.NEW_ROW
        }
    }
}

private fun removeInputsContainer(inputsContainer: HTMLElement) {
    inputsContainer.remove()
    comparisonContainer?.remove()
    inputsContainers.singleOrNull()?.inputsContainerRemoveButton?.remove()
}

//private fun cloneInputsContainer(inputsContainerToCopyFrom: HTMLElement) {
//    comparisonContainer?.remove()
//    if (inputsContainers.size == 1) {
//        addRemoveInputsContainerButton(inputsContainerToCopyFrom)
//    }
//    val clonedInputsContainer = inputsContainerToCopyFrom.after {
//        inputFormDiv(inputsContainerToCopyFrom)
//    }.single()
//    setupFirstRow(clonedInputsContainer)
//}

//private fun addRemoveInputsContainerButton(inputContainer: HTMLElement) {
//    inputContainer.inputsContainerCloneButton.before {
//        button(type = ButtonType.button, classes = "minus") {
//            +"\u274C"
//            id = Ids.INPUTS_CONTAINER_REMOVE_BUTTON
//            style = "float: right"
//            onClickFunction = { event ->
//                removeInputsContainer(findInputContainer(event))
//            }
//        }
//    }
//}

private fun addCompareButtonIfNeeded() {
    if (comparisonContainer != null ||
        inputsContainers.size < 2 ||
        inputsContainers.any { it.haizDatesList == null }
    ) return

    inputsContainersContainer.after {
        div {
            id = Ids.COMPARISON_CONTAINER
            button(type = ButtonType.button) {
                +"Calculate difference"
                style = "margin: 0.2rem auto; display: block;"
                onClickFunction = { compareResults() }
            }
            content {
                id = Ids.CONTENT_DATES_DIFFERENCE
            }
            table {
                id = Ids.DATES_DIFFERENCE_TABLE
            }
        }
    }
}

private fun TagConsumer<HTMLElement>.inputFormDiv(inputContainerToCopyFrom: HTMLElement? = null) {
    div {
        id = Ids.INPUT_CONTAINER
//        style = "width:${FORM_WIDTH_DATE_ONLY}px; border:${FORM_BORDER}px; padding:${FORM_PADDING}px;"
        if (inputContainerToCopyFrom != null) {
            removeInputsContainerButton()
        }
        classes = setOf(Ids.INPUT_CONTAINER, "date_only")
//        addInputsContainerButton()
        inputForm(inputContainerToCopyFrom)
        content()
    }
}

//private fun TagConsumer<HTMLElement>.addInputsContainerButton() {
//    inputsContainerAddRemoveButton {
//        +"Clone"
//        classes = setOf("plus", "clone")
//        id = Ids.INPUTS_CONTAINER_CLONE_BUTTON
//        onClickFunction = { event ->
//            cloneInputsContainer(findInputContainer(event))
//        }
//    }
//}

private fun TagConsumer<HTMLElement>.removeInputsContainerButton() {
    inputsContainerAddRemoveButton {
        +"\u274C"
        classes = setOf("minus")
        id = Ids.INPUTS_CONTAINER_REMOVE_BUTTON
        onClickFunction = { event ->
            removeInputsContainer(findInputContainer(event))
        }
    }
}

private fun TagConsumer<HTMLElement>.inputsContainerAddRemoveButton(block : BUTTON.() -> Unit = {}) {
    button(type = ButtonType.button) {
        style = "float: right"
        block()
    }
}

private fun TagConsumer<HTMLElement>.content() {
    div(classes = "invisible") {
        id = Ids.CONTENT_CONTAINER
        content {
            id = Ids.CONTENT
        }
        hr()
        content {
            id = Ids.CONTENT_DATES
        }
        hr()
    }
}

private fun TagConsumer<HTMLElement>.inputForm(inputContainerToCopyFrom: HTMLElement?) {
    form(action = "javascript:void(0);") {
        div(classes = "label-input") {
            typeConfigurationSelectDropdown(inputContainerToCopyFrom)
            aadatInputs(inputContainerToCopyFrom)
//            mubtadiaCheckBox(inputContainerToCopyFrom)
//            pregnancyCheckBox(inputContainerToCopyFrom)
            mustabeenCheckBox(inputContainerToCopyFrom)
            pregnancyStartTimeInput(inputContainerToCopyFrom)
            pregnancyEndTimeInput(inputContainerToCopyFrom)
        }
        hr()
        haizDatesInputTable(inputContainerToCopyFrom)
//        istimrarCheckBox(inputContainerToCopyFrom)
        calculateButton()
        hr()
        onSubmitFunction = { event ->
            parseEntries(findInputContainer(event))
        }
    }
}

private fun FlowContent.typeConfigurationSelectDropdown(inputContainerToCopyFrom: HTMLElement?) {
    val isDateOnly = inputContainerToCopyFrom?.isDateOnly ?: IS_DEFAULT_INPUT_MODE_DATE_ONLY
    div(classes = "row") {
        label(classes = "english lang-invisible") {
            htmlFor = Ids.DATE_TIME_RADIO
            +"Type of input: "
        }
        label(classes = "urdu") {
            htmlFor = Ids.DATE_TIME_RADIO
            +"تاریخ و وقت"
        }
        p {
            span {  }
        }
        select {
            id = "typePicker"
            onChangeFunction = { event ->
                onClickTypeConfigurationSelectDropdown(findInputContainer(event))
//                if((event.currentTarget as HTMLSelectElement).value in setOf("dateOnly", "dateTime")) {
//                    onClickDateConfigurationRadioButton(findInputContainer(event))
//                } else { onClickDurationRadioButton(findInputContainer(event)) }
            }
            option(classes = "english lang-invisible") {
                value = "dateOnly"
                +"Date only"
            }
            option(classes = "urdu") {
                selected = true
                value = "dateOnly"
                +"صرف تاریخیں"
            }
            option(classes = "english lang-invisible") {
                value = "dateTime"
                +"Date and Time: "
            }
            option(classes = "urdu") {
                value = "dateTime"
                +"تاریخ اور وقت"
            }
            option(classes = "english lang-invisible") {
                value = "duration"
                +"Duration: "
            }
            option(classes = "urdu") {
                value = "duration"
                +"Duration: "
            }
        }
//        div(classes = "flex") {
//            radioInput(classes = "zero-flex") {
//                id = Ids.DATE_TIME_RADIO
//                name = Ids.DATE_AND_OR_RADIO
//                checked = !isDateOnly
//                onChangeFunction = { event -> onClickDateConfigurationRadioButton(findInputContainer(event)) }
//            }
//            label(classes = "english lang-invisible") {
//                htmlFor = Ids.DATE_TIME_RADIO
//                +"Date and Time"
//            }
//            label(classes = "urdu") {
//                htmlFor = Ids.DATE_TIME_RADIO
//                +"تاریخ اور وقت"
//            }
//        }
//        div(classes = "flex") {
//            radioInput(classes = "zero-flex") {
//                id = Ids.DATE_ONLY_RADIO
//                name = Ids.DATE_AND_OR_RADIO
//                checked = isDateOnly
//                onChangeFunction = { event -> onClickDateConfigurationRadioButton(findInputContainer(event)) }
//            }
//            label(classes = "english lang-invisible") {
//                htmlFor = Ids.DATE_ONLY_RADIO
//                +"Date only"
//            }
//            label(classes = "urdu") {
//                htmlFor = Ids.DATE_ONLY_RADIO
//                +"صرف تاریخیں"
//            }
//        }
    }
}

private fun FlowContent.aadatInputs(inputContainerToCopyFrom: HTMLElement?) {
    div(classes = "row") {
        label(classes = "english lang-invisible") {
            htmlFor = Ids.AADAT_HAIZ_INPUT
            +(StringsOfLanguages.ENGLISH.haizAadat)
        }
        label(classes = "urdu") {
            htmlFor = Ids.AADAT_HAIZ_INPUT
            +(StringsOfLanguages.URDU.haizAadat)
        }
        input(classes = "aadat") {
            id = Ids.AADAT_HAIZ_INPUT
            value = inputContainerToCopyFrom?.aadatHaz?.value.orEmpty()
            onInputFunction = { event -> (event.currentTarget as HTMLInputElement).validateAadat(3..10) }
        }
    }
    div(classes = "row") {
        label(classes = "english lang-invisible") {
            htmlFor = Ids.AADAT_TUHR_INPUT
            +(StringsOfLanguages.ENGLISH.tuhrAadat)
        }
        label(classes = "urdu") {
            htmlFor = Ids.AADAT_TUHR_INPUT
            +(StringsOfLanguages.URDU.tuhrAadat)
        }
        input(classes = "aadat") {
            id = Ids.AADAT_TUHR_INPUT
            value = inputContainerToCopyFrom?.aadatTuhr?.value.orEmpty()
            onInputFunction = { event -> (event.currentTarget as HTMLInputElement).validateAadat(15..6 * 30) }
        }
    }
    div(classes = "row") {
        label(classes = "english lang-invisible") {
            htmlFor = Ids.MAWJOODA_TUHR_INPUT
            +(StringsOfLanguages.ENGLISH.mawjoodahTuhr)
        }
        label(classes = "urdu") {
            htmlFor = Ids.MAWJOODA_TUHR_INPUT
            +(StringsOfLanguages.URDU.mawjoodahTuhr)
        }
        input(classes = "aadat") {
            id = Ids.MAWJOODA_TUHR_INPUT
            value = inputContainerToCopyFrom?.mawjoodaTuhr?.value.orEmpty()
            onInputFunction = { event -> (event.currentTarget as HTMLInputElement).validateAadat(15..10000) }
            //TODO: Find out how to do infinity, rather than 10000
        }
        div {
            id = "fasid"
            label(classes = "english lang-invisible") {
                htmlFor = Ids.MAWJOODA_FASID_CHECKBOX
                +(StringsOfLanguages.ENGLISH.faasid)
            }
            label(classes = "urdu") {
                htmlFor = Ids.MAWJOODA_FASID_CHECKBOX
                +(StringsOfLanguages.URDU.faasid)
            }
            input(type = InputType.checkBox) {
                id = Ids.MAWJOODA_FASID_CHECKBOX
                checked = false
            }
        }
    }
    pregnancyCheckBox(inputContainerToCopyFrom)
    div(classes = "row preg-checked invisible") {
        label {
            htmlFor = Ids.AADAT_NIFAS_INPUT
            classes = setOfNotNull(
                "preg-checked",
                "english",
                "lang-invisible",
                if (inputContainerToCopyFrom?.isPregnancy != true) "invisible" else null,
            )
            +(StringsOfLanguages.ENGLISH.nifasAadat)
        }
        label {
            htmlFor = Ids.AADAT_NIFAS_INPUT
            classes = setOfNotNull(
                "preg-checked",
                "urdu",
                if (inputContainerToCopyFrom?.isPregnancy != true) "invisible" else null,
            )
            +(StringsOfLanguages.URDU.nifasAadat)
        }
        input {
            id = Ids.AADAT_NIFAS_INPUT
            classes = setOfNotNull(
                "preg-checked",
                "aadat",
                if (inputContainerToCopyFrom?.isPregnancy != true) "invisible" else null
            )
            step = "any"
            required = false
            disabled = inputContainerToCopyFrom?.isPregnancy != true
            value = inputContainerToCopyFrom?.aadatNifas?.value.orEmpty()
            onInputFunction = { event -> (event.currentTarget as HTMLInputElement).validateAadat(1..40) }
        }
    }
}

private fun HTMLInputElement.validateAadat(validityRange: ClosedRange<Int>) {
    value = value.replace("[^0-9:]".toRegex(), "")
    val doubleValidityRange = validityRange.start.toDouble()..validityRange.endInclusive.toDouble()
    setCustomValidity(try {
        val days = (parseDays(value)?.div(MILLISECONDS_IN_A_DAY))?.toDouble()
        require(days == null || days in doubleValidityRange) { "Aadat is incorrect" }
        ""
    } catch (e: IllegalArgumentException) {
        e.message ?: "Aadat is incorrect"
    })
}

private fun FlowContent.pregnancyCheckBox(inputContainerToCopyFrom: HTMLElement?) {
    div(classes = "row") {
        div {
            label(classes = "english lang-invisible") {
                htmlFor = Ids.PREGNANCY_CHECKBOX
                +(StringsOfLanguages.ENGLISH.nifas)
            }
            label(classes = "urdu") {
                htmlFor = Ids.PREGNANCY_CHECKBOX
                +(StringsOfLanguages.URDU.nifas)
            }
            checkBoxInput {
                id = Ids.PREGNANCY_CHECKBOX
                checked = inputContainerToCopyFrom?.isPregnancy == true
                onChangeFunction = { event ->
                    val isChecked = (event.currentTarget as HTMLInputElement).checked
                    for (pregnancyElement in findInputContainer(event).pregnancyInputs) {
                            pregnancyElement.visibility = isChecked
                            pregnancyElement.disabled = !isChecked
                    }
                    for (pregnancyElement in findInputContainer(event).pregnancyElements) {
                            pregnancyElement.visibility = isChecked
                    }
                }
            }
        }
    }
}
//private fun FlowContent.mubtadiaCheckBox(inputContainerToCopyFrom: HTMLElement?) {
//    label() {
//        htmlFor = Ids.MUBTADIA_CHECKBOX
//        +"Mubtadia"
//    }
//    checkBoxInput() {
//        id = Ids.MUBTADIA_CHECKBOX
//        checked = inputContainerToCopyFrom?.isPregnancy == true
//    }
//}

private fun FlowContent.mustabeenCheckBox(inputContainerToCopyFrom: HTMLElement?) {
    div(classes = "row preg-checked invisible") {
        div {
            label {
                htmlFor = Ids.MUSTABEEN_CHECKBOX
                classes = setOfNotNull(
                    "preg-checked",
                    "english",
                    "lang-invisible",
                    if (inputContainerToCopyFrom?.isPregnancy != true) "invisible" else null,
                )
                +StringsOfLanguages.ENGLISH.mustabeenUlKhilqa
            }
            label {
                htmlFor = Ids.MUSTABEEN_CHECKBOX
                classes = setOfNotNull(
                    "preg-checked",
                    "urdu",
                    if (inputContainerToCopyFrom?.isPregnancy != true) "invisible" else null,
                )
                +StringsOfLanguages.URDU.mustabeenUlKhilqa
            }
            checkBoxInput {
                id = Ids.MUSTABEEN_CHECKBOX
                classes = setOfNotNull(
                    "preg-checked",
                    if (inputContainerToCopyFrom?.isPregnancy != true) "invisible" else null
                )
                checked = inputContainerToCopyFrom == null || inputContainerToCopyFrom.mustabeen
                checked = inputContainerToCopyFrom?.mustabeen != false
                disabled = inputContainerToCopyFrom?.isPregnancy != true
            }
        }
    }
}

private fun FlowContent.pregnancyStartTimeInput(inputContainerToCopyFrom: HTMLElement?) {
    div(classes = "row preg-checked invisible") {
        div {
            label {
                htmlFor = Ids.PREG_START_TIME_INPUT
                classes = setOfNotNull(
                    "preg-checked",
                    "english",
                    "lang-invisible",
                    if (inputContainerToCopyFrom?.isPregnancy != true) "invisible" else null,
                )
                +StringsOfLanguages.ENGLISH.pregnancyStartTime
            }
            label {
                htmlFor = Ids.PREG_START_TIME_INPUT
                classes = setOfNotNull(
                    "preg-checked",
                    "urdu",
                    if (inputContainerToCopyFrom?.isPregnancy != true) "invisible" else null,
                )
                +StringsOfLanguages.URDU.pregnancyStartTime
            }
            pregnancyTimeInput(inputContainerToCopyFrom) {
                classes = setOfNotNull(
                    "preg-checked",
                    if (inputContainerToCopyFrom?.isPregnancy != true) "invisible" else null
                )
                id = Ids.PREG_START_TIME_INPUT
                onChangeFunction = { event ->
                    findInputContainer(event).pregEndTime.min = (event.currentTarget as HTMLInputElement).value
                }
            }
        }
    }
}

private fun FlowContent.pregnancyEndTimeInput(inputContainerToCopyFrom: HTMLElement?) {
    div(classes = "row preg-checked invisible") {
        div {
            label {
                htmlFor = Ids.PREG_END_TIME_INPUT
                classes = setOfNotNull(
                    "preg-checked",
                    "english",
                    "lang-invisible",
                    if (inputContainerToCopyFrom?.isPregnancy != true) "invisible" else null,
                )
                +StringsOfLanguages.ENGLISH.birthMiscarrriageTime
            }
            label {
                htmlFor = Ids.PREG_END_TIME_INPUT
                classes = setOfNotNull(
                    "preg-checked",
                    "urdu",
                    if (inputContainerToCopyFrom?.isPregnancy != true) "invisible" else null,
                )
                +StringsOfLanguages.URDU.birthMiscarrriageTime
            }
            pregnancyTimeInput(inputContainerToCopyFrom) {
                classes = setOfNotNull(
                    "preg-checked",
                    if (inputContainerToCopyFrom?.isPregnancy != true) "invisible" else null
                )
                id = Ids.PREG_END_TIME_INPUT
                onChangeFunction = { event ->
                    findInputContainer(event).pregStartTime.max = (event.currentTarget as HTMLInputElement).value
                }
            }
        }
    }
}

private fun FlowContent.pregnancyTimeInput(inputContainerToCopyFrom: HTMLElement?, block: INPUT.() -> Unit = {}) {
    if (inputContainerToCopyFrom != null) {
        timeInput(inputContainerToCopyFrom) {
            disabled = !inputContainerToCopyFrom.isPregnancy
            block()
        }
    } else {
        timeInput(IS_DEFAULT_INPUT_MODE_DATE_ONLY) {
            disabled = true
//            max = currentTimeString(IS_DEFAULT_INPUT_MODE_DATE_ONLY)
            block()
        }
    }
}

private fun FlowContent.calculateButton() {
    button(classes = "english lang-invisible calc-btn") {
        +StringsOfLanguages.ENGLISH.calculate
        onClickFunction = { event -> setMaxToCurrentTimeForTimeInputs(findInputContainer(event)) }
    }
    button(classes = "urdu calc-btn") {
        +StringsOfLanguages.URDU.calculate
        onClickFunction = { event -> setMaxToCurrentTimeForTimeInputs(findInputContainer(event)) }
    }
}

private fun TagConsumer<HTMLElement>.content(block : P.() -> Unit = {}) {
    p {
        style = "white-space: pre-wrap;"
        block()
    }
}

private fun TagConsumer<HTMLElement>.haizDatesInputTable(inputContainerToCopyFrom: HTMLElement?) {
    table {
        id = Ids.HAIZ_INPUT_TABLE
        thead {
            tr {
                th(classes = "english lang-invisible") { +StringsOfLanguages.ENGLISH.startTime }
                th(classes = "english lang-invisible") { +StringsOfLanguages.ENGLISH.endTime }
                th(classes = "urdu") { +StringsOfLanguages.URDU.startTime }
                th(classes = "urdu") { +StringsOfLanguages.URDU.endTime }
                th {addBeforeButton()}
            }
        }
        tbody {
            if (inputContainerToCopyFrom != null) {
                for (inputDateRow in inputContainerToCopyFrom.haizInputDatesRows) {
                    inputRow(inputContainerToCopyFrom, inputDateRow.startTimeInput, inputDateRow.endTimeInput)
                }
            } else {
                inputRow(
                    isDateOnlyLayout = IS_DEFAULT_INPUT_MODE_DATE_ONLY,
                    minTimeInput = "",
                    maxTimeInput = ""//currentTimeString(IS_DEFAULT_INPUT_MODE_DATE_ONLY)
                )
            }
        }
    }
}

private fun TagConsumer<HTMLElement>.haizDurationInputTable(inputContainerToCopyFrom: HTMLElement?) {
    table {
        id = Ids.HAIZ_DURATION_INPUT_TABLE
        thead {
            tr {
                th(classes = "english lang-invisible") { +"Duration" }
                th(classes = "english lang-invisible") { +"Dam/Tuhr" }
                th(classes = "urdu") { +"DurationU" }
                th(classes = "urdu") { +"Dam w Tuhr" }
                th {durationAddBeforeButton()}
            }
        }
        tbody {
            durationInputRow(false)
        }
    }
}

private fun TagConsumer<HTMLElement>.inputRow(isDateOnlyLayout: Boolean, minTimeInput: String, maxTimeInput: String) {
    tr {
        td {
            timeInput(isDateOnlyLayout, minTimeInput, maxTimeInput, indexWithinRow = 0) {
                id = Ids.Row.INPUT_START_TIME
            }
        }
        td {
            timeInput(isDateOnlyLayout, minTimeInput, maxTimeInput, indexWithinRow = 1) {
                id = Ids.Row.INPUT_END_TIME
            }
        }
        addRemoveButtonsTableData()
    }
}

private fun TagConsumer<HTMLElement>.durationInputRow(lastWasDam: Boolean) {
    val urdu = languageSelecter.value == "urdu"
    tr {
        td {
            input(type = InputType.number)
        }
        td {
            select {
                option(classes = "english lang-invisible") {
                    selected = !urdu && !lastWasDam
                    value = "dam"
                    + "Dam"
                }
                option(classes = "english lang-invisible") {
                    selected = !urdu && lastWasDam
                    value = "tuhr"
                    + "Tuhr"
                }
                option(classes = "urdu") {
                    selected = urdu && !lastWasDam
                    value = "dam"
                    + "Dam"
                }
                option(classes = "urdu") {
                    selected = urdu && lastWasDam
                    value = "tuhr"
                    + "Tuhr"
                }
            }
        }
        addRemoveButtonsDurationData()
    }
}

private fun TagConsumer<HTMLElement>.inputRow(
    inputContainerToCopyFrom: HTMLElement,
    startTimeInputToCopyFrom: HTMLInputElement,
    endTimeInputToCopyFrom: HTMLInputElement
) {
    tr {
        td {
            timeInput(inputContainerToCopyFrom, startTimeInputToCopyFrom, indexWithinRow = 0) {
                id = Ids.Row.INPUT_START_TIME
            }
        }
        td {
            timeInput(inputContainerToCopyFrom, endTimeInputToCopyFrom, indexWithinRow = 1) {
                id = Ids.Row.INPUT_END_TIME
            }
        }
        addRemoveButtonsTableData()
    }
}

private fun TR.addRemoveButtonsTableData() {
    td {
        id = Ids.Row.BUTTONS_CONTAINER
        addButton()
        removeButton()
    }
}

private fun TR.addRemoveButtonsDurationData() {
    td {
        id = Ids.Row.BUTTONS_CONTAINER
        durationAddButton()
        durationRemoveButton()
    }
}

private fun FlowContent.timeInput(
    isDateOnlyLayout: Boolean,
    minTimeInput: String,
    maxTimeInput: String,
    indexWithinRow: Int,
    block: INPUT.() -> Unit = {}
) {
    timeInput(isDateOnlyLayout, minTimeInput, maxTimeInput) {
        onChangeFunction = { event -> setMinMaxForTimeInputsOnInput(event, indexWithinRow) }
        block()
    }
}

private fun FlowContent.timeInput(
    inputContainerToCopyFrom: HTMLElement,
    timeInputToCopyFrom: HTMLInputElement,
    indexWithinRow: Int,
    block: INPUT.() -> Unit = {}
) {
    timeInput(inputContainerToCopyFrom, timeInputToCopyFrom) {
        onChangeFunction = { event -> setMinMaxForTimeInputsOnInput(event, indexWithinRow) }
        block()
    }
}

private fun FlowContent.timeInput(
    isDateOnlyLayout: Boolean,
    minTimeInput: String,
    maxTimeInput: String,
    block: INPUT.() -> Unit = {}
) {
    timeInput(isDateOnlyLayout) {
        min = minTimeInput
        max = maxTimeInput
        block()
    }
}

private fun FlowContent.timeInput(
    inputContainerToCopyFrom: HTMLElement,
    timeInputToCopyFrom: HTMLInputElement? = null,
    block: INPUT.() -> Unit = {}
) {
    timeInput(inputContainerToCopyFrom.isDateOnly) {
        block()
        @Suppress("NAME_SHADOWING")
        val timeInputToCopyFrom = timeInputToCopyFrom ?: inputContainerToCopyFrom.getChildById(id) as HTMLInputElement
        value = timeInputToCopyFrom.value
        min = timeInputToCopyFrom.min
        max = timeInputToCopyFrom.max
    }
}

private fun FlowContent.timeInput(
    isDateOnlyLayout: Boolean,
    block: INPUT.() -> Unit = {}
) {
    customDateTimeInput(isDateOnlyLayout) {
        required = true
        onClickFunction = { event ->
            setMaxToCurrentTimeForTimeInputs(findInputContainer(event))
        }
        block()
    }
}

private fun FlowContent.removeButton() {
    button(type = ButtonType.button, classes = "minus") {
        +"\u274C"
        title = "Remove"
        id = Ids.Row.BUTTON_REMOVE
        onClickFunction = { event ->
            val row = findRow(event)
            val inputContainer = findInputContainer(event)
            updateMinMaxForTimeInputsBeforeRemovingRow(inputContainer, row.rowIndexWithinTableBody)
            row.remove()
            setupFirstRow(inputContainer)
        }
    }
}

private fun FlowContent.durationRemoveButton() {
    button(type = ButtonType.button, classes = "minus") {
        +"\u274C"
        title = "Remove"
        id = Ids.Row.BUTTON_REMOVE
        onClickFunction = { event ->
            val row = findRow(event)
            val inputContainer = findInputContainer(event)
            row.remove()
            setupFirstRow(inputContainer)
        }
    }
}

private fun FlowContent.addButton() {
    button(type = ButtonType.button, classes = "plus") {
        +"\u2795"
        title = "Add"
        onClickFunction = { event ->
            val row = findRow(event)
            val inputContainer = findInputContainer(event)
            row.after {
                inputRow(
                    inputContainer.isDateOnly,
                    minTimeInput = row.endTimeInput.run { value.takeUnless(String::isEmpty) ?: min },
                    maxTimeInput = row.endTimeInput.max
                )
            }
            setupRows(inputContainer)
        }
    }
}

private fun FlowContent.durationAddButton() {
    button(type = ButtonType.button, classes = "plus") {
        +"\u2795"
        title = "Add"
        onClickFunction = { event ->
            val row = findRow(event)
            val inputContainer = findInputContainer(event)
            row.after {
                durationInputRow(true)
            }
            setupRows(inputContainer)
        }
    }
}

private fun TagConsumer<HTMLElement>.durationAddBeforeButton() {
    button(type = ButtonType.button, classes = "plus") {
        +"\u2795 \u25B2"
        title = "Add at Start"
        id = Ids.Row.BUTTON_ADD_BEFORE
        onClickFunction = { event ->
            val inputContainer = findInputContainer(event)
            val row = inputContainer.hazInputTableBody.firstChild as HTMLTableRowElement

            inputContainer.hazInputTableBody.prepend { inputRow(
                inputContainer.isDateOnly,
                minTimeInput = "",
                maxTimeInput = row.startTimeInput.run { value.takeUnless(String::isEmpty) ?: max }
            ) }
            setupRows(inputContainer)
        }
    }
}

private fun TagConsumer<HTMLElement>.addBeforeButton() {
    button(type = ButtonType.button, classes = "plus") {
        +"\u2795 \u25B2"
        title = "Add at Start"
        id = Ids.Row.BUTTON_ADD_BEFORE
        onClickFunction = { event ->
            val inputContainer = findInputContainer(event)
            val row = inputContainer.hazInputTableBody.firstChild as HTMLTableRowElement

            inputContainer.hazInputTableBody.prepend { inputRow(
                inputContainer.isDateOnly,
                minTimeInput = "",
                maxTimeInput = row.startTimeInput.run { value.takeUnless(String::isEmpty) ?: max }
            ) }
            setupRows(inputContainer)
        }
    }
}

private fun findInputContainer(event: Event) =
    (event.currentTarget as Element).getAncestor<HTMLElement> { it.id.startsWith(Ids.INPUT_CONTAINER)}!!
private fun findRow(event: Event) = (event.currentTarget as Element).getAncestor<HTMLTableRowElement>()!!

private fun setupRows(inputContainer: HTMLElement) {
    setMaxToCurrentTimeForTimeInputs(inputContainer)
    setupFirstRow(inputContainer)
}

private fun setupFirstRow(inputContainer: HTMLElement) {
    updateRemoveButtonDisabledStateForFirstRow(inputContainer)
//    ensureAddFirstButtonOnlyShownInFirstRow(inputContainer)
}

private fun updateRemoveButtonDisabledStateForFirstRow(inputContainer: HTMLElement) {
    val inputDatesRows = inputContainer.haizInputDatesRows
    inputDatesRows.first().removeButton.visibility = inputDatesRows.size != 1
    inputDatesRows.getOrNull(1)?.removeButton?.visibility = true
}

//private fun ensureAddFirstButtonOnlyShownInFirstRow(inputContainer: HTMLElement) {
//    for ((index, row) in inputContainer.haizInputDatesRows.withIndex()) {
//        if (index > 0) {
//            row.addBeforeButton?.remove()
//        } else if (row.addBeforeButton == null) {
//            row.buttonsContainer.append { addBeforeButton() }
//        }
//    }
//}

private fun setMaxToCurrentTimeForTimeInputs(inputContainer: HTMLElement) {
    val currentTime = currentTimeString(inputContainer.isDateOnly)
    for (timeInputsGroup in inputContainer.timeInputsGroups) {
        for (timeInput in timeInputsGroup.asReversed()) {
//            timeInput.max = currentTime
            if (timeInput.value.isNotEmpty()) break
        }
    }
}

private fun setMinMaxForTimeInputsOnInput(event: Event, indexWithinRow: Int) {
    setMinMaxForTimeInputsOnInput(
        findInputContainer(event),
        (findRow(event).rowIndexWithinTableBody * 2) + indexWithinRow
    )
}

private fun setMinMaxForTimeInputsOnInput(inputContainer: HTMLElement, index: Int) {
    val timeInputs = inputContainer.haizTimeInputs
    val timeInput = timeInputs[index]
    val min: String
    val max: String
    val previousTimeInputs: List<HTMLInputElement>
    val nextTimeInputs: List<HTMLInputElement>
    val value = timeInput.value
    if (value.isEmpty()) {
        min = timeInput.min
        max = timeInput.max
        previousTimeInputs = timeInputs.take(index + 1)
        nextTimeInputs = timeInputs.drop(index)
    } else {
        min = value
        max = value
        previousTimeInputs = timeInputs.take(index)
        nextTimeInputs = timeInputs.drop(index + 1)
    }
    for (previousTimeInput in previousTimeInputs.asReversed()) {
        previousTimeInput.max = max
        if (previousTimeInput.value.isNotEmpty()) break
    }
    for (nextTimeInput in nextTimeInputs) {
        nextTimeInput.min = min
        if (nextTimeInput.value.isNotEmpty()) break
    }
}

private fun updateMinMaxForTimeInputsBeforeRemovingRow(inputContainer: HTMLElement, rowIndex: Int) {
    val timeInputs = inputContainer.haizTimeInputs
    val startDateIndex = rowIndex * 2
    val endDateIndex = startDateIndex + 1
    val min = timeInputs[startDateIndex].min
    val max = timeInputs[endDateIndex].max
    val previousTimeInputs = timeInputs.take(startDateIndex)
    val nextTimeInputs = timeInputs.drop(endDateIndex + 1)
    for (previousTimeInput in previousTimeInputs.asReversed()) {
        previousTimeInput.max = max
        if (previousTimeInput.value.isNotEmpty()) break
    }
    for (nextTimeInput in nextTimeInputs) {
        nextTimeInput.min = min
        if (nextTimeInput.value.isNotEmpty()) break
    }
}

private fun onClickTypeConfigurationSelectDropdown(inputContainer: HTMLElement) {
    val isDateOnly = inputContainer.isDateOnly
    val isDateTime = inputContainer.isDateTime
    val isDuration = inputContainer.isDuration
    if (isDateOnly || isDateTime) {
        disableDateTable(inputContainer, false)
        inputContainer.haizInputTable.visibility = true
        for (timeInput in inputContainer.timeInputsGroups.flatten()) {
            val newValue = convertInputValue(timeInput.value, isDateOnly)
            val newMin = convertInputValue(timeInput.min, isDateOnly)
            val newMax = convertInputValue(timeInput.max, isDateOnly)

            val dateInputType = if (isDateOnly) InputType.date else InputType.dateTimeLocal
            timeInput.type = dateInputType.realValue

            timeInput.value = newValue
            timeInput.min = newMin
            timeInput.max = newMax
        }

        inputContainer.classList.toggle("date_only", isDateOnly)
        inputContainer.classList.toggle("date_and_time", !isDateOnly)
        inputContainer.classList.toggle("duration", false)

        if (!isDateOnly) {
            setMaxToCurrentTimeForTimeInputs(inputContainer)
        }
    } else if (isDuration) {
        for (timeInput in inputContainer.timeInputsGroups.flatten()) {
            val newValue = convertInputValue(timeInput.value, isDateOnly)
            val newMin = convertInputValue(timeInput.min, isDateOnly)
            val newMax = convertInputValue(timeInput.max, isDateOnly)

            val dateInputType = if (isDateOnly) InputType.date else InputType.dateTimeLocal
            timeInput.type = dateInputType.realValue

            timeInput.value = newValue
            timeInput.min = newMin
            timeInput.max = newMax
        }
        inputContainer.classList.toggle("date_only", false)
        inputContainer.classList.toggle("date_and_time", false)
        inputContainer.classList.toggle("duration", true)
        inputContainer.haizInputTable.visibility = false
        disableDateTable(inputContainer, true)
    }
}

private fun disableDateTable(inputContainer: HTMLElement, disable: Boolean) {
    for (timeInput in inputContainer.timeInputsGroups) {
        for (input in timeInput) {
            input.disabled = disable
        }
    }
}

private fun parseEntries(inputContainer: HTMLElement) {

    with(inputContainer) {
        val entries = haizInputDatesRows.map { row ->
            Entry(
                startTime = Date(row.startTimeInput.valueAsNumber),
                endTime = Date(row.endTimeInput.valueAsNumber)
            )
        }
        @Suppress("UnsafeCastFromDynamic")
        val output = handleEntries(
            entries,
            parseDays(aadatHaz.value),
            parseDays(aadatTuhr.value),
            parseDays(mawjoodaTuhr.value),
            isMawjoodaFasid,
            isDateOnly,
            isPregnancy,
            Pregnancy(
                Date(pregStartTime.valueAsNumber),
                Date(pregEndTime.valueAsNumber),
                parseDays(aadatNifas.value),
                mustabeen
            ),
            false,
            languageSelecter.value
        )
        contentContainer.visibility = true
        if (languageSelecter.value == "english") {
            println("1")
            contentElement.innerHTML = output.englishText
            contentElement.classList.toggle("rtl", false)
        } else {
            println("2")
            contentElement.innerHTML = output.urduText
            contentElement.classList.toggle("rtl", true)
        }
        contentDatesElement.innerHTML = output.haizDatesText
        haizDatesList = output.hazDatesList
    }
    addCompareButtonIfNeeded()
}

private fun compareResults() {
    val listOfLists = inputsContainers.map { it.haizDatesList!! }
    val str = getDifferenceFromMultiple(listOfLists)
    contentDatesDifferenceElement!!.innerHTML = str
    val output = generatInfoForCompareTable(listOfLists.toMutableList())
    drawCompareTable(output.headerList,output.listOfColorsOfDaysList, output.resultColors)
}

fun drawCompareTable(headerList:List<Date>, listOfColorsOfDaysList: List<List<Int>>, resultColors: List<Int>){
    val datesDifferenceTableElement = datesDifferenceTableElement!!
    datesDifferenceTableElement.style.width = "${headerList.size*30 +15}px"
    datesDifferenceTableElement.replaceChildren {
        div { id = "tHead"
            style = Styles.TABLE_HEAD_STYLE
            div { id = "monthRow"
                style =Styles.TABLE_ROW_STYLE
                for (header in headerList) {
                    val date = header.getDate()
                    div { id = "cello"
                        style = Styles.TABLE_CELL_STYLE
                        if (date == 1) {
                            +MonthNames[header.getMonth()]
                        }
                    }
                }
            }
            div{
                style = Styles.NEW_ROW
            }
            div { id = "datesRow"
                style = Styles.TABLE_ROW_STYLE
                for (i in headerList.indices) {
                    val header = headerList[i]
                    val date = header.getDate().toString()

                    div { id = "cello"
                        style =Styles.TABLE_CELL_STYLE
                        +date
                    }
                }
            }
        }
        div{
            style = Styles.NEW_ROW
        }
        div { id = "tBody"
            style = Styles.TABLE_BODY_STYLE
            div{
                style = Styles.NEW_ROW
            }
            div { id = "emptyRow"
                style=Styles.TABLE_ROW_STYLE
                div{
                    id = "emptyHalfCellTopRow"
                    style = Styles.EMPTY_HALF_CELL_STYLE
                }
                for (day in resultColors){
                    div{
                        id = "emptyCellTopRow"
                        style = Styles.EMPTY_CELL_STYLE

                        if (day == 2) {
                            style += Styles.NA_PAKI
                        } else if(day == 1){
                            style += Styles.AYYAAM_E_SHAKK
                        }
                    }
                }
            }

            for (j in listOfColorsOfDaysList.indices) {
                val colorsOfDaysList = listOfColorsOfDaysList[j]
                div{
                    style = Styles.NEW_ROW
                }
                div { id = "sit${j+1}"
                    Styles.TABLE_ROW_STYLE
                    div { id="half_cell"
                        style = Styles.HALF_CELL
                    }

                    for (k in colorsOfDaysList.indices) {
                        val cellValue = colorsOfDaysList[k]
                        div { id = "cello"
                            style = Styles.TABLE_CELL_BORDER_STYLE +
                                    (if (cellValue == 1) Styles.NA_PAKI else "")
                            +"${k+1}"
                        }
                    }
                }
            }
        }
    }
}
