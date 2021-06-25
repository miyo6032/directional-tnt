package net.barribob.directionaltnt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.NoRenderParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.Vec3d;

@Environment(EnvType.CLIENT)
public class DirectionalExplosionParticle extends NoRenderParticle {
    private int age_;
    private final int maxAge_ = 8;
    private final Vec3d direction;

    DirectionalExplosionParticle(ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
        super(clientWorld, d, e, f, 0.0, 0.0, 0.0);
        direction = new Vec3d(g, h, i);
    }

    public void tick() {
        for(int i = 0; i < 6; ++i) {
            double d = this.x + direction.x * age_ + (this.random.nextDouble() - this.random.nextDouble()) * 4.0D;
            double e = this.y + direction.y * age_ + (this.random.nextDouble() - this.random.nextDouble()) * 4.0D;
            double f = this.z + direction.z * age_ + (this.random.nextDouble() - this.random.nextDouble()) * 4.0D;
            this.world.addParticle(ParticleTypes.EXPLOSION, d, e, f, (float)this.age_ / (float)this.maxAge_, 0.0D, 0.0D);
        }

        ++this.age_;
        if (this.age_ == this.maxAge_) {
            this.markDead();
        }
    }

    @Environment(EnvType.CLIENT)
    public static class Factory implements ParticleFactory<DefaultParticleType> {
        public Particle createParticle(DefaultParticleType defaultParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
            return new DirectionalExplosionParticle(clientWorld, d, e, f, g, h, i);
        }
    }
}
