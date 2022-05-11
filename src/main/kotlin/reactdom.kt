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
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.label
import react.dom.html.ReactHTML.input
import react.dom.html.ReactHTML.option
import react.dom.html.ReactHTML.select

fun addReact(inputsContainerToCopyFrom: HTMLElement? = null, clonedInputsContainer: HTMLElement? = null) {
    println(inputsContainerToCopyFrom?.maslaSelect?.value ?: Vls.Maslas.MUTADA)
    println(inputsContainerToCopyFrom?.typeSelect?.value ?: Vls.Types.DATE_ONLY)
    println(NifasValues(
        inputsContainerToCopyFrom?.pregStartTime?.value ?: "",
        inputsContainerToCopyFrom?.pregEndTime?.value ?: "",
        inputsContainerToCopyFrom?.isMustabeen ?: true,
        inputsContainerToCopyFrom?.aadatNifas?.value ?: "").pregStart)
    println(MutadaValues(
        inputsContainerToCopyFrom?.aadatHaz?.value ?: "",
        inputsContainerToCopyFrom?.aadatTuhr?.value ?: "",
        inputsContainerToCopyFrom?.cycleLength?.value ?: "",
        inputsContainerToCopyFrom?.mawjoodaTuhr?.value ?: "",
        inputsContainerToCopyFrom?.isMawjoodaFasid ?: false))

    val reactDiv = clonedInputsContainer?.reactDiv ?: document.body!!.reactDiv
    render(ReactInputs.create {
        maslaState = inputsContainerToCopyFrom?.maslaSelect?.value ?: Vls.Maslas.MUTADA
        typeState = inputsContainerToCopyFrom?.typeSelect?.value ?: Vls.Types.DATE_ONLY
        nifasState = NifasValues(
            inputsContainerToCopyFrom?.pregStartTime?.value ?: "",
            inputsContainerToCopyFrom?.pregEndTime?.value ?: "",
            inputsContainerToCopyFrom?.isMustabeen ?: true,
            inputsContainerToCopyFrom?.aadatNifas?.value ?: ""
        )
        mutadaState = MutadaValues(
            inputsContainerToCopyFrom?.aadatHaz?.value ?: "",
            inputsContainerToCopyFrom?.aadatTuhr?.value ?: "",
            inputsContainerToCopyFrom?.cycleLength?.value ?: "",
            inputsContainerToCopyFrom?.mawjoodaTuhr?.value ?: "",
            inputsContainerToCopyFrom?.isMawjoodaFasid ?: false
        )
    }, reactDiv)
}

external interface StateProp : Props {
    var maslaState: String
    var typeState: String
    var langState: String
    var dropdownChangeHandler: (String) -> Unit

    var nifasState: NifasValues
    var updateNifas: (NifasValues) -> Unit

    var mutadaState: MutadaValues
    var updateMutada: (MutadaValues) -> Unit
}
private val ReactInputs = FC<StateProp> { props ->
    var lang : String by useState(languageSelector.value)
    var masla : String by useState(props.maslaState)
    var type : String by useState(props.typeState)

    var nifasInputs: NifasValues by useState(props.nifasState)
    var mutadaInputs: MutadaValues by useState(props.mutadaState)

    MaslaConfigDropdown {
        dropdownChangeHandler = { newMasla: String -> masla = newMasla }
        langState = lang
    }
    TypeConfigDropdown {
        dropdownChangeHandler = { newType: String -> type = newType }
        langState = lang
    }
    if(masla == Vls.Maslas.NIFAS) {
        NifasInputs {
            typeState = type
            langState = lang
            nifasState = nifasInputs
            updateNifas = { newNifasInputs: NifasValues -> nifasInputs = newNifasInputs }
        }
    }
    MutadaInputs {
        maslaState = masla
        typeState = type
        langState = lang
        mutadaState = mutadaInputs
        updateMutada = { newMutadaInputs: MutadaValues -> mutadaInputs = newMutadaInputs }
    }
    // to update!
    input {
        className = CssC.HIDDEN      // not working because of css
        id = "update_lang"
        css {
            height = 0.px
            width = 0.px
            margin = 0.px
            padding = 0.px
            visibility = Visibility.hidden
        }
        checked = lang == Vls.Langs.ENGLISH
        onClick = { lang = languageSelector.value }
    }
}

private var MaslaConfigDropdown = FC<StateProp> { props ->
    var masla: String by useState(props.maslaState)
    var isZaalla: Boolean by useState(false)
    div {
        className = CssC.ROW
        Label {
            inputId = Ids.Inputs.MASLA_TYPE_SELECT
            englishText = StringsOfLanguages.ENGLISH.typeOfMasla
            urduText = StringsOfLanguages.URDU.typeOfMasla
            language = props.langState
        }
        select {
            value = masla
            id = Ids.Inputs.MASLA_TYPE_SELECT
            onChange = { event ->
                props.dropdownChangeHandler(event.currentTarget.value)
                masla = event.currentTarget.value
            }
            DropdownOption {
                optionVal = Vls.Maslas.MUTADA
                englishText = StringsOfLanguages.ENGLISH.mutada
                urduText = StringsOfLanguages.URDU.mutada
                language = props.langState
            }
            DropdownOption {
                optionVal = Vls.Maslas.NIFAS
                englishText = StringsOfLanguages.ENGLISH.nifas
                urduText = StringsOfLanguages.URDU.nifas
                language = props.langState
            }
            if (devmode) {
                DropdownOption {
                    optionVal = Vls.Maslas.MUBTADIA
                    englishText = StringsOfLanguages.ENGLISH.mubtadia
                    urduText = StringsOfLanguages.URDU.mubtadia
                    language = props.langState
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
                    language = props.langState
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
    var type: String by useState(props.typeState)

    div {
        className = CssC.ROW
        Label {
            inputId = Ids.Inputs.INPUT_TYPE_SELECT
            englishText = StringsOfLanguages.ENGLISH.typeOfInput
            urduText = StringsOfLanguages.URDU.typeOfInput
            language = props.langState
        }
        select {
            id = Ids.Inputs.INPUT_TYPE_SELECT
            value = type
            onChange = { event ->
                props.dropdownChangeHandler(event.currentTarget.value)
                type = event.currentTarget.value
            }
//            onChangeFunction = { event -> onClickTypeConfigurationSelectDropdown(event) }
            DropdownOption {
                optionVal = Vls.Types.DATE_ONLY
                englishText = StringsOfLanguages.ENGLISH.dateOnly
                urduText = StringsOfLanguages.URDU.dateOnly
                language = props.langState
            }
            DropdownOption {
                optionVal = Vls.Types.DATE_TIME
                englishText = StringsOfLanguages.ENGLISH.dateAndTime
                urduText = StringsOfLanguages.URDU.dateAndTime
                language = props.langState
            }
            DropdownOption {
                optionVal = Vls.Types.DURATION
                englishText = StringsOfLanguages.ENGLISH.duration
                urduText = StringsOfLanguages.URDU.duration
                language = props.langState
            }
        }
    }
}

private var NifasInputs = FC<StateProp> { props ->
    var pregStart: String by useState(props.nifasState.pregStart)
    var pregEnd: String by useState(props.nifasState.pregEnd)
    var isMustabeen: Boolean by useState(props.nifasState.isMustabeen)
    var pregAadat: String by useState(props.nifasState.pregAadat)

    // Pregnancy Start Time
    if (props.typeState != Vls.Types.DURATION) {
        div {
            className = "${CssC.ROW} ${CssC.NIFAS} ${CssC.DATETIME_AADAT}"
            Label {
                inputId = Ids.Inputs.PREG_START_TIME_INPUT
                englishText = StringsOfLanguages.ENGLISH.pregnancyStartTime
                urduText = StringsOfLanguages.URDU.pregnancyStartTime
                language = props.langState
            }
            TimeInput {
                inputId = Ids.Inputs.PREG_START_TIME_INPUT
                inputVal = pregStart
                inputType = props.typeState
                onChange = { event: ChangeEvent<HTMLElement> ->
                    val thisElem = event.currentTarget as HTMLInputElement
                    findInputContainer(thisElem).pregEndTime!!.min = thisElem.value
                    props.nifasState.pregStart = thisElem.value
                    pregStart = props.nifasState.pregStart
                    props.updateNifas(props.nifasState)
                }
            }
        }
    }

    // Pregnancy End Time
    div {
        className = "${CssC.ROW} ${CssC.DATETIME_AADAT} ${CssC.NIFAS}"
        if (props.typeState != Vls.Types.DURATION) {
            Label {
                inputId = Ids.Inputs.PREG_END_TIME_INPUT
                englishText = StringsOfLanguages.ENGLISH.birthMiscarrriageTime
                urduText = StringsOfLanguages.URDU.birthMiscarrriageTime
                language = props.langState
            }
            TimeInput {
                inputId = Ids.Inputs.PREG_END_TIME_INPUT
                inputVal = pregEnd
                inputType = props.typeState
                onChange = { event: ChangeEvent<HTMLElement> ->
                    val thisElem = event.currentTarget as HTMLInputElement
                    findInputContainer(thisElem).pregStartTime!!.max = thisElem.value
                    props.nifasState.pregEnd = thisElem.value
                    pregEnd = props.nifasState.pregEnd
                    props.updateNifas(props.nifasState)
                }
            }
        }
        // Pregnancy Mustabeen ul Khilqa?
        div {
            Label {
                inputId = Ids.Inputs.MUSTABEEN_CHECKBOX
                englishText = StringsOfLanguages.ENGLISH.mustabeenUlKhilqa
                urduText = StringsOfLanguages.URDU.mustabeenUlKhilqa
                language = props.langState
            }
            input {
                type = InputType.checkbox
                id = Ids.Inputs.MUSTABEEN_CHECKBOX
                name = Ids.Inputs.MUSTABEEN_CHECKBOX
                checked = isMustabeen
                onChange = { event ->
                    props.nifasState.isMustabeen = event.currentTarget.checked
                    isMustabeen = props.nifasState.isMustabeen
                    switchWiladatIsqat(findInputContainer(event.currentTarget))
                    props.updateNifas(props.nifasState)
                }
            }
        }
    }

    // Pregnancy Aadat
    div {
        className = "${CssC.ROW} ${CssC.NIFAS}"
        Label {
            inputId = Ids.Inputs.AADAT_NIFAS_INPUT
            englishText = StringsOfLanguages.ENGLISH.nifasAadat
            urduText = StringsOfLanguages.URDU.nifasAadat
            language = props.langState
        }
        NumberInput {
            inputId = Ids.Inputs.AADAT_NIFAS_INPUT
            inputVal = pregAadat
            onChange = { event ->
                props.nifasState.pregAadat = fixInputNumber(event.currentTarget.value)
                pregAadat = props.nifasState.pregAadat
                event.currentTarget.validateAadat(1..40)
                props.updateNifas(props.nifasState)
            }
        }
    }
}
private val MutadaInputs = FC<StateProp> { props ->
    var haizAadat: String by useState(props.mutadaState.haizAadat)
    var tuhrAadat: String by useState(props.mutadaState.tuhrAadat)
    var zaallaCycle: String by useState(props.mutadaState.zaallaCycle)
    var mawjoodaTuhr: String by useState(props.mutadaState.mawjoodaTuhr)
    var isFaasid: Boolean by useState(props.mutadaState.isFaasid)

    // Aadat of Haiz
    div {
        className = CssC.ROW
        Label {
            inputId = Ids.Inputs.AADAT_HAIZ_INPUT
            englishText = StringsOfLanguages.ENGLISH.haizAadat
            urduText = StringsOfLanguages.URDU.haizAadat
            language = props.langState
        }
        NumberInput {
            inputId = Ids.Inputs.AADAT_HAIZ_INPUT
            inputVal = haizAadat
            disabled = tuhrAadat.isNotEmpty() && zaallaCycle.isNotEmpty()
            onChange = { event ->
                props.mutadaState.haizAadat = fixInputNumber(event.currentTarget.value)
                haizAadat = props.mutadaState.haizAadat
                event.currentTarget.validateAadat(3..10)
                props.updateMutada(props.mutadaState)
            }
        }
    }
    // Aadat of Tuhr
    if (props.maslaState != Vls.Maslas.MUBTADIA) {
        div {
            className = "${CssC.ROW} ${CssC.MUTADA}"
            Label {
                inputId = Ids.Inputs.AADAT_TUHR_INPUT
                englishText = StringsOfLanguages.ENGLISH.tuhrAadat
                urduText = StringsOfLanguages.URDU.tuhrAadat
                language = props.langState
            }
            NumberInput {
                inputId = Ids.Inputs.AADAT_TUHR_INPUT
                inputVal = tuhrAadat
                disabled = haizAadat.isNotEmpty() && zaallaCycle.isNotEmpty()
                onChange = { event ->
                    props.mutadaState.tuhrAadat = fixInputNumber(event.currentTarget.value)
                    tuhrAadat = props.mutadaState.tuhrAadat
                    event.currentTarget.validateAadat(15..6 * 30)
                    props.updateMutada(props.mutadaState)
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
                language = props.langState
            }
            NumberInput {
                inputId = Ids.Inputs.ZAALLA_CYCLE_LENGTH
                inputVal = zaallaCycle
                disabled = haizAadat.isNotEmpty() && tuhrAadat.isNotEmpty()
                onChange = { event ->
                    props.mutadaState.zaallaCycle = fixInputNumber(event.currentTarget.value)
                    zaallaCycle = props.mutadaState.zaallaCycle
                    event.currentTarget.validateAadat(8..6 * 30 + 10)
                    props.updateMutada(props.mutadaState)
                }
            }
        }
    }
    // Mawjooda Tuhr
    if (props.typeState != Vls.Types.DURATION) {
        div {
            className = "${CssC.ROW} ${CssC.DATETIME_AADAT}"
            Label {
                inputId = Ids.Inputs.MAWJOODA_TUHR_INPUT
                englishText = StringsOfLanguages.ENGLISH.mawjoodahTuhr
                urduText = StringsOfLanguages.URDU.mawjoodahTuhr
                language = props.langState
            }
            NumberInput {
                inputId = Ids.Inputs.MAWJOODA_TUHR_INPUT
                inputVal = mawjoodaTuhr
                onChange = { event ->
                    props.mutadaState.mawjoodaTuhr = fixInputNumber(event.currentTarget.value)
                    mawjoodaTuhr = props.mutadaState.mawjoodaTuhr
                    event.currentTarget.validateAadat(5..10000)
                    props.updateMutada(props.mutadaState)
                }
            }
            // Faasid?
            div {
                Label {
                    inputId = Ids.Inputs.MAWJOODA_FASID_CHECKBOX
                    englishText = StringsOfLanguages.ENGLISH.faasid
                    urduText = StringsOfLanguages.URDU.faasid
                    language = props.langState
                }
                input {
                    type = InputType.checkbox
                    id = Ids.Inputs.MAWJOODA_FASID_CHECKBOX
                    name = Ids.Inputs.MAWJOODA_FASID_CHECKBOX
                    checked = isFaasid
                    onChange = { event ->
                        props.mutadaState.isFaasid = event.currentTarget.checked
                        isFaasid = props.mutadaState.isFaasid
                        props.updateMutada(props.mutadaState)
                    }
                }
            }
        }
    }
}

// MiniComponents
external interface TimeInputProps : Props {
    var inputId: String
    var inputVal: String
    var inputType: String
    var onChange: (ChangeEvent<HTMLElement>) -> Unit
    var block: (INPUT) -> Unit?
}
private var TimeInput = FC<TimeInputProps> { props ->
    val dateOnly = props.inputType == Vls.Types.DATE_ONLY

    input {
        id = props.inputId
        name = props.inputId
        required = true
        value = convertInputValue(props.inputVal, dateOnly)

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
