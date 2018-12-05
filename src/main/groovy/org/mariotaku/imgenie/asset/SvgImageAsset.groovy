package org.mariotaku.imgenie.asset

import com.android.resources.Density
import org.apache.batik.anim.dom.SAXSVGDocumentFactory
import org.apache.batik.bridge.BridgeContext
import org.apache.batik.bridge.DocumentLoader
import org.apache.batik.bridge.GVTBuilder
import org.apache.batik.bridge.ViewBox
import org.apache.batik.transcoder.SVGAbstractTranscoder
import org.apache.batik.transcoder.TranscoderInput
import org.apache.batik.transcoder.TranscoderOutput
import org.apache.batik.transcoder.image.ImageTranscoder
import org.apache.batik.transcoder.image.JPEGTranscoder
import org.apache.batik.transcoder.image.PNGTranscoder
import org.apache.batik.util.XMLResourceDescriptor
import org.mariotaku.imgenie.batik.DensityUserAgentAdapter
import org.mariotaku.imgenie.batik.WEBPTranscoder
import org.mariotaku.imgenie.model.OutputFormat

import java.awt.*

class SvgImageAsset extends ImageAsset {
    SvgImageAsset(File source, OutputFormat defOutputFormat) {
        super(source, defOutputFormat)
    }

    @Override
    Dimension baseDimension() {
        def factory = new SAXSVGDocumentFactory(XMLResourceDescriptor.getXMLParserClassName())
        def agent = new DensityUserAgentAdapter(Density.MEDIUM)
        def loader = new DocumentLoader(agent)
        def builder = new GVTBuilder()
        def context = new BridgeContext(agent, loader)
        context.dynamic = true
        return source.withInputStream {
            def document = factory.createDocument(source.toURI().toString(), it)
            def docElem = document.documentElement
            def viewBoxAttr = docElem.getAttribute("viewBox")
            if (!viewBoxAttr) {
                def viewBox = ViewBox.parseViewBoxAttribute(docElem, viewBoxAttr, context)
                return new Dimension((viewBox[2] - viewBox[0]).toInt(), (viewBox[3] - viewBox[1]).toInt())
            }
            String widthAttr = docElem.getAttribute("width")
            String heightAttr = docElem.getAttribute("height")
            if (widthAttr && heightAttr) {
                return new Dimension(parseDimension(widthAttr), parseDimension(heightAttr))
            }
            def root = builder.build(context, document)
            def bounds = root.primitiveBounds
            return new Dimension((bounds.width * DensityUserAgentAdapter.MM_PER_INCH) as int,
                    (bounds.height * DensityUserAgentAdapter.MM_PER_INCH) as int)
        }
    }

    @Override
    void transcodeImage(File output, OutputFormat format, Dimension baseDimension, Dimension outputDimension) {
        ImageTranscoder t
        switch (format) {
            case OutputFormat.JPEG:
                t = new JPEGTranscoder()
                t.addTranscodingHint(JPEGTranscoder.KEY_QUALITY, quality / 100f)
                break
            case OutputFormat.PNG:
                t = new PNGTranscoder()
                break
            case OutputFormat.WEBP:
                t = new WEBPTranscoder()
                t.addTranscodingHint(WEBPTranscoder.KEY_QUALITY, quality)
                break
            default: throw new UnsupportedOperationException("Unsupported output format $format")
        }
        if (outputDimension != null) {
            t.addTranscodingHint(SVGAbstractTranscoder.KEY_WIDTH, outputDimension.width as Float)
            t.addTranscodingHint(SVGAbstractTranscoder.KEY_HEIGHT, outputDimension.height as Float)
        }
        source.withInputStream { inStream ->
            output.withOutputStream { outStream ->
                t.transcode(new TranscoderInput(inStream), new TranscoderOutput(outStream))
                outStream.flush()
            }
        }
    }

    private static int parseDimension(String str) {
        if (str.endsWith("px")) {
            return Integer.parseInt(str.substring(0, str.length() - 2))
        }
        return Integer.parseInt(str)
    }
}