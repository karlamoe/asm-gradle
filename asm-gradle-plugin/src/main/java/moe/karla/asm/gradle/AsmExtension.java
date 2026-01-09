package moe.karla.asm.gradle;

import org.gradle.api.Action;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.provider.Property;
import org.gradle.jvm.toolchain.JavaToolchainSpec;

public abstract class AsmExtension {
    public abstract DirectoryProperty getGeneratedClassSourceDirectory();

    public abstract DirectoryProperty getGeneratedClassOutputDirectory();


    public abstract Property<Action<JavaToolchainSpec>> getJavaToolchain();

    {
        getJavaToolchain().convention(spec -> {
        });
    }

    public void toolchain(Action<JavaToolchainSpec> action) {
        getJavaToolchain().set(action);
    }
}
