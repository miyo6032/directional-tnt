package net.barribob.directionaltnt.mixin;

import net.barribob.directionaltnt.DirectionalExplosion;
import net.barribob.directionaltnt.DirectionalTntClient;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Environment(EnvType.CLIENT)
@Mixin(Explosion.class)
public abstract class ExplosionMixinClient {
    @Shadow @Final private World world;

    @Shadow public abstract void affectWorld(boolean particles);

    @Shadow @Final private double x;

    @Shadow @Final private double y;

    @Shadow @Final private double z;

    @Shadow @Final private Random random;

    @Inject(at = @At(value = "HEAD"), method = "affectWorld", cancellable = true)
    public void onAffectWorld(boolean particles, CallbackInfo ci) {
        Explosion explosion = (Explosion) (Object) this;

        if(particles && explosion instanceof DirectionalExplosion directionalExplosion) {
            Direction direction = directionalExplosion.getDirection();
            double offX = direction.getOffsetX();
            double offY = direction.getOffsetY();
            double offZ = direction.getOffsetZ();
            world.addParticle(DirectionalTntClient.DIRECTIONAL_EXPLOSION_PARTICLE, this.x, this.y, this.z, offX, offY, offZ);
            for(int i = 0; i < 10; i++) {
                world.addParticle(ParticleTypes.CLOUD, x, y, z, (offX + random.nextGaussian() * 0.5) * 0.5, (offY + random.nextGaussian() * 0.5) * 0.5, (offZ + random.nextGaussian() * 0.5) * 0.5);
            }
            affectWorld(false);
            ci.cancel();
        }
    }
}
