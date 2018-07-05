package org.mariotaku.imgenie

import com.android.resources.Density
import org.gradle.api.model.ObjectFactory
import org.mariotaku.imgenie.model.OutputFormat

open class ImageAssetsConfig(
        var outputDensities: Set<String> = setOf("mdpi", "hdpi", "xhdpi", "xxhdpi", "xxxhdpi"),
        var outputFormat: String = "png",
        var outputFormats: Map<String, String> = emptyMap()
) {

    internal val densitiesList: List<Density>
        get() = outputDensities.map { Density.getEnum(it) }
    internal val defFormat: OutputFormat
        get() = OutputFormat.valueOf(outputFormat.toUpperCase())
    internal val defFormats: List<Pair<Regex, OutputFormat>>
        get() = outputFormats.map { (k, v) ->
            Regex(k) to OutputFormat.valueOf(v.toUpperCase())
        }

    constructor(objectFactory: ObjectFactory) : this()
}

