package net.lopymine.itp.manager;

import lombok.*;
import net.lopymine.itp.config.ItemParticlesConfig;
import net.lopymine.itp.element.base.TickElement;
import net.lopymine.itp.element.inventory.IElement;
import net.lopymine.mossylib.logger.MossyLogger;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.*;
import net.minecraft.util.RandomSource;

@Getter
@Setter
public abstract class AbstractElementsManager<E extends IElement, R> extends TickElement {

	private final RandomSource random = RandomSource.create();

	protected void runSoft(Runnable runnable, String action) {
		try {
			runnable.run();
		} catch (Exception e) {
			this.getLogger().error("[{}] Unexpected error!", action, e);
			ItemParticlesConfig config = ItemParticlesConfig.getInstance();
			config.getMainConfig().setModEnabled(false);
			config.saveAsync();
			LocalPlayer player = Minecraft.getInstance().player;
			if (player != null) {
				MutableComponent text = Component.literal("[%s] ".formatted(this.getModName())).append(Component.literal("Unexpected error with id \"%s\", please report this issue with your game logs! Item Particles was automatically disabled to prevent spamming ^^".formatted(action)).withStyle(ChatFormatting.RED));
				//? if >=26.1 {
				/*player.sendSystemMessage(text);
				*///?} else {
				player.displayClientMessage(text, false);
				 //?}
			}
		}
	}

	protected abstract String getModName();

	protected abstract MossyLogger getLogger();
}
