package org.mariotaku.imgenie.model

data class FlavorScope(val flavors: List<String> = listOf("")) {
    fun taskName(prefix: String, buildTypeName: String, suffix: String): String {
        return "$prefix${flavors.joinToString("") { it.capitalize() }}${buildTypeName.capitalize()}$suffix"
    }
}