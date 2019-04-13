package firis.yuzukotoalchemy.common.tileentity;

import moze_intel.projecte.api.tile.IEmcAcceptor;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;

public abstract class YKTileEmcGeneratorBase extends TileEntity implements ITickable {

	/**
	 * EMCジェネレータのランク
	 */
	public static enum EnumGenTier implements IStringSerializable {
		TIER1(1, "tier1", 10F),
		TIER2(2, "tier2", 30F),
		TIER3(3, "tier3", 100F);
		private EnumGenTier(int id, String name, double emcRate) {
			this.id = id;
			this.name = name;
			this.emcRate = emcRate;
		}
		private int id;
		private String name;
		private double emcRate;
		public int getId() {
			return this.id;
		}
		@Override
		public String getName() {
			return this.name;
		}
		public double getEmcRate() {
			return this.emcRate;
		}
		public static EnumGenTier getEnumTier(int id) {
			EnumGenTier ret = TIER1;
			for (EnumGenTier tier : EnumGenTier.values()) {
				if (id == tier.getId()) {
					ret = tier;
					break;
				}
			}
			return ret;
		}
	}
	
	public abstract EnumGenTier getGenTier();
	
	@Override
	public void update() {
		
		//周辺ブロックを走査
		//IEmcAcceptorがいればEMCを加算
		for (EnumFacing facing : EnumFacing.values()) {
			
			BlockPos pos = this.getPos().offset(facing);
			TileEntity tile = this.getWorld().getTileEntity(pos);
			if (tile instanceof IEmcAcceptor) {
				IEmcAcceptor emcAcceptor = (IEmcAcceptor) tile;
				//空き容量がある場合は加算
				if (emcAcceptor.getStoredEmc() < emcAcceptor.getMaximumEmc()) {
					emcAcceptor.acceptEMC(facing.getOpposite(), this.getGenTier().getEmcRate() / 20F);
					break;
				}
			}
		}
	}

}
