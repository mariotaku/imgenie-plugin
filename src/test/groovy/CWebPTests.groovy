import org.mariotaku.imgenie.util.CWebP
import spock.lang.Specification

import javax.imageio.ImageIO

class CWebPTests extends Specification {

    def "decode webp image"() {
        when:
        def input = CWebPTests.classLoader.getResource("bg_login_entrance.webp").newInputStream()
        def output = new ByteArrayOutputStream()
        CWebP.decode(input, output)

        then:
        def img = ImageIO.read(new ByteArrayInputStream(output.toByteArray()))
        System.identityHashCode(img)
    }

}
