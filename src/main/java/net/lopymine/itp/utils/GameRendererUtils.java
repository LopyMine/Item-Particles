package net.lopymine.itp.utils;

import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
//? if >=1.21.9 {
/*import net.minecraft.client.renderer.feature.FeatureRenderDispatcher;
*///?}
//? if <1.21 {
import net.minecraft.util.Mth;
//?}
import net.minecraft.world.phys.Vec3;
import org.joml.*;

public class GameRendererUtils {

	private GameRendererUtils() {
	}

	public static Camera getMainCamera() {
		GameRenderer gameRenderer = Minecraft.getInstance().gameRenderer;
		//? if >=26.2 {
		/*return gameRenderer.mainCamera();
		*///?} else {
		return gameRenderer.getMainCamera();
		//?}
	}

	//? if >=1.21.9 {
	/*public static FeatureRenderDispatcher getFeatureRenderDispatcher() {
		GameRenderer gameRenderer = Minecraft.getInstance().gameRenderer;
		//? if >=26.2 {
		/^return gameRenderer.featureRenderDispatcher();
		^///?} else {
		return gameRenderer.getFeatureRenderDispatcher();
		//?}
	}
	*///?}

	public static Vec3 getPosition(Camera camera) {
		//? if >=1.21.9 {
		/*return camera.position();
		*///?} else {
		return camera.getPosition();
		//?}
	}

	public static float getLevelFov() {
		//? if >=26.1 {
		/*return GameRendererUtils.getMainCamera().getFov();
		*///?} else {
		Camera camera = GameRendererUtils.getMainCamera();
		//? if >=1.21.10 {
		/*return Minecraft.getInstance().gameRenderer.getFov(camera, getPartialTick(camera), true);
		*///?} else {
		return (float) Minecraft.getInstance().gameRenderer.getFov(camera, getPartialTick(camera), true);
		//?}
		//?}
	}

	public static float getHudFov() {
		//? if >=26.2 {
		/*return Minecraft.getInstance().gameRenderer.gameRenderState().levelRenderState.cameraRenderState.hudFov;
		*///?} elif >=26.1 {
		/*return Minecraft.getInstance().gameRenderer.getGameRenderState().levelRenderState.cameraRenderState.hudFov;
		*///?} else {
		Camera camera = GameRendererUtils.getMainCamera();
		//? if >=1.21.10 {
		/*return Minecraft.getInstance().gameRenderer.getFov(camera, getPartialTick(camera), false);
		*///?} else {
		return (float) Minecraft.getInstance().gameRenderer.getFov(camera, getPartialTick(camera), false);
		//?}
		//?}
	}

	//? if <26.1 {
	private static float getPartialTick(Camera camera) {
		//? if >=1.21 {
		/*return camera.getPartialTickTime();
		*///?} else {
		return Minecraft.getInstance().getFrameTime();
		//?}
	}
	//?}

	public static Matrix4f getViewRotationMatrix(Matrix4f dest) {
		//? if >=26.2 {
		/*return dest.set(Minecraft.getInstance().gameRenderer.gameRenderState().levelRenderState.cameraRenderState.viewRotationMatrix);
		*///?} elif >=26.1 {
		/*return dest.set(Minecraft.getInstance().gameRenderer.getGameRenderState().levelRenderState.cameraRenderState.viewRotationMatrix);
		*///?} elif >=1.21 {
		/*return dest.rotation(GameRendererUtils.getMainCamera().rotation().conjugate(new Quaternionf()));
		*///?} else {
		Camera camera = GameRendererUtils.getMainCamera();
		return dest.rotationX(camera.getXRot() * Mth.DEG_TO_RAD).rotateY((camera.getYRot() + 180.0F) * Mth.DEG_TO_RAD);
		//?}
	}
}
