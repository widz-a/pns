package withicality.pns;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
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
    private PnsConfig config;

    private static final KeyBinding khit = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.pns.hit", InputUtil.UNKNOWN_KEY.getCode(), "key.pns.pns"));
    private static final KeyBinding kshift = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.pns.shift", InputUtil.UNKNOWN_KEY.getCode(), "key.pns.pns"));
    private static final KeyBinding knod = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.pns.nod", InputUtil.UNKNOWN_KEY.getCode(), "key.pns.pns"));

    private static boolean bhit = false;
    private static boolean bshift = false;
    private static boolean bnod = false;

    private static long hit = 0;
    private static long shift = 0;
    private static boolean nod = false;
    private static long nodL = 0;

    @Override
    public void onInitializeClient() {
        AutoConfig.register(PnsConfig.class, JanksonConfigSerializer::new);
        config = AutoConfig.getConfigHolder(PnsConfig.class).getConfig();

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (khit.wasPressed()) bhit = !bhit;
            while (kshift.wasPressed()) bshift = !bshift;
            while (knod.wasPressed()) bnod = !bnod;

            if (client.isPaused()) return;
            if (client.currentScreen != null) return;

            ClientPlayerEntity player = client.player;
            if (player == null) {
                hit = 0;
                shift = 0;
                nodL = 0;
                bhit = false;
                bshift = false;
                bnod = false;
                return;
            }

            if (bhit) {
                if (hit == 0) hit = System.currentTimeMillis();
                else if (hit < System.currentTimeMillis()) {
                    KeyBinding.onKeyPressed(KeyBindingHelper.getBoundKeyOf(client.options.attackKey));
                    hit = System.currentTimeMillis() + config.punch.delay; //og: 143
                }
            }

            if (bshift) {
                if (shift == 0) shift = System.currentTimeMillis();
                else if (shift < System.currentTimeMillis()) {
                    KeyBinding.setKeyPressed(KeyBindingHelper.getBoundKeyOf(client.options.sneakKey), true);
                    setTimeout(() -> KeyBinding.setKeyPressed(KeyBindingHelper.getBoundKeyOf(client.options.sneakKey), false), 100);
                    shift = System.currentTimeMillis() + config.sneak.delay; //og: 200
                }
            }

            if (bnod) {
                if (nodL == 0) nodL = System.currentTimeMillis();
                else if (nodL < System.currentTimeMillis()) {
                    float currentPitch = player.getPitch(); // current angle of the player
                    float targetPitch = nod ? config.nod.degreeUp : config.nod.degreeDown; // the angle the player should face (in degrees)
                    float deltaAngle = targetPitch - currentPitch; // angle to turn by
                    if (deltaAngle >= 180) {
                        deltaAngle -= 360; // ensure deltaAngle is within -180 to 180 range
                    } else if (deltaAngle < -180) {
                        deltaAngle += 360;
                    }
                    float newPitch = currentPitch + Math.signum(deltaAngle) * Math.min(config.nod.speed, Math.abs(deltaAngle));
                    player.setPitch(newPitch); // set the new Pitch value for the player

                    nod = newPitch != config.nod.degreeUp && (newPitch == config.nod.degreeDown || nod);
                    nodL = System.currentTimeMillis() + (nod ? config.nod.delayUp : config.nod.delayDown);
                }
            }
        });
    }
    public static void setTimeout(Runnable runnable, int delay) {
        final String CREDIT = "https://stackoverflow.com/questions/26311470/what-is-the-equivalent-of-javascript-settimeout-in-java";
        new Thread(() -> { 
            try {
                Thread.sleep(delay);
                runnable.run();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    public static boolean[] getEnabled() {
        return new boolean[] { bhit, bnod, bshift };
    }
}