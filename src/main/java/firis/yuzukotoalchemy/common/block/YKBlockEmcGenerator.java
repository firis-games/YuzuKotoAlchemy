package firis.yuzukotoalchemy.common.block;

import java.util.List;

import javax.annotation.Nullable;

import firis.yuzukotoalchemy.YuzuKotoAlchemy;
import firis.yuzukotoalchemy.common.tileentity.YKTileEmcGeneratorBase.EnumGenTier;
import firis.yuzukotoalchemy.common.tileentity.YKTileEmcGeneratorMk1;
import firis.yuzukotoalchemy.common.tileentity.YKTileEmcGeneratorMk2;
import firis.yuzukotoalchemy.common.tileentity.YKTileEmcGeneratorMk3;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * EMCジェネレータ
 * @author computer
 *
 */
public class YKBlockEmcGenerator extends BlockContainer {
	
	/**
	 * EMCジェネレータのTIER
	 */
	protected EnumGenTier tier;
	
	/**
	 * コンストラクタ
	 */
	public YKBlockEmcGenerator(int tier) {
		
		super(Material.GLASS);
		
		this.setHardness(0.8F);
		this.setResistance(20.0F);
		this.setCreativeTab(YuzuKotoAlchemy.YuzuKotoCreativeTab);
		
		//TIERを設定する
		this.tier = EnumGenTier.getEnumTier(tier);
		
	}
	
	@SideOnly(Side.CLIENT)
	@Override
    public void addInformation(ItemStack stack, @Nullable World player, List<String> tooltip, ITooltipFlag advanced)
    {
		tooltip.add(TextFormatting.DARK_PURPLE 
				+ String.format(I18n.format("tooltip.emc.maximum_generation")
				+ TextFormatting.BLUE + " %d " + I18n.format("tooltip.emc.rate"), 
				(long) tier.getEmcRate()));
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
		
		switch (this.tier) {
			case TIER1:
				return new YKTileEmcGeneratorMk1();
			case TIER2:
				return new YKTileEmcGeneratorMk2();
			case TIER3:
				return new YKTileEmcGeneratorMk3();
		}
		
		//対象がない場合
		return null;
	}
	
}
