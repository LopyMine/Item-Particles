package net.lopymine.itp.atlas;

import java.util.Set;
import java.util.concurrent.*;
import lombok.Getter;
import net.lopymine.itp.ItemParticles;
import net.lopymine.itp.family.atlas.manager.FamilyParticlesAtlasManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.renderer.texture.*;
import net.minecraft.network.chat.*;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.*;
import org.jetbrains.annotations.Nullable;

public class ItemParticlesAtlasManager {

	public static final Identifier ATLAS_ID = ItemParticles.id("textures/atlas/particles");
	public static final Identifier LEGACY_ATLAS_ID = ItemParticles.id("textures/atlas/iparticles");
	public static final Identifier FOLDER_ID = ItemParticles.id("particles");
	public static final Identifier LEGACY_FOLDER_ID = ItemParticles.id("iparticles");
	private static ItemParticlesAtlasManager INSTANCE;
	@Getter
	private final TextureAtlas atlas;
	@Getter
	private final TextureAtlas legacyAtlas;

	public ItemParticlesAtlasManager() {
		this.atlas = new TextureAtlas(ATLAS_ID);
		this.legacyAtlas = new TextureAtlas(LEGACY_ATLAS_ID);
		Minecraft.getInstance().getTextureManager().register(ATLAS_ID, this.atlas);
		Minecraft.getInstance().getTextureManager().register(LEGACY_ATLAS_ID, this.legacyAtlas);
	}

	public static ItemParticlesAtlasManager getInstance() {
		if (INSTANCE == null) {
			return INSTANCE = new ItemParticlesAtlasManager();
		}
		return INSTANCE;
	}

	public void reload(PreparableReloadListener.PreparationBarrier synchronizer, ResourceManager resourceManager, Executor prepareExecutor, Executor applyExecutor) {
		CompletableFuture<SpriteLoader.Preparations> preparations = stitch(this.atlas, FOLDER_ID, resourceManager, prepareExecutor);
		CompletableFuture<SpriteLoader.Preparations> legacyPreparations = stitch(this.legacyAtlas, LEGACY_FOLDER_ID, resourceManager, prepareExecutor);

		CompletableFuture.allOf(preparations, legacyPreparations)
				.thenCompose(synchronizer::wait)
				.thenAcceptAsync((unused) -> {
					this.atlas.upload(preparations.join());
					this.legacyAtlas.upload(legacyPreparations.join());
				}, applyExecutor);
	}

	private static CompletableFuture<SpriteLoader.Preparations> stitch(TextureAtlas atlas, Identifier folderId, ResourceManager resourceManager, Executor prepareExecutor) {
		//? if >=1.21.9 {
		return SpriteLoader.create(atlas).loadAndStitch(resourceManager, folderId, 0, prepareExecutor, Set.of());
		//?} else {
		/*return SpriteLoader.create(atlas)
				.loadAndStitch(resourceManager, folderId, 0, prepareExecutor)
				.thenCompose(SpriteLoader.Preparations::waitForUpload);
		*///?}
	}

	public void close() {
		this.atlas.close();
		this.legacyAtlas.close();
	}

	public Identifier resolveAtlasId(@Nullable Identifier id, Identifier atlasId) {
		if (id == null || !(ATLAS_ID.equals(atlasId) || LEGACY_ATLAS_ID.equals(atlasId))) {
			return atlasId;
		}
		if (this.atlas.getSprite(id) != getMissingSprite(this.atlas)) {
			return ATLAS_ID;
		}
		return this.legacyAtlas.getSprite(id) == getMissingSprite(this.legacyAtlas) ? ATLAS_ID : LEGACY_ATLAS_ID;
	}

	public TextureAtlasSprite getSprite(@Nullable Identifier id, @Nullable Identifier atlasId) {
		try {
			if (id == null) {
				return this.getMissingSprite();
			}
			if (atlasId == null || ATLAS_ID.equals(atlasId) || LEGACY_ATLAS_ID.equals(atlasId)) {
				TextureAtlasSprite sprite = this.atlas.getSprite(id);
				if (sprite != getMissingSprite(this.atlas)) {
					return sprite;
				}
				TextureAtlasSprite legacySprite = this.legacyAtlas.getSprite(id);
				return legacySprite == getMissingSprite(this.legacyAtlas) ? sprite : legacySprite;
			}
			FamilyParticlesAtlasManager familyManager = FamilyParticlesAtlasManager.get(atlasId.getPath());
			if (familyManager != null) {
				return familyManager.getSprite(id);
			}
			return OtherAtlasManager.getSprite(id, atlasId, this.getMissingSprite());
		} catch (Exception e) {
			if (e.getMessage().equals("Tried to lookup sprite, but atlas is not initialized")) {
				MutableComponent message = Component.literal("[Item Particles] Hey, wait! This error is special, and I don’t know when or why it happens. If you see this message, please report this bug with the !!full game logs!! — they’re important. Thanks!\n")
						.append("\n Another mod might be breaking Item Particles. \n\n To find it, try disabling half of your active mods at a time until the problem goes away, that will help you find that mod quickly. \nThat would be a huge help <3");
				//? if >=26.2 {
				ChatComponent chat = Minecraft.getInstance().gui.hud.getChat();
				//?} else {
				/*ChatComponent chat = Minecraft.getInstance().gui.getChat();
				 *///?}
				//? if >=26.1 {
				chat.addClientSystemMessage(message);
				//?} else {
				/*chat.addMessage(message);
				 *///?}

				ItemParticles.LOGGER.error("TRIED TO GET SPRITE FROM ATLAS, BUT IT'S NOT INITIALIZED YET: " + (id == null ? "null" : id.toString()) + " " + (atlasId == null ? "null" : atlasId.toString()));
			}
			throw e;
		}
	}

	public TextureAtlasSprite getMissingSprite() {
		return getMissingSprite(this.atlas);
	}

	private static TextureAtlasSprite getMissingSprite(TextureAtlas atlas) {
		return /*? if >=1.21 {*/ atlas.missingSprite /*?} else {*/ /*atlas.getSprite(MissingTextureAtlasSprite.getLocation()) *//*?}*/;
	}
}
