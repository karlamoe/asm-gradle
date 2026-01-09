package moe.karla.asm.runtime.dumper;

import org.objectweb.asm.*;
import org.objectweb.asm.util.Printer;

public class DummyClassPrinter extends Printer {
    public DummyClassPrinter() {
        super(Opcodes.ASM9);
    }

    String simpledName;

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        packageName:
        {
            var split = name.lastIndexOf('/');
            if (split == -1) break packageName;
            text.add("package " + name.substring(0, split).replace('/', '.') + ";\n");
        }

        if ((access & Opcodes.ACC_PUBLIC) != 0) {
            text.add("public ");
        }
        if ((access & Opcodes.ACC_PRIVATE) != 0) {
            text.add("private ");
        }
        if ((access & Opcodes.ACC_ABSTRACT) != 0) {
            text.add("abstract ");
        }
        if ((access & Opcodes.ACC_INTERFACE) != 0) {
            text.add("interface ");
        } else if ((access & Opcodes.ACC_ENUM) != 0) {
            text.add("enum ");
        } else if ((access & Opcodes.ACC_RECORD) != 0) {
            text.add("record ");
        } else {
            text.add("class ");
        }

        text.add(this.simpledName = name.substring(name.lastIndexOf('/') + 1));
        text.add(" extends ");
        text.add(superName.replace('/', '.'));
        if (interfaces != null && interfaces.length > 0) {
            text.add(" implements ");
            for (int i = 0; i < interfaces.length; i++) {
                if (i != 0) text.add(", ");
                text.add(interfaces[i].replace('/', '.'));
            }
        }

        text.add(" {\n");
    }

    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
    }

    @Override
    public void visitSource(String source, String debug) {

    }

    @Override
    public void visitOuterClass(String owner, String name, String descriptor) {

    }

    @Override
    public Printer visitClassAnnotation(String descriptor, boolean visible) {
        return new DummyClassPrinter();
    }

    @Override
    public void visitClassAttribute(Attribute attribute) {

    }

    @Override
    public void visitInnerClass(String name, String outerName, String innerName, int access) {

    }

    @Override
    public Printer visitField(int access, String name, String descriptor, String signature, Object value) {
        if ((access & Opcodes.ACC_SYNTHETIC) != 0) {
            return new DummyClassPrinter();
        }
        if ((access & Opcodes.ACC_PUBLIC) != 0) {
            text.add("public ");
        }
        if ((access & Opcodes.ACC_PRIVATE) != 0) {
            text.add("private ");
        }
        if ((access & Opcodes.ACC_PROTECTED) != 0) {
            text.add("protected ");
        }
        if ((access & Opcodes.ACC_STATIC) != 0) {
            text.add("static ");
        }
        if ((access & Opcodes.ACC_FINAL) != 0) {
            text.add("final ");
        }

        var type = Type.getType(descriptor);
        text.add(type.getClassName());
        text.add(" ");
        text.add(name);
        if ((access & Opcodes.ACC_FINAL) != 0) {
            text.add(" = (");
            text.add(type.getClassName());
            text.add(") (Object) null");
        }
        text.add(";\n");

        return new DummyClassPrinter();
    }

    @Override
    public Printer visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {

        if ((access & Opcodes.ACC_SYNTHETIC) != 0) {
            return new DummyClassPrinter();
        }
        if ("<clinit>".equals(name)) {
            return new DummyClassPrinter();
        }


        if ((access & Opcodes.ACC_PUBLIC) != 0) {
            text.add("public ");
        }
        if ((access & Opcodes.ACC_PRIVATE) != 0) {
            text.add("private ");
        }
        if ((access & Opcodes.ACC_PROTECTED) != 0) {
            text.add("protected ");
        }
        if ((access & Opcodes.ACC_STATIC) != 0) {
            text.add("static ");
        }
        if ((access & Opcodes.ACC_ABSTRACT) != 0) {
            text.add("abstract ");
        }
        if ((access & Opcodes.ACC_NATIVE) != 0) {
            text.add("native ");
        }

        if (name.equals("<init>")) {
            text.add(this.simpledName);
        } else {
            if ((access & Opcodes.ACC_FINAL) != 0) {
                text.add("final ");
            }

            text.add(Type.getReturnType(descriptor).getClassName());
            text.add(" ");
            text.add(name);
        }
        text.add("(");

        var c = 0;
        for (var arg : Type.getArgumentTypes(descriptor)) {
            if (c != 0) {
                text.add(", ");
            }
            text.add(arg.getClassName());
            text.add(" ");
            text.add("arg" + (c++));
        }
        text.add(")");

        if ((access & (Opcodes.ACC_ABSTRACT | Opcodes.ACC_NATIVE)) == 0) {
            text.add("{ throw new AbstractMethodError(\"This is a generated class and not compiled from a source file.\"); }");
        }
        text.add("\n");

        return new DummyClassPrinter();
    }

    @Override
    public void visitClassEnd() {
        text.add("}\n");
    }

    @Override
    public void visit(String name, Object value) {

    }

    @Override
    public void visitEnum(String name, String descriptor, String value) {

    }

    @Override
    public Printer visitAnnotation(String name, String descriptor) {
        return new DummyClassPrinter();
    }

    @Override
    public Printer visitArray(String name) {
        return new DummyClassPrinter();
    }

    @Override
    public void visitAnnotationEnd() {

    }

    @Override
    public Printer visitFieldAnnotation(String descriptor, boolean visible) {
        return new DummyClassPrinter();
    }

    @Override
    public void visitFieldAttribute(Attribute attribute) {

    }

    @Override
    public void visitFieldEnd() {

    }

    @Override
    public Printer visitAnnotationDefault() {
        return new DummyClassPrinter();
    }

    @Override
    public Printer visitMethodAnnotation(String descriptor, boolean visible) {
        return new DummyClassPrinter();
    }

    @Override
    public Printer visitParameterAnnotation(int parameter, String descriptor, boolean visible) {
        return new DummyClassPrinter();
    }

    @Override
    public void visitMethodAttribute(Attribute attribute) {

    }

    @Override
    public void visitCode() {

    }

    @Override
    public void visitFrame(int type, int numLocal, Object[] local, int numStack, Object[] stack) {

    }

    @Override
    public void visitInsn(int opcode) {

    }

    @Override
    public void visitIntInsn(int opcode, int operand) {

    }

    @Override
    public void visitVarInsn(int opcode, int varIndex) {

    }

    @Override
    public void visitTypeInsn(int opcode, String type) {

    }

    @Override
    public void visitFieldInsn(int opcode, String owner, String name, String descriptor) {

    }

    @Override
    public void visitInvokeDynamicInsn(String name, String descriptor, Handle bootstrapMethodHandle, Object... bootstrapMethodArguments) {

    }

    @Override
    public void visitJumpInsn(int opcode, Label label) {

    }

    @Override
    public void visitLabel(Label label) {

    }

    @Override
    public void visitLdcInsn(Object value) {

    }

    @Override
    public void visitIincInsn(int varIndex, int increment) {

    }

    @Override
    public void visitTableSwitchInsn(int min, int max, Label dflt, Label... labels) {

    }

    @Override
    public void visitLookupSwitchInsn(Label dflt, int[] keys, Label[] labels) {

    }

    @Override
    public void visitMultiANewArrayInsn(String descriptor, int numDimensions) {

    }

    @Override
    public void visitTryCatchBlock(Label start, Label end, Label handler, String type) {

    }

    @Override
    public void visitLocalVariable(String name, String descriptor, String signature, Label start, Label end, int index) {

    }

    @Override
    public void visitLineNumber(int line, Label start) {

    }

    @Override
    public void visitMaxs(int maxStack, int maxLocals) {

    }

    @Override
    public void visitMethodEnd() {

    }
}
