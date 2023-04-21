@file:Suppress("SpellCheckingInspection")

import io.ktor.util.date.*
import kotlinx.datetime.internal.JSJoda.Instant
import kotlinx.datetime.internal.JSJoda.LocalDateTime
import kotlinx.serialization.*
import kotlin.js.Date
import kotlin.random.Random

//object DateAsLongSerializer : KSerializer<Date> {
//    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Date", PrimitiveKind.LONG)
//    override fun serialize(encoder: Encoder, value: Date) {
//        encoder.encodeLong(value.getTime().toLong())
//    }
//
//    override fun deserialize(decoder: Decoder): Date {
//        return Date(decoder.decodeLong())
//    }
//}

data class Strings(
    val answer: String,
    val headerline: String,
    val headerlinemubtadia: String,
    val writedown: String,
    val allahknows: String,
    val currentpaki: String,
    val currenthaiz: String,
    val situationmaychange:String,
    val haizend: String,
    val beforepregheader: String,
    val preg: String,
    val birth: String,
    val afterpregheader:String,
    val earlymiscarriage: String,
    val ihtiyatighusl: String,
    val sexnotallowed: String,
    val endofpaki: String,
    val habit: String,
    val haizdays: String,
    val haizdaysinsolution:String,
    val istihazadays: String,
    val nifasdays: String,
    val nifasdaysinsolution:String,
    val blooddays: String,
    val solution: String,
    val istihazadetailslineone: String,
    val istihazadetailslinetwo: String,
    val continuosbleeding: String,
    val pakidays:String,
    val tuhrfasid:String,
    val tuhrfasidwithaddition:String,
    val twomonthstuhr: String,
    val dashesline: String,
    val becamemutadah:String,
    val thereisnoaadat:String,
    val aadatofhaizonly:String,
    val selectLanguage:String,
    val dateOnly:String,
    val dateAndTime:String,
    val urdu:String,
    val english:String,
    val haizAadat:String,
    val tuhrAadat:String,
    val mawjoodahTuhr:String,
    val faasid:String,
    val nifasAadat:String,
    val pregnancyStartTime:String,
    val birthMiscarrriageTime:String,
    val startTime:String,
    val endTime:String,
    val calculate:String,
    val incorrectAadat:String,
    val nifas:String,
    val mustabeenUlKhilqa: String,
    val errorEnterAadat: String,
    val errorEnterMawjoodaPaki:String,
    val errorEnterNifasAadat:String,
    val passwordRequired:String,
    val warningOnlyAuthorizedPersonnel:String,
    val typeOfInput: String,
    val duration: String,
    val damOrTuhr: String,
    val dam: String,
    val tuhr: String,
    val durationDam: String,
    val durationPaki:String,
    val durationTuhrefasid:String,
    val durationTuhreFasidWithAddition:String,
    val durationHaiz:String,
    val durationNifas:String,
    val startingFromIstehaza:String,
    val startingFromHaiz:String,
    val followedByistehazaAfter:String,
    val followedByHaizAfter:String,
    val khatimaplusnewline:String,
    val birthduration: String,
    val pregduration:String,
    val startingFromNifas:String,
    val bleedingstopsbeforethree:String,
    val bleedingstopsbeforethreemaslachanges: String,
    val afterfortydays:String,
    val aftertendays:String,
    val considerTuhrInGhiarMustabeenIsqaatIkhtilaf:String,
    val ikhtilafimasail: String,
    val aadatIncreasingAtEndOfDaurIkhtilaf:String,
    val beforetendaysayyameqabliyyaallconsideredhaiz:String,
    val endofistehazaayyameqabliyya:String,
    val daysayyameqabliyya:String,
    val mubtadia:String,
    val mutada:String,
    val typeOfMasla:String,
    val askagainnodate:String,
    val tendaysdoghusl:String,
    val habitwithnifas: String,
    val earlymiscarriageduration: String,
    val ayyameqabliyyaikhtilaf:String,
    val mubtadiaikhitilaf:String,
    val onlynifashabit:String,
    val nifasAndHaizHabit:String,
    val nifasAndTuhrHabit:String,
    val onlyTuhrHabit:String,
    val titleTextFieldLabel:String,
    val questionTextFieldLabel:String,
    val isqat: String,
    val preMaslaHabitOfHaizAndTuhr: String,
    val preMaslaValueOfMawjoodaPaki: String,
    val zaalla: String,
    val zaallaCycleLength: String,
    val isDaylightSavings: String,
    )

data class AllTheInputs(
    val entries: List<Entry>?= null,
    val preMaslaValues: PreMaslaValues = PreMaslaValues(null,null, null, false),
    val typeOfMasla:TypesOfMasla = TypesOfMasla.MUTADAH,
    val pregnancy: Pregnancy? = null,
    val typeOfInput: TypesOfInputs = TypesOfInputs.DATE_ONLY,
    val language: String = Vls.Langs.URDU,
    val ikhtilaafaat:Ikhtilaafaat = Ikhtilaafaat(true,
        false,
        false,
        false),
    val timeZone: String? = "UTC",
)

// Todo: make proper uid
@Serializable
data class SaveData(
    val uid: String = getTimeMillis().toString() + Random.nextInt(100, 1000).toString(),
    val typeOfMasla: String = Vls.Maslas.MUTADA,
    val typeOfInput: String = Vls.Types.DATE_ONLY,
    val entries: List<SaveEntries>? = null,
    val answerEnglish: String? = "",
    val answerUrdu: String? = "",
    val others: OtherValues? = null,
)

@Serializable
data class SaveEntries(
    val startTime: String,
    val endTime: String,
)

@Serializable
data class OtherValues(
    val question: String? = null,
    var aadatHaiz: String? = null,
    var aadatTuhr: String? = null,
    var mawjoodahTuhr: String? = null,
    var isMawjoodaFasid: Boolean? = false,
//    @Serializable(with = DateAsLongSerializer::class)
    val pregStartTime: String? = null,
//    @Serializable(with = DateAsLongSerializer::class)
    val birthTime: String? = null,
    val aadatNifas: String? = null,
    val mustabeenUlKhilqat: Boolean? = false,
    val ghairMustabeenIkhtilaaf: Boolean = false,
    val daurHaizIkhtilaf: Boolean = false,
    var ayyameQabliyyaIkhtilaf: Boolean = false,
    val mubtadiaIkhitilaf: Boolean = false,
)

data class PreMaslaValues(
    var inputtedAadatHaiz: Long? = null,
    var inputtedAadatTuhr: Long? = null,
    var inputtedMawjoodahTuhr: Long? = null,
    var isMawjoodaFasid: Boolean = false,
)

data class Ikhtilaafaat(
    val ghairMustabeenIkhtilaaf: Boolean = false,
    val daurHaizIkhtilaf: Boolean = false,
    var ayyameQabliyyaIkhtilaf: Boolean = false,
    val mubtadiaIkhitilaf: Boolean = false
)


enum class TypesOfInputs {
    DATE_ONLY,
    DATE_AND_TIME,
    DURATION
}

enum class TypesOfMasla {
    MUBTADIA,
    MUTADAH,
    NIFAS
}

data class Entry(
    val startTime: Instant,
    val endTime: Instant
)
data class LocalEntry(
    val startTime: LocalDateTime,
    val endTime: LocalDateTime
)
enum class TypesOfFutureDates {
    A3_CHANGING_TO_A2,
    END_OF_AADAT_HAIZ,
    END_OF_AADAT_TUHR,
    BEFORE_THREE_DAYS_MASLA_WILL_CHANGE,
    BEFORE_THREE_DAYS,
    AFTER_TEN_DAYS,
    FORTY_DAYS,
    IC_FORBIDDEN_DATE,
    IHTIYATI_GHUSL,
    BEFORE_TEN_DAYS_AYYAMEQABLIYYAH,
    START_OF_AADAT_AYYAMEQABLIYYA,
    TEN_DAYS_EXACTLY

}

class FutureDateType(
    val date:Instant,
    val futureDates:TypesOfFutureDates,
    val date2:Instant? = null,
)
class EndingOutputValues(
    val filHaalPaki:Boolean?,
    val aadats:AadatsOfHaizAndTuhr?,
    val futureDateType: MutableList<FutureDateType>
)
class OutputTexts (
    var englishText:String,
    var urduText: String,
    var haizDatesText:String,
    var hazDatesList: MutableList<Entry>,
    var endingOutputValues:EndingOutputValues,
    var fixedDurations: MutableList<FixedDuration>
)


class InfoForCompareTable(
    val headerList: List<Instant>,
    val listOfColorsOfDaysList: List<List<Int>>,
    val resultColors: List<Int>
)

data class Pregnancy(
    val pregStartTime: Instant = ARBITRARY_DATE,
    val birthTime: Instant = ARBITRARY_DATE,
    var aadatNifas: Long? = 40.getMilliDays(),
    val mustabeenUlKhilqat: Boolean,
)

enum class DurationType {
    DAM,
    TUHR,
    TUHREFAASID,
    TUHR_IN_HAML,
    TUHREFAASID_IN_HAML,
    NIFAS,
    DAM_IN_HAML,
    DAM_IN_NIFAS_PERIOD,
    ISTIHAZA_BEFORE,
    ISTIHAZA_AFTER,
    HAIZ,
    LESS_THAN_3_HAIZ,
    HAML,
    WILADAT_ISQAT,
    TUHR_BIGGER_THAN_6_MONTHS,
    TUHR_MUBTADIA_BECAME_A_MUTADA_NOW,
    DAM_MUBTADIA,
    TUHR_MUBTADIA,
    TUHREFAASID_MUBTADIA,
    TUHREFAASID_MUBTADIA_WITH_ISTEHAZA,
    TUHREFAASID_WITH_ISTEHAZA,
    ISTEHAZA_AYYAMEQABLIYYA,
    START_OF_AADAT_AYYAMEQABLIYYA
}

class DateTypeList (
    val date: Date,
    val type: DateTypes
)
enum class DateTypes {START,END, YAQEENI_PAKI,YAQEENI_NA_PAKI,AYYAAM_E_SHAKK_DUKHOOL, AYYAAM_E_SHAKK_KHUROOJ}

class DurationTypes (
    val startTime: Date,
    val endTime: Date,
    val type: DateTypes
)

enum class Soortain {
    A_1, A_2, A_3, B_2, B_3
}

data class Duration(
    val type: DurationType,
    val timeInMilliseconds: Long,
    var startTime: Instant = ARBITRARY_DATE
) {
    val days: Double get() = timeInMilliseconds / MILLISECONDS_IN_A_DAY.toDouble()
    val endDate: Instant get() = startTime.plusMillis(timeInMilliseconds)

}

data class FixedDuration(
    var type: DurationType,
    var timeInMilliseconds: Long,
    var indices: MutableList<Int> = mutableListOf(),
    var istihazaAfter: Long = 0,
    var ayyameqabliyya:AyyameQabliyya? = null,
    var biggerThanTen: BiggerThanTenDm? = null,
    var biggerThanForty: BiggerThanFortyNifas? = null,
    var startDate: Instant = Instant.EPOCH,
) {
    val days: Double get() = timeInMilliseconds / MILLISECONDS_IN_A_DAY.toDouble()
    val endDate: Instant get() = this.startDate.plusMillis(this.timeInMilliseconds)
}

data class AyyameQabliyya(
    var ayyameqabliyya: Long,
    var aadatHaiz: Long,
    var aadatTuhr: Long
)

data class BiggerThanTenDm(
    var mp: Long, //mawjooda paki
    var gp: Long, //aadat of Tuhr before solving this
    var dm: Long, //dam
    var hz: Long, //aadat of haiz before solving this
    var qism: Soortain, //name of that case A-1, A-2, A-3, B-2, B-3
    var istihazaBefore: Long, //number of days of istihaza before haiz
    var haiz:Long, //number of days of haiz (also aadat of haiz before istimrar)
    var istihazaAfter: Long, //number of days of istihaza after haiz
    var aadatHaiz:Long, //aadat of haiz after end of istimrar
    var aadatTuhr:Long, //aadat of tuhur after solving this
    var durationsList: MutableList<Duration>

)
data class BiggerThanFortyNifas(
    var nifas: Long, //muddate nifas
    var istihazaAfter: Long, //number of days of istihaza after nifas
    var haiz:Long, //aadat of haiz before solving this
    var aadatHaiz:Long, //aadat of haiz after solving this
    var aadatTuhr:Long, //aadat of tuhur after solving this
    var durationsList: MutableList<Duration>
)

data class AadatsOfHaizAndTuhr(
    var aadatHaiz: Long,
    var aadatTuhr: Long,
    var aadatNifas: Long? = null
)

data class AadaatWithChangeability(
    var aadaat:AadatsOfHaizAndTuhr,
    var isChangeable:Boolean
)

data class AadatAfterIndexOfFixedDuration(
    var aadat: Long,
    var index: Int
)

data class OutputStringsLanguages(
    var urduString:String,
    var englishString: String
)

data class TzInfo (
    val info: String,
    val tz: String
)
