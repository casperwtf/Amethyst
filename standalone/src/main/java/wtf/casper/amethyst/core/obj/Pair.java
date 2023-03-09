package wtf.casper.amethyst.core.obj;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Pair<K, V> {

    private K first;
    private V second;

    public static <K, V> Pair<K, V> of(K first, V second) {
        return new Pair<>(first, second);
    }

}
