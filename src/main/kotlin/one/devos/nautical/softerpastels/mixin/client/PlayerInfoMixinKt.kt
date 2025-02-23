package one.devos.nautical.softerpastels.mixin.client

import com.llamalad7.mixinextras.injector.ModifyReturnValue
import com.mojang.authlib.GameProfile
import net.minecraft.client.multiplayer.PlayerInfo
import net.minecraft.client.resources.PlayerSkin
import net.minecraft.resources.ResourceLocation
import one.devos.nautical.softerpastels.SofterPastels
import one.devos.nautical.softerpastels.utils.CapeUtils
import org.spongepowered.asm.mixin.*
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo
import java.util.function.Supplier


@Mixin(value = [PlayerInfo::class], priority = 1100)
abstract class PlayerInfoMixinKt {
    @JvmField
    @field:Unique
    val DEV_CAPE = ResourceLocation.tryBuild(SofterPastels.MOD_ID, "textures/misc/cape.png")

    @JvmField
    @field:Shadow
    @field:Final
    var profile: GameProfile? = null

    @JvmField
    @field:Mutable
    @field:Shadow
    @field:Final
    var skinLookup: Supplier<PlayerSkin>? = null

    @JvmField
    @field:Unique
    var `softerpastels$texturesLoaded` = false

    @Inject(method = ["<init>"], at = [At("TAIL")])
    private fun replaceSkinInfoIfNeeded(gameProfile: GameProfile, bl: Boolean, ci: CallbackInfo) {
        if (!`softerpastels$texturesLoaded` && CapeUtils.INSTANCE.useDevCape(profile!!.id)) {
            `softerpastels$texturesLoaded` = true
            val original = this.skinLookup
            val devCape = DEV_CAPE
            this.skinLookup = Supplier {
                val originalResult = original!!.get()
                PlayerSkin(originalResult.texture(), originalResult.textureUrl(), devCape, originalResult.elytraTexture(), originalResult.model(), originalResult.secure())
            }
        }
    }

    @ModifyReturnValue(method = ["getSkin"], at = [At("RETURN")])
    private fun replaceSkinCapeIfNeeded(skin: PlayerSkin): PlayerSkin {
        if (DEV_CAPE == skin.capeTexture() && !CapeUtils.INSTANCE.useDevCape(profile!!.id)) {
            val playerSkin = PlayerSkin(skin.texture(), skin.textureUrl(), null, skin.elytraTexture(), skin.model(), skin.secure())

            this.skinLookup = Supplier { playerSkin }
            return playerSkin
        }

        return skin
    }


}