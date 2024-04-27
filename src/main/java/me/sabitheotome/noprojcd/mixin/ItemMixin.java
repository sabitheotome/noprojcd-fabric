package me.sabitheotome.noprojcd.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import me.sabitheotome.noprojcd.NoProjectileCooldown;
import net.minecraft.block.Blocks;
import net.minecraft.entity.TntEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

@Mixin(Item.class)
public class ItemMixin {
	@Inject(at = @At("HEAD"), method = "use", cancellable = true)
	private void use(World world, PlayerEntity user, Hand hand,
			CallbackInfoReturnable<TypedActionResult<ItemStack>> info) {
		if (!NoProjectileCooldown.isEnabled.get())
			return;

		var itemStack = user.getStackInHand(hand);

		var isTNT = itemStack.isOf(Blocks.TNT.asItem());
		var isFireCharge = itemStack.isOf(Items.FIRE_CHARGE);

		if (isTNT || isFireCharge) {

			var projectileEntity = new FireballEntity(world, user, 0, 0, 0, 1);
			projectileEntity.setVelocity(user, user.getPitch(), user.getYaw(), 0.0F, 2.0F, 0.0F);

			if (isFireCharge) {
				world.playSound((PlayerEntity) null, user.getX(), user.getY(), user.getZ(),
						SoundEvents.ENTITY_BLAZE_SHOOT, SoundCategory.NEUTRAL, 0.5F,
						0.4F / (world.getRandom().nextFloat() * 0.4F + 0.8F));
				if (!world.isClient) {
					projectileEntity.setItem(itemStack);
					world.spawnEntity(projectileEntity);
				}
			}

			if (isTNT) {
				world.playSound((PlayerEntity) null, user.getX(), user.getY(), user.getZ(),
						SoundEvents.ENTITY_TNT_PRIMED, SoundCategory.NEUTRAL, 0.5F,
						0.4F / (world.getRandom().nextFloat() * 0.4F + 0.8F));
				if (!world.isClient) {
					var tntEntity = new TntEntity(world, user.getX(), user.getY(), user.getZ(), user);
					tntEntity.setVelocity(projectileEntity.getVelocity());
					world.spawnEntity(tntEntity);
				}
			}

			user.incrementStat(Stats.USED.getOrCreateStat((Item) (Object) this));
			if (!user.getAbilities().creativeMode) {
				itemStack.decrement(1);
			}

			info.setReturnValue(TypedActionResult.consume(itemStack));
		}
	}
}
