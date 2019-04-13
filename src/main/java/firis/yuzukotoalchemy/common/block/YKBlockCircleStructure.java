package firis.yuzukotoalchemy.common.block;

import java.util.List;

import javax.annotation.Nullable;

import firis.yuzukotoalchemy.YuzuKotoAlchemy;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * 魔方陣用構造体ブロック
 * @author computer
 *
 */
public class YKBlockCircleStructure extends Block {

	/**
	 * 練成陣のランク
	 */
	public static final PropertyEnum<EnumTier> TIER = PropertyEnum.create("tier", YKBlockCircleStructure.EnumTier.class);
	public static enum EnumTier implements IStringSerializable {
		TIER1(0, "tier1", 100F),
		TIER2(1, "tier2", 1000F),
		TIER3(2, "tier3", 10000F);
		private EnumTier(int id, String name, double emcRate) {
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
		public static EnumTier getEnumTier(int id) {
			EnumTier ret = TIER1;
			for (EnumTier tier : EnumTier.values()) {
				if (id == tier.getId()) {
					ret = tier;
					break;
				}
			}
			return ret;
		}
	}
	
	/**
	 * 練成陣の起動状態
	 */
	public static final PropertyEnum<EnumActive> ACTIVE = PropertyEnum.create("active", YKBlockCircleStructure.EnumActive.class);
	public static enum EnumActive implements IStringSerializable {
		OFF(0, "off"),
		ON(1, "on");
		private EnumActive(int id, String name) {
			this.id = id;
			this.name = name;
		}
		private int id;
		private String name;
		public int getId() {
			return this.id;
		}
		@Override
		public String getName() {
			return this.name;
		}
		public static EnumActive getEnumAvtive(int id) {
			EnumActive ret = OFF;
			for (EnumActive active : EnumActive.values()) {
				if (id == active.getId()) {
					ret = active;
					break;
				}
			}
			return ret;
		}
	}
	
	/**
	 * コンストラクタ
	 */
	public YKBlockCircleStructure() {
		
		super(Material.GLASS);
		
		this.setHardness(0.8F);
		this.setResistance(20.0F);
		this.setCreativeTab(YuzuKotoAlchemy.YuzuKotoCreativeTab);
		
		this.setDefaultState(this.blockState.getBaseState()
				.withProperty(TIER, EnumTier.TIER1)
				.withProperty(ACTIVE, EnumActive.OFF));
		
	}
	
	/**
	 * メタデータからTierを取得する
	 * @param meta
	 * @return
	 */
	public static EnumTier getTierFromMetadata(int meta) {
		return EnumTier.getEnumTier(meta > 7 ? meta - 8 : meta);
	}
	
	/**
	 * IBlockStateからTierを取得する
	 * @param state
	 * @return
	 */
	public static EnumTier getTierFromIBlockState(IBlockState state) {
		return (EnumTier) state.getProperties().get(TIER);
	}
	
	/**
	 * ドロップ制御
	 */
	@Override
    public int damageDropped(IBlockState state)
    {
		EnumTier tier = (EnumTier) state.getProperties().get(TIER);
		
		return tier.getId();
    }
	
	@Deprecated
	@Override
    public IBlockState getStateFromMeta(int meta)
    {
		int meta1 = meta > 7 ? meta - 8 : meta;
		int meta2 = meta > 7 ? 1 : 0;
		EnumTier enumTier = EnumTier.getEnumTier(meta1);
		EnumActive enumActive = EnumActive.getEnumAvtive(meta2);
		
        return this.getDefaultState()
        		.withProperty(TIER, enumTier)
        		.withProperty(ACTIVE, enumActive);
    }


	@Override
    public int getMetaFromState(IBlockState state)
    {
		EnumTier tier = (EnumTier) state.getProperties().get(TIER);
		EnumActive active = (EnumActive) state.getProperties().get(ACTIVE);
		
		return tier.getId() + (active.getId() * 8);
    }
	
	@Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, new IProperty[] {TIER, ACTIVE});
    }
	
	@Override
    public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items)
    {
		//Tier1からTier3まで登録
		for (int i = 0; i < 3; i++) {
			items.add(new ItemStack(this, 1, i));
		}
    }
	
	@SideOnly(Side.CLIENT)
	@Override
    public void addInformation(ItemStack stack, @Nullable World player, List<String> tooltip, ITooltipFlag advanced)
    {
		EnumTier tier = EnumTier.getEnumTier(stack.getMetadata());
		tooltip.add(TextFormatting.DARK_PURPLE 
				+ String.format(I18n.format("tooltip.emc.generation")
				+ TextFormatting.BLUE + " %d " + I18n.format("tooltip.emc.rate"), 
				(long) tier.getEmcRate()));
    }
	
}
