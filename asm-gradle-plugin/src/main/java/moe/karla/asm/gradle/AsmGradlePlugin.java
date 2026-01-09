package moe.karla.asm.gradle;

import lombok.val;
import moe.karla.asm.gradle.internal.AsmProperties;
import moe.karla.asm.gradle.tasks.RunGeneratorTask;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.attributes.Usage;
import org.gradle.api.attributes.java.TargetJvmVersion;
import org.gradle.api.plugins.JavaPluginExtension;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.SourceSetContainer;
import org.gradle.api.tasks.compile.JavaCompile;
import org.gradle.jvm.toolchain.JavaToolchainService;

import java.util.ArrayList;

public class AsmGradlePlugin implements Plugin<Project> {
    @Override
    public void apply(Project target) {
        val ext = target.getExtensions().create("asm", AsmExtension.class);
        ext.getGeneratedClassOutputDirectory().convention(
                target.getLayout().getBuildDirectory().dir("generated/asm/classes")
        );
        ext.getGeneratedClassSourceDirectory().convention(
                target.getLayout().getBuildDirectory().dir("generated/asm/sources")
        );


        target.getPluginManager().apply("java");
        setupWithJava(target, ext);
    }

    private boolean isInSync(Project project) {
        val prop = project.getProviders().systemProperty("idea.sync.active");
        return prop.isPresent() && "true".equals(prop.get());
    }

    private void setupWithJava(Project project, AsmExtension extension) {
        val java = project.getExtensions().getByType(JavaPluginExtension.class);
        val sourceSets = project.getExtensions().getByType(SourceSetContainer.class);

        val asmDependencies = project.getConfigurations().register("asm", conf -> {
            conf.setCanBeConsumed(false);
            conf.setCanBeResolved(false);

            conf.getDependencies().add(
                    project.getDependencies().create("moe.karla.asm:asm-transformer-api:" + AsmProperties.VERSION)
            );
        });


        val srcAsm = sourceSets.register("asm", asm -> {
            // TargetJvmVersion.TARGET_JVM_VERSION_ATTRIBUTE

            asm.getResources().setSrcDirs(new ArrayList<>());
            project.getConfigurations().named(asm.getCompileClasspathConfigurationName()).configure(cc -> {
                cc.getAttributes().attribute(TargetJvmVersion.TARGET_JVM_VERSION_ATTRIBUTE, Integer.MAX_VALUE);
                cc.extendsFrom(asmDependencies.get());
            });

            project.getTasks().named(asm.getCompileTaskName("java"), JavaCompile.class).configure(task -> {
                task.getJavaCompiler().convention(extension.getJavaToolchain()
                        .flatMap(javaToolchainSpec -> project.getExtensions().getByType(JavaToolchainService.class)
                                .compilerFor(javaToolchainSpec)
                        )
                );
            });
        });

        // java.disableAutoTargetJvm();

        val asmRuntime = project.getConfigurations().register("asmRuntime", conf -> {
            conf.setCanBeConsumed(false);
            conf.getAttributes().attribute(
                    Usage.USAGE_ATTRIBUTE,
                    project.getObjects().named(Usage.class, Usage.JAVA_RUNTIME)
            );
            conf.getAttributes().attribute(TargetJvmVersion.TARGET_JVM_VERSION_ATTRIBUTE, Integer.MAX_VALUE);

            conf.extendsFrom(asmDependencies.get());

            conf.getDependencies().add(
                    project.getDependencies().create("moe.karla.asm:asm-transformer-launcher:" + AsmProperties.VERSION)
            );
            conf.getDependencies().add(
                    project.getDependencies().create(srcAsm.get().getOutput())
            );
        });


        val runAsmGenerator = project.getTasks().register("runAsmGenerator", RunGeneratorTask.class);
        runAsmGenerator.configure(task -> {
            task.getGeneratedClassOutputDirectory().set(extension.getGeneratedClassOutputDirectory());
            task.getGeneratedClassSourceDirectory().set(extension.getGeneratedClassSourceDirectory());
            val classpath = project.getObjects().fileCollection();
            task.setClasspath(classpath);

            classpath.from(asmRuntime);
            classpath.from(srcAsm.map(SourceSet::getOutput));

            task.getGeneratorClasspath().from(
                    srcAsm.map(SourceSet::getOutput)
            );

            task.getMainClass().set("moe.karla.asm.transformer.launcher.TransformerLauncher");

            task.getJavaLauncher().convention(extension.getJavaToolchain()
                    .flatMap(javaToolchainSpec -> project.getExtensions().getByType(JavaToolchainService.class)
                            .launcherFor(javaToolchainSpec)
                    )
            );

            task.setup();
        });


        if (isInSync(project)) {
            sourceSets.named("main").configure(main -> {
                main.getJava().srcDir(extension.getGeneratedClassSourceDirectory());
            });
        }

        sourceSets.named("main").configure(main -> {
            main.compiledBy(runAsmGenerator);
            val extraClasspath = project.getObjects().fileCollection();

            extraClasspath.from(runAsmGenerator.map(RunGeneratorTask::getGeneratedClassOutputDirectory));
            extraClasspath.builtBy(runAsmGenerator);

            main.getOutput().dir(extraClasspath);


            project.getDependencies().add(
                    main.getCompileOnlyConfigurationName(),
                    extraClasspath
            );

        });
    }
}
