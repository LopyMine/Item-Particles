package net.lopymine.itp.utils.iac;

import java.util.ArrayList;
import lombok.Getter;
import net.lopymine.itp.utils.NativeImageUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

@Getter
public class RenderedFluidImage extends RenderedItemImage {

	@Nullable
	private final ColorGetter colorGetter;

	public RenderedFluidImage(ArrayList<Pixel> colors, @Nullable ColorGetter colorGetter) {
		super(colors);
		this.colorGetter = colorGetter;
	}

	@Override
	public int getColor(int anotherColor) {
		if (this.colorGetter == null) {
			return anotherColor;
		}

		ClientLevel level = Minecraft.getInstance().level;
		LocalPlayer player = Minecraft.getInstance().player;
		if (level == null || player == null) {
			int tint = this.colorGetter.getFallback(Blocks.AIR.defaultBlockState());
			return NativeImageUtils.applyTint(anotherColor, tint);
		}

		BlockPos pos = player.blockPosition();
		int tint = this.colorGetter.getWorld(level.getBlockState(pos), level, pos);
		return NativeImageUtils.applyTint(anotherColor, tint);
	}

	public interface ColorGetter {

		int getFallback(BlockState state);

		int getWorld(BlockState state, ClientLevel level, BlockPos pos);

	}
}
