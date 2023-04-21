package withicality.pns.mixin;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import withicality.pns.client.PnsClient;

import java.util.Arrays;

@Mixin(InGameHud.class)
public class HudMixin {
    private float y = 30;
    private final MinecraftClient client = MinecraftClient.getInstance();
    private MatrixStack matrices;

    @Inject(method = "render", at = @At("RETURN"))
    public void a(MatrixStack matrices, float tickDelta, CallbackInfo ci) {
        ClientPlayerEntity player = client.player;
        if (player == null) return;

        y = 20;
        this.matrices = matrices;

        renderText("pns-" + FabricLoader.getInstance().getModContainer("pns").get().getMetadata().getVersion());
        renderText("Settings:");
        renderText("- MS_PER_SPEAK: " + PnsClient.Settings.MS_PER_SNEAK.val());
        renderText("- MS_PER_HIT: " + PnsClient.Settings.MS_PER_HIT.val());
        renderText("- PITCH_DOWN: " + PnsClient.Settings.PITCH_DOWN);
        renderText("- PITCH_UP: " + PnsClient.Settings.PITCH_UP);
        renderText("- ROTATION_SPEED: " + PnsClient.Settings.ROTATION_SPEED);
        renderText("Debugs:");
        renderText("- ENABLED: " + Arrays.toString(PnsClient.getEnabled()));
        renderText("- HIT_IN_MS: " + (PnsClient.getHit() - System.currentTimeMillis()));
        renderText("- SNEAK_IN_MS: " + (PnsClient.getShift() - System.currentTimeMillis()));
        renderText("- NOD_UP: " + PnsClient.isNod());
    }

    private void renderText(String text) {
        y = y + 10;
        client.textRenderer.drawWithShadow(matrices, text, 5, y, -1);
    }
}
