package net.lopymine.itp.t2o;

import com.mojang.blaze3d.platform.NativeImage;
import java.io.InputStream;
import java.util.*;
import lombok.experimental.ExtensionMethod;
import net.lopymine.itp.client.ItemParticlesClient;
import net.lopymine.itp.config.ItemParticlesConfig;
import net.lopymine.itp.extension.NativeImageExtension;
import net.lopymine.mossylib.loader.MossyLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;

@ExtensionMethod(NativeImageExtension.class)
public class Texture2ObjectsManager {

	public static <T> List<T> readFromTexture(NativeImage image, Identifier id, String objectName, Texture2ObjectPixelFilter filter, Texture2Object<T> texture2Object) {
		try {
			List<T> list = new ArrayList<>();
			Texture2Object<Boolean> test = filter.getFilter();

			int width = image.getWidth();
			int height = image.getHeight();
			for (int x = 0; x < width; x++) {
				for (int y = 0; y < height; y++) {
					int color = image.getPixelArgb(x, y);
					if (Boolean.FALSE.equals(test.accept(x, y, width, height, color))) {
						continue;
					}
					T object = texture2Object.accept(x, y, width, height, color);
					if (object != null) {
						list.add(object);
					}
				}
			}

			return list;
		} catch (Exception e) {
			ItemParticlesClient.LOGGER.error("Failed to create {} from texture \"{}\"! Reason:", objectName, id, e);
		}
		return List.of();
	}

	public static <T> List<T> readFromTexture(Identifier id, String objectName, Texture2ObjectPixelFilter filter, Texture2Object<T> texture2Object) {
		try {
			Optional<Resource> optional = Minecraft.getInstance().getResourceManager().getResource(id);
			if (optional.isEmpty()) {
				if (ItemParticlesConfig.getInstance().getMainConfig().isDebugModeEnabled() || MossyLoader.isDevelopmentEnvironment()) {
					ItemParticlesClient.LOGGER.error("Failed to find texture from \"{}\" to create {} from texture!", id, objectName);
				}
				return List.of();
			}
			Resource resource = optional.get();
			InputStream inputStream = resource.open();
			NativeImage image = NativeImage.read(inputStream);

			return readFromTexture(image, id, objectName, filter, texture2Object);
		} catch (Exception e) {
			ItemParticlesClient.LOGGER.error("Failed to load create {} from texture \"{}\"! Reason:", id, e);
		}
		return List.of();
	}

}
