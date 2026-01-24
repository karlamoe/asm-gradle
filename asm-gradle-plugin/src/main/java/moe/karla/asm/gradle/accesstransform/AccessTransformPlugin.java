package moe.karla.asm.gradle.accesstransform;

import lombok.val;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.type.ArtifactTypeDefinition;
import org.gradle.api.tasks.SourceSetContainer;

public class AccessTransformPlugin implements Plugin<Project> {
    @Override
    public void apply(Project project) {
        val subExtension = project.getExtensions().create("accessTransform", AccessTransformExtension.class);


        val dependencies = project.getDependencies();
        dependencies.getAttributesSchema().attribute(AccessTransformAttribute.ATTRIBUTE_KEY);


        dependencies.registerTransform(AccessTransformAction.class, setup -> {
            setup.getFrom().attribute(
                    AccessTransformAttribute.ATTRIBUTE_KEY,
                    project.getObjects().named(AccessTransformAttribute.class, AccessTransformAttribute.UNTRANSFORMED)
            );
            setup.getTo().attribute(
                    AccessTransformAttribute.ATTRIBUTE_KEY,
                    project.getObjects().named(AccessTransformAttribute.class, AccessTransformAttribute.TRANSFORMED)
            );

            setup.parameters(at -> {
                at.getAtFiles().from(subExtension.getAtFiles());
            });
        });


        project.getPluginManager().withPlugin("jvm-ecosystem", $ -> {
            dependencies.getArtifactTypes().named(ArtifactTypeDefinition.JAR_TYPE, define -> {
                define.getAttributes().attribute(
                        AccessTransformAttribute.ATTRIBUTE_KEY,
                        project.getObjects().named(AccessTransformAttribute.class, AccessTransformAttribute.UNTRANSFORMED)
                );
            });

            project.afterEvaluate(p -> {
                project.getExtensions().configure(SourceSetContainer.class, sourceSets -> {
                    sourceSets.configureEach(src -> {
                        if (subExtension.applyToCompileClasspath) {
                            project.getConfigurations().named(src.getCompileClasspathConfigurationName())
                                    .configure(subExtension::applyToConfiguration);
                        }
                        if (subExtension.applyToRuntimeClasspath) {
                            project.getConfigurations().named(src.getRuntimeClasspathConfigurationName())
                                    .configure(subExtension::applyToConfiguration);
                        }
                    });
                });
            });
        });
    }
}
