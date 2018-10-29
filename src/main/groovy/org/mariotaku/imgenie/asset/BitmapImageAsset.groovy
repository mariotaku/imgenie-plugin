package org.mariotaku.imgenie.asset

import org.imgscalr.Scalr
import org.mariotaku.imgenie.model.OutputFormat

import javax.imageio.ImageIO
import java.awt.*
import java.awt.color.ColorSpace
import java.awt.image.ColorConvertOp

class BitmapImageAsset extends ImageAsset {

    BitmapImageAsset(File source, OutputFormat defOutputFormat, boolean canScaleUp = false) {
        super(source, defOutputFormat)
        this.canScaleUp = canScaleUp
    }

    @Override
    Dimension baseDimension() {
        def image = ImageIO.read(source)
        return new Dimension(image.width, image.height)
    }

    @Override
    void transcodeImage(File output, OutputFormat format, Dimension baseDimension, Dimension outputDimension) {
        def image = ImageIO.read(source)
        Image scaledImage;
        if (outputDimension != null) {
            def method = antiAliasing ? Scalr.Method.BALANCED : Scalr.Method.SPEED
            scaledImage = Scalr.resize(image, method, outputDimension.width as int, outputDimension.height as int,
                    new ColorConvertOp(ColorSpace.getInstance(ColorSpace.CS_sRGB), null))
        } else {
            scaledImage = image
        }

        ImageIO.write(scaledImage, format.formatName, output)
    }
}