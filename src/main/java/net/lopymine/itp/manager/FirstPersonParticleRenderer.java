package net.lopymine.itp.manager;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.*;
import net.lopymine.itp.particle.ItemParticle;
import net.lopymine.itp.utils.*;
import net.minecraft.client.*;
import net.minecraft.client.renderer.feature.FeatureRenderDispatcher;
//? if >=26.1 {
/*import net.minecraft.client.renderer.state.level.QuadParticleRenderState;
*///?} else {
import net.minecraft.client.renderer.state.QuadParticleRenderState;
//?}
import net.minecraft.world.phys.Vec3;
import org.joml.*;
//? if >=26.2 {
/*import net.minecraft.client.renderer.SubmitNodeStorage;
*///?}

public class FirstPersonParticleRenderer {

	private static final FirstPersonParticleRenderer INSTANCE = new FirstPersonParticleRenderer();

	private final List<ItemParticle> particles = new ArrayList<>();
	private final QuadParticleRenderState renderState = new QuadParticleRenderState();
	//? if >=26.2 {
	/*private final SubmitNodeStorage submitNodeStorage = new SubmitNodeStorage();
	*///?}

	private FirstPersonParticleRenderer() { }

	public static FirstPersonParticleRenderer getInstance() {
		return INSTANCE;
	}

	public static double getViewDepth(Vec3 cameraPos, double x, double y, double z) {
		Vector3fc forward = GameRendererUtils.getMainCamera().forwardVector();
		return (x - cameraPos.x()) * forward.x() + (y - cameraPos.y()) * forward.y() + (z - cameraPos.z()) * forward.z();
	}

	public void add(ItemParticle particle) {
		this.particles.add(particle);
	}

	public void clear() {
		this.particles.clear();
	}

	public void tick() {
		if (Minecraft.getInstance().level != null) {
			return;
		}
		this.clear();
	}

	public void render(float tickProgress) {
		if (this.particles.isEmpty()) {
			return;
		}

		Camera camera = GameRendererUtils.getMainCamera();

		Matrix4f levelToHand = FirstPersonSpace.getToHand(new Matrix4f());
		float sizeScale = 1.0F / FirstPersonSpace.getFovScale();

		this.renderState.clear();

		Iterator<ItemParticle> iterator = this.particles.iterator();
		boolean any = false;

		while (iterator.hasNext()) {
			ItemParticle particle = iterator.next();

			if (!particle.isAlive()) {
				iterator.remove();
				continue;
			}

			if (!particle.isInFrontOfHand(camera, tickProgress)) {
				continue;
			}

			particle.extractInHandSpace(this.renderState, camera, tickProgress, levelToHand, sizeScale);
			any = true;
		}

		if (!any) {
			return;
		}

		Matrix4fStack modelViewStack = RenderSystem.getModelViewStack();
		modelViewStack.pushMatrix().mul(GameRendererUtils.getViewRotationMatrix(new Matrix4f()));

		FeatureRenderDispatcher dispatcher = GameRendererUtils.getFeatureRenderDispatcher();
		//? if >=26.2 {
		/*this.submitNodeStorage.submitQuadParticleGroup(this.renderState);
		dispatcher.renderAllFeatures(this.submitNodeStorage);
		*///?} else {
		dispatcher.getSubmitNodeStorage().submitParticleGroup(this.renderState);
		dispatcher.renderAllFeatures();
		dispatcher.endFrame();
		//?}

		modelViewStack.popMatrix();
	}
}
