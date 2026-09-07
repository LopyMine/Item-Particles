package net.lopymine.itp.config.particle;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.*;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.*;
import java.util.function.*;
import lombok.*;
import net.lopymine.itp.config.misc.CachedItem;
import net.lopymine.itp.config.range.IntegerRange;
import net.lopymine.itp.element.color.*;
import net.lopymine.itp.element.color.advanced.AdvancedColorProviderRandomStatic;
import net.lopymine.itp.element.predicate.ISpawnPredicate;
import net.lopymine.itp.element.predicate.nbt.*;
import net.lopymine.itp.element.spawner.*;
import net.lopymine.itp.manager.ItemParticleManager.ItemParticleRequest;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import org.jspecify.annotations.NonNull;
import static net.lopymine.mossylib.utils.CodecUtils.option;

@Getter
@Setter
@AllArgsConstructor
public class ParticleHolder {

	public static final Codec<IColorProvider> STANDARD_AND_LIST_COLOR_TYPE_CODEC = Codec.either(IColorProvider.CODEC, IColorProvider.CODEC.listOf())
			.xmap((either) -> {
				Optional<List<IColorProvider>> right = either.right();
				if (right.isPresent()) {
					return new AdvancedColorProvider(new AdvancedColorProviderRandomStatic(), right.get(), 0);
				}
				Optional<IColorProvider> left = either.left();
				return left.orElse(null);
			}, (type) -> {
				if (type instanceof AdvancedColorProvider advancedType) {
					return Either.right(advancedType.getValues());
				}
				return Either.left(type);
			});

	public static final Codec<IColorProvider> STANDARD_AND_ADVANCED_COLOR_TYPE_CODEC = Codec.either(AdvancedColorProvider.CODEC, STANDARD_AND_LIST_COLOR_TYPE_CODEC)
			.xmap((either) -> {
				Optional<IColorProvider> right = either.right();
				if (right.isPresent()) {
					return right.get();
				}
				Optional<AdvancedColorProvider> left = either.left();
				return left.orElse(null);
			}, (type) -> {
				if (type instanceof AdvancedColorProvider advancedType) {
					return Either.left(advancedType);
				}
				return Either.right(type);
			});

	public static final Codec<IParticleSpawnArea> ADVANCED_SPAWN_AREA_CODEC = Codec.either(AdvancedSpawnAreas.CODEC, Codec.STRING).xmap((either) -> {
		Either<AdvancedSpawnAreas, IParticleSpawnArea> optional = either.mapRight((s) -> {
			if (s.startsWith("#")) {
				if (s.toLowerCase(Locale.ROOT).equals("#full")) {
					return AdvancedSpawnAreas.FULL;
				}
				throw new IllegalArgumentException("Unknown advanced spawn area format: " + s);
			} else {
				AdvancedSpawnAreaId id = AdvancedSpawnAreaId.read(null, s).getOrThrow();
				return new AdvancedSpawnAreas(new AbstractMap<>() {

					@Override
					public boolean isEmpty() {
						return false;
					}

					@Override
					public AdvancedSpawnAreaId get(Object key) {
						return id;
					}

					@Override
					public @NonNull Set<Entry<Identifier, AdvancedSpawnAreaId>> entrySet() {
						return Set.of();
					}
				});
			}
		});
		return optional.right().orElseGet(() -> optional.left().orElseThrow());
	}, (areas) -> {
		return Either.right("incompatible format");
	});

	public static final Codec<Identifier> TAG_CODEC = Codec.STRING.comapFlatMap((s) -> {
		if (s.startsWith("#")) {
			return Identifier.read(s.substring(1));
		}
		return DataResult.error(() -> "Failed to resolve and item, and tag from string \"%s\"".formatted(s));
	}, Identifier::toString);

	public static final Codec<ParticleHolder> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			option("name", (Supplier<String>) () -> "UnknownParticle@" + RandomSource.create().nextIntBetweenInclusive(0, 100000), Codec.STRING, ParticleHolder::getName),
			option("item", Either.left(new CachedItem()), Codec.either(CachedItem.CODEC, TAG_CODEC), ParticleHolder::getItemOrTag),
			option("nbt_conditions_match", NbtNodeMatch.ANY, NbtNodeMatch.CODEC, ParticleHolder::getMatch),
			option("nbt_conditions", new HashSet<>(), NbtNode.CODEC, ParticleHolder::getNbtCondition),
			option("spawn_area", AdvancedSpawnAreas.EMPTY, ADVANCED_SPAWN_AREA_CODEC, ParticleHolder::getSpawnArea),
			option("spawn_count", new IntegerRange(), IntegerRange.CODEC, ParticleHolder::getSpawnCount),
			option("spawn_frequency", new IntegerRange(), IntegerRange.CODEC, ParticleHolder::getSpawnFrequency),
			option("color", new StandardColorProvider(), STANDARD_AND_ADVANCED_COLOR_TYPE_CODEC, ParticleHolder::getColor),
			option("speed_coefficient", 0.0D, Codec.DOUBLE, ParticleHolder::getSpeedCoefficient)
	).apply(instance, ParticleHolder::new));

	private String name;
	private Either<CachedItem, Identifier> itemOrTag;
	private NbtNodeMatch match;
	private HashSet<NbtNode> nbtCondition;
	private IParticleSpawnArea spawnArea;
	private IntegerRange spawnCount;
	private IntegerRange spawnFrequency;
	private IColorProvider color;
	private double speedCoefficient;

	public ParticleSpawner createSpawner(Function<SpawnContext, ItemParticleRequest> function) {
		return new ParticleSpawner(
				this.spawnArea,
				this.spawnCount,
				this.spawnFrequency,
				this.speedCoefficient,
				this.color,
				this.getSpawnCondition(),
				function
		);
	}

	public ISpawnPredicate getSpawnCondition() {
		return new NbtSpawnPredicate(this.name, this.nbtCondition, this.match);
	}
}
