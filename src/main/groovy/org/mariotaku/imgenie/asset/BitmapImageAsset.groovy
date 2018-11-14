package org.mariotaku.imgenie.asset

import org.imgscalr.Scalr
import org.mariotaku.imgenie.model.OutputFormat
import org.mariotaku.imgenie.util.CWebP

import javax.imageio.ImageIO
import java.awt.*
import java.awt.color.ColorSpace
import java.awt.image.ColorConvertOp

class BitmapImageAsset extends ImageAsset {

    public final OutputFormat assetFormat;

    BitmapImageAsset(File source, OutputFormat assetFormat, OutputFormat defOutputFormat, boolean canScaleUp = false) {
        super(source, defOutputFormat)
        this.assetFormat = assetFormat
        this.canScaleUp = canScaleUp
    }

    @Override
    Dimension baseDimension() {
        if (assetFormat == OutputFormat.WEBP) {
            def tmp = new ByteArrayOutputStream()
            CWebP.decode(source.newInputStream(), tmp)
            def image = ImageIO.read(new ByteArrayInputStream(tmp.toByteArray()))
            return new Dimension(image.width, image.height)
        }
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

        if (format == OutputFormat.WEBP) {
            def tmp = new ByteArrayOutputStream()
            ImageIO.write(scaledImage, "PNG", tmp)
            CWebP.encode(new ByteArrayInputStream(tmp.toByteArray()), output.newOutputStream(), quality)
        } else {
            ImageIO.write(scaledImage, format.formatName, output)
        }
    }
}