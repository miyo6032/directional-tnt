package net.barribob.directionaltnt.mixin;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.barribob.directionaltnt.DirectionalTnt;
import net.barribob.directionaltnt.DirectionalTntBlock;
import net.barribob.directionaltnt.DirectionalTntEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Iterator;

@Mixin(Explosion.class)
public abstract class ExplosionMixin {
    @Shadow
    @Final
    private World world;

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;I)Z"), method = "affectWorld", locals = LocalCapture.CAPTURE_FAILSOFT)
    public void beforeBlockDestroy(boolean particles, CallbackInfo ci, boolean b1, ObjectArrayList objectArrayList, Iterator var4, BlockPos blockPos, BlockState blockState) {
        Explosion explosion = (Explosion) (Object) this;
        if (!world.isClient && blockState.getBlock() == DirectionalTnt.DIRECTIONAL_TNT && blockState.contains(DirectionalTntBlock.FACING)) {
            DirectionalTntEntity tntEntity = new DirectionalTntEntity(world, (double) blockPos.getX() + 0.5D, blockPos.getY(), (double) blockPos.getZ() + 0.5D, explosion.getCausingEntity(), blockState.get(DirectionalTntBlock.FACING));
            int i = tntEntity.getFuse();
            tntEntity.setFuse((short) (world.random.nextInt(i / 4) + i / 8));
            world.spawnEntity(tntEntity);
        }
    }
}
