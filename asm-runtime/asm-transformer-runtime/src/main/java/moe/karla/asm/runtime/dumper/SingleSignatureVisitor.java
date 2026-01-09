package moe.karla.asm.runtime.dumper;

import lombok.var;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.signature.SignatureVisitor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SingleSignatureVisitor extends SignatureVisitor {
    protected List<Object> elms = new ArrayList<>();
    protected int arrayDepth;
    protected boolean hasTypeArgument;

    public SingleSignatureVisitor() {
        super(Opcodes.ASM9);
    }

    public void reset() {
        elms.clear();
        arrayDepth = 0;
        hasTypeArgument = false;
    }

    @Override
    public void visitBaseType(char descriptor) {
        elms.add(Type.getType(String.valueOf(descriptor)).getClassName());
    }

    @Override
    public SignatureVisitor visitArrayType() {
        arrayDepth++;
        return this;
    }


    public String render() {
        for (int i = 0; i < arrayDepth; i++) {
            elms.add("[]");
        }
        arrayDepth = 0;
        if (hasTypeArgument) {
            elms.add(">");
            hasTypeArgument = false;
        }

        return toResult(elms).toString().replace('/', '.');
    }

    @Override
    public String toString() {
        return render();
    }

    @Override
    public void visitTypeVariable(String name) {
        elms.add(name);
    }


    @Override
    public void visitClassType(String name) {
        elms.add(name);
    }

    @Override
    public void visitTypeArgument() {
        visitTypeArgument('?');
    }

    @Override
    public SignatureVisitor visitTypeArgument(char wildcard) {
        elms.add(hasTypeArgument ? ", " : "<");
        hasTypeArgument = true;

        if (wildcard == '+') {
            elms.add("? extends ");
        } else if (wildcard == '-') {
            elms.add("? super ");
        } else if (wildcard == '?') {
            elms.add("?");
        }

        var result = new SingleSignatureVisitor();
        elms.add(result);
        return result;
    }

    protected static StringBuilder toResult(Object result) {
        StringBuilder sb = new StringBuilder();
        if (result instanceof Collection<?>) {
            for (Object o : (Collection<?>) result) {
                sb.append(toResult(o));
            }
        } else if (result instanceof SingleSignatureVisitor) {
            sb.append(((SingleSignatureVisitor) result).render());
        } else {
            sb.append(result);
        }
        return sb;
    }

    public String renderAndReset() {
        var result = render();
        reset();
        return result;
    }
}
