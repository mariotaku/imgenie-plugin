package org.mariotaku.imgenie.model

import com.android.annotations.VisibleForTesting

import java.awt.image.BufferedImage

class NinePatch {
    List<IntRange> xScalable = new ArrayList<>()
    List<IntRange> yScalable = new ArrayList<>()

    IntRange xPadding = null
    IntRange yPadding = null

    static NinePatch parse(BufferedImage image) {
        NinePatch patch = new NinePatch()
        def alpha = image.getAlphaRaster()
        int[] tmpX = new int[alpha.width]
        int[] tmpY = new int[alpha.height]
        alpha.getPixels(0, 0, image.width, 1, tmpX)
        patch.xScalable = parseRanges(tmpX)
        alpha.getPixels(0, 0, 1, image.height, tmpY)
        patch.yScalable = parseRanges(tmpY)

        alpha.getPixels(0, image.height - 1, image.width, 1, tmpX)
        patch.xPadding = parseSingleRange(tmpX)
        alpha.getPixels(image.width - 1, 0, 1, image.height, tmpY)
        patch.yPadding = parseSingleRange(tmpY)

        return patch
    }

    NinePatch scaled(double scale) {
        NinePatch result = new NinePatch()
        result.xScalable = xScalable.collect { return scaleRange(it, scale) }
        result.yScalable = yScalable.collect { return scaleRange(it, scale) }
        result.xPadding = scaleRange(xPadding, scale)
        result.yPadding = scaleRange(yPadding, scale)
        return result
    }

    void draw(BufferedImage image) {
        def alpha = image.alphaRaster
        int[] opaque = [0xFF]
        xScalable.each { range -> range.each { x -> alpha.setPixel(x, 0, opaque) } }
        yScalable.each { range -> range.each { y -> alpha.setPixel(0, y, opaque) } }

        xPadding?.each { x -> alpha.setPixel(x, image.height - 1, opaque) }
        yPadding?.each { y -> alpha.setPixel(image.width - 1, y, opaque) }
    }

    @VisibleForTesting
    static List<IntRange> parseRanges(int[] alphas) {
        List<IntRange> result = new LinkedList<>()
        int startIdx = 0
        int lastColor = 0x0
        for (int i = 0; i < alphas.length; i++) {
            def alpha = alphas[i]
            if (lastColor != alpha) {
                if (alpha == 0xFF) {
                    // Transparent -> Black
                    startIdx = i
                } else if (alpha == 0x00) {
                    // Black -> Transparent
                    result.add(new IntRange(false, startIdx, i))
                }
            }
            lastColor = alpha
        }
        return result
    }

    @VisibleForTesting
    static IntRange parseSingleRange(int[] colors) {
        def ranges = parseRanges(colors)
        if (ranges.isEmpty()) return null
        return ranges.first()
    }

    private static IntRange scaleRange(IntRange range, double scale) {
        if (range == null) return null
        int scaledStart = 1 + ((range.from - 1) * scale).round() as int
        int scaledSize = (range.size() * scale).round() as int
        return new IntRange(false, scaledStart, scaledStart + scaledSize)
    }
}