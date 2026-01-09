package moe.karla.asm.function;

public interface ThrowingConsumer<T> {
    public void accept(T t) throws Throwable;
}