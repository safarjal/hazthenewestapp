@file:Suppress("SpellCheckingInspection")

//package react

import kotlinx.html.dom.append
import kotlinx.html.*
import kotlinx.html.js.*
import kotlinx.html.tr
import org.w3c.dom.*
import react.*
import kotlinx.browser.document
import react.dom.render
import react.dom.events.ChangeEvent
import react.dom.html.InputType
import react.dom.html.ReactHTML
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.details
import react.dom.html.ReactHTML.summary
import react.dom.html.ReactHTML.label
import react.dom.html.ReactHTML.input
import react.dom.html.ReactHTML.option
import react.dom.html.ReactHTML.select
//import kotlinx.serialization.Serializable

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
    val inputId = "abc" // uuid4().toString()
    form {
        action = "javascript:void(0);"
        autoComplete = false
//        ikhtilafiMasle
        br()
        div(classes = CssC.LABEL_INPUT) {
            id = inputId
//            next(inputId)
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

fun next(inputId: String) {
    val inputDiv = document.getChildById(inputId)
    render(Inputs.create(), inputDiv!!)
}

external interface ChangeHandlerProp : Props {
    var changeHandler: () -> Unit
}
private val Inputs = FC<Props> {
    var masla : String by useState(InputState.masla)
    var type : String by useState(InputState.type)
    fun typeChange(newType: String) { type = newType }
    fun maslaChange(newType: String) { masla = newType }

    MaslaConfigDropdown { changeHandler = maslaChange }
    TypeConfigDropdown { changeHandler = typeChange }
    if(masla == Vls.Maslas.NIFAS) {
        NifasInputs
        println("nifas")
    }
    MutadaInputs { }
}

private val ikhtilafiMasle = FC<Props> {
    val language: String by useState(languageSelector.value)

    div {
        details {
            summary { className = CssC.IKHTILAF }
            ReactHTML.b {
                if (language == Vls.Langs.ENGLISH) {
                    +StringsOfLanguages.ENGLISH.ikhtilafimasail
                } else {
                    +StringsOfLanguages.URDU.ikhtilafimasail
                }
            }
            IkhtilafiMasla { Ids.Ikhtilafat.IKHTILAF1
                StringsOfLanguages.ENGLISH.considerTuhrInGhiarMustabeenIsqaatIkhtilaf
                StringsOfLanguages.URDU.considerTuhrInGhiarMustabeenIsqaatIkhtilaf
            }
            if (devmode) {
                IkhtilafiMasla {
                    Ids.Ikhtilafat.IKHTILAF2
                    StringsOfLanguages.ENGLISH.aadatIncreasingAtEndOfDaurIkhtilaf
                    StringsOfLanguages.URDU.aadatIncreasingAtEndOfDaurIkhtilaf
//                    extraClasses = CssC.DEV
                }
                IkhtilafiMasla {
                    Ids.Ikhtilafat.IKHTILAF3
                    StringsOfLanguages.ENGLISH.ayyameqabliyyaikhtilaf
                    StringsOfLanguages.URDU.ayyameqabliyyaikhtilaf
//                    extraClasses = CssC.DEV
                }
                IkhtilafiMasla {
                    Ids.Ikhtilafat.IKHTILAF4
                    StringsOfLanguages.ENGLISH.mubtadiaikhitilaf
                    StringsOfLanguages.URDU.mubtadiaikhitilaf
//                    extraClasses = CssC.DEV
                }
            }
        }
    }
}
private val IkhtilafiMasla = FC<LabelProps> { props ->
    div {
//        className = "${CssC.ROW} ${props.extraClasses}"
            div {
                Label {
                    inputId = props.inputId
                    englishText = props.englishText
                    urduText = props.urduText
                }
            Switch { inputId }
            }
//        block()
    }
}

external interface LabelProps : Props {
    var inputId: String
    var englishText: String
    var urduText: String
//    var extraClasses: String?
//    val block: (LABEL) -> Unit?
}
private val Label = FC<LabelProps> { props ->
    console.log(props.inputId)
    val language: String by useState(languageSelector.value)
    println("lang $language")
    label {
        htmlFor = props.inputId
//        className = props.extraClasses
        +if (language == Vls.Langs.ENGLISH) props.englishText
        else props.urduText
//        props.block
        console.log(this)
    }
}

external interface SwitchProps : Props { val inputId: String }
private val Switch = FC<SwitchProps> { props ->
    label {
        className = CssC.SWITCH
        input {
            type = InputType.checkbox
            id = props.inputId
        }
        ReactHTML.span { className = "${CssC.SLIDER} ${CssC.ROUND}" }
    }
}

external interface DropdownProps : Props {
    var isSelected: Boolean
    var optionVal: String
    var englishText: String
    var urduText: String
    var extraClasses: String
//    var block: OPTION.() -> Unit = {}
}
private val DropdownOption = FC<DropdownProps> { props ->
    val language: String by useState(languageSelector.value)
    option {
        className = props.extraClasses
        selected = props.isSelected
        value = props.optionVal
        id = props.optionVal
//        block()
        if (language == Vls.Langs.ENGLISH) {
            +props.englishText
        } else {
            +props.urduText
        }
    }
}

private var MaslaConfigDropdown = FC<ChangeHandlerProp> { props ->
    var masla = InputState.masla

    val isMutada = masla == Vls.Maslas.MUTADA
    val isNifas = masla == Vls.Maslas.NIFAS
    val isMubtadia = masla == Vls.Maslas.MUBTADIA

    div {
        className = CssC.ROW
        Label {
            inputId = Ids.Inputs.MASLA_TYPE_SELECT
            englishText = StringsOfLanguages.ENGLISH.typeOfMasla
            urduText = StringsOfLanguages.URDU.typeOfMasla
        }
        select {
            id = Ids.Inputs.MASLA_TYPE_SELECT
            onChange = { event -> props.changeHandler(event.currentTarget.value) }
            DropdownOption {
                isSelected = isMutada
                optionVal = Vls.Maslas.MUTADA
                englishText = StringsOfLanguages.ENGLISH.mutada
                urduText = StringsOfLanguages.URDU.mutada }
            DropdownOption {
                isSelected = isNifas
                optionVal = Vls.Maslas.NIFAS
                englishText = StringsOfLanguages.ENGLISH.nifas
                urduText = StringsOfLanguages.URDU.nifas }
            if (devmode) {
                DropdownOption {
                    isSelected = isMubtadia
                    optionVal = Vls.Maslas.MUBTADIA
                    englishText = StringsOfLanguages.ENGLISH.mubtadia
                    urduText = StringsOfLanguages.URDU.mubtadia
//                    "dev"
                }
            }
        }
        // Zaalla?
        if (devmode) {
            div {
                className = CssC.DEV
                Label {
                    inputId = Ids.Inputs.ZAALLA_CHECKBOX
                    englishText = "Zaalla"
                    urduText = "Zaalla"
                }
                input {
                    type = InputType.checkbox
                    id = Ids.Inputs.ZAALLA_CHECKBOX
                    name = Ids.Inputs.ZAALLA_CHECKBOX
                    checked = false
//                onChangeFunction = { event -> disableTree(findInputContainer(event)) }
                }
            }
        }
    }
}
private var TypeConfigDropdown = FC<ChangeHandlerProp>  {
    var type: String by useState(InputState.masla)
    val isDateTime = !IS_DEFAULT_INPUT_MODE_DATE_ONLY
    val isDateOnly = IS_DEFAULT_INPUT_MODE_DATE_ONLY
    val isDuration = !IS_DEFAULT_INPUT_MODE_DATE_ONLY
    div {
        className = CssC.ROW
        Label {
            inputId = Ids.Inputs.INPUT_TYPE_SELECT
            englishText = StringsOfLanguages.ENGLISH.typeOfInput
            urduText = StringsOfLanguages.URDU.typeOfInput
        }
        select {
            id = Ids.Inputs.INPUT_TYPE_SELECT
            onChange = { event -> type = event.currentTarget.value }
//            onChangeFunction = { event -> onClickTypeConfigurationSelectDropdown(event) }
            DropdownOption {
                isSelected = isDateOnly
                optionVal = Vls.Types.DATE_ONLY
                englishText = StringsOfLanguages.ENGLISH.dateOnly
                urduText = StringsOfLanguages.URDU.dateOnly }
            DropdownOption {
                isSelected = isDateTime
                optionVal = Vls.Types.DATE_TIME
                englishText = StringsOfLanguages.ENGLISH.dateAndTime
                urduText = StringsOfLanguages.URDU.dateAndTime }
            DropdownOption {
                isSelected = isDuration
                optionVal = Vls.Types.DURATION
                englishText = StringsOfLanguages.ENGLISH.duration
                urduText = StringsOfLanguages.URDU.duration }
        }
    }
}

external interface TimeInputProps : Props {
    val inputId: String
    var onChange: (ChangeEvent<HTMLElement>) -> Unit
    var block: (INPUT) -> Unit?
}
private var TimeInput = FC<TimeInputProps> { props ->
    val dateOnly: Boolean by useState(InputState.type == Vls.Types.DATE_ONLY)
    input {
        id = props.inputId
        name = props.inputId
        required = true

        if (dateOnly) {
            type = InputType.date
        }
        else {
            type = InputType.datetimeLocal
            placeholder = "YYYY-MM-DDThh:mm"
            pattern = "[0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}:[0-9]{2}"
        }

//        onClick = { event -> setMaxToCurrentTimeForTimeInputs(findInputContainer(event.currentTarget)) }
        onChange = { event -> props.onChange(event) }
        props.block
    }
}

external interface NumberInputProps : Props {
    var inputId: String
    var inputVal: String?
    var inputRange: ClosedRange<Int>
    var onChange: (ChangeEvent<HTMLInputElement>) -> Unit
    var block: (HTMLInputElement) -> Unit
}
private var NumberInput = FC<NumberInputProps> { props ->
    input {
        id = props.inputId
        name = props.inputId
        value = props.inputVal.orEmpty()
        //TODO: Uncomment this later after fixing validator
        onInput = { event -> event.currentTarget.validateAadat(props.inputRange) }
        onChange = onChange
        props.block
    }
}

private var NifasInputs = FC<Props> {
    // Pregnancy Start Time
    if (InputState.type != Vls.Types.DURATION) {
        div {
            className = "${CssC.ROW} ${CssC.NIFAS} ${CssC.DATETIME_AADAT}"
            Label {
                inputId = Ids.Inputs.PREG_START_TIME_INPUT
                englishText = StringsOfLanguages.ENGLISH.pregnancyStartTime
                urduText = StringsOfLanguages.URDU.pregnancyStartTime
            }
//        pregnancyTimeInput(inputContainerToCopyFrom, Ids.Inputs.PREG_START_TIME_INPUT)
            TimeInput {
                Ids.Inputs.PREG_START_TIME_INPUT
                onChange = { event: ChangeEvent<HTMLElement> ->
                    findInputContainer(event.currentTarget).pregEndTime.min =
                        (event.currentTarget as HTMLInputElement).value
                }
            }
        }
        // Pregnancy End Time
        div {
            className = "${CssC.ROW} ${CssC.DATETIME_AADAT} ${CssC.NIFAS}"
            Label {
                inputId = Ids.Inputs.PREG_END_TIME_INPUT
                englishText = StringsOfLanguages.ENGLISH.birthMiscarrriageTime
                urduText = StringsOfLanguages.URDU.birthMiscarrriageTime
            }
            TimeInput {
                Ids.Inputs.PREG_END_TIME_INPUT
                onChange = { event: ChangeEvent<HTMLElement> ->
                    findInputContainer(event.currentTarget).pregStartTime.max =
                        (event.currentTarget as HTMLInputElement).value
                }
            }
        }
    }
    // Pregnancy Mustabeen ul Khilqa?
    div {
        className = "${CssC.ROW} ${CssC.NIFAS}"
        div {
            Label {
                inputId = Ids.Inputs.MUSTABEEN_CHECKBOX
                englishText = StringsOfLanguages.ENGLISH.mustabeenUlKhilqa
                urduText = StringsOfLanguages.URDU.mustabeenUlKhilqa
            }
            input {
                type = InputType.checkbox
                id = Ids.Inputs.MUSTABEEN_CHECKBOX
                name = Ids.Inputs.MUSTABEEN_CHECKBOX
                checked = true
                onChange = { event -> switchWiladatIsqat(findInputContainer(event.currentTarget)) }
            }
        }
    }

    // Pregnancy Aadat
    div {
        className = "${CssC.ROW} ${CssC.NIFAS} ${CssC.INVIS}"
        Label {
            inputId = Ids.Inputs.AADAT_NIFAS_INPUT
            englishText = StringsOfLanguages.ENGLISH.nifasAadat
            urduText = StringsOfLanguages.URDU.nifasAadat
        }
        NumberInput {
            inputId = Ids.Inputs.AADAT_NIFAS_INPUT
            inputVal = "" // inputContainerToCopyFrom?.aadatNifas?.value.orEmpty();
            inputRange = (1..40)
        }
    }
}

private val MutadaInputs = FC<Props> {
    // Aadat of Haiz
    div {
        className = CssC.ROW
        Label {
            inputId = Ids.Inputs.AADAT_HAIZ_INPUT
            englishText = StringsOfLanguages.ENGLISH.haizAadat
            urduText = StringsOfLanguages.URDU.haizAadat
        }
        NumberInput {
            inputId = Ids.Inputs.AADAT_HAIZ_INPUT
            inputVal = "" // inputContainerToCopyFrom?.aadatHaz?.value.orEmpty()
            inputRange = (3..10)
            onChange = { event -> onlyTwo(event) }
        }
    }
    // Aadat of Tuhr
    if (InputState.masla != Vls.Maslas.MUBTADIA) {
        div {
            className = "${CssC.ROW} ${CssC.MUTADA}"
            Label {
                inputId = Ids.Inputs.AADAT_TUHR_INPUT
                englishText = StringsOfLanguages.ENGLISH.tuhrAadat
                urduText = StringsOfLanguages.URDU.tuhrAadat
            }
            NumberInput {
                inputId = Ids.Inputs.AADAT_TUHR_INPUT
                inputVal = "" // inputContainerToCopyFrom?.aadatTuhr?.value.orEmpty()
                inputRange = (15..6 * 30)
                onChange = { event -> onlyTwo(event) }
            }
        }
    }
    // Zaalla Cycle Length
    div {
        className = "${CssC.ROW} ${CssC.ZAALLA} ${CssC.INVIS}"
        Label {
            inputId = Ids.Inputs.ZAALLA_CYCLE_LENGTH
            englishText = "Cycle Length"
            urduText = "Cycle Length"
        }
        NumberInput {
            inputId = Ids.Inputs.ZAALLA_CYCLE_LENGTH
            inputVal = "" // inputContainerToCopyFrom?.cycleLength?.value.orEmpty()
            inputRange = (8..6 * 30+10)
            onChange = { event -> onlyTwo(event) }
        }
    }
    // Mawjooda Tuhr
    if (InputState.type != Vls.Types.DURATION) {
        div {
            className = "${CssC.ROW} ${CssC.DATETIME_AADAT}"
            Label {
                inputId = Ids.Inputs.MAWJOODA_TUHR_INPUT
                englishText = StringsOfLanguages.ENGLISH.mawjoodahTuhr
                urduText = StringsOfLanguages.URDU.mawjoodahTuhr
            }
            NumberInput {
                inputId = Ids.Inputs.MAWJOODA_TUHR_INPUT
                inputVal = "" // inputContainerToCopyFrom?.mawjoodaTuhr?.value.orEmpty()
                inputRange = (15..10000)
            }
            // Fasid?
            div {
                Label {
                    inputId = Ids.Inputs.MAWJOODA_FASID_CHECKBOX
                    englishText = StringsOfLanguages.ENGLISH.faasid
                    urduText = StringsOfLanguages.URDU.faasid
                }
                input {
                    type = InputType.checkbox
                    id = Ids.Inputs.MAWJOODA_FASID_CHECKBOX
                    name = Ids.Inputs.MAWJOODA_FASID_CHECKBOX
                    checked = false
                }
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
//        onClickFunction = { event -> setMaxToCurrentTimeForTimeInputs(findInputContainer(event)) }
    }
    button(classes = "${CssC.URDU} ${CssC.CALC_BTN}") {
        id = Ids.Results.CALCULATE_BUTTON
        +StringsOfLanguages.URDU.calculate
//        onClickFunction = { event -> setMaxToCurrentTimeForTimeInputs(findInputContainer(event)) }
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
