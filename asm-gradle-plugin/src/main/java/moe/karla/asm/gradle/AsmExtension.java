package moe.karla.asm.gradle;

import org.gradle.api.file.DirectoryProperty;

public abstract class AsmExtension {
    public abstract DirectoryProperty getGeneratedClassSourceDirectory();

    public abstract DirectoryProperty getGeneratedClassOutputDirectory();
}
