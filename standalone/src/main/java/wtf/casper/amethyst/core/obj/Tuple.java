package wtf.casper.amethyst.core.obj;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
class Tuple<J, K, L> extends Pair<J, K> {
    private final L third;

    public Tuple(J first, K second, L third) {
        super(first, second);
        this.third = third;
    }

    public static <J, K, L> Tuple<J, K, L> of(J first, K second, L third) {
        return new Tuple<>(first, second, third);
    }
}
