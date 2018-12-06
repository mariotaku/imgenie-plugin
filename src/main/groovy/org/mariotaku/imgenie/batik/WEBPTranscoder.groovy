package org.mariotaku.imgenie.batik

import org.apache.batik.transcoder.TranscoderException
import org.apache.batik.transcoder.TranscoderOutput
import org.apache.batik.transcoder.TranscodingHints
import org.apache.batik.transcoder.image.ImageTranscoder
import org.mariotaku.imgenie.util.CWebP

import javax.imageio.ImageIO
import java.awt.image.BufferedImage

class WEBPTranscoder extends ImageTranscoder {

    public static final TranscodingHints.Key KEY_QUALITY = new QualityKey()

    /**
     * A transcoding Key represented the WEBP image quality.
     */
    private static class QualityKey extends TranscodingHints.Key {
        boolean isCompatibleValue(Object v) {
            if (v instanceof Integer) {
                int q = v.intValue()
                return (q > 0 && q <= 100)
            } else {
                return false
            }
        }
    }

    @Override
    BufferedImage createImage(int width, int height) {
        return new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
    }

    @Override
    void writeImage(BufferedImage img, TranscoderOutput output) throws TranscoderException {
        def tmp = new ByteArrayOutputStream()
        ImageIO.write(img, "PNG", tmp)
        def quality = hints.containsKey(KEY_QUALITY) ? ((Integer) hints.get(KEY_QUALITY)).intValue() : 100
        try {
            CWebP.encode(new ByteArrayInputStream(tmp.toByteArray()), output.outputStream, quality)
        } catch (IOException e) {
            throw new TranscoderException(e)
        }
    }
}