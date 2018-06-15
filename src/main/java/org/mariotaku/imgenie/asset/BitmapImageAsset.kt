package org.mariotaku.imgenie.asset

import org.imgscalr.Scalr
import org.mariotaku.imgenie.model.OutputFormat
import java.awt.Dimension
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
            Scalr.resize(image, outputDimension.width, outputDimension.height)
        } else image

        ImageIO.write(scaledImage, format.formatName, output)
    }
}