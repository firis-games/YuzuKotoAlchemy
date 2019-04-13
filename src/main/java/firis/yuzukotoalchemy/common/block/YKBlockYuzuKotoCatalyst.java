package firis.yuzukotoalchemy.common.block;

import firis.yuzukotoalchemy.YuzuKotoAlchemy;
import firis.yuzukotoalchemy.common.tileentity.YKTileYuzuKotoCatalyst;
import firis.yuzukotoalchemy.common.tileentity.YKTileYuzuKotoCatalyst.CircleTier;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

public class YKBlockYuzuKotoCatalyst extends BlockContainer {

	public YKBlockYuzuKotoCatalyst() {
		
		super(Material.GLASS);
		
		this.setCreativeTab(YuzuKotoAlchemy.YuzuKotoCreativeTab);
		this.setLightLevel(1.0F);
		
	}
	
	@Override
	public EnumBlockRenderType getRenderType(IBlockState state)
    {
        return EnumBlockRenderType.MODEL;
    }
	
    @Override
    public boolean isOpaqueCube(IBlockState state)
    {
        return true;
    }

    @Override
    public boolean isFullCube(IBlockState state)
    {
        return true;
    }

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new YKTileYuzuKotoCatalyst();
	}

	@Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
		//メインのみ
		if (hand == EnumHand.OFF_HAND) {
			return false;
		}
		
		TileEntity tile = worldIn.getTileEntity(pos);
		
		if (tile instanceof YKTileYuzuKotoCatalyst) {
			
			ItemStack stack = playerIn.getHeldItemMainhand();
			YKTileYuzuKotoCatalyst catalyst = (YKTileYuzuKotoCatalyst) tile;
			
			if (catalyst.getCircleTier() == CircleTier.NONE) {
				if (catalyst.activateCatalyst(stack)) {
					return true;
				}
			} else if (worldIn.isRemote){
				//クライアントのチャット欄に状態を表示する
				Long genEmc = (long) catalyst.getGenEmc();
				TextComponentString langtext = new TextComponentString(
						String.format(I18n.format("tooltip.emc.generation")
						+ " %d " + I18n.format("tooltip.emc.rate"), genEmc));
				playerIn.sendMessage(langtext);
			}
		}
		
    	return false;
    }
	
    /**
     * Called serverside after this block is replaced with another in Chunk, but before the Tile Entity is updated
     */
	@Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state)
    {
		TileEntity tileentity = worldIn.getTileEntity(pos);
		if (tileentity != null && tileentity instanceof YKTileYuzuKotoCatalyst) {
			YKTileYuzuKotoCatalyst tile = (YKTileYuzuKotoCatalyst) tileentity;
			tile.deactivateCatalyst();
		}
		super.breakBlock(worldIn, pos, state);
    }
}
