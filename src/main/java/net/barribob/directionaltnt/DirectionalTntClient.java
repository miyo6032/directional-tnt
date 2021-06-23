package net.barribob.directionaltnt;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.minecraft.client.render.entity.TntEntityRenderer;

@Environment(EnvType.CLIENT)
public class DirectionalTntClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.INSTANCE.register(DirectionalTnt.DIRECTIONAL_TNT_ENTITY, TntEntityRenderer::new);
    }
}