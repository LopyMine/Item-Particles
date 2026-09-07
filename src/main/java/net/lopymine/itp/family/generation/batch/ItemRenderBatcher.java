package net.lopymine.itp.family.generation.batch;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.*;
import java.util.function.Consumer;
import net.lopymine.itp.ItemParticles;
import net.lopymine.itp.client.ItemParticlesClient;
import net.lopymine.itp.family.generation.ItemRendering;
import net.lopymine.itp.family.utils.FamilySafeRenderExecutor;
import net.lopymine.itp.texel.ModelTexelScanner;
import net.lopymine.itp.utils.iac.*;
import net.lopymine.itp.utils.iac.RenderedItemImage.Pixel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.item.TrackingItemStackRenderState;
import net.minecraft.world.item.*;
import org.jetbrains.annotations.Nullable;

public class ItemRenderBatcher {

	private static final int WAIT_STEP_MS = 1;
	private static final int MAX_RENDER_TIME = 10000;

	public static RenderedItemImages render(List<ItemRenderRequest> requests) {
		RenderedItemImages images = new RenderedItemImages();
		if (requests.isEmpty() || Minecraft.getInstance().level == null) {
			return images;
		}

		List<ItemRenderRequest> fluidRequests = new ArrayList<>();
		List<ItemRenderRequest> itemRequests = new ArrayList<>();

		for (ItemRenderRequest request : requests) {
			List<ItemRenderRequest> list = request.isFluid() ? fluidRequests : itemRequests;
			list.add(request);
		}

		ItemParticlesClient.LOGGER.info("Batching item images...");
		long a = System.currentTimeMillis();
		renderFluids(fluidRequests, images);
		long b = System.currentTimeMillis();

		ItemParticlesClient.LOGGER.info("Fluids took {} seconds. Amount: {}", (b - a) / 1000D, fluidRequests.size());

		long c = System.currentTimeMillis();
		renderItems(itemRequests, images);
		long d = System.currentTimeMillis();

		ItemParticlesClient.LOGGER.info("Items took {} seconds. Amount: {}", (d - c) / 1000D, itemRequests.size());

		return images;
	}

	private static void renderFluids(List<ItemRenderRequest> requests, RenderedItemImages images) {
		if (requests.isEmpty()) {
			return;
		}

		List<BucketItem> buckets = new ArrayList<>(requests.size());
		for (ItemRenderRequest request : requests) {
			buckets.add(request.bucket());
		}

		BatchResult<List<RenderedFluidImage>> result = new BatchResult<>();
		ItemRendering.renderFluidsIntoImages(buckets, result::complete);

		List<RenderedFluidImage> rendered = awaitAngGet(result, "%s fluids".formatted(buckets.size()));
		if (rendered == null) {
			return;
		}

		for (int i = 0; i < requests.size() && i < rendered.size(); i++) {
			RenderedFluidImage image = rendered.get(i);
			if (image != null) {
				images.putFluid(requests.get(i).item(), image);
			}
		}
	}

	private static void renderItems(List<ItemRenderRequest> list, RenderedItemImages images) {
		List<ItemStack> itemStacks = new ArrayList<>(list.size());
		for (ItemRenderRequest request : list) {
			itemStacks.add(request.item().getDefaultInstance());
		}

		BatchResult<List<RenderedItemImage>> result = new BatchResult<>();

		ItemRendering.renderItems(itemStacks, result::complete);

		List<RenderedItemImage> atlas = awaitAngGet(result, "%s items".formatted(list.size()));
		if (atlas == null) {
			return;
		}

		for (int i = 0; i < list.size(); i++) {
			images.putItem(list.get(i).item(), atlas.get(i));
		}
	}

	@Nullable
	private static <T> T awaitAngGet(BatchResult<T> result, String description) {
		int waited = 0;

		while (!result.isReady() && waited < MAX_RENDER_TIME) {
			try {
				Thread.sleep(WAIT_STEP_MS);
				waited += WAIT_STEP_MS;
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				return null;
			}
		}

		if (!result.isReady()) {
			ItemParticles.LOGGER.error("Skipping rendering of {} because it took too long!", description);
			return null;
		}

		return result.getValue();
	}
}
