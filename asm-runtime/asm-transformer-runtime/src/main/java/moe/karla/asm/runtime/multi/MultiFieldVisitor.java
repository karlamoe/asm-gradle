package moe.karla.asm.runtime.multi;

import org.objectweb.asm.*;

import java.util.Collection;
import java.util.function.Function;

public final class MultiFieldVisitor extends FieldVisitor {
    public static final Function<Collection<FieldVisitor>, FieldVisitor> CREATOR = MultiFieldVisitor::new;

    private final Collection<FieldVisitor> fvs;

    public MultiFieldVisitor(Collection<FieldVisitor> fvs) {
        super(Opcodes.ASM9);
        this.fvs = fvs;
    }

    @Override
    public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
        return MultiCreator.createMulti(
                fvs,
                it -> it.visitAnnotation(descriptor, visible),
                MultiAnnotationVisitor.CREATOR
        );
    }

    @Override
    public AnnotationVisitor visitTypeAnnotation(int typeRef, TypePath typePath, String descriptor, boolean visible) {
        return MultiCreator.createMulti(
                fvs,
                it -> it.visitTypeAnnotation(typeRef, typePath, descriptor, visible),
                MultiAnnotationVisitor.CREATOR
        );
    }

    @Override
    public void visitAttribute(Attribute attribute) {
        for (FieldVisitor fv : fvs) {
            fv.visitAttribute(attribute);
        }
    }

    @Override
    public void visitEnd() {
        for (FieldVisitor fv : fvs) {
            fv.visitEnd();
        }
    }
}
