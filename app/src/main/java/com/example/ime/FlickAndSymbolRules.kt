package com.example.ime

import com.example.engine.AmharicPhoneticRules

/**
 * Rules and mappings for flick gestures and secondary symbol assignments.
 */
object FlickAndSymbolRules {

    // Number & symbol mappings for QWERTY rows when flicking UP
    val FLICK_UP_MAPPINGS: Map<String, String> = mapOf(
        "q" to "1", "w" to "2", "e" to "3", "r" to "4", "t" to "5",
        "y" to "6", "u" to "7", "i" to "8", "o" to "9", "p" to "0",
        "a" to "@", "s" to "#", "d" to "$", "f" to "%", "g" to "&",
        "h" to "*", "j" to "-", "k" to "+", "l" to "=",
        "z" to "!", "x" to "?", "c" to "'", "v" to "\"", "b" to "/",
        "n" to ";", "m" to ":"
    )

    /**
     * Gets the full Ge'ez family orders for a given English key.
     * Returns list of characters: [1st, 2nd, 3rd, 4th, 5th, 6th, 7th, 8th]
     */
    fun getGeezFamily(keyChar: String): List<String>? {
        val lower = keyChar.lowercase()
        val family = AmharicPhoneticRules.FAMILIES.find { it.baseKey == lower } ?: return null
        return buildList {
            add(family.first)   // 1st (ä/e)
            add(family.second)  // 2nd (u)
            add(family.third)   // 3rd (i)
            add(family.fourth)  // 4th (a)
            add(family.fifth)   // 5th (ee)
            add(family.sixth)   // 6th (ï/base)
            add(family.seventh) // 7th (o)
            if (family.eighth != null) {
                add(family.eighth)
            }
        }
    }
}
