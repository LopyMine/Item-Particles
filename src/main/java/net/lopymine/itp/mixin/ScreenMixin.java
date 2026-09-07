package net.lopymine.itp.mixin;

import net.lopymine.itp.family.gui.ParticlesLinkingInfo;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Screen.class)
public class ScreenMixin {

	@Shadow public int width;

	@Shadow public int height;

	@Inject(
			at = @At(
					value = "INVOKE",
					//? if >=26.1 {
					/*target = "Lnet/minecraft/client/gui/screens/Screen;extractRenderState(Lnet/minecraft/client/gui/GuiGraphics;IIF)V",
					*///?} else {
					target = "Lnet/minecraft/client/gui/screens/Screen;render(Lnet/minecraft/client/gui/GuiGraphics;IIF)V",
					//?}
					shift = Shift.AFTER
			),
			//? if >=26.1 {
			/*method = "extractRenderStateWithTooltipAndSubtitles"
			*///?} elif >=1.21.9 {
			method = "renderWithTooltipAndSubtitles"
			//?} else {
			/*method = "renderWithTooltip"
			*///?}
	)
	private void renderInventoryParticles(GuiGraphics context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
		ParticlesLinkingInfo.render(context, this.width - 5 - 8, 5, mouseX, mouseY);
	}
}