package com.example

import android.app.DatePickerDialog
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.BorderStroke
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.AndroidViewModel
import androidx.room.*
import com.example.ui.theme.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.text.TextStyle
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import java.text.SimpleDateFormat
import java.util.*

// ==========================================
// DATA MODELS
// ==========================================
data class Match(
    val date: String,      // YYYY-MM-DD
    val day: String,       // e.g. "Friday"
    val d: Int,            // Day of month
    val m: String,         // e.g. "June"
    val time: String,      // HH:mm (24h)
    val t1: String,        // Team 1 Code or TBD string
    val t2: String,        // Team 2 Code or TBD string
    val venue: String,     // Venue name
    val stage: String,     // group, r32, r16, qf, sf, 3rd, final
    val group: String,     // Group name or stage label
    val matchNumber: Int   // Unique match index (1-104)
)

// ==========================================
// METADATA & CONSTANTS
// ==========================================
val TEAM_FLAGS = mapOf(
    "MEX" to "🇲🇽", "RSA" to "🇿🇦", "KOR" to "🇰🇷", "CZE" to "🇨🇿", "CAN" to "🇨🇦", "BIH" to "🇧🇦",
    "USA" to "🇺🇸", "PAR" to "🇵🇾", "QAT" to "🇶🇦", "SUI" to "🇨🇭", "BRA" to "🇧🇷", "MAR" to "🇲🇦",
    "HAI" to "🇭🇹", "SCO" to "🏴󠁧󠁢󠁳󠁣󠁴󠁿",
    "AUS" to "🇦🇺", "TUR" to "🇹🇷", "GER" to "🇩🇪", "CUW" to "🇨🇼", "NED" to "🇳🇱", "JPN" to "🇯🇵",
    "CIV" to "🇨🇮", "ECU" to "🇪🇨", "SWE" to "🇸🇪", "TUN" to "🇹🇳", "ESP" to "🇪🇸", "CPV" to "🇨🇻",
    "BEL" to "🇧🇪", "EGY" to "🇪🇬", "KSA" to "🇸🇦", "URU" to "🇺🇾", "IRN" to "🇮🇷", "NZL" to "🇳🇿",
    "FRA" to "🇫🇷", "SEN" to "🇸🇳", "IRQ" to "🇮🇶", "NOR" to "🇳🇴", "ARG" to "🇦🇷", "ALG" to "🇩🇿",
    "AUT" to "🇦🇹", "JOR" to "🇯🇴", "POR" to "🇵🇹", "COD" to "🇨🇩", "ENG" to "🏴󠁧󠁢󠁥󠁮󠁧󠁿", "CRO" to "🇭🇷",
    "GHA" to "🇬🇭", "PAN" to "🇵🇦", "UZB" to "🇺🇿", "COL" to "🇨🇴"
)

// Standard cleaner flag getter to ensure we don't have escape sequences causing build errors
fun getTeamFlag(code: String): String {
    if (code == "SCO") return "🏴󠁧󠁢󠁳󠁣󠁴󠁿"
    return TEAM_FLAGS[code] ?: ""
}

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

// ==========================================
// STAGE INFO & STYLING HELPER
// ==========================================
data class StageStyle(
    val label: String,
    val icon: String,
    val primaryColor: Color,
    val cardGradientLight: List<Color>,
    val cardGradientDark: List<Color>
)

val STAGE_STYLES = mapOf(
    "group" to StageStyle(
        label = "Group Stage",
        icon = "⚽",
        primaryColor = Color(0xFFC9A84C),
        cardGradientLight = listOf(Color(0xFFFFFFFF), Color(0xFFFBFBF9)),
        cardGradientDark = listOf(Color(0xFF1E1E2F), Color(0xFF151522))
    ),
    "r32" to StageStyle(
        label = "Round of 32",
        icon = "⚔️",
        primaryColor = Color(0xFF1D6FB8),
        cardGradientLight = listOf(Color(0xFFEFF6FF), Color(0xFFFFFFFF)),
        cardGradientDark = listOf(Color(0xFF0F2C59), Color(0xFF121225))
    ),
    "r16" to StageStyle(
        label = "Round of 16",
        icon = "💫",
        primaryColor = Color(0xFF7C3AED),
        cardGradientLight = listOf(Color(0xFFF5F3FF), Color(0xFFFFFFFF)),
        cardGradientDark = listOf(Color(0xFF3B1D5F), Color(0xFF121225))
    ),
    "qf" to StageStyle(
        label = "Quarter-finals",
        icon = "🔥",
        primaryColor = Color(0xFFD97706),
        cardGradientLight = listOf(Color(0xFFFFFBEB), Color(0xFFFFFFFF)),
        cardGradientDark = listOf(Color(0xFF5A3006), Color(0xFF121225))
    ),
    "sf" to StageStyle(
        label = "Semi-finals",
        icon = "⭐",
        primaryColor = Color(0xFFDC2626),
        cardGradientLight = listOf(Color(0xFFFEF2F2), Color(0xFFFFFFFF)),
        cardGradientDark = listOf(Color(0xFF5E0C0C), Color(0xFF121225))
    ),
    "3rd" to StageStyle(
        label = "3rd Place Match",
        icon = "🥉",
        primaryColor = Color(0xFF6B7280),
        cardGradientLight = listOf(Color(0xFFF3F4F6), Color(0xFFFFFFFF)),
        cardGradientDark = listOf(Color(0xFF2D3748), Color(0xFF121225))
    ),
    "final" to StageStyle(
        label = "🏆 FINAL",
        icon = "🏆",
        primaryColor = Color(0xFFB45309),
        cardGradientLight = listOf(Color(0xFFFFFDF0), Color(0xFFFEF9E7)),
        cardGradientDark = listOf(Color(0xFF5E490D), Color(0xFF282006))
    )
)

// ==========================================
// TOURNAMENT MATCHES DATA (104 MATCHES)
// ==========================================
val TOURNAMENT_MATCHES = listOf(
    // Group Stage
    Match("2026-06-12", "Friday", 12, "June", "01:00", "MEX", "RSA", "Mexico City Stadium", "group", "Group A", 1),
    Match("2026-06-12", "Friday", 12, "June", "08:00", "KOR", "CZE", "Guadalajara Stadium", "group", "Group A", 2),
    Match("2026-06-13", "Saturday", 13, "June", "01:00", "CAN", "BIH", "Toronto Stadium", "group", "Group B", 3),
    Match("2026-06-13", "Saturday", 13, "June", "07:00", "USA", "PAR", "Los Angeles Stadium", "group", "Group D", 4),
    Match("2026-06-14", "Sunday", 14, "June", "01:00", "QAT", "SUI", "San Francisco Bay Area", "group", "Group B", 5),
    Match("2026-06-14", "Sunday", 14, "June", "04:00", "BRA", "MAR", "New York/New Jersey", "group", "Group C", 6),
    Match("2026-06-14", "Sunday", 14, "June", "07:00", "HAI", "SCO", "Boston Stadium", "group", "Group C", 7),
    Match("2026-06-14", "Sunday", 14, "June", "10:00", "AUS", "TUR", "BC Place Vancouver", "group", "Group D", 8),
    Match("2026-06-14", "Sunday", 14, "June", "23:00", "GER", "CUW", "Houston Stadium", "group", "Group E", 9),
    Match("2026-06-15", "Monday", 15, "June", "02:00", "NED", "JPN", "Dallas Stadium", "group", "Group F", 10),
    Match("2026-06-15", "Monday", 15, "June", "05:00", "CIV", "ECU", "Philadelphia Stadium", "group", "Group E", 11),
    Match("2026-06-15", "Monday", 15, "June", "08:00", "SWE", "TUN", "Monterrey Stadium", "group", "Group F", 12),
    Match("2026-06-15", "Monday", 15, "June", "22:00", "ESP", "CPV", "Atlanta Stadium", "group", "Group H", 13),
    Match("2026-06-16", "Tuesday", 16, "June", "01:00", "BEL", "EGY", "Seattle Stadium", "group", "Group G", 14),
    Match("2026-06-16", "Tuesday", 16, "June", "04:00", "KSA", "URU", "Miami Stadium", "group", "Group H", 15),
    Match("2026-06-16", "Tuesday", 16, "June", "07:00", "IRN", "NZL", "Los Angeles Stadium", "group", "Group G", 16),
    Match("2026-06-17", "Wednesday", 17, "June", "01:00", "FRA", "SEN", "New York/New Jersey", "group", "Group I", 17),
    Match("2026-06-17", "Wednesday", 17, "June", "04:00", "IRQ", "NOR", "Boston Stadium", "group", "Group I", 18),
    Match("2026-06-17", "Wednesday", 17, "June", "07:00", "ARG", "ALG", "Kansas City Stadium", "group", "Group J", 19),
    Match("2026-06-17", "Wednesday", 17, "June", "10:00", "AUT", "JOR", "San Francisco Bay Area", "group", "Group J", 20),
    Match("2026-06-17", "Wednesday", 17, "June", "23:00", "POR", "COD", "Houston Stadium", "group", "Group K", 21),
    Match("2026-06-18", "Thursday", 18, "June", "02:00", "ENG", "CRO", "Dallas Stadium", "group", "Group L", 22),
    Match("2026-06-18", "Thursday", 18, "June", "05:00", "GHA", "PAN", "Toronto Stadium", "group", "Group L", 23),
    Match("2026-06-18", "Thursday", 18, "June", "08:00", "UZB", "COL", "Mexico City Stadium", "group", "Group K", 24),
    Match("2026-06-18", "Thursday", 18, "June", "22:00", "CZE", "RSA", "Atlanta Stadium", "group", "Group A", 25),
    Match("2026-06-19", "Friday", 19, "June", "01:00", "SUI", "BIH", "Los Angeles Stadium", "group", "Group B", 26),
    Match("2026-06-19", "Friday", 19, "June", "04:00", "CAN", "QAT", "BC Place Vancouver", "group", "Group B", 27),
    Match("2026-06-19", "Friday", 19, "June", "07:00", "MEX", "KOR", "Guadalajara Stadium", "group", "Group A", 28),
    Match("2026-06-20", "Saturday", 20, "June", "01:00", "USA", "AUS", "Seattle Stadium", "group", "Group D", 29),
    Match("2026-06-20", "Saturday", 20, "June", "04:00", "SCO", "MAR", "Boston Stadium", "group", "Group C", 30),
    Match("2026-06-20", "Saturday", 20, "June", "06:30", "BRA", "HAI", "Philadelphia Stadium", "group", "Group C", 31),
    Match("2026-06-20", "Saturday", 20, "June", "09:00", "TUR", "PAR", "San Francisco Bay Area", "group", "Group D", 32),
    Match("2026-06-20", "Saturday", 20, "June", "23:00", "NED", "SWE", "Houston Stadium", "group", "Group F", 33),
    Match("2026-06-21", "Sunday", 21, "June", "02:00", "GER", "CIV", "Toronto Stadium", "group", "Group E", 34),
    Match("2026-06-21", "Sunday", 21, "June", "06:00", "ECU", "CUW", "Kansas City Stadium", "group", "Group E", 35),
    Match("2026-06-21", "Sunday", 21, "June", "10:00", "TUN", "JPN", "Monterrey Stadium", "group", "Group F", 36),
    Match("2026-06-21", "Sunday", 21, "June", "22:00", "ESP", "KSA", "Atlanta Stadium", "group", "Group H", 37),
    Match("2026-06-22", "Monday", 22, "June", "01:00", "BEL", "IRN", "Los Angeles Stadium", "group", "Group G", 38),
    Match("2026-06-22", "Monday", 22, "June", "04:00", "URU", "CPV", "Miami Stadium", "group", "Group H", 39),
    Match("2026-06-22", "Monday", 22, "June", "07:00", "NZL", "EGY", "BC Place Vancouver", "group", "Group G", 40),
    Match("2026-06-22", "Monday", 22, "June", "23:00", "ARG", "AUT", "Dallas Stadium", "group", "Group J", 41),
    Match("2026-06-23", "Tuesday", 23, "June", "03:00", "FRA", "IRQ", "Philadelphia Stadium", "group", "Group I", 42),
    Match("2026-06-23", "Tuesday", 23, "June", "06:00", "NOR", "SEN", "New York/New Jersey", "group", "Group I", 43),
    Match("2026-06-23", "Tuesday", 23, "June", "09:00", "JOR", "ALG", "San Francisco Bay Area", "group", "Group J", 44),
    Match("2026-06-23", "Tuesday", 23, "June", "23:00", "POR", "UZB", "Houston Stadium", "group", "Group K", 45),
    Match("2026-06-24", "Wednesday", 24, "June", "02:00", "ENG", "GHA", "Boston Stadium", "group", "Group L", 46),
    Match("2026-06-24", "Wednesday", 24, "June", "05:00", "PAN", "CRO", "Toronto Stadium", "group", "Group L", 47),
    Match("2026-06-24", "Wednesday", 24, "June", "08:00", "COL", "COD", "Guadalajara Stadium", "group", "Group K", 48),
    Match("2026-06-25", "Thursday", 25, "June", "01:00", "SUI", "CAN", "BC Place Vancouver", "group", "Group B", 49),
    Match("2026-06-25", "Thursday", 25, "June", "01:00", "BIH", "QAT", "Seattle Stadium", "group", "Group B", 50),
    Match("2026-06-25", "Thursday", 25, "June", "04:00", "SCO", "BRA", "Miami Stadium", "group", "Group C", 51),
    Match("2026-06-25", "Thursday", 25, "June", "04:00", "MAR", "HAI", "Atlanta Stadium", "group", "Group C", 52),
    Match("2026-06-25", "Thursday", 25, "June", "07:00", "CZE", "MEX", "Mexico City Stadium", "group", "Group A", 53),
    Match("2026-06-25", "Thursday", 25, "June", "07:00", "RSA", "KOR", "Monterrey Stadium", "group", "Group A", 54),
    Match("2026-06-26", "Friday", 26, "June", "02:00", "CUW", "CIV", "Philadelphia Stadium", "group", "Group E", 55),
    Match("2026-06-26", "Friday", 26, "June", "02:00", "ECU", "GER", "New York/New Jersey", "group", "Group E", 56),
    Match("2026-06-26", "Friday", 26, "June", "05:00", "JPN", "SWE", "Dallas Stadium", "group", "Group F", 57),
    Match("2026-06-26", "Friday", 26, "June", "05:00", "TUN", "NED", "Kansas City Stadium", "group", "Group F", 58),
    Match("2026-06-26", "Friday", 26, "June", "08:00", "TUR", "USA", "Los Angeles Stadium", "group", "Group D", 59),
    Match("2026-06-26", "Friday", 26, "June", "08:00", "PAR", "AUS", "San Francisco Bay Area", "group", "Group D", 60),
    Match("2026-06-27", "Saturday", 27, "June", "01:00", "NOR", "FRA", "Boston Stadium", "group", "Group I", 61),
    Match("2026-06-27", "Saturday", 27, "June", "01:00", "SEN", "IRQ", "Toronto Stadium", "group", "Group I", 62),
    Match("2026-06-27", "Saturday", 27, "June", "06:00", "CPV", "KSA", "Houston Stadium", "group", "Group H", 63),
    Match("2026-06-27", "Saturday", 27, "June", "06:00", "URU", "ESP", "Guadalajara Stadium", "group", "Group H", 64),
    Match("2026-06-27", "Saturday", 27, "June", "09:00", "EGY", "IRN", "Seattle Stadium", "group", "Group G", 65),
    Match("2026-06-27", "Saturday", 27, "June", "09:00", "NZL", "BEL", "BC Place Vancouver", "group", "Group G", 66),
    Match("2026-06-28", "Sunday", 28, "June", "03:00", "PAN", "ENG", "New York/New Jersey", "group", "Group L", 67),
    Match("2026-06-28", "Sunday", 28, "June", "03:00", "CRO", "GHA", "Philadelphia Stadium", "group", "Group L", 68),
    Match("2026-06-28", "Sunday", 28, "June", "05:30", "COL", "POR", "Miami Stadium", "group", "Group K", 69),
    Match("2026-06-28", "Sunday", 28, "June", "05:30", "COD", "UZB", "Atlanta Stadium", "group", "Group K", 70),
    Match("2026-06-28", "Sunday", 28, "June", "08:00", "ALG", "AUT", "Kansas City Stadium", "group", "Group J", 71),
    Match("2026-06-28", "Sunday", 28, "June", "08:00", "JOR", "ARG", "Dallas Stadium", "group", "Group J", 72),

    // Round of 32
    Match("2026-06-29", "Monday", 29, "June", "01:00", "2A", "2B", "Los Angeles Stadium", "r32", "Round of 32", 73),
    Match("2026-06-29", "Monday", 29, "June", "23:00", "1C", "2F", "Houston Stadium", "r32", "Round of 32", 74),
    Match("2026-06-30", "Tuesday", 30, "June", "02:30", "1E", "3ABCDF", "Boston Stadium", "r32", "Round of 32", 75),
    Match("2026-06-30", "Tuesday", 30, "June", "07:00", "1F", "2C", "Monterrey Stadium", "r32", "Round of 32", 76),
    Match("2026-06-30", "Tuesday", 30, "June", "23:00", "2E", "2I", "Dallas Stadium", "r32", "Round of 32", 77),
    Match("2026-07-01", "Wednesday", 1, "July", "03:00", "1I", "3CDFGH", "New York/New Jersey", "r32", "Round of 32", 78),
    Match("2026-07-01", "Wednesday", 1, "July", "07:00", "1A", "3CEFHI", "Mexico City Stadium", "r32", "Round of 32", 79),
    Match("2026-07-01", "Wednesday", 1, "July", "22:00", "1L", "3EHJK", "Atlanta Stadium", "r32", "Round of 32", 80),
    Match("2026-07-02", "Thursday", 2, "July", "02:00", "1G", "3AEHIJ", "Seattle Stadium", "r32", "Round of 32", 81),
    Match("2026-07-02", "Thursday", 2, "July", "06:00", "1D", "3BEFIJ", "San Francisco Bay Area", "r32", "Round of 32", 82),
    Match("2026-07-02", "Thursday", 2, "July", "01:00", "1H", "2J", "Los Angeles Stadium", "r32", "Round of 32", 83),
    Match("2026-07-02", "Thursday", 2, "July", "05:00", "2K", "2L", "Toronto Stadium", "r32", "Round of 32", 84),
    Match("2026-07-02", "Thursday", 2, "July", "09:00", "1B", "3EFGIJ", "BC Place Vancouver", "r32", "Round of 32", 85),
    Match("2026-07-04", "Saturday", 4, "July", "00:00", "2D", "2G", "Dallas Stadium", "r32", "Round of 32", 86),
    Match("2026-07-04", "Saturday", 4, "July", "04:00", "1J", "2H", "Miami Stadium", "r32", "Round of 32", 87),
    Match("2026-07-04", "Saturday", 4, "July", "07:30", "1K", "3DEJKL", "Kansas City Stadium", "r32", "Round of 32", 88),

    // Round of 16
    Match("2026-07-04", "Saturday", 4, "July", "23:00", "W73", "W75", "Houston Stadium", "r16", "Round of 16", 89),
    Match("2026-07-05", "Sunday", 5, "July", "03:00", "W74", "W77", "Philadelphia Stadium", "r16", "Round of 16", 90),
    Match("2026-07-06", "Monday", 6, "July", "02:00", "W76", "W78", "New York/New Jersey", "r16", "Round of 16", 91),
    Match("2026-07-06", "Monday", 6, "July", "06:00", "W79", "W80", "Mexico City Stadium", "r16", "Round of 16", 92),
    Match("2026-07-07", "Tuesday", 7, "July", "01:00", "W83", "W84", "Dallas Stadium", "r16", "Round of 16", 93),
    Match("2026-07-07", "Tuesday", 7, "July", "06:00", "W81", "W82", "Seattle Stadium", "r16", "Round of 16", 94),
    Match("2026-07-07", "Tuesday", 7, "July", "22:00", "W86", "W88", "Atlanta Stadium", "r16", "Round of 16", 95),
    Match("2026-07-08", "Wednesday", 8, "July", "02:00", "W85", "W87", "BC Place Vancouver", "r16", "Round of 16", 96),

    // Quarter-finals
    Match("2026-07-10", "Friday", 10, "July", "02:00", "W89", "W90", "Boston Stadium", "qf", "Quarter-final", 97),
    Match("2026-07-11", "Saturday", 11, "July", "01:00", "W93", "W94", "Los Angeles Stadium", "qf", "Quarter-final", 98),
    Match("2026-07-12", "Sunday", 12, "July", "03:00", "W91", "W92", "Miami Stadium", "qf", "Quarter-final", 99),
    Match("2026-07-12", "Sunday", 12, "July", "07:00", "W95", "W96", "Kansas City Stadium", "qf", "Quarter-final", 100),

    // Semi-finals
    Match("2026-07-15", "Wednesday", 15, "July", "01:00", "W97", "W98", "Dallas Stadium", "sf", "Semi-final", 101),
    Match("2026-07-16", "Thursday", 16, "July", "01:00", "W99", "W100", "Atlanta Stadium", "sf", "Semi-final", 102),

    // 3rd Place Match & Final
    Match("2026-07-19", "Sunday", 19, "July", "03:00", "RUI01", "RUI02", "Miami Stadium", "3rd", "3rd Place Match", 103),
    Match("2026-07-20", "Monday", 20, "July", "01:00", "W101", "W102", "New York/New Jersey Stadium", "final", "🏆 FINAL", 104)
)

// ==========================================
// DATABASE & PERSISTENCE & POINT TABLES LOGIC
// ==========================================
@Entity(tableName = "match_results")
data class MatchResultEntity(
    @PrimaryKey val matchNumber: Int,
    val team1Score: Int?,
    val team2Score: Int?,
    val winnerTeamCode: String? = null
)

@Dao
interface MatchResultDao {
    @Query("SELECT * FROM match_results")
    fun getAllResultsFlow(): Flow<List<MatchResultEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertResult(result: MatchResultEntity)

    @Query("DELETE FROM match_results WHERE matchNumber = :matchNumber")
    suspend fun deleteResult(matchNumber: Int)

    @Query("DELETE FROM match_results")
    suspend fun clearAll()
}

@Database(entities = [MatchResultEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun matchResultDao(): MatchResultDao
}

object DatabaseProvider {
    private var db: AppDatabase? = null
    fun getDatabase(context: android.content.Context): AppDatabase {
        return db ?: synchronized(this) {
            val instance = Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "world_cup_database_v3"
            ).fallbackToDestructiveMigration().build()
            db = instance
            instance
        }
    }
}

data class TeamStanding(
    val teamCode: String,
    val played: Int,
    val won: Int,
    val drawn: Int,
    val lost: Int,
    val goalsFor: Int,
    val goalsAgainst: Int,
    val goalDifference: Int,
    val points: Int
)

data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)

val GROUP_TEAMS = mapOf(
    "A" to listOf("MEX", "RSA", "KOR", "CZE"),
    "B" to listOf("CAN", "BIH", "QAT", "SUI"),
    "C" to listOf("BRA", "MAR", "HAI", "SCO"),
    "D" to listOf("USA", "PAR", "AUS", "TUR"),
    "E" to listOf("GER", "CUW", "CIV", "ECU"),
    "F" to listOf("NED", "JPN", "SWE", "TUN"),
    "G" to listOf("BEL", "EGY", "IRN", "NZL"),
    "H" to listOf("ESP", "CPV", "KSA", "URU"),
    "I" to listOf("FRA", "SEN", "IRQ", "NOR"),
    "J" to listOf("ARG", "ALG", "AUT", "JOR"),
    "K" to listOf("POR", "COD", "UZB", "COL"),
    "L" to listOf("ENG", "CRO", "GHA", "PAN")
)

fun computeGroupStandings(
    groupLetter: String,
    results: Map<Int, MatchResultEntity>
): List<TeamStanding> {
    val teams = GROUP_TEAMS[groupLetter] ?: emptyList()
    val standings = teams.associateWith {
        TeamStanding(
            teamCode = it,
            played = 0, won = 0, drawn = 0, lost = 0,
            goalsFor = 0, goalsAgainst = 0, goalDifference = 0, points = 0
        )
    }.toMutableMap()

    val groupMatches = TOURNAMENT_MATCHES.filter { it.group == "Group $groupLetter" }
    for (match in groupMatches) {
        val result = results[match.matchNumber]
        if (result != null && result.team1Score != null && result.team2Score != null) {
            val s1 = result.team1Score
            val s2 = result.team2Score

            val t1 = match.t1
            val t2 = match.t2

            val st1 = standings[t1]
            val st2 = standings[t2]

            if (st1 != null && st2 != null) {
                val p1 = st1.played + 1
                val p2 = st2.played + 1

                val gf1 = st1.goalsFor + s1
                val ga1 = st1.goalsAgainst + s2
                val gd1 = gf1 - ga1

                val gf2 = st2.goalsFor + s2
                val ga2 = st2.goalsAgainst + s1
                val gd2 = gf2 - ga2

                val (w1, d1, l1, pts1) = when {
                    s1 > s2 -> Quadruple(st1.won + 1, st1.drawn, st1.lost, st1.points + 3)
                    s1 < s2 -> Quadruple(st1.won, st1.drawn, st1.lost + 1, st1.points)
                    else -> Quadruple(st1.won, st1.drawn + 1, st1.lost, st1.points + 1)
                }

                val (w2, d2, l2, pts2) = when {
                    s2 > s1 -> Quadruple(st2.won + 1, st2.drawn, st2.lost, st2.points + 3)
                    s2 < s1 -> Quadruple(st2.won, st2.drawn, st2.lost + 1, st2.points)
                    else -> Quadruple(st2.won, st2.drawn + 1, st2.lost, st2.points + 1)
                }

                standings[t1] = st1.copy(played = p1, won = w1, drawn = d1, lost = l1, goalsFor = gf1, goalsAgainst = ga1, goalDifference = gd1, points = pts1)
                standings[t2] = st2.copy(played = p2, won = w2, drawn = d2, lost = l2, goalsFor = gf2, goalsAgainst = ga2, goalDifference = gd2, points = pts2)
            }
        }
    }

    return standings.values.sortedWith(
        compareByDescending<TeamStanding> { it.points }
            .thenByDescending { it.goalDifference }
            .thenByDescending { it.goalsFor }
            .thenBy { it.teamCode }
    )
}

fun getBestThirdPlacedTeams(
    results: Map<Int, MatchResultEntity>
): List<TeamStanding> {
    val thirds = mutableListOf<TeamStanding>()
    GROUP_TEAMS.keys.forEach { groupLetter ->
        val standings = computeGroupStandings(groupLetter, results)
        if (standings.size >= 3) {
            thirds.add(standings[2])
        }
    }
    return thirds.sortedWith(
        compareByDescending<TeamStanding> { it.points }
            .thenByDescending { it.goalDifference }
            .thenByDescending { it.goalsFor }
            .thenBy { it.teamCode }
    )
}

fun getGroupForTeam(teamCode: String): String {
    for ((groupLetter, teams) in GROUP_TEAMS) {
        if (teamCode in teams) return groupLetter
    }
    return ""
}

fun resolveThirdPlacedSlots(
    results: Map<Int, MatchResultEntity>
): Map<String, String> {
    val topThirds = getBestThirdPlacedTeams(results).take(8).toMutableList()
    val slotMappings = mutableMapOf<String, String>()

    val slots = listOf(
        "3ABCDF" to listOf("A", "B", "C", "D", "F"),
        "3CDFGH" to listOf("C", "D", "F", "G", "H"),
        "3CEFHI" to listOf("C", "E", "F", "H", "I"),
        "3EHJK" to listOf("E", "H", "J", "K"),
        "3AEHIJ" to listOf("A", "E", "H", "I", "J"),
        "3BEFIJ" to listOf("B", "E", "F", "I", "J"),
        "3EFGIJ" to listOf("E", "F", "G", "I", "J"),
        "3DEJKL" to listOf("D", "E", "J", "K", "L")
    )

    for ((slotCode, allowedGroups) in slots) {
        val matchedTeamIndex = topThirds.indexOfFirst { team ->
            val teamGroup = getGroupForTeam(team.teamCode)
            teamGroup in allowedGroups
        }
        if (matchedTeamIndex != -1) {
            val matchedTeam = topThirds.removeAt(matchedTeamIndex)
            slotMappings[slotCode] = matchedTeam.teamCode
        } else if (topThirds.isNotEmpty()) {
            val matchedTeam = topThirds.removeAt(0)
            slotMappings[slotCode] = matchedTeam.teamCode
        }
    }
    return slotMappings
}

fun resolveTeamCode(
    code: String,
    results: Map<Int, MatchResultEntity>,
    thirdPlacedMappings: Map<String, String>,
    shouldResolveKnockouts: Boolean = true
): String {
    if (TEAM_NAMES.containsKey(code)) {
        return code
    }

    if (!shouldResolveKnockouts) {
        return code
    }

    if (code.length == 2 && code[0].isDigit() && code[1].isLetter()) {
        val position = code[0].toString().toInt()
        val groupLetter = code[1].toString().uppercase()
        val standings = computeGroupStandings(groupLetter, results)
        return if (standings.size >= position) {
            standings[position - 1].teamCode
        } else {
            code
        }
    }

    if (thirdPlacedMappings.containsKey(code)) {
        return thirdPlacedMappings[code]!!
    }

    if (code.startsWith("W") && code.substring(1).all { it.isDigit() }) {
        val matchNum = code.substring(1).toInt()
        val matchResult = results[matchNum]
        if (matchResult != null) {
            if (matchResult.winnerTeamCode != null) {
                return matchResult.winnerTeamCode
            }
            val s1 = matchResult.team1Score
            val s2 = matchResult.team2Score
            if (s1 != null && s2 != null) {
                val match = TOURNAMENT_MATCHES.find { it.matchNumber == matchNum }
                if (match != null) {
                    val t1Resolved = resolveTeamCode(match.t1, results, thirdPlacedMappings, shouldResolveKnockouts)
                    val t2Resolved = resolveTeamCode(match.t2, results, thirdPlacedMappings, shouldResolveKnockouts)
                    return when {
                        s1 > s2 -> t1Resolved
                        s1 < s2 -> t2Resolved
                        else -> t1Resolved
                    }
                }
            }
        }
        return code
    }

    if (code == "RUI01" || code == "RUI02") {
        val sfMatchNum = if (code == "RUI01") 101 else 102
        val matchResult = results[sfMatchNum]
        if (matchResult != null) {
            val s1 = matchResult.team1Score
            val s2 = matchResult.team2Score
            if (s1 != null && s2 != null) {
                val match = TOURNAMENT_MATCHES.find { it.matchNumber == sfMatchNum }
                if (match != null) {
                    val t1Resolved = resolveTeamCode(match.t1, results, thirdPlacedMappings, shouldResolveKnockouts)
                    val t2Resolved = resolveTeamCode(match.t2, results, thirdPlacedMappings, shouldResolveKnockouts)
                    return when {
                        s1 > s2 -> t2Resolved
                        s1 < s2 -> t1Resolved
                        else -> t2Resolved
                    }
                }
            }
        }
        return code
    }

    return code
}

// ==========================================
// VIEWMODEL FOR STATE MANAGEMENT
// ==========================================
class ScheduleViewModel(application: android.app.Application) : AndroidViewModel(application) {
    private val db = DatabaseProvider.getDatabase(application)
    private val dao = db.matchResultDao()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedDate = MutableStateFlow<String?>(null)
    val selectedDate: StateFlow<String?> = _selectedDate.asStateFlow()

    val matchResultsMap: StateFlow<Map<Int, MatchResultEntity>> = dao.getAllResultsFlow()
        .map { list -> list.associateBy { it.matchNumber } }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyMap()
        )

    val computedGroupStandings: StateFlow<Map<String, List<TeamStanding>>> = matchResultsMap.map { resultsMap ->
        GROUP_TEAMS.keys.associateWith { groupLetter ->
            computeGroupStandings(groupLetter, resultsMap)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = GROUP_TEAMS.keys.associateWith { emptyList() }
    )

    val allGroupStageMatchesCompleted: StateFlow<Boolean> = matchResultsMap.map { resultsMap ->
        (1..72).all { matchNum ->
            val result = resultsMap[matchNum]
            result?.team1Score != null && result?.team2Score != null
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = false
    )

    private val _manualBracketUpdate = MutableStateFlow(false)
    val manualBracketUpdate: StateFlow<Boolean> = _manualBracketUpdate.asStateFlow()

    fun triggerManualBracketUpdate() {
        _manualBracketUpdate.value = !_manualBracketUpdate.value
    }

    val resolvedMatches: StateFlow<List<Match>> = combine(
        matchResultsMap,
        _searchQuery,
        _selectedDate,
        allGroupStageMatchesCompleted,
        manualBracketUpdate
    ) { resultsMap, query, date, groupCompleted, manualUpdate ->
        val shouldResolve = groupCompleted || manualUpdate
        val thirdPlacedMappings = if (shouldResolve) resolveThirdPlacedSlots(resultsMap) else emptyMap()

        val resolvedList = TOURNAMENT_MATCHES.map { match ->
            val resolvedT1 = resolveTeamCode(match.t1, resultsMap, thirdPlacedMappings, shouldResolve)
            val resolvedT2 = resolveTeamCode(match.t2, resultsMap, thirdPlacedMappings, shouldResolve)
            match.copy(
                t1 = resolvedT1,
                t2 = resolvedT2
            )
        }

        resolvedList.filter { match ->
            val matchesDate = date == null || match.date == date
            val matchesQuery = if (query.isBlank()) {
                true
            } else {
                val q = query.lowercase().trim()
                val t1Name = TEAM_NAMES[match.t1]?.lowercase() ?: ""
                val t2Name = TEAM_NAMES[match.t2]?.lowercase() ?: ""
                match.t1.lowercase().contains(q) ||
                        match.t2.lowercase().contains(q) ||
                        t1Name.contains(q) ||
                        t2Name.contains(q)
            }
            matchesDate && matchesQuery
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = TOURNAMENT_MATCHES
    )

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun selectDate(date: String?) {
        _selectedDate.value = date
    }

    fun clearFilters() {
        _searchQuery.value = ""
        _selectedDate.value = null
    }

    fun saveMatchResult(matchNumber: Int, t1Score: Int, t2Score: Int, winnerTeamCode: String?) {
        viewModelScope.launch {
            dao.insertResult(MatchResultEntity(matchNumber, t1Score, t2Score, winnerTeamCode))
        }
    }

    fun deleteMatchResult(matchNumber: Int) {
        viewModelScope.launch {
            dao.deleteResult(matchNumber)
        }
    }
}

// ==========================================
// ACTIVITY
// ==========================================
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    ScheduleApp(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

// ==========================================
// COMPOSABLE COMPONENT IMPLEMENTATIONS
// ==========================================

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ScheduleApp(
    modifier: Modifier = Modifier,
    viewModel: ScheduleViewModel = viewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedDate by viewModel.selectedDate.collectAsState()
    val filteredMatches by viewModel.resolvedMatches.collectAsState()
    val resultsMap by viewModel.matchResultsMap.collectAsState()
    val standingsMap by viewModel.computedGroupStandings.collectAsState()
    val allGroupStageCompleted by viewModel.allGroupStageMatchesCompleted.collectAsState()
    val manualBracketUpdated by viewModel.manualBracketUpdate.collectAsState()

    val listState = rememberLazyListState()
    
    val todayIndex by remember(filteredMatches) {
        derivedStateOf {
            val todayString = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            val nextMatch = TOURNAMENT_MATCHES.find { it.date >= todayString } ?: TOURNAMENT_MATCHES.firstOrNull()
            if (nextMatch == null) {
                -1
            } else {
                val groupedByDate = filteredMatches.groupBy { it.date }
                var currentStage: String? = null
                var targetIndex = -1
                var indexCounter = 2 // Start after Hero (0) and Sticky Header (1)
                
                for ((date, matchesForDate) in groupedByDate) {
                    val firstMatchOfDate = matchesForDate.firstOrNull()
                    
                    if (firstMatchOfDate != null && firstMatchOfDate.stage != currentStage) {
                        currentStage = firstMatchOfDate.stage
                        if (date == nextMatch.date) {
                            targetIndex = indexCounter
                            break
                        }
                        indexCounter++ // stage break
                    }
                    
                    if (date == nextMatch.date) {
                        targetIndex = indexCounter
                        break
                    }
                    indexCounter++ // date header
                    
                    val matchIndexInDate = matchesForDate.indexOfFirst { it.matchNumber == nextMatch.matchNumber }
                    if (matchIndexInDate != -1) {
                        targetIndex = indexCounter + matchIndexInDate
                        break
                    }
                    
                    indexCounter += matchesForDate.size // matches
                }
                targetIndex
            }
        }
    }

    val showTopButton by remember(listState, todayIndex) {
        derivedStateOf {
            todayIndex != -1 && listState.firstVisibleItemIndex >= todayIndex
        }
    }
    
    var selectedTab by remember { mutableStateOf(0) } // 0 = Matches, 1 = Points Table
    var selectedGroup by remember { mutableStateOf("A") }
    var editingMatch by remember { mutableStateOf<Match?>(null) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 88.dp) // Space for FAB
        ) {
            // 1. Hero Header & Owner credit
            item {
                HeroHeader()
            }

            // 2. Controls Section (Sticky Header with dynamic contents based on Tab)
            stickyHeader {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    tonalElevation = 4.dp,
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
                    shadowElevation = 2.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Styled Segmented Tabs
                        TabRow(
                            selectedTabIndex = selectedTab,
                            containerColor = Color.Transparent,
                            contentColor = MaterialTheme.colorScheme.primary,
                            indicator = { tabPositions ->
                                TabRowDefaults.SecondaryIndicator(
                                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                                    color = MaterialTheme.colorScheme.primary
                                )
                            },
                            divider = {}
                        ) {
                            Tab(
                                selected = selectedTab == 0,
                                onClick = { selectedTab = 0 },
                                text = { Text("Matches", fontWeight = FontWeight.Bold, fontSize = 15.sp) }
                            )
                            Tab(
                                selected = selectedTab == 1,
                                onClick = { selectedTab = 1 },
                                text = { Text("Points Table", fontWeight = FontWeight.Bold, fontSize = 15.sp) }
                            )
                        }

                        if (selectedTab == 0) {
                            // Search bar + Calendar Date Filter
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedTextField(
                                    value = searchQuery,
                                    onValueChange = { viewModel.updateSearchQuery(it) },
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("searchInput"),
                                    placeholder = { Text(text = "Search by Team...", fontSize = 13.sp) },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.Search,
                                            contentDescription = "Search"
                                        )
                                    },
                                    trailingIcon = {
                                        if (searchQuery.isNotEmpty()) {
                                            IconButton(onClick = { viewModel.updateSearchQuery("") }) {
                                                Icon(
                                                    imageVector = Icons.Default.Clear,
                                                    contentDescription = "Clear search"
                                                )
                                            }
                                        }
                                    },
                                    singleLine = true,
                                    shape = RoundedCornerShape(12.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                                        unfocusedBorderColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.4f)
                                    )
                                )

                                // Calendar button for precise date picker dialog
                                IconButton(
                                    onClick = {
                                        val calendar = Calendar.getInstance()
                                        selectedDate?.let { sDate ->
                                            try {
                                                val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
                                                val parsedDate = sdf.parse(sDate)
                                                if (parsedDate != null) {
                                                    calendar.time = parsedDate
                                                }
                                            } catch (e: Exception) {
                                                // ignore
                                            }
                                        }
                                        DatePickerDialog(
                                            context,
                                            { _, year, month, dayOfMonth ->
                                                val formattedMonth = String.format("%02d", month + 1)
                                                val formattedDay = String.format("%02d", dayOfMonth)
                                                viewModel.selectDate("$year-$formattedMonth-$formattedDay")
                                            },
                                            calendar.get(Calendar.YEAR),
                                            calendar.get(Calendar.MONTH),
                                            calendar.get(Calendar.DAY_OF_MONTH)
                                        ).apply {
                                            val minCal = Calendar.getInstance().apply { set(2026, 5, 12) }
                                            val maxCal = Calendar.getInstance().apply { set(2026, 6, 20) }
                                            datePicker.minDate = minCal.timeInMillis
                                            datePicker.maxDate = maxCal.timeInMillis
                                            show()
                                        }
                                    },
                                    modifier = Modifier
                                        .size(52.dp)
                                        .background(
                                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                            shape = RoundedCornerShape(12.dp)
                                        )
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CalendarMonth,
                                        contentDescription = "Select Date",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }

                            // Active Date Filter Badge row
                            if (selectedDate != null) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CalendarMonth,
                                        contentDescription = null,
                                        modifier = Modifier.size(14.dp),
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Date: $selectedDate",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Clear Date Filter",
                                        modifier = Modifier
                                            .size(16.dp)
                                            .clickable { viewModel.selectDate(null) },
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }

                            // Reset Filters button when filter is active
                            if (selectedDate != null || searchQuery.isNotEmpty()) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    TextButton(
                                        onClick = { viewModel.clearFilters() },
                                        contentPadding = PaddingValues(0.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Rounded.RestartAlt,
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Reset Filters", fontSize = 12.sp)
                                    }
                                }
                            }
                        } else {
                            // Point Table: Horizontal Group Standings Pills
                            LazyRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                contentPadding = PaddingValues(vertical = 4.dp)
                            ) {
                                items(listOf("A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L")) { group ->
                                    val isSelected = selectedGroup == group
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                                            .border(1.dp, if (isSelected) Color.Transparent else MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f), RoundedCornerShape(10.dp))
                                            .clickable { selectedGroup = group }
                                            .padding(horizontal = 16.dp, vertical = 8.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "Group $group",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (selectedTab == 0) {
                // 3. Matches List rendering with corrected stage transition separation
                if (filteredMatches.isEmpty()) {
                    item {
                        EmptyState()
                    }
                } else {
                    val groupedByDate = filteredMatches.groupBy { it.date }
                    var currentStage: String? = null

                    groupedByDate.forEach { (date, matchesForDate) ->
                        val firstMatchOfDate = matchesForDate.firstOrNull()

                        if (firstMatchOfDate != null && firstMatchOfDate.stage != currentStage) {
                            val stageKeyToRender = firstMatchOfDate.stage
                            currentStage = stageKeyToRender
                            item(key = "stage_break_${stageKeyToRender}") {
                                StageBreakDivider(stageKey = stageKeyToRender)
                            }
                        }

                        item(key = "date_header_$date") {
                            DateHeaderView(
                                dateStr = date,
                                dayName = firstMatchOfDate?.day ?: "",
                                dayNum = firstMatchOfDate?.d ?: 12,
                                monthName = firstMatchOfDate?.m ?: "June",
                                matchCount = matchesForDate.size
                            )
                        }

                        items(
                            items = matchesForDate,
                            key = { "match_${it.matchNumber}" }
                        ) { match ->
                            MatchCardView(
                                match = match,
                                result = resultsMap[match.matchNumber],
                                onTap = { editingMatch = match }
                            )
                        }
                    }
                }
            } else {
                // Points Table View
                val standings = standingsMap[selectedGroup] ?: emptyList()
                item {
                    GroupStandingsTable(groupLetter = selectedGroup, standings = standings)
                }
            }
        }

        // Floating Action Buttons Column in the Bottom-Right Corner
        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalAlignment = Alignment.End
        ) {
            // 1. Update Bracket button (only visible if group stage is not completed)
            AnimatedVisibility(
                visible = !allGroupStageCompleted,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                val updateLabel = if (manualBracketUpdated) "Undo" else "Update"
                val buttonColor = if (manualBracketUpdated) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary
                
                FloatingActionButton(
                    onClick = { viewModel.triggerManualBracketUpdate() },
                    containerColor = buttonColor,
                    contentColor = Color.White,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .height(38.dp)
                        .testTag("update_bracket_fab")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Update Bracket",
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = updateLabel,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            letterSpacing = 0.5.sp
                        )
                    }
                }
            }

            // 2. Jump to Today or Move to Top button (dynamically switches based on scroll state)
            if (showTopButton) {
                FloatingActionButton(
                    onClick = {
                        scope.launch {
                            listState.animateScrollToItem(0)
                        }
                    },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .height(38.dp)
                        .testTag("todayBtn")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.ArrowUpward,
                            contentDescription = "Move to Top",
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "TOP",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            letterSpacing = 0.5.sp
                        )
                    }
                }
            } else {
                FloatingActionButton(
                    onClick = {
                        scope.launch {
                            if (todayIndex != -1) {
                                listState.animateScrollToItem(todayIndex)
                            } else {
                                listState.animateScrollToItem(2)
                            }
                        }
                    },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .height(38.dp)
                        .testTag("todayBtn")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.MyLocation,
                            contentDescription = "Jump Today",
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "Today",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            letterSpacing = 0.5.sp
                        )
                    }
                }
            }
        }
    }

    // Edit Score Dialog
    editingMatch?.let { match ->
        ResultEntryDialog(
            match = match,
            currentResult = resultsMap[match.matchNumber],
            onDismiss = { editingMatch = null },
            onSave = { s1, s2, winner ->
                viewModel.saveMatchResult(match.matchNumber, s1, s2, winner)
                editingMatch = null
            },
            onClear = {
                viewModel.deleteMatchResult(match.matchNumber)
                editingMatch = null
            }
        )
    }
}

@Composable
fun GroupStandingsTable(groupLetter: String, standings: List<TeamStanding>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f)
        ),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            // Table Title
            Text(
                text = "GROUP $groupLetter STANDINGS",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // Header Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "#",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    modifier = Modifier.width(20.dp)
                )
                Text(
                    text = "Team",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    modifier = Modifier.weight(1f)
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val headerStyle = TextStyle(fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                    Text(text = "P", style = headerStyle, modifier = Modifier.width(20.dp), textAlign = TextAlign.Center)
                    Text(text = "W", style = headerStyle, modifier = Modifier.width(20.dp), textAlign = TextAlign.Center)
                    Text(text = "D", style = headerStyle, modifier = Modifier.width(20.dp), textAlign = TextAlign.Center)
                    Text(text = "L", style = headerStyle, modifier = Modifier.width(20.dp), textAlign = TextAlign.Center)
                    Text(text = "GD", style = headerStyle, modifier = Modifier.width(28.dp), textAlign = TextAlign.Center)
                    Text(text = "PTS", style = headerStyle.copy(color = MaterialTheme.colorScheme.primary), modifier = Modifier.width(28.dp), textAlign = TextAlign.Center)
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.12f), thickness = 1.dp)

            // Standings rows
            standings.forEachIndexed { index, row ->
                val teamName = TEAM_NAMES[row.teamCode] ?: row.teamCode
                val flag = TEAM_FLAGS[row.teamCode] ?: "🏳️"
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Position
                    Text(
                        text = "${index + 1}",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 13.sp,
                        color = if (index < 2) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                        modifier = Modifier.width(20.dp)
                    )

                    // Team Name & Flag
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(text = flag, fontSize = 16.sp)
                        Text(
                            text = teamName,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    // Stats
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val rowStyle = TextStyle(fontWeight = FontWeight.Medium, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface)
                        Text(text = "${row.played}", style = rowStyle, modifier = Modifier.width(20.dp), textAlign = TextAlign.Center)
                        Text(text = "${row.won}", style = rowStyle, modifier = Modifier.width(20.dp), textAlign = TextAlign.Center)
                        Text(text = "${row.drawn}", style = rowStyle, modifier = Modifier.width(20.dp), textAlign = TextAlign.Center)
                        Text(text = "${row.lost}", style = rowStyle, modifier = Modifier.width(20.dp), textAlign = TextAlign.Center)
                        
                        val gdSign = if (row.goalDifference > 0) "+${row.goalDifference}" else "${row.goalDifference}"
                        Text(
                            text = gdSign,
                            style = rowStyle.copy(
                                color = when {
                                    row.goalDifference > 0 -> Color(0xFF4CAF50)
                                    row.goalDifference < 0 -> Color(0xFFF44336)
                                    else -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                }
                            ),
                            modifier = Modifier.width(28.dp),
                            textAlign = TextAlign.Center
                        )
                        
                        Text(
                            text = "${row.points}",
                            style = rowStyle.copy(fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary),
                            modifier = Modifier.width(28.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                if (index < standings.size - 1) {
                    HorizontalDivider(color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.06f), thickness = 0.5.dp)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultEntryDialog(
    match: Match,
    currentResult: MatchResultEntity?,
    onDismiss: () -> Unit,
    onSave: (team1Score: Int, team2Score: Int, winnerTeamCode: String?) -> Unit,
    onClear: () -> Unit
) {
    var score1Str by remember { mutableStateOf(currentResult?.team1Score?.toString() ?: "") }
    var score2Str by remember { mutableStateOf(currentResult?.team2Score?.toString() ?: "") }
    
    val isKnockout = match.stage != "group"
    val score1 = score1Str.toIntOrNull()
    val score2 = score2Str.toIntOrNull()
    val isDraw = score1 != null && score2 != null && score1 == score2
    
    var selectedWinnerCode by remember(currentResult) { mutableStateOf(currentResult?.winnerTeamCode) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Enter Match Result",
                    fontWeight = FontWeight.Black,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Match #${match.matchNumber} | ${match.group}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = TEAM_FLAGS[match.t1] ?: "🏳️",
                            fontSize = 32.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = TEAM_NAMES[match.t1] ?: match.t1,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = score1Str,
                            onValueChange = { score1Str = it.filter { char -> char.isDigit() } },
                            modifier = Modifier.width(64.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            textStyle = TextStyle(textAlign = TextAlign.Center, fontWeight = FontWeight.Bold, fontSize = 18.sp),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }

                    Text(
                        text = "vs",
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                        fontSize = 16.sp
                    )

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = TEAM_FLAGS[match.t2] ?: "🏳️",
                            fontSize = 32.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = TEAM_NAMES[match.t2] ?: match.t2,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = score2Str,
                            onValueChange = { score2Str = it.filter { char -> char.isDigit() } },
                            modifier = Modifier.width(64.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            textStyle = TextStyle(textAlign = TextAlign.Center, fontWeight = FontWeight.Bold, fontSize = 18.sp),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }

                if (isKnockout && isDraw) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Match is a draw. Select winner of tiebreaker:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val isWinner1 = selectedWinnerCode == match.t1
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isWinner1) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant)
                                .clickable { selectedWinnerCode = match.t1 }
                                .padding(10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = TEAM_NAMES[match.t1] ?: match.t1,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isWinner1) Color.White else MaterialTheme.colorScheme.onSurface,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        val isWinner2 = selectedWinnerCode == match.t2
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isWinner2) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant)
                                .clickable { selectedWinnerCode = match.t2 }
                                .padding(10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = TEAM_NAMES[match.t2] ?: match.t2,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isWinner2) Color.White else MaterialTheme.colorScheme.onSurface,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (currentResult != null) {
                        TextButton(
                            onClick = onClear,
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                        ) {
                            Text("Clear", fontWeight = FontWeight.Bold)
                        }
                    } else {
                        TextButton(
                            onClick = onDismiss,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Cancel", fontWeight = FontWeight.Bold)
                        }
                    }

                    Button(
                        onClick = {
                            val s1 = score1Str.toIntOrNull()
                            val s2 = score2Str.toIntOrNull()
                            if (s1 != null && s2 != null) {
                                val winner = if (isKnockout && s1 == s2) {
                                    selectedWinnerCode ?: match.t1
                                } else {
                                    null
                                }
                                onSave(s1, s2, winner)
                            }
                        },
                        enabled = score1Str.isNotEmpty() && score2Str.isNotEmpty(),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Save", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// ==========================================
// INDIVIDUAL COMPOSE SUB-VIEWS
// ==========================================

@Composable
fun HeroHeader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                        MaterialTheme.colorScheme.background
                    )
                )
            )
            .padding(top = 28.dp, bottom = 20.dp, start = 16.dp, end = 16.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Owner name beautifully stylized at the top
            Text(
                text = "Hridoy Hasan Yeasin",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.ExtraBold,
                fontStyle = FontStyle.Normal,
                letterSpacing = 2.sp,
                modifier = Modifier.padding(bottom = 6.dp)
            )

            // Trophy with glowing background effect
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(68.dp)
                        .background(
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                            shape = CircleShape
                        )
                )
                Text(
                    text = "🏆",
                    fontSize = 38.sp
                )
            }

            Text(
                text = "OFFICIAL MATCH SCHEDULE",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 3.sp,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "FIFA ",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Light,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "World Cup ",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "2026",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "USA · CANADA · MEXICO   |   12 JUNE – 20 JULY",
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 0.5.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Stats grid card
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp, horizontal = 4.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    StatItem(number = "48", label = "Teams")
                    DividerLine()
                    StatItem(number = "104", label = "Matches")
                    DividerLine()
                    StatItem(number = "16", label = "Venues")
                    DividerLine()
                    StatItem(number = "39", label = "Days")
                }
            }
        }
    }
}

@Composable
fun StatItem(number: String, label: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(36.dp)
    ) {
        Text(
            text = number,
            fontSize = 11.sp,
            fontWeight = FontWeight.Black,
            color = MaterialTheme.colorScheme.primary,
            lineHeight = 12.sp
        )
        Text(
            text = label.uppercase(Locale.getDefault()),
            fontSize = 5.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
            letterSpacing = 0.5.sp
        )
    }
}

@Composable
fun DividerLine() {
    Box(
        modifier = Modifier
            .width(1.dp)
            .height(14.dp)
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color.Transparent,
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                        Color.Transparent
                    )
                )
            )
    )
}

@Composable
fun DateFiltersRow(
    selectedDate: String?,
    onDateSelect: (String) -> Unit,
    onClearDate: () -> Unit
) {
    // Generate dates range from 2026-06-12 to 2026-07-20
    val dateItems = remember {
        val list = mutableListOf<Pair<String, String>>() // Pair of (formatted date, display label)
        val calendar = Calendar.getInstance()
        val sdfDate = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val sdfLabel = SimpleDateFormat("dd MMM", Locale.US)
        
        // Loop from June 12 (month 5) to July 20 (month 6)
        calendar.set(2026, 5, 12)
        val endCalendar = Calendar.getInstance().apply { set(2026, 6, 21) }
        
        while (calendar.before(endCalendar)) {
            val dateStr = sdfDate.format(calendar.time)
            val labelStr = sdfLabel.format(calendar.time)
            list.add(Pair(dateStr, labelStr))
            calendar.add(Calendar.DATE, 1)
        }
        list
    }

    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        contentPadding = PaddingValues(vertical = 4.dp)
    ) {
        item {
            DatePill(
                selected = selectedDate == null,
                onClick = onClearDate,
                label = "All Dates"
            )
        }

        items(dateItems) { (dateStr, displayLabel) ->
            DatePill(
                selected = selectedDate == dateStr,
                onClick = { onDateSelect(dateStr) },
                label = displayLabel
            )
        }
    }
}

@Composable
fun DatePill(
    selected: Boolean,
    onClick: () -> Unit,
    label: String
) {
    val backgroundColor = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
    val contentColor = if (selected) Color.White else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
    val borderColor = if (selected) Color.Transparent else MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f)

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(backgroundColor)
            .border(1.dp, borderColor, RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.SemiBold,
            color = contentColor
        )
    }
}

@Composable
fun StageBreakDivider(stageKey: String) {
    val style = STAGE_STYLES[stageKey] ?: STAGE_STYLES["group"]!!

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 24.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .height(1.dp)
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(Color.Transparent, style.primaryColor.copy(alpha = 0.4f))
                    )
                )
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 12.dp)
        ) {
            Text(
                text = style.icon,
                fontSize = 18.sp,
                modifier = Modifier.padding(end = 6.dp)
            )
            Text(
                text = style.label.uppercase(Locale.getDefault()),
                fontSize = 14.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 2.sp,
                color = style.primaryColor
            )
            Text(
                text = style.icon,
                fontSize = 18.sp,
                modifier = Modifier.padding(start = 6.dp)
            )
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .height(1.dp)
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(style.primaryColor.copy(alpha = 0.4f), Color.Transparent)
                    )
                )
        )
    }
}

@Composable
fun DateHeaderView(
    dateStr: String,
    dayName: String,
    dayNum: Int,
    monthName: String,
    matchCount: Int
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 22.dp, bottom = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Date badge circle
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(54.dp)
                .background(
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                    shape = RoundedCornerShape(12.dp)
                )
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.25f),
                    shape = RoundedCornerShape(12.dp)
                )
        ) {
            Text(
                text = dayNum.toString(),
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = dayName.uppercase(Locale.getDefault()),
                fontSize = 14.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.sp,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "$monthName 2026",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                letterSpacing = 1.sp
            )
        }

        // Horizontal elegant line connecting info
        Box(
            modifier = Modifier
                .weight(0.6f)
                .height(1.dp)
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                            Color.Transparent
                        )
                    )
                )
        )

        Spacer(modifier = Modifier.width(8.dp))

        // Right match count label
        Text(
            text = "$matchCount Match${if (matchCount > 1) "es" else ""}",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            letterSpacing = 0.5.sp,
            modifier = Modifier
                .background(
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(4.dp)
                )
                .padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}

@Composable
fun MatchCardView(
    match: Match,
    result: MatchResultEntity? = null,
    onTap: () -> Unit = {}
) {
    val isDark = isSystemInDarkTheme()
    val stageStyle = STAGE_STYLES[match.stage] ?: STAGE_STYLES["group"]!!
    
    val gradientColors = if (isDark) stageStyle.cardGradientDark else stageStyle.cardGradientLight

    val winnerTeamCode = when {
        result == null -> null
        result.winnerTeamCode != null -> result.winnerTeamCode
        result.team1Score != null && result.team2Score != null -> {
            when {
                result.team1Score > result.team2Score -> match.t1
                result.team1Score < result.team2Score -> match.t2
                else -> null
            }
        }
        else -> null
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .testTag("match_card_${match.matchNumber}")
            .pointerInput(Unit) {
                detectTapGestures(
                    onDoubleTap = { onTap() }
                )
            },
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(
            width = 1.dp,
            color = stageStyle.primaryColor.copy(alpha = 0.25f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.linearGradient(colors = gradientColors))
                .padding(16.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                
                // Top Header Row (Pill, Match #, Time Badge)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Stage Pill
                    Box(
                        modifier = Modifier
                            .background(
                                color = stageStyle.primaryColor.copy(alpha = 0.12f),
                                shape = RoundedCornerShape(6.dp)
                            )
                            .border(
                                width = 1.dp,
                                color = stageStyle.primaryColor.copy(alpha = 0.25f),
                                shape = RoundedCornerShape(6.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = match.group.uppercase(Locale.getDefault()),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = stageStyle.primaryColor,
                            letterSpacing = 0.5.sp
                        )
                    }

                    // Match number
                    Text(
                        text = "Match ${match.matchNumber}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                        letterSpacing = 0.5.sp
                    )

                    // Time Badge
                    Text(
                        text = formatTo12Hour(match.time),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.primary,
                        letterSpacing = 0.5.sp
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Teams Row (Team 1 vs Team 2)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    // Team 1
                    Box(modifier = Modifier.weight(1f)) {
                        TeamBlock(
                            code = match.t1,
                            isRightAligned = false,
                            isWinner = winnerTeamCode == match.t1
                        )
                    }

                    // Scoreboard Display
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (result?.team1Score != null && result.team2Score != null) {
                            Text(
                                text = result.team1Score.toString(),
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f), CircleShape)
                            )
                            Text(
                                text = result.team2Score.toString(),
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.primary
                            )
                        } else {
                            // VS Circle
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(28.dp)
                                    .background(
                                        color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.08f),
                                        shape = CircleShape
                                    )
                                    .border(
                                        width = 1.dp,
                                        color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f),
                                        shape = CircleShape
                                    )
                            ) {
                                Text(
                                    text = "VS",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                            }
                        }
                    }

                    // Team 2
                    Box(modifier = Modifier.weight(1f)) {
                        TeamBlock(
                            code = match.t2,
                            isRightAligned = true,
                            isWinner = winnerTeamCode == match.t2
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Bottom Divider Line
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.06f))
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Footer Location
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Place,
                        contentDescription = "Stadium",
                        modifier = Modifier.size(13.dp),
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = match.venue,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
fun TeamBlock(code: String, isRightAligned: Boolean, isWinner: Boolean = false) {
    val isTbd = TEAM_FLAGS[code] == null && code != "SCO" // Flag helper is SCO custom but in map we have SCO fallback

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = if (isRightAligned) Arrangement.End else Arrangement.Start
    ) {
        if (!isRightAligned) {
            // Left Team: Flag then info
            if (!isTbd) {
                Text(
                    text = getTeamFlag(code),
                    fontSize = 28.sp,
                    modifier = Modifier.padding(end = 8.dp)
                )
            }
            Column(horizontalAlignment = Alignment.Start) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = code,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = if (isWinner) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                        lineHeight = 20.sp
                    )
                    if (isWinner) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("🏆", fontSize = 14.sp)
                    }
                }
                Text(
                    text = TEAM_NAMES[code] ?: "TBD Qualified",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isWinner) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontStyle = if (isTbd) FontStyle.Italic else FontStyle.Normal
                )
            }
        } else {
            // Right Team: Info then flag
            Column(horizontalAlignment = Alignment.End) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (isWinner) {
                        Text("🏆", fontSize = 14.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                    }
                    Text(
                        text = code,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = if (isWinner) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                        lineHeight = 20.sp
                    )
                }
                Text(
                    text = TEAM_NAMES[code] ?: "TBD Qualified",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isWinner) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontStyle = if (isTbd) FontStyle.Italic else FontStyle.Normal
                )
            }
            if (!isTbd) {
                Text(
                    text = getTeamFlag(code),
                    fontSize = 28.sp,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    }
}

@Composable
fun EmptyState() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 64.dp, horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "⚽",
            fontSize = 52.sp,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        Text(
            text = "No Matches Found",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "Try clearing your filters or searching for another team/venue.",
            fontSize = 13.sp,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
        )
    }
}

// ==========================================
// UTILITY FUNCTIONS
// ==========================================
fun formatTo12Hour(time24: String): String {
    return try {
        val parts = time24.split(":")
        val hour = parts[0].toInt()
        val minute = parts[1]
        val amPm = if (hour >= 12) "PM" else "AM"
        val displayHour = when {
            hour == 0 -> 12
            hour > 12 -> hour - 12
            else -> hour
        }
        "$displayHour:$minute $amPm"
    } catch (e: Exception) {
        time24
    }
}
