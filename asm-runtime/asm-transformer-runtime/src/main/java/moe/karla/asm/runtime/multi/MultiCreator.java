package moe.karla.asm.runtime.multi;

import lombok.var;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Function;

public class MultiCreator {
    public static <T, R> R createMulti(
            Collection<T> src,
            Function<? super T, ? extends R> func,
            Function<? super Collection<R>, ? extends R> merger
    ) {
        var iterator = src.iterator();
        while (iterator.hasNext()) {
            var result = func.apply(iterator.next());
            if (result == null) continue;

            while (iterator.hasNext()) {
                var result2 = func.apply(iterator.next());
                if (result2 == null) continue;

                var results = new ArrayList<R>();
                results.add(result);
                results.add(result2);

                while (iterator.hasNext()) {
                    var result3 = func.apply(iterator.next());
                    if (result3 != null) {
                        results.add(result3);
                    }
                }
                return merger.apply(results);
            }

            return result;
        }

        return null;
    }
}
