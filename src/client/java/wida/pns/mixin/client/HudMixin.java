package wida.pns.mixin.client;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import wida.pns.IsToggled;
import wida.pns.PnsClient;
import wida.pns.PnsConfig;

import java.util.Arrays;

@Mixin(InGameHud.class)
public class HudMixin {
	private int y = 30;
	private final MinecraftClient client = MinecraftClient.getInstance();
	private DrawContext context;

	@Inject(method = "render", at = @At("RETURN"))
	public void a(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
		ClientPlayerEntity player = client.player;
		if (client.getDebugHud().shouldShowDebugHud()) return;
		if (player == null) return;

		y = 20;
		this.context = context;
		renderText("Debug: pns-" + FabricLoader.getInstance().getModContainer("pns").get().getMetadata().getVersion());
		renderText("{punch, nod, shift}: " + Arrays.toString(IsToggled.INSTANCE.enables()));
		renderText("");

		PnsConfig config = PnsClient.INSTANCE.getConfig();
		long ms = System.currentTimeMillis();
		renderText("Punch: " + (PnsClient.INSTANCE.getTPunch() - ms) + "/" + config.punch.delay);
		renderText("Sneak: " + (PnsClient.INSTANCE.getTShift() - ms) + "/"+ config.sneak.holdDelay);
		renderText("Nod: " + player.getPitch() + " C (" + config.nod.degreeDown + "; " + config.nod.degreeUp + ")");
	}

	private void renderText(String text) {
		y = y + 10;
		context.drawTextWithShadow(client.textRenderer, text, 5, y, -1);
	}
}