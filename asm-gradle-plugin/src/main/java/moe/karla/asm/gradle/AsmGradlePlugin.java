package moe.karla.asm.gradle;

import moe.karla.asm.gradle.internal.AsmProperties;
import moe.karla.asm.gradle.tasks.RunGeneratorTask;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.attributes.Usage;
import org.gradle.api.plugins.JavaPluginExtension;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.SourceSetContainer;

import java.util.ArrayList;

public class AsmGradlePlugin implements Plugin<Project> {
    @Override
    public void apply(Project target) {
        var ext = target.getExtensions().create("asm", AsmExtension.class);
        ext.getGeneratedClassOutputDirectory().convention(
                target.getLayout().getBuildDirectory().dir("generated/asm/classes")
        );
        ext.getGeneratedClassSourceDirectory().convention(
                target.getLayout().getBuildDirectory().dir("generated/asm/sources")
        );


        target.getPluginManager().withPlugin("java", $ -> {
            setupWithJava(target, ext);
        });
    }

    private boolean isInSync(Project project) {
        var prop = project.getProviders().systemProperty("idea.sync.active");
        return prop.isPresent() && "true".equals(prop.get());
    }

    private void setupWithJava(Project project, AsmExtension extension) {
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


        var asmRuntime = project.getConfigurations().register("asmRuntime", conf -> {
            conf.setCanBeConsumed(false);
            conf.getAttributes().attribute(
                    Usage.USAGE_ATTRIBUTE,
                    project.getObjects().named(Usage.class, Usage.JAVA_RUNTIME)
            );

            conf.extendsFrom(asmDependencies.get());

            conf.getDependencies().add(
                    project.getDependencies().create("moe.karla.asm:asm-transformer-launcher:" + AsmProperties.VERSION)
            );
            conf.getDependencies().add(
                    project.getDependencies().create(srcAsm.get().getOutput())
            );
        });


        var runTransformer = project.getTasks().register("runTransformer", RunGeneratorTask.class);
        runTransformer.configure(task -> {
            task.getGeneratedClassOutputDirectory().set(extension.getGeneratedClassOutputDirectory());
            task.getGeneratedClassSourceDirectory().set(extension.getGeneratedClassSourceDirectory());
            var classpath = project.getObjects().fileCollection();
            task.setClasspath(classpath);

            classpath.from(asmRuntime);
            classpath.from(srcAsm.map(SourceSet::getOutput));

            task.getGeneratorClasspath().from(
                    srcAsm.map(SourceSet::getOutput)
            );

            task.getMainClass().set("moe.karla.asm.transformer.launcher.TransformerLauncher");

            task.setup();
        });


        if (isInSync(project)) {
            sourceSets.named("main").configure(main -> {
                main.getJava().srcDir(extension.getGeneratedClassSourceDirectory());
            });
        }

        sourceSets.named("main").configure(main -> {
            main.compiledBy(runTransformer);
            var extraClasspath = project.getObjects().fileCollection();

            extraClasspath.from(runTransformer.map(RunGeneratorTask::getGeneratedClassOutputDirectory));
            extraClasspath.builtBy(runTransformer);

            main.getOutput().dir(extraClasspath);


            project.getDependencies().add(
                    main.getCompileOnlyConfigurationName(),
                    extraClasspath
            );

        });
    }
}
