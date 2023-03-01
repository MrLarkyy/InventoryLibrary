package xyz.larkyy.inventorylibrary.api;

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;

import java.util.function.Function;

public class Utils {

    public static <U,T> Int2ObjectMap<T> map(Int2ObjectMap<U> map,Function<U,T> mappingFunction) {
        Int2ObjectMap<T> resultMap = new Int2ObjectArrayMap<>();
        for (var entry : map.int2ObjectEntrySet()) {
            resultMap.put(entry.getIntKey(), mappingFunction.apply(entry.getValue()));
        }
        return resultMap;
    }

}
