package net.barribob.directionaltnt;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class DirectionalTnt implements ModInitializer {
	public static final Block DIRECTIONAL_TNT = new Block(FabricBlockSettings.copy(Blocks.TNT));

	@Override
	public void onInitialize() {
		Identifier tntId = new Identifier("directionaltnt", "directional_tnt");
		Registry.register(Registry.BLOCK, tntId, DIRECTIONAL_TNT);
		Registry.register(Registry.ITEM, tntId, new BlockItem(DIRECTIONAL_TNT, new FabricItemSettings().group(ItemGroup.MISC)));
	}
}
