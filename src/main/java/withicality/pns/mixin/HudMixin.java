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
import withicality.pns.PnsClient;

import java.util.Arrays;

@Mixin(InGameHud.class)
public class HudMixin {
    private float y = 30;
    private final MinecraftClient client = MinecraftClient.getInstance();
    private MatrixStack matrices;

    @Inject(method = "render", at = @At("RETURN"))
    public void a(MatrixStack matrices, float tickDelta, CallbackInfo ci) {
        ClientPlayerEntity player = client.player;
        if (client.options.debugEnabled) return;
        if (player == null) return;

        y = 20;
        this.matrices = matrices;
        renderText("Debug: pns-" + FabricLoader.getInstance().getModContainer("pns").get().getMetadata().getVersion());
        renderText("{punch, nod, shift}: " + Arrays.toString(PnsClient.getEnabled()));
    }

    private void renderText(String text) {
        y = y + 10;
        client.textRenderer.drawWithShadow(matrices, text, 5, y, -1);
    }
}
