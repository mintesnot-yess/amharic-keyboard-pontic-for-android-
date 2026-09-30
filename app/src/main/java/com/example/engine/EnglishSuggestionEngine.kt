package com.example.engine

/**
 * Fast, offline English word prediction and auto-completion engine.
 * Provides real-time context and prefix suggestions as letters are typed in English mode.
 */
object EnglishSuggestionEngine {

    // Comprehensive list of common English words sorted by frequency
    private val VOCABULARY: List<String> = listOf(
        "the", "be", "to", "of", "and", "a", "in", "that", "have", "i",
        "it", "for", "not", "on", "with", "he", "as", "you", "do", "at",
        "this", "but", "his", "by", "from", "they", "we", "say", "her", "she",
        "or", "an", "will", "my", "one", "all", "would", "there", "their", "what",
        "so", "up", "out", "if", "about", "who", "get", "which", "go", "me",
        "when", "make", "can", "like", "time", "no", "just", "him", "know", "take",
        "people", "into", "year", "your", "good", "some", "could", "them", "see", "other",
        "than", "then", "now", "look", "only", "come", "its", "over", "think", "also",
        "back", "after", "use", "two", "how", "our", "work", "first", "well", "way",
        "even", "new", "want", "because", "any", "these", "give", "day", "most", "us",
        "hello", "help", "helpful", "here", "hear", "great", "morning", "night", "thanks", "thank",
        "please", "yes", "sorry", "welcome", "friend", "family", "house", "place", "school", "work",
        "today", "tomorrow", "yesterday", "tonight", "always", "never", "sometimes", "often", "usually", "happy",
        "love", "life", "live", "hope", "peace", "world", "country", "city", "street", "road",
        "phone", "number", "email", "address", "name", "message", "call", "send", "receive", "write",
        "read", "watch", "listen", "speak", "talk", "tell", "ask", "answer", "question", "problem",
        "need", "feel", "try", "leave", "call", "should", "must", "might", "may", "can",
        "right", "wrong", "true", "false", "big", "small", "long", "short", "high", "low",
        "fast", "slow", "easy", "hard", "simple", "difficult", "strong", "weak", "hot", "cold",
        "warm", "cool", "clean", "dirty", "fine", "nice", "kind", "beautiful", "wonderful", "amazing",
        "awesome", "perfect", "ready", "done", "finished", "started", "start", "stop", "open", "close",
        "meet", "meeting", "check", "point", "part", "number", "group", "problem", "fact", "case",
        "week", "month", "hour", "minute", "second", "moment", "thing", "things", "everything", "nothing",
        "something", "anything", "everyone", "someone", "anyone", "nobody", "anywhere", "everywhere", "somewhere",
        "before", "during", "while", "until", "since", "again", "already", "almost", "together", "around",
        "through", "under", "between", "without", "against", "behind", "beyond", "beside", "inside", "outside",
        "money", "business", "service", "system", "program", "question", "change", "information", "water", "food",
        "coffee", "tea", "bread", "breakfast", "lunch", "dinner", "book", "computer", "internet", "online",
        "offline", "account", "password", "settings", "profile", "search", "share", "save", "delete", "edit",
        "update", "download", "upload", "install", "version", "application", "keyboard", "language", "amharic",
        "english", "ethiopia", "addis", "ababa", "africa", "office", "store", "market", "shop", "hospital",
        "doctor", "nurse", "student", "teacher", "class", "lesson", "paper", "pen", "pencil", "card",
        "photo", "picture", "video", "music", "song", "audio", "voice", "sound", "light", "dark",
        "color", "white", "black", "red", "green", "blue", "yellow", "orange", "purple", "brown",
        "car", "bus", "train", "flight", "plane", "travel", "trip", "hotel", "room", "bed",
        "door", "window", "table", "chair", "bag", "box", "key", "screen", "battery", "power",
        "happy", "sad", "angry", "tired", "excited", "busy", "free", "late", "early", "soon",
        "later", "maybe", "sure", "definitely", "probably", "exactly", "actually", "really", "very", "much"
    )

    /**
     * Look up matching words starting with [prefix].
     * Preserves capitalization (e.g. "Hel" -> "Hello", "THE" -> "THE").
     */
    fun getSuggestions(prefix: String, limit: Int = 6): List<String> {
        if (prefix.isBlank()) return emptyList()

        val lowerPrefix = prefix.lowercase()
        val isCapitalized = prefix.isNotEmpty() && prefix[0].isUpperCase() && (prefix.length == 1 || prefix.drop(1).all { it.isLowerCase() })
        val isAllUpper = prefix.length > 1 && prefix.all { it.isUpperCase() }

        val matches = VOCABULARY.filter { it.startsWith(lowerPrefix) && it != lowerPrefix }
            .take(limit)

        return matches.map { word ->
            when {
                isAllUpper -> word.uppercase()
                isCapitalized -> word.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
                else -> word
            }
        }
    }
}
