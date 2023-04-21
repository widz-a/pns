package withicality.pns.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;

@Environment(EnvType.CLIENT)
public class PnsClient implements ClientModInitializer {

    public static final class Settings {
        public final static PSRandom MS_PER_HIT = new PSRandom(100, 150);
        public final static PSRandom MS_PER_SNEAK = new PSRandom(170, 235);

        public final static float ROTATION_SPEED = 40;
        public final static float PITCH_UP = -60;
        public final static float PITCH_DOWN = 60;
    }

    private static final KeyBinding khit = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.pns.hit", InputUtil.UNKNOWN_KEY.getCode(), "key.pns.pns"));
    private static final KeyBinding kshift = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.pns.shift", InputUtil.UNKNOWN_KEY.getCode(), "key.pns.pns"));
    private static final KeyBinding knod = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.pns.nod", InputUtil.UNKNOWN_KEY.getCode(), "key.pns.pns"));

    private static boolean bhit = false;
    private static boolean bshift = false;
    private static boolean bnod = false;

    private static long hit = 0;
    private static long shift = 0;
    private static boolean nod = false;

    @Override
    public void onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register((client) -> {
            while (khit.wasPressed()) bhit = !bhit;
            while (kshift.wasPressed()) bshift = !bshift;
            while (knod.wasPressed()) bnod = !bnod;

            if (client.isPaused()) return;
            if (client.currentScreen != null) return;

            ClientPlayerEntity player = client.player;
            if (player == null) {
                hit = 0;
                shift = 0;
                bhit = false;
                bshift = false;
                bnod = false;
                return;
            }

            if (bhit) {
                if (hit == 0) hit = System.currentTimeMillis();
                else if (hit < System.currentTimeMillis()) {
                    KeyBinding.onKeyPressed(client.options.attackKey.getDefaultKey());
                    hit = System.currentTimeMillis() + Settings.MS_PER_HIT.randomI(); //og: 143
                }
            }

            if (bshift) {
                if (shift == 0) shift = System.currentTimeMillis();
                else if (shift < System.currentTimeMillis()) {
                    KeyBinding.setKeyPressed(client.options.sneakKey.getDefaultKey(), true);
                    setTimeout(() -> KeyBinding.setKeyPressed(client.options.sneakKey.getDefaultKey(), false), 100);
                    shift = System.currentTimeMillis() + Settings.MS_PER_SNEAK.randomI(); //og: 200
                }
            }

            if (bnod) {
                float currentPitch = player.getPitch(); // current angle of the player
                float targetPitch = nod ? Settings.PITCH_UP : Settings.PITCH_DOWN; // the angle the player should face (in degrees)
                float deltaAngle = targetPitch - currentPitch; // angle to turn by
                if (deltaAngle >= 180) {
                    deltaAngle -= 360; // ensure deltaAngle is within -180 to 180 range
                } else if (deltaAngle < -180) {
                    deltaAngle += 360;
                }
                float newPitch = currentPitch + Math.signum(deltaAngle) * Math.min(Settings.ROTATION_SPEED, Math.abs(deltaAngle));
                player.setPitch(newPitch); // set the new Pitch value for the player

                nod = newPitch != Settings.PITCH_UP && (newPitch == Settings.PITCH_DOWN || nod);
            }
        });
    }
    //Yoink https://stackoverflow.com/questions/26311470/what-is-the-equivalent-of-javascript-settimeout-in-java
    public static void setTimeout(Runnable runnable, int delay) {
        new Thread(() -> { 
            try {
                Thread.sleep(delay);
                runnable.run();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
    public record PSRandom(int min, int max) {
        public int randomI() { return min + (int)(Math.random() * ((max - min) + 1)); }
        public String val() { return min + " " + max; }
    }

    public static boolean isNod() {
        return nod;
    }

    public static long getShift() {
        return shift;
    }

    public static long getHit() {
        return hit;
    }

    public static boolean[] getEnabled() {
        return new boolean[] { bhit, bnod, bshift };
    }
}