package buildlogic;

import org.gradle.api.Plugin;
import org.gradle.api.initialization.Settings;
import org.gradle.api.internal.file.FileOperations;
import org.gradle.api.tasks.testing.Test;

import javax.inject.Inject;
import java.io.File;

public abstract class BuildLogic implements Plugin<Settings> {
    @Inject
    public abstract FileOperations getFileOperations();

    @Override
    public void apply(Settings target) {
        target.dependencyResolutionManagement(dep -> {
            dep.getVersionCatalogs().register("libs", setup -> {
                setup.from(getFileOperations().configurableFiles(
                        new File(
                                target.getRootDir(),
                                "../gradle/libs.versions.toml"
                        )
                ));
            });
        });


        target.getGradle().allprojects(project -> {
            project.getTasks().withType(Test.class).configureEach(test -> {
                test.useJUnitPlatform();
            });

            var junit = project.getConfigurations().register("junit", conf -> {
                conf.setCanBeConsumed(false);
                conf.setCanBeResolved(false);

                var deps = project.getDependencies();

                conf.getDependencies().add(deps.platform("org.junit:junit-bom:5.14.2"));
                conf.getDependencies().add(deps.create("org.junit.jupiter:junit-jupiter"));
                conf.getDependencies().add(deps.create("org.junit.platform:junit-platform-launcher"));
            });

            project.getPluginManager().withPlugin("java", $ -> {
                project.getConfigurations().named("testImplementation").configure(conf -> {
                    conf.extendsFrom(junit.get());
                });
            });
        });
    }
}
