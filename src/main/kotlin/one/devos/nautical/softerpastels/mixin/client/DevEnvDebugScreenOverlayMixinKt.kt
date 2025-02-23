package one.devos.nautical.softerpastels.mixin.client

import com.llamalad7.mixinextras.injector.ModifyReturnValue
import gay.asoji.fmw.FMW
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.client.gui.components.DebugScreenOverlay
import one.devos.nautical.softerpastels.SofterPastels
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable

@Mixin(DebugScreenOverlay::class)
abstract class DevEnvDebugScreenOverlayMixinKt {
    @ModifyReturnValue(method = ["getSystemInformation"], at = [At("RETURN")])
    fun appendInfo(messages: java.util.List<String>): java.util.List<String> {
        if (FabricLoader.getInstance().isDevelopmentEnvironment) {
            if (!FabricLoader.getInstance().isModLoaded("desolatedpastels")) {
                messages.add("")
                messages.add("[${FMW.getName(SofterPastels.MOD_ID)} - Development Environment]")
                messages.add("Internal Library Version: ${FMW.getName("innerpastels")}")
                messages.add("Version: ${FMW.getVersion(SofterPastels.MOD_ID)}")
            }
        }
        return messages
    }
}