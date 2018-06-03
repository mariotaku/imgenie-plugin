package org.mariotaku.imgenie.asset

import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.rendering.PDFRenderer
import org.mariotaku.imgenie.model.OutputFormat
import java.awt.Color
import java.awt.Dimension
import java.awt.Graphics2D
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
        PDDocument.load(source).use {
            val renderer = PDFRenderer(it)
            val image = if (outputDimension != null) {
                BufferedImage(outputDimension.width, outputDimension.height, BufferedImage.TYPE_INT_ARGB)
            } else {
                BufferedImage(baseDimension.width, baseDimension.height, BufferedImage.TYPE_INT_ARGB)
            }
            val graphics = image.graphics as Graphics2D
            graphics.background = Color(255, 255, 255, 0)
            renderer.renderPageToGraphics(0, graphics, image.width / baseDimension.width.toFloat())
            ImageIO.write(image, format.formatName, output)
            return@use
        }
    }

}
