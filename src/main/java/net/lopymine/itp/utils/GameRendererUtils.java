package net.lopymine.itp.utils;

import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
//? if >=1.21.9 {
/*import net.minecraft.client.renderer.feature.FeatureRenderDispatcher;
*///?}
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
		/*return Minecraft.getInstance().gameRenderer.getFov(camera, camera.getPartialTickTime(), true);
		*///?} else {
		return (float) Minecraft.getInstance().gameRenderer.getFov(camera, camera.getPartialTickTime(), true);
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
		/*return Minecraft.getInstance().gameRenderer.getFov(camera, camera.getPartialTickTime(), false);
		*///?} else {
		return (float) Minecraft.getInstance().gameRenderer.getFov(camera, camera.getPartialTickTime(), false);
		//?}
		//?}
	}

	public static Matrix4f getViewRotationMatrix(Matrix4f dest) {
		//? if >=26.2 {
		/*return dest.set(Minecraft.getInstance().gameRenderer.gameRenderState().levelRenderState.cameraRenderState.viewRotationMatrix);
		*///?} elif >=26.1 {
		/*return dest.set(Minecraft.getInstance().gameRenderer.getGameRenderState().levelRenderState.cameraRenderState.viewRotationMatrix);
		*///?} else {
		return dest.rotation(GameRendererUtils.getMainCamera().rotation().conjugate(new Quaternionf()));
		//?}
	}
}
