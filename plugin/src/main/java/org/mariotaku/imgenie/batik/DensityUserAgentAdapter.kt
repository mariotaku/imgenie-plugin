package org.mariotaku.imgenie.batik

import com.android.resources.Density
import org.apache.batik.bridge.UserAgentAdapter
import org.mariotaku.imgenie.MM_PER_INCH

class DensityUserAgentAdapter(val density: Density) : UserAgentAdapter() {

    override fun getPixelUnitToMillimeter(): Float {
        return MM_PER_INCH / density.dpiValue
    }

    override fun getPixelToMM(): Float {
        return pixelUnitToMillimeter
    }

}