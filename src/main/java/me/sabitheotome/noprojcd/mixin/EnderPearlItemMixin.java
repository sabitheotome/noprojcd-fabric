package me.sabitheotome.noprojcd.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.EnderPearlItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnderPearlItem.class)
public class EnderPearlItemMixin {
	@Inject(at = @At("RETURN"), method = "use")
	private void use(World world, PlayerEntity user, Hand hand,
			CallbackInfoReturnable<TypedActionResult<ItemStack>> info) {
		user.getItemCooldownManager().set((EnderPearlItem) (Object) this, 0);
	}
}