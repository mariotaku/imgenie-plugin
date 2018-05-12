package org.mariotaku.imgenie.model

class Density(val value: Float) {
    fun forName(name: String): Density = when (name) {
        "" -> Density(0f)
        "ldpi" -> Density(0.75f)
        "mdpi" -> Density(1f)
        "hdpi" -> Density(1.5f)
        "xhdpi" -> Density(2f)
        "xxhdpi" -> Density(3f)
        "xxxhdpi" -> Density(4f)
        "nodpi" -> Density(0f)
        "tvdpi" -> Density(1.33125f)
        "anydpi" -> Density(1f)
        else -> Density(0f)
    }
}