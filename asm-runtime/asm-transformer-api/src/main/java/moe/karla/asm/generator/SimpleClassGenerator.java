package moe.karla.asm.generator;

import org.objectweb.asm.ClassVisitor;

public abstract class SimpleClassGenerator extends ClassGenerator {
    @Override
    public void generate(GeneratorContext context) throws Throwable {
        context.addClass(cv -> generate(cv, context));
    }

    public abstract void generate(ClassVisitor cv, GeneratorContext context) throws Throwable;
}
