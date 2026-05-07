package com.bailing.lark.roll.data

import com.bailing.lark.roll.ui.icons.UtilityIconKind

enum class BillStatus { Paid, DueSoon, Overdue }

enum class UtilityKind(
    val title: String,
    val iconKind: UtilityIconKind,
    val unit: String
) {
    Electricity("Electricity", UtilityIconKind.Electricity, "kWh"),
    Water("Water", UtilityIconKind.Water, "m³"),
    Gas("Gas", UtilityIconKind.Gas, "m³"),
    Heating("Heating", UtilityIconKind.Heating, "Gcal"),
    Internet("Internet", UtilityIconKind.Internet, "Mbps"),
    BuildingMaintenance("Building maintenance", UtilityIconKind.Building, "")
}

data class UtilityBill(
    val id: Int,
    val kind: UtilityKind,
    val amount: String,
    val dueDate: String,
    val status: BillStatus,
    val usageNote: String,
    val provider: String,
    val paidForMonth: String? = null
)

object MockData {
    val bills: List<UtilityBill> = listOf(
        UtilityBill(
            id = 1,
            kind = UtilityKind.Electricity,
            amount = "$ 64.20",
            dueDate = "Due May 12",
            status = BillStatus.DueSoon,
            usageNote = "Used 312 kWh this month",
            provider = "City Power Co."
        ),
        UtilityBill(
            id = 2,
            kind = UtilityKind.Water,
            amount = "$ 28.40",
            dueDate = "Due May 18",
            status = BillStatus.DueSoon,
            usageNote = "Used 8.4 m³ this month",
            provider = "Metro Water"
        ),
        UtilityBill(
            id = 3,
            kind = UtilityKind.Gas,
            amount = "$ 41.10",
            dueDate = "Due May 02",
            status = BillStatus.Overdue,
            usageNote = "Used 24 m³ this month",
            provider = "Northern Gas"
        ),
        UtilityBill(
            id = 4,
            kind = UtilityKind.Heating,
            amount = "$ 78.00",
            dueDate = "Paid Apr 28",
            status = BillStatus.Paid,
            usageNote = "0.42 Gcal this period",
            provider = "Warmline Heat"
        ),
        UtilityBill(
            id = 5,
            kind = UtilityKind.Internet,
            amount = "$ 19.90",
            dueDate = "Paid Apr 30",
            status = BillStatus.Paid,
            usageNote = "300 Mbps plan",
            provider = "FiberLink"
        ),
        UtilityBill(
            id = 6,
            kind = UtilityKind.BuildingMaintenance,
            amount = "$ 35.50",
            dueDate = "Due May 25",
            status = BillStatus.DueSoon,
            usageNote = "Common areas, lifts, cleaning",
            provider = "House Council"
        )
    )
}
