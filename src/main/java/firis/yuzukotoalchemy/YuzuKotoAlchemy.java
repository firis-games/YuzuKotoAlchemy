package firis.yuzukotoalchemy;

import org.apache.logging.log4j.Logger;

import firis.yuzukotoalchemy.client.tesr.YKTesrYuzuKotoCatalyst;
import firis.yuzukotoalchemy.common.block.YKBlockCircleStructure;
import firis.yuzukotoalchemy.common.block.YKBlockCircleStructure.EnumTier;
import firis.yuzukotoalchemy.common.block.YKBlockEmcGenerator;
import firis.yuzukotoalchemy.common.block.YKBlockYuzuKotoCatalyst;
import firis.yuzukotoalchemy.common.item.YKItemAkariStar;
import firis.yuzukotoalchemy.common.proxy.CommonProxy;
import firis.yuzukotoalchemy.common.tileentity.YKTileEmcGeneratorMk1;
import firis.yuzukotoalchemy.common.tileentity.YKTileEmcGeneratorMk2;
import firis.yuzukotoalchemy.common.tileentity.YKTileEmcGeneratorMk3;
import firis.yuzukotoalchemy.common.tileentity.YKTileYuzuKotoCatalyst;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod(
		modid = YuzuKotoAlchemy.MODID, 
		name = YuzuKotoAlchemy.NAME,
		version = YuzuKotoAlchemy.VERSION,
		dependencies = YuzuKotoAlchemy.MOD_DEPENDENCIES,
		acceptedMinecraftVersions = YuzuKotoAlchemy.MOD_ACCEPTED_MINECRAFT_VERSIONS
)
@EventBusSubscriber
public class YuzuKotoAlchemy
{
    public static final String MODID = "yuzukotoalchemy";
    public static final String NAME = "YuzuKoto Alchemy";
    public static final String VERSION = "0.1";
    public static final String MOD_DEPENDENCIES = "required-after:forge@[1.12.2-14.23.5.2768,);required-after:projecte@[1.12,);after:jei@[1.12.2-4.13.1.220,)";
    public static final String MOD_ACCEPTED_MINECRAFT_VERSIONS = "[1.12.2]";

    private static Logger logger;
    
    @Instance(YuzuKotoAlchemy.MODID)
    public static YuzuKotoAlchemy INSTANCE;
    
    @SidedProxy(serverSide = "firis.yuzukotoalchemy.common.proxy.CommonProxy", 
    		clientSide = "firis.yuzukotoalchemy.client.proxy.ClientProxy")
    public static CommonProxy proxy;
    
    /**
     * クリエイティブタブ
     */
    public static final CreativeTabs YuzuKotoCreativeTab = new CreativeTabs("tabYuzuKotoAlchemy") {
    	@SideOnly(Side.CLIENT)
    	@Override
        public ItemStack getTabIconItem()
        {
            return new ItemStack(Item.getItemFromBlock(YuzuKotoBlocks.YUZUKOTO_CATALYST));
        }
    };
    
    /**
     * アイテムインスタンス保持用
     */
    @ObjectHolder(YuzuKotoAlchemy.MODID)
    public static class YuzuKotoItems{
    	public final static Item AKARI_STAR = null;
    }
    
    /**
     * ブロックインスタンス保持用
     */
    @ObjectHolder(YuzuKotoAlchemy.MODID)
    public static class YuzuKotoBlocks{
    	public final static Block YUZUKOTO_CATALYST = null;
    	public final static Block CIRCLE_STRUCTURE = null;
    	public final static Block EMC_GENERATOR_MK1 = null;
    	public final static Block EMC_GENERATOR_MK2 = null;
    	public final static Block EMC_GENERATOR_MK3 = null;
    }

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        logger = event.getModLog();
        
        logger.info("YuzuKotoAlchemy Starting...");
        
        GameRegistry.registerTileEntity(YKTileYuzuKotoCatalyst.class, 
				new ResourceLocation(YuzuKotoAlchemy.MODID, "yk_te_yuzukoto_catalyst"));
        
        GameRegistry.registerTileEntity(YKTileEmcGeneratorMk1.class, 
				new ResourceLocation(YuzuKotoAlchemy.MODID, "ykte_emc_generator_mk1"));        

        GameRegistry.registerTileEntity(YKTileEmcGeneratorMk2.class, 
				new ResourceLocation(YuzuKotoAlchemy.MODID, "ykte_emc_generator_mk2"));        
        
        GameRegistry.registerTileEntity(YKTileEmcGeneratorMk3.class, 
				new ResourceLocation(YuzuKotoAlchemy.MODID, "ykte_emc_generator_mk3"));        
    }
    
    @EventHandler
    public void init(FMLInitializationEvent event)
    {
    }
    
    @EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
    }
    
    /**
     * ブロックを登録するイベント
     */
    @SubscribeEvent
    protected static void registerBlocks(RegistryEvent.Register<Block> event)
    {
    	//ゆづことカタリスト
    	event.getRegistry().register(
                new YKBlockYuzuKotoCatalyst()
                .setRegistryName(MODID, "yuzukoto_catalyst")
                .setUnlocalizedName("yuzukoto_catalyst")
        );
    	
    	//練成陣構造体
    	event.getRegistry().register(
                new YKBlockCircleStructure()
                .setRegistryName(MODID, "circle_structure")
                .setUnlocalizedName("circle_structure")
        );
    	
    	//EMCジェネレータMk1
    	event.getRegistry().register(
                new YKBlockEmcGenerator(1)
                .setRegistryName(MODID, "emc_generator_mk1")
                .setUnlocalizedName("emc_generator_mk1")
        );

    	//EMCジェネレータMk2
    	event.getRegistry().register(
                new YKBlockEmcGenerator(2)
                .setRegistryName(MODID, "emc_generator_mk2")
                .setUnlocalizedName("emc_generator_mk2")
        );
    	
    	//EMCジェネレータMk3
    	event.getRegistry().register(
                new YKBlockEmcGenerator(3)
                .setRegistryName(MODID, "emc_generator_mk3")
                .setUnlocalizedName("emc_generator_mk3")
        );

    	
    }
    
    /**
     * アイテムを登録するイベント
     */
    @SubscribeEvent
    protected static void registerItems(RegistryEvent.Register<Item> event)
    {
    	// ゆづことカタリスト
    	event.getRegistry().register(new ItemBlock(YuzuKotoBlocks.YUZUKOTO_CATALYST)
    			.setRegistryName(MODID, "yuzukoto_catalyst")
    	);

    	// 練成陣構造体(メタデータ対応)
    	event.getRegistry().register(new ItemBlock(YuzuKotoBlocks.CIRCLE_STRUCTURE) 
    			{
		    		@Override
		    		public int getMetadata(int damage)
		    	    {
		    	        return damage;
		    	    }
		    		@Override
		    		public String getUnlocalizedName(ItemStack stack) {
		    			EnumTier tier = YKBlockCircleStructure.getTierFromMetadata(stack.getMetadata());
		    			return this.getUnlocalizedName() + "_" + tier.getName();
		    		}
		    	}
    			.setRegistryName(MODID, "circle_structure")
    			.setHasSubtypes(true)
    	);
    	
    	//　あかりスター
    	event.getRegistry().register(new YKItemAkariStar()
    			.setRegistryName(MODID, "akari_star")
    			.setUnlocalizedName("akari_star")
    	);
    	
    	// EMCジェネレータMk1
    	event.getRegistry().register(new ItemBlock(YuzuKotoBlocks.EMC_GENERATOR_MK1)
    			.setRegistryName(MODID, "emc_generator_mk1")
    	);
    	
    	// EMCジェネレータMk2
    	event.getRegistry().register(new ItemBlock(YuzuKotoBlocks.EMC_GENERATOR_MK2)
    			.setRegistryName(MODID, "emc_generator_mk2")
    	);
    	
    	// EMCジェネレータMk3
    	event.getRegistry().register(new ItemBlock(YuzuKotoBlocks.EMC_GENERATOR_MK3)
    			.setRegistryName(MODID, "emc_generator_mk3")
    	);
    }
    
    /**
     * モデル登録イベント
     */
    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    protected static void registerModels(ModelRegistryEvent event)
    {
    	// ゆづことカタリスト
    	ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(YuzuKotoBlocks.YUZUKOTO_CATALYST), 0,
    			new ModelResourceLocation(YuzuKotoBlocks.YUZUKOTO_CATALYST.getRegistryName(), "inventory"));
    	
    	// ゆづことカタリスト魔方陣描画
    	ClientRegistry.bindTileEntitySpecialRenderer(YKTileYuzuKotoCatalyst.class, 
    			new YKTesrYuzuKotoCatalyst());
    	
    	// 練成陣構造体(Metadata)
    	ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(YuzuKotoBlocks.CIRCLE_STRUCTURE), 0,
    			new ModelResourceLocation(YuzuKotoBlocks.CIRCLE_STRUCTURE.getRegistryName() + "_tier1", "inventory"));

    	ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(YuzuKotoBlocks.CIRCLE_STRUCTURE), 1,
    			new ModelResourceLocation(YuzuKotoBlocks.CIRCLE_STRUCTURE.getRegistryName() + "_tier2", "inventory"));
    	
    	ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(YuzuKotoBlocks.CIRCLE_STRUCTURE), 2,
    			new ModelResourceLocation(YuzuKotoBlocks.CIRCLE_STRUCTURE.getRegistryName() + "_tier3", "inventory"));
    	
    	// あかりスター
    	ModelLoader.setCustomModelResourceLocation(YuzuKotoItems.AKARI_STAR, 0,
    			new ModelResourceLocation(YuzuKotoItems.AKARI_STAR.getRegistryName(), "inventory"));
    	
    	//EMCジェネレータMk1
    	ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(YuzuKotoBlocks.EMC_GENERATOR_MK1), 0,
    			new ModelResourceLocation(YuzuKotoBlocks.EMC_GENERATOR_MK1.getRegistryName(), "inventory"));
    	
    	//EMCジェネレータMk2
    	ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(YuzuKotoBlocks.EMC_GENERATOR_MK2), 0,
    			new ModelResourceLocation(YuzuKotoBlocks.EMC_GENERATOR_MK2.getRegistryName(), "inventory"));
    	
    	//EMCジェネレータMk3
    	ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(YuzuKotoBlocks.EMC_GENERATOR_MK3), 0,
    			new ModelResourceLocation(YuzuKotoBlocks.EMC_GENERATOR_MK3.getRegistryName(), "inventory"));
    	
    }
}
