package net.lopymine.itp.manager;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.*;
import java.util.Map.Entry;
import java.util.function.Function;
import lombok.*;
import net.lopymine.itp.ItemParticles;
import net.lopymine.itp.config.ItemParticlesConfig;
import net.lopymine.itp.config.particle.ParticleConfig;
import net.lopymine.itp.element.controller.color.ColorController;
import net.lopymine.itp.element.spawner.*;
import net.lopymine.itp.manager.ItemParticleManager.ItemParticleRequest;
import net.lopymine.itp.particle.ItemParticle;
import net.lopymine.itp.resourcepack.manager.ParticlesConfigsManager;
import net.lopymine.itp.texel.ModelTexelScanner;
import net.lopymine.itp.utils.FirstPersonSpace;
import net.lopymine.itp.utils.mixin.RenderStateWithUUID;
import net.lopymine.mossylib.logger.MossyLogger;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.entity.state.*;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.*;
import net.minecraft.world.phys.Vec3;
import org.joml.*;
import org.jspecify.annotations.Nullable;

@Getter
@Setter
public class ItemParticleManager extends AbstractElementsManager<ItemParticle, ItemParticleRequest> {

	private static final ItemParticleManager INSTANCE = new ItemParticleManager();

	private final Set<Vector3fc> debugItemTexels = new HashSet<>();
	private final Set<Vector3fc> debugUsedTexels = new HashSet<>();

	private final Map<UUID, Map<HumanoidArm, List<ItemParticleRequest>>> entityArmParticles = new HashMap<>();
	private final Map<UUID, List<ItemParticleRequest>> droppedItemParticles = new HashMap<>();
	private final Map<UUID, List<ItemParticleRequest>> itemFrameParticles = new HashMap<>();
	private final Map<BlockPos, Map<Integer, List<ItemParticleRequest>>> shelfParticles = new HashMap<>();
	private final Map<BlockPos, List<ItemStack>> trackedShelfItems = new HashMap<>();

	private ItemParticleManager() { }

	public static ItemParticleManager getInstance() {
		return INSTANCE;
	}

	@Nullable
	private static Vector3fc getRandomPosVector(ItemParticleRequest request, Function<Identifier, @Nullable IParticleSpawnPos> spawnPosFunction, SpawnPositions spawnPositions) {
		ArrayList<Identifier> keys = spawnPositions.keys;
		if (keys.isEmpty()) {
			return null;
		}
		Identifier actualTexture = keys.get(request.random.nextInt(0, keys.size()));

		IParticleSpawnPos pos = spawnPosFunction.apply(actualTexture);
		if (pos == null) {
			return null;
		}

		Map<PixelPos, Vector3fc[]> map = spawnPositions.map.get(actualTexture);
		if (map == null) {
			return null;
		}

		Vector3fc[] array = pos.get(request, map);

		if (array == null || array.length == 0) {
			return null;
		}

		return array[request.getRandom().nextInt(array.length)];
	}

	private static SpawnPositions getSpawnPositions(ItemStackRenderState state, PoseStack poseStack) {
		Map<Identifier, Map<PixelPos, Vector3fc[]>> map = new HashMap<>();

		ModelTexelScanner.visitPixels(state, poseStack, (texture, x, y, argb, position, faceCenters) -> {
			PixelPos pos = new PixelPos(x, y);

			map.computeIfAbsent(AdvancedSpawnAreaId.getValidatedBaseTexture(texture), (key) -> new HashMap<>()).put(pos, faceCenters);

			if (ItemParticlesConfig.getInstance().getMainConfig().isDebugModeEnabled()) {
				for (Vector3fc center : faceCenters) {
					ItemParticleManager.getInstance().getDebugItemTexels().add(center);
				}
			}
		});

		return new SpawnPositions(map, new ArrayList<>(map.keySet()));
	}

	@Override
	protected MossyLogger getLogger() {
		return ItemParticles.LOGGER;
	}

	@Override
	protected String getModName() {
		return ItemParticles.MOD_NAME;
	}

	public void acceptDroppedItemParticleRequests(ItemStackRenderState state, PoseStack poseStack, ItemClusterRenderState renderState) {
		UUID uuid = ((RenderStateWithUUID) renderState).itemParticles$getUUID();
		if (uuid == null) {
			return;
		}
		List<ItemParticleRequest> requests = this.droppedItemParticles.get(uuid);
		if (requests == null || requests.isEmpty()) {
			return;
		}
		this.acceptItemParticles(state, poseStack, requests);
		this.droppedItemParticles.remove(uuid);
	}

	public void acceptItemFrameParticleRequests(ItemStackRenderState state, PoseStack poseStack, ItemFrameRenderState renderState) {
		UUID uuid = ((RenderStateWithUUID) renderState).itemParticles$getUUID();
		if (uuid == null) {
			return;
		}
		this.acceptItemParticles(state, poseStack, this.itemFrameParticles.remove(uuid));
	}

	public void acceptShelfItemParticleRequests(ItemStackRenderState state, PoseStack poseStack, BlockPos blockPos, int slot) {
		Map<Integer, List<ItemParticleRequest>> slots = this.shelfParticles.get(blockPos);
		if (slots == null) {
			return;
		}

		this.acceptItemParticles(state, poseStack, slots.remove(slot));

		if (slots.isEmpty()) {
			this.shelfParticles.remove(blockPos);
		}
	}

	public void trackShelfItems(BlockPos blockPos, List<ItemStack> items) {
		this.trackedShelfItems.put(blockPos.immutable(), new ArrayList<>(items));
	}

	public void acceptArmedItemParticleRequests(ItemStackRenderState state, PoseStack poseStack, HumanoidArm arm, ArmedEntityRenderState entityState) {
		UUID uuid = ((RenderStateWithUUID) entityState).itemParticles$getUUID();
		if (uuid == null) {
			return;
		}
		this.acceptItemParticles(state, poseStack, this.pollArmRequests(uuid, arm), null);
	}

	public void acceptFirstPersonItemParticleRequests(ItemStackRenderState state, PoseStack poseStack, ItemDisplayContext displayContext, LivingEntity entity) {
		HumanoidArm arm = switch (displayContext) {
			case FIRST_PERSON_RIGHT_HAND -> HumanoidArm.RIGHT;
			case FIRST_PERSON_LEFT_HAND -> HumanoidArm.LEFT;
			// Punchy moment
			case THIRD_PERSON_RIGHT_HAND -> isFirstPersonCameraEntity(entity) ? HumanoidArm.RIGHT : null;
			case THIRD_PERSON_LEFT_HAND -> isFirstPersonCameraEntity(entity) ? HumanoidArm.LEFT : null;
			default -> null;
		};

		if (arm == null) {
			return;
		}

		this.acceptItemParticles(state, poseStack, this.pollArmRequests(entity.getUUID(), arm), FirstPersonSpace.getToLevel(new Matrix4f()));
	}

	@Nullable
	private List<ItemParticleRequest> pollArmRequests(UUID uuid, HumanoidArm arm) {
		Map<HumanoidArm, List<ItemParticleRequest>> map = this.entityArmParticles.get(uuid);
		if (map == null || map.isEmpty()) {
			return null;
		}

		List<ItemParticleRequest> requests = map.remove(arm);
		if (map.isEmpty()) {
			this.entityArmParticles.remove(uuid);
		}
		return requests;
	}

	private void acceptItemParticles(ItemStackRenderState state, PoseStack poseStack, @Nullable List<ItemParticleRequest> requests) {
		this.acceptItemParticles(state, poseStack, requests, null);
	}

	private void acceptItemParticles(ItemStackRenderState state, PoseStack poseStack, @Nullable List<ItemParticleRequest> requests, @Nullable Matrix4f transform) {
		if (requests == null || requests.isEmpty()) {
			return;
		}

		Minecraft minecraft = Minecraft.getInstance();
		ClientLevel level = minecraft.level;
		//? if >=26.2 {
		/*Vec3 camera = Minecraft.getInstance().gameRenderer.mainCamera().position();
		*///?} else {
		Vec3 camera = Minecraft.getInstance().gameRenderer.getMainCamera().position();
		 //?}
		if (level == null) {
			return;
		}

		this.debugUsedTexels.clear();
		this.debugItemTexels.clear();

		SpawnPositions spawnPositions = getSpawnPositions(state, poseStack);

		for (ItemParticleRequest request : requests) {
			Function<Identifier, @Nullable IParticleSpawnPos> spawnPosFunction = request.getSpawnPosFunction();
			if (spawnPosFunction == null) {
				continue;
			}

			Vector3fc vector = getRandomPosVector(request, spawnPosFunction, spawnPositions);
			if (vector == null) {
				continue;
			}

			if (transform != null) {
				vector = transform.transformPosition(new Vector3f(vector));
			}

			ItemParticle particle = request.create(vector.x() + camera.x(), vector.y() + camera.y(), vector.z() + camera.z());
			if (particle == null) {
				continue;
			}

			if (transform != null) {
				particle.setSpawnedInHand(true);
				particle.setHandDepth(FirstPersonParticleRenderer.getViewDepth(camera, particle.getX(), particle.getY(), particle.getZ()));
				FirstPersonParticleRenderer.getInstance().add(particle);
			}

			particle.tick();
			Minecraft.getInstance().particleEngine.add(particle);

			if (ItemParticlesConfig.getInstance().getMainConfig().isDebugModeEnabled()) {
				ItemParticleManager.getInstance().getDebugUsedTexels().add(vector);
			}
		}
	}

	public void tick() {
		if (!ItemParticlesConfig.getInstance().getMainConfig().isDebugModeEnabled()) {
			this.debugItemTexels.clear();
			this.debugUsedTexels.clear();
		}
		this.runSoft(() -> {
			this.createArmItemParticleRequests();
			this.createDroppedItemParticleRequests();
			this.createItemFrameParticleRequests();
			this.createShelfItemParticleRequests();
		}, "ticking_item_particles");
	}

	private List<ItemParticleRequest> createParticleRequests(ItemStack stack, SpawnCategory category, Vec3 impulse) {
		List<IParticleSpawner> particleSpawners = ParticlesConfigsManager.getSpawnersForItem(stack.getItem());
		if (particleSpawners == null) {
			return new ArrayList<>();
		}

		SpawnContext context = new SpawnContext(stack, category, impulse.x() * 5, impulse.y() * 5, impulse.z() * 5);
		List<ItemParticleRequest> particles = new ArrayList<>();

		for (IParticleSpawner spawner : particleSpawners) {
			particles.addAll(spawner.tickAndSpawn(context));
		}

		return particles;
	}

	private void createDroppedItemParticleRequests() {
		this.droppedItemParticles.clear();
		ClientLevel level = Minecraft.getInstance().level;
		if (level == null) {
			return;
		}
		for (Entity next : level.entitiesForRendering()) {
			if (!(next instanceof ItemEntity itemEntity)) {
				continue;
			}

			ItemStack stack = itemEntity.getItem();
			if (stack.isEmpty()) {
				continue;
			}

			List<ItemParticleRequest> particleRequests = this.createParticleRequests(stack, SpawnCategory.DROPPED_ITEM, itemEntity.getKnownSpeed());
			if (particleRequests.isEmpty()) {
				continue;
			}
			this.droppedItemParticles.put(itemEntity.getUUID(), particleRequests);
		}
	}

	private void createItemFrameParticleRequests() {
		this.itemFrameParticles.clear();
		ClientLevel level = Minecraft.getInstance().level;
		if (level == null) {
			return;
		}
		for (Entity next : level.entitiesForRendering()) {
			if (!(next instanceof ItemFrame itemFrame)) {
				continue;
			}

			ItemStack stack = itemFrame.getItem();
			if (stack.isEmpty()) {
				continue;
			}

			List<ItemParticleRequest> particleRequests = this.createParticleRequests(stack, SpawnCategory.ITEM_FRAME, Vec3.ZERO);
			if (particleRequests.isEmpty()) {
				continue;
			}
			this.itemFrameParticles.put(itemFrame.getUUID(), particleRequests);
		}
	}

	private void createShelfItemParticleRequests() {
		this.shelfParticles.clear();
		for (Entry<BlockPos, List<ItemStack>> entry : this.trackedShelfItems.entrySet()) {
			List<ItemStack> items = entry.getValue();
			Map<Integer, List<ItemParticleRequest>> slots = new HashMap<>();

			for (int slot = 0; slot < items.size(); slot++) {
				ItemStack stack = items.get(slot);
				if (stack.isEmpty()) {
					continue;
				}

				List<ItemParticleRequest> particleRequests = this.createParticleRequests(stack, SpawnCategory.SHELF, Vec3.ZERO);
				if (particleRequests.isEmpty()) {
					continue;
				}
				slots.put(slot, particleRequests);
			}

			this.shelfParticles.put(entry.getKey(), slots);
		}
		this.trackedShelfItems.clear();
	}

	private void createArmItemParticleRequests() {
		this.entityArmParticles.clear();
		ClientLevel level = Minecraft.getInstance().level;
		if (level == null) {
			return;
		}
		for (Entity next : level.entitiesForRendering()) {
			if (!(next instanceof LivingEntity livingEntity)) {
				continue;
			}

			Vec3 impulse = livingEntity.getKnownSpeed();
			SpawnCategory category = getArmSpawnCategory(livingEntity);

			HashMap<HumanoidArm, List<ItemParticleRequest>> map = new HashMap<>();
			for (HumanoidArm arm : HumanoidArm.values()) {
				ItemStack stack = livingEntity.getItemHeldByArm(arm);
				if (stack.isEmpty()) {
					continue;
				}

				List<ItemParticleRequest> particleRequests = this.createParticleRequests(stack, category, impulse);
				if (particleRequests.isEmpty()) {
					continue;
				}
				map.putIfAbsent(arm, particleRequests);
			}
			this.entityArmParticles.put(livingEntity.getUUID(), map);
		}
	}

	private static SpawnCategory getArmSpawnCategory(LivingEntity entity) {
		return isFirstPersonCameraEntity(entity) ? SpawnCategory.FIRST_PERSON : SpawnCategory.THIRD_PERSON;
	}

	private static boolean isFirstPersonCameraEntity(LivingEntity entity) {
		Minecraft minecraft = Minecraft.getInstance();
		return entity == minecraft.getCameraEntity() && minecraft.options.getCameraType().isFirstPerson();
	}

	@Setter
	@Getter
	public static class ItemParticleRequest {

		private RandomSource random = RandomSource.create();
		@Nullable
		private SpawnContext context;
		@Nullable
		private ColorController<ItemParticle> colorController;
		@Nullable
		private ParticleConfig config;
		@Nullable
		private Function<Identifier, @Nullable IParticleSpawnPos> spawnPosFunction;

		@Nullable
		public ItemParticle create(double x, double y, double z) {
			if (this.config == null || this.context == null) {
				return null;
			}
			return ItemParticle.create(this, x, y, z);
		}

	}

	private record SpawnPositions(Map<Identifier, Map<PixelPos, Vector3fc[]>> map, ArrayList<Identifier> keys) {

	}
}
