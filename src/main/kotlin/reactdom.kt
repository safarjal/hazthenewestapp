@file:Suppress("SpellCheckingInspection")

//package react

import csstype.Visibility
import csstype.px
import kotlinx.html.*
import org.w3c.dom.*
import react.*
import kotlinx.browser.document
import react.css.css
import react.dom.render
import react.dom.events.ChangeEvent
import react.dom.html.InputType
import react.dom.html.ReactHTML
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

    println(lang)

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
    ReactHTML.input {
        className = CssC.HIDDEN      // not working because of css
        id = "update_lang"
        css {
            height = 0.px
            width = 0.px
            visibility = Visibility.hidden
        }
        checked = lang == Vls.Langs.ENGLISH
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
                urduText = StringsOfLanguages.URDU.mutada
                language = props.stateLang
            }
            DropdownOption {
                optionVal = Vls.Maslas.NIFAS
                englishText = StringsOfLanguages.ENGLISH.nifas
                urduText = StringsOfLanguages.URDU.nifas
                language = props.stateLang
            }
            if (devmode) {
                DropdownOption {
                    optionVal = Vls.Maslas.MUBTADIA
                    englishText = StringsOfLanguages.ENGLISH.mubtadia
                    urduText = StringsOfLanguages.URDU.mubtadia
                    language = props.stateLang
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
                    urduText = "ضالة"
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
                urduText = StringsOfLanguages.URDU.dateOnly
                language = props.stateLang
            }
            DropdownOption {
                optionVal = Vls.Types.DATE_TIME
                englishText = StringsOfLanguages.ENGLISH.dateAndTime
                urduText = StringsOfLanguages.URDU.dateAndTime
                language = props.stateLang
            }
            DropdownOption {
                optionVal = Vls.Types.DURATION
                englishText = StringsOfLanguages.ENGLISH.duration
                urduText = StringsOfLanguages.URDU.duration
                language = props.stateLang
            }
        }
    }
}

private var NifasInputs = FC<StateProp> { props ->
    var pregStart: String by useState("")
    var pregEnd: String by useState("")
    var isMustabeen: Boolean by useState(true)
    var pregAadat: String by useState("")

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
                inputVal = pregStart
                inputType = props.stateType
                onChange = { event: ChangeEvent<HTMLElement> ->
                    var thisElem = event.currentTarget as HTMLInputElement
                    findInputContainer(thisElem).pregEndTime.min = thisElem.value
                    pregStart = thisElem.value
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
                inputVal = pregEnd
                inputType = props.stateType
                onChange = { event: ChangeEvent<HTMLElement> ->
                    var thisElem = event.currentTarget as HTMLInputElement
                    findInputContainer(thisElem).pregStartTime.min = thisElem.value
                    pregEnd = thisElem.value
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
                checked = isMustabeen
                onChange = { event ->
                    isMustabeen = event.currentTarget.checked
                    switchWiladatIsqat(findInputContainer(event.currentTarget))
                }
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
            inputVal = pregAadat
            onChange = { event ->
                pregAadat = fixInputNumber(event.currentTarget.value)
                event.currentTarget.validateAadat(1..40)
            }
        }
    }
}
private val MutadaInputs = FC<StateProp> { props ->
    var haizAadat: String by useState("")
    var tuhrAadat: String by useState("")
    var zaallaCycle: String by useState("")
    var mawjoodaTuhr: String by useState("")
    var isFaasid: Boolean by useState(false)

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
            inputVal = haizAadat
            disabled = tuhrAadat.isNotEmpty() && zaallaCycle.isNotEmpty()
            onChange = { event ->
                haizAadat = fixInputNumber(event.currentTarget.value)
                event.currentTarget.validateAadat(3..10)
//                updateTwo()
            }
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
                inputVal = tuhrAadat
                disabled = haizAadat.isNotEmpty() && zaallaCycle.isNotEmpty()
                onChange = { event ->
//                    updateTwo()
                    tuhrAadat = fixInputNumber(event.currentTarget.value)
                    event.currentTarget.validateAadat(15..6 * 30)
                }
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
                urduText = "سائیکل کی لمبائی"
                language = props.stateLang
            }
            NumberInput {
                inputId = Ids.Inputs.ZAALLA_CYCLE_LENGTH
                inputVal = zaallaCycle
                disabled = haizAadat.isNotEmpty() && tuhrAadat.isNotEmpty()
                onChange = { event ->
                    zaallaCycle = fixInputNumber(event.currentTarget.value)
                    event.currentTarget.validateAadat(8..6 * 30 + 10)
//                    updateTwo()
                }
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
                inputVal = mawjoodaTuhr
                onChange = { event ->
                    mawjoodaTuhr = fixInputNumber(event.currentTarget.value)
                    event.currentTarget.validateAadat(5..10000)
                }
            }
            // Faasid?
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
                    checked = isFaasid
                    onChange = { event -> isFaasid = event.currentTarget.checked }
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
    var inputVal: String
    var inputType: String
    var onChange: (ChangeEvent<HTMLElement>) -> Unit
    var block: (INPUT) -> Unit?
}
private var TimeInput = FC<TimeInputProps> { props ->
    val dateOnly: Boolean by useState(props.inputType == Vls.Types.DATE_ONLY)

    input {
        id = props.inputId
        name = props.inputId
        required = true
        value = props.inputVal

        if (dateOnly) {
            type = InputType.date
        }
        else {
            type = InputType.datetimeLocal
            placeholder = "YYYY-MM-DDThh:mm"
            pattern = "[0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}:[0-9]{2}"
        }

        onChange = { event -> props.onChange(event) }
        props.block
    }
}

external interface NumberInputProps : Props {
    var inputId: String
    var inputVal: String?
    var disabled: Boolean
//    var inputRange: ClosedRange<Int>
    var onChange: (ChangeEvent<HTMLInputElement>) -> Unit
    var block: (HTMLInputElement) -> Unit
}
private var NumberInput = FC<NumberInputProps> { props ->
    input {
        id = props.inputId
        name = props.inputId
        value = props.inputVal
        disabled = props.disabled
        onChange = { event -> props.onChange(event) }
        props.block
    }
}
private fun fixInputNumber(thisValue: String): String {
    return if (devmode && thisValue.contains("-")) {
        thisValue.replace("[^0-9-]".toRegex(), "")
    } else {
        thisValue.replace("[^0-9:]".toRegex(), "")
    }

}