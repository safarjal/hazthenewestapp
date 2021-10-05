import kotlinx.html.dom.append
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.html.*
import kotlinx.html.form
import kotlinx.html.js.*
import kotlinx.html.tr
import org.w3c.dom.*
import org.w3c.dom.events.Event
import kotlin.js.Date

private const val IS_DEFAULT_INPUT_MODE_DATE_ONLY = true

object Ids {
    const val HAIZ_INPUT_TABLE = "haiz_input_table"

    object Row {
        const val INPUT_START_TIME = "input_start_time"
        const val INPUT_END_TIME = "input_end_time"
        const val BUTTONS_CONTAINER = "button_add_before_container"
        const val BUTTON_REMOVE = "button_remove"
        const val BUTTON_ADD_BEFORE = "button_add_before"
    }

    const val CONTENT_ENG = "content_eng"
    const val CONTENT_URDU = "content_urdu"
    const val CONTENT_DATES = "content_dates"
    const val INPUT_CONTAINERS_CONTAINER = "input_containers_container"
    const val INPUT_CONTAINER_PREFIX = "input_container"
    const val INPUT_CONTAINER_PRIMARY = "${INPUT_CONTAINER_PREFIX}_primary"
    const val INPUT_CONTAINER_SECONDARY = "${INPUT_CONTAINER_PREFIX}_secondary"
    const val COMPARISON_CONTAINER = "comparison_container"
    const val ISTIMRAR_CHECKBOX = "istimrar_checkbox"
    const val PREGNANCY_CHECKBOX = "pregnancy_checkbox"
    const val MUSTABEEN_CHECKBOX = "mustabeen_checkbox"
    const val PREG_START_TIME_INPUT = "preg_start_time_input"
    const val PREG_END_TIME_INPUT = "preg_end_time_input"
    const val AADAT_HAIZ_INPUT = "aadat_haiz_input"
    const val AADAT_TUHR_INPUT = "aadat_tuhr_input"
    const val AADAT_NIFAS_INPUT = "aadat_nifas_input"
    const val DATE_ONLY_RADIO = "date_only_radio"
    const val DATE_TIME_RADIO = "date_time_radio"
    const val DATE_AND_OR_RADIO = "date_and_or_time"

    val pregnancyElementIds = listOf(
        PREG_START_TIME_INPUT,
        PREG_END_TIME_INPUT,
        MUSTABEEN_CHECKBOX,
        AADAT_NIFAS_INPUT
    )
}

private val inputsContainersContainer get() = document.getElementById(Ids.INPUT_CONTAINERS_CONTAINER) as HTMLElement
private val primaryInputsContainer get() = document.getElementById(Ids.INPUT_CONTAINER_PRIMARY) as HTMLElement
private val secondaryInputsContainer get() = document.getElementById(Ids.INPUT_CONTAINER_SECONDARY) as HTMLElement?

private val comparisonContainer get() = document.getElementById(Ids.COMPARISON_CONTAINER) as HTMLElement?

private val HTMLElement.isDateOnly get() = (getChildById(Ids.DATE_ONLY_RADIO) as HTMLInputElement).checked
private val HTMLElement.isIstimrar get() = (getChildById(Ids.ISTIMRAR_CHECKBOX) as HTMLInputElement).checked
private val HTMLElement.isPregnancy get() = (getChildById(Ids.PREGNANCY_CHECKBOX) as HTMLInputElement).checked
private val HTMLElement.mustabeen get() = (getChildById(Ids.MUSTABEEN_CHECKBOX) as HTMLInputElement).checked
private val HTMLElement.pregStartTime get() = getChildById(Ids.PREG_START_TIME_INPUT) as HTMLInputElement
private val HTMLElement.pregEndTime get() = getChildById(Ids.PREG_END_TIME_INPUT) as HTMLInputElement
private val HTMLElement.aadatHaz get() = getChildById(Ids.AADAT_HAIZ_INPUT) as HTMLInputElement
private val HTMLElement.aadatTuhr get() = getChildById(Ids.AADAT_TUHR_INPUT) as HTMLInputElement
private val HTMLElement.aadatNifas get() = getChildById(Ids.AADAT_NIFAS_INPUT) as HTMLInputElement
private val HTMLElement.contentEnglishElement get() = getChildById(Ids.CONTENT_ENG) as HTMLParagraphElement
private val HTMLElement.contentUrduElement get() = getChildById(Ids.CONTENT_URDU) as HTMLParagraphElement
private val HTMLElement.contentDatesElement get() = getChildById(Ids.CONTENT_DATES) as HTMLParagraphElement

private var HTMLElement.haizDatesList: List<Entry>?
    get() = (contentDatesElement.asDynamic().haizDatesList as List<Entry>?)?.takeIf { it != undefined }
    set(value) { contentDatesElement.asDynamic().haizDatesList = value }

private val HTMLElement.pregnancyElements get() = Ids.pregnancyElementIds.map { id ->
    getChildById(id) as HTMLInputElement
}

private val HTMLElement.haizInputDatesRows: List<HTMLTableRowElement>
    get() {
        val inputDatesTable = getChildById(Ids.HAIZ_INPUT_TABLE) as HTMLTableElement
        val inputDatesTableBody = inputDatesTable.tBodies[0] as HTMLTableSectionElement
        @Suppress("UNCHECKED_CAST")
        return inputDatesTableBody.rows.asList() as List<HTMLTableRowElement>
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
        document.body!!.addInputLayout()
        setupRows(primaryInputsContainer)
        document.addEventListener(Events.VISIBILITY_CHANGE, {
            if (!document.isHidden) {
                setMaxToCurrentTimeForTimeInputs(primaryInputsContainer)
            }
        })
    }
}

fun Node.addInputLayout() {
    append {
        headers()
        div {
            id = Ids.INPUT_CONTAINERS_CONTAINER
            inputFormDiv {
                id = Ids.INPUT_CONTAINER_PRIMARY
            }
        }
    }
}

private fun TagConsumer<HTMLElement>.headers() {
    h1 {
        +"Mashqi Sawal"
        // TODO: Add toggle button for Zallah to handle this
        onClickFunction = { toggleSecondaryInputsContainer() }
    }
    p {
        +"""
            Please enter the start date-time for first dam in the first box, and the end date-time for that dam in the
            second box. To add another period after that, press Add. If you need to remove a period in the middle, click
            the remove button next to it. To add a spot, enter a period where the start time and the end time are the
            same. If this masla ends with istimrar, make a period that ends on today's date, then check the istimrar
            check box. Once all periods have been added, click Calculate button, to get the solution.
        """.trimIndent()
    }
}

private fun toggleSecondaryInputsContainer() {
    if (secondaryInputsContainer?.remove() != null) {
        comparisonContainer!!.remove()
        return
    }

    primaryInputsContainer.after {
        inputFormDiv(inputContainerToCopyFrom = primaryInputsContainer) {
            id = Ids.INPUT_CONTAINER_SECONDARY
        }
    }
    setupFirstRow(secondaryInputsContainer!!)
}

private fun appendCompareButtonIfNeeded() {
    if (comparisonContainer != null ||
        primaryInputsContainer.haizDatesList == null ||
        secondaryInputsContainer?.haizDatesList == null
    ) return

    inputsContainersContainer.after {
        div {
            id = Ids.COMPARISON_CONTAINER
            button(type = ButtonType.button) {
                +"Calculate difference"
                style = "margin: 0 auto; display: block;"
                onClickFunction = { compareResults() }
            }
        }
    }
}

private fun TagConsumer<HTMLElement>.inputFormDiv(
    inputContainerToCopyFrom: HTMLElement? = null,
    block : DIV.() -> Unit = {}
) {
    div {
        style = "width:50%; float: left;"
        inputForm(inputContainerToCopyFrom)
        content()
        block()
    }
}

private fun TagConsumer<HTMLElement>.content() {
    content {
        id = Ids.CONTENT_ENG
    }
    content {
        id = Ids.CONTENT_URDU
        dir = Dir.rtl
//            style += "font-family: Helvetica"
    }
    content {
        id = Ids.CONTENT_DATES
    }
}

private fun TagConsumer<HTMLElement>.inputForm(inputContainerToCopyFrom: HTMLElement?) {
    form(action = "javascript:void(0);") {
        dateConfigurationRadioButtons(inputContainerToCopyFrom)
        br()
        aadatInputs(inputContainerToCopyFrom)
        br()
        pregnancyCheckBox(inputContainerToCopyFrom)
        br()
        mustabeenCheckBox(inputContainerToCopyFrom)
        br()
        pregnancyStartTimeInput(inputContainerToCopyFrom)
        br()
        pregnancyEndTimeInput(inputContainerToCopyFrom)
        br()
        haizDatesInputTable(inputContainerToCopyFrom)
        istimrarCheckBox(inputContainerToCopyFrom)
        br()
        calculateButton()
        onSubmitFunction = { event -> parseEntries(findInputContainer(event)) }
    }
}

private fun FlowContent.dateConfigurationRadioButtons(inputContainerToCopyFrom: HTMLElement?) {
    val isDateOnly = inputContainerToCopyFrom?.isDateOnly ?: IS_DEFAULT_INPUT_MODE_DATE_ONLY
    radioInput {
        id = Ids.DATE_TIME_RADIO
        name = Ids.DATE_AND_OR_RADIO
        checked = !isDateOnly
        onChangeFunction = { event -> onClickDateConfigurationRadioButton(findInputContainer(event)) }
    }
    label {
        htmlFor = Ids.DATE_TIME_RADIO
        +"Date and Time"
    }
    radioInput {
        id = Ids.DATE_ONLY_RADIO
        name = Ids.DATE_AND_OR_RADIO
        checked = isDateOnly
        onChangeFunction = { event -> onClickDateConfigurationRadioButton(findInputContainer(event)) }
    }
    label {
        htmlFor = Ids.DATE_ONLY_RADIO
        +"Date only"
    }
}

private fun FlowContent.aadatInputs(inputContainerToCopyFrom: HTMLElement?) {
    label {
        htmlFor = Ids.AADAT_HAIZ_INPUT
        +("Haiz Aadat: ")
    }
    input {
        id = Ids.AADAT_HAIZ_INPUT
        value = inputContainerToCopyFrom?.aadatHaz?.value.orEmpty()
        onInputFunction = { event -> (event.currentTarget as HTMLInputElement).validateAadat(3..10) }
    }
    br()
    label {
        htmlFor = Ids.AADAT_TUHR_INPUT
        +"Tuhr Aadat: "
    }
    input {
        id = Ids.AADAT_TUHR_INPUT
        value = inputContainerToCopyFrom?.aadatTuhr?.value.orEmpty()
        onInputFunction = { event -> (event.currentTarget as HTMLInputElement).validateAadat(15..6*30) }
    }
    br()
    label {
        htmlFor = Ids.AADAT_NIFAS_INPUT
        +"Nifas Aadat: "
    }
    input {
        id = Ids.AADAT_NIFAS_INPUT
        step = "any"
        required = true
        disabled = true
        value = inputContainerToCopyFrom?.aadatNifas?.value.orEmpty()
        onInputFunction = { event -> (event.currentTarget as HTMLInputElement).validateAadat(1..40) }
    }
}

private fun HTMLInputElement.validateAadat(validityRange: ClosedRange<Int>) {
    value = value.replace("[^0-9:]".toRegex(), "")
    val doubleValidityRange = validityRange.start.toDouble()..validityRange.endInclusive.toDouble()
    setCustomValidity(try {
        val days = parseDays(value)
        require(days == null || days in doubleValidityRange) { "Aadat is incorrect" }
        ""
    } catch (e: IllegalArgumentException) {
        e.message ?: "Aadat is incorrect"
    })
}

private fun FlowContent.pregnancyCheckBox(inputContainerToCopyFrom: HTMLElement?) {
    label {
        htmlFor = Ids.PREGNANCY_CHECKBOX
        +"Pregnancy"
    }
    checkBoxInput {
        id = Ids.PREGNANCY_CHECKBOX
        checked = inputContainerToCopyFrom?.isPregnancy == true
        onChangeFunction = { event ->
            val isChecked = (event.currentTarget as HTMLInputElement).checked
            for (pregnancyElement in findInputContainer(event).pregnancyElements) {
                pregnancyElement.disabled = !isChecked
            }
        }
    }
}

private fun FlowContent.mustabeenCheckBox(inputContainerToCopyFrom: HTMLElement?) {
    label {
        htmlFor = Ids.MUSTABEEN_CHECKBOX
        +"Mustabeen ul Khilqah"
    }
    checkBoxInput {
        id = Ids.MUSTABEEN_CHECKBOX
        checked = inputContainerToCopyFrom?.mustabeen == true
        disabled = inputContainerToCopyFrom?.mustabeen == true
    }
}

private fun FlowContent.pregnancyStartTimeInput(inputContainerToCopyFrom: HTMLElement?) {
    label {
        htmlFor = Ids.PREG_START_TIME_INPUT
        +"Pregnancy Start Time"
    }
    pregnancyTimeInput(inputContainerToCopyFrom) {
        id = Ids.PREG_START_TIME_INPUT
        onChangeFunction = { event ->
            findInputContainer(event).pregEndTime.min = (event.currentTarget as HTMLInputElement).value
        }
    }
}

private fun FlowContent.pregnancyEndTimeInput(inputContainerToCopyFrom: HTMLElement?) {
    label {
        htmlFor = Ids.PREG_END_TIME_INPUT
        +"Birth/Miscarriage time"
    }
    pregnancyTimeInput(inputContainerToCopyFrom) {
        id = Ids.PREG_END_TIME_INPUT
        onChangeFunction = { event ->
            findInputContainer(event).pregStartTime.max = (event.currentTarget as HTMLInputElement).value
        }
    }
}

private fun FlowContent.pregnancyTimeInput(inputContainerToCopyFrom: HTMLElement?, block: INPUT.() -> Unit = {}) {
    if (inputContainerToCopyFrom != null) {
        timeInput(inputContainerToCopyFrom.isDateOnly, inputContainerToCopyFrom.pregStartTime) {
            disabled = true
            block()
        }
    } else {
        timeInput(IS_DEFAULT_INPUT_MODE_DATE_ONLY) {
            disabled = true
            max = currentTimeString(IS_DEFAULT_INPUT_MODE_DATE_ONLY)
            block()
        }
    }
}

private fun FlowContent.istimrarCheckBox(inputContainerToCopyFrom: HTMLElement?) {
    label {
        htmlFor = Ids.ISTIMRAR_CHECKBOX
        +"Istimrar"
    }
    checkBoxInput {
        id = Ids.ISTIMRAR_CHECKBOX
        checked = inputContainerToCopyFrom?.isIstimrar == true
    }
}

private fun FlowContent.calculateButton() {
    button {
        +"Calculate"
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
                th { +"Start Time" }
                th { +"End Time" }
            }
        }
        tbody {
            if (inputContainerToCopyFrom != null) {
                val isDateOnly = inputContainerToCopyFrom.isDateOnly
                for (inputDateRow in inputContainerToCopyFrom.haizInputDatesRows) {
                    inputRow(isDateOnly, inputDateRow.startTimeInput, inputDateRow.endTimeInput)
                }
            } else {
                inputRow(
                    isDateOnlyLayout = IS_DEFAULT_INPUT_MODE_DATE_ONLY,
                    minTimeInput = "",
                    maxTimeInput = currentTimeString(IS_DEFAULT_INPUT_MODE_DATE_ONLY)
                )
            }
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

private fun TagConsumer<HTMLElement>.inputRow(
    isDateOnlyLayout: Boolean,
    startTimeInputToCopyFrom: HTMLInputElement,
    endTimeInputToCopyFrom: HTMLInputElement
) {
    tr {
        td {
            timeInput(isDateOnlyLayout, startTimeInputToCopyFrom, indexWithinRow = 0) {
                id = Ids.Row.INPUT_START_TIME
            }
        }
        td {
            timeInput(isDateOnlyLayout, endTimeInputToCopyFrom, indexWithinRow = 1) {
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
    isDateOnlyLayout: Boolean,
    timeInputToCopyFrom: HTMLInputElement,
    indexWithinRow: Int,
    block: INPUT.() -> Unit = {}
) {
    timeInput(isDateOnlyLayout, timeInputToCopyFrom) {
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
    isDateOnlyLayout: Boolean,
    timeInputToCopyFrom: HTMLInputElement,
    block: INPUT.() -> Unit = {}
) {
    timeInput(isDateOnlyLayout) {
        value = timeInputToCopyFrom.value
        min = timeInputToCopyFrom.min
        max = timeInputToCopyFrom.max
        block()
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
    button(type = ButtonType.button) {
        +"Remove"
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

private fun FlowContent.addButton() {
    button(type = ButtonType.button) {
        +"Add"
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

private fun TagConsumer<HTMLElement>.addBeforeButton() {
    button(type = ButtonType.button) {
        +"Add Before"
        id = Ids.Row.BUTTON_ADD_BEFORE
        onClickFunction = { event ->
            val row = findRow(event)
            val inputContainer = findInputContainer(event)
            row.before {
                inputRow(
                    inputContainer.isDateOnly,
                    minTimeInput = row.startTimeInput.min,
                    maxTimeInput = row.startTimeInput.run { value.takeUnless(String::isEmpty) ?: max }
                )
            }
            setupRows(inputContainer)
        }
    }
}

private fun findInputContainer(event: Event) =
    (event.currentTarget as Element).getAncestor<HTMLElement> { it.id.startsWith(Ids.INPUT_CONTAINER_PREFIX)}!!
private fun findRow(event: Event) = (event.currentTarget as Element).getAncestor<HTMLTableRowElement>()!!

private fun setupRows(inputContainer: HTMLElement) {
    setMaxToCurrentTimeForTimeInputs(inputContainer)
    setupFirstRow(inputContainer)
}

private fun setupFirstRow(inputContainer: HTMLElement) {
    updateRemoveButtonDisabledStateForFirstRow(inputContainer)
    ensureAddFirstButtonOnlyShownInFirstRow(inputContainer)
}

private fun updateRemoveButtonDisabledStateForFirstRow(inputContainer: HTMLElement) {
    val inputDatesRows = inputContainer.haizInputDatesRows
    inputDatesRows.first().removeButton.disabled = inputDatesRows.size == 1
    inputDatesRows.getOrNull(1)?.removeButton?.disabled = false
}

private fun ensureAddFirstButtonOnlyShownInFirstRow(inputContainer: HTMLElement) {
    for ((index, row) in inputContainer.haizInputDatesRows.withIndex()) {
        if (index > 0) {
            row.addBeforeButton?.remove()
        } else if (row.addBeforeButton == null) {
            row.buttonsContainer.append { addBeforeButton() }
        }
    }
}

private fun setMaxToCurrentTimeForTimeInputs(inputContainer: HTMLElement) {
    val currentTime = currentTimeString(inputContainer.isDateOnly)
    for (timeInputsGroup in inputContainer.timeInputsGroups) {
        for (timeInput in timeInputsGroup.asReversed()) {
            timeInput.max = currentTime
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

private fun onClickDateConfigurationRadioButton(inputContainer: HTMLElement) {
    val isDateOnly = inputContainer.isDateOnly
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
    if (!isDateOnly) {
        setMaxToCurrentTimeForTimeInputs(inputContainer)
    }
}

private fun parseEntries(inputContainer: HTMLElement) {
    println("Calculate button was clicked")
    val entries = inputContainer.haizInputDatesRows.map { row ->
        Entry(
            startTime = Date(row.startTimeInput.valueAsNumber),
            endTime = Date(row.endTimeInput.valueAsNumber)
        )
    }

    with(inputContainer) {
        @Suppress("UnsafeCastFromDynamic")
        val output = handleEntries(
            entries,
            isIstimrar,
            parseDays(aadatHaz.value),
            parseDays(aadatTuhr.value),
            isDateOnly,
            isPregnancy,
            Pregnancy(
                pregStartTime.valueAsDate,
                pregEndTime.valueAsDate,
                parseDays(aadatNifas.value),
                mustabeen
            )
        )
        contentEnglishElement.innerHTML = output.englishText
        contentUrduElement.innerHTML = output.urduText
        contentDatesElement.innerHTML = output.haizDatesText
        haizDatesList = output.hazDatesList
    }
    appendCompareButtonIfNeeded()
}

private fun compareResults(): String {
    val primaryHaizDatesList = primaryInputsContainer.haizDatesList!!
    val secondaryHaizDatesList = secondaryInputsContainer!!.haizDatesList!!
    //step 1: merge them into one list
    var dateTypeList = mutableListOf<DateTypeList>()
    for(date in primaryHaizDatesList){
        dateTypeList += DateTypeList(date.startTime,DateTypes.START)
        dateTypeList += DateTypeList(date.endTime,DateTypes.END)
    }
    for(date in secondaryHaizDatesList){
        dateTypeList += DateTypeList(date.startTime,DateTypes.START)
        dateTypeList += DateTypeList(date.endTime,DateTypes.END)
    }
    //step 2: order list by date
    dateTypeList.sortBy { it.date.getTime() }

    //step 3: create a counter
    var counter = 0

    //step 4: step through the list, create an output list
    var outputList = mutableListOf<DateTypeList>()
    for(dateType in dateTypeList){
        //plus 1 for every start time, -1 for every end time
        if(dateType.type==DateTypes.START){
            counter++
        }else{//the type is end
            counter--
        }

        if(counter == 0){
            outputList += DateTypeList(dateType.date, DateTypes.YAQEENI_PAKI)
        }else if(counter == 1){
            outputList += DateTypeList(dateType.date, DateTypes.AYYAAM_E_SHAKK)
        }else{//counter is 2
            outputList += DateTypeList(dateType.date, DateTypes.YAQEENI_NA_PAKI)
        }
    }
    //create a people-friendly version of output list
    var str = ""
    var i=0
    while (i<outputList.size-1){
        var startTime = outputList[i].date
        var endTime = outputList[i+1].date
        var type = ""
        if (outputList[i].type==DateTypes.YAQEENI_PAKI){type = "yaqeeni paki"}
        else if (outputList[i].type==DateTypes.YAQEENI_NA_PAKI){type = "yaqeeni na paki"}
        else if (outputList[i].type==DateTypes.AYYAAM_E_SHAKK){type = "ayyaam-e-shakk"}

        str += "From ${startTime} to ${endTime} is ${type}<br>"
        i++
    }
    println(str)
    return str

}
class DateTypeList (
    val date:Date,
    val type: DateTypes
)
enum class DateTypes {START,END, YAQEENI_PAKI,YAQEENI_NA_PAKI,AYYAAM_E_SHAKK}
