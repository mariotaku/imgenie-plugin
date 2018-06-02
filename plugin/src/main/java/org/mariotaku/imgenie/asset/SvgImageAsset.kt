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
import org.apache.batik.transcoder.image.JPEGTranscoder
import org.apache.batik.transcoder.image.PNGTranscoder
import org.apache.batik.util.XMLResourceDescriptor
import org.mariotaku.imgenie.MM_PER_INCH
import org.mariotaku.imgenie.batik.DensityUserAgentAdapter
import org.mariotaku.imgenie.model.OutputFormat
import java.awt.Dimension
import java.io.File


class SvgImageAsset(source: File, defOutputFormat: OutputFormat) : ImageAsset(source, defOutputFormat) {

    override val canScale: Boolean
        get() = true

    override fun baseDimension(): Dimension {
        val factory = SAXSVGDocumentFactory(XMLResourceDescriptor.getXMLParserClassName())
        val agent = DensityUserAgentAdapter(Density.MEDIUM)
        val loader = DocumentLoader(agent)
        val builder = GVTBuilder()
        val context = BridgeContext(agent, loader).apply {
            isDynamic = true
        }
        return source.inputStream().use {
            val document = factory.createDocument(source.toURI().toString(), it)
            val docElem = document.documentElement
            val viewBoxAttr = docElem.getAttribute("viewBox")
            if (!viewBoxAttr.isNullOrEmpty()) {
                val viewBox = ViewBox.parseViewBoxAttribute(docElem, viewBoxAttr, context)
                return@use Dimension((viewBox[2] - viewBox[0]).toInt(), (viewBox[3] - viewBox[1]).toInt())
            }
            val widthAttr = docElem.getAttribute("width")
            val heightAttr = docElem.getAttribute("height")
            if (!widthAttr.isNullOrEmpty() && !heightAttr.isNullOrEmpty()) {
                return@use Dimension(widthAttr.toInt(), heightAttr.toInt())
            }
            val root = builder.build(context, document)
            val bounds = root.primitiveBounds
            return@use Dimension((bounds.width * MM_PER_INCH).toInt(), (bounds.height * MM_PER_INCH).toInt())
        }
    }

    override fun transcodeImage(output: File, format: OutputFormat, dimension: Dimension?) {
        println("output: $output, dimension: $dimension")
        val t = when (format) {
            OutputFormat.JPEG -> JPEGTranscoder().apply {
                addTranscodingHint(JPEGTranscoder.KEY_QUALITY, 1f)
            }
            OutputFormat.PNG -> PNGTranscoder()
            else -> throw UnsupportedOperationException()
        }
        if (dimension != null) {
            t.addTranscodingHint(SVGAbstractTranscoder.KEY_WIDTH, dimension.width.toFloat())
            t.addTranscodingHint(SVGAbstractTranscoder.KEY_HEIGHT, dimension.height.toFloat())
        }
        source.inputStream().use { inStream ->
            output.outputStream().use { outStream ->
                t.transcode(TranscoderInput(inStream), TranscoderOutput(outStream))
                outStream.flush()
            }
        }
    }
}