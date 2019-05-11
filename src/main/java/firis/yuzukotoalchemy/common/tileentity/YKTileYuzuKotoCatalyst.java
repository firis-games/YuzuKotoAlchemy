package firis.yuzukotoalchemy.common.tileentity;

import javax.annotation.Nullable;

import firis.yuzukotoalchemy.YuzuKotoAlchemy.YuzuKotoBlocks;
import firis.yuzukotoalchemy.common.block.YKBlockCircleStructure;
import moze_intel.projecte.api.item.IItemEmc;
import moze_intel.projecte.api.tile.IEmcAcceptor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

public class YKTileYuzuKotoCatalyst extends TileEntity implements ITickable {

	/**
	 * 練成陣のTier
	 * @author computer
	 *
	 */
	public static enum CircleTier {
		TIER3(3, 3, 50000000L, 1.5f),
		TIER2(2, 2, 10000000L, 1.2f),
		TIER1(1, 1, 1000000L, 1f),
		NONE(0, 0, 0, 0);
		
		private CircleTier(int tier, int range, long cost, double boosts) {
			this.tier = tier;
			this.range = range;
			this.cost = cost;
			this.boosts = boosts;
		}
		private int tier;
		private int range;
		private long cost;
		private double boosts;
		public int getTier() {
			return this.tier;
		}
		public int getRange() {
			return this.range;
		}
		public long getCost() {
			return this.cost;
		}
		public double getBoosts() {
			return this.boosts;
		}
		public static CircleTier getCircleTier(int tier) {
			CircleTier ret = CircleTier.NONE;
			for (CircleTier circleTier : CircleTier.values()) {
				if (circleTier.getTier() == tier) {
					ret = circleTier;
					break;
				}
			}
			return ret;
		}
	}
	
	/**
	 * 練成陣のTier
	 */
	protected CircleTier circleTier = CircleTier.NONE;
	public CircleTier getCircleTier() {
		return this.circleTier;
	}
	
	/**
	 * 1sあたりの練成陣の生成EMC
	 */
	protected long genEmc = 0;
	public long getGenEmc() {
		return this.genEmc;
	}
	/**
	 * 1tickあたりのEMC生成
	 * @return
	 */
	public long getTickGenEmc() {
		return (long) ((double)this.genEmc / 20f);
	}
	
	@Override
	public void update() {
		
		//練成陣未起動時は何もしない
		if (this.getCircleTier() == CircleTier.NONE) return;
		
		if (this.getStructureCircleEmc(this.getCircleTier()) == 0) {
			//構造ブロックが破損
			//練成陣を無効化
			this.deactivateCatalyst();
			return;
		}
		
		//自身のひとつ上のブロックを参照
		TileEntity tile = this.getWorld().getTileEntity(this.getPos().up());
		
		if (tile instanceof IEmcAcceptor) {
			
			IEmcAcceptor emcAcceptor = (IEmcAcceptor) tile;
			//1tickあたりのEMCを加算
			emcAcceptor.acceptEMC(EnumFacing.DOWN, this.getTickGenEmc());
			
		}
	}
	

	/**
	 * 描画範囲を設定する
	 */
	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		
		int range = this.getCircleTier().getRange();
		
		//Rederer描画範囲設定
        return new AxisAlignedBB(pos.add(-range, 0, -range), pos.add(range, 2, range));
	}
	
	
	/**********************************************************************/
	
	/**
	 * 練成陣の発動チェックを行う
	 */
	public boolean activateCatalyst(ItemStack stack) {
		
		//練成陣起動中は何もしない
		if (this.getCircleTier() != CircleTier.NONE) return false;
		
		//EMC充電アイテム確認
		if (stack.getItem() instanceof IItemEmc) {
			
			IItemEmc emcItem = (IItemEmc) stack.getItem();
			double emc = emcItem.getStoredEmc(stack);
			
			CircleTier actCircleTier = CircleTier.NONE;
			double circleEmc = 0;
			
			//最大サイズからチェックする
			for (CircleTier circleTier : CircleTier.values()) {
				
				if (circleTier == CircleTier.NONE) continue;
				
				//EMCチェック
				if (emc < circleTier.getCost()) {
					continue;
				}
				//練成陣の構造をチェック
				double workCircleEmc = this.getStructureCircleEmc(circleTier);
				if (workCircleEmc != 0) {
					actCircleTier = circleTier;
					circleEmc = workCircleEmc;
					break;
				}
			}
			//練成陣をActive
			if (actCircleTier != CircleTier.NONE) {
				
				//EMCを消費
				emcItem.extractEmc(stack, actCircleTier.getCost());
				
				//練成陣をActive化
				this.circleTier = actCircleTier;
				
				this.genEmc = (long)(circleEmc * this.circleTier.getBoosts());
				
				//ブロックを変換
				this.replaceCicleStructure(actCircleTier, true);
				
				return true;
			}
			
		}
		return false;
	}
	
	/**
	 * 練成陣を無効化する
	 */
	public void deactivateCatalyst() {
		
		this.replaceCicleStructure(this.circleTier, false);
		this.circleTier = CircleTier.NONE;
	}
	
	/**********************************************************************/
	
	/**
	 * 練成陣を構成するブロックが出力するEMCを取得する
	 * 練成陣のブロック構造がそもそも成り立っていない場合は0を返却する
	 * @return
	 */
	protected double getStructureCircleEmc(CircleTier circleTier) {
		
		double circleEmc = 0;
		
		//設置ブロックチェック
		int range = circleTier.getRange();
		BlockPos startRange = this.getPos().north(range).west(range);
		BlockPos endRange = this.getPos().south(range).east(range);
		
		//ブロックチェック
		boolean chkPos = true;
		for (BlockPos pos : BlockPos.getAllInBox(startRange, endRange)) {
			//自身はチェックしない
			if (this.getPos().equals(pos)) {
				continue;
			}
			//暫定でダイヤモンドブロックをチェックする
			IBlockState state = this.getWorld().getBlockState(pos);
			if (state.getBlock() != YuzuKotoBlocks.CIRCLE_STRUCTURE) {
				chkPos = false;
				break;
			} else {
				
				YKBlockCircleStructure.EnumTier tier = YKBlockCircleStructure.getTierFromIBlockState(state);
				//ダイヤモンドブロックは1つにつき
				//とりあえず究極とおなじに設定する
				//tickあたりのレートなので1sあたりに置換する
				circleEmc += tier.getEmcRate();
			}
		}
		if (!chkPos) {
			circleEmc = 0;
		}
		
		return circleEmc;
	}
	
	/**
	 * 該当範囲の練成陣構造体を置換する
	 * @param circleTier
	 */
	protected void replaceCicleStructure(CircleTier circleTier, boolean isActive) {
		
		//設置ブロックチェック
		int range = circleTier.getRange();
		BlockPos startRange = this.getPos().north(range).west(range);
		BlockPos endRange = this.getPos().south(range).east(range);
		
		//ブロック置換
		for (BlockPos pos : BlockPos.getAllInBox(startRange, endRange)) {
			//自身はチェックしない
			if (this.getPos().equals(pos)) {
				continue;
			}
			//暫定でダイヤモンドブロックをチェックする
			IBlockState state = this.getWorld().getBlockState(pos);
			if (state.getBlock() == YuzuKotoBlocks.CIRCLE_STRUCTURE) {
				
				//Active
				if (isActive) {
					state = state.withProperty(YKBlockCircleStructure.ACTIVE, YKBlockCircleStructure.EnumActive.ON);
				} else {
					state = state.withProperty(YKBlockCircleStructure.ACTIVE, YKBlockCircleStructure.EnumActive.OFF);
				}
				this.getWorld().setBlockState(pos, state, 3);				
			}
		}
	}
	
	/**********************************************************************/
	
	/**
	 * NBTを読み込みクラスへ反映する処理
	 */
	@Override
	public void readFromNBT(NBTTagCompound compound)
    {
		super.readFromNBT(compound);
		
		this.circleTier = CircleTier.getCircleTier(compound.getInteger("circleTier"));
		this.genEmc = compound.getLong("genEmc");

    }
	
	/**
	 * クラスの情報をNBTへ反映する処理
	 */
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound)
    {
        compound = super.writeToNBT(compound);
        
        compound.setInteger("circleTier", this.circleTier.getTier());
        compound.setDouble("genEmc", this.genEmc);
        
        return compound;
    }
	
	@Override
    public NBTTagCompound getUpdateTag()
    {
		return this.writeToNBT(new NBTTagCompound());
    }
	
	@Override
    public void handleUpdateTag(NBTTagCompound tag)
    {
        this.readFromNBT(tag);
    }
	
	@Override
    @Nullable
    public SPacketUpdateTileEntity getUpdatePacket()
    {
		return new SPacketUpdateTileEntity(this.pos, 0, this.writeToNBT(new NBTTagCompound()));
    }

	@Override
    public void onDataPacket(net.minecraft.network.NetworkManager net, net.minecraft.network.play.server.SPacketUpdateTileEntity pkt)
    {
		this.readFromNBT(pkt.getNbtCompound());
    }
	
}
