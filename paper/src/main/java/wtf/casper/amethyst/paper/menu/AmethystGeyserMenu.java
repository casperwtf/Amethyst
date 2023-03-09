package wtf.casper.amethyst.paper.menu;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.geysermc.cumulus.Forms;
import org.geysermc.cumulus.component.ButtonComponent;
import org.geysermc.cumulus.component.impl.ButtonComponentImpl;
import org.geysermc.cumulus.form.CustomForm;
import org.geysermc.cumulus.form.Form;
import org.geysermc.cumulus.form.ModalForm;
import org.geysermc.cumulus.form.SimpleForm;
import org.geysermc.cumulus.form.util.FormType;
import org.geysermc.cumulus.util.FormImage;
import org.geysermc.floodgate.api.FloodgateApi;

import java.util.UUID;
import java.util.function.BiConsumer;

/**
* This entire class is just a wrapper for Geyser's Cumulus API. I just don't remember where things come from so this is to add it all in one place.
* */
public class AmethystGeyserMenu {

    public static ButtonComponent getButtonComponentOf(String text, FormImage.Type type, @Nullable String data) {
        return new ButtonComponentImpl(text, FormImage.of(type, data == null ? "" : data));
    }

    public static SimpleForm.Builder simpleFormBuilder() {
        return SimpleForm.builder();
    }

    public static ModalForm.Builder modalFormBuilder() {
        return ModalForm.builder();
    }

    public static CustomForm.Builder customFormBuilder() {
        return CustomForm.builder();
    }

    public static FormImage formImage(FormImage.Type type, @Nullable String data) {
        return FormImage.of(type, data == null ? "" : data);
    }

    public static ButtonComponent buttonComponent(String text, FormImage.Type type, @Nullable String data) {
        return new ButtonComponentImpl(text, formImage(type, data));
    }

    public static ButtonComponent buttonComponent(String text, FormImage image) {
        return new ButtonComponentImpl(text, image);
    }

    public static ButtonComponent buttonComponent(String text) {
        return new ButtonComponentImpl(text, FormImage.of(FormImage.Type.PATH, ""));
    }

    public static <T extends Form> T fromJson(String json, FormType type, BiConsumer<T, @Nullable String> responseHandler) {
        return Forms.fromJson(json, type, responseHandler);
    }

    public static void sendForm(Form form, UUID target) {
        FloodgateApi.getInstance().sendForm(target, form);
    }

}
