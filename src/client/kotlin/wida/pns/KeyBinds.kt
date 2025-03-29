package wida.pns

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.minecraft.client.option.KeyBinding
import net.minecraft.client.util.InputUtil

object KeyBinds {
    val punch = getKey("key.pns.hit")
    val nod = getKey("key.pns.nod")
    val shift = getKey("key.pns.shift")

    fun init() {}

    private fun getKey(key: String): KeyBinding {
        return KeyBindingHelper.registerKeyBinding(KeyBinding(key, InputUtil.UNKNOWN_KEY.code, "key.pns"))
    }
}