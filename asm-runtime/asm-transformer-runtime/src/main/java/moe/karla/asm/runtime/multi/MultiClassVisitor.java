package moe.karla.asm.runtime.multi;

import org.objectweb.asm.*;

import java.util.Collection;
import java.util.function.Function;

public final class MultiClassVisitor extends ClassVisitor {
    public static final Function<Collection<ClassVisitor>, ClassVisitor> CREATOR = MultiClassVisitor::new;


    private final Collection<ClassVisitor> cvs;

    public MultiClassVisitor(Collection<ClassVisitor> cvs) {
        super(Opcodes.ASM9);
        this.cvs = cvs;
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        for (ClassVisitor cv : cvs) {
            cv.visit(version, access, name, signature, superName, interfaces);
        }
    }

    @Override
    public void visitSource(String source, String debug) {
        for (ClassVisitor cv : cvs) {
            cv.visitSource(source, debug);
        }
    }


    @Override
    public void visitNestHost(String nestHost) {
        for (ClassVisitor cv : cvs) {
            cv.visitNestHost(nestHost);
        }
    }

    @Override
    public void visitOuterClass(String owner, String name, String descriptor) {
        for (ClassVisitor cv : cvs) {
            cv.visitOuterClass(owner, name, descriptor);
        }
    }

    @Override
    public void visitAttribute(Attribute attribute) {
        for (ClassVisitor cv : cvs) {
            cv.visitAttribute(attribute);
        }
    }

    @Override
    public void visitNestMember(String nestMember) {
        for (ClassVisitor cv : cvs) {
            cv.visitNestMember(nestMember);
        }
    }

    @Override
    public void visitPermittedSubclass(String permittedSubclass) {
        for (ClassVisitor cv : cvs) {
            cv.visitPermittedSubclass(permittedSubclass);
        }
    }

    @Override
    public void visitInnerClass(String name, String outerName, String innerName, int access) {
        for (ClassVisitor cv : cvs) {
            cv.visitInnerClass(name, outerName, innerName, access);
        }
    }

    @Override
    public void visitEnd() {
        for (ClassVisitor cv : cvs) {
            cv.visitEnd();
        }
    }


    // multi merge

    @Override
    public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
        return MultiCreator.createMulti(
                cvs,
                it -> it.visitField(access, name, descriptor, signature, value),
                MultiFieldVisitor.CREATOR
        );
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        return MultiCreator.createMulti(
                cvs,
                it -> it.visitMethod(access, name, descriptor, signature, exceptions),
                MultiMethodVisitor.CREATOR
        );
    }

    @Override
    public ModuleVisitor visitModule(String name, int access, String version) {
        return MultiCreator.createMulti(
                cvs,
                it -> it.visitModule(name, access, version),
                MultiModuleVisitor.CREATOR
        );
    }


    @Override
    public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
        return MultiCreator.createMulti(
                cvs,
                it -> it.visitAnnotation(descriptor, visible),
                MultiAnnotationVisitor.CREATOR
        );
    }

    @Override
    public AnnotationVisitor visitTypeAnnotation(int typeRef, TypePath typePath, String descriptor, boolean visible) {
        return MultiCreator.createMulti(
                cvs,
                it -> it.visitTypeAnnotation(typeRef, typePath, descriptor, visible),
                MultiAnnotationVisitor.CREATOR
        );
    }

    @Override
    public RecordComponentVisitor visitRecordComponent(String name, String descriptor, String signature) {
        return MultiCreator.createMulti(
                cvs,
                it -> it.visitRecordComponent(name, descriptor, signature),
                MultiRecordComponentVisitor.CREATOR
        );
    }

}
