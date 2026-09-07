package net.lopymine.itp.utils;

import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.feature.FeatureRenderDispatcher;
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

	public static FeatureRenderDispatcher getFeatureRenderDispatcher() {
		GameRenderer gameRenderer = Minecraft.getInstance().gameRenderer;
		//? if >=26.2 {
		/*return gameRenderer.featureRenderDispatcher();
		*///?} else {
		return gameRenderer.getFeatureRenderDispatcher();
		//?}
	}

	public static float getLevelFov() {
		//? if >=26.1 {
		/*return GameRendererUtils.getMainCamera().getFov();
		*///?} else {
		Camera camera = GameRendererUtils.getMainCamera();
		return Minecraft.getInstance().gameRenderer.getFov(camera, camera.getPartialTickTime(), true);
		//?}
	}

	public static float getHudFov() {
		//? if >=26.2 {
		/*return Minecraft.getInstance().gameRenderer.gameRenderState().levelRenderState.cameraRenderState.hudFov;
		*///?} elif >=26.1 {
		/*return Minecraft.getInstance().gameRenderer.getGameRenderState().levelRenderState.cameraRenderState.hudFov;
		*///?} else {
		Camera camera = GameRendererUtils.getMainCamera();
		return Minecraft.getInstance().gameRenderer.getFov(camera, camera.getPartialTickTime(), false);
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
