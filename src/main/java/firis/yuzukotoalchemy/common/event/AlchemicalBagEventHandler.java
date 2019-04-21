package firis.yuzukotoalchemy.common.event;

import java.util.ArrayList;
import java.util.List;

import firis.yuzukotoalchemy.common.YKConfig;
import moze_intel.projecte.api.ProjectEAPI;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
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
	
		//falseの場合は何もしない
		if (!YKConfig.ALCHEMICAL_BAG_MOVE_CHEST) return;
		
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
	
	/**
	 * 錬金バッグのアイテム回収
	 * @param event
	 */
	@SubscribeEvent
	public static void onEntityItemPickupEvent(EntityItemPickupEvent event) {
		
		//falseの場合は何もしない
		if (!YKConfig.ALCHEMICAL_BAG_AUTO_COLLECT) return;
		
		EntityPlayer player = event.getEntityPlayer();
		if (player == null) return;
		
		//Inventoryに錬金バッグがあるか確認
		int idx = -1;
		for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
			if (isAlchemicalBag(player.inventory.getStackInSlot(i))) {
				idx = i;
				break;
			}			
		}
		
		if (idx == -1) return;
		
		//錬金バッグが存在する場合
		IItemHandler alchemicalBag = player.getCapability(
				ProjectEAPI.ALCH_BAG_CAPABILITY, null)
				.getBag(EnumDyeColor.byMetadata(player.inventory.getStackInSlot(idx).getItemDamage()));
		
		//錬金バッグの1列目のItemStackを取得(13こ)
		List<ItemStack> checkStack = new ArrayList<ItemStack>();
		for (int i = 0; i < 13; i++) {
			ItemStack stack = alchemicalBag.getStackInSlot(i);
			if (!stack.isEmpty()) {
				boolean ret = false;
				for(ItemStack check : checkStack) {
					if (!ItemStack.areItemsEqual(stack, check)) {
						ret = true;
						break;
					}
				}
				if (ret || checkStack.size() == 0) {
					checkStack.add(stack.copy());
				}
			}			
		}
		
		ItemStack stack = event.getItem().getItem().copy();
		//チェック処理
		boolean ret = false;
		for (ItemStack check : checkStack) {
			if (ItemStack.areItemsEqual(check, stack)) {
				ret = true;
				break;
			}
		}
		
		if (!ret) return;
		
		//錬金バッグへ挿入する
		for (int i = 0; i < alchemicalBag.getSlots(); i++) {
			stack = alchemicalBag.insertItem(i, stack, false);
			if (stack.isEmpty()) {
				break;
			}
		}
		
		//減らす数
		int stackCount = event.getItem().getItem().getCount() 
				- event.getItem().getItem().getCount() 
				- stack.getCount();
		
		event.getItem().getItem().setCount(stackCount);
		
		event.setResult(Result.ALLOW);
	}
	
}
