package net.barribob.directionaltnt.mixin;

import net.barribob.directionaltnt.DirectionalExplosion;
import net.barribob.directionaltnt.DirectionalTnt;
import net.barribob.directionaltnt.DirectionalTntEntity;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.explosion.ExplosionBehavior;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin {
    @Inject(at = @At("HEAD"), method = "createExplosion", cancellable = true)
    private void onCreateExplosion(Entity entity, DamageSource damageSource, ExplosionBehavior behavior, double x, double y, double z, float power, boolean createFire, Explosion.DestructionType destructionType, CallbackInfoReturnable<Explosion> cir) {
        if (entity instanceof DirectionalTntEntity directionalTntEntity) {
            ServerWorld world = (ServerWorld) (Object) this;
            DirectionalExplosion explosion = new DirectionalExplosion(world, entity, damageSource, behavior, x, y, z, power, createFire, destructionType, directionalTntEntity.getDirection());
            explosion.collectBlocksAndDamageEntities();
            explosion.affectWorld(false);
            if (destructionType == Explosion.DestructionType.NONE) {
                explosion.clearAffectedBlocks();
            }

            for (ServerPlayerEntity serverPlayerEntity : world.getPlayers()) {
                if (serverPlayerEntity.squaredDistanceTo(x, y, z) < 4096.0D) {
                    PacketByteBuf buf = PacketByteBufs.create();
                    new ExplosionS2CPacket(x, y, z, power, explosion.getAffectedBlocks(), explosion.getAffectedPlayers().get(serverPlayerEntity)).write(buf);
                    buf.writeInt(directionalTntEntity.getDirection().getId());
                    ServerPlayNetworking.send(serverPlayerEntity, DirectionalTnt.explosionNetworkId, buf);
                }
            }

            cir.setReturnValue(explosion);
        }
    }
}
