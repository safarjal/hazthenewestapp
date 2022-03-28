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
private const val IS_DEFAULT_INPUT_MODE_MUTADA = true

object Ids {
    const val HAIZ_INPUT_TABLE = "haiz_input_table"
    const val HAIZ_DURATION_INPUT_TABLE = "haiz_duration_input_table"

    object AddTimeToDate {
//        const val IS_DATE_ONLY = "is_date_only_add_time_to_date"
        const val DATE_TO_ADD_TO = "date_to_add_to"
        const val TIME_TO_ADD = "time_to_add"
        const val OUTOUT_FIELD = "add_time_date_output"
    }
    object CalcDuration {
//        const val IS_DATE_ONLY = "get_duration_is_date_only"
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
    const val MUSTABEEN_CHECKBOX = "mustabeen_checkbox"
    const val PREG_START_TIME_INPUT = "preg_start_time_input"
    const val PREG_END_TIME_INPUT = "preg_end_time_input"
    const val AADAT_HAIZ_INPUT = "aadat_haiz_input"
    const val AADAT_TUHR_INPUT = "aadat_tuhr_input"
    const val MAWJOODA_TUHR_INPUT = "mawjooda_tuhr_input"
    const val MAWJOODA_FASID_CHECKBOX = "mawjooda_fasid_checkbox"
    const val AADAT_NIFAS_INPUT = "aadat_nifas_input"
    const val INPUT_TYPE_SELECT = "input_type_select"
    const val MASLA_TYPE_SELECT = "masla_type_select"
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
private val languageSelector get() = document.getElementById("language") as HTMLSelectElement

private val HTMLElement.haizInputTable get() = getChildById(Ids.HAIZ_INPUT_TABLE) as HTMLTableElement
private val HTMLElement.haizDurationInputTable get() = getChildById(Ids.HAIZ_DURATION_INPUT_TABLE) as HTMLTableElement

private val HTMLElement.isDateTime get() = (getChildById(Ids.INPUT_TYPE_SELECT) as HTMLSelectElement).value == "dateTime"
private val HTMLElement.isDateOnly get() = (getChildById(Ids.INPUT_TYPE_SELECT) as HTMLSelectElement).value == "dateOnly"
private val HTMLElement.isDuration get() = (getChildById(Ids.INPUT_TYPE_SELECT) as HTMLSelectElement).value == "duration"

private val HTMLElement.isMutada get() = (getChildById(Ids.MASLA_TYPE_SELECT) as HTMLSelectElement).value == "mutada"
private val HTMLElement.isNifas get() = (getChildById(Ids.MASLA_TYPE_SELECT) as HTMLSelectElement).value == "nifas"
private val HTMLElement.isMubtadia get() = (getChildById(Ids.MASLA_TYPE_SELECT) as HTMLSelectElement).value == "mubtadia"

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

private val HTMLElement.questionText get() = (getChildById(Ids.INPUT_QUESTION) as HTMLTextAreaElement)

private val HTMLElement.inputsContainerCloneButton get() = getChildById(Ids.INPUTS_CONTAINER_CLONE_BUTTON) as HTMLButtonElement
private val HTMLElement.inputsContainerRemoveButton get() = getChildById(Ids.INPUTS_CONTAINER_REMOVE_BUTTON) as HTMLButtonElement

private val HTMLElement.ikhtilaf1 get() = (getChildById(Ids.Ikhtilafat.IKHTILAF1) as HTMLInputElement).checked
private val HTMLElement.ikhtilaf2 get() = (getChildById(Ids.Ikhtilafat.IKHTILAF2) as HTMLInputElement).checked

private var HTMLElement.haizDatesList: List<Entry>?
    get() = (contentDatesElement.asDynamic().haizDatesList as List<Entry>?)?.takeIf { it != undefined }
    set(value) { contentDatesElement.asDynamic().haizDatesList = value }

private val englishElements get() = document.getElementsByClassName("english").asList()
private val urduElements get() = document.getElementsByClassName("urdu").asList()
private val languageElements get() = listOf(englishElements, urduElements).flatten()
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
private val HTMLTableRowElement.damOrTuhr get() = durationTypeInput.value
private val HTMLTableRowElement.removeButton get() = getChildById(Ids.Row.BUTTON_REMOVE) as HTMLButtonElement

private val HTMLElement.haizTimeInputs get() = haizInputDatesRows.flatMap { row ->
    listOf(row.startTimeInput, row.endTimeInput)
}
private val HTMLElement.haizDurationInputs get() = haizDurationInputDatesRows.flatMap { row ->
    listOf(row.durationInput, row.durationTypeInput)
}

private val HTMLElement.timeInputsGroups get() = listOf(listOf(pregStartTime, pregEndTime), haizTimeInputs)
private val HTMLElement.durationInputsGroups get() = listOf(haizDurationInputs)

// START PROGRAM

fun main() {
    window.onload = {
        if (root_hazapp.isNotEmpty() && askPassword()) {
            document.body!!.addInputLayout()
            setupRows(inputsContainers.first())
            document.addEventListener(Events.VISIBILITY_CHANGE, {
                if (!document.isHidden) {
                    setMaxToCurrentTimeForTimeInputs(inputsContainers.first())
                }
            })
            languageSelector.onchange = { languageChange() }
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
    return pass1 == password || askPassword()
}

fun devMode() {
    for (element in devElements) element.visibility = window.location.href.contains("dev")
}

fun handleLanguage() {
    if (window.location.href.contains("lang=en")) languageSelector.value = "english"
    else languageSelector.value = "urdu"
    languageChange()
}

fun languageChange() {
    val lang = languageSelector.value
    console.log(languageElements)
    for (element in languageElements) element.classList.toggle("lang-invisible", !element.classList.contains(lang))
    document.body!!.classList.toggle("rtl", lang == "urdu")
    document.querySelectorAll("select")
        .asList()
        .map { it as HTMLSelectElement }
        .forEach { select ->
            select.children
                .asList()
                .map { it as HTMLOptionElement }
                .firstOrNull { option ->
                    option.value == select.value && option.classList.contains(lang)
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

// CLONING

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
    languageChange()
    onClickTypeConfigurationSelectDropdown(clonedInputsContainer)
    disableByMasla(clonedInputsContainer)
//    onClickMaslaConfigurationSelectDropdown(clonedInputsContainer)
    setupFirstRow(clonedInputsContainer, inputsContainerToCopyFrom.isDuration)
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

// ANSWER

private fun TagConsumer<HTMLElement>.content() {
    div(classes = "invisible") {
        id = Ids.CONTENT_CONTAINER
        div(classes = "urdu") {
            id = "content_wrapper"
            div(classes = "left") {
                small(classes = "rtl")
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
    val div = (event.currentTarget as HTMLElement).getAncestor<HTMLDivElement> { it.id == "content_wrapper" }
    val questionTxt = findInputContainer(event).questionText.value
    val divider = "\uD83C\uDF00➖➖➖➖➖\uD83C\uDF00"
    val answerTxt = div?.querySelector("p")?.textContent
    var dateStr = ""
    if (languageSelector.value=="urdu"){
        dateStr += urduDateFormat(Date(Date.now()),true)
    }else if(languageSelector.value=="english"){
        dateStr += englishDateFormat(Date(Date.now()),true)
    }
    dateStr+= " ${Date(Date.now()).getFullYear()}"
    val copyTxt = "*${dateStr}*\n\n${questionTxt}\n\n${divider}\n\n${answerTxt}"
    val small = div?.querySelector("small")
    copyTxt.let { window.navigator.clipboard.writeText(it) }
    small?.innerHTML?.let { small.innerHTML = " Copied " }
    window.setTimeout({ if (small != null) small.innerHTML = "" }, 1000)
}

private fun TagConsumer<HTMLElement>.content(block : P.() -> Unit = {}) {
    p {
        id = "content"
        style = "white-space: pre-wrap;"
        block()
    }
}

// MAIN PROGRAM DRAWN HERE

private fun TagConsumer<HTMLElement>.inputForm(inputContainerToCopyFrom: HTMLElement?) {
    form(action = "javascript:void(0);") {
        autoComplete = false
        ikhtilafiMasle()
        br()
        div(classes = "label-input") {
            maslaConfigurationSelectDropdown(inputContainerToCopyFrom)
            typeConfigurationSelectDropdown(inputContainerToCopyFrom)
            nifasInputs(inputContainerToCopyFrom)
            mutadaInputs(inputContainerToCopyFrom)
        }
        hr()
        questionInput(inputContainerToCopyFrom)
        hr()
        haizDatesInputTable(inputContainerToCopyFrom)
        haizDurationInputTable(inputContainerToCopyFrom)
        calculateButton()
        hr()
        onSubmitFunction = { event -> parseEntries(findInputContainer(event)) }
    }
}

private fun FlowContent.makeLabel(inputId: String, englishText: String, urduText: String, preg: Boolean = false) {
    label {
        htmlFor = inputId
        classes = setOfNotNull(
            "english",
            if (preg) "is-nifas" else null,
            if (preg) "invisible" else null,
        )
        +englishText
    }
    label {
        htmlFor = inputId
        classes = setOfNotNull(
            "urdu",
            if (preg) "is-nifas" else null,
            if (preg) "invisible" else null,
        )
        +urduText
    }
}

private fun FlowContent.makeIkhtilafiMasla(inputId: String, englishText: String, urduText: String) {
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

private fun FlowContent.ikhtilafiMasle() {
    div {
        details {
            summary(classes = "ikhtilaf")
            b {
                span(classes = "english") { +StringsOfLanguages.ENGLISH.ikhtilafimasail }
                span(classes = "urdu") { +StringsOfLanguages.URDU.ikhtilafimasail }
            }
            makeIkhtilafiMasla(Ids.Ikhtilafat.IKHTILAF1, StringsOfLanguages.ENGLISH.considerTuhrInGhiarMustabeenIsqaatIkhtilaf, StringsOfLanguages.URDU.considerTuhrInGhiarMustabeenIsqaatIkhtilaf)
            div(classes = "invisible"){makeIkhtilafiMasla(Ids.Ikhtilafat.IKHTILAF2, StringsOfLanguages.ENGLISH.aadatIncreasingAtEndOfDaurIkhtilaf, StringsOfLanguages.URDU.aadatIncreasingAtEndOfDaurIkhtilaf)}
        }
    }
}

private fun TagConsumer<HTMLElement>.makeDropdownOptions(
    isSelected: Boolean,
    optionVal: String,
    englishText: String,
    urduText: String,
    extraClasses: String = ""
) {
    option {
        classes = setOfNotNull(
            "english",
            extraClasses,
            if (languageSelector.value != "english") "lang-invisible" else null
        )
        selected = isSelected && languageSelector.value == "english"
        value = optionVal
        +englishText
    }
    option {
        classes = setOfNotNull(
            "urdu",
            extraClasses,
            if (languageSelector.value != "urdu") "lang-invisible" else null
        )
        selected = isSelected && languageSelector.value == "urdu"
        value = optionVal
        +urduText
    }
}

private fun TagConsumer<HTMLElement>.maslaConfigurationSelectDropdown(inputContainerToCopyFrom: HTMLElement?) {
    val isMutada = inputContainerToCopyFrom?.isMutada ?: IS_DEFAULT_INPUT_MODE_MUTADA
    val isNifas = inputContainerToCopyFrom?.isNifas ?: !IS_DEFAULT_INPUT_MODE_MUTADA
    val isMubtadia = inputContainerToCopyFrom?.isMubtadia ?: !IS_DEFAULT_INPUT_MODE_MUTADA
    div(classes = "row") {
        makeLabel(Ids.MASLA_TYPE_SELECT, StringsOfLanguages.ENGLISH.typeOfMasla, StringsOfLanguages.URDU.typeOfMasla)
        select {
            id = Ids.MASLA_TYPE_SELECT
            onChangeFunction = { event ->
                disableByMasla(findInputContainer(event))
//                onClickMaslaConfigurationSelectDropdown(findInputContainer(event))
            }
            makeDropdownOptions(isMutada, "mutada", StringsOfLanguages.ENGLISH.mutada, StringsOfLanguages.URDU.mutada)
            makeDropdownOptions(isNifas, "nifas", StringsOfLanguages.ENGLISH.nifas, StringsOfLanguages.URDU.nifas)
            makeDropdownOptions(isMubtadia, "mubtadia", StringsOfLanguages.ENGLISH.mubtadia, StringsOfLanguages.URDU.mubtadia, "dev")
        }
    }
}

private fun TagConsumer<HTMLElement>.typeConfigurationSelectDropdown(inputContainerToCopyFrom: HTMLElement?) {
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
            makeDropdownOptions(isDateOnly, "dateOnly", StringsOfLanguages.ENGLISH.dateOnly, StringsOfLanguages.URDU.dateOnly)
            makeDropdownOptions(isDateTime, "dateTime", StringsOfLanguages.ENGLISH.dateAndTime, StringsOfLanguages.URDU.dateAndTime)
            makeDropdownOptions(isDuration, "duration", StringsOfLanguages.ENGLISH.duration, StringsOfLanguages.URDU.duration)
        }
    }
}

private fun FlowContent.nifasInputs(inputContainerToCopyFrom: HTMLElement?) {
    div(classes = "row is-nifas invisible nonduration-aadat") {
        div(classes = "row is-nifas nonduration-aadat") {
            makeLabel(Ids.PREG_START_TIME_INPUT,
                StringsOfLanguages.ENGLISH.pregnancyStartTime,
                StringsOfLanguages.URDU.pregnancyStartTime, true)
            pregnancyTimeInput(inputContainerToCopyFrom) {
                classes = setOfNotNull(
                    "is-nifas",
                )
                id = Ids.PREG_START_TIME_INPUT
                name = Ids.PREG_START_TIME_INPUT
                onChangeFunction = { event ->
                    findInputContainer(event).pregEndTime.min = (event.currentTarget as HTMLInputElement).value
                }
            }
        }
    }
    div(classes = "row is-nifas invisible nonduration-aadat") {
        div(classes = "row is-nifas nonduration-aadat") {
            makeLabel(Ids.PREG_END_TIME_INPUT, StringsOfLanguages.ENGLISH.birthMiscarrriageTime, StringsOfLanguages.URDU.birthMiscarrriageTime, true)
            pregnancyTimeInput(inputContainerToCopyFrom) {
                classes = setOfNotNull(
                    "is-nifas",
                )
                id = Ids.PREG_END_TIME_INPUT
                name = Ids.PREG_END_TIME_INPUT
                onChangeFunction = { event ->
                    findInputContainer(event).pregStartTime.max = (event.currentTarget as HTMLInputElement).value
                }
            }
        }
    }
    div(classes = "row is-nifas invisible") {
        div {
            makeLabel(Ids.MUSTABEEN_CHECKBOX,
                StringsOfLanguages.ENGLISH.mustabeenUlKhilqa,
                StringsOfLanguages.URDU.mustabeenUlKhilqa, true)
            checkBoxInput {
                id = Ids.MUSTABEEN_CHECKBOX
                name = Ids.MUSTABEEN_CHECKBOX
                classes = setOfNotNull(
                    "is-nifas",
                )
                checked = inputContainerToCopyFrom?.mustabeen != false
                disabled = inputContainerToCopyFrom?.isNifas != true
            }
        }
    }
    div(classes = "row is-nifas invisible") {
        makeLabel(Ids.AADAT_NIFAS_INPUT, StringsOfLanguages.ENGLISH.nifasAadat, StringsOfLanguages.URDU.nifasAadat, true)
        input {
            id = Ids.AADAT_NIFAS_INPUT
            name = Ids.AADAT_NIFAS_INPUT
            classes = setOfNotNull(
                "is-nifas",
            )
            step = "any"
            required = false
            disabled = inputContainerToCopyFrom?.isNifas != true
            value = inputContainerToCopyFrom?.aadatNifas?.value.orEmpty()
            onInputFunction = { event -> (event.currentTarget as HTMLInputElement).validateAadat(1..40) }
        }
    }
}

private fun FlowContent.pregnancyTimeInput(inputContainerToCopyFrom: HTMLElement?, block: INPUT.() -> Unit = {}) {
    if (inputContainerToCopyFrom != null) {
        timeInput(inputContainerToCopyFrom) {
            disabled = !inputContainerToCopyFrom.isNifas
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

private fun FlowContent.mutadaInputs(inputContainerToCopyFrom: HTMLElement?) {
    div(classes = "row") {
        makeLabel(Ids.AADAT_HAIZ_INPUT, StringsOfLanguages.ENGLISH.haizAadat, StringsOfLanguages.URDU.haizAadat)
        input {
            id = Ids.AADAT_HAIZ_INPUT
            name = Ids.AADAT_HAIZ_INPUT
            value = inputContainerToCopyFrom?.aadatHaz?.value.orEmpty()
            onInputFunction = { event -> (event.currentTarget as HTMLInputElement).validateAadat(3..10) }
        }
    }
    div(classes = "row mutada") {
        makeLabel(Ids.AADAT_TUHR_INPUT, StringsOfLanguages.ENGLISH.tuhrAadat, StringsOfLanguages.URDU.tuhrAadat)
        input {
            id = Ids.AADAT_TUHR_INPUT
            name = Ids.AADAT_TUHR_INPUT
            value = inputContainerToCopyFrom?.aadatTuhr?.value.orEmpty()
            onInputFunction = { event -> (event.currentTarget as HTMLInputElement).validateAadat(15..6 * 30) }
        }
    }
    div(classes = "row nonduration-aadat") {
        makeLabel(Ids.MAWJOODA_TUHR_INPUT, StringsOfLanguages.ENGLISH.mawjoodahTuhr, StringsOfLanguages.URDU.mawjoodahTuhr)
        input {
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
//    pregnancyCheckBox(inputContainerToCopyFrom)
}

fun HTMLInputElement.validateAadat(validityRange: ClosedRange<Int>) {
    val errormessage = if(languageSelector.value=="english") {StringsOfLanguages.ENGLISH.incorrectAadat } else {StringsOfLanguages.URDU.incorrectAadat}
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

private fun TagConsumer<HTMLElement>.questionInput(inputContainerToCopyFrom: HTMLElement?) {
    details {
        summary {
            span(classes = "urdu") { +"سوال" }
            span(classes = "english") { +"Question" }
        }
        div(classes = "row") {
            textArea {
                id = Ids.INPUT_QUESTION
                onInputFunction = { event ->
                    val txtarea = event.currentTarget as HTMLTextAreaElement
                    txtarea.dir = "auto"
                    txtarea.style.height = "auto"
                    txtarea.style.height = "${txtarea.scrollHeight + 6}px"
                }
            }
        }
    }
}

private fun TagConsumer<HTMLElement>.haizDatesInputTable(inputContainerToCopyFrom: HTMLElement?) {
    val isDuration = inputContainerToCopyFrom?.isDuration ?: false
    table {
        id = Ids.HAIZ_INPUT_TABLE
        classes = setOf( if (isDuration) "invisible" else "" )
        thead {
            tr {
                th {
                    span(classes = "english") { +StringsOfLanguages.ENGLISH.startTime }
                    span(classes = "urdu") { +StringsOfLanguages.URDU.startTime }
                }
                th {
                    span(classes = "english") { +StringsOfLanguages.ENGLISH.endTime }
                    span(classes = "urdu") { +StringsOfLanguages.URDU.endTime }
                }
                th { addBeforeButton() }
            }
        }
        tbody {
            if (inputContainerToCopyFrom != null) {
                for (inputDateRow in inputContainerToCopyFrom.haizInputDatesRows) {
                    inputRow(inputContainerToCopyFrom, inputDateRow.startTimeInput, inputDateRow.endTimeInput, isDuration)
                }
            } else {
                inputRow(
                    isDateOnlyLayout = IS_DEFAULT_INPUT_MODE_DATE_ONLY,
                    minTimeInput = "",
                    maxTimeInput = "", //currentTimeString(IS_DEFAULT_INPUT_MODE_DATE_ONLY)
                    disable = isDuration
                )
            }
        }
    }
}

private fun TagConsumer<HTMLElement>.haizDurationInputTable(inputContainerToCopyFrom: HTMLElement?) {
    val isDuration = inputContainerToCopyFrom?.isDuration ?: false
    table {
        id = Ids.HAIZ_DURATION_INPUT_TABLE
        classes = setOf( if (!isDuration) "invisible" else "" )
        thead {
            tr {
                th {
                    span(classes = "english") { +StringsOfLanguages.ENGLISH.duration }
                    span(classes = "urdu") { +StringsOfLanguages.URDU.duration }
                }
                th {
                    span(classes = "english") { +StringsOfLanguages.ENGLISH.damOrTuhr }
                    span(classes = "urdu") { +StringsOfLanguages.URDU.damOrTuhr }
                }
                th { addBeforeButton(true) }
            }
        }
        tbody {
            if (inputContainerToCopyFrom != null) {
                for (inputDateRow in inputContainerToCopyFrom.haizDurationInputDatesRows) {
                    copyDurationInputRow(
                        aadat = inputDateRow.durationInput.value,
                        selectedOption = inputDateRow.damOrTuhr,
                        disable = !isDuration,
                        preg = inputContainerToCopyFrom.isNifas)
                }
            } else {
                durationInputRow(false, !isDuration)
            }
        }
    }
}

private fun TagConsumer<HTMLElement>.copyDurationInputRow(aadat: String, selectedOption: String, disable: Boolean, preg: Boolean) {
    tr {
        td {
            input {
                id = Ids.DurationRow.INPUT_DURATION
                name = Ids.DurationRow.INPUT_DURATION
                disabled = disable
                required = true
                value = aadat
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
                    val pregOpts = (event.target as HTMLSelectElement).value in setOf("haml", "wiladat")
                    row.durationInput.value = if (pregOpts) "0" else row.durationInput.value
                    row.durationInput.disabled = pregOpts
                }
                makeDropdownOptions((selectedOption == "dam"), "dam", StringsOfLanguages.ENGLISH.dam, StringsOfLanguages.URDU.dam)
                makeDropdownOptions((selectedOption == "tuhr"), "tuhr", StringsOfLanguages.ENGLISH.tuhr, StringsOfLanguages.URDU.tuhr)
                makeDropdownOptions(
                    (selectedOption == "haml"),
                    "haml",
                    StringsOfLanguages.ENGLISH.preg,
                    StringsOfLanguages.URDU.preg,
                    "is-nifas ${if (!preg) "invisible" else null}"
                )
                makeDropdownOptions(
                    (selectedOption == "wiladat"),
                    "wiladat",
                    StringsOfLanguages.ENGLISH.birthduration,
                    StringsOfLanguages.URDU.birthduration,
                    "is-nifas ${if (!preg) "invisible" else null}"
                )
            }
        }
        addRemoveButtonsTableData(true)
    }
}

private fun TagConsumer<HTMLElement>.inputRow(
    isDateOnlyLayout: Boolean,
    minTimeInput: String,
    maxTimeInput: String,
    disable: Boolean = false) {
    tr {
        td {
            timeInput(isDateOnlyLayout, minTimeInput, maxTimeInput, indexWithinRow = 0) {
                id = Ids.Row.INPUT_START_TIME
                disabled = disable
            }
        }
        td {
            timeInput(isDateOnlyLayout, minTimeInput, maxTimeInput, indexWithinRow = 1) {
                id = Ids.Row.INPUT_END_TIME
                disabled = disable
            }
        }
        addRemoveButtonsTableData()
    }
}

private fun TagConsumer<HTMLElement>.durationInputRow(lastWasDam: Boolean, disable: Boolean, preg: Boolean = false) {
    val urdu = languageSelector.value == "urdu"
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
                    val pregOct = (event.target as HTMLSelectElement).value in setOf("haml", "wiladat")
                    println("preg")
                    println(pregOct)
                    row.durationInput.value = if (pregOct) "0" else row.durationInput.value
                    row.durationInput.disabled = (event.target as HTMLSelectElement).value in setOf("haml", "wiladat")
                }
                makeDropdownOptions(!lastWasDam, "dam", StringsOfLanguages.ENGLISH.dam, StringsOfLanguages.URDU.dam)
                makeDropdownOptions(lastWasDam, "tuhr", StringsOfLanguages.ENGLISH.tuhr, StringsOfLanguages.URDU.tuhr)
                makeDropdownOptions(
                    false,
                    "haml",
                    StringsOfLanguages.ENGLISH.preg,
                    StringsOfLanguages.URDU.preg,
                    "is-nifas ${if (!preg) "invisible" else null}"
                )
                makeDropdownOptions(
                    false,
                    "wiladat",
                    StringsOfLanguages.ENGLISH.birthduration,
                    StringsOfLanguages.URDU.birthduration,
                    "is-nifas ${if (!preg) "invisible" else null}"
                )
            }
        }
        addRemoveButtonsTableData(true)
    }
}

private fun TagConsumer<HTMLElement>.inputRow(
    inputContainerToCopyFrom: HTMLElement,
    startTimeInputToCopyFrom: HTMLInputElement,
    endTimeInputToCopyFrom: HTMLInputElement,
    disable: Boolean = false
) {
    tr {
        td {
            timeInput(inputContainerToCopyFrom, startTimeInputToCopyFrom, indexWithinRow = 0) {
                id = Ids.Row.INPUT_START_TIME
                disabled = disable
            }
        }
        td {
            timeInput(inputContainerToCopyFrom, endTimeInputToCopyFrom, indexWithinRow = 1) {
                id = Ids.Row.INPUT_END_TIME
                disabled = disable
            }
        }
        addRemoveButtonsTableData()
    }
}

private fun TR.addRemoveButtonsTableData(duration: Boolean = false) {
    td {
        id = Ids.Row.BUTTONS_CONTAINER
        addButton(duration)
        removeButton(duration)
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

private fun findInputContainer(event: Event) =
    (event.currentTarget as Element).getAncestor<HTMLElement> { it.id.startsWith(Ids.INPUT_CONTAINER)}!!
private fun findRow(event: Event) = (event.currentTarget as Element).getAncestor<HTMLTableRowElement>()!!

private fun FlowContent.removeButton(duration: Boolean = false) {
    button(type = ButtonType.button, classes = "minus") {
        +"\u274C"
        title = "Remove"
        id = Ids.Row.BUTTON_REMOVE
        onClickFunction = { event ->
            val row = findRow(event)
            val inputContainer = findInputContainer(event)
            if (!duration) {
                updateMinMaxForTimeInputsBeforeRemovingRow(inputContainer, row.rowIndexWithinTableBody)
            }
            row.remove()
            setupFirstRow(inputContainer, duration)
        }
    }
}

private fun FlowContent.addButton(duration: Boolean = false) {
    button(type = ButtonType.button, classes = "plus") {
        +"\u2795"
        title = "Add"
        onClickFunction = { event ->
            val row = findRow(event)
            val inputContainer = findInputContainer(event)
            if (duration) {
                val rowIsDam = row.damOrTuhr in setOf("dam", "haml")
                row.after {
                    durationInputRow(rowIsDam, false, inputContainer.isNifas)
                }
                setupFirstRow(inputContainer, true)
            } else {
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
}

private fun TagConsumer<HTMLElement>.addBeforeButton(duration: Boolean = false) {
    button(type = ButtonType.button, classes = "plus") {
        +"\u2795 \u25B2"
        title = "Add at Start"
        id = Ids.Row.BUTTON_ADD_BEFORE
        onClickFunction = { event ->
            val inputContainer = findInputContainer(event)
            if (duration) {
                val firstIsDam = inputContainer.haizDurationInputDatesRows.first().damOrTuhr in setOf("dam", "wiladat")
                inputContainer.hazDurationInputTableBody.prepend { durationInputRow(firstIsDam, false, inputContainer.isNifas) }
                setupFirstRow(inputContainer, true)
            } else {
                val row = inputContainer.hazInputTableBody.firstChild as HTMLTableRowElement

                inputContainer.hazInputTableBody.prepend {
                    inputRow(
                        inputContainer.isDateOnly,
                        minTimeInput = "",
                        maxTimeInput = row.startTimeInput.run { value.takeUnless(String::isEmpty) ?: max }
                    )
                }
                setupRows(inputContainer)
            }
        }
    }
}

private fun setupRows(inputContainer: HTMLElement) {
    setMaxToCurrentTimeForTimeInputs(inputContainer)
    setupFirstRow(inputContainer, false)
    setupFirstRow(inputContainer, true)
}

private fun setupFirstRow(inputContainer: HTMLElement, duration: Boolean = false) {
    val inputDatesRows = if (duration) inputContainer.haizDurationInputDatesRows else inputContainer.haizInputDatesRows
    inputDatesRows.first().removeButton.visibility = inputDatesRows.size != 1
    inputDatesRows.getOrNull(1)?.removeButton?.visibility = true
}

private fun setMaxToCurrentTimeForTimeInputs(inputContainer: HTMLElement) {
//    val currentTime = currentTimeString(inputContainer.isDateOnly)
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
    if (isDateTime) {
        setMaxToCurrentTimeForTimeInputs(inputContainer)
    }
    switchToDurationTable(inputContainer)
}

private fun switchToDurationTable(inputContainer: HTMLElement, isDuration: Boolean = inputContainer.isDuration) {
    disableDateTable(inputContainer, isDuration)
    inputContainer.haizInputTable.visibility = !isDuration
    inputContainer.haizDurationInputTable.visibility = isDuration
}

private fun disableDateTable(inputContainer: HTMLElement, disable: Boolean = inputContainer.isDuration) {
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
    disableByClass("nonduration-aadat", "invisible", inputContainer, disable)
    disableByMasla(inputContainer)
}

private fun disableByClass(classSelector: String, classInvis: String, inputContainer: HTMLElement, disable: Boolean) {
    inputContainer.getElementsByClassName(classSelector)
        .asList()
        .forEach { row ->
            row.classList.toggle(classInvis, disable)
            row.querySelectorAll("input")
                .asList()
                .map { input ->
                    input as HTMLInputElement
                    input.disabled = disable
                }
        }
}

private fun disableByMasla(inputContainer: HTMLElement) {
    disableByClass("is-nifas", "invisible", inputContainer, !inputContainer.isNifas)
    disableByClass("mutada", "invisible", inputContainer, !inputContainer.isMubtadia)
}

private fun parseEntries(inputContainer: HTMLElement) {
    var entries = listOf<Entry>()

    with(inputContainer) {
        var mawjodahtuhreditable = parseDays(mawjoodaTuhr.value)
        var pregnancyIs = isNifas
        var pregnancyStrt = Date(pregStartTime.valueAsNumber)
        var pregnancyEnd = Date(pregEndTime.valueAsNumber)
        var mubtadiaIs = isMubtadia

        if(isDuration){
            //take arbitrary date
            val arbitraryDate = Date(0,0,0)
            val durations = haizDurationInputDatesRows.map { row ->
                Duration(
                    type = when (row.damOrTuhr) {
                        "dam" -> {DurationType.DAM}
                        "tuhr" -> {DurationType.TUHR}
                        "haml" -> {DurationType.HAML}
                        "wiladat" -> {DurationType.WILADAT_ISQAT}
                        else -> {DurationType.NIFAS}
                    },
                    timeInMilliseconds = parseDays(row.durationInput.value)!!,
                    startTime = arbitraryDate
                ) }
            for (index in durations.indices){
                if(index > 0){
                    durations[index].startTime = durations[index-1].endDate
                }
            }
            if(durations[0].type == DurationType.TUHR){ mawjodahtuhreditable = durations[0].timeInMilliseconds }
            println(durations)
            for(dur in durations){
                when (dur.type) {
                    DurationType.DAM -> {
                        entries += Entry(dur.startTime, dur.endDate)
                    }
                    DurationType.HAML -> {
                        pregnancyIs=true
                        pregnancyStrt=dur.startTime
                    }
                    DurationType.WILADAT_ISQAT -> {
                        pregnancyEnd=dur.startTime
                    }
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
            mubtadiaIs,
            languageSelector.value,
            isDuration,
            ikhtilaf1,
            ikhtilaf2
        )
        contentContainer.visibility = true
        contentEnglish.innerHTML = replaceBoldTagWithBoldAndStar("${output.englishText}")
        contentUrdu.innerHTML = replaceBoldTagWithBoldAndStar("${output.urduText}")
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
