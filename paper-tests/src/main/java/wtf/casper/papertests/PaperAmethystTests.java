package wtf.casper.papertests;

import org.reflections.Reflections;
import wtf.casper.amethyst.core.utils.AmethystLogger;
import wtf.casper.amethyst.paper.AmethystPlugin;
import wtf.casper.papertests.tests.StorageTest;

import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;

public final class PaperAmethystTests extends AmethystPlugin {

    private final HashSet<Class<? extends Test>> tests = new HashSet<>();

    @Override
    public void onEnable() {
        saveDefaultConfig();
        saveConfig();

        // reflection doesnt work idk why
        tests.add(StorageTest.class);
        tests.add(ColorPerfTest.class);

        Reflections reflections = new Reflections("wtf.casper.papertests.tests");
        tests.addAll(reflections.getSubTypesOf(Test.class));

        tests.forEach(this::test);
    }

    @Override
    public void onDisable() {

    }

    private void test(Class<? extends Test> test) {
        AmethystLogger.log("Testing: " + test.getSimpleName());
        try {
            Test instance = test.getConstructor().newInstance();

            boolean b = instance.test();
            AmethystLogger.log("Test: " + test.getSimpleName() + " " + (b ? "passed" : "failed"));
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                 NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
}
