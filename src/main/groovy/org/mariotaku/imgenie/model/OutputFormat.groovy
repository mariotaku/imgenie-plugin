package org.mariotaku.imgenie.model

enum OutputFormat {
    PNG("png", "png"),
    JPEG("jpg", "jpeg"),
    WEBP("webp", "webp");

    final String extension;
    final String formatName;

    OutputFormat(String extension, String formatName) {
        this.extension = extension;
        this.formatName = formatName;
    }

    static OutputFormat forExtension(String extension) {
        return values().find {
            it.extension.equalsIgnoreCase(extension)
        }
    }
}
