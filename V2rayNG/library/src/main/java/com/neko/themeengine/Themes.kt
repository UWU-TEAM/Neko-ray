package com.neko.themeengine

import androidx.annotation.ColorRes
import androidx.annotation.StyleRes

import com.neko.R

enum class Theme(@StyleRes val themeId: Int, @ColorRes val primaryColor: Int) {
    Amber(R.style.Theme_ThemeEngine_Amber, R.color.amber_primary),
    BlueVariant(R.style.Theme_ThemeEngine_BlueVariant, R.color.blue_variant_primary),
    Blue(R.style.Theme_ThemeEngine_Blue, R.color.blue_primary),
    Brown(R.style.Theme_ThemeEngine_Brown, R.color.brown_primary),
    Cyan(R.style.Theme_ThemeEngine_Cyan, R.color.cyan_primary),
    DeepOrange(R.style.Theme_ThemeEngine_DeepOrange, R.color.deep_orange_primary),
    DeepPurple(R.style.Theme_ThemeEngine_DeepPurple, R.color.deep_purple_primary),
    Green(R.style.Theme_ThemeEngine_Green, R.color.green_primary),
    Indigo(R.style.Theme_ThemeEngine_Indigo, R.color.indigo_primary),
    LightBlue(R.style.Theme_ThemeEngine_LightBlue, R.color.light_blue_primary),
    LightGreen(R.style.Theme_ThemeEngine_LightGreen, R.color.light_green_primary),
    Lime(R.style.Theme_ThemeEngine_Lime, R.color.lime_primary),
    Orange(R.style.Theme_ThemeEngine_Orange, R.color.orange_primary),
    Pink(R.style.Theme_ThemeEngine_Pink, R.color.pink_primary),
    Purple(R.style.Theme_ThemeEngine_Purple, R.color.purple_primary),
    Red(R.style.Theme_ThemeEngine_Red, R.color.red_primary),
    Teal(R.style.Theme_ThemeEngine_Teal, R.color.teal_primary),
    Violet(R.style.Theme_ThemeEngine_Violet, R.color.violet_primary),
    Yellow(R.style.Theme_ThemeEngine_Yellow, R.color.yellow_primary),
    Nekocok(R.style.Theme_ThemeEngine_Nekocok, R.color.nekocok_primary)
}