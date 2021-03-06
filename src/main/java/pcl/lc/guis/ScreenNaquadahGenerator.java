package pcl.lc.guis;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import pcl.common.base.GenericContainerGUI;
import pcl.lc.LanteaCraft;
import pcl.lc.containers.ContainerNaquadahGenerator;
import pcl.lc.tileentity.TileEntityNaquadahGenerator;

public class ScreenNaquadahGenerator extends GenericContainerGUI {

	private static final int guiWidth = 177;
	private static final int guiHeight = 208;

	private final TileEntityNaquadahGenerator tileEntity;
	private ResourceLocation background;

	public ScreenNaquadahGenerator(TileEntityNaquadahGenerator te, EntityPlayer player) {
		super(new ContainerNaquadahGenerator(te, player), guiWidth, guiHeight);
		tileEntity = te;
		background = LanteaCraft.getResource("textures/gui/naquadah_generator_"
				+ LanteaCraft.getProxy().getRenderMode() + ".png");
	}

	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}

	@Override
	protected void drawBackgroundLayer() {
		bindTexture(background, 256, 256);
		drawTexturedRectUV(0, 0, 177, 208, 0, 0, 177d / 256d, 208d / 256d);
		drawTexturedRectUV(48, 96, (80d * (tileEntity.displayEnergy / 100d)) / 100d, 11, 176d / 256d, 0,
				(80d * (tileEntity.displayEnergy / 100d)) / 100d / 256d, 11d / 256d);
	}

	@Override
	protected void drawForegroundLayer() {
		StringBuilder sg = new StringBuilder().append(tileEntity.displayEnergy / 100d);
		if (sg.substring(sg.indexOf(".") + 1).length() != 2)
			sg.append("0");
		sg.append("%");
		int dw = fontRenderer.getStringWidth(sg.toString());
		fontRenderer.drawString(sg.toString(), 48 + ((int) Math.floor((80d - dw) / 2)), 98,
				(tileEntity.displayEnergy > 0.00d) ? 0xFFFFFF : 0x9F0101, true);

		StringBuilder st = new StringBuilder().append("Tank: ").append(tileEntity.displayTankVolume / 100d);
		if (st.substring(st.indexOf(".") + 1).length() != 2)
			st.append("0");
		st.append("%");
		int dm = fontRenderer.getStringWidth(st.toString());
		fontRenderer.drawString(st.toString(), 48 + ((int) Math.floor((80d - dm) / 2)), 110, 0xFFFFFF);
	}

}
