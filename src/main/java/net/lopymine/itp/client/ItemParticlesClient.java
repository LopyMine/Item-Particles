package net.lopymine.itp.client;

import java.util.*;
import net.lopymine.itp.ItemParticles;
import net.lopymine.itp.config.ItemParticlesConfig;
import net.lopymine.mossylib.loader.MossyLoader;
import net.lopymine.mossylib.logger.MossyLogger;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.sounds.SoundEvents;
import org.jspecify.annotations.Nullable;
import org.slf4j.helpers.MessageFormatter;

public class ItemParticlesClient {

	@Nullable
	private static Thread VALIDATION_THREAD = null;
	@Nullable
	public static String CURRENT_STACK = null;
	public static MossyLogger LOGGER = ItemParticles.LOGGER.extend("Client");
	private static final Map<String, Set<String>> VALIDATION_ERRORS = new LinkedHashMap<>();

	public static boolean isValidationEnabled() {
		return VALIDATION_THREAD == Thread.currentThread();
	}

	public static void setValidationEnabled(boolean enabled) {
		VALIDATION_THREAD = enabled ? Thread.currentThread() : null;
		if (!enabled) {
			printValidationErrors();
		}
	}

	public static void logValidationError(String message, Object... args) {
		VALIDATION_ERRORS.computeIfAbsent(message, (key) -> new LinkedHashSet<>()).add(MessageFormatter.arrayFormat(message, args).getMessage());
	}

	private static void printValidationErrors() {
		for (Set<String> errors : VALIDATION_ERRORS.values()) {
			for (String error : errors) {
				LOGGER.error(error);
			}
		}
		VALIDATION_ERRORS.clear();
	}

	public static void onInitializeClient() {
		LOGGER.info("{} Client Initialized", ItemParticles.MOD_NAME);
	}

	public static void sendNoticeMessage(int size) {
		if (ItemParticlesConfig.getInstance().isSentNoticeMessage() || Minecraft.getInstance().level == null || Minecraft.getInstance().player == null) {
			return;
		}

		String name = Minecraft.getInstance().getUser().getName();

		MutableComponent message = ItemParticles.text("notice_message", name, size);

		//? if >=26.2 {
		/*ChatComponent chat = Minecraft.getInstance().gui.hud.getChat();
		*///?} else {
		ChatComponent chat = Minecraft.getInstance().gui.getChat();
		 //?}

		//? if >=26.1 {
		/*chat.addClientSystemMessage(message);
		*///?} else {
		chat.addMessage(message);
		 //?}

		Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.EXPERIENCE_ORB_PICKUP, 1.0F, 1.5F));

		if (!MossyLoader.isDevelopmentEnvironment()) {
			ItemParticlesConfig.getInstance().setSentNoticeMessage(true);
			ItemParticlesConfig.getInstance().saveAsync();
		}
	}

}
