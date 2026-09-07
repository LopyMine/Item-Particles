package net.lopymine.itp.texel;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.vertex.PoseStack;
import java.io.InputStream;
import java.lang.Math;
import java.util.*;
import net.lopymine.itp.mixin.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.ModelPart.*;
import net.minecraft.client.model.geom.builders.UVPair;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.block.MovingBlockRenderState;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer.CrumblingOverlay;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState.FoilType;
import net.minecraft.client.renderer.rendertype.*;
import net.minecraft.client.renderer.texture.*;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.*;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.phys.Vec3;
import org.joml.*;
import org.jspecify.annotations.Nullable;
//? if >=26.2 {
/*import net.minecraft.client.renderer.gizmos.DrawableGizmoPrimitives.Group;
import net.minecraft.world.phys.shapes.VoxelShape;
*///?}
//? if >=26.1 {
/*import net.minecraft.client.renderer.block.dispatch.*;
import net.minecraft.client.renderer.state.level.*;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.client.resources.model.geometry.BakedQuad.MaterialInfo;
*///?} else {
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.state.*;
import net.minecraft.world.level.block.state.BlockState;
//?}

public class ModelTexelScanner {

	//? if >=26.1 {
	/*private static final int FULL_BRIGHT = LightCoordsUtil.FULL_BRIGHT;
	*///?} else {
	private static final int FULL_BRIGHT = LightTexture.FULL_BRIGHT;
	 //?}

	private static final Map<Identifier, Optional<TexelSource>> TEXTURES = new HashMap<>();

	public static void clearTextureCache() {
		for (Optional<TexelSource> source : TEXTURES.values()) {
			source.ifPresent((texture) -> texture.image().close());
		}

		TEXTURES.clear();
	}

	@Nullable
	private static TexelSource getTexture(@Nullable Identifier texture) {
		if (texture == null) {
			return null;
		}

		return TEXTURES.computeIfAbsent(texture, (id) -> {
			try (InputStream stream = Minecraft.getInstance().getResourceManager().open(id)) {
				NativeImage image = NativeImage.read(stream);
				return Optional.of(new TexelSource(id, image, image.getWidth(), image.getHeight()));
			} catch (Exception e) {
				return Optional.empty();
			}
		}).orElse(null);
	}

	@Nullable
	private static TexelSource getTexture(@Nullable RenderType renderType) {
		if (renderType == null) {
			return null;
		}

		RenderSetup setup = ((RenderTypeAccessor) renderType).ItemParticles$getState();
		Map<String, ?> textures = ((RenderSetupAccessor) (Object) setup).ItemParticles$getTextures();

		return textures.entrySet()
				.stream()
				.min(Map.Entry.comparingByKey())
				.map((entry) -> getTexture(((TextureBindingAccessor) entry.getValue()).ItemParticles$getLocation()))
				.orElse(null);
	}

	public static void visitPixels(ItemStackRenderState state, PoseStack poseStack, PixelVisitor output) {
		Map<PixelKey, Pixel> pixels = new LinkedHashMap<>();

		state.submit(poseStack, new TexelCollector((texture, x, y, position, argb) -> {
			pixels.computeIfAbsent(new PixelKey(texture, x, y), (key) -> new Pixel(argb, new ArrayList<>()))
					.faceCenters()
					.add(new Vector3f(position));
		}), FULL_BRIGHT, OverlayTexture.NO_OVERLAY, 0);

		for (Map.Entry<PixelKey, Pixel> entry : pixels.entrySet()) {
			PixelKey key = entry.getKey();
			Pixel pixel = entry.getValue();
			List<Vector3f> faceCenters = pixel.faceCenters();

			output.visit(key.texture(), key.x(), key.y(), pixel.argb(), getPixelCenter(faceCenters), faceCenters.toArray(new Vector3fc[0]));
		}
	}

	private static Vector3fc getPixelCenter(List<Vector3f> faceCenters) {
		Vector3f center = new Vector3f();

		for (Vector3f faceCenter : faceCenters) {
			center.add(faceCenter);
		}

		return center.div(faceCenters.size());
	}

	private static void visitQuad(
			Vector3fc position0, Vector3fc position1, Vector3fc position2, Vector3fc position3,
			float u0, float v0, float u1, float v1, float u2, float v2, float u3, float v3,
			Matrix4fc transform, TexelSource source, int tint, RawTexelVisitor output
	) {
		@SuppressWarnings("all")
		NativeImage image = source.image();

		int width = source.width();
		int height = source.height();

		int stepsU = getTexelCount(u1 - u0, v1 - v0, width, height);
		int stepsV = getTexelCount(u3 - u0, v3 - v0, width, height);

		Vector3f position = new Vector3f();

		for (int j = 0; j < stepsV; j++) {
			float t = (j + 0.5F) / stepsV;
			for (int i = 0; i < stepsU; i++) {
				float s = (i + 0.5F) / stepsU;
				int x = Mth.clamp((int) (bilinear(u0, u1, u2, u3, s, t) * width), 0, width - 1);
				int y = Mth.clamp((int) (bilinear(v0, v1, v2, v3, s, t) * height), 0, height - 1);

				int color = image.getPixel(x, y);
				if (ARGB.alpha(color) == 0) {
					continue;
				}

				if (tint != -1) {
					color = ARGB.multiply(color, tint);
				}

				position.set(
						bilinear(position0.x(), position1.x(), position2.x(), position3.x(), s, t),
						bilinear(position0.y(), position1.y(), position2.y(), position3.y(), s, t),
						bilinear(position0.z(), position1.z(), position2.z(), position3.z(), s, t)
				).mulPosition(transform);

				output.visit(source.texture(), x, y, position, color);
			}
		}
	}

	private static int getTexelCount(float deltaU, float deltaV, int width, int height) {
		float pixelsU = deltaU * width;
		float pixelsV = deltaV * height;

		return Math.max(1, Math.round((float) Math.sqrt(pixelsU * pixelsU + pixelsV * pixelsV)));
	}

	private static float bilinear(float corner0, float corner1, float corner2, float corner3, float s, float t) {
		return Mth.lerp(t, Mth.lerp(s, corner0, corner1), Mth.lerp(s, corner3, corner2));
	}

	private static void visitBakedQuad(BakedQuad quad, Matrix4fc transform, int[] tints, RawTexelVisitor output) {
		//? if >=26.1 {
		/*MaterialInfo materialInfo = quad.materialInfo();
		TextureAtlasSprite sprite = materialInfo.sprite();
		*///?} else {
		TextureAtlasSprite sprite = quad.sprite();
		//?}
		TexelSource source = TexelSource.of(sprite);

		float minU = sprite.getU0();
		float minV = sprite.getV0();
		float spanU = sprite.getU1() - minU;
		float spanV = sprite.getV1() - minV;

		long packedUV0 = quad.packedUV(0);
		long packedUV1 = quad.packedUV(1);
		long packedUV2 = quad.packedUV(2);
		long packedUV3 = quad.packedUV(3);

		//? if >=26.1 {
		/*int tintIndex = materialInfo.tintIndex();
		int tint = materialInfo.isTinted() && tintIndex < tints.length ? tints[tintIndex] : -1;
		*///?} else {
		int tintIndex = quad.tintIndex();
		int tint = quad.isTinted() && tintIndex < tints.length ? tints[tintIndex] : -1;
		//?}

		visitQuad(
				quad.position0(), quad.position1(), quad.position2(), quad.position3(),
				(UVPair.unpackU(packedUV0) - minU) / spanU, (UVPair.unpackV(packedUV0) - minV) / spanV,
				(UVPair.unpackU(packedUV1) - minU) / spanU, (UVPair.unpackV(packedUV1) - minV) / spanV,
				(UVPair.unpackU(packedUV2) - minU) / spanU, (UVPair.unpackV(packedUV2) - minV) / spanV,
				(UVPair.unpackU(packedUV3) - minU) / spanU, (UVPair.unpackV(packedUV3) - minV) / spanV,
				transform, source, tint, output
		);
	}

	private static void visitModelPart(ModelPart part, PoseStack poseStack, TexelSource source, int tint, RawTexelVisitor output) {
		Vector3f corner0 = new Vector3f();
		Vector3f corner1 = new Vector3f();
		Vector3f corner2 = new Vector3f();
		Vector3f corner3 = new Vector3f();

		part.visit(poseStack, (pose, path, cubeIndex, cube) -> {
			for (Polygon polygon : cube.polygons) {
				Vertex[] vertices = polygon.vertices();
				Vertex vertex0 = vertices[0];
				Vertex vertex1 = vertices[1];
				Vertex vertex2 = vertices[2];
				Vertex vertex3 = vertices[3];

				visitQuad(
						corner0.set(vertex0.worldX(), vertex0.worldY(), vertex0.worldZ()),
						corner1.set(vertex1.worldX(), vertex1.worldY(), vertex1.worldZ()),
						corner2.set(vertex2.worldX(), vertex2.worldY(), vertex2.worldZ()),
						corner3.set(vertex3.worldX(), vertex3.worldY(), vertex3.worldZ()),
						vertex0.u(),
						vertex0.v(),
						vertex1.u(),
						vertex1.v(),
						vertex2.u(),
						vertex2.v(),
						vertex3.u(),
						vertex3.v(),
						pose.pose(),
						source,
						tint,
						output
				);
			}
		});
	}

	private static <T> void setupAnim(Model<T> model, T state) {
		model.setupAnim(state);
	}

	@FunctionalInterface
	public interface PixelVisitor {

		void visit(Identifier texture, int x, int y, int argb, Vector3fc position, Vector3fc[] faceCenters);

	}

	@FunctionalInterface
	private interface RawTexelVisitor {

		void visit(Identifier texture, int x, int y, Vector3fc position, int argb);

	}

	private record TexelSource(Identifier texture, NativeImage image, int width, int height) {

		static TexelSource of(TextureAtlasSprite sprite) {
			SpriteContents contents = sprite.contents();

			return new TexelSource(contents.name(), ((SpriteContentsAccessor) contents).mossy$getOriginalImage(), contents.width(), contents.height());
		}

	}

	@SuppressWarnings("NullableProblems")
	private record TexelCollector(RawTexelVisitor output) implements SubmitNodeCollector {

		//? if >=26.1 {
		/*@Override
		public void submitItem(PoseStack poseStack, ItemDisplayContext displayContext, int light, int overlay, int color, int[] tints, List<BakedQuad> quads, FoilType foilType) {
			this.visitQuads(poseStack, tints, quads);
		}

		@Override
		public <S> void submitModel(Model<? super S> model, S state, PoseStack poseStack, Identifier texture, int light, int overlay, int outlineColor, @Nullable CrumblingOverlay crumblingOverlay) {
			this.submitModel(model, state, poseStack, getTexture(texture), -1);
		}
		*///?} else {
		@Override
		public void submitItem(PoseStack poseStack, ItemDisplayContext displayContext, int light, int overlay, int color, int[] tints, List<BakedQuad> quads, RenderType renderType, FoilType foilType) {
			this.visitQuads(poseStack, tints, quads);
		}
		//?}

		private void visitQuads(PoseStack poseStack, int[] tints, List<BakedQuad> quads) {
			Matrix4f transform = poseStack.last().pose();

			for (BakedQuad quad : quads) {
				visitBakedQuad(quad, transform, tints, this.output);
			}
		}

		@Override
		public <S> void submitModel(Model<? super S> model, S state, PoseStack poseStack, RenderType renderType, int light, int overlay, int color, TextureAtlasSprite sprite, int outline, CrumblingOverlay crumblingOverlay) {
			this.submitModel(model, state, poseStack, getSource(sprite, renderType), color);
		}

		private <S> void submitModel(Model<? super S> model, S state, PoseStack poseStack, @Nullable TexelSource source, int color) {
			if (source == null) {
				return;
			}

			setupAnim(model, state);
			visitModelPart(model.root(), poseStack, source, color, this.output);
		}

		@Nullable
		private static TexelSource getSource(@Nullable TextureAtlasSprite sprite, @Nullable RenderType renderType) {
			return sprite != null ? TexelSource.of(sprite) : getTexture(renderType);
		}

		@Override
		public OrderedSubmitNodeCollector order(int order) {
			return this;
		}

		//? if >=26.2 {
		/*@Override
		public void submitMovingBlock(PoseStack poseStack, MovingBlockRenderState movingBlockRenderState, int outlineColor) {
			// NO-OP
		}
		*///?} else {
		@Override
		public void submitMovingBlock(PoseStack poseStack, MovingBlockRenderState movingBlockRenderState) {
			// NO-OP
		}

		@Override
		public void submitModelPart(ModelPart modelPart, PoseStack poseStack, RenderType renderType, int light, int overlay, @Nullable TextureAtlasSprite sprite, boolean sheeted, boolean hasFoil, int color, @Nullable CrumblingOverlay crumblingOverlay, int outline) {
			TexelSource source = getSource(sprite, renderType);
			if (source == null) {
				return;
			}

			visitModelPart(modelPart, poseStack, source, color, this.output);
		}
		//?}

		@Override
		public void submitShadow(PoseStack poseStack, float radius, List<EntityRenderState.ShadowPiece> pieces) {
			// NO-OP
		}

		//? if >=26.2 {
		/*@Override
		public void submitNameTag(PoseStack poseStack, @Nullable Vec3 nameTagAttachment, int offset, Component name, boolean seeThrough, int lightCoords, CameraRenderState camera) {
			// NO-OP
		}
		*///?} else {
		@Override
		public void submitNameTag(PoseStack poseStack, @Nullable Vec3 nameTagAttachment, int offset, Component name, boolean seeThrough, int lightCoords, double distanceToCameraSq, CameraRenderState camera) {
			// NO-OP
		}
		//?}

		@Override
		public void submitText(PoseStack poseStack, float x, float y, FormattedCharSequence text, boolean dropShadow, Font.DisplayMode displayMode, int light, int color, int backgroundColor, int outline) {
			// NO-OP
		}

		@Override
		public void submitFlame(PoseStack poseStack, EntityRenderState entityRenderState, Quaternionf rotation) {
			// NO-OP
		}

		@Override
		public void submitLeash(PoseStack poseStack, EntityRenderState.LeashState leashState) {
			// NO-OP
		}

		//? if >=26.1 {
		/*@Override
		public void submitBlockModel(PoseStack poseStack, RenderType renderType, List<BlockStateModelPart> parts, int[] tints, int light, int overlay, int outline) {
			// NO-OP
		}
		*///?} else {
		@Override
		public void submitBlockModel(PoseStack poseStack, RenderType renderType, BlockStateModel blockStateModel, float red, float green, float blue, int light, int overlay, int outline) {
			// NO-OP
		}

		@Override
		public void submitBlock(PoseStack poseStack, BlockState blockState, int light, int overlay, int outline) {
			// NO-OP
		}
		//?}

		//? if >=26.2 {
		/*@Override
		public void submitBreakingBlockModel(PoseStack poseStack, List<BlockStateModelPart> parts, int progress) {
			// NO-OP
		}

		@Override
		public void submitShapeOutline(PoseStack poseStack, VoxelShape shape, RenderType renderType, int color, float width, boolean afterTerrain) {
			// NO-OP
		}
		*///?} elif >=26.1 {
		/*@Override
		public void submitBreakingBlockModel(PoseStack poseStack, BlockStateModel model, long seed, int progress) {
			// NO-OP
		}
		*///?}

		@Override
		public void submitCustomGeometry(PoseStack poseStack, RenderType renderType, CustomGeometryRenderer renderer) {
			// NO-OP
		}

		//? if >=26.2 {
		/*@Override
		public void submitQuadParticleGroup(QuadParticleRenderState particles) {
			// NO-OP
		}

		@Override
		public void submitGizmoPrimitives(Group group, CameraRenderState camera, boolean onTop) {
			// NO-OP
		}
		*///?} else {
		@Override
		public void submitParticleGroup(ParticleGroupRenderer particleGroupRenderer) {
			// NO-OP
		}
		//?}

	}

	private record Pixel(int argb, List<Vector3f> faceCenters) {

	}

	private record PixelKey(Identifier texture, int x, int y) {

	}
}
