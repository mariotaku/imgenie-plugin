package org.mariotaku.imgenie

import java.awt.geom.AffineTransform
import java.awt.image.BufferedImage


fun BufferedImage.scale(imageType: Int, width: Int, height: Int): BufferedImage {
    val dbi = BufferedImage(width, height, imageType)
    val g = dbi.createGraphics()
    val at = AffineTransform.getScaleInstance(width / width.toDouble(), height / height.toDouble())
    g.drawRenderedImage(this, at)
    return dbi
}