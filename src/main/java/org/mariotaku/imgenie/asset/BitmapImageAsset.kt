package org.mariotaku.imgenie.asset

import org.mariotaku.imgenie.model.OutputFormat
import org.mariotaku.imgenie.scale
import java.awt.Dimension
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO


class BitmapImageAsset(source: File, defOutputFormat: OutputFormat) : ImageAsset(source, defOutputFormat) {

    override fun baseDimension(): Dimension {
        val image = ImageIO.read(source)
        return Dimension(image.width, image.height)
    }

    override fun transcodeImage(output: File, format: OutputFormat, baseDimension: Dimension, outputDimension: Dimension?) {
        val image = ImageIO.read(source)
        val scaledImage = if (outputDimension != null) {
            image.scale(outputDimension.width, outputDimension.height, BufferedImage.TYPE_INT_ARGB)
        } else image

        ImageIO.write(scaledImage, format.formatName, output)
    }
}