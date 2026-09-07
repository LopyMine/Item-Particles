package net.lopymine.itp.element.spawner;

import java.util.*;
import java.util.function.*;
import lombok.*;
import net.lopymine.itp.config.ItemParticlesConfig;
import net.lopymine.itp.config.range.IntegerRange;
import net.lopymine.itp.element.base.TickElement;
import net.lopymine.itp.element.color.IColorProvider;
import net.lopymine.itp.element.controller.color.ColorController;
import net.lopymine.itp.element.predicate.ISpawnPredicate;
import net.lopymine.itp.manager.ItemParticleManager.ItemParticleRequest;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

@Getter
@Setter
@AllArgsConstructor
public class ParticleSpawner extends TickElement implements IParticleSpawner {

	private final RandomSource random = RandomSource.create();

	private IParticleSpawnArea spawnArea;
	private IntegerRange countRange;
	private IntegerRange frequencyRange;
	private double speedCoefficient;
	private IColorProvider colorType;
	private ISpawnPredicate spawnCondition;
	private Function<SpawnContext, ItemParticleRequest> function;
	private int nextSpawnTicks = 0;

	public ParticleSpawner(IParticleSpawnArea spawnArea, IntegerRange countRange, IntegerRange frequencyRange, double speedCoefficient, IColorProvider colorType, ISpawnPredicate spawnCondition, Function<SpawnContext, ItemParticleRequest> function) {
		this.spawnArea        = spawnArea;
		this.countRange       = countRange;
		this.frequencyRange   = frequencyRange;
		this.speedCoefficient = speedCoefficient;
		this.colorType        = colorType;
		this.spawnCondition   = spawnCondition;
		this.function         = function;
	}

	public List<ItemParticleRequest> tickAndSpawn(SpawnContext context) {
		this.tick();

		if (this.nextSpawnTicks == 0) {
			int ticks = this.random.nextIntBetweenInclusive(this.frequencyRange.getMin(), this.frequencyRange.getMax());
			int ticksToWaitForNextSpawn = (int) ((((float) ticks)) * ItemParticlesConfig.getInstance().getCoefficientsConfig().getCooldownCoefficient(context.category()));
			this.nextSpawnTicks = this.ticks + ticksToWaitForNextSpawn;
		}

		if (this.ticks < this.nextSpawnTicks) {
			return List.of();
		}

		this.nextSpawnTicks = 0;

		return this.spawn(context);
	}

	@Override
	public List<ItemParticleRequest> spawn(SpawnContext context) {
		return this.createParticles(this.random.nextIntBetweenInclusive(this.countRange.getMin(), this.countRange.getMax()), context, (particle) -> {});
	}

	private List<ItemParticleRequest> createParticles(int spawnCount, SpawnContext context, Consumer<ItemParticleRequest> consumer) {
		if (!this.spawnCondition.test(context.stack())) {
			return List.of();
		}

		SpawnCategory category = context.category();
		ItemParticlesConfig config = ItemParticlesConfig.getInstance();

		if (!config.getParticleConfig().isSpawnEnabled(category)) {
			return List.of();
		}

		if (config.getWhitelistsConfig().getConfig(category).cannotProcess(context.stack().getItem())) {
			return List.of();
		}

		float count = (float) ((((float) spawnCount)) * config.getCoefficientsConfig().getCountCoefficient(category));
		int countOfParticles = count > 0.0F && count < 1.0F ? 1 : (int) count;

		List<ItemParticleRequest> particles = new ArrayList<>();
		for (int i = 0; i < countOfParticles; i++) {
			ItemParticleRequest particle = this.function.apply(context);
			consumer.accept(particle);

			this.setSpawnPos(particle);
			this.setParticleColorController(particle, context);

			particles.add(particle);
		}

		return particles;
	}

	private void setSpawnPos(ItemParticleRequest particle) {
		particle.setSpawnPosFunction(this.getRandomPos(particle.getRandom()));
	}

	@Nullable
	private Function<Identifier, @Nullable IParticleSpawnPos> getRandomPos(RandomSource random) {
		if (this.spawnArea == AdvancedSpawnAreas.FULL) {
			return (ignored) -> AdvancedSpawnPos.FULL_POS;
		}
		if (this.spawnArea.isEmpty()) {
			return null;
		}
		return this.spawnArea.getRandomPosFunction(random);
	}

	private void setParticleColorController(ItemParticleRequest request, SpawnContext context) {
		ItemStack currentItem = context.stack();
		IColorProvider type = this.colorType.copy();
		type.compile(currentItem, request.getRandom());
		request.setColorController(new ColorController<>(type));
	}
}
