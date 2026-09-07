package net.lopymine.itp.element.spawner;

import com.mojang.serialization.DataResult;
import lombok.*;
import net.lopymine.itp.ItemParticles;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;

@Getter
@Setter
public class AdvancedSpawnAreaId {

	//"spawn_area": "2d/trident_drip.png",
	// null id

	//"spawn_area": "#full",
	// null null

	//"minecraft:block/grass_block_side": "#full",
	// id null

	// "minecraft:item/trident": "2d/trident_drip.png",
	// id id

	@Nullable
	private final Identifier baseTexture;
	@Nullable
	private final Identifier maskTexture;

	private boolean initialized;
	@Nullable
	private AdvancedSpawnArea area;

	public AdvancedSpawnAreaId(@Nullable Identifier baseTexture, @Nullable Identifier maskTexture) {
		this.baseTexture = AdvancedSpawnAreaId.getValidatedBaseTexture(baseTexture);
		this.maskTexture = maskTexture;
	}

	public static DataResult<AdvancedSpawnAreaId> read(@Nullable Identifier texture, String maskPath) {
		if (maskPath.isEmpty()) {
			return DataResult.error(() -> "No mask specified for %s".formatted(texture));
		} else if (maskPath.startsWith("#")) {

			//"minecraft:block/grass_block_side": "#full",
			if (maskPath.equals("#full")) {
				return DataResult.success(new AdvancedSpawnAreaId(texture, null));
			}

			return DataResult.error(() -> "Unknown advanced spawn area format: " + maskPath);
		}
		String path = maskPath.endsWith(".png") ? maskPath : maskPath + ".png";

		//"minecraft:block/grass_block_side": "othermod:textures/path_to_spawn_area.png",
		if (maskPath.contains(":")) {
			return Identifier.read(path).map((mask) -> new AdvancedSpawnAreaId(texture, mask));
		}

		// "minecraft:item/trident": "2d/trident_drip.png",
		return DataResult.success(new AdvancedSpawnAreaId(texture, ItemParticles.id("spawn_areas/" + path)));
	}

	@Nullable
	public static Identifier getValidatedBaseTexture(@Nullable Identifier texture) {
		if (texture == null) {
			return null;
		}

		String path = texture.getPath();
		if (path.endsWith(".png")) {
			texture = texture.withPath(texture.getPath().substring(0, path.length() - ".png".length()));
		}

		if (path.startsWith("textures/")) {
			texture = texture.withPath(texture.getPath().substring("textures/".length()));
		}

		return texture;
	}

	@Nullable
	public AdvancedSpawnArea getArea() {
		if (this.area == null && !this.initialized) {
			if (this.maskTexture == null) {
				if (this.baseTexture == null) {
					throw new IllegalArgumentException("Both base and mask textures are null");
				} else {
					this.area = AdvancedSpawnArea.createFull();
				}
			} else {
				this.area = AdvancedSpawnArea.readFromTexture(this.baseTexture, this.maskTexture);
			}
			this.initialized = true;
		}
		return this.area;
	}
}
