package org.mariotaku.imgenie.asset

import org.imgscalr.Scalr
import org.mariotaku.imgenie.model.OutputFormat
import java.awt.Dimension
import java.awt.color.ColorSpace
import java.awt.image.ColorConvertOp
import java.io.File
import javax.imageio.ImageIO


class BitmapImageAsset(source: File, defOutputFormat: OutputFormat) : ImageAsset(source, defOutputFormat) {

    override val canScaleUp: Boolean
        get() = false

    override fun baseDimension(): Dimension {
        val image = ImageIO.read(source)
        return Dimension(image.width, image.height)
    }

    override fun transcodeImage(output: File, format: OutputFormat, baseDimension: Dimension, outputDimension: Dimension?) {
        val image = ImageIO.read(source)
        val scaledImage = if (outputDimension != null) {
            Scalr.resize(image, outputDimension.width, outputDimension.height,
                    ColorConvertOp(ColorSpace.getInstance(ColorSpace.CS_sRGB), null))
        } else image

        ImageIO.write(scaledImage, format.formatName, output)
    }
}