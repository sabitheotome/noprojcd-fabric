package me.sabitheotome.noprojcd;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.Text;

import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NoProjectileCooldown implements ModInitializer {

	public static final Logger LOGGER = LoggerFactory.getLogger("no-projectile-cooldown");
	public static final AtomicBoolean isEnabled = new AtomicBoolean(false);

	@Override
	public void onInitialize() {
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			dispatcher
					.register(CommandManager.literal("noprojcd-enable").requires(source -> source.hasPermissionLevel(2))
							.executes(context -> {
								context.getSource().sendFeedback(() -> Text.literal("No Projectile Cooldown enabled"),
										true);
								NoProjectileCooldown.isEnabled.set(true);
								return 0;
							}));
			dispatcher
					.register(
							CommandManager.literal("noprojcd-disable").requires(source -> source.hasPermissionLevel(2))
									.executes(context -> {
										context.getSource().sendFeedback(
												() -> Text.literal("No Projectile Cooldown disabled"),
												true);
										NoProjectileCooldown.isEnabled.set(false);
										return 0;
									}));
		});
	}
}