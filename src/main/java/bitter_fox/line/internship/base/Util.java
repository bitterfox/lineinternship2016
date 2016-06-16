/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bitter_fox.line.internship.base;

import java.util.function.Function;
import java.util.stream.Stream;

/**
 *
 * @author bitter_fox
 */
public final class Util {
    private Util() {}

    public static <T, R> R computeAndClose(Stream<? extends T> stream, Function<Stream<? extends T>, ? extends R> function) {
        try (Stream<? extends T> s = stream) {
            return function.apply(s);
        }
    }
}
