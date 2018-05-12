package org.mariotaku.imgenie.asset

import com.twelvemonkeys.imageio.plugins.svg.SVGImageReaderSpi
import com.twelvemonkeys.imageio.plugins.svg.SVGReadParam
import org.mariotaku.imgenie.model.OutputFormat
import java.awt.Dimension
import java.awt.image.RenderedImage
import java.io.File
import javax.imageio.ImageIO

class SvgImageAsset(source: File, defOutputFormat: OutputFormat) : ImageAsset(source, defOutputFormat) {

    override val canScale: Boolean
        get() = true

    override fun readImage(dimension: Dimension?): RenderedImage? {
        val svgImageReaderSpi = SVGImageReaderSpi()
        val reader = svgImageReaderSpi.createReaderInstance()
        reader.input = ImageIO.createImageInputStream(source)
        return reader.read(0, SVGReadParam().also { param ->
            param.sourceRenderSize = dimension
        })
    }
}