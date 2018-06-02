package org.mariotaku.imgenie.asset

import com.android.resources.Density
import org.mariotaku.imgenie.ImageAssetsConfig
import org.mariotaku.imgenie.model.OutputFormat
import java.awt.Dimension
import java.io.File
import java.util.*

abstract class ImageAsset(val source: File, val defOutputFormat: OutputFormat) {

    open val canScale: Boolean = false

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
            outputFormat = try {
                OutputFormat.valueOf(nameWithoutExtension.substringAfter('.').toUpperCase())
            } catch (e: IllegalArgumentException) {
                defOutputFormat
            }
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

    fun generateImages(config: ImageAssetsConfig, genDir: File) {
        val dimension = baseDimension()
        val name = "$outputFilename.${outputFormat.extension}"
        if (!canScale || sourceDensity == Density.NODPI) {
            val fileName = File(getOutputDir(genDir), name)
            transcodeImage(fileName, outputFormat, dimension)
            return
        }
        config.outputDensities.forEach { outDensity ->
            val fileName = File(getOutputDir(genDir, outDensity), name)
            transcodeImage(fileName, outputFormat, dimension, scaledDimension(dimension.width,
                    dimension.height, outDensity))
        }
    }

    fun getOutputFiles(config: ImageAssetsConfig, genDir: File): List<File> {
        val name = "$outputFilename.${outputFormat.extension}"
        if (sourceDensity == Density.NODPI) {
            return listOf(File(getOutputDir(genDir), name))
        }
        return config.outputDensities.map { outDensity ->
            File(getOutputDir(genDir, outDensity), name)
        }
    }

    private fun getOutputDir(genDir: File, density: Density = Density.NODPI): File {
        if (density.dpiValue == 0) return File(genDir, outputQualifiers)
        return File(genDir, "$outputQualifiers-${density.resourceValue}")
    }

    private fun scaledDimension(width: Int, height: Int, outDensity: Density): Dimension {
        val multiplier = outDensity.dpiValue / sourceDensity.dpiValue.toFloat()
        return Dimension((width * multiplier).toInt(), (height * multiplier).toInt())
    }

    companion object {

        private val densityRegex = Regex("(\\w+dpi)")

        fun get(file: File, defOutputFormat: OutputFormat): ImageAsset {
            when (file.extension.toLowerCase()) {
                "svg" -> return SvgImageAsset(file, defOutputFormat)
                "pdf" -> return PdfImageAsset(file, defOutputFormat)
                else -> throw UnknownFormatConversionException("Unrecognized file ${file.name}")
            }
        }
    }
}