package com.example.engine

/**
 * Built-in offline bidirectional Amharic <-> English dictionary
 * for instant translation and search directly on the keyboard toolbar.
 */
data class DictEntry(
    val amharic: String,
    val english: String,
    val phonetic: String,
    val category: String = "General"
)

object AmharicDictionary {
    val ENTRIES: List<DictEntry> = listOf(
        // Greetings & Essentials
        DictEntry("ሰላም", "Peace / Hello", "selam", "Greetings"),
        DictEntry("ጤና ይስጥልኝ", "Hello / Good health to you", "tena yistillign", "Greetings"),
        DictEntry("እንደምን አለህ?", "How are you? (to male)", "endemen aleh?", "Greetings"),
        DictEntry("እንደምን አለሽ?", "How are you? (to female)", "endemen alesh?", "Greetings"),
        DictEntry("እንደምን አደራችሁ", "Good morning (to plural)", "endemen aderachehu", "Greetings"),
        DictEntry("እንደምን ዋላችሁ", "Good afternoon", "endemen walachehu", "Greetings"),
        DictEntry("ደህና እደሩ", "Good night", "dehna ederu", "Greetings"),
        DictEntry("ደህና ሁን", "Goodbye (to male)", "dehna hun", "Greetings"),
        DictEntry("ደህና ሁኚ", "Goodbye (to female)", "dehna hugni", "Greetings"),
        DictEntry("አመሰግናለሁ", "Thank you", "amesegenalehu", "Common"),
        DictEntry("በጣም አመሰግናለሁ", "Thank you very much", "betam amesegenalehu", "Common"),
        DictEntry("ምንም አይደለም", "You're welcome / It's nothing", "minim aydelem", "Common"),
        DictEntry("ይቅርታ", "Excuse me / Sorry", "yiqirta", "Common"),
        DictEntry("እባክህ", "Please (to male)", "ebakih", "Common"),
        DictEntry("እባክሽ", "Please (to female)", "ebakish", "Common"),
        DictEntry("እሺ", "Okay / All right", "eshi", "Common"),
        DictEntry("አዎ", "Yes", "awo", "Common"),
        DictEntry("አይ", "No", "ay", "Common"),
        DictEntry("እግዚአብሔር ይመስገን", "Praise God / I'm good", "egziabher yimesgen", "Common"),
        DictEntry("መልካም ቀን", "Have a nice day", "melkam qen", "Greetings"),
        DictEntry("መልካም እድል", "Good luck", "melkam edil", "Greetings"),
        DictEntry("እንኳን ደስ አለዎት", "Congratulations", "enkwan des alewot", "Greetings"),

        // People & Family
        DictEntry("ሰው", "Person / Human", "sew", "People"),
        DictEntry("ጓደኛ", "Friend", "gwadegna", "People"),
        DictEntry("ወንድም", "Brother", "wendim", "Family"),
        DictEntry("እህት", "Sister", "ehet", "Family"),
        DictEntry("አባት", "Father", "abat", "Family"),
        DictEntry("እናት", "Mother", "enat", "Family"),
        DictEntry("ልጅ", "Child", "lij", "Family"),
        DictEntry("ወንድ ልጅ", "Son / Boy", "wend lij", "Family"),
        DictEntry("ሴት ልጅ", "Daughter / Girl", "set lij", "Family"),
        DictEntry("ሚስት", "Wife", "mist", "Family"),
        DictEntry("ባል", "Husband", "bal", "Family"),
        DictEntry("ቤተሰብ", "Family", "beteseb", "Family"),

        // Places & Everyday Life
        DictEntry("ቤት", "House / Home", "bet", "Places"),
        DictEntry("አገር", "Country", "ager", "Places"),
        DictEntry("ኢትዮጵያ", "Ethiopia", "ityopya", "Places"),
        DictEntry("አዲስ አበባ", "Addis Ababa", "addis ababa", "Places"),
        DictEntry("ትምህርት ቤት", "School", "timihirt bet", "Places"),
        DictEntry("ሆስፒታል", "Hospital", "hospital", "Places"),
        DictEntry("ገበያ", "Market", "gebeya", "Places"),
        DictEntry("መንገድ", "Road / Street / Way", "menged", "Places"),
        DictEntry("ከተማ", "City / Town", "ketema", "Places"),
        DictEntry("ስራ", "Work / Job", "sira", "Life"),
        DictEntry("ገንዘብ", "Money", "genzeb", "Life"),
        DictEntry("ጊዜ", "Time", "gize", "Life"),
        DictEntry("ቀን", "Day", "qen", "Life"),
        DictEntry("ማታ", "Evening / Night", "mata", "Life"),
        DictEntry("ጠዋት", "Morning", "tewat", "Life"),
        DictEntry("ዛሬ", "Today", "zare", "Life"),
        DictEntry("ነገ", "Tomorrow", "nege", "Life"),
        DictEntry("ትናንት", "Yesterday", "tinant", "Life"),

        // Food & Drink
        DictEntry("ምግብ", "Food", "migib", "Food"),
        DictEntry("ውሃ", "Water", "wuha", "Food"),
        DictEntry("ዳቦ", "Bread", "dabo", "Food"),
        DictEntry("ቡና", "Coffee", "buna", "Food"),
        DictEntry("ሻይ", "Tea", "shay", "Food"),
        DictEntry("ወተት", "Milk", "wetet", "Food"),
        DictEntry("እንጀራ", "Injera", "enjera", "Food"),
        DictEntry("ወጥ", "Stew / Wat", "wet", "Food"),
        DictEntry("ስጋ", "Meat", "siga", "Food"),
        DictEntry("ፍራፍሬ", "Fruit", "fira fire", "Food"),

        // Questions & Verbs
        DictEntry("ምን?", "What?", "min?", "Questions"),
        DictEntry("የት?", "Where?", "yet?", "Questions"),
        DictEntry("መቼ?", "When?", "meche?", "Questions"),
        DictEntry("ማን?", "Who?", "man?", "Questions"),
        DictEntry("ለምን?", "Why?", "lemin?", "Questions"),
        DictEntry("እንዴት?", "How?", "endet?", "Questions"),
        DictEntry("ስንት?", "How much / How many?", "sint?", "Questions"),
        DictEntry("እፈልጋለሁ", "I want", "efeligalehu", "Verbs"),
        DictEntry("አለ", "There is / Exists", "ale", "Verbs"),
        DictEntry("የለም", "There is none", "yelem", "Verbs"),
        DictEntry("ሂድ", "Go (to male)", "hid", "Verbs"),
        DictEntry("ና", "Come (to male)", "na", "Verbs"),
        DictEntry("ውደድ", "Love", "weded", "Verbs"),
        DictEntry("ፍቅር", "Love", "fiqir", "Concepts"),
        DictEntry("እውነት", "Truth", "iwnet", "Concepts")
    )

    /**
     * Search dictionary matching both English and Amharic queries.
     */
    fun search(query: String): List<DictEntry> {
        val q = query.trim().lowercase()
        if (q.isEmpty()) return ENTRIES.take(15)

        return ENTRIES.filter { entry ->
            entry.amharic.contains(q, ignoreCase = true) ||
            entry.english.lowercase().contains(q) ||
            entry.phonetic.lowercase().contains(q) ||
            entry.category.lowercase().contains(q)
        }
    }
}
