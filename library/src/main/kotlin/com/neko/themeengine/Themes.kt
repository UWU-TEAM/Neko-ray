package com.neko.themeengine

import androidx.annotation.ColorRes
import androidx.annotation.StyleRes

import com.neko.R

enum class Theme(@StyleRes val themeId: Int, @ColorRes val primaryColor: Int) {
    Amber(R.style.Theme_ThemeEngine_Amber, R.color.amber_primaryInverse),
    Blue(R.style.Theme_ThemeEngine_Blue, R.color.blue_primaryInverse),
    BlueVariant(R.style.Theme_ThemeEngine_BlueVariant, R.color.blue_variant_primaryInverse),
    Brown(R.style.Theme_ThemeEngine_Brown, R.color.brown_primaryInverse),
    Cyan(R.style.Theme_ThemeEngine_Cyan, R.color.cyan_primaryInverse),
    DeepOrange(R.style.Theme_ThemeEngine_DeepOrange, R.color.deep_orange_primaryInverse),
    DeepPurple(R.style.Theme_ThemeEngine_DeepPurple, R.color.deep_purple_primaryInverse),
    Green(R.style.Theme_ThemeEngine_Green, R.color.green_primaryInverse),
    Indigo(R.style.Theme_ThemeEngine_Indigo, R.color.indigo_primaryInverse),
    LightBlue(R.style.Theme_ThemeEngine_LightBlue, R.color.light_blue_primaryInverse),
    LightGreen(R.style.Theme_ThemeEngine_LightGreen, R.color.light_green_primaryInverse),
    Lime(R.style.Theme_ThemeEngine_Lime, R.color.lime_primaryInverse),
    Orange(R.style.Theme_ThemeEngine_Orange, R.color.orange_primaryInverse),
    Pink(R.style.Theme_ThemeEngine_Pink, R.color.pink_primaryInverse),
    Purple(R.style.Theme_ThemeEngine_Purple, R.color.purple_primaryInverse),
    Red(R.style.Theme_ThemeEngine_Red, R.color.red_primaryInverse),
    Teal(R.style.Theme_ThemeEngine_Teal, R.color.teal_primaryInverse),
    Violet(R.style.Theme_ThemeEngine_Violet, R.color.violet_primaryInverse),
    Yellow(R.style.Theme_ThemeEngine_Yellow, R.color.yellow_primaryInverse),
    Nekocok(R.style.Theme_ThemeEngine_Nekocok, R.color.nekocok_primaryInverse)
}