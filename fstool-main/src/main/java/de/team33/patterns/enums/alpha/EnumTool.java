package de.team33.patterns.enums.alpha;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class EnumTool<E extends Enum<E>> {

    private final Class<E> enumClass;
    private final Function<Optional<E>, E> resolver;

    private EnumTool(final Class<E> enumClass, final Function<Optional<E>, E> resolver) {
        this.enumClass = enumClass;
        this.resolver = resolver;
    }

    public static <E extends Enum<E>> EnumTool<E> of(final Class<E> enumClass) {
        return new EnumTool<>(enumClass, Optional::orElseThrow);
    }

    public final EnumTool<E> resolving(final Function<Optional<E>, E> resolver) {
        return new EnumTool<>(enumClass, resolver);
    }

    public final EnumTool<E> fallback(final E fallback) {
        return resolving(opt -> opt.orElse(fallback));
    }

    public final EnumTool<E> fallback(final Supplier<E> fallback) {
        return resolving(opt -> opt.orElseGet(fallback));
    }

    public final EnumTool<E> failing(final Supplier<? extends RuntimeException> failing) {
        return resolving(opt -> opt.orElseThrow(failing));
    }

    public final Stream<E> stream() {
        return Stream.of(enumClass.getEnumConstants());
    }

    public final Stream<E> findAll(final Predicate<? super E> filter) {
        return stream().filter(filter);
    }

    public final E findAny(final Predicate<? super E> filter) {
        return resolver.apply(findAll(filter).findAny());
    }

    public final E findFirst(final Predicate<? super E> filter) {
        return resolver.apply(findAll(filter).findFirst());
    }

    public final <R> R mapAny(final Predicate<? super E> filter,
                              final Function<? super E, ? extends R> mapping) {
        return mapping.apply(findAny(filter));
    }

    public final <R> R mapFirst(final Predicate<? super E> filter,
                                final Function<? super E, ? extends R> mapping) {
        return mapping.apply(findFirst(filter));
    }
}
