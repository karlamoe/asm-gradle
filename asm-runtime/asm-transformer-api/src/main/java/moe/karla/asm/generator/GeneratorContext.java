package moe.karla.asm.generator;

import moe.karla.asm.function.ThrowingConsumer;
import moe.karla.asm.function.ThrowingRunnable;
import org.objectweb.asm.ClassVisitor;

import java.nio.file.Path;
import java.util.concurrent.Executor;

public abstract class GeneratorContext implements Executor {
    public abstract void addClass(
            ThrowingConsumer<ClassVisitor> consumer
    ) throws Throwable;

    @Override
    public abstract void execute(Runnable command);

    public abstract void executeThrowing(ThrowingRunnable command);

    public abstract void executeThrowing(Object context, ThrowingRunnable command);


    public abstract Path getGeneratedClassesDir();

    public abstract Path getGeneratedSourcesDir();
}
