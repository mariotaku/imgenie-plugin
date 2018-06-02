package org.mariotaku.imgenie.model

import java.util.*

data class FlavorScope(val flavors: List<String> = listOf("")) {
    fun camelCaseName(buildTypeName: String, prefix: String = "", suffix: String = ""): String {
        val segs = LinkedList<String>()
        if (prefix.isNotEmpty()) {
            segs += prefix
        }
        segs += flavors
        segs += buildTypeName
        if (suffix.isNotEmpty()) {
            segs += suffix
        }
        return segs.mapIndexed { index, s ->
            if (index > 0) s.capitalize() else s.decapitalize()
        }.joinToString("")
    }
}