package org.mariotaku.imgenie.batik

import com.android.resources.Density
import org.apache.batik.bridge.UserAgentAdapter

class DensityUserAgentAdapter extends UserAgentAdapter {

    static final float MM_PER_INCH = 25.4f

    final Density density

    DensityUserAgentAdapter(Density density) {
        this.density = density
    }

    @Override
    float getPixelUnitToMillimeter(){
        return MM_PER_INCH / density.dpiValue
    }

    @Override
    float getPixelToMM() {
        return pixelUnitToMillimeter
    }
}