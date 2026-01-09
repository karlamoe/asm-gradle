package moe.karla.asm.gradle.tasks;

import lombok.val;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.internal.file.FileOperations;
import org.gradle.api.tasks.InputFiles;
import org.gradle.api.tasks.JavaExec;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.OutputDirectory;

import javax.inject.Inject;
import java.util.ArrayList;

public abstract class RunGeneratorTask extends JavaExec {
    @OutputDirectory
    @Optional
    public abstract DirectoryProperty getGeneratedProtoOutputDirectory();

    @InputFiles
    public abstract ConfigurableFileCollection getGeneratorClasspath();

    @OutputDirectory
    public abstract DirectoryProperty getGeneratedClassSourceDirectory();

    @OutputDirectory
    public abstract DirectoryProperty getGeneratedClassOutputDirectory();

    @Inject
    protected abstract FileOperations getFileOperations();

    public RunGeneratorTask() {
        setGroup("asm");

        getArgumentProviders().add(() -> {
            val result = new ArrayList<String>();
            result.add(getGeneratedClassSourceDirectory().get().getAsFile().getAbsolutePath());
            result.add(getGeneratedClassOutputDirectory().get().getAsFile().getAbsolutePath());

            val dir = getGeneratedProtoOutputDirectory().getOrNull();
            if (dir == null) {
                result.add("");
            } else {
                result.add(dir.getAsFile().getAbsolutePath());
            }

            getGeneratorClasspath().getAsFileTree().forEach(f -> result.add(f.getAbsolutePath()));
            return result;
        });
    }

    public void setup() {
        doFirst($ -> {
            getFileOperations().delete(getGeneratedClassOutputDirectory());
            getFileOperations().delete(getGeneratedClassSourceDirectory());
            if (getGeneratedProtoOutputDirectory().isPresent()) {
                getFileOperations().delete(getGeneratedProtoOutputDirectory());
            }
        });
    }
}
