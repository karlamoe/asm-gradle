package moe.karla.asm.generator;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;

public abstract class ClassGenerator {
    public abstract void generate(ClassVisitor cv) throws Throwable;


    /// Util functions
    public static void generateConstructor(ClassVisitor cv, boolean isPrivate, String superClass) {
        var mv = cv.visitMethod(
                isPrivate ? Opcodes.ACC_PRIVATE : Opcodes.ACC_PUBLIC,
                "<init>",
                "()V",
                null, null
        );
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitMethodInsn(
                Opcodes.INVOKESPECIAL,
                superClass == null ? "java/lang/Object" : superClass,
                "<init>", "()V", false
        );
        mv.visitInsn(Opcodes.RETURN);
        mv.visitMaxs(1, 1);
        mv.visitEnd();
    }
}
