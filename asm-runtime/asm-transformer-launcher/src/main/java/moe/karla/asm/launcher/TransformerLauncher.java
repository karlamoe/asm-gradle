package moe.karla.asm.launcher;

import lombok.val;
import lombok.var;
import moe.karla.asm.function.ThrowingConsumer;
import moe.karla.asm.function.ThrowingRunnable;
import moe.karla.asm.generator.ClassGenerator;
import moe.karla.asm.generator.GeneratorContext;
import moe.karla.asm.runtime.dumper.DummyClassPrinter;
import moe.karla.asm.runtime.util.ClassInfoVisitor;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.util.TraceClassVisitor;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class TransformerLauncher extends GeneratorContext {
    private final Path outputSources;
    private final Path outputClasses;
    private final Path outputProto;


    private final ExecutorService executorService;
    private final AtomicLong taskCounter = new AtomicLong();

    public TransformerLauncher(Path outputSources, Path outputClasses, Path outputProto) {
        this.outputSources = outputSources;
        this.outputClasses = outputClasses;
        this.outputProto = outputProto;
        this.executorService = Executors.newScheduledThreadPool(
                4,
                new ThreadFactory() {
                    private final AtomicInteger count = new AtomicInteger(0);

                    @Override
                    public Thread newThread(Runnable r) {
                        Thread t = new Thread(r, "Transform Worker #" + count.getAndIncrement());
                        t.setDaemon(false);
                        return t;
                    }
                }
        );
    }

    public static void main(String[] args00) throws Throwable {
        var args = new ArrayList<>(Arrays.asList(args00));
        var outputSource = Paths.get(args.remove(0));
        var outputClasses = Paths.get(args.remove(0));
        var outputProto0 = args.remove(0);


//        System.out.println("outputSource: " + outputSource);
//        System.out.println("outputClasses: " + outputClasses);


        Files.createDirectories(outputClasses);
        Files.createDirectories(outputSource);

        var launcher = new TransformerLauncher(outputSource, outputClasses, outputProto0.isEmpty() ? null : Paths.get(outputProto0));


        for (var file : args) {
            if (file.endsWith(".class")) {
                var reader = new ClassReader(Files.readAllBytes(Paths.get(file)));

                launcher.executeThrowing(() -> {
                    launcher.runGenerator(reader.getClassName());
                });
            }
            if (file.endsWith(".jproto")) {
                launcher.executeThrowing(file, () -> {
                    new ProtoParser(file).generate(launcher);
                });
            }
        }
        launcher.runKiller();
    }

    private void runKiller() {
        if (taskCounter.compareAndSet(0, -1)) {
            Runtime.getRuntime().halt(0);
        }
    }

    @Override
    public void execute(Runnable command) {
        executeThrowing(command, command::run);
    }

    @Override
    public void executeThrowing(ThrowingRunnable command) {
        executeThrowing(command, command);
    }

    @Override
    public void executeThrowing(Object context, ThrowingRunnable command) {
        taskCounter.getAndIncrement();
        executorService.execute(() -> {
            try {
                command.run();
            } catch (Throwable throwable) {
                try {
                    System.err.println("Exception while executing with " + context);
                } catch (Throwable ignored) {
                }
                try {
                    throwable.printStackTrace(System.err);
                } catch (Throwable ignored) {
                }
                Runtime.getRuntime().halt(3553);
            }
            taskCounter.getAndDecrement();
            runKiller();
        });
    }

    private void runGenerator(String className) throws Throwable {
        Class<?> targetClass = Class.forName(className.replace('/', '.'));
        if (!ClassGenerator.class.isAssignableFrom(targetClass)) {
            return;
        }
        if (Modifier.isAbstract(targetClass.getModifiers())) {
            return;
        }

        ClassGenerator generator = (ClassGenerator) targetClass.newInstance();
        generator.generate(this);
    }

    @Override
    public void addClass(ThrowingConsumer<ClassVisitor> consumer) throws Throwable {

        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        ClassInfoVisitor cv = new ClassInfoVisitor(Opcodes.ASM9, writer);

        consumer.accept(cv);

        if (cv.name.contains(".")) {
            throw new IllegalStateException("Illegal class name " + cv.name);
        }

        val outputClass = outputClasses.resolve(cv.name + ".class");
        val outputSource = outputSources.resolve(cv.name + ".java");

        Files.createDirectories(outputClass.getParent());
        Files.createDirectories(outputSource.getParent());


        byte[] code = writer.toByteArray();

        Files.write(outputClass, code);
        {
            var sw = new StringWriter();
            new ClassReader(code).accept(new TraceClassVisitor(
                    null,
                    new DummyClassPrinter(),
                    new PrintWriter(sw)
            ), 0);
            Files.write(outputSource, sw.toString().getBytes(StandardCharsets.UTF_8));
        }

        if (this.outputProto != null) {
            var outputProto = this.outputProto.resolve(cv.name + ".jproto");
            Files.createDirectories(outputProto.getParent());

            var sw = new StringBuilder();
            new ClassReader(code).accept(new ProtoVisitor(sw), ClassReader.SKIP_CODE);
            Files.write(outputProto, sw.toString().getBytes(StandardCharsets.UTF_8));
        }

    }


    @Override
    public Path getGeneratedClassesDir() {
        return outputClasses;
    }

    @Override
    public Path getGeneratedSourcesDir() {
        return outputSources;
    }
}
