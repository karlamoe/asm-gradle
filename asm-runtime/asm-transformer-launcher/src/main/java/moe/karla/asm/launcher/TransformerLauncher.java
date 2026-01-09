package moe.karla.asm.launcher;

import moe.karla.asm.generator.ClassGenerator;
import moe.karla.asm.runtime.dumper.DummyClassPrinter;
import moe.karla.asm.runtime.util.ClassInfoVisitor;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.util.TraceClassVisitor;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class TransformerLauncher {
    public static void main(String[] args00) throws Throwable {
        var args = new ArrayList<>(List.of(args00));
        var outputSource = Path.of(args.remove(0));
        var outputClasses = Path.of(args.remove(0));

        System.out.println("outputSource: " + outputSource);
        System.out.println("outputClasses: " + outputClasses);


        Files.createDirectories(outputClasses);
        Files.createDirectories(outputSource);


        for (var file : args) {
            var reader = new ClassReader(
                    Files.readAllBytes(Path.of(file))
            );
            System.out.println("Running " + reader.getClassName());
            runGenerator(reader.getClassName(), outputClasses, outputSource);
        }
    }

    private static void runGenerator(String className, Path outputClasses, Path outputSources) throws Throwable {
        Class<?> targetClass = Class.forName(className.replace('/', '.'));
        if (!ClassGenerator.class.isAssignableFrom(targetClass)) {
            return;
        }

        ClassGenerator generator = (ClassGenerator) targetClass.newInstance();

        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        StringWriter sw = new StringWriter();
        ClassInfoVisitor cv = new ClassInfoVisitor(Opcodes.ASM9, writer);

        generator.generate(cv);

        if (cv.name.contains(".")) {
            throw new IllegalStateException("Illegal class name " + cv.name);
        }

        var outputClass = outputClasses.resolve(cv.name + ".class");
        var outputSource = outputSources.resolve(cv.name + ".java");

        Files.createDirectories(outputClass.getParent());
        Files.createDirectories(outputSource.getParent());


        byte[] code = writer.toByteArray();

        Files.write(outputClass, code);

        new ClassReader(code).accept(new TraceClassVisitor(
                null,
                new DummyClassPrinter(),
                new PrintWriter(sw)
        ), 0);
        Files.writeString(outputSource, sw.toString());

    }
}
