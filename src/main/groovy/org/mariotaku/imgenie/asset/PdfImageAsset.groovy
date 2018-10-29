package org.mariotaku.imgenie.asset

import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.rendering.ImageType
import org.apache.pdfbox.rendering.PDFRenderer
import org.imgscalr.Scalr
import org.mariotaku.imgenie.model.OutputFormat

import javax.imageio.ImageIO
import java.awt.*

class PdfImageAsset extends ImageAsset {

    PdfImageAsset(File source, OutputFormat defOutputFormat) {
        super(source, defOutputFormat)
    }

    @Override
    Dimension baseDimension() {
        PDDocument.load(source).withCloseable {
            def singlePage = it.documentCatalog.pages.first()
            def box = singlePage.mediaBox
            return new Dimension(box.width as int, box.height as int)
        }
    }

    @Override
    void transcodeImage(File output, OutputFormat format, Dimension baseDimension, Dimension outputDimension) {
        PDDocument.load(source).withCloseable { doc ->
            PDFRenderer renderer = new PDFRenderer(doc)
            renderer.subsamplingAllowed = true
            if (outputDimension != null) {
                def scale = (outputDimension.width / baseDimension.width * 4f) as float
                def renderImage = renderer.renderImage(0, scale, ImageType.ARGB)
                ImageIO.write(Scalr.resize(renderImage, outputDimension.width as int,
                        outputDimension.height as int, Scalr.OP_ANTIALIAS), format.formatName, output)
            } else {
                def renderImage = renderer.renderImage(0, 4f, ImageType.ARGB)
                ImageIO.write(Scalr.resize(renderImage, baseDimension.width as int,
                        baseDimension.height as int, Scalr.OP_ANTIALIAS), format.formatName, output)
            }
        }
    }


}