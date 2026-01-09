package moe.karla.asm.runtime.util;

import org.objectweb.asm.ClassVisitor;

public class ClassInfoVisitor extends ClassVisitor {
    public int version;
    public int access;
    public String name;
    public String signature;
    public String superName;
    public String[] interfaces;

    public ClassInfoVisitor(int api) {
        super(api);
    }

    public ClassInfoVisitor(int api, ClassVisitor classVisitor) {
        super(api, classVisitor);
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        this.version = version;
        this.access = access;
        this.name = name;
        this.signature = signature;
        this.superName = superName;
        this.interfaces = interfaces;
        super.visit(version, access, name, signature, superName, interfaces);
    }
}
