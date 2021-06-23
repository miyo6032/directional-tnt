package net.barribob.directionaltnt;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class DirectionalTnt implements ModInitializer {
	private static final Identifier tntId = new Identifier("directionaltnt", "directional_tnt");
    public static final Block DIRECTIONAL_TNT = new DirectionalTntBlock(FabricBlockSettings.copy(Blocks.TNT));
    public static final EntityType<DirectionalTntEntity> DIRECTIONAL_TNT_ENTITY = Registry.register(Registry.ENTITY_TYPE, tntId,
            FabricEntityTypeBuilder.create(SpawnGroup.MISC, (EntityType.EntityFactory<DirectionalTntEntity>) DirectionalTntEntity::new)
                    .dimensions(EntityDimensions.fixed(1f, 1f)).build()
    );


    @Override
    public void onInitialize() {
        Registry.register(Registry.BLOCK, tntId, DIRECTIONAL_TNT);
        Registry.register(Registry.ITEM, tntId, new BlockItem(DIRECTIONAL_TNT, new FabricItemSettings().group(ItemGroup.MISC)));
    }
}
