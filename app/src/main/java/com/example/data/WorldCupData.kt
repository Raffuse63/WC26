package com.example.data

data class Match(
    val matchNumber: Int,
    val date: String,     // e.g. "2026-06-12"
    val day: String,      // e.g. "Friday"
    val dayNum: Int,      // e.g. 12
    val month: String,    // e.g. "June"
    val time: String,     // e.g. "01:00" (24-hour style format)
    val team1Code: String,
    val team2Code: String,
    val venue: String,
    val stage: String,    // "group", "r32", "r16", "qf", "sf", "3rd", "final"
    val group: String     // e.g. "Group A"
)

object WorldCupData {
    val TEAM_FLAGS = mapOf(
        "MEX" to "🇲🇽", "RSA" to "🇿🇦", "KOR" to "🇰🇷", "CZE" to "🇨🇿", "CAN" to "🇨🇦", "BIH" to "🇧🇦", "USA" to "🇺🇸", "PAR" to "🇵🇾",
        "QAT" to "🇶🇦", "SUI" to "🇨🇭", "BRA" to "🇧🇷", "MAR" to "🇲🇦", "HAI" to "🇭🇹", "SCO" to "🏴󠁧󠁢󠁳󠁣󠁴󠁿", "AUS" to "🇦🇺", "TUR" to "🇹🇷",
        "GER" to "🇩🇪", "CUW" to "🇨🇼", "NED" to "🇳🇱", "JPN" to "🇯🇵", "CIV" to "🇨🇮", "ECU" to "🇪🇨", "SWE" to "🇸🇪", "TUN" to "🇹🇳",
        "ESP" to "🇪🇸", "CPV" to "🇨🇻", "BEL" to "🇧🇪", "EGY" to "🇪🇬", "KSA" to "🇸🇦", "URU" to "🇺🇾", "IRN" to "🇮🇷", "NZL" to "🇳🇿",
        "FRA" to "🇫🇷", "SEN" to "🇸🇳", "IRQ" to "🇮🇶", "NOR" to "🇳🇴", "ARG" to "🇦🇷", "ALG" to "🇩🇿", "AUT" to "🇦🇹", "JOR" to "🇯🇴",
        "POR" to "🇵🇹", "COD" to "🇨🇩", "ENG" to "🏴󠁧󠁢󠁥󠁮󠁧󠁿", "CRO" to "🇭🇷", "GHA" to "🇬🇭", "PAN" to "🇵🇦", "UZB" to "🇺🇿", "COL" to "🇨🇴"
    )

    val TEAM_NAMES = mapOf(
        "MEX" to "Mexico", "RSA" to "South Africa", "KOR" to "Korea Republic", "CZE" to "Czech Republic",
        "CAN" to "Canada", "BIH" to "Bosnia & Herzegovina", "USA" to "United States", "PAR" to "Paraguay",
        "QAT" to "Qatar", "SUI" to "Switzerland", "BRA" to "Brazil", "MAR" to "Morocco", "HAI" to "Haiti",
        "SCO" to "Scotland", "AUS" to "Australia", "TUR" to "Türkiye", "GER" to "Germany", "CUW" to "Curaçao",
        "NED" to "Netherlands", "JPN" to "Japan", "CIV" to "Côte d'Ivoire", "ECU" to "Ecuador", "SWE" to "Sweden",
        "TUN" to "Tunisia", "ESP" to "Spain", "CPV" to "Cape Verde", "BEL" to "Belgium", "EGY" to "Egypt",
        "KSA" to "Saudi Arabia", "URU" to "Uruguay", "IRN" to "Iran", "NZL" to "New Zealand", "FRA" to "France",
        "SEN" to "Senegal", "IRQ" to "Iraq", "NOR" to "Norway", "ARG" to "Argentina", "ALG" to "Algeria",
        "AUT" to "Austria", "JOR" to "Jordan", "POR" to "Portugal", "COD" to "DR Congo", "ENG" to "England",
        "CRO" to "Croatia", "GHA" to "Ghana", "PAN" to "Panama", "UZB" to "Uzbekistan", "COL" to "Colombia"
    )

    val matches = listOf(
        // Group Stage (72 matches)
        Match(1, "2026-06-12", "Friday", 12, "June", "01:00", "MEX", "RSA", "Mexico City Stadium", "group", "Group A"),
        Match(2, "2026-06-12", "Friday", 12, "June", "08:00", "KOR", "CZE", "Guadalajara Stadium", "group", "Group A"),
        Match(3, "2026-06-13", "Saturday", 13, "June", "01:00", "CAN", "BIH", "Toronto Stadium", "group", "Group B"),
        Match(4, "2026-06-13", "Saturday", 13, "June", "07:00", "USA", "PAR", "Los Angeles Stadium", "group", "Group D"),
        Match(5, "2026-06-14", "Sunday", 14, "June", "01:00", "QAT", "SUI", "San Francisco Bay Area", "group", "Group B"),
        Match(6, "2026-06-14", "Sunday", 14, "June", "04:00", "BRA", "MAR", "New York/New Jersey", "group", "Group C"),
        Match(7, "2026-06-14", "Sunday", 14, "June", "07:00", "HAI", "SCO", "Boston Stadium", "group", "Group C"),
        Match(8, "2026-06-14", "Sunday", 14, "June", "10:00", "AUS", "TUR", "BC Place Vancouver", "group", "Group D"),
        Match(9, "2026-06-14", "Sunday", 14, "June", "23:00", "GER", "CUW", "Houston Stadium", "group", "Group E"),
        Match(10, "2026-06-15", "Monday", 15, "June", "02:00", "NED", "JPN", "Dallas Stadium", "group", "Group F"),
        Match(11, "2026-06-15", "Monday", 15, "June", "05:00", "CIV", "ECU", "Philadelphia Stadium", "group", "Group E"),
        Match(12, "2026-06-15", "Monday", 15, "June", "08:00", "SWE", "TUN", "Monterrey Stadium", "group", "Group F"),
        Match(13, "2026-06-15", "Monday", 15, "June", "22:00", "ESP", "CPV", "Atlanta Stadium", "group", "Group H"),
        Match(14, "2026-06-16", "Tuesday", 16, "June", "01:00", "BEL", "EGY", "Seattle Stadium", "group", "Group G"),
        Match(15, "2026-06-16", "Tuesday", 16, "June", "04:00", "KSA", "URU", "Miami Stadium", "group", "Group H"),
        Match(16, "2026-06-16", "Tuesday", 16, "June", "07:00", "IRN", "NZL", "Los Angeles Stadium", "group", "Group G"),
        Match(17, "2026-06-17", "Wednesday", 17, "June", "01:00", "FRA", "SEN", "New York/New Jersey", "group", "Group I"),
        Match(18, "2026-06-17", "Wednesday", 17, "June", "04:00", "IRQ", "NOR", "Boston Stadium", "group", "Group I"),
        Match(19, "2026-06-17", "Wednesday", 17, "June", "07:00", "ARG", "ALG", "Kansas City Stadium", "group", "Group J"),
        Match(20, "2026-06-17", "Wednesday", 17, "June", "10:00", "AUT", "JOR", "San Francisco Bay Area", "group", "Group J"),
        Match(21, "2026-06-17", "Wednesday", 17, "June", "23:00", "POR", "COD", "Houston Stadium", "group", "Group K"),
        Match(22, "2026-06-18", "Thursday", 18, "June", "02:00", "ENG", "CRO", "Dallas Stadium", "group", "Group L"),
        Match(23, "2026-06-18", "Thursday", 18, "June", "05:00", "GHA", "PAN", "Toronto Stadium", "group", "Group L"),
        Match(24, "2026-06-18", "Thursday", 18, "June", "08:00", "UZB", "COL", "Mexico City Stadium", "group", "Group K"),
        Match(25, "2026-06-18", "Thursday", 18, "June", "22:00", "CZE", "RSA", "Atlanta Stadium", "group", "Group A"),
        Match(26, "2026-06-19", "Friday", 19, "June", "01:00", "SUI", "BIH", "Los Angeles Stadium", "group", "Group B"),
        Match(27, "2026-06-19", "Friday", 19, "June", "04:00", "CAN", "QAT", "BC Place Vancouver", "group", "Group B"),
        Match(28, "2026-06-19", "Friday", 19, "June", "07:00", "MEX", "KOR", "Guadalajara Stadium", "group", "Group A"),
        Match(29, "2026-06-20", "Saturday", 20, "June", "01:00", "USA", "AUS", "Seattle Stadium", "group", "Group D"),
        Match(30, "2026-06-20", "Saturday", 20, "June", "04:00", "SCO", "MAR", "Boston Stadium", "group", "Group C"),
        Match(31, "2026-06-20", "Saturday", 20, "June", "06:30", "BRA", "HAI", "Philadelphia Stadium", "group", "Group C"),
        Match(32, "2026-06-20", "Saturday", 20, "June", "09:00", "TUR", "PAR", "San Francisco Bay Area", "group", "Group D"),
        Match(33, "2026-06-20", "Saturday", 20, "June", "23:00", "NED", "SWE", "Houston Stadium", "group", "Group F"),
        Match(34, "2026-06-21", "Sunday", 21, "June", "02:00", "GER", "CIV", "Toronto Stadium", "group", "Group E"),
        Match(35, "2026-06-21", "Sunday", 21, "June", "06:00", "ECU", "CUW", "Kansas City Stadium", "group", "Group E"),
        Match(36, "2026-06-21", "Sunday", 21, "June", "10:00", "TUN", "JPN", "Monterrey Stadium", "group", "Group F"),
        Match(37, "2026-06-21", "Sunday", 21, "June", "22:00", "ESP", "KSA", "Atlanta Stadium", "group", "Group H"),
        Match(38, "2026-06-22", "Monday", 22, "June", "01:00", "BEL", "IRN", "Los Angeles Stadium", "group", "Group G"),
        Match(39, "2026-06-22", "Monday", 22, "June", "04:00", "URU", "CPV", "Miami Stadium", "group", "Group H"),
        Match(40, "2026-06-22", "Monday", 22, "June", "07:00", "NZL", "EGY", "BC Place Vancouver", "group", "Group G"),
        Match(41, "2026-06-22", "Monday", 22, "June", "23:00", "ARG", "AUT", "Dallas Stadium", "group", "Group J"),
        Match(42, "2026-06-23", "Tuesday", 23, "June", "03:00", "FRA", "IRQ", "Philadelphia Stadium", "group", "Group I"),
        Match(43, "2026-06-23", "Tuesday", 23, "June", "06:00", "NOR", "SEN", "New York/New Jersey", "group", "Group I"),
        Match(44, "2026-06-23", "Tuesday", 23, "June", "09:00", "JOR", "ALG", "San Francisco Bay Area", "group", "Group J"),
        Match(45, "2026-06-23", "Tuesday", 23, "June", "23:00", "POR", "UZB", "Houston Stadium", "group", "Group K"),
        Match(46, "2026-06-24", "Wednesday", 24, "June", "02:00", "ENG", "GHA", "Boston Stadium", "group", "Group L"),
        Match(47, "2026-06-24", "Wednesday", 24, "June", "05:00", "PAN", "CRO", "Toronto Stadium", "group", "Group L"),
        Match(48, "2026-06-24", "Wednesday", 24, "June", "08:00", "COL", "COD", "Guadalajara Stadium", "group", "Group K"),
        Match(49, "2026-06-25", "Thursday", 25, "June", "01:00", "SUI", "CAN", "BC Place Vancouver", "group", "Group B"),
        Match(50, "2026-06-25", "Thursday", 25, "June", "01:00", "BIH", "QAT", "Seattle Stadium", "group", "Group B"),
        Match(51, "2026-06-25", "Thursday", 25, "June", "04:00", "SCO", "BRA", "Miami Stadium", "group", "Group C"),
        Match(52, "2026-06-25", "Thursday", 25, "June", "04:00", "MAR", "HAI", "Atlanta Stadium", "group", "Group C"),
        Match(53, "2026-06-25", "Thursday", 25, "June", "07:00", "CZE", "MEX", "Mexico City Stadium", "group", "Group A"),
        Match(54, "2026-06-25", "Thursday", 25, "June", "07:00", "RSA", "KOR", "Monterrey Stadium", "group", "Group A"),
        Match(55, "2026-06-26", "Friday", 26, "June", "02:00", "CUW", "CIV", "Philadelphia Stadium", "group", "Group E"),
        Match(56, "2026-06-26", "Friday", 26, "June", "02:00", "ECU", "GER", "New York/New Jersey", "group", "Group E"),
        Match(57, "2026-06-26", "Friday", 26, "June", "05:00", "JPN", "SWE", "Dallas Stadium", "group", "Group F"),
        Match(58, "2026-06-26", "Friday", 26, "June", "05:00", "TUN", "NED", "Kansas City Stadium", "group", "Group F"),
        Match(59, "2026-06-26", "Friday", 26, "June", "08:00", "TUR", "USA", "Los Angeles Stadium", "group", "Group D"),
        Match(60, "2026-06-26", "Friday", 26, "June", "08:00", "PAR", "AUS", "San Francisco Bay Area", "group", "Group D"),
        Match(61, "2026-06-27", "Saturday", 27, "June", "01:00", "NOR", "FRA", "Boston Stadium", "group", "Group I"),
        Match(62, "2026-06-27", "Saturday", 27, "June", "01:00", "SEN", "IRQ", "Toronto Stadium", "group", "Group I"),
        Match(63, "2026-06-27", "Saturday", 27, "June", "06:00", "CPV", "KSA", "Houston Stadium", "group", "Group H"),
        Match(64, "2026-06-27", "Saturday", 27, "June", "06:00", "URU", "ESP", "Guadalajara Stadium", "group", "Group H"),
        Match(65, "2026-06-27", "Saturday", 27, "June", "09:00", "EGY", "IRN", "Seattle Stadium", "group", "Group G"),
        Match(66, "2026-06-27", "Saturday", 27, "June", "09:00", "NZL", "BEL", "BC Place Vancouver", "group", "Group G"),
        Match(67, "2026-06-28", "Sunday", 28, "June", "03:00", "PAN", "ENG", "New York/New Jersey", "group", "Group L"),
        Match(68, "2026-06-28", "Sunday", 28, "June", "03:00", "CRO", "GHA", "Philadelphia Stadium", "group", "Group L"),
        Match(69, "2026-06-28", "Sunday", 28, "June", "05:30", "COL", "POR", "Miami Stadium", "group", "Group K"),
        Match(70, "2026-06-28", "Sunday", 28, "June", "05:30", "COD", "UZB", "Atlanta Stadium", "group", "Group K"),
        Match(71, "2026-06-28", "Sunday", 28, "June", "08:00", "ALG", "AUT", "Kansas City Stadium", "group", "Group J"),
        Match(72, "2026-06-28", "Sunday", 28, "June", "08:00", "JOR", "ARG", "Dallas Stadium", "group", "Group J"),

        // Round of 32 (16 matches)
        Match(73, "2026-06-29", "Monday", 29, "June", "01:00", "2A", "2B", "Los Angeles Stadium", "r32", "Round of 32"),
        Match(74, "2026-06-29", "Monday", 29, "June", "23:00", "1C", "2F", "Houston Stadium", "r32", "Round of 32"),
        Match(75, "2026-06-30", "Tuesday", 30, "June", "02:30", "1E", "3ABCDF", "Boston Stadium", "r32", "Round of 32"),
        Match(76, "2026-06-30", "Tuesday", 30, "June", "07:00", "1F", "2C", "Monterrey Stadium", "r32", "Round of 32"),
        Match(77, "2026-06-30", "Tuesday", 30, "June", "23:00", "2E", "2I", "Dallas Stadium", "r32", "Round of 32"),
        Match(78, "2026-07-01", "Wednesday", 1, "July", "03:00", "1I", "3CDFGH", "New York/New Jersey", "r32", "Round of 32"),
        Match(79, "2026-07-01", "Wednesday", 1, "July", "07:00", "1A", "3CEFHI", "Mexico City Stadium", "r32", "Round of 32"),
        Match(80, "2026-07-01", "Wednesday", 1, "July", "22:00", "1L", "3EHJK", "Atlanta Stadium", "r32", "Round of 32"),
        Match(81, "2026-07-02", "Thursday", 2, "July", "02:00", "1G", "3AEHIJ", "Seattle Stadium", "r32", "Round of 32"),
        Match(82, "2026-07-02", "Thursday", 2, "July", "06:00", "1D", "3BEFIJ", "San Francisco Bay Area", "r32", "Round of 32"),
        Match(83, "2026-07-02", "Thursday", 2, "July", "01:00", "1H", "2J", "Los Angeles Stadium", "r32", "Round of 32"),
        Match(84, "2026-07-02", "Thursday", 2, "July", "05:00", "2K", "2L", "Toronto Stadium", "r32", "Round of 32"),
        Match(85, "2026-07-02", "Thursday", 2, "July", "09:00", "1B", "3EFGIJ", "BC Place Vancouver", "r32", "Round of 32"),
        Match(86, "2026-07-04", "Saturday", 4, "July", "00:00", "2D", "2G", "Dallas Stadium", "r32", "Round of 32"),
        Match(87, "2026-07-04", "Saturday", 4, "July", "04:00", "1J", "2H", "Miami Stadium", "r32", "Round of 32"),
        Match(88, "2026-07-04", "Saturday", 4, "July", "07:30", "1K", "3DEJKL", "Kansas City Stadium", "r32", "Round of 32"),

        // Round of 16 (8 matches)
        Match(89, "2026-07-04", "Saturday", 4, "July", "23:00", "W73", "W75", "Houston Stadium", "r16", "Round of 16"),
        Match(90, "2026-07-05", "Sunday", 5, "July", "03:00", "W74", "W77", "Philadelphia Stadium", "r16", "Round of 16"),
        Match(91, "2026-07-06", "Monday", 6, "July", "02:00", "W76", "W78", "New York/New Jersey", "r16", "Round of 16"),
        Match(92, "2026-07-06", "Monday", 6, "July", "06:00", "W79", "W80", "Mexico City Stadium", "r16", "Round of 16"),
        Match(93, "2026-07-07", "Tuesday", 7, "July", "01:00", "W83", "W84", "Dallas Stadium", "r16", "Round of 16"),
        Match(94, "2026-07-07", "Tuesday", 7, "July", "06:00", "W81", "W82", "Seattle Stadium", "r16", "Round of 16"),
        Match(95, "2026-07-07", "Tuesday", 7, "July", "22:00", "W86", "W88", "Atlanta Stadium", "r16", "Round of 16"),
        Match(96, "2026-07-08", "Wednesday", 8, "July", "02:00", "W85", "W87", "BC Place Vancouver", "r16", "Round of 16"),

        // Quarter-finals (4 matches)
        Match(97, "2026-07-10", "Friday", 10, "July", "02:00", "W89", "W90", "Boston Stadium", "qf", "Quarter-final"),
        Match(98, "2026-07-11", "Saturday", 11, "July", "01:00", "W93", "W94", "Los Angeles Stadium", "qf", "Quarter-final"),
        Match(99, "2026-07-12", "Sunday", 12, "July", "03:00", "W91", "W92", "Miami Stadium", "qf", "Quarter-final"),
        Match(100, "2026-07-12", "Sunday", 12, "July", "07:00", "W95", "W96", "Kansas City Stadium", "qf", "Quarter-final"),

        // Semi-finals (2 matches)
        Match(101, "2026-07-15", "Wednesday", 15, "July", "01:00", "W97", "W98", "Dallas Stadium", "sf", "Semi-final"),
        Match(102, "2026-07-16", "Thursday", 16, "July", "01:00", "W99", "W100", "Atlanta Stadium", "sf", "Semi-final"),

        // 3rd Place & Final (2 matches)
        Match(103, "2026-07-19", "Sunday", 19, "July", "03:00", "RUI01", "RUI02", "Miami Stadium", "3rd", "3rd Place"),
        Match(104, "2026-07-20", "Monday", 20, "July", "01:00", "W101", "W102", "New York/New Jersey Stadium", "final", "🏆 FINAL")
    )
}
