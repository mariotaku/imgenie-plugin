package org.mariotaku.imgenie.asset

import com.android.resources.Density
import org.mariotaku.imgenie.ImageAssetsConfig
import org.mariotaku.imgenie.model.OutputFormat
import java.awt.Dimension
import java.awt.image.RenderedImage
import java.io.File
import java.io.IOException
import java.util.*
import javax.imageio.ImageIO

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

    open fun readImage(dimension: Dimension? = null): RenderedImage? {
        return ImageIO.read(source)
    }

    fun generateImages(config: ImageAssetsConfig, genDir: File) {
        val baseImage = readImage() ?: throw IOException("Unsupported file $source")
        val name = "$outputFilename.${outputFormat.extension}"
        if (!canScale || sourceDensity == Density.NODPI) {
            val fileName = File(getOutputDir(genDir), name)
            ImageIO.write(baseImage, outputFormat.formatName, fileName)
            return
        }
        config.outputDensities.forEach { outDensity ->
            val fileName = File(getOutputDir(genDir, outDensity), name)
            val scaledImage = if (outDensity == sourceDensity) baseImage else
                readImage(scaledDimension(baseImage.width, baseImage.height, outDensity))
            ImageIO.write(scaledImage, outputFormat.formatName, fileName)
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
                "svg" -> {
                    return SvgImageAsset(file, defOutputFormat)
                }
                else -> throw UnknownFormatConversionException("Unrecognized file ${file.name}")
            }
        }
    }
}