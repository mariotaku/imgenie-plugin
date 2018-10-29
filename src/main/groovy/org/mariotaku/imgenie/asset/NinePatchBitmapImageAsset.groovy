package org.mariotaku.imgenie.asset

import org.imgscalr.Scalr
import org.mariotaku.imgenie.model.NinePatch
import org.mariotaku.imgenie.model.OutputFormat

import javax.imageio.ImageIO
import java.awt.*
import java.awt.color.ColorSpace
import java.awt.image.BufferedImage
import java.awt.image.ColorConvertOp

class NinePatchBitmapImageAsset extends ImageAsset {

    NinePatchBitmapImageAsset(File source, OutputFormat defOutputFormat, boolean canScaleUp = false) {
        super(source, defOutputFormat)
        this.canScaleUp = canScaleUp
    }

    @Override
    Dimension baseDimension() {
        def image = ImageIO.read(source)
        return new Dimension(image.width - 2, image.height - 2)
    }

    @Override
    void transcodeImage(File output, OutputFormat format, Dimension baseDimension, Dimension outputDimension) {
        BufferedImage image = ImageIO.read(source)
        BufferedImage content = image.getSubimage(1, 1, baseDimension.width as int, baseDimension.height as int)
        NinePatch patches = NinePatch.parse(image).scaled(outputDimension.width / baseDimension.width)
        Image scaledContent
        if (outputDimension != null) {
            def method = antiAliasing ? Scalr.Method.BALANCED : Scalr.Method.SPEED
            scaledContent = Scalr.resize(content, method, outputDimension.width as int, outputDimension.height as int,
                    new ColorConvertOp(ColorSpace.getInstance(ColorSpace.CS_sRGB), null))
        } else {
            scaledContent = content
        }
        BufferedImage patched = Scalr.pad(scaledContent, 1, new Color(0, 0, 0, 0))
        patches.draw(patched)
        ImageIO.write(patched, format.formatName, output)
    }
}