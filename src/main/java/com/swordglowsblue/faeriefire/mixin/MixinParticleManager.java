package com.swordglowsblue.faeriefire.mixin;

import com.swordglowsblue.faeriefire.FaerieFire;
import com.swordglowsblue.faeriefire.FaerieFireParticle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.util.DyeColor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;

@Mixin(ParticleManager.class)
public abstract class MixinParticleManager {
    @Shadow
    private <T extends ParticleEffect> void registerFactory(ParticleType<T> pt, ParticleFactory<T> pf) {}

    @Inject(method = "net/minecraft/client/particle/ParticleManager.registerDefaultFactories()V", at = @At("RETURN"))
    private void registerCustomFactories() {
        this.registerFactory(FaerieFire.particleTypeFaerieFire,
            (pt, world, x, y, z, vx, vy, vz) -> new FaerieFireParticle(world, x, y, z, DyeColor.LIGHT_BLUE));
    }
}
