package net.lopymine.itp.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.util.RandomSource;
//? if >=1.21.9 {
import net.minecraft.server.level.ParticleStatus;
//?} else {
/*import net.minecraft.client.ParticleStatus;
*///?}

public class ParticlesOptionUtils {

	private ParticlesOptionUtils() {
	}

	public static boolean canSpawnParticle(RandomSource random) {
		ParticleStatus status = Minecraft.getInstance().options.particles().get();

		if (status == ParticleStatus.DECREASED && random.nextInt(3) == 0) {
			status = ParticleStatus.MINIMAL;
		}

		return status != ParticleStatus.MINIMAL;
	}
}
