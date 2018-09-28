package org.mariotaku.imgenie

import com.android.build.gradle.api.BaseVariant
import com.android.resources.Density
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.mariotaku.imgenie.asset.ImageAsset
import org.mariotaku.imgenie.model.OutputFormat

class ImageAssetsGeneratorPlugin implements Plugin<Project> {

    @Override
    void apply(Project target) {
        if (!target.hasProperty("android")) {
            throw IllegalArgumentException("Project ${target.name} is not an Android project")
        }
        ImageAssetsExtension config = target.extensions.create("imageAssets", ImageAssetsExtension)

        target.afterEvaluate { p ->
            p.android.applicationVariants.all { BaseVariant variant ->
                def taskName = "generate${variant.name.capitalize()}ImageAssets"
                def genImagesDir = new File(p.buildDir, ["generated", "images", variant.name]
                        .join(File.separator))

                def task = p.task(taskName, group: "imageassets") {
                    List<String> sourceDirNames = [variant.name, variant.buildType.name] + variant.productFlavors.collect {
                        it.name
                    } + "main"

                    def imageTrees = sourceDirNames.collect {
                        p.file(["src", it, "images"].join(File.separator))
                    }.findAll { it.isDirectory() }.collect { project.fileTree(it) }

                    if (!imageTrees.isEmpty()) {
                        inputs.files(*imageTrees.toArray())
                        outputs.dir(genImagesDir)
                    }

                    doFirst {
                        it.outputs.files.each { file ->
                            if (file.isDirectory()) {
                                file.deleteDir()
                            } else {
                                file.delete()
                            }
                        }
                    }
                    doLast {
                        it.inputs.files.forEach { file ->
                            OutputFormat fmt = config.outputFormats.find {
                                file.name.matches(it.key)
                            }?.value ?: config.outputFormat
                            Set<Density> densities = config.outputDensities

                            ImageAsset.get(file, fmt).generateImages(densities, genImagesDir)
                        }
                    }
                }

                variant.registerGeneratedResFolders(p.files(genImagesDir).builtBy(task))
            }
        }
    }


}