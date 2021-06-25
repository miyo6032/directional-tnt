package net.barribob.directionaltnt;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.explosion.Explosion;

@Environment(EnvType.CLIENT)
public class DirectionalTntClient implements ClientModInitializer {
    public static DefaultParticleType DIRECTIONAL_EXPLOSION_PARTICLE = Registry.register(
            Registry.PARTICLE_TYPE,
            new Identifier("directionaltnt", "directional_explosion"),
            FabricParticleTypes.simple()
    );

    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.INSTANCE.register(DirectionalTnt.DIRECTIONAL_TNT_ENTITY, DirectionalTntRenderer::new);
        ClientPlayNetworking.registerGlobalReceiver(DirectionalTnt.explosionNetworkId, (client, handler, buf, responseSender) -> {
            ExplosionS2CPacket packet = new ExplosionS2CPacket(buf);
            Direction direction = Direction.byId(buf.readInt());

            client.execute(() -> {
                Explosion explosion = new DirectionalExplosion(client.world, null, packet.getX(), packet.getY(), packet.getZ(), packet.getRadius(), packet.getAffectedBlocks(), direction);
                explosion.affectWorld(true);
                if (client.player != null) {
                    client.player.setVelocity(client.player.getVelocity().add(packet.getPlayerVelocityX(), packet.getPlayerVelocityY(), packet.getPlayerVelocityZ()));
                }
            });
        });

        ParticleFactoryRegistry.getInstance().register(DIRECTIONAL_EXPLOSION_PARTICLE, new DirectionalExplosionParticle.Factory());
    }
}