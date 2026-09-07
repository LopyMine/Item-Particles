package net.lopymine.itp.utils;

import org.joml.Matrix4f;

public class FirstPersonSpace {

	private FirstPersonSpace() {
	}

	public static float getFovScale() {
		double levelTan = Math.tan(Math.toRadians(GameRendererUtils.getLevelFov()) / 2.0D);
		double hudTan = Math.tan(Math.toRadians(GameRendererUtils.getHudFov()) / 2.0D);

		return (float) (levelTan / hudTan);
	}

	public static Matrix4f getToLevel(Matrix4f dest) {
		return FirstPersonSpace.scaleInViewSpace(dest, FirstPersonSpace.getFovScale());
	}

	public static Matrix4f getToHand(Matrix4f dest) {
		return FirstPersonSpace.scaleInViewSpace(dest, 1.0F / FirstPersonSpace.getFovScale());
	}

	private static Matrix4f scaleInViewSpace(Matrix4f dest, float scale) {
		Matrix4f viewRotation = GameRendererUtils.getViewRotationMatrix(new Matrix4f());

		return dest.set(viewRotation).invert().scale(scale, scale, 1.0F).mul(viewRotation);
	}
}
