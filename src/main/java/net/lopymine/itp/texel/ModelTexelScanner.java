package net.lopymine.itp.texel;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.vertex.PoseStack;
import java.io.InputStream;
import java.lang.Math;
import java.util.*;
import net.lopymine.itp.extension.NativeImageExtension;
import net.lopymine.itp.mixin.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.texture.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.*;
import net.minecraft.world.item.ItemDisplayContext;
import org.joml.*;
import org.jspecify.annotations.*;
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
//?}
//? if >=1.21.10 {
/*import net.minecraft.client.gui.Font;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.ModelPart.*;
import net.minecraft.client.model.geom.builders.UVPair;
import net.minecraft.client.renderer.block.MovingBlockRenderState;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer.CrumblingOverlay;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState.FoilType;
import net.minecraft.client.renderer.rendertype.*;
import net.minecraft.client.renderer.state.*;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
*///?} else {
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemStack;
//?}

public class ModelTexelScanner {

	//? if >=26.1 {
	/*private static final int FULL_BRIGHT = LightCoordsUtil.FULL_BRIGHT;
	*///?} else {
	private static final int FULL_BRIGHT = LightTexture.FULL_BRIGHT;
	 //?}

	private static final Map<ResourceLocation, Optional<TexelSource>> TEXTURES = new HashMap<>();
	//? if <1.21.10 {
	private static final Map<ResourceLocation, Collection<TextureAtlasSprite>> ATLAS_SPRITES = new HashMap<>();
	//?}

	public static void clearTextureCache() {
		for (Optional<TexelSource> source : TEXTURES.values()) {
			source.ifPresent((texture) -> texture.image().close());
		}

		TEXTURES.clear();
		//? if <1.21.10 {
		ATLAS_SPRITES.clear();
		//?}
	}

	@Nullable
	private static TexelSource getTexture(@Nullable ResourceLocation texture) {
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

	//? if >=1.21.10 {
	/*@Nullable
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
	*///?} else {
	private static Collection<TextureAtlasSprite> getAtlasSprites(TextureAtlas atlas) {
		return ATLAS_SPRITES.computeIfAbsent(atlas.location(), (id) -> List.copyOf(atlas.texturesByName.values()));
	}

	@Nullable
	private static ResourceLocation getTextureLocation(RenderType renderType) {
		if (!(renderType instanceof RenderType.CompositeRenderType composite)
				|| !(composite.state().textureState instanceof RenderStateShard.TextureStateShard textureState)) {
			return null;
		}

		return textureState.texture.orElse(null);
	}

	private static boolean isGlint(ResourceLocation texture) {
		return ItemRenderer.ENCHANTED_GLINT_ITEM.equals(texture) || ItemRenderer.ENCHANTED_GLINT_ENTITY.equals(texture);
	}
	//?}

	//? if >=1.21.10 {
	/*public static void visitPixels(ItemStackRenderState state, PoseStack poseStack, PixelVisitor output) {
		Map<PixelKey, Pixel> pixels = new LinkedHashMap<>();

		state.submit(poseStack, new TexelCollector(collectInto(pixels)), FULL_BRIGHT, OverlayTexture.NO_OVERLAY, 0);

		emitPixels(pixels, output);
	}
	*///?} else {
	public static void visitPixels(ItemStack stack, ItemDisplayContext displayContext, PoseStack poseStack, PixelVisitor output) {
		Minecraft minecraft = Minecraft.getInstance();
		ItemRenderer itemRenderer = minecraft.getItemRenderer();
		BakedModel model = itemRenderer.getModel(stack, minecraft.level, null, 0);

		boolean leftHand = displayContext == ItemDisplayContext.FIRST_PERSON_LEFT_HAND || displayContext == ItemDisplayContext.THIRD_PERSON_LEFT_HAND;

		Map<PixelKey, Pixel> pixels = new LinkedHashMap<>();
		itemRenderer.render(stack, displayContext, leftHand, poseStack, new TexelBufferSource(collectInto(pixels)), FULL_BRIGHT, OverlayTexture.NO_OVERLAY, model);
		emitPixels(pixels, output);
	}
	//?}

	private static RawTexelVisitor collectInto(Map<PixelKey, Pixel> pixels) {
		return (texture, x, y, position, argb) -> pixels
				.computeIfAbsent(new PixelKey(texture, x, y), (key) -> new Pixel(argb, new ArrayList<>()))
				.faceCenters()
				.add(new Vector3f(position));
	}

	private static void emitPixels(Map<PixelKey, Pixel> pixels, PixelVisitor output) {
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

				//? if >=1.21.4 {
				/*int color = image.getPixel(x, y);
				*///?} else {
				int color = NativeImageExtension.getPixelArgb(image, x, y);
				//?}
				if (alpha(color) == 0) {
					continue;
				}

				if (tint != -1) {
					color = multiply(color, tint);
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

	private static int alpha(int argb) {
		//? if >=1.21.2 {
		/*return ARGB.alpha(argb);
		*///?} else {
		return FastColor.ARGB32.alpha(argb);
		//?}
	}

	private static int multiply(int argb, int tint) {
		//? if >=1.21.2 {
		/*return ARGB.multiply(argb, tint);
		*///?} else {
		return FastColor.ARGB32.color(
				FastColor.ARGB32.alpha(argb) * FastColor.ARGB32.alpha(tint) / 255,
				FastColor.ARGB32.red(argb) * FastColor.ARGB32.red(tint) / 255,
				FastColor.ARGB32.green(argb) * FastColor.ARGB32.green(tint) / 255,
				FastColor.ARGB32.blue(argb) * FastColor.ARGB32.blue(tint) / 255
		);
		//?}
	}

	//? if >=1.21.10 {
	/*private static void visitBakedQuad(BakedQuad quad, Matrix4fc transform, int[] tints, RawTexelVisitor output) {
		//? if >=26.1 {
		/^MaterialInfo materialInfo = quad.materialInfo();
		TextureAtlasSprite sprite = materialInfo.sprite();
		^///?} else {
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
		/^int tintIndex = materialInfo.tintIndex();
		int tint = materialInfo.isTinted() && tintIndex < tints.length ? tints[tintIndex] : -1;
		^///?} else {
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
	*///?} else {
	private static void visitBakedQuad(BakedQuad quad, Matrix4fc transform, int tint, RawTexelVisitor output) {
		TextureAtlasSprite sprite = quad.getSprite();

		float minU = sprite.getU0();
		float minV = sprite.getV0();
		float spanU = sprite.getU1() - minU;
		float spanV = sprite.getV1() - minV;

		if (spanU == 0.0F || spanV == 0.0F) {
			return;
		}

		int[] vertices = quad.getVertices();

		visitQuad(
				readPosition(vertices, 0), readPosition(vertices, 1), readPosition(vertices, 2), readPosition(vertices, 3),
				(readU(vertices, 0) - minU) / spanU, (readV(vertices, 0) - minV) / spanV,
				(readU(vertices, 1) - minU) / spanU, (readV(vertices, 1) - minV) / spanV,
				(readU(vertices, 2) - minU) / spanU, (readV(vertices, 2) - minV) / spanV,
				(readU(vertices, 3) - minU) / spanU, (readV(vertices, 3) - minV) / spanV,
				transform, TexelSource.of(sprite), tint, output
		);
	}

	private static Vector3f readPosition(int[] vertices, int vertex) {
		int offset = vertex * 8;

		return new Vector3f(
				Float.intBitsToFloat(vertices[offset]),
				Float.intBitsToFloat(vertices[offset + 1]),
				Float.intBitsToFloat(vertices[offset + 2])
		);
	}

	private static float readU(int[] vertices, int vertex) {
		return Float.intBitsToFloat(vertices[vertex * 8 + 4]);
	}

	private static float readV(int[] vertices, int vertex) {
		return Float.intBitsToFloat(vertices[vertex * 8 + 5]);
	}
	//?}

	//? if >=1.21.10 {
	/*private static void visitModelPart(ModelPart part, PoseStack poseStack, TexelSource source, int tint, RawTexelVisitor output) {
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
	*///?}

	@FunctionalInterface
	public interface PixelVisitor {

		void visit(ResourceLocation texture, int x, int y, int argb, Vector3fc position, Vector3fc[] faceCenters);

	}

	@FunctionalInterface
	private interface RawTexelVisitor {

		void visit(ResourceLocation texture, int x, int y, Vector3fc position, int argb);

	}

	private record TexelSource(ResourceLocation texture, NativeImage image, int width, int height) {

		static TexelSource of(TextureAtlasSprite sprite) {
			SpriteContents contents = sprite.contents();

			return new TexelSource(contents.name(), ((SpriteContentsAccessor) contents).mossy$getOriginalImage(), contents.width(), contents.height());
		}

	}

	//? if >=1.21.10 {
	/*@SuppressWarnings("NullableProblems")
	private record TexelCollector(RawTexelVisitor output) implements SubmitNodeCollector {

		//? if >=26.1 {
		/^@Override
		public void submitItem(PoseStack poseStack, ItemDisplayContext displayContext, int light, int overlay, int color, int[] tints, List<BakedQuad> quads, FoilType foilType) {
			this.visitQuads(poseStack, tints, quads);
		}

		@Override
		public <S> void submitModel(Model<? super S> model, S state, PoseStack poseStack, ResourceLocation texture, int light, int overlay, int outlineColor, @Nullable CrumblingOverlay crumblingOverlay) {
			this.submitModel(model, state, poseStack, getTexture(texture), -1);
		}
		^///?} else {
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
		/^@Override
		public void submitMovingBlock(PoseStack poseStack, MovingBlockRenderState movingBlockRenderState, int outlineColor) {
			// NO-OP
		}
		^///?} else {
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
		/^@Override
		public void submitNameTag(PoseStack poseStack, @Nullable Vec3 nameTagAttachment, int offset, Component name, boolean seeThrough, int lightCoords, CameraRenderState camera) {
			// NO-OP
		}
		^///?} else {
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
		/^@Override
		public void submitBlockModel(PoseStack poseStack, RenderType renderType, List<BlockStateModelPart> parts, int[] tints, int light, int overlay, int outline) {
			// NO-OP
		}
		^///?} else {
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
		/^@Override
		public void submitBreakingBlockModel(PoseStack poseStack, List<BlockStateModelPart> parts, int progress) {
			// NO-OP
		}

		@Override
		public void submitShapeOutline(PoseStack poseStack, VoxelShape shape, RenderType renderType, int color, float width, boolean afterTerrain) {
			// NO-OP
		}
		^///?} elif >=26.1 {
		/^@Override
		public void submitBreakingBlockModel(PoseStack poseStack, BlockStateModel model, long seed, int progress) {
			// NO-OP
		}
		^///?}

		@Override
		public void submitCustomGeometry(PoseStack poseStack, RenderType renderType, CustomGeometryRenderer renderer) {
			// NO-OP
		}

		//? if >=26.2 {
		/^@Override
		public void submitQuadParticleGroup(QuadParticleRenderState particles) {
			// NO-OP
		}

		@Override
		public void submitGizmoPrimitives(Group group, CameraRenderState camera, boolean onTop) {
			// NO-OP
		}
		^///?} else {
		@Override
		public void submitParticleGroup(ParticleGroupRenderer particleGroupRenderer) {
			// NO-OP
		}
		//?}

	}
	*///?} else {
	private record TexelBufferSource(RawTexelVisitor output) implements MultiBufferSource {

		@Override
		public @NonNull VertexConsumer getBuffer(RenderType renderType) {
			return new TexelCollector(this.output, renderType);
		}

	}

	private static class TexelCollector implements VertexConsumer {

		private static final Matrix4f NO_TRANSFORM = new Matrix4f();

		private final RawTexelVisitor output;
		private final boolean glint;
		@Nullable
		private final TextureAtlas atlas;
		@Nullable
		private final TexelSource texture;

		private final Vector3f[] positions = {new Vector3f(), new Vector3f(), new Vector3f(), new Vector3f()};
		private final float[] us = new float[4];
		private final float[] vs = new float[4];

		private int vertexIndex;
		private int color = -1;
		@Nullable
		private TextureAtlasSprite lastSprite;

		private TexelCollector(RawTexelVisitor output, RenderType renderType) {
			this.output = output;
			ResourceLocation location = getTextureLocation(renderType);
			this.glint = location != null && isGlint(location);
			if (location == null || this.glint) {
				this.atlas   = null;
				this.texture = null;
				return;
			}
			this.atlas   = Minecraft.getInstance().getTextureManager().getTexture(location, null) instanceof TextureAtlas atlas ? atlas : null;
			this.texture = this.atlas == null ? getTexture(location) : null;
		}

		@Override
		public void putBulkData(PoseStack.Pose pose, BakedQuad quad, float[] brightness, float red, float green, float blue, float alpha, int[] lights, int overlay, boolean readExistingColor) {
			if (this.glint) {
				return;
			}

			int tint = FastColor.ARGB32.color((int) (alpha * 255.0F), (int) (red * 255.0F), (int) (green * 255.0F), (int) (blue * 255.0F));

			visitBakedQuad(quad, pose.pose(), tint, this.output);
		}

		@Override
		public @NonNull VertexConsumer addVertex(float x, float y, float z) {
			this.positions[this.vertexIndex].set(x, y, z);
			return this;
		}

		@Override
		public @NonNull VertexConsumer setColor(int red, int green, int blue, int alpha) {
			this.color = FastColor.ARGB32.color(alpha, red, green, blue);
			return this;
		}

		@Override
		public @NonNull VertexConsumer setUv(float u, float v) {
			this.us[this.vertexIndex] = u;
			this.vs[this.vertexIndex] = v;

			// every textured vertex sets its uv exactly once, so this doubles as the end of a vertex
			if (++this.vertexIndex == 4) {
				this.vertexIndex = 0;
				this.visitCollectedQuad();
			}

			return this;
		}

		@Override
		public @NonNull VertexConsumer setUv1(int u, int v) {
			return this;
		}

		@Override
		public @NonNull VertexConsumer setUv2(int u, int v) {
			return this;
		}

		@Override
		public @NonNull VertexConsumer setNormal(float x, float y, float z) {
			return this;
		}

		private void visitCollectedQuad() {
			if (this.texture != null) {
				visitQuad(
						this.positions[0], this.positions[1], this.positions[2], this.positions[3],
						this.us[0], this.vs[0],
						this.us[1], this.vs[1],
						this.us[2], this.vs[2],
						this.us[3], this.vs[3],
						NO_TRANSFORM, this.texture, this.color, this.output
				);
				return;
			}

			TextureAtlasSprite sprite = this.findSprite();
			if (sprite == null) {
				return;
			}

			float minU = sprite.getU0();
			float minV = sprite.getV0();
			float spanU = sprite.getU1() - minU;
			float spanV = sprite.getV1() - minV;

			if (spanU == 0.0F || spanV == 0.0F) {
				return;
			}

			visitQuad(
					this.positions[0], this.positions[1], this.positions[2], this.positions[3],
					(this.us[0] - minU) / spanU, (this.vs[0] - minV) / spanV,
					(this.us[1] - minU) / spanU, (this.vs[1] - minV) / spanV,
					(this.us[2] - minU) / spanU, (this.vs[2] - minV) / spanV,
					(this.us[3] - minU) / spanU, (this.vs[3] - minV) / spanV,
					NO_TRANSFORM, TexelSource.of(sprite), this.color, this.output
			);
		}

		@Nullable
		private TextureAtlasSprite findSprite() {
			if (this.atlas == null) {
				return null;
			}

			float u = (this.us[0] + this.us[1] + this.us[2] + this.us[3]) / 4.0F;
			float v = (this.vs[0] + this.vs[1] + this.vs[2] + this.vs[3]) / 4.0F;

			if (this.lastSprite != null && contains(this.lastSprite, u, v)) {
				return this.lastSprite;
			}

			for (TextureAtlasSprite sprite : getAtlasSprites(this.atlas)) {
				if (contains(sprite, u, v)) {
					return this.lastSprite = sprite;
				}
			}

			return null;
		}

		private static boolean contains(TextureAtlasSprite sprite, float u, float v) {
			return u >= sprite.getU0() && u <= sprite.getU1() && v >= sprite.getV0() && v <= sprite.getV1();
		}

	}
	//?}

	private record Pixel(int argb, List<Vector3f> faceCenters) {

	}

	private record PixelKey(ResourceLocation texture, int x, int y) {

	}
}
