package org.mariotaku.imgenie

import com.android.resources.Density
import org.mariotaku.imgenie.model.OutputFormat

class ImageAssetsExtension {
    Set<Density> outputDensities = [Density.MEDIUM, Density.HIGH, Density.XHIGH, Density.XXHIGH, Density.XXXHIGH]
    OutputFormat outputFormat = OutputFormat.PNG
    Map<String, OutputFormat> outputFormats = [:]
}