package moe.karla.asm.launcher;

import lombok.var;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class ProtoVisitor extends ClassVisitor {
    private final StringBuilder sw;

    public ProtoVisitor(StringBuilder sw) {
        super(Opcodes.ASM9);
        this.sw = sw;
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        sw.append(version).append(',')
                .append(access).append(',')
                .append(name).append(',')
                .append(superName);
        if (interfaces != null) for (var itf : interfaces) {
            sw.append(',').append(itf);
        }
        sw.append('\n');
    }

    @Override
    public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
        sw.append(access).append(',').append(name).append(',').append(descriptor).append('\n');
        return null;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        sw.append(access).append(',').append(name).append(',').append(descriptor).append('\n');
        return null;
    }
}
