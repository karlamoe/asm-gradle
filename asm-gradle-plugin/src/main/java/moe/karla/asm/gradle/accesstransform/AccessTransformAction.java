package moe.karla.asm.gradle.accesstransform;

import lombok.SneakyThrows;
import lombok.val;
import lombok.var;
import moe.karla.asm.gradle.internal.IOUtil;
import moe.karla.asm.libs.accesstransformer.api.AccessTransformerEngine;
import org.gradle.api.artifacts.transform.*;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.file.FileSystemLocation;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.InputFiles;
import org.gradle.api.tasks.PathSensitive;
import org.gradle.api.tasks.PathSensitivity;
import org.jspecify.annotations.NonNull;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

@CacheableTransform
public abstract class AccessTransformAction implements TransformAction<AccessTransformAction.Parameters> {
    public interface Parameters extends TransformParameters {
        @InputFiles
        @PathSensitive(PathSensitivity.NONE)
        ConfigurableFileCollection getAtFiles();
    }

    @InputArtifact
    @PathSensitive(PathSensitivity.NONE)
    public abstract Provider<FileSystemLocation> getInputArtifact();


    @Override
    @SneakyThrows
    public void transform(@NonNull TransformOutputs outputs) {

        val engine = AccessTransformerEngine.newEngine();
        for (File file : getParameters().getAtFiles()) {
            engine.loadATFromPath(file.toPath());
        }

        val artifact = getInputArtifact().get().getAsFile();
        // System.out.println("Run transform " + artifact);

        try (val zipFile = new ZipFile(artifact)) {
            val classes = zipFile.stream()
                    .map(ZipEntry::getName)
                    .filter(it -> it.endsWith(".class"))
                    .collect(Collectors.toList());

            boolean needTransform = false;
            for (var name : classes) {
                if (name.startsWith("META-INF/versions/")) {
                    name = name.substring("META-INF/versions/".length());

                    // drop version subfolder
                    name = name.substring(name.indexOf('/') + 1);
                }

                name = name.substring(0, name.length() - ".class".length());
                if (engine.containsClassTarget(Type.getObjectType(name))) {
                    needTransform = true;
                    break;
                }
            }

            if (!needTransform) {
                outputs.file(getInputArtifact());
                return;
            }


            val outputFile = outputs.file(artifact.getName().replace(".jar", "") + "-ATtransformed.jar");
            // System.out.println("Output file: " + outputFile);

            try (val output = new ZipOutputStream(new BufferedOutputStream(Files.newOutputStream(outputFile.toPath())))) {
                class Helper {
                    void rawCopy(ZipEntry entry) throws IOException {
                        output.putNextEntry(new ZipEntry(entry));
                        if (!entry.isDirectory()) {
                            try (val src = zipFile.getInputStream(entry)) {
                                IOUtil.copy(src, output);
                            }
                        }
                    }
                }
                val helper = new Helper();

                val entries = zipFile.entries();
                while (entries.hasMoreElements()) {
                    val entry = entries.nextElement();
                    if (!entry.getName().endsWith(".class")) {
                        helper.rawCopy(entry);
                        continue;
                    }

                    val cn = new ClassNode();
                    try (val src = zipFile.getInputStream(entry)) {
                        new ClassReader(src).accept(cn, 0);
                    }

                    val type = Type.getObjectType(cn.name);
                    if (!engine.containsClassTarget(type)) {
                        helper.rawCopy(entry);
                        continue;
                    }

                    engine.transform(cn, type);

                    val cw = new ClassWriter(0);
                    cn.accept(cw);

                    output.putNextEntry(new ZipEntry(entry));
                    output.write(cw.toByteArray());
                }
            }
        }
    }
}
