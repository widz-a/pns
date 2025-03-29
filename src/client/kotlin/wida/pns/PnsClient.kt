package wida.pns

import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.minecraft.client.option.KeyBinding
import kotlin.math.abs
import kotlin.math.min
import kotlin.math.sign

object PnsClient : ClientModInitializer {
	val config = PnsConfig() //For now...

	var tPunch = -1L
	var tShift = -1L

	var isNoddingDown = false
	var tNod = 0L

	override fun onInitializeClient() {
		KeyBinds.init() //Trick lỏ
		ClientTickEvents.END_CLIENT_TICK.register {client ->

			if (client.isPaused) return@register
			if (client.currentScreen != null) return@register

			//Toggle if pressed.
			while (KeyBinds.punch.wasPressed()) IsToggled.punch = !IsToggled.punch
			while (KeyBinds.nod.wasPressed()) IsToggled.nod = !IsToggled.nod
			while (KeyBinds.shift.wasPressed()) IsToggled.shift = !IsToggled.shift

			val player = client.player
			if (player == null) {
				//Reset all datas
				tPunch = -1L
				tNod = -1L
				tShift = -1L
				IsToggled.punch = false
				IsToggled.nod = false
				IsToggled.shift = false
				return@register
			}

			val ms = System.currentTimeMillis()

			if (IsToggled.punch) {
				if (tPunch == -1L) tPunch = ms
				else if (tPunch < ms) {
					KeyBinding.onKeyPressed(KeyBindingHelper.getBoundKeyOf(client.options.attackKey))
					tPunch = ms + config.punch.delay
				}
			}

			//Shift first cuz nod complicated
			if (IsToggled.shift) {
				if (tShift == -1L) {
					tShift = ms
				}
				else if (tShift < ms) {
					KeyBinding.setKeyPressed(KeyBindingHelper.getBoundKeyOf(client.options.sneakKey), true)
					Thread {
						Thread.sleep(config.sneak.releaseDelay)
						KeyBinding.setKeyPressed(KeyBindingHelper.getBoundKeyOf(client.options.sneakKey), false)
						tShift = ms + config.sneak.holdDelay
					}.start()
				}
			}

			if (IsToggled.nod) {
				if (tNod == -1L) tNod = ms
				else if (tNod < ms) {
					val currentPitch = player.pitch
					val targetPitch =
						if (isNoddingDown) config.nod.degreeUp else config.nod.degreeDown
					var deltaAngle = targetPitch - currentPitch
					//(-180; 180)
					if (deltaAngle >= 180) {
						deltaAngle -= 360f
					} else if (deltaAngle < -180) {
						deltaAngle += 360f
					}
					val newPitch =
						(currentPitch + sign(deltaAngle.toDouble()) * min(
							config.nod.speed.toDouble(),
							abs(deltaAngle.toDouble())
						)).toFloat()
					player.pitch = newPitch

					isNoddingDown = newPitch != config.nod.degreeUp && (newPitch == config.nod.degreeDown || isNoddingDown)
					tNod = System.currentTimeMillis() + (if (isNoddingDown) config.nod.delayUp else config.nod.delayDown)
				}
			}
		}
	}
}