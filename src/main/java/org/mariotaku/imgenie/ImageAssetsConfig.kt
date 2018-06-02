package org.mariotaku.imgenie

import com.android.resources.Density
import org.gradle.api.model.ObjectFactory
import org.mariotaku.imgenie.model.OutputFormat

open class ImageAssetsConfig(
        vararg outputDensities: String = arrayOf("mdpi", "hdpi", "xhdpi", "xxhdpi", "xxxhdpi"),
        outputFormat: String = "png"
) {

    val outputDensities: List<Density> = outputDensities.map { Density.getEnum(it) }
    val outputFormat = OutputFormat.valueOf(outputFormat.toUpperCase())

    constructor(objectFactory: ObjectFactory) : this()
}
