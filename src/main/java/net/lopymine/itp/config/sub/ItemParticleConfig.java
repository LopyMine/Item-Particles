package net.lopymine.itp.config.sub;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.function.Supplier;
import lombok.*;
import net.lopymine.itp.element.spawner.SpawnCategory;
import net.lopymine.mossylib.utils.CodecUtils;
import static net.lopymine.mossylib.utils.CodecUtils.option;

@Getter
@Setter
@AllArgsConstructor
public class ItemParticleConfig {

	public static final Codec<ItemParticleConfig> CODEC = RecordCodecBuilder.create(inst -> inst.group(
			option("first_person_spawn_enabled", true, Codec.BOOL, ItemParticleConfig::isFirstPersonSpawnEnabled),
			option("third_person_spawn_enabled", true, Codec.BOOL, ItemParticleConfig::isThirdPersonSpawnEnabled),
			option("dropped_item_spawn_enabled", true, Codec.BOOL, ItemParticleConfig::isDroppedItemSpawnEnabled),
			option("item_frame_spawn_enabled", true, Codec.BOOL, ItemParticleConfig::isItemFrameSpawnEnabled),
			option("shelf_spawn_enabled", true, Codec.BOOL, ItemParticleConfig::isShelfSpawnEnabled),
			option("particle_transparency", 1.0D, Codec.DOUBLE, ItemParticleConfig::getParticleTransparency)
	).apply(inst, ItemParticleConfig::new));

	private boolean firstPersonSpawnEnabled;
	private boolean thirdPersonSpawnEnabled;
	private boolean droppedItemSpawnEnabled;
	private boolean itemFrameSpawnEnabled;
	private boolean shelfSpawnEnabled;
	private double particleTransparency;

	public static Supplier<ItemParticleConfig> getNewInstance() {
		return () -> CodecUtils.parseNewInstanceHacky(CODEC);
	}

	public boolean isSpawnEnabled(SpawnCategory category) {
		return switch (category) {
			case FIRST_PERSON -> this.firstPersonSpawnEnabled;
			case THIRD_PERSON -> this.thirdPersonSpawnEnabled;
			case DROPPED_ITEM -> this.droppedItemSpawnEnabled;
			case ITEM_FRAME -> this.itemFrameSpawnEnabled;
			case SHELF -> this.shelfSpawnEnabled;
		};
	}
}
