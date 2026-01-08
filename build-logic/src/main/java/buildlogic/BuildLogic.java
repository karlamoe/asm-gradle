package buildlogic;

import org.gradle.api.Plugin;
import org.gradle.api.initialization.Settings;
import org.gradle.api.internal.file.FileOperations;

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
    }
}
