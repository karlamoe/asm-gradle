package moe.karla.asm.runtime.multi;

import org.objectweb.asm.*;

import java.util.Collection;
import java.util.function.Function;

public final class MultiRecordComponentVisitor extends RecordComponentVisitor {
    public static final Function<Collection<RecordComponentVisitor>, RecordComponentVisitor> CREATOR = MultiRecordComponentVisitor::new;
    private final Collection<RecordComponentVisitor> rvs;

    public MultiRecordComponentVisitor(Collection<RecordComponentVisitor> rvs) {
        super(Opcodes.ASM9);
        this.rvs = rvs;
    }

    @Override
    public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
        return MultiCreator.createMulti(
                rvs,
                it -> it.visitAnnotation(descriptor, visible),
                MultiAnnotationVisitor.CREATOR
        );
    }

    @Override
    public AnnotationVisitor visitTypeAnnotation(int typeRef, TypePath typePath, String descriptor, boolean visible) {
        return MultiCreator.createMulti(
                rvs,
                it -> it.visitTypeAnnotation(typeRef, typePath, descriptor, visible),
                MultiAnnotationVisitor.CREATOR
        );
    }

    @Override
    public void visitAttribute(Attribute attribute) {
        for (RecordComponentVisitor rv : rvs) {
            rv.visitAttribute(attribute);
        }
    }

    @Override
    public void visitEnd() {
        for (RecordComponentVisitor rv : rvs) {
            rv.visitEnd();
        }
    }
}
