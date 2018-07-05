package org.mariotaku.imgenie.asset

import com.android.resources.Density
import org.mariotaku.imgenie.model.OutputFormat
import java.awt.Dimension
import java.io.File
import java.util.*

abstract class ImageAsset(val source: File, defOutputFormat: OutputFormat) {

    open val canScaleUp: Boolean = true

    val outputQualifiers: String
    val outputFilename: String
    val outputFormat: OutputFormat
    val sourceDensity: Density

    init {
        val nameWithoutExtension = source.nameWithoutExtension
        if (!nameWithoutExtension.contains('.')) {
            outputFilename = nameWithoutExtension
            outputFormat = defOutputFormat
        } else {
            outputFilename = nameWithoutExtension.substringBefore('.')
            outputFormat = OutputFormat.forExtension(nameWithoutExtension.substringAfter('.')) ?: defOutputFormat
        }
        val sourceQualifiers = source.parentFile.name
        val qualifierParts = sourceQualifiers.split('-')
        val densityQualifier = qualifierParts.firstOrNull { it.matches(densityRegex) }
        if (densityQualifier != null) {
            outputQualifiers = qualifierParts.filterNot { it.matches(densityRegex) }.joinToString("-")
            sourceDensity = Density.getEnum(densityQualifier)
        } else {
            outputQualifiers = sourceQualifiers
            sourceDensity = Density.NODPI
        }
    }

    abstract fun baseDimension(): Dimension

    abstract fun transcodeImage(output: File, format: OutputFormat, baseDimension: Dimension,
                                outputDimension: Dimension? = null)

    fun generateImages(densities: List<Density>, genDir: File) {
        val dimension = baseDimension()
        val name = "$outputFilename.${outputFormat.extension}"
        if (sourceDensity == Density.NODPI) {
            val fileName = File(createOutputDir(genDir), name)
            transcodeImage(fileName, outputFormat, dimension)
            return
        }
        densities.forEach { outDensity ->
            val fileName = File(createOutputDir(genDir, outDensity), name)
            val scaledDimension = scaledDimension(dimension.width, dimension.height, outDensity)
            if (canScaleUp || scaledDimension.width <= dimension.width) {
                transcodeImage(fileName, outputFormat, dimension, scaledDimension)
            }
        }
    }

    private fun createOutputDir(genDir: File, density: Density = Density.NODPI): File {
        if (density.dpiValue == 0) return File(genDir, outputQualifiers)
        val dir = File(genDir, "$outputQualifiers-${density.resourceValue}")
        if (!dir.exists()) {
            dir.mkdirs()
        }
        return dir
    }

    private fun scaledDimension(width: Int, height: Int, outDensity: Density): Dimension {
        val multiplier = outDensity.dpiValue / sourceDensity.dpiValue.toFloat()
        return Dimension((width * multiplier).toInt(), (height * multiplier).toInt())
    }

    companion object {

        private val densityRegex = Regex("(\\w+dpi)")

        fun get(file: File, defOutputFormat: OutputFormat): ImageAsset {
            return when (file.extension.toLowerCase()) {
                "svg" -> SvgImageAsset(file, defOutputFormat)
                "pdf" -> PdfImageAsset(file, defOutputFormat)
                "jpg", "png" -> BitmapImageAsset(file, defOutputFormat)
                else -> throw UnknownFormatConversionException("Unrecognized file ${file.name}")
            }
        }
    }
}