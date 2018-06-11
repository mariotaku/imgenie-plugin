package org.mariotaku.imgenie.asset

import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.rendering.ImageType
import org.apache.pdfbox.rendering.PDFRenderer
import org.imgscalr.Scalr
import org.mariotaku.imgenie.model.OutputFormat
import java.awt.Dimension
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO


class PdfImageAsset(file: File, defOutputFormat: OutputFormat) : ImageAsset(file, defOutputFormat) {

    override fun baseDimension(): Dimension {
        return PDDocument.load(source).use {
            val singlePage = it.documentCatalog.pages.single()
            val box = singlePage.mediaBox
            return@use Dimension(box.width.toInt(), box.height.toInt())
        }
    }

    override fun transcodeImage(output: File, format: OutputFormat, baseDimension: Dimension,
                                outputDimension: Dimension?) {
        PDDocument.load(source).use { doc ->
            val renderer = PDFRenderer(doc)
            renderer.isSubsamplingAllowed = true
            if (outputDimension != null) {
                val scale = outputDimension.width / baseDimension.width.toFloat() * 4f
                val renderImage = renderer.renderImage(0, scale, ImageType.ARGB)
                ImageIO.write(renderImage.scale(outputDimension.width,
                        outputDimension.height), format.formatName, output)
            } else {
                val renderImage = renderer.renderImage(0, 4f, ImageType.ARGB)
                ImageIO.write(renderImage.scale(baseDimension.width,
                        baseDimension.height), format.formatName, output)
            }
            return@use
        }
    }

    private fun BufferedImage.scale(dWidth: Int, dHeight: Int): BufferedImage {
        return Scalr.resize(this, dWidth, dHeight)
    }
}
