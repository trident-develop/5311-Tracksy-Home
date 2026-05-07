package com.bailing.lark.roll.ui.theme

import androidx.compose.ui.graphics.Color
import com.bailing.lark.roll.data.UtilityKind

data class UtilityPalette(val primary: Color, val soft: Color)

fun paletteFor(kind: UtilityKind): UtilityPalette = when (kind) {
    UtilityKind.Electricity -> UtilityPalette(ElectricityPrimary, ElectricitySoft)
    UtilityKind.Water -> UtilityPalette(WaterPrimary, WaterSoft)
    UtilityKind.Gas -> UtilityPalette(GasPrimary, GasSoft)
    UtilityKind.Heating -> UtilityPalette(HeatingPrimary, HeatingSoft)
    UtilityKind.Internet -> UtilityPalette(InternetPrimary, InternetSoft)
    UtilityKind.BuildingMaintenance -> UtilityPalette(BuildingPrimary, BuildingSoft)
}
