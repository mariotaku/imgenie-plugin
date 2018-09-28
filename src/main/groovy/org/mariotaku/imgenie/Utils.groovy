package org.mariotaku.imgenie

class Utils {
    static String extension(File file) {
        def index = file.name.lastIndexOf('.')
        if (index < 0) return ''
        return file.name.substring(index + 1)
    }

    static String nameWithoutExtension(File file) {
        def index = file.name.lastIndexOf('.')
        if (index < 0) return file.name
        return file.name.substring(0, index)
    }
}