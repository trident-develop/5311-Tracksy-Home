package com.bailing.lark.roll.data

data class MeterReading(
    val id: Long,
    val year: Int,
    val month: Int,
    val value: Double
) {
    fun monthIndex(): Int = year * 12 + (month - 1)
}

data class UtilityRecord(
    val kind: UtilityKind,
    val providerName: String,
    val tariff: Double,
    val readings: List<MeterReading>,
    val discountPercent: Double = 0.0
) {
    val sortedReadings: List<MeterReading>
        get() = readings.sortedBy { it.monthIndex() }

    val lastMonthUsage: Double
        get() {
            val s = sortedReadings
            if (s.size < 2) return 0.0
            val a = s[s.size - 2]
            val b = s.last()
            return (b.value - a.value).coerceAtLeast(0.0)
        }

    val lastMonthLabel: String
        get() {
            val s = sortedReadings
            if (s.size < 2) return "—"
            return "${monthName(s.last().month)} ${s.last().year} vs ${monthName(s[s.size - 2].month)} ${s[s.size - 2].year}"
        }

    val lastMonthCost: Double
        get() = lastMonthUsage * tariff

    val effectiveDiscountFraction: Double
        get() = (discountPercent / 100.0).coerceIn(0.0, 1.0)

    val discountAmount: Double
        get() = lastMonthCost * effectiveDiscountFraction

    val finalCost: Double
        get() = (lastMonthCost - discountAmount).coerceAtLeast(0.0)

    fun hasReadingFor(year: Int, month: Int): Boolean =
        readings.any { it.year == year && it.month == month }
}

const val MIN_RECORD_YEAR = 2026
const val MAX_RECORD_YEAR = 2032

fun monthName(month: Int): String = when (month) {
    1 -> "Jan"; 2 -> "Feb"; 3 -> "Mar"; 4 -> "Apr"; 5 -> "May"; 6 -> "Jun"
    7 -> "Jul"; 8 -> "Aug"; 9 -> "Sep"; 10 -> "Oct"; 11 -> "Nov"; 12 -> "Dec"
    else -> "—"
}

fun monthFullName(month: Int): String = when (month) {
    1 -> "January"; 2 -> "February"; 3 -> "March"; 4 -> "April"; 5 -> "May"; 6 -> "June"
    7 -> "July"; 8 -> "August"; 9 -> "September"; 10 -> "October"; 11 -> "November"; 12 -> "December"
    else -> "—"
}

object MockRecords {
    fun initial(): List<UtilityRecord> = listOf(
        UtilityRecord(
            kind = UtilityKind.Electricity,
            providerName = "City Power Co.",
            tariff = 0.18,
            readings = listOf(
                MeterReading(1L, 2026, 1, 12340.0),
                MeterReading(2L, 2026, 2, 12652.0),
                MeterReading(3L, 2026, 3, 12970.0),
                MeterReading(4L, 2026, 4, 13302.0)
            )
        ),
        UtilityRecord(
            kind = UtilityKind.Water,
            providerName = "Metro Water",
            tariff = 2.40,
            readings = listOf(
                MeterReading(11L, 2026, 1, 248.0),
                MeterReading(12L, 2026, 2, 256.4),
                MeterReading(13L, 2026, 3, 264.8),
                MeterReading(14L, 2026, 4, 273.6)
            )
        ),
        UtilityRecord(
            kind = UtilityKind.Gas,
            providerName = "Northern Gas",
            tariff = 0.85,
            readings = listOf(
                MeterReading(21L, 2026, 1, 540.0),
                MeterReading(22L, 2026, 2, 568.0),
                MeterReading(23L, 2026, 3, 596.0),
                MeterReading(24L, 2026, 4, 620.0)
            )
        ),
        UtilityRecord(
            kind = UtilityKind.Heating,
            providerName = "Warmline Heat",
            tariff = 95.00,
            readings = listOf(
                MeterReading(31L, 2026, 1, 12.4),
                MeterReading(32L, 2026, 2, 12.8),
                MeterReading(33L, 2026, 3, 13.2),
                MeterReading(34L, 2026, 4, 13.6)
            )
        ),
        UtilityRecord(
            kind = UtilityKind.Internet,
            providerName = "FiberLink",
            tariff = 19.90,
            readings = listOf(
                MeterReading(41L, 2026, 1, 1.0),
                MeterReading(42L, 2026, 2, 2.0),
                MeterReading(43L, 2026, 3, 3.0),
                MeterReading(44L, 2026, 4, 4.0)
            )
        ),
        UtilityRecord(
            kind = UtilityKind.BuildingMaintenance,
            providerName = "House Council",
            tariff = 35.50,
            readings = listOf(
                MeterReading(51L, 2026, 1, 1.0),
                MeterReading(52L, 2026, 2, 2.0),
                MeterReading(53L, 2026, 3, 3.0),
                MeterReading(54L, 2026, 4, 4.0)
            )
        )
    )
}
