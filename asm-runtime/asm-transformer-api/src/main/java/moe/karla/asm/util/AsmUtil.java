package moe.karla.asm.util;

import lombok.val;
import lombok.var;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;

import java.io.IOException;

public class AsmUtil {
    public static ClassNode readClass(String name) throws IOException {
        return readClass(new ClassReader(name));
    }

    public static ClassNode readClass(ClassReader classReader) {
        var node = new ClassNode();
        classReader.accept(node, 0);
        return node;
    }

    public static ClassNode readClass(byte[] bytes) {
        return readClass(new ClassReader(bytes));
    }

    public static void pushArguments(
            MethodVisitor mv,
            int startIndex,
            Type[] descriptor
    ) {
        var slot = startIndex;
        for (val arg : descriptor) {
            mv.visitVarInsn(arg.getOpcode(Opcodes.ILOAD), slot);
            slot += arg.getSize();
        }
    }
}
