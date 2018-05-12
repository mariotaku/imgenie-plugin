package org.mariotaku.imgenie

import com.android.build.gradle.AndroidConfig
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.TaskContainer
import org.mariotaku.imgenie.model.FlavorScope

class ImageAssetsGeneratorPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        if (!project.hasProperty("android")) {
            throw IllegalArgumentException("Project ${project.name} is not an Android project")
        }
        val config = project.extensions.create("imageAssets",
                ImageAssetsConfig::class.java, project.objects)

        project.afterEvaluate {
            setupTasks(it, config)
        }
    }

    private fun setupTasks(project: Project, config: ImageAssetsConfig) {
        val android = project.property("android") as AndroidConfig
        val buildTypeNames = android.buildTypes.map { type -> type.name }
        val buildVariants = android.buildVariants

        buildVariants.forEach { buildVariant ->
            buildTypeNames.forEach { buildTypeName ->
                val taskName = buildVariant.taskName("generate", buildTypeName, "ImageAssets")
                val task = project.tasks.create(taskName, ImageAssetsGeneratorTask::class.java) {
                    it.group = "imageassets"
                    it.config = config
                    it.buildVariant = buildVariant
                    it.buildType = buildTypeName

                    it.setupInputOutput()
                }

                project.tasks.injectDependency(buildVariant.taskName("generate", buildTypeName, "Resources"), task)
            }
        }
    }

    companion object {

        fun TaskContainer.injectDependency(path: String, dependsOn: Task) {
            findByPath(path)?.dependsOn(dependsOn)
        }

        val AndroidConfig.buildVariants: List<FlavorScope>
            get() {
                val dimensions = flavorDimensionList?.takeIf(Collection<*>::isNotEmpty)
                        ?: return listOf(FlavorScope())
                val flavors = productFlavors?.takeIf(Collection<*>::isNotEmpty)
                        ?: return listOf(FlavorScope())
                return dimensions.map { dimension ->
                    flavors.filter { flavor ->
                        flavor.dimension == dimension
                    }.map { flavor ->
                        flavor.name
                    }
                }.combinations().map(::FlavorScope)
            }
    }

}
