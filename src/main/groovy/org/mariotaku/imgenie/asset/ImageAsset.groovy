package org.mariotaku.imgenie.asset

import com.android.resources.Density
import org.mariotaku.imgenie.Utils
import org.mariotaku.imgenie.model.OutputFormat

import java.awt.*
import java.util.regex.Pattern

abstract class ImageAsset {

    final File source;
    final String outputQualifiers
    final String outputFilename
    final OutputFormat outputFormat
    final Density sourceDensity

    boolean canScaleUp = true

    ImageAsset(File source, OutputFormat defOutputFormat) {
        this.source = source
        def nameWithoutExtension = Utils.nameWithoutExtension(source)
        if (!nameWithoutExtension.contains('.')) {
            outputFilename = nameWithoutExtension
            outputFormat = defOutputFormat
        } else {
            outputFilename = nameWithoutExtension.substringBefore('.')
            outputFormat = OutputFormat.forExtension(nameWithoutExtension.substringAfter('.')) ?: defOutputFormat
        }
        def sourceQualifiers = source.parentFile.name
        def qualifierParts = sourceQualifiers.split('-')
        def densityQualifier = qualifierParts.find { it.matches(densityRegex) }
        if (densityQualifier != null) {
            outputQualifiers = qualifierParts.findAll {
                !it.matches(densityRegex)
            }.join("-")
            sourceDensity = Density.getEnum(densityQualifier)
        } else {
            outputQualifiers = sourceQualifiers
            sourceDensity = Density.NODPI
        }
    }

    abstract Dimension baseDimension()

    abstract void transcodeImage(File output, OutputFormat format, Dimension baseDimension,
                                 Dimension outputDimension = null)

    final void generateImages(Collection<Density> densities, File genDir) {
        def dimension = baseDimension()
        def name = "$outputFilename.${outputFormat.extension}"
        if (sourceDensity == Density.NODPI) {
            def fileName = new File(createOutputDir(genDir), name)
            if (fileName.exists()) return
            if (!fileName.parentFile.exists()) {
                fileName.parentFile.mkdirs()
            }
            transcodeImage(fileName, outputFormat, dimension)
            return
        }
        for (density in densities) {
            def fileName = new File(createOutputDir(genDir, density), name)
            if (fileName.exists()) continue
            if (!fileName.parentFile.exists()) {
                fileName.parentFile.mkdirs()
            }
            def scaledDimension = scaledDimension(dimension.width, dimension.height, density)
            if (canScaleUp || scaledDimension.width <= dimension.width) {
                transcodeImage(fileName, outputFormat, dimension, scaledDimension)
            }
        }
    }

    private File createOutputDir(File genDir, Density density = Density.NODPI) {
        if (density.dpiValue == 0) return new File(genDir, outputQualifiers)
        def dir = new File(genDir, "$outputQualifiers-${density.resourceValue}")
        if (!dir.exists()) {
            dir.mkdirs()
        }
        return dir
    }

    private Dimension scaledDimension(double width, double height, Density density) {
        def multiplier = density.dpiValue / (sourceDensity.dpiValue as double)
        return new Dimension((width * multiplier) as int, (height * multiplier) as int)
    }

    static densityRegex = Pattern.compile("(\\w+dpi)")

    static ImageAsset get(File file, OutputFormat defOutputFormat) {
        switch (Utils.extension(file).toLowerCase()) {
            case "svg": return new SvgImageAsset(file, defOutputFormat)
            case "pdf": return new PdfImageAsset(file, defOutputFormat)
            case "jpg": case "png": return new BitmapImageAsset(file, defOutputFormat)
            default: throw UnknownFormatConversionException("Unrecognized file ${file.name}")
        }
    }
}