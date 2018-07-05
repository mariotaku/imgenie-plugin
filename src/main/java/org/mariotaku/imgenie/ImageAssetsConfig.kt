package org.mariotaku.imgenie

import com.android.resources.Density
import org.gradle.api.model.ObjectFactory
import org.mariotaku.imgenie.model.OutputFormat

open class ImageAssetsConfig(
        vararg outputDensities: String = arrayOf("mdpi", "hdpi", "xhdpi", "xxhdpi", "xxxhdpi"),
        outputFormat: String = "png",
        outputFormats: Map<String, String> = emptyMap()
) {

    val outputDensities: List<Density> = outputDensities.map { Density.getEnum(it) }
    val outputFormat: OutputFormat = OutputFormat.valueOf(outputFormat.toUpperCase())
    val outputFormats: List<Pair<Regex, OutputFormat>> = outputFormats.map { (k, v) ->
        Regex(k) to OutputFormat.valueOf(v.toUpperCase())
    }

    constructor(objectFactory: ObjectFactory) : this()
}

