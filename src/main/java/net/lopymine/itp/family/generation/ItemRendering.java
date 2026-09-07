package net.lopymine.itp.family.generation;

//? if >=26.2 {

/*import com.mojang.blaze3d.*;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.textures.*;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.*;
import java.util.function.Consumer;
import lombok.experimental.ExtensionMethod;
import net.lopymine.itp.extension.NativeImageExtension;
import net.lopymine.itp.family.utils.FamilySafeRenderExecutor;
import net.lopymine.itp.texel.ModelTexelScanner;
import net.lopymine.itp.utils.iac.*;
import net.lopymine.itp.utils.iac.RenderedFluidImage.ColorGetter;
import net.lopymine.itp.utils.iac.RenderedItemImage.Pixel;
import net.minecraft.client.*;
import net.minecraft.client.color.block.BlockTintSource;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.block.*;
import net.minecraft.client.renderer.item.TrackingItemStackRenderState;
import net.minecraft.client.renderer.texture.*;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.*;

@ExtensionMethod(NativeImageExtension.class)
public class ItemRendering {

	public static void renderItems(List<ItemStack> itemStacks, Consumer<List<RenderedItemImage>> consumer) {
		FamilySafeRenderExecutor.submit(() -> {
			List<RenderedItemImage> list = new ArrayList<>();

			for (ItemStack itemStack : itemStacks) {
				Set<Pixel> colors = new HashSet<>();
				for (ItemDisplayContext context : ItemDisplayContext.values()) {
					TrackingItemStackRenderState renderState = new TrackingItemStackRenderState();
					Minecraft.getInstance().getItemModelResolver().updateForTopItem(
							renderState,
							itemStack,
							context,
							null,
							null,
							0
					);

					ModelTexelScanner.visitPixels(renderState, new PoseStack(), (texture, x, y, argb, position, faceCenters) -> {
						colors.add(new Pixel(texture, x, y, argb));
					});
				}
				list.add(new RenderedItemImage(new ArrayList<>(colors)));
			}

			consumer.accept(list);
		});
	}

	public static void renderFluidsIntoImages(List<BucketItem> bucketItems, Consumer<List<RenderedFluidImage>> consumer) {
		FamilySafeRenderExecutor.submit(() -> {
			FluidStateModelSet set = Minecraft.getInstance().getModelManager().getFluidStateModelSet();

			List<RenderedFluidImage> images = new ArrayList<>();
			for (BucketItem bucketItem : bucketItems) {
				images.add(ItemRendering.createFluidImage(set, bucketItem));
			}

			consumer.accept(images);
		});
	}

	@Nullable
	private static RenderedFluidImage createFluidImage(FluidStateModelSet set, BucketItem bucketItem) {
		FluidModel model = set.get(bucketItem.getContent().defaultFluidState());
		TextureAtlasSprite sprite = model.stillMaterial().sprite();

		if (sprite.contents().name().getPath().equals("missingno")) {
			return null;
		}

		ArrayList<Pixel> pixels = new ArrayList<>();

		NativeImage originalImage = sprite.contents().originalImage;
		int d = Math.min(originalImage.getWidth(), originalImage.getHeight());

		for (int x = 0; x < d; x++) {
			for (int y = 0; y < d; y++) {
				int argb = originalImage.getPixelArgb(x, y);
				pixels.add(new Pixel(sprite.contents().name(), x, y, argb));
			}
		}

		if (pixels.isEmpty()) {
			return null;
		}

		return new RenderedFluidImage(pixels, new ColorGetter() {

			@Nullable
			private final BlockTintSource source = model.tintSource();

			@Override
			public int getFallback(BlockState state) {
				if (this.source == null) {
					return -1;
				}
				return this.source.color(state);
			}

			@Override
			public int getWorld(BlockState state, ClientLevel level, BlockPos pos) {
				if (this.source == null) {
					return -1;
				}
				return this.source.colorInWorld(state, level, pos);
			}
		});
	}

}
*///?} elif >=26.1 {

/*import com.mojang.blaze3d.platform.*;
import com.mojang.blaze3d.textures.*;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.*;
import java.util.function.Consumer;
import lombok.experimental.ExtensionMethod;
import net.lopymine.itp.extension.NativeImageExtension;
import net.lopymine.itp.family.utils.FamilySafeRenderExecutor;
import net.lopymine.itp.texel.ModelTexelScanner;
import net.lopymine.itp.utils.iac.*;
import net.lopymine.itp.utils.iac.RenderedFluidImage.ColorGetter;
import net.lopymine.itp.utils.iac.RenderedItemImage.Pixel;
import net.minecraft.client.*;
import net.minecraft.client.color.block.BlockTintSource;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.*;
import net.minecraft.client.renderer.item.TrackingItemStackRenderState;
import net.minecraft.client.renderer.texture.*;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

@ExtensionMethod(NativeImageExtension.class)
public class ItemRendering {

	public static void renderItems(List<ItemStack> itemStacks, Consumer<List<RenderedItemImage>> consumer) {
		FamilySafeRenderExecutor.submit(() -> {
			List<RenderedItemImage> list = new ArrayList<>();

			for (ItemStack itemStack : itemStacks) {
				Set<Pixel> colors = new HashSet<>();
				for (ItemDisplayContext context : ItemDisplayContext.values()) {
					TrackingItemStackRenderState renderState = new TrackingItemStackRenderState();
					Minecraft.getInstance().getItemModelResolver().updateForTopItem(
							renderState,
							itemStack,
							context,
							null,
							null,
							0
					);

					ModelTexelScanner.visitPixels(renderState, new PoseStack(), (texture, x, y, argb, position, faceCenters) -> {
						colors.add(new Pixel(texture, x, y, argb));
					});
				}
				list.add(new RenderedItemImage(new ArrayList<>(colors)));
			}

			consumer.accept(list);
		});
	}

	public static void renderFluidsIntoImages(List<BucketItem> bucketItems, Consumer<List<RenderedFluidImage>> consumer) {
		FamilySafeRenderExecutor.submit(() -> {
			FluidStateModelSet set = Minecraft.getInstance().getModelManager().getFluidStateModelSet();

			List<RenderedFluidImage> images = new ArrayList<>();
			for (BucketItem bucketItem : bucketItems) {
				images.add(ItemRendering.createFluidImage(set, bucketItem));
			}

			consumer.accept(images);
		});
	}

	@Nullable
	private static RenderedFluidImage createFluidImage(FluidStateModelSet set, BucketItem bucketItem) {
		FluidModel model = set.get(bucketItem.getContent().defaultFluidState());
		TextureAtlasSprite sprite = model.stillMaterial().sprite();

		if (sprite.contents().name().getPath().equals("missingno")) {
			return null;
		}

		ArrayList<Pixel> pixels = new ArrayList<>();

		NativeImage originalImage = sprite.contents().originalImage;
		int d = Math.min(originalImage.getWidth(), originalImage.getHeight());

		for (int x = 0; x < d; x++) {
			for (int y = 0; y < d; y++) {
				int argb = originalImage.getPixelArgb(x, y);
				pixels.add(new Pixel(sprite.contents().name(), x, y, argb));
			}
		}

		if (pixels.isEmpty()) {
			return null;
		}

		return new RenderedFluidImage(pixels, new ColorGetter() {

			@Nullable
			private final BlockTintSource source = model.tintSource();

			@Override
			public int getFallback(BlockState state) {
				if (this.source == null) {
					return -1;
				}
				return this.source.color(state);
			}

			@Override
			public int getWorld(BlockState state, ClientLevel level, BlockPos pos) {
				if (this.source == null) {
					return -1;
				}
				return this.source.colorInWorld(state, level, pos);
			}
		});
	}
}
*///?} elif >=1.21.10 {

import com.mojang.blaze3d.platform.*;
import com.mojang.blaze3d.textures.*;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.*;
import java.util.function.Consumer;
import lombok.experimental.ExtensionMethod;
import net.lopymine.itp.extension.NativeImageExtension;
import net.lopymine.itp.family.utils.FamilySafeRenderExecutor;
import net.lopymine.itp.texel.ModelTexelScanner;
import net.lopymine.itp.utils.iac.*;
import net.lopymine.itp.utils.iac.RenderedFluidImage.ColorGetter;
import net.lopymine.itp.utils.iac.RenderedItemImage.Pixel;
import net.minecraft.client.*;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.*;
import net.minecraft.client.renderer.item.TrackingItemStackRenderState;
import net.minecraft.client.renderer.texture.*;
import net.minecraft.core.BlockPos;
import net.minecraft.data.AtlasIds;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import org.jetbrains.annotations.Nullable;

//? if fabric {
import net.fabricmc.fabric.api.client.render.fluid.v1.*;
//?} else {
/*import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
*///?}

@ExtensionMethod(NativeImageExtension.class)
public class ItemRendering {

	public static void renderItems(List<ItemStack> itemStacks, Consumer<List<RenderedItemImage>> consumer) {
		FamilySafeRenderExecutor.submit(() -> {
			List<RenderedItemImage> list = new ArrayList<>();

			for (ItemStack itemStack : itemStacks) {
				Set<Pixel> colors = new HashSet<>();
				for (ItemDisplayContext context : ItemDisplayContext.values()) {
					TrackingItemStackRenderState renderState = new TrackingItemStackRenderState();
					Minecraft.getInstance().getItemModelResolver().updateForTopItem(
							renderState,
							itemStack,
							context,
							null,
							null,
							0
					);

					ModelTexelScanner.visitPixels(renderState, new PoseStack(), (texture, x, y, argb, position, faceCenters) -> {
						colors.add(new Pixel(texture, x, y, argb));
					});
				}
				list.add(new RenderedItemImage(new ArrayList<>(colors)));
			}

			consumer.accept(list);
		});
	}

	public static void renderFluidsIntoImages(List<BucketItem> bucketItems, Consumer<List<RenderedFluidImage>> consumer) {
		FamilySafeRenderExecutor.submit(() -> {
			List<RenderedFluidImage> images = new ArrayList<>();
			for (BucketItem bucketItem : bucketItems) {
				images.add(ItemRendering.createFluidImage(bucketItem));
			}

			consumer.accept(images);
		});
	}

	@Nullable
	private static RenderedFluidImage createFluidImage(BucketItem bucketItem) {
		FluidState fluidState = bucketItem.getContent().defaultFluidState();
		//? if fabric {
		FluidRenderHandler handler = FluidRenderHandlerRegistry.INSTANCE.get(bucketItem.getContent());
		if (handler == null) {
			return null;
		}

		TextureAtlasSprite[] fluidSprites = handler.getFluidSprites(null, null, fluidState);
		TextureAtlasSprite sprite = fluidSprites[0];

		if (sprite.contents().name().getPath().equals("missingno")) {
			return null;
		}

		ArrayList<Pixel> pixels = new ArrayList<>();

		NativeImage originalImage = sprite.contents().originalImage;
		int d = Math.min(originalImage.getWidth(), originalImage.getHeight());

		for (int x = 0; x < d; x++) {
			for (int y = 0; y < d; y++) {
				int argb = originalImage.getPixelArgb(x, y);
				pixels.add(new Pixel(sprite.contents().name(), x, y, argb));
			}
		}

		if (pixels.isEmpty()) {
			return null;
		}

		return new RenderedFluidImage(pixels, new ColorGetter() {
			@Override
			public int getFallback(BlockState state) {
				return handler.getFluidColor(null, null, fluidState);
			}

			@Override
			public int getWorld(BlockState state, ClientLevel level, BlockPos pos) {
				return handler.getFluidColor(level, pos, fluidState);
			}
		});
		//?} else {

		/*IClientFluidTypeExtensions extensions = IClientFluidTypeExtensions.of(fluidState);
		TextureAtlas atlas;
		try {
			atlas = Minecraft.getInstance().getAtlasManager().getAtlasOrThrow(AtlasIds.BLOCKS);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		Identifier stillTexture = extensions.getStillTexture();
		if (stillTexture == null) { // can be null!! Ignore warning
			return null;
		}

		TextureAtlasSprite sprite = atlas.getTextures().get(stillTexture);
		if (sprite == null) {
			return null;
		}

		ArrayList<Pixel> pixels = new ArrayList<>();

		NativeImage originalImage = sprite.contents().getOriginalImage();
		int d = Math.min(originalImage.getWidth(), originalImage.getHeight());

		for (int x = 0; x < d; x++) {
			for (int y = 0; y < d; y++) {
				int argb = originalImage.getPixelArgb(x, y);
				pixels.add(new Pixel(sprite.contents().name(), x, y, argb));
			}
		}

		if (pixels.isEmpty()) {
			return null;
		}

		return new RenderedFluidImage(pixels, new ColorGetter() {
			@Override
			public int getFallback(BlockState state) {
				return extensions.getTintColor();
			}

			@Override
			public int getWorld(BlockState state, ClientLevel level, BlockPos pos) {
				return extensions.getTintColor(fluidState, level, pos);
			}
		});
		*///?}
	}

}
//?}
