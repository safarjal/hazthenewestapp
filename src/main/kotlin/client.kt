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

    object AddTimeToDate {
        const val IS_DATE_ONLY = "is_date_only_add_time_to_date"
        const val DATE_TO_ADD_TO = "date_to_add_to"
        const val TIME_TO_ADD = "time_to_add"
        const val OUTOUT_FIELD = "add_time_date_output"
    }
    object CalcDuration {
        const val IS_DATE_ONLY = "get_duration_is_date_only"
        const val STRT_DATE = "start_date"
        const val END_DATE = "end_date"
        const val OUTPUT_FIELD = "calc_duration_output"
    }

    object Row {
        const val INPUT_START_TIME = "input_start_time"
        const val INPUT_END_TIME = "input_end_time"
        const val BUTTONS_CONTAINER = "button_add_before_container"
        const val BUTTON_REMOVE = "button_remove"
        const val BUTTON_ADD_BEFORE = "button_add_before"
    }

    object DurationRow {
        const val INPUT_DURATION = "input_duration"
        const val INPUT_TYPE_OF_DURATION = "input_duration_type"
        const val DURATION_BUTTONS_CONTAINER = "duration_button_add_before_container"
        const val DURATION_BUTTON_REMOVE = "duration_button_remove"
        const val DURATION_BUTTON_ADD_BEFORE = "duration_button_add_before"
    }

    object Ikhtilafat {
        const val IKHTILAF1 = "ikhtilaf1"
        const val IKHTILAF2 = "ikhtilaf2"
    }

    const val CONTENT_CONTAINER = "content_container"
    const val CONTENT_URDU = "content_urdu"
    const val CONTENT_ENGLISH = "content_english"
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
    const val INPUT_TYPE_SELECT = "input_type_select"
    const val INPUT_QUESTION = "input_question"
    const val INPUTS_CONTAINER_CLONE_BUTTON = "inputs_container_clone_button"
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
private val root_hazapp = document.getElementsByClassName("root").asList()
private val languageSelecter get() = document.getElementById("language") as HTMLSelectElement
private val languageSelecterValue get() = (document.getElementById("language") as HTMLSelectElement).value

private val HTMLElement.haizInputTable get() = getChildById(Ids.HAIZ_INPUT_TABLE) as HTMLTableElement
private val HTMLElement.haizDurationInputTable get() = getChildById(Ids.HAIZ_DURATION_INPUT_TABLE) as HTMLTableElement

private val HTMLElement.isDateTime get() = (getChildById(Ids.INPUT_TYPE_SELECT) as HTMLSelectElement).value == "dateTime"
private val HTMLElement.isDateOnly get() = (getChildById(Ids.INPUT_TYPE_SELECT) as HTMLSelectElement).value == "dateOnly"
private val HTMLElement.isDuration get() = (getChildById(Ids.INPUT_TYPE_SELECT) as HTMLSelectElement).value == "duration"

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
private val HTMLElement.contentEnglish get() = getChildById(Ids.CONTENT_ENGLISH) as HTMLParagraphElement
private val HTMLElement.contentUrdu get() = getChildById(Ids.CONTENT_URDU) as HTMLParagraphElement
private val HTMLElement.contentDatesElement get() = getChildById(Ids.CONTENT_DATES) as HTMLParagraphElement

private val HTMLElement.inputsContainerCloneButton get() =
    getChildById(Ids.INPUTS_CONTAINER_CLONE_BUTTON) as HTMLButtonElement
private val HTMLElement.inputsContainerRemoveButton get() =
    getChildById(Ids.INPUTS_CONTAINER_REMOVE_BUTTON) as HTMLButtonElement

private val HTMLElement.ikhtilaf1 get() = (getChildById(Ids.Ikhtilafat.IKHTILAF1) as HTMLInputElement).checked
private val HTMLElement.ikhtilaf2 get() = (getChildById(Ids.Ikhtilafat.IKHTILAF2) as HTMLInputElement).checked

private var HTMLElement.haizDatesList: List<Entry>?
    get() = (contentDatesElement.asDynamic().haizDatesList as List<Entry>?)?.takeIf { it != undefined }
    set(value) { contentDatesElement.asDynamic().haizDatesList = value }

private val HTMLElement.pregnancyInputs get() = Ids.pregnancyElementIds.map { id ->
    getChildById(id) as HTMLInputElement
}
private val HTMLElement.pregnancyElements get() = getElementsByClassName("preg-checked").asList()
private val englishElements get() = document.getElementsByClassName("english").asList()
private val urduElements get() = document.getElementsByClassName("urdu").asList()
private val devElements get() = document.getElementsByClassName("dev").asList()

private val HTMLElement.hazInputTableBody: HTMLTableSectionElement
    get() {
        val inputDatesTable = getChildById(Ids.HAIZ_INPUT_TABLE) as HTMLTableElement
        return inputDatesTable.tBodies[0] as HTMLTableSectionElement
    }
private val HTMLElement.hazDurationInputTableBody: HTMLTableSectionElement
    get() {
        val inputDatesTable = getChildById(Ids.HAIZ_DURATION_INPUT_TABLE) as HTMLTableElement
        return inputDatesTable.tBodies[0] as HTMLTableSectionElement
    }

private val HTMLElement.haizInputDatesRows: List<HTMLTableRowElement>
    get() {
        @Suppress("UNCHECKED_CAST")
        return hazInputTableBody.rows.asList() as List<HTMLTableRowElement>
    }
private val HTMLElement.haizDurationInputDatesRows: List<HTMLTableRowElement>
    get() {
        @Suppress("UNCHECKED_CAST")
        return hazDurationInputTableBody.rows.asList() as List<HTMLTableRowElement>
    }

private val HTMLTableRowElement.startTimeInput get() = getChildById(Ids.Row.INPUT_START_TIME) as HTMLInputElement
private val HTMLTableRowElement.endTimeInput get() = getChildById(Ids.Row.INPUT_END_TIME) as HTMLInputElement
private val HTMLTableRowElement.durationInput get() = getChildById(Ids.DurationRow.INPUT_DURATION) as HTMLInputElement
private val HTMLTableRowElement.durationTypeInput get() = getChildById(Ids.DurationRow.INPUT_TYPE_OF_DURATION) as HTMLSelectElement
private val HTMLTableRowElement.removeButton get() = getChildById(Ids.Row.BUTTON_REMOVE) as HTMLButtonElement
private val HTMLTableRowElement.removeDurationButton get() = getChildById(Ids.DurationRow.DURATION_BUTTON_REMOVE) as HTMLButtonElement
private val HTMLTableRowElement.damOrTuhr get() = (getChildById(Ids.DurationRow.INPUT_TYPE_OF_DURATION) as HTMLSelectElement?)?.value
private val HTMLTableRowElement.duration get() = (getChildById(Ids.DurationRow.INPUT_DURATION) as HTMLInputElement)

private val HTMLElement.haizTimeInputs get() = haizInputDatesRows.flatMap { row ->
    listOf(row.startTimeInput, row.endTimeInput)
}
private val HTMLElement.haizDurationInputs get() = haizDurationInputDatesRows.flatMap { row ->
    listOf(row.durationInput, row.durationTypeInput)
}

private val HTMLElement.timeInputsGroups get() = listOf(listOf(pregStartTime, pregEndTime), haizTimeInputs)
private val HTMLElement.durationInputsGroups get() = listOf(haizDurationInputs)

fun main() {
    window.onload = {
        if (root_hazapp.isNotEmpty() && askPassword()) {
                document.body!!.addInputLayout()
                setupRows(inputsContainers.first())
                setupFirstDurationRow(inputsContainers.first())
                document.addEventListener(Events.VISIBILITY_CHANGE, {
                    if (!document.isHidden) {
                        setMaxToCurrentTimeForTimeInputs(inputsContainers.first())
                    }
                })
        } else {
            mainOtherCalcs()
        }
        handleLanguage()
        devMode()
    }
}

fun askPassword():Boolean {
    val pass1 = "786"
    val password = window.prompt("${StringsOfLanguages.ENGLISH.warningOnlyAuthorizedPersonnel}\n\n" +
            "${StringsOfLanguages.URDU.warningOnlyAuthorizedPersonnel}\n\n" +
            "${StringsOfLanguages.URDU.passwordRequired}\n\n", "")
    if (pass1 == password) {
        return true
    }
    else return askPassword()
}

fun handleLanguage() {
    languageSelecter.onchange = { languageChange() }
    if (window.location.href.contains("lang=en")) {
        languageSelecter.value = "english"
    } else {
        languageSelecter.value = "urdu"
    }
    languageChange()
}
fun devMode() {
    for (element in devElements) element.visibility = window.location.href.contains("dev")
}

fun languageChange() {
    for (element in englishElements) element.classList.toggle("lang-invisible", languageSelecterValue == "urdu")
    for (element in urduElements) element.classList.toggle("lang-invisible", languageSelecterValue == "english")
    document.body!!.classList.toggle("rtl", languageSelecterValue == "urdu")
    document.querySelectorAll("select")
        .asList()
        .map { it as HTMLSelectElement }
        .forEach { select ->
            select.children
                .asList()
                .map { it as HTMLOptionElement }
                .firstOrNull { option ->
                    option.value == select.value && option.classList.contains(languageSelecterValue)
                }
                ?.selected = true
        }
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

private fun cloneInputsContainer(inputsContainerToCopyFrom: HTMLElement) {
    comparisonContainer?.remove()
    if (inputsContainers.size == 1) {
        addRemoveInputsContainerButton(inputsContainerToCopyFrom)
    }
    val clonedInputsContainer = inputsContainerToCopyFrom.after {
        inputFormDiv(inputsContainerToCopyFrom)
    }.single()
    setupFirstRow(clonedInputsContainer)
    languageChange()
    invisPregnancy(clonedInputsContainer)
}

private fun addRemoveInputsContainerButton(inputContainer: HTMLElement) {
    inputContainer.inputsContainerCloneButton.before {
        button(type = ButtonType.button, classes = "minus dev") {
            +"\u274C"
            id = Ids.INPUTS_CONTAINER_REMOVE_BUTTON
            style = "float: right"
            onClickFunction = { event ->
                removeInputsContainer(findInputContainer(event))
            }
        }
    }
}

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
        if (inputContainerToCopyFrom != null) {
            removeInputsContainerButton()
        }
        classes = setOf(Ids.INPUT_CONTAINER, "date_only")
        addInputsContainerButton()
        inputForm(inputContainerToCopyFrom)
        content()
    }
}

private fun TagConsumer<HTMLElement>.addInputsContainerButton() {
    inputsContainerAddRemoveButton {
        +"Clone"
        classes = setOf("plus", "clone", "dev")
        id = Ids.INPUTS_CONTAINER_CLONE_BUTTON
        onClickFunction = { event ->
            cloneInputsContainer(findInputContainer(event))
        }
    }
}

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
        div(classes = "urdu") {
            id = "content_wrapper"
            div(classes = "left") {
                small(classes = "rtl") { }
                button(classes = "rtl") {
                    onClickFunction = { event -> copyText(event) }
                    +"Copy ⎙"
                }
            }
            content {
                id = Ids.CONTENT_URDU
                classes = setOfNotNull("urdu")
            }
        }
        div(classes = "english") {
            id = "content_wrapper"
            div(classes = "right") {
                small { }
                button {
                    onClickFunction = { event -> copyText(event) }
                    +"Copy ⎙"
                }
            }
            div(classes = "content") {
                content {
                    id = Ids.CONTENT_ENGLISH
                    classes = setOfNotNull("english")
                }
            }
        }
        hr()
        content {
            id = Ids.CONTENT_DATES
        }
        hr()
    }
}

private fun copyText(event: Event) {
    val div = (event.currentTarget as HTMLElement).getAncestor<HTMLDivElement> { it.id.equals("content_wrapper") }
    val para = div?.querySelector("p")
    val small = div?.querySelector("small")
    para?.textContent?.let { window.navigator.clipboard.writeText(it) }
    small?.innerHTML?.let { small.innerHTML = " Copied! " }
    window.setTimeout({
        if (small != null) {
            small.innerHTML = ""
        }
    }, 1000)
}

private fun TagConsumer<HTMLElement>.inputForm(inputContainerToCopyFrom: HTMLElement?) {
    form(action = "javascript:void(0);") {
        autoComplete = false
        ikhtilafiMasle()
        br()
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
        questionInput(inputContainerToCopyFrom)
        hr()
        haizDatesInputTable(inputContainerToCopyFrom)
        haizDurationInputTable(inputContainerToCopyFrom)
        calculateButton()
        hr()
        onSubmitFunction = { event ->
            println("submit")
            parseEntries(findInputContainer(event))
        }
    }
}

private fun FlowContent.makeLabel(inputId: String, englishText: String, urduText: String, preg: Boolean = false) {
    label {
        htmlFor = inputId
        classes = setOfNotNull(
            "english",
            if (preg) "preg-checked" else null,
            if (preg) "invisible" else null,
        )
        +englishText
    }
    label {
        htmlFor = inputId
        classes = setOfNotNull(
            "urdu",
            if (preg) "preg-checked" else null,
            if (preg) "invisible" else null,
        )
        +urduText
    }
}

private fun FlowContent.typeConfigurationSelectDropdown(inputContainerToCopyFrom: HTMLElement?) {
    val isDateTime = inputContainerToCopyFrom?.isDateTime ?: !IS_DEFAULT_INPUT_MODE_DATE_ONLY
    val isDateOnly = inputContainerToCopyFrom?.isDateOnly ?: IS_DEFAULT_INPUT_MODE_DATE_ONLY
    val isDuration = inputContainerToCopyFrom?.isDuration ?: !IS_DEFAULT_INPUT_MODE_DATE_ONLY
    div(classes = "row") {
        makeLabel(Ids.INPUT_TYPE_SELECT, StringsOfLanguages.ENGLISH.typeOfInput, StringsOfLanguages.URDU.typeOfInput)
        select {
            id = Ids.INPUT_TYPE_SELECT
            onChangeFunction = { event ->
                onClickTypeConfigurationSelectDropdown(findInputContainer(event))
            }
            option(classes = "english") {
                selected = isDateOnly
                value = "dateOnly"
                +StringsOfLanguages.ENGLISH.dateOnly
            }
            option(classes = "urdu") {
                selected = isDateOnly
                value = "dateOnly"
                +StringsOfLanguages.URDU.dateOnly
            }
            option(classes = "english") {
                selected = isDateTime
                value = "dateTime"
                +StringsOfLanguages.ENGLISH.dateAndTime
            }
            option(classes = "urdu") {
                selected = isDateTime
                value = "dateTime"
                +StringsOfLanguages.URDU.dateAndTime
            }
            option(classes = "english") {
                selected = isDuration
                value = "duration"
                +StringsOfLanguages.ENGLISH.duration
            }
            option(classes = "urdu") {
                selected = isDuration
                value = "duration"
                +StringsOfLanguages.URDU.duration
            }
        }
    }
}

private fun FlowContent.aadatInputs(inputContainerToCopyFrom: HTMLElement?) {
    div(classes = "row") {
        makeLabel(Ids.AADAT_HAIZ_INPUT, StringsOfLanguages.ENGLISH.haizAadat, StringsOfLanguages.URDU.haizAadat)
        input(classes = "aadat") {
            id = Ids.AADAT_HAIZ_INPUT
            name = Ids.AADAT_HAIZ_INPUT
            value = inputContainerToCopyFrom?.aadatHaz?.value.orEmpty()
            onInputFunction = { event -> (event.currentTarget as HTMLInputElement).validateAadat(3..10) }
        }
    }
    div(classes = "row") {
        makeLabel(Ids.AADAT_TUHR_INPUT, StringsOfLanguages.ENGLISH.tuhrAadat, StringsOfLanguages.URDU.tuhrAadat)
        input(classes = "aadat") {
            id = Ids.AADAT_TUHR_INPUT
            name = Ids.AADAT_TUHR_INPUT
            value = inputContainerToCopyFrom?.aadatTuhr?.value.orEmpty()
            onInputFunction = { event -> (event.currentTarget as HTMLInputElement).validateAadat(15..6 * 30) }
        }
    }
    div(classes = "row aadat_inputs") {
        makeLabel(Ids.MAWJOODA_TUHR_INPUT, StringsOfLanguages.ENGLISH.mawjoodahTuhr, StringsOfLanguages.URDU.mawjoodahTuhr)
        input(classes = "aadat") {
            id = Ids.MAWJOODA_TUHR_INPUT
            name = Ids.MAWJOODA_TUHR_INPUT
            value = inputContainerToCopyFrom?.mawjoodaTuhr?.value.orEmpty()
            onInputFunction = { event -> (event.currentTarget as HTMLInputElement).validateAadat(15..10000) }
            //TODO: Find out how to do infinity, rather than 10000
        }
        div {
            id = "fasid"
            makeLabel(Ids.MAWJOODA_FASID_CHECKBOX, StringsOfLanguages.ENGLISH.faasid, StringsOfLanguages.URDU.faasid)
            input(type = InputType.checkBox) {
                id = Ids.MAWJOODA_FASID_CHECKBOX
                name = Ids.MAWJOODA_FASID_CHECKBOX
                checked = inputContainerToCopyFrom?.isMawjoodaFasid?.or(false) == true
            }
        }
    }
    pregnancyCheckBox(inputContainerToCopyFrom)
    div(classes = "row preg-checked invisible") {
        makeLabel(Ids.AADAT_NIFAS_INPUT, StringsOfLanguages.ENGLISH.nifasAadat, StringsOfLanguages.URDU.nifasAadat, true)
        input {
            id = Ids.AADAT_NIFAS_INPUT
            name = Ids.AADAT_NIFAS_INPUT
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

fun HTMLInputElement.validateAadat(validityRange: ClosedRange<Int>) {
    val errormessage = if(languageSelecterValue=="english") {StringsOfLanguages.ENGLISH.incorrectAadat } else {StringsOfLanguages.URDU.incorrectAadat}
    value = value.replace("[^0-9:]".toRegex(), "")
    val doubleValidityRange = validityRange.start.toDouble()..validityRange.endInclusive.toDouble()
    setCustomValidity(try {
        val days = (parseDays(value)?.div(MILLISECONDS_IN_A_DAY))?.toDouble()
        require(days == null || days in doubleValidityRange) { errormessage }
        ""
    } catch (e: IllegalArgumentException) {
        e.message ?: errormessage
    })
}

private fun FlowContent.pregnancyCheckBox(inputContainerToCopyFrom: HTMLElement?) {
    div(classes = "row") {
        div {
            makeLabel(Ids.PREGNANCY_CHECKBOX, StringsOfLanguages.ENGLISH.nifas, StringsOfLanguages.URDU.nifas)
            checkBoxInput {
                id = Ids.PREGNANCY_CHECKBOX
                name = Ids.PREGNANCY_CHECKBOX
                checked = inputContainerToCopyFrom?.isPregnancy == true
                onChangeFunction = { event ->
//                    val isChecked = (event.currentTarget as HTMLInputElement).checked
                    val inputContainer = findInputContainer(event)
//                    for (pregnancyElement in inputContainer.pregnancyInputs) {
//                        pregnancyElement.visibility = isChecked
//                        pregnancyElement.disabled = !isChecked
//                    }
//                    for (pregnancyElement in inputContainer.pregnancyElements) {
//                            pregnancyElement.visibility = isChecked
//                    }
                    invisPregnancy(inputContainer)
                    if (inputContainer.isDuration) disableAadaat(inputContainer, inputContainer.isDuration)
                }
            }
        }
    }
}

fun invisPregnancy(inputContainer: HTMLElement) {
    for (pregnancyElement in inputContainer.pregnancyInputs) {
        pregnancyElement.visibility = inputContainer.isPregnancy
        pregnancyElement.disabled = !inputContainer.isPregnancy
    }
    for (pregnancyElement in inputContainer.pregnancyElements) {
        pregnancyElement.visibility = inputContainer.isPregnancy
    }
}

private fun FlowContent.mustabeenCheckBox(inputContainerToCopyFrom: HTMLElement?) {
    div(classes = "row preg-checked invisible") {
        div {
            makeLabel(Ids.MUSTABEEN_CHECKBOX,
                StringsOfLanguages.ENGLISH.mustabeenUlKhilqa,
                StringsOfLanguages.URDU.mustabeenUlKhilqa, true)
            checkBoxInput {
                id = Ids.MUSTABEEN_CHECKBOX
                name = Ids.MUSTABEEN_CHECKBOX
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
    div(classes = "row preg-checked invisible aadat_inputs") {
        div(classes = "row preg-checked invisible aadat_inputs") {
            makeLabel(Ids.PREG_START_TIME_INPUT,
                StringsOfLanguages.ENGLISH.pregnancyStartTime,
                StringsOfLanguages.URDU.pregnancyStartTime, true)
            pregnancyTimeInput(inputContainerToCopyFrom) {
                classes = setOfNotNull(
                    "preg-checked",
                    if (inputContainerToCopyFrom?.isPregnancy != true) "invisible" else null
                )
                id = Ids.PREG_START_TIME_INPUT
                name = Ids.PREG_START_TIME_INPUT
                onChangeFunction = { event ->
                    findInputContainer(event).pregEndTime.min = (event.currentTarget as HTMLInputElement).value
                }
            }
        }
    }
}

private fun FlowContent.pregnancyEndTimeInput(inputContainerToCopyFrom: HTMLElement?) {
    div(classes = "row preg-checked invisible aadat_inputs") {
        div(classes = "row preg-checked invisible aadat_inputs") {
            makeLabel(Ids.PREG_END_TIME_INPUT, StringsOfLanguages.ENGLISH.birthMiscarrriageTime, StringsOfLanguages.URDU.birthMiscarrriageTime, true)
            pregnancyTimeInput(inputContainerToCopyFrom) {
                classes = setOfNotNull(
                    "preg-checked",
                    if (inputContainerToCopyFrom?.isPregnancy != true) "invisible" else null
                )
                id = Ids.PREG_END_TIME_INPUT
                name = Ids.PREG_END_TIME_INPUT
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

private fun FlowContent.ikhtilafiMasle() {
    div {
        details {
            summary(classes = "ikhtilaf")
            b {
                span(classes = "english") { +StringsOfLanguages.ENGLISH.ikhtilafimasail }
                span(classes = "urdu") { +StringsOfLanguages.URDU.ikhtilafimasail }
            }
            isIkhtilafiMasla(Ids.Ikhtilafat.IKHTILAF1, StringsOfLanguages.ENGLISH.considerTuhrInGhiarMustabeenIsqaatIkhtilaf, StringsOfLanguages.URDU.considerTuhrInGhiarMustabeenIsqaatIkhtilaf)
            isIkhtilafiMasla(Ids.Ikhtilafat.IKHTILAF2, StringsOfLanguages.ENGLISH.aadatIncreasingAtEndOfDaurIkhtilaf, StringsOfLanguages.URDU.aadatIncreasingAtEndOfDaurIkhtilaf)
        }
    }
}

private fun FlowContent.isIkhtilafiMasla(inputId: String, englishText: String, urduText: String) {
    div(classes = "row") {
        div {
            makeLabel(inputId, englishText, urduText)
            label(classes = "switch") {
                checkBoxInput {
                    id = inputId
                }
                span(classes = "slider round")
            }
        }
    }
}

private fun FlowContent.calculateButton() {
    button(classes = "english calc-btn") {
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
        id = "content"
        style = "white-space: pre-wrap;"
        block()
    }
}

private fun TagConsumer<HTMLElement>.questionInput(inputContainerToCopyFrom: HTMLElement?) {
    details {
        summary {
            span(classes = "urdu") { +"سوال" }
            span(classes = "english lang-invisible") { +"Question" }
        }
//        makeLabel(Ids.INPUT_QUESTION, "Question", "سوال")
        div(classes = "row") {
            textArea {
                id = Ids.INPUT_QUESTION
                onInputFunction = { event ->
                    val txtarea = event.currentTarget as HTMLTextAreaElement
                    txtarea.style.height = "auto"
                    txtarea.style.height = "${txtarea.scrollHeight + 6}px"
                }
            }
        }
    }
}

private fun TagConsumer<HTMLElement>.haizDatesInputTable(inputContainerToCopyFrom: HTMLElement?) {
    table {
        id = Ids.HAIZ_INPUT_TABLE
        thead {
            tr {
                th(classes = "english") { +StringsOfLanguages.ENGLISH.startTime }
                th(classes = "english") { +StringsOfLanguages.ENGLISH.endTime }
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
    table(classes = "invisible") {
        id = Ids.HAIZ_DURATION_INPUT_TABLE
        thead {
            tr {
                th(classes = "english") { +StringsOfLanguages.ENGLISH.duration }
                th(classes = "english") { +StringsOfLanguages.ENGLISH.damOrTuhr }
                th(classes = "urdu") { +StringsOfLanguages.URDU.duration }
                th(classes = "urdu") { +StringsOfLanguages.URDU.damOrTuhr }
                th {durationAddBeforeButton()}
            }
        }
        tbody {
            durationInputRow(false, true)
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

private fun TagConsumer<HTMLElement>.durationInputRow(lastWasDam: Boolean, disable: Boolean, preg: Boolean = false) {
    val urdu = languageSelecterValue == "urdu"
    tr {
        td {
            input {
                id = Ids.DurationRow.INPUT_DURATION
                name = Ids.DurationRow.INPUT_DURATION
                disabled = disable
                required = true
                onInputFunction = { event -> (event.currentTarget as HTMLInputElement).validateAadat(0..10000) }
            }
        }
        td {
            select {
                id = Ids.DurationRow.INPUT_TYPE_OF_DURATION
                name = Ids.DurationRow.INPUT_TYPE_OF_DURATION
                disabled = disable
                onChangeFunction = { event ->
                    val row = findRow(event)
                    row.durationInput.value = "0"
                    row.durationInput.disabled = (event.target as HTMLSelectElement).value in setOf("haml", "wiladat")
                }
                option(classes = "english") {
                    selected = !urdu && !lastWasDam
                    value = "dam"
                    + StringsOfLanguages.ENGLISH.dam
                }
                option(classes = "english") {
                    selected = !urdu && lastWasDam
                    value = "tuhr"
                    + StringsOfLanguages.ENGLISH.tuhr
                }
                option {
                    classes = setOfNotNull(
                        "english",
                        "preg-checked",
                        if (!preg) "invisible" else null,
                    )
                    value = "haml"
                    + StringsOfLanguages.ENGLISH.preg
                }
                option(classes = "english preg-checked invisible") {
                    classes = setOfNotNull(
                        "english",
                        "preg-checked",
                        if (!preg) "invisible" else null,
                    )
                    value = "wiladat"
                    + StringsOfLanguages.ENGLISH.birthduration
                }
                option(classes = "urdu") {
                    selected = urdu && !lastWasDam
                    value = "dam"
                    + StringsOfLanguages.URDU.dam
                }
                option(classes = "urdu") {
                    selected = urdu && lastWasDam
                    value = "tuhr"
                    + StringsOfLanguages.URDU.tuhr
                }
                option {
                    classes = setOfNotNull(
                        "urdu",
                        "preg-checked",
                        if (!preg) "invisible" else null,
                    )
                    value = "haml"
                    + StringsOfLanguages.URDU.pregduration
                }
                option {
                    classes = setOfNotNull(
                        "urdu",
                        "preg-checked",
                        if (!preg) "invisible" else null,
                    )
                    value = "wiladat"
                    + StringsOfLanguages.URDU.birthduration
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
        id = Ids.DurationRow.DURATION_BUTTONS_CONTAINER
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
        id = Ids.DurationRow.DURATION_BUTTON_REMOVE
        onClickFunction = { event ->
            val row = findRow(event)
            val inputContainer = findInputContainer(event)
            row.remove()
            setupFirstDurationRow(inputContainer)
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
            val rowIsDam = row.damOrTuhr in setOf("dam", "haml")
            val inputContainer = findInputContainer(event)
            row.after {
                durationInputRow(rowIsDam, false, inputContainer.isPregnancy)
            }
            setupFirstDurationRow(inputContainer)
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
            val inputDatesRows = inputContainer.haizDurationInputDatesRows
            val firstIsDam = inputDatesRows.first().damOrTuhr in setOf("dam", "wiladat")

            inputContainer.hazDurationInputTableBody.prepend { durationInputRow(firstIsDam, false, inputContainer.isPregnancy) }
            setupFirstDurationRow(inputContainer)
        }
    }
}

private fun TagConsumer<HTMLElement>.addBeforeButton() {
    button(type = ButtonType.button, classes = "plus") {
        +"\u2795 \u25B2"
        title = "Add at Start"
        id = Ids.DurationRow.DURATION_BUTTON_ADD_BEFORE
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

//private fun setupFirstRow(inputContainer: HTMLElement) {
//    ensureAddFirstButtonOnlyShownInFirstRow(inputContainer)
//}

//private fun updateRemoveButtonDisabledStateForFirstRow(inputContainer: HTMLElement) {
private fun setupFirstRow(inputContainer: HTMLElement) {
    val inputDatesRows = inputContainer.haizInputDatesRows
    inputDatesRows.first().removeButton.visibility = inputDatesRows.size != 1
    inputDatesRows.getOrNull(1)?.removeButton?.visibility = true
}

private fun setupFirstDurationRow(inputContainer: HTMLElement) {
    val inputDatesRows = inputContainer.haizDurationInputDatesRows
    inputDatesRows.first().removeDurationButton.visibility = inputDatesRows.size != 1
    inputDatesRows.getOrNull(1)?.removeDurationButton?.visibility = true
}

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
        inputContainer.haizDurationInputTable.visibility = false
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

        if (isDateTime) {
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

        disableDateTable(inputContainer, true)
        inputContainer.haizInputTable.visibility = false
        inputContainer.haizDurationInputTable.visibility = true
    }
}

private fun disableDateTable(inputContainer: HTMLElement, disable: Boolean) {
    for (timeInput in inputContainer.timeInputsGroups) {
        for (input in timeInput) {
            input.disabled = disable
        }
    }
    for (durationInput in inputContainer.durationInputsGroups) {
        for (input in durationInput) {
            input.asDynamic().disabled = !disable
        }
    }
    disableAadaat(inputContainer, disable)
}

private fun disableAadaat(inputContainer: HTMLElement, disable: Boolean = inputContainer.isDuration) {
    inputContainer.getElementsByClassName("aadat_inputs")
        .asList()
        .forEach { row ->
            row.classList.toggle("duration-invis", disable)
            row.querySelectorAll("input")
                .asList()
                .map { input ->
                    input as HTMLInputElement
                    input.disabled = disable
                }
        }
    if (!inputContainer.isPregnancy) {
        for (pregnancyElement in inputContainer.pregnancyInputs) {
            pregnancyElement.visibility = false
            pregnancyElement.disabled = true

        }
    }
}

private fun parseEntries(inputContainer: HTMLElement) {
    var entries= listOf<Entry>()

    with(inputContainer) {
        var mawjodahtuhreditable = parseDays(mawjoodaTuhr.value)
        var pregnancyIs = isPregnancy
        var pregnancyStrt = Date(pregStartTime.valueAsNumber)
        var pregnancyEnd = Date(pregEndTime.valueAsNumber)


        if(isDuration){
            //take arbitrary date
            val arbitraryDate= Date(0,0,0)
            var durations = haizDurationInputDatesRows.map { row ->
                Duration(
                    type = if(row.damOrTuhr == "dam"){DurationType.DAM}
                    else if(row.damOrTuhr == "tuhr"){DurationType.TUHR}
                    else if(row.damOrTuhr == "haml"){DurationType.HAML}
                    else if(row.damOrTuhr == "wiladat"){DurationType.WILADAT_ISQAT}
                            else{DurationType.NIFAAS},
                    timeInMilliseconds = parseDays(row.duration.value)!!,
                    startTime = arbitraryDate
                ) }
            for (index in durations.indices){
                if(index>0){
                    durations[index].startTime = durations[index-1].endDate
                }
            }
            if(durations[0].type==DurationType.TUHR){mawjodahtuhreditable=durations[0].timeInMilliseconds}
            println(durations)
            for(dur in durations){
                if(dur.type==DurationType.DAM){
                    entries+=Entry(dur.startTime, dur.endDate)
                }else if(dur.type==DurationType.HAML){
                    pregnancyIs=true
                    pregnancyStrt=dur.startTime
                }else if(dur.type==DurationType.WILADAT_ISQAT){
                    pregnancyEnd=dur.startTime
                }
            }
        }else{
            entries = haizInputDatesRows.map { row ->
                Entry(
                    startTime = Date(row.startTimeInput.valueAsNumber),
                    endTime = Date(row.endTimeInput.valueAsNumber)
                )
            }

        }
        println(entries)
        @Suppress("UnsafeCastFromDynamic")
        val output = handleEntries(
            entries,
            parseDays(aadatHaz.value),
            parseDays(aadatTuhr.value),
            mawjodahtuhreditable,
            isMawjoodaFasid,
            isDateOnly,
            pregnancyIs,
            Pregnancy(
                pregnancyStrt,
                pregnancyEnd,
                parseDays(aadatNifas.value),
                mustabeen
            ),
            false,
            languageSelecterValue,
            isDuration,
            ikhtilaf1,
            ikhtilaf2
        )
        contentContainer.visibility = true
//        if (languageSelecterValue == "english") {
            contentEnglish.innerHTML = replaceBoldTagWithBoldAndStar(output.englishText)
//            contentElement.classList.toggle("rtl", false)
//        } else {
            contentUrdu.innerHTML = replaceBoldTagWithBoldAndStar(output.urduText)
//            contentElement.classList.toggle("rtl", true)
//        }
//        contentDatesElement.innerHTML = output.haizDatesText
        haizDatesList = output.hazDatesList
    }
    addCompareButtonIfNeeded()
}

fun replaceBoldTagWithBoldAndStar(string: String): String {
    return string.replace("<b>", "<b><span class='invisible'>*</span>")
        .replace("</b>", "<span class='invisible'>*</span></b>")
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
