package moe.karla.asm.gradle;

import moe.karla.asm.gradle.internal.AsmProperties;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.plugins.JavaPluginExtension;
import org.gradle.api.tasks.SourceSetContainer;

import java.util.ArrayList;

public class AsmGradlePlugin implements Plugin<Project> {
    @Override
    public void apply(Project target) {
        target.getPluginManager().withPlugin("java", $ -> {
            setupWithJava(target);
        });
    }

    private void setupWithJava(Project project) {
        var java = project.getExtensions().getByType(JavaPluginExtension.class);
        var sourceSets = project.getExtensions().getByType(SourceSetContainer.class);

        var asmDependencies = project.getConfigurations().register("asm", conf -> {
            conf.setCanBeConsumed(false);
            conf.setCanBeResolved(false);

            conf.getDependencies().add(
                    project.getDependencies().create("moe.karla.asm:asm-transformer-api:" + AsmProperties.VERSION)
            );
        });
        var srcAsm = sourceSets.register("asm", asm -> {
            asm.getResources().setSrcDirs(new ArrayList<>());
            project.getConfigurations().named(asm.getCompileClasspathConfigurationName()).configure(cc -> {
                cc.extendsFrom(asmDependencies.get());
            });
        });


    }
}
