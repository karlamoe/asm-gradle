package moe.karla.asm.gradle.accesstransform;


import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.file.ConfigurableFileCollection;

import javax.inject.Inject;

public abstract class AccessTransformExtension {
    public abstract ConfigurableFileCollection getAtFiles();

    public boolean applyToCompileClasspath = true;
    public boolean applyToRuntimeClasspath = true;

    public void applyToConfiguration(Configuration configuration) {
        configuration.getAttributes().attribute(
                AccessTransformAttribute.ATTRIBUTE_KEY,
                getProject().getObjects().named(AccessTransformAttribute.class, AccessTransformAttribute.TRANSFORMED)
        );
    }


    @Inject
    protected abstract Project getProject();
}
