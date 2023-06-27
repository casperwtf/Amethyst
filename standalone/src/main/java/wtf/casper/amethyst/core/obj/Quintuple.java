package wtf.casper.amethyst.core.obj;

public class Quintuple<J, K, L, M> extends Tuple<J, K, L> {

    private final M fourth;

    public Quintuple(J first, K second, L third, M fourth) {
        super(first, second, third);
        this.fourth = fourth;
    }

    public static <J, K, L, M> Quintuple<J, K, L, M> of(J first, K second, L third, M fourth) {
        return new Quintuple<>(first, second, third, fourth);
    }
}
