package org.mariotaku.imgenie

import org.gradle.api.DefaultTask
import org.mariotaku.imgenie.asset.ImageAsset
import org.mariotaku.imgenie.model.FlavorScope
import java.io.File

open class ImageAssetsGeneratorTask : DefaultTask() {

    lateinit var config: ImageAssetsConfig

    lateinit var buildVariant: FlavorScope
    lateinit var buildType: String
    lateinit var genDir: File

    init {
        doFirst {
            outputs.files.forEach { it.deleteRecursively() }
        }
        doLast {
            val defFormat = config.defFormat
            val defFormats = config.defFormats
            val densities = config.densitiesList
            inputs.files.forEach { file ->
                val fmt = defFormats.firstOrNull { (regex, _) ->
                    if (regex.matches(file.name)) return@firstOrNull true
                    return@firstOrNull false
                }?.second ?: defFormat

                val asset = ImageAsset.get(file, fmt)
                asset.generateImages(densities, genDir)
            }
        }
    }


    fun setupInputOutput() {
        val imageTrees = arrayOf(buildVariant.camelCaseName(buildType),
                buildVariant.camelCaseName(""), *buildVariant.flavors.toTypedArray(),
                buildType, "main").map {
            return@map project.file(arrayOf("src", it, "images").joinToString(File.separator))
        }.filter { it.isDirectory }.map { project.fileTree(it) }
        if (!imageTrees.isEmpty()) {
            inputs.files(*imageTrees.toTypedArray())
            outputs.dir(genDir)
        }
    }
}
