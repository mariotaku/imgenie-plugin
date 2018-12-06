package org.mariotaku.imgenie.util

class CWebP {
    static void encode(InputStream input, OutputStream output, int quality) {
        def process = new ProcessBuilder("cwebp", "-q", quality.toString(), "-o", "-", "--", "-").start()
        process.outputStream << input
        process.outputStream.flush()
        process.outputStream.close()
        output << process.inputStream
        output.flush()
        output.close()
        def retCode = process.waitFor()
        if (retCode != 0) throw new IOException("command returned $retCode")
    }

    static void decode(InputStream input, OutputStream output) {
        def process = new ProcessBuilder("dwebp", "-o", "-", "--", "-").start()
        process.outputStream << input
        process.outputStream.flush()
        process.outputStream.close()
        output << process.inputStream
        output.flush()
        output.close()
        def retCode = process.waitFor()
        if (retCode != 0) throw new IOException("command returned $retCode")
    }
}
