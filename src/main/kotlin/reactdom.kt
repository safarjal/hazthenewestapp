@file:Suppress("SpellCheckingInspection")

//package react

import csstype.px
import kotlinx.html.*
import org.w3c.dom.*
import react.*
import kotlinx.browser.document
import react.css.css
import react.dom.render
import react.dom.events.ChangeEvent
import react.dom.html.InputType
import react.dom.html.ReactHTML.button
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.label
import react.dom.html.ReactHTML.input
import react.dom.html.ReactHTML.option
import react.dom.html.ReactHTML.select

//import kotlinx.serialization.Serializable

fun addInputs(inputId: String) {
    val inputDiv = document.getChildById(inputId)
    render(Inputs.create(), inputDiv!!)
}

external interface StateProp : Props {
    var changeHandler: (String) -> Unit
    var stateMasla: String
    var stateType: String
    var stateLang: String
}
private val Inputs = FC<Props> {
    var masla : String by useState(InputState.masla)
    var type : String by useState(InputState.type)
    var lang : String by useState(InputState.lang)
    console.log(masla, type, lang)
    console.log(InputState.masla, InputState.type, InputState.lang)

    MaslaConfigDropdown {
        changeHandler = { newMasla: String -> masla = newMasla }
        stateLang = lang
    }
    TypeConfigDropdown {
        changeHandler = { newType: String -> type = newType }
        stateLang = lang
    }
    if(masla == Vls.Maslas.NIFAS) {
        NifasInputs {
            stateType = type
            stateLang = lang
        }
    }
    MutadaInputs {
        stateMasla = masla
        stateType = type
        stateLang = lang
    }
    button {
        className = CssC.HIDDEN
        id = "update_lang"
        css {
            height = 0.px
            width = 0.px
        }
        onClick = { lang = languageSelector.value }
    }
}

private var MaslaConfigDropdown = FC<StateProp> { props ->
    var masla: String by useState(props.stateMasla)
    var isZaalla: Boolean by useState(false)
    div {
        className = CssC.ROW
        Label {
            inputId = Ids.Inputs.MASLA_TYPE_SELECT
            englishText = StringsOfLanguages.ENGLISH.typeOfMasla
            urduText = StringsOfLanguages.URDU.typeOfMasla
            language = props.stateLang
        }
        select {
            value = masla
            id = Ids.Inputs.MASLA_TYPE_SELECT
            onChange = { event ->
                props.changeHandler(event.currentTarget.value)
                masla = event.currentTarget.value
            }
            DropdownOption {
                optionVal = Vls.Maslas.MUTADA
                englishText = StringsOfLanguages.ENGLISH.mutada
                urduText = StringsOfLanguages.URDU.mutada }
            DropdownOption {
                optionVal = Vls.Maslas.NIFAS
                englishText = StringsOfLanguages.ENGLISH.nifas
                urduText = StringsOfLanguages.URDU.nifas }
            if (devmode) {
                DropdownOption {
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
                    language = props.stateLang
                }
                input {
                    type = InputType.checkbox
                    id = Ids.Inputs.ZAALLA_CHECKBOX
                    name = Ids.Inputs.ZAALLA_CHECKBOX
                    checked = isZaalla
                    onChange = { event -> isZaalla = event.currentTarget.checked }
                }
            }
        }
    }
}
private var TypeConfigDropdown = FC<StateProp>  { props ->
    var type: String by useState(props.stateType)

    div {
        className = CssC.ROW
        Label {
            inputId = Ids.Inputs.INPUT_TYPE_SELECT
            englishText = StringsOfLanguages.ENGLISH.typeOfInput
            urduText = StringsOfLanguages.URDU.typeOfInput
            language = props.stateLang
        }
        select {
            id = Ids.Inputs.INPUT_TYPE_SELECT
            value = type
            onChange = { event ->
                props.changeHandler(event.currentTarget.value)
                type = event.currentTarget.value
            }
//            onChangeFunction = { event -> onClickTypeConfigurationSelectDropdown(event) }
            DropdownOption {
                optionVal = Vls.Types.DATE_ONLY
                englishText = StringsOfLanguages.ENGLISH.dateOnly
                urduText = StringsOfLanguages.URDU.dateOnly }
            DropdownOption {
                optionVal = Vls.Types.DATE_TIME
                englishText = StringsOfLanguages.ENGLISH.dateAndTime
                urduText = StringsOfLanguages.URDU.dateAndTime }
            DropdownOption {
                optionVal = Vls.Types.DURATION
                englishText = StringsOfLanguages.ENGLISH.duration
                urduText = StringsOfLanguages.URDU.duration }
        }
    }
}

private var NifasInputs = FC<StateProp> { props ->
    // Pregnancy Start Time
    if (props.stateType != Vls.Types.DURATION) {
        div {
            className = "${CssC.ROW} ${CssC.NIFAS} ${CssC.DATETIME_AADAT}"
            Label {
                inputId = Ids.Inputs.PREG_START_TIME_INPUT
                englishText = StringsOfLanguages.ENGLISH.pregnancyStartTime
                urduText = StringsOfLanguages.URDU.pregnancyStartTime
                language = props.stateLang
            }
            TimeInput {
                inputId = Ids.Inputs.PREG_START_TIME_INPUT
                onChange = { event: ChangeEvent<HTMLElement> ->
                    findInputContainer(event.currentTarget).pregEndTime.min =
                        (event.currentTarget as HTMLInputElement).value
                }
            }
        }
    }

    // Pregnancy End Time
    div {
        className = "${CssC.ROW} ${CssC.DATETIME_AADAT} ${CssC.NIFAS}"
        if (props.stateType != Vls.Types.DURATION) {
            Label {
                inputId = Ids.Inputs.PREG_END_TIME_INPUT
                englishText = StringsOfLanguages.ENGLISH.birthMiscarrriageTime
                urduText = StringsOfLanguages.URDU.birthMiscarrriageTime
                language = props.stateLang
            }
            TimeInput {
                inputId = Ids.Inputs.PREG_END_TIME_INPUT
                onChange = { event: ChangeEvent<HTMLElement> ->
                    findInputContainer(event.currentTarget).pregStartTime.max =
                        (event.currentTarget as HTMLInputElement).value
                }
            }
        }
        // Pregnancy Mustabeen ul Khilqa?
        div {
            Label {
                inputId = Ids.Inputs.MUSTABEEN_CHECKBOX
                englishText = StringsOfLanguages.ENGLISH.mustabeenUlKhilqa
                urduText = StringsOfLanguages.URDU.mustabeenUlKhilqa
                language = props.stateLang
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
            language = props.stateLang
        }
        NumberInput {
            inputId = Ids.Inputs.AADAT_NIFAS_INPUT
            inputVal = "" // inputContainerToCopyFrom?.aadatNifas?.value.orEmpty();
            inputRange = (1..40)
        }
    }
}

private val MutadaInputs = FC<StateProp> { props ->
    // Aadat of Haiz
    div {
        className = CssC.ROW
        Label {
            inputId = Ids.Inputs.AADAT_HAIZ_INPUT
            englishText = StringsOfLanguages.ENGLISH.haizAadat
            urduText = StringsOfLanguages.URDU.haizAadat
            language = props.stateLang
        }
        NumberInput {
            inputId = Ids.Inputs.AADAT_HAIZ_INPUT
            inputVal = "" // inputContainerToCopyFrom?.aadatHaz?.value.orEmpty()
            inputRange = (3..10)
            onChange = { event -> onlyTwo(event) }
        }
    }
    // Aadat of Tuhr
    if (props.stateMasla != Vls.Maslas.MUBTADIA) {
        div {
            className = "${CssC.ROW} ${CssC.MUTADA}"
            Label {
                inputId = Ids.Inputs.AADAT_TUHR_INPUT
                englishText = StringsOfLanguages.ENGLISH.tuhrAadat
                urduText = StringsOfLanguages.URDU.tuhrAadat
                language = props.stateLang
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
    if (devmode) {
        div {
            className = "${CssC.ROW} ${CssC.ZAALLA}"
            Label {
                inputId = Ids.Inputs.ZAALLA_CYCLE_LENGTH
                englishText = "Cycle Length"
                urduText = "Cycle Length"
                language = props.stateLang
            }
            NumberInput {
                inputId = Ids.Inputs.ZAALLA_CYCLE_LENGTH
                inputVal = "" // inputContainerToCopyFrom?.cycleLength?.value.orEmpty()
                inputRange = (8..6 * 30 + 10)
                onChange = { event -> onlyTwo(event) }
            }
        }
    }
    // Mawjooda Tuhr
    if (props.stateType != Vls.Types.DURATION) {
        div {
            className = "${CssC.ROW} ${CssC.DATETIME_AADAT}"
            Label {
                inputId = Ids.Inputs.MAWJOODA_TUHR_INPUT
                englishText = StringsOfLanguages.ENGLISH.mawjoodahTuhr
                urduText = StringsOfLanguages.URDU.mawjoodahTuhr
                language = props.stateLang
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
                    language = props.stateLang
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

external interface LabelProps : Props {
    var inputId: String
    var englishText: String
    var urduText: String
    var extraClasses: String?
    var language: String
//    val block: (LABEL) -> Unit?
}
private val Label = FC<LabelProps> { props ->
    label {
        htmlFor = props.inputId
        className = "${props.extraClasses} ${props.language}"
        +if (props.language == Vls.Langs.ENGLISH) props.englishText
        else props.urduText
//        props.block
    }
}

external interface DropdownProps : Props {
    var optionVal: String
    var englishText: String
    var urduText: String
    var extraClasses: String
    var language: String
}
private val DropdownOption = FC<DropdownProps> { props ->
    option {
        className = "${props.extraClasses} ${props.language}"
//        selected = props.isSelected
        value = props.optionVal
        id = props.optionVal
//        block()
        if (props.language == Vls.Langs.ENGLISH) {
            +props.englishText
        } else {
            +props.urduText
        }
    }
}

external interface TimeInputProps : Props {
    var inputId: String
    var onChange: (ChangeEvent<HTMLElement>) -> Unit
    var block: (INPUT) -> Unit?
}
private var TimeInput = FC<TimeInputProps> { props ->
    val dateOnly: Boolean by useState(InputState.type == Vls.Types.DATE_ONLY)
    var inputVal: String by useState("")
    input {
        id = props.inputId
        name = props.inputId
        required = true
        value = inputVal

        if (dateOnly) {
            type = InputType.date
        }
        else {
            type = InputType.datetimeLocal
            placeholder = "YYYY-MM-DDThh:mm"
            pattern = "[0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}:[0-9]{2}"
        }

        onChange = { event ->
            props.onChange(event)
            inputVal = event.currentTarget.value
        }
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
