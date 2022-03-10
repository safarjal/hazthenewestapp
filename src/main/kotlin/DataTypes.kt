import kotlinx.html.attributes.StringSetEncoder
import kotlin.js.Date


data class Strings(
    val answer: String,
//    جواب: 
    val headerline: String,
//    مندرجہ ذیل ترتیب سے دم و طہر آیا:
    val headerlinemubtadia: String,
//    مبتداہ کو اس ترتیب سے دم و طہر آیا:
    val writedown: String,
//    جب بھی خون یا دھبے آئیں تو وقت تاریخ مہینہ نوٹ فرمالیجئے۔
    val allahknows: String,
//    واللہ تعالی اعلم بالصواب
    val currentpaki: String,
//    فی الحال آپ کے پاکی کے دن ہیں اپنی عبادات جاری رکھیں۔
    val currenthaiz: String,
//    فی الحال آپ کے حیض کے دن ہیں نمازیں نہ پڑھیں۔
    val situationmaychange:String,
//    "اگر خون اسی طرح جاری رہے یا فی الحال بند ہوجائے لیکن پندرہ دن کی کامل پاکی نہیں ملی کہ دوبارہ خون یا دھبہ آگیا تب پھرdate کو ضرور دوبارہ پوچھ لیں، اس لیے کہ مسئلہ کی صورت بدل جائے گی۔
    val haizend: String,
//    "اگر خون اسی طرح جاری رہے تب پھرdate تک آپ کے حیض کے دن ہونگے۔
    val beforepregheader: String,
//    "حمل سے پہلے اس ترتیب سے خون آیا اور پاکی ملی:
    val preg: String,
//    حمل
    val birth: String,
//     date ولادت
    val afterpregheader:String,
//    ولادت کے بعد اس ترتیب سے خون آیااور پاکی ملی:
    val earlymiscarriage: String,
//    date کو اسقاط ہوا (غیر مستبین الخلقہ)
    val ihtiyatighusl: String,
//    اگر خون date سے پہلے بند ہو جاۓ تو غسل کر کے نمازیں شروع کر لیں، لیکن احتیاطا date کو بھی غسل کر لیجیے۔
    val sexnotallowed: String,
//    اگر سائلہ شادی شدہ ہیں تو یہ مسئلہ بھی مدنظر رکھیں: 
//    اگر خون رک بھي جاۓ اور غسل کر کے نمازيں بھي شروع کر لي ہوں، تب بھي date سے پہلے صحبت کي اجازت نہيں۔
    val endofpaki: String,
//    اگر خون اسی طرح جاری رہے یا فی الحال بند ہوجائے لیکن پندرہ دن کی کامل پاکی نہیں ملی کہ دوبارہ خون یا دھبہ آگیا تب پھرdate تک آپ کے یقینی پاکی کے دن ہونگے۔
    val habit: String,
//    عادت:: حیض: days, hours, minutes ، طہر: days, hours, minutes
    val haizdays: String,
//    date تا date کل days, hours, minutes حیض کے ہیں۔
    val haizdaysinsolution:String,
    //this will have the red circle
    val istihazadays: String,
//    date تا date کل days, hours, minutes یقینی پاکی (استحاضہ) کے ہیں۔
    val nifasdays: String,
//    date تا date کل days, hours, minutes نفاس کے ہیں۔
    val nifasdaysinsolution:String,
    //these will have red circle
    val blooddays: String,
//    date سے date تک کل days, hours, minutes خون۔
    val solution: String,
//    مسئلہ کا حل ::
    val istihazadetailslineone: String,
//    اس دوران میں جو نمازیں حیض سمجھ کر چھوڑیں،  ان کی قضاء ضروری ہے۔
    val istihazadetailslinetwo: String,
//    date کو اگر غسل کر لیا تھا، تو غسل کے بعد والی نمازیں درست ہیں۔ اگر غسل نہیں کیا تھا، تو جب تک غسل نہیں کیا، اس کی نمازیں قضاء کریں۔
//    اگر اس دوران میں کوئی نمازیں حیض سمجھ کر چھوڑیں تھیں، ان کو بھی قضاء کریں۔
    val continuosbleeding: String,
//    date سے date تک کل days, hours, minutes خون جاری رھا (چونکہ آپ کو دو خون کے درمیان میں 15 دن کی کامل پاکی نہیں ملی ہے اسلیئے یوں سمجھا جائے گا کہ آپ کو مسلسل خون جاری ہی رہا ہے۔)
    val pakidays:String,
//    days, hours, minutes پاکی۔
    val tuhrfasid:String,
    val tuhrfasidwithaddition:String,
//    days, hours, minutes استحاضہ + days, hours, minutes پاکی = days, hours, minutes  طہر فاسد۔
    val twomonthstuhr: String,
//    days, hours, minutes طہر (چونکہ طہر 6 ماہ سے زیادہ ہے، اس لیے عادت میں 60 دن لیا جاۓ گا۔)
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
    val tab:String,
    val startingFromIstehaza:String,
    val startingFromHaiz:String,
    val followedByistehazaAfter:String,
    val followedByHaizAfter:String,
    val khatimaplusnewline:String,
    val birthduration: String,

)




enum class LanguageNames { ENGLISH, URDU }

data class Entry(
    val startTime: Date,
    val endTime: Date
)
enum class TypesOfFutureDates { A3_CHANGING_TO_A2, END_OF_AADAT_HAIZ, END_OF_AADAT_TUHR }

class FutureDateType(
    val date:Date,
    val futureDates:TypesOfFutureDates
)
class EndingOutputValues(
    val filHaalPaki:Boolean,
    val aadats:AadatsOfHaizAndTuhr?,
    val futureDateType: FutureDateType?
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
    val headerList: List<Date>,
    val listOfColorsOfDaysList: List<List<Int>>,
    val resultColors:List<Int>
)


data class Pregnancy(
    val pregStartTime:Date,
    val birthTime:Date,
    var aadatNifas:Long? = 40*MILLISECONDS_IN_A_DAY,
    val mustabeenUlKhilqat:Boolean
)

enum class DurationType {
    DAM,
    TUHR,
    TUHREFAASID,
    TUHR_IN_HAML,
    NIFAAS,
    DAM_IN_HAML,
    DAM_IN_NIFAAS_PERIOD,
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
    TUHREFAASID_WITH_ISTEHAZA
}

class DateTypeList (
    val date:Date,
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
    var startTime: Date
) {
    val days: Double get() = timeInMilliseconds / MILLISECONDS_IN_A_DAY.toDouble()
    val endDate: Date get() = Date(startTime.getTime().toLong() + (timeInMilliseconds))

}

data class FixedDuration(
    var type: DurationType,
    var timeInMilliseconds: Long,
    var indices: MutableList<Int> = mutableListOf(),
    var istihazaAfter: Long = 0,
    var biggerThanTen: BiggerThanTenDm? = null,
    var biggerThanForty: BiggerThanFortyNifas? = null,
    var startDate: Date = Date(1,1,1),
) {
    val days: Double get() = timeInMilliseconds / MILLISECONDS_IN_A_DAY.toDouble()
    val endDate: Date get() = addTimeToDate(this.startDate, this.timeInMilliseconds)
}

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
    var aadatTuhr: Long
//    var decisionBasedOnBloodStopping: Boolean
)