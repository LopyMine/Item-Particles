package net.lopymine.itp.client;

import net.lopymine.itp.ItemParticles;
import net.lopymine.itp.config.ItemParticlesConfig;
import net.lopymine.mossylib.loader.MossyLoader;
import net.lopymine.mossylib.logger.MossyLogger;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.*;
import org.jspecify.annotations.Nullable;


public class ItemParticlesClient {

	public static boolean VALIDATION_ENABLED = false;
	@Nullable
	public static String CURRENT_STACK = null;
	public static MossyLogger LOGGER = ItemParticles.LOGGER.extend("Client");

	public static void onInitializeClient() {
		LOGGER.info("{} Client Initialized", ItemParticles.MOD_NAME);

		//Item[] items = {
		//	//? if >=26.2 {
		//	/^Items.BED.red(),
		//	^///?} else {
		//	Items.RED_BED,
		//	//?}
		//		Items.DIAMOND_SWORD,
		//		Items.DIAMOND_SPEAR,
		//		Items.DECORATED_POT,
		//		Items.SHIELD
		//};
//
		//ItemStackRenderState[] states = new ItemStackRenderState[items.length];
//
		//for (int i = 0; i < states.length; i++) {
		//	states[i] = new ItemStackRenderState();
		//}
		//LevelRenderEvents.COLLECT_SUBMITS.register(context -> {
		//	Minecraft minecraft = Minecraft.getInstance();
		//	Vec3 camera = minecraft.gameRenderer.mainCamera().position();
//
		//	for (Vector3fc texel : ItemParticleManager.getInstance().getDebugItemTexels()) {
		//		Gizmos.point(new Vec3(texel.x() + camera.x, texel.y() + camera.y, texel.z() + camera.z), -1, 10F);
		//	}
		//	for (Vector3fc texel : ItemParticleManager.getInstance().getDebugUsedTexels()) {
		//		Gizmos.point(new Vec3(texel.x() + camera.x, texel.y() + camera.y, texel.z() + camera.z), ArgbUtils.getArgb(255, 255, 0, 255), 15F);
		//	}
//
		//	for (int i = 0; i < items.length; i++) {
		//		Vec3 origin = TEST_ORIGIN.add(i * TEST_SPACING, 0.0D, 0.0D);
		//		ItemStackRenderState state = states[i];
//
		//		minecraft.getItemModelResolver().updateForTopItem(state, items[i].getDefaultInstance(), ItemDisplayContext.FIXED, null, null, 0);
//
		//		PoseStack poseStack = new PoseStack();
		//		poseStack.translate((float) (origin.x - camera.x), (float) (origin.y - camera.y), (float) (origin.z - camera.z));
//
		//		state.submit(poseStack, context.submitNodeCollector(), LightCoordsUtil.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, 0);
//
		//		ModelTexelScanner.visitPixels(state, poseStack, (sprite, x, y, argb, position, faceCenters) -> {
		//			for (Vector3fc center : faceCenters) {
		//				Gizmos.point(new Vec3(center.x() + camera.x, center.y() + camera.y, center.z() + camera.z), -1, 10F);
		//			}
		//			//Gizmos.point(new Vec3(position.x() + camera.x, position.y() + camera.y, position.z() + camera.z), ArgbUtils.getArgb(255, 255, 0,0), 10F);
		//		});
		//	}
		//});
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
