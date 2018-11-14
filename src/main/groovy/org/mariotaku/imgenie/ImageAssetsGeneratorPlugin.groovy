package org.mariotaku.imgenie

import com.android.build.gradle.api.BaseVariant
import com.android.resources.Density
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.file.ConfigurableFileTree
import org.mariotaku.imgenie.asset.ImageAsset
import org.mariotaku.imgenie.model.OutputFormat

class ImageAssetsGeneratorPlugin implements Plugin<Project> {

    @Override
    void apply(Project target) {
        if (!target.hasProperty("android")) {
            throw IllegalArgumentException("Project ${target.name} is not an Android project")
        }
        ImageAssetsExtension config = target.extensions.create("imageAssets", ImageAssetsExtension)

        def genMainImagesDir = new File(target.buildDir, ["generated", "images", "main"]
                .join(File.separator))

        if (config.allowOverride) {
            target.android.sourceSets.main.res.srcDir(genMainImagesDir)
        }

        target.afterEvaluate { p ->
            def variants
            if (p.plugins.hasPlugin("com.android.application")) {
                variants = p.android.applicationVariants
            } else if (p.plugins.hasPlugin("com.android.library")) {
                variants = p.android.libraryVariants
            } else throw new UnsupportedOperationException("Unsupported project type")
            variants.all { BaseVariant variant ->
                def taskName = "generate${variant.name.capitalize()}ImageAssets"
                def genImagesDir = new File(p.buildDir, ["generated", "images", variant.dirName]
                        .join(File.separator))

                def task = p.task(taskName, group: "imageassets") { Task t ->
                    List<String> sourceDirNames = [variant.name, variant.buildType.name] + variant.productFlavors.collect {
                        it.name
                    }

                    def mainImagesDir = p.file(["src", "main", "images"].join(File.separator))
                    ConfigurableFileTree[] imageTrees = (sourceDirNames.collect {
                        p.file(["src", it, "images"].join(File.separator))
                    } + mainImagesDir).findAll { it.isDirectory() }.collect { p.fileTree(it) }

                    if (imageTrees) {
                        t.inputs.files(imageTrees)
                        t.outputs.dirs(genImagesDir, genMainImagesDir)
                    }

                    t.doFirst {
                        it.outputs.files.each { file ->
                            if (file.isDirectory()) {
                                file.deleteDir()
                            } else {
                                file.delete()
                            }
                        }
                    }
                    t.doLast {
                        it.inputs.files.forEach { File file ->
                            OutputFormat fmt = config.outputFormats.find {
                                file.name.matches(it.key)
                            }?.value ?: config.outputFormat
                            boolean scaleUpBitmap = config.scaleUpBitmaps.find {
                                file.name.matches(it.key)
                            }?.value ?: config.scaleUpBitmap
                            boolean antiAliasing = config.antiAliasingMap.find {
                                file.name.matches(it.key)
                            }?.value ?: config.antiAliasing
                            int quality = config.outputQualities.find {
                                file.name.matches(it.key)
                            }?.value ?: config.outputQuality
                            Set<Density> densities = config.outputDensities

                            def asset = ImageAsset.get(file, fmt, scaleUpBitmap)
                            asset.antiAliasing = antiAliasing
                            asset.quality = quality
                            if (Utils.sameParent(file, mainImagesDir)) {
                                asset.generateImages(densities, genMainImagesDir)
                            } else {
                                asset.generateImages(densities, genImagesDir)
                            }
                        }
                    }
                }
                if (config.allowOverride) {
                    p.android.sourceSets.maybeCreate(variant.name).res.srcDir(genImagesDir)
                    variant.mergeResources.dependsOn(task)
                } else {
                    variant.registerGeneratedResFolders(p.files(genImagesDir).builtBy(task))
                }
            }
        }
    }


}