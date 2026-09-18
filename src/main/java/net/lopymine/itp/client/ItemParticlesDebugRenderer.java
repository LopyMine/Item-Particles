package net.lopymine.itp.client;

import net.lopymine.itp.config.ItemParticlesConfig;
import net.lopymine.itp.manager.ItemParticleManager;
import net.lopymine.mossylib.utils.ArgbUtils;
import org.joml.Vector3fc;
//? if >=1.21.11 {
/*import net.lopymine.itp.utils.GameRendererUtils;
import net.minecraft.gizmos.Gizmos;
import net.minecraft.world.phys.Vec3;
*///?} else {
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.debug.DebugRenderer;
//?}

public class ItemParticlesDebugRenderer {

	private static final int ITEM_TEXEL_COLOR = ArgbUtils.getArgb(255, 255, 255, 255);
	private static final int USED_TEXEL_COLOR = ArgbUtils.getArgb(255, 255, 0, 255);

	private static final float ITEM_TEXEL_SIZE = 10.0F;
	private static final float USED_TEXEL_SIZE = 15.0F;

	//? if <1.21.11 {
	private static final float POINT_SIZE_TO_BLOCKS = 1.0F / 2000.0F;
	//?}

	private ItemParticlesDebugRenderer() { }

	public static boolean isEnabled() {
		return ItemParticlesConfig.getInstance().getMainConfig().isDebugModeEnabled();
	}

	//? if >=1.21.11 {
	/*public static void emitGizmos() {
		if (!isEnabled()) {
			return;
		}

		Vec3 camera = GameRendererUtils.getPosition(GameRendererUtils.getMainCamera());
		visitTexels((texel, color, size) -> Gizmos.point(camera.add(texel.x(), texel.y(), texel.z()), color, size));
	}
	*///?} else {
	public static void render(PoseStack poseStack, MultiBufferSource bufferSource) {
		if (!isEnabled()) {
			return;
		}

		visitTexels((texel, color, size) -> renderPoint(poseStack, bufferSource, texel, color, size));
	}

	private static void renderPoint(PoseStack poseStack, MultiBufferSource bufferSource, Vector3fc texel, int color, float size) {
		float radius = size * POINT_SIZE_TO_BLOCKS;

		DebugRenderer.renderFilledBox(
				poseStack,
				bufferSource,
				texel.x() - radius,
				texel.y() - radius,
				texel.z() - radius,
				texel.x() + radius,
				texel.y() + radius,
				texel.z() + radius,
				ArgbUtils.getRed(color) / 255.0F,
				ArgbUtils.getGreen(color) / 255.0F,
				ArgbUtils.getBlue(color) / 255.0F,
				ArgbUtils.getAlpha(color) / 255.0F
		);
	}
	//?}

	private static void visitTexels(TexelVisitor visitor) {
		ItemParticleManager manager = ItemParticleManager.getInstance();

		for (Vector3fc texel : manager.getDebugItemTexels()) {
			visitor.visit(texel, ITEM_TEXEL_COLOR, ITEM_TEXEL_SIZE);
		}
		for (Vector3fc texel : manager.getDebugUsedTexels()) {
			visitor.visit(texel, USED_TEXEL_COLOR, USED_TEXEL_SIZE);
		}
	}

	@FunctionalInterface
	private interface TexelVisitor {

		void visit(Vector3fc texel, int color, float size);

	}
}
