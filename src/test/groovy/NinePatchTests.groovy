import org.imgscalr.Scalr
import org.mariotaku.imgenie.model.NinePatch
import spock.lang.Specification

import javax.imageio.ImageIO
import java.awt.*

class NinePatchTests extends Specification {

    def "nine patch bitmap parses correctly"() {
        when:
        def img = ImageIO.read(NinePatchTests.classLoader.getResource("bg_3d_object.9.png"))
        def ninepatch = NinePatch.parse(img)
        def content = img.getSubimage(1, 1, img.width - 2, img.height - 2)
        def scaledContent = Scalr.resize(content, Scalr.Method.SPEED, content.width * 2, content.height * 2)
        def resized = Scalr.pad(scaledContent, 1, new Color(0, 0, 0, 0))
        ninepatch.scaled(2).draw(resized)

        then:
        assert ninepatch.xScalable[0] == new IntRange(false, 3, 15)
        assert ninepatch.yScalable[0] == new IntRange(false, 3, 13)
        assert ninepatch.xPadding == new IntRange(false, 2, 16)
        assert ninepatch.yPadding == new IntRange(false, 2, 14)
        assert resized.width == 34
        assert resized.height == 30
    }
}
