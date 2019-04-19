package firis.yuzukotoalchemy.common.event;

import moze_intel.projecte.api.ProjectEAPI;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

@EventBusSubscriber
public class AlchemicalBagEventHandler {

	private static ResourceLocation alchemical_bag = null;
	/**
	 * 錬金バッグか判断する
	 * @param stack
	 * @return
	 */
	private static boolean isAlchemicalBag(ItemStack stack) {
		if (AlchemicalBagEventHandler.alchemical_bag == null) {
			alchemical_bag = new ResourceLocation("projecte", "item.pe_alchemical_bag");
		}
		//手持ちアイテムが錬金バッグの場合
		if (!stack.isEmpty() && AlchemicalBagEventHandler.alchemical_bag.equals(stack.getItem().getRegistryName())) {
			return true;
		}
		return false;
	}
	
	/**
	 * 錬金バッグからチェストへのアイテム移動
	 * @param event
	 */
	@SubscribeEvent
	public static void onPlayerInteractEventRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
	
		EntityPlayer player = event.getEntityPlayer();
		
		//スニーク判定
		if (!player.isSneaking()) {
			return;
		}
		
		ItemStack handStack = player.getHeldItemMainhand();
		//手持ちアイテムが錬金バッグでない場合
		if (!isAlchemicalBag(handStack)) {
			return;
		}
		
		//クリックしたブロックのTileEntityを取得
		World world = event.getWorld();
		BlockPos pos = event.getPos(); 
		
		//Inventoryを持つブロックかチェックする
		TileEntity tile = world.getTileEntity(pos);
		if (tile == null) {
			return;
		}
		IItemHandler inventory = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
		if (inventory == null) {
			return;
		}
		
		//錬金バッグから
		IItemHandler alchemicalBag = player.getCapability(ProjectEAPI.ALCH_BAG_CAPABILITY, null)
				.getBag(EnumDyeColor.byMetadata(handStack.getItemDamage()));
		
		//錬金バッグの中身をInventoryへ移動する
		for (int i = 0; i < alchemicalBag.getSlots(); i++) {
			
			ItemStack stack = alchemicalBag.extractItem(i, alchemicalBag.getStackInSlot(i).getCount(), false);
			
			if (!stack.isEmpty()) {
				//inventoryへ移動する
				for (int inv = 0; inv < inventory.getSlots(); inv++) {
					stack = inventory.insertItem(inv, stack, false);
					if (stack.isEmpty()) {
						break;
					}
				}
				
				//残った場合は戻す
				alchemicalBag.insertItem(i, stack, false);
			}
		}
		//後続の処理を停止する
		event.setCanceled(true);
	}
	
}
