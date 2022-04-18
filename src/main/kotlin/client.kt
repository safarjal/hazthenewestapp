@file:Suppress("SpellCheckingInspection")

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
    const val LANGUAGE = "language"

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
        const val IKHTILAF3 = "ikhtilaf3"
        const val IKHTILAF4 = "ikhtilaf4"
    }

    const val CONTENT_CONTAINER = "content_container"
    const val CONTENT_WRAPPER = "content_wrapper"
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
}

object CssC {
    const val INVIS = "invisible"                   // Invis. Put on any element that shouldn't show; also doable by elem.visibility
    const val LANG_INVIS = "lang-invisible"         // Invis. Put on any element that shouldn't show because of lang

    const val ENGLISH = "english"                   // Switch. Put on any element that should only show when lang is english
    const val URDU = "urdu"                         // Switch. Put on any element that should only show when lang is urdu
    const val DEV = "dev"                           // Switch. Put on any element that should only show when devmode
    const val RTL = "rtl"                           // Switch. Put on any element that should switch rtl but NOT invis

    const val NIFAS = "nifas"                       // Switch. Put on any input that only shows when Nifas
    const val MUTADA = "mutada"                     // Switch. Put on any input that only shows when NOT Mubtadia
    const val DATETIME_AADAT = "datetime_aadat"     // Switch. Put on any input that only shows when NOT Duration

    const val ROW = "row"                           // CSS Style. Make nice alternating colorful rows of inputs
    const val IKHTILAF = "ikhtilaf"                 // CSS Style. Makes the gearbox icon on the detail
    const val LEFT = "left"                         // CSS Style.
    const val RIGHT = "right"                       // CSS Style.
    const val CALC_BTN = "calc-btn"                 // CSS Style.
    const val PLUS = "plus"                         // CSS Style.
    const val MINUS = "minus"                       // CSS Style.
    const val SWITCH = "switch"                     // CSS Style.
    const val SLIDER = "slider"                     // CSS Style.
    const val ROUND = "round"                       // CSS Style.
    const val LABEL_INPUT = "label-input"           // CSS Style.
}

object Vls {                                        // Values
    object Langs {
        const val ENGLISH = "english"
        const val URDU = "urdu"
    }
    object Maslas {
        const val MUTADA = "mutada"
        const val NIFAS = "nifas"
        const val MUBTADIA = "mubtadia"
    }
    object Types {
        const val DATE_ONLY = "dateOnly"
        const val DATE_TIME = "dateTime"
        const val DURATION = "duration"
    }
    object Opts {                                   // Options for duration dropdowns
        const val DAM = "dam"
        const val TUHR = "tuhr"
        const val HAML = "haml"
        const val WILADAT = "wiladat"
    }
}

private val inputsContainersContainer get() = document.getElementById(Ids.INPUT_CONTAINERS_CONTAINER) as HTMLElement
@Suppress("UNCHECKED_CAST")
private val inputsContainers get() = inputsContainersContainer.children.asList() as List<HTMLElement>
private val comparisonContainer get() = document.getElementById(Ids.COMPARISON_CONTAINER) as HTMLElement?

private val contentDatesDifferenceElement get() = document.getElementById(Ids.CONTENT_DATES_DIFFERENCE) as HTMLParagraphElement?
private val datesDifferenceTableElement get() = document.getElementById(Ids.DATES_DIFFERENCE_TABLE) as HTMLElement?
private val root_hazapp = document.getElementsByClassName("root").asList()
private val languageSelector get() = document.getElementById(Ids.LANGUAGE) as HTMLSelectElement

private val HTMLElement.haizInputTable get() = getChildById(Ids.HAIZ_INPUT_TABLE) as HTMLTableElement
private val HTMLElement.haizDurationInputTable get() = getChildById(Ids.HAIZ_DURATION_INPUT_TABLE) as HTMLTableElement

private val HTMLElement.isDateTime get() = (getChildById(Ids.INPUT_TYPE_SELECT) as HTMLSelectElement).value == Vls.Types.DATE_TIME
private val HTMLElement.isDateOnly get() = (getChildById(Ids.INPUT_TYPE_SELECT) as HTMLSelectElement).value == Vls.Types.DATE_ONLY
private val HTMLElement.isDuration get() = (getChildById(Ids.INPUT_TYPE_SELECT) as HTMLSelectElement).value == Vls.Types.DURATION

private val HTMLElement.isMutada get() = (getChildById(Ids.MASLA_TYPE_SELECT) as HTMLSelectElement).value == Vls.Maslas.MUTADA
private val HTMLElement.isNifas get() = (getChildById(Ids.MASLA_TYPE_SELECT) as HTMLSelectElement).value == Vls.Maslas.NIFAS
private val HTMLElement.isMubtadia get() = (getChildById(Ids.MASLA_TYPE_SELECT) as HTMLSelectElement).value == Vls.Maslas.MUBTADIA

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
private val HTMLElement.ikhtilaf3 get() = (getChildById(Ids.Ikhtilafat.IKHTILAF3) as HTMLInputElement).checked
private val HTMLElement.ikhtilaf4 get() = (getChildById(Ids.Ikhtilafat.IKHTILAF4) as HTMLInputElement).checked

private var HTMLElement.haizDatesList: List<Entry>?
    get() = (contentDatesElement.asDynamic().haizDatesList as List<Entry>?)?.takeIf { it != undefined }
    set(value) { contentDatesElement.asDynamic().haizDatesList = value }

private val englishElements get() = document.getElementsByClassName(CssC.ENGLISH).asList()
private val urduElements get() = document.getElementsByClassName(CssC.URDU).asList()
private val languageElements get() = listOf(englishElements, urduElements).flatten()
private val devElements get() = document.getElementsByClassName(CssC.DEV).asList()

private val HTMLElement.hazInputTableBody: HTMLTableSectionElement get() {
        val inputDatesTable = getChildById(Ids.HAIZ_INPUT_TABLE) as HTMLTableElement
        return inputDatesTable.tBodies[0] as HTMLTableSectionElement
    }
private val HTMLElement.hazDurationInputTableBody: HTMLTableSectionElement get() {
        val inputDatesTable = getChildById(Ids.HAIZ_DURATION_INPUT_TABLE) as HTMLTableElement
        return inputDatesTable.tBodies[0] as HTMLTableSectionElement
    }

private val HTMLElement.haizInputDatesRows: List<HTMLTableRowElement> get() {
        @Suppress("UNCHECKED_CAST")
        return hazInputTableBody.rows.asList() as List<HTMLTableRowElement>
    }
private val HTMLElement.haizDurationInputDatesRows: List<HTMLTableRowElement> get() {
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
        if (root_hazapp.isNotEmpty() && askPassword()) {    // Hazapp Page
            document.body!!.addInputLayout()
            setupRows(inputsContainers.first())
            document.addEventListener(Events.VISIBILITY_CHANGE, {
                if (!document.isHidden) {
                    setMaxToCurrentTimeForTimeInputs(inputsContainers.first())
                }
            })
        } else mainOtherCalcs()                             // Other Calcs Page

        parseHREF()
    }
}

fun askPassword():Boolean {
    val pass1 = "786"
    val password = window.prompt("${StringsOfLanguages.ENGLISH.warningOnlyAuthorizedPersonnel}\n\n" +
            "${StringsOfLanguages.URDU.warningOnlyAuthorizedPersonnel}\n\n" +
            "${StringsOfLanguages.URDU.passwordRequired}\n\n", "")
    return pass1 == password || askPassword()
}

fun parseHREF() {
    // DEVMODE
    for (element in devElements) element.visibility = window.location.href.contains("dev")

    // DEFAULT LANGUAGE
    languageSelector.onchange = { languageChange() }
    if (window.location.href.contains("lang=en")) languageSelector.value = Vls.Langs.ENGLISH
    languageChange()
}

fun languageChange() {
    val lang = languageSelector.value
    // Invis every language dependent element based on if it DOESN'T have the selected language: // TODO: Make better.
    for (element in languageElements) element.classList.toggle(CssC.LANG_INVIS, !element.classList.contains(lang))

    document.body!!.classList.toggle(CssC.RTL, lang == Vls.Langs.URDU) // RTL on body if Urdu
    document.querySelectorAll("select") // Selected options don't invis so switch them.
        .asList()
        .map { it as HTMLSelectElement }
        .forEach { select ->
            select.children
                .asList()
                .map { it as HTMLOptionElement }
                // Same val as selected option, different lang:
                .firstOrNull { option -> option.value == select.value && option.classList.contains(lang) }
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
    if (inputsContainers.size == 1) addRemoveInputsContainerButton(inputsContainerToCopyFrom)

    val clonedInputsContainer = inputsContainerToCopyFrom.after {
        inputFormDiv(inputsContainerToCopyFrom)
    }.single()

    // Make sure all invises are maintained
    languageChange()
    disableTree(clonedInputsContainer)
    setupFirstRow(clonedInputsContainer, inputsContainerToCopyFrom.isDuration)
}

private fun addRemoveInputsContainerButton(inputContainer: HTMLElement) {
    inputContainer.inputsContainerCloneButton.before {
        button(type = ButtonType.button, classes = "${CssC.MINUS} ${CssC.DEV}") {
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
    div(classes = Ids.INPUT_CONTAINER) {
        id = Ids.INPUT_CONTAINER
        if (inputContainerToCopyFrom != null) {
            removeInputsContainerButton()
        }
        addInputsContainerButton()
        inputForm(inputContainerToCopyFrom)
        content()
    }
}

private fun TagConsumer<HTMLElement>.addInputsContainerButton() {
    inputsContainerAddRemoveButton {
        +"Clone"
        classes = setOf(CssC.PLUS, CssC.DEV)
        id = Ids.INPUTS_CONTAINER_CLONE_BUTTON
        onClickFunction = { event ->
            cloneInputsContainer(findInputContainer(event))
        }
    }
}

private fun TagConsumer<HTMLElement>.removeInputsContainerButton() {
    inputsContainerAddRemoveButton {
        +"\u274C"
        classes = setOf(CssC.MINUS)
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
private fun FlowContent.copyBtn(divClass:String, btnClass: String? = null) {
    div(classes = divClass) {
        small(classes = btnClass)
        button(classes = btnClass) {
            onClickFunction = { event -> copyText(event) }
            +"Copy ⎙"
        }
    }
}
private fun TagConsumer<HTMLElement>.content() {
    div(classes = CssC.INVIS) {
        id = Ids.CONTENT_CONTAINER
        div(classes = CssC.URDU) {
            id = Ids.CONTENT_WRAPPER
            copyBtn(CssC.LEFT, CssC.RTL)
            content { id = Ids.CONTENT_URDU }
        }
        div(classes = CssC.ENGLISH) {
            id = Ids.CONTENT_WRAPPER
            copyBtn(CssC.RIGHT)
            content { id = Ids.CONTENT_ENGLISH }
        }
        hr()
        content { id = Ids.CONTENT_DATES }
        hr()
    }
}

private fun getNow(): String {
    var dateStr = ""
    if (languageSelector.value == Vls.Langs.URDU){
        dateStr = urduDateFormat(Date(Date.now()),TypesOfInputs.DATE_ONLY)
    }else if(languageSelector.value == Vls.Langs.ENGLISH){
        dateStr = englishDateFormat(Date(Date.now()),TypesOfInputs.DATE_ONLY)
    }
    dateStr += " ${Date(Date.now()).getFullYear()}"
    return dateStr
}
private fun copyText(event: Event) {
    val div = (event.currentTarget as HTMLElement).getAncestor<HTMLDivElement> { it.id == "content_wrapper" }

    val dateStr = getNow()
    val questionTxt = findInputContainer(event).questionText.value
    val divider = "${UnicodeChars.BLUE_SWIRL}➖➖➖➖➖${UnicodeChars.BLUE_SWIRL}"
    val answerTxt = div?.querySelector("p")?.textContent

    val copyTxt = "*${dateStr}*\n\n${questionTxt}\n\n${divider}\n\n${answerTxt}"
    copyTxt.let { window.navigator.clipboard.writeText(it) }

    val small = div?.querySelector("small")
    small?.innerHTML?.let { small.innerHTML = " Copied " }

    window.setTimeout({ if (small != null) small.innerHTML = "" }, 1000)
}

private fun TagConsumer<HTMLElement>.content(classes: String? = null, block : P.() -> Unit = {}) {
    p(classes = classes) {
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
        div(classes = CssC.LABEL_INPUT) {
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

private fun FlowContent.makeLabel(inputId: String, englishText: String, urduText: String, extraClasses: String = "", block: LABEL.() -> Unit = {}) {
    label {
        htmlFor = inputId
        classes = setOf(CssC.ENGLISH, extraClasses)
        block()
        +englishText
    }
    label {
        htmlFor = inputId
        classes = setOf(CssC.URDU, extraClasses)
        block()
        +urduText
    }
}

private fun FlowContent.makeIkhtilafiMasla(inputId: String, englishText: String, urduText: String, extraClasses: String? = null, block: DIV.() -> Unit = {}) {
    div(classes = "${CssC.ROW} $extraClasses") {
        div {
            makeLabel(inputId, englishText, urduText)
            label(classes = CssC.SWITCH) {
                checkBoxInput {
                    id = inputId
                }
                span(classes = "${CssC.SLIDER} ${CssC.ROUND}")
            }
        }
        block()
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

private fun TagConsumer<HTMLElement>.makeDropdownOptions(
    isSelected: Boolean,
    optionVal: String,
    englishText: String,
    urduText: String,
    extraClasses: String = "",
    block: OPTION.() -> Unit = {}
) {
    option {
        classes = setOfNotNull(
            CssC.ENGLISH,
            extraClasses,
            if (languageSelector.value != Vls.Langs.ENGLISH) CssC.LANG_INVIS else null
        )
        selected = isSelected && languageSelector.value == Vls.Langs.ENGLISH
        value = optionVal
        block()
        +englishText
    }
    option {
        classes = setOfNotNull(
            CssC.URDU,
            extraClasses,
            if (languageSelector.value != Vls.Langs.URDU) CssC.LANG_INVIS else null
        )
        selected = isSelected && languageSelector.value == Vls.Langs.URDU
        value = optionVal
        block()
        +urduText
    }
}

private fun TagConsumer<HTMLElement>.maslaConfigurationSelectDropdown(inputContainerToCopyFrom: HTMLElement?) {
    val isMutada = inputContainerToCopyFrom?.isMutada ?: IS_DEFAULT_INPUT_MODE_MUTADA
    val isNifas = inputContainerToCopyFrom?.isNifas ?: !IS_DEFAULT_INPUT_MODE_MUTADA
    val isMubtadia = inputContainerToCopyFrom?.isMubtadia ?: !IS_DEFAULT_INPUT_MODE_MUTADA
    div(classes = CssC.ROW) {
        makeLabel(Ids.MASLA_TYPE_SELECT, StringsOfLanguages.ENGLISH.typeOfMasla, StringsOfLanguages.URDU.typeOfMasla)
        select {
            id = Ids.MASLA_TYPE_SELECT
            onChangeFunction = { event -> disableTree(findInputContainer(event)) }
            makeDropdownOptions(isMutada, Vls.Maslas.MUTADA, StringsOfLanguages.ENGLISH.mutada, StringsOfLanguages.URDU.mutada)
            makeDropdownOptions(isNifas, Vls.Maslas.NIFAS, StringsOfLanguages.ENGLISH.nifas, StringsOfLanguages.URDU.nifas)
            makeDropdownOptions(isMubtadia, Vls.Maslas.MUBTADIA, StringsOfLanguages.ENGLISH.mubtadia, StringsOfLanguages.URDU.mubtadia, "dev")
        }
    }
}

private fun TagConsumer<HTMLElement>.typeConfigurationSelectDropdown(inputContainerToCopyFrom: HTMLElement?) {
    val isDateTime = inputContainerToCopyFrom?.isDateTime ?: !IS_DEFAULT_INPUT_MODE_DATE_ONLY
    val isDateOnly = inputContainerToCopyFrom?.isDateOnly ?: IS_DEFAULT_INPUT_MODE_DATE_ONLY
    val isDuration = inputContainerToCopyFrom?.isDuration ?: !IS_DEFAULT_INPUT_MODE_DATE_ONLY
    div(classes = CssC.ROW) {
        makeLabel(Ids.INPUT_TYPE_SELECT, StringsOfLanguages.ENGLISH.typeOfInput, StringsOfLanguages.URDU.typeOfInput)
        select {
            id = Ids.INPUT_TYPE_SELECT
            onChangeFunction = { event ->
                onClickTypeConfigurationSelectDropdown(findInputContainer(event))
            }
            makeDropdownOptions(isDateOnly, Vls.Types.DATE_ONLY, StringsOfLanguages.ENGLISH.dateOnly, StringsOfLanguages.URDU.dateOnly)
            makeDropdownOptions(isDateTime, Vls.Types.DATE_TIME, StringsOfLanguages.ENGLISH.dateAndTime, StringsOfLanguages.URDU.dateAndTime)
            makeDropdownOptions(isDuration, Vls.Types.DURATION, StringsOfLanguages.ENGLISH.duration, StringsOfLanguages.URDU.duration)
        }
    }
}

private fun FlowContent.pregnancyTimeInput(inputContainerToCopyFrom: HTMLElement?, inputId: String, block: INPUT.() -> Unit = {}) {
    if (inputContainerToCopyFrom != null) {
        timeInput(inputContainerToCopyFrom) {
            disabled = !inputContainerToCopyFrom.isNifas
            block()
        }
    } else {
        timeInput(IS_DEFAULT_INPUT_MODE_DATE_ONLY) {
            disabled = true
            id = inputId
            name = inputId
//            max = currentTimeString(IS_DEFAULT_INPUT_MODE_DATE_ONLY)
            block()
        }
    }
}

private fun FlowContent.makeNumberInput(inputId: String, inputVal: String?, inputRange: IntRange, block: INPUT.() -> Unit = {}) {
    input {
        id = inputId
        name = inputId
        value = inputVal.orEmpty()
        onInputFunction = { event -> (event.currentTarget as HTMLInputElement).validateAadat(inputRange) }
        block()
    }
}

private fun FlowContent.nifasInputs(inputContainerToCopyFrom: HTMLElement?) {
    // Pregnancy Start Time
    div(classes = "${CssC.ROW} ${CssC.NIFAS} ${CssC.INVIS} ${CssC.DATETIME_AADAT}") {
        makeLabel(Ids.PREG_START_TIME_INPUT, StringsOfLanguages.ENGLISH.pregnancyStartTime, StringsOfLanguages.URDU.pregnancyStartTime)
        pregnancyTimeInput(inputContainerToCopyFrom, Ids.PREG_START_TIME_INPUT) {
            onChangeFunction = { event ->
                findInputContainer(event).pregEndTime.min = (event.currentTarget as HTMLInputElement).value
            }
        }
    }

    // Pregnancy End Time
    div(classes = "${CssC.ROW} ${CssC.DATETIME_AADAT} ${CssC.NIFAS} ${CssC.INVIS}") {
        makeLabel(Ids.PREG_END_TIME_INPUT, StringsOfLanguages.ENGLISH.birthMiscarrriageTime, StringsOfLanguages.URDU.birthMiscarrriageTime)
        pregnancyTimeInput(inputContainerToCopyFrom, Ids.PREG_END_TIME_INPUT) {
            onChangeFunction = { event ->
                findInputContainer(event).pregStartTime.max = (event.currentTarget as HTMLInputElement).value
            }
        }
    }

    // Pregnancy Mustabeen ul Khilqa?
    div(classes = "${CssC.ROW} ${CssC.NIFAS} ${CssC.INVIS}"){
        div {
            makeLabel(Ids.MUSTABEEN_CHECKBOX, StringsOfLanguages.ENGLISH.mustabeenUlKhilqa, StringsOfLanguages.URDU.mustabeenUlKhilqa)
            checkBoxInput {
                id = Ids.MUSTABEEN_CHECKBOX
                name = Ids.MUSTABEEN_CHECKBOX
                checked = inputContainerToCopyFrom?.mustabeen != false
            }
        }
    }

    // Pregnancy Aadat
    div(classes = "${CssC.ROW} ${CssC.NIFAS} ${CssC.INVIS}") {
        makeLabel(Ids.AADAT_NIFAS_INPUT, StringsOfLanguages.ENGLISH.nifasAadat, StringsOfLanguages.URDU.nifasAadat)
        makeNumberInput(Ids.AADAT_NIFAS_INPUT, inputContainerToCopyFrom?.aadatNifas?.value.orEmpty(), (1..40)) {
            step = "any"
            required = false
            disabled = inputContainerToCopyFrom?.isNifas != true
        }
    }
}

private fun FlowContent.mutadaInputs(inputContainerToCopyFrom: HTMLElement?) {
    // Aadat of Haiz
    div(classes = "${CssC.ROW}") {
        makeLabel(Ids.AADAT_HAIZ_INPUT, StringsOfLanguages.ENGLISH.haizAadat, StringsOfLanguages.URDU.haizAadat)
        makeNumberInput(Ids.AADAT_HAIZ_INPUT, inputContainerToCopyFrom?.aadatHaz?.value.orEmpty(), (3..10))
    }
    // Aadat of Tuhr
    div(classes = "${CssC.ROW} ${CssC.MUTADA}") {
        makeLabel(Ids.AADAT_TUHR_INPUT, StringsOfLanguages.ENGLISH.tuhrAadat, StringsOfLanguages.URDU.tuhrAadat)
        makeNumberInput(Ids.AADAT_TUHR_INPUT, inputContainerToCopyFrom?.aadatTuhr?.value.orEmpty(), (15..6 * 30))
    }
    // Mawjooda Tuhr
    div(classes = "${CssC.ROW} ${CssC.DATETIME_AADAT}") {
        makeLabel(Ids.MAWJOODA_TUHR_INPUT, StringsOfLanguages.ENGLISH.mawjoodahTuhr, StringsOfLanguages.URDU.mawjoodahTuhr)
        makeNumberInput(Ids.MAWJOODA_TUHR_INPUT, inputContainerToCopyFrom?.mawjoodaTuhr?.value.orEmpty(), (15..10000))
        // Fasid?
        div {
            makeLabel(Ids.MAWJOODA_FASID_CHECKBOX, StringsOfLanguages.ENGLISH.faasid, StringsOfLanguages.URDU.faasid)
            checkBoxInput {
                id = Ids.MAWJOODA_FASID_CHECKBOX
                name = Ids.MAWJOODA_FASID_CHECKBOX
                checked = inputContainerToCopyFrom?.isMawjoodaFasid?.or(false) == true
            }
        }
    }
}

fun HTMLInputElement.validateAadat(validityRange: ClosedRange<Int>) {
    val errormessage = if(languageSelector.value == Vls.Langs.ENGLISH) { StringsOfLanguages.ENGLISH.incorrectAadat } else {StringsOfLanguages.URDU.incorrectAadat}
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
    button(classes = "${CssC.ENGLISH} ${CssC.CALC_BTN}") {
        +StringsOfLanguages.ENGLISH.calculate
        onClickFunction = { event -> setMaxToCurrentTimeForTimeInputs(findInputContainer(event)) }
    }
    button(classes = "${CssC.URDU} ${CssC.CALC_BTN}") {
        +StringsOfLanguages.URDU.calculate
        onClickFunction = { event -> setMaxToCurrentTimeForTimeInputs(findInputContainer(event)) }
    }
}

private fun TagConsumer<HTMLElement>.makeSpans(englishText: String, urduText: String, block: SPAN.() -> Unit = {}) {
    span(classes = CssC.ENGLISH) {
        block()
        +englishText
    }
    span(classes = CssC.URDU) {
        block()
        +urduText
    }
}

private fun TagConsumer<HTMLElement>.questionInput(inputContainerToCopyFrom: HTMLElement?) {
    details {
        summary {
            makeSpans("Question", "سوال")
        }
        div(classes = CssC.ROW) {
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
        classes = setOf( if (isDuration) CssC.INVIS else "" )
        thead {
            tr {
                th { makeSpans(StringsOfLanguages.ENGLISH.startTime, StringsOfLanguages.URDU.startTime) }
                th { makeSpans(StringsOfLanguages.ENGLISH.endTime, StringsOfLanguages.URDU.endTime) }
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
        classes = setOf( if (!isDuration) CssC.INVIS else "" )
        thead {
            tr {
                th { makeSpans(StringsOfLanguages.ENGLISH.duration, StringsOfLanguages.URDU.duration) }
                th { makeSpans(StringsOfLanguages.ENGLISH.damOrTuhr, StringsOfLanguages.URDU.damOrTuhr) }
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
            } else { durationInputRow(false, !isDuration) }
        }
    }
}

private fun onChangeDurationSelect(event: Event) {
    val row = findRow(event)
    val pregOct = (event.target as HTMLSelectElement).value in setOf(Vls.Opts.HAML, Vls.Opts.WILADAT)
    row.durationInput.value = if (pregOct) "0" else row.durationInput.value
    row.durationInput.disabled = (event.target as HTMLSelectElement).value in setOf(Vls.Opts.HAML, Vls.Opts.WILADAT)
}

private fun TagConsumer<HTMLElement>.makeDurationSelect(disable: Boolean, selectedOption: String, preg: Boolean) {
    select {
        id = Ids.DurationRow.INPUT_TYPE_OF_DURATION
        name = Ids.DurationRow.INPUT_TYPE_OF_DURATION
        disabled = disable
        onChangeFunction = { event -> onChangeDurationSelect(event) }
        makeDropdownOptions(selectedOption == Vls.Opts.DAM, Vls.Opts.DAM, StringsOfLanguages.ENGLISH.dam, StringsOfLanguages.URDU.dam)
        makeDropdownOptions(selectedOption == Vls.Opts.TUHR, Vls.Opts.TUHR, StringsOfLanguages.ENGLISH.tuhr, StringsOfLanguages.URDU.tuhr)
        makeDropdownOptions(
            selectedOption == Vls.Opts.HAML,
            Vls.Opts.HAML,
            StringsOfLanguages.ENGLISH.pregduration,
            StringsOfLanguages.URDU.pregduration,
            CssC.NIFAS + " " + if (!preg) CssC.INVIS else null
        )
        makeDropdownOptions(
            selectedOption == Vls.Opts.WILADAT,
            Vls.Opts.WILADAT,
            StringsOfLanguages.ENGLISH.birthduration,
            StringsOfLanguages.URDU.birthduration,
            CssC.NIFAS + " " + if (!preg) CssC.INVIS else null
        )
    }
}

private fun TagConsumer<HTMLElement>.copyDurationInputRow(aadat: String, selectedOption: String, disable: Boolean, preg: Boolean) {
    tr {
        td {
            makeNumberInput(Ids.DurationRow.INPUT_DURATION, aadat, (0..10000)) {
                required = true
                disabled = disable
            }
        }
        td {
            makeDurationSelect(disable, selectedOption, preg)
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
    tr {
        td {
            makeNumberInput(Ids.DurationRow.INPUT_DURATION, "", (0..10000)) {
                disabled = disable
                required = true
            }
        }
        td { makeDurationSelect(disable, if (lastWasDam) Vls.Opts.TUHR else Vls.Opts.DAM, preg) }
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
    button(type = ButtonType.button, classes = CssC.MINUS) {
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
    button(type = ButtonType.button, classes = CssC.PLUS) {
        +"\u2795"
        title = "Add"
        onClickFunction = { event ->
            val row = findRow(event)
            val inputContainer = findInputContainer(event)
            if (duration) {
                val rowIsDam = row.damOrTuhr in setOf(Vls.Opts.DAM, Vls.Opts.HAML)
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
    button(type = ButtonType.button, classes = CssC.PLUS) {
        +"\u2795 \u25B2"
        title = "Add at Start"
        id = Ids.Row.BUTTON_ADD_BEFORE
        onClickFunction = { event ->
            val inputContainer = findInputContainer(event)
            if (duration) {
                val firstIsDam = inputContainer.haizDurationInputDatesRows.first().damOrTuhr in setOf(Vls.Opts.DAM, Vls.Opts.WILADAT)
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
//    disableByClass(CssC.DATETIME_AADAT, CssC.DUR_INVIS, inputContainer, disable)
    disableTree(inputContainer)
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
                    input.value = ""
                }
        }
}
private fun disableByMasla(inputContainer: HTMLElement) {
    disableByClass(CssC.NIFAS, CssC.INVIS, inputContainer, !inputContainer.isNifas)
    disableByClass(CssC.MUTADA, CssC.INVIS, inputContainer, inputContainer.isMubtadia)
}

private fun disableTree(inputContainer: HTMLElement) {
    val isNifas = inputContainer.isNifas
    val isMubtadia = inputContainer.isMubtadia
    val isDateTime = !inputContainer.isDuration

    disableByClass(CssC.DATETIME_AADAT, CssC.INVIS, inputContainer, !isDateTime)
    disableByMasla(inputContainer)
    disableByClass("${CssC.DATETIME_AADAT} ${CssC.NIFAS}", CssC.INVIS, inputContainer, !isNifas || !isDateTime)
    disableByClass("${CssC.DATETIME_AADAT} ${CssC.MUTADA}", CssC.INVIS, inputContainer, isMubtadia || !isDateTime)

    val mawjoodaFasidCheck = inputContainer.getChildById(Ids.MAWJOODA_FASID_CHECKBOX) as HTMLInputElement
    if (inputContainer.isMubtadia) {
        mawjoodaFasidCheck.checked = true
        mawjoodaFasidCheck.disabled = true
    }
}

private fun parseEntries(inputContainer: HTMLElement) {
    var entries = listOf<Entry>()

    with(inputContainer) {
        val pregnancyStrt = Date(pregStartTime.valueAsNumber)
        val pregnancyEnd = Date(pregEndTime.valueAsNumber)

        val typeOfMasla:TypesOfMasla = if(isMubtadia){
            TypesOfMasla.MUBTADIA
        } else if(isNifas){
            TypesOfMasla.NIFAS
        } else{
            TypesOfMasla.MUTADAH
        }
        val typesOfInputs:TypesOfInputs = if(isDateOnly){
            TypesOfInputs.DATE_ONLY
        } else if(isDuration){
            TypesOfInputs.DURATION
        }else{TypesOfInputs.DATE_AND_TIME}

        val preMaslaValues = PreMaslaValues(
            parseDays(aadatHaz.value),
            parseDays(aadatTuhr.value),
            parseDays(mawjoodaTuhr.value),
            isMawjoodaFasid
        )
        val ikhtilaafaat = Ikhtilaafaat(
            ikhtilaf1,
            ikhtilaf2,
            ikhtilaf3,
            ikhtilaf4)

        val pregnancy = Pregnancy(
            pregnancyStrt,
            pregnancyEnd,
            parseDays(aadatNifas.value),
            mustabeen
        )

        var allTheInputs:AllTheInputs=AllTheInputs()

        if(typesOfInputs==TypesOfInputs.DURATION){
            val durations = haizDurationInputDatesRows.map { row ->
                Duration(
                    type = when (row.damOrTuhr) {
                        Vls.Opts.DAM -> {DurationType.DAM}
                        Vls.Opts.TUHR -> {DurationType.TUHR}
                        Vls.Opts.HAML -> {DurationType.HAML}
                        Vls.Opts.WILADAT -> {DurationType.WILADAT_ISQAT}
                        else -> {DurationType.NIFAS}
                    },
                    timeInMilliseconds = parseDays(row.durationInput.value)!!
                ) }
            allTheInputs = AllTheInputs(
                entries,
                preMaslaValues,
                typeOfMasla,
                pregnancy,
                typesOfInputs,
                languageSelector.value,
                ikhtilaafaat)
            allTheInputs = convertDurationsIntoEntries(durations, allTheInputs)
        }else{
            entries = haizInputDatesRows.map { row ->
                Entry(
                    startTime = Date(row.startTimeInput.valueAsNumber),
                    endTime = Date(row.endTimeInput.valueAsNumber)
                )
            }
            allTheInputs = AllTheInputs(
                entries,
                preMaslaValues,
                typeOfMasla,
                pregnancy,
                typesOfInputs,
                languageSelector.value,
                ikhtilaafaat)
        }


        @Suppress("UnsafeCastFromDynamic")
        var output:OutputTexts
        if(allTheInputs.entries!=null){
            output = handleEntries(allTheInputs)
        }else{
            output = NO_OUTPUT
        }
        contentContainer.visibility = true
        contentEnglish.innerHTML = replaceBoldTagWithBoldAndStar(output.englishText)
        contentUrdu.innerHTML = replaceBoldTagWithBoldAndStar(output.urduText)
        haizDatesList = output.hazDatesList
    }
    addCompareButtonIfNeeded()
}
fun validateNifasDurations(durations:List<Duration>):Boolean{
    //this is ensuring that we have both pregnancy and birth, and only one of each.

    //I am wondering if, if preg or birth, or both are missing, we can just arbitrarily add them to the start of the masla.
    //it seems possible, but idk if that is what we want.


    var pregnancy=false
    var wiladatIsqat=false
    for(duration in durations){
        if(duration.type==DurationType.HAML){
            if(pregnancy){
                window.alert("You can only solve one pregnancy per masla")
                return false
            }else{//is false
                pregnancy=true
            }
        }else if(duration.type==DurationType.WILADAT_ISQAT){
            if(wiladatIsqat){
                window.alert("You can only solve one birth at a time")
                return false
            }else if(!pregnancy){
                window.alert("Please add pregnancy before birth")
                return false
            }else{//is false
                wiladatIsqat=true
            }
        }
    }
    if(!pregnancy||!wiladatIsqat){
        window.alert("You need to add pregnancy and birth/miscarriage to solve a nifas question.")
        return false
    }
    return true
}


fun convertDurationsIntoEntries(durations:List<Duration>, allTheOriginalInputs: AllTheInputs):AllTheInputs{
    //Todo:validate pregnancy start and endtimes and deal with them appropraitely
    if(allTheOriginalInputs.typeOfMasla==TypesOfMasla.NIFAS){
        if(!validateNifasDurations(durations)){
            return AllTheInputs(null)
        }
    }
    for (index in durations.indices){
        if(index > 0){
            durations[index].startTime = durations[index-1].endDate
        }
    }
    var mawjodahtuhreditable:Long?=allTheOriginalInputs.preMaslaValues.inputtedMawjoodahTuhr
    val entries= mutableListOf<Entry>()
    var pregnancyEnd = ARBITRARY_DATE
    var pregnancyStrt:Date = ARBITRARY_DATE
    if(durations[0].type == DurationType.TUHR){ mawjodahtuhreditable = durations[0].timeInMilliseconds }
    for(dur in durations){
        when (dur.type) {
            DurationType.DAM -> {
                entries += Entry(dur.startTime, dur.endDate)
            }
            DurationType.HAML -> {
                pregnancyStrt=dur.startTime

            }
            DurationType.WILADAT_ISQAT -> {
                pregnancyEnd=dur.startTime
            }
        }
    }
    val newPreMaslaValues = PreMaslaValues(
        allTheOriginalInputs.preMaslaValues.inputtedAadatHaiz,
        allTheOriginalInputs.preMaslaValues.inputtedAadatTuhr,
        mawjodahtuhreditable,
        allTheOriginalInputs.preMaslaValues.isMawjoodaFasid
    )
    var newPregnancy:Pregnancy? = null
    if(allTheOriginalInputs.pregnancy!=null){
        newPregnancy= Pregnancy(
            pregnancyStrt,
            pregnancyEnd,
            allTheOriginalInputs.pregnancy.aadatNifas,
            allTheOriginalInputs.pregnancy.mustabeenUlKhilqat
        )
    }
    return AllTheInputs(entries,
        newPreMaslaValues,
        allTheOriginalInputs.typeOfMasla,
        newPregnancy,
        TypesOfInputs.DURATION,
        allTheOriginalInputs.language,
        allTheOriginalInputs.ikhtilaafaat
    )
}

fun replaceBoldTagWithBoldAndStar(string: String): String {
    return string.replace("<b>", "<b><span class='${CssC.INVIS}'>*</span>")
        .replace("</b>", "<span class='${CssC.INVIS}'>*</span></b>")
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
