package firis.yuzukotoalchemy.common.item;

import firis.yuzukotoalchemy.YuzuKotoAlchemy;
import moze_intel.projecte.api.item.IItemEmc;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;

public class YKItemAkariStar extends Item implements IItemEmc {

	public YKItemAkariStar() {
		
		// 充電の容量(omegaの5倍)
		this.starMaxEmc = 51200000 * 5;
		this.setCreativeTab(YuzuKotoAlchemy.YuzuKotoCreativeTab);
		this.setMaxStackSize(1);
	}
	
	/**
	 * 最大EMC
	 */
	protected double starMaxEmc = 0;
	protected double getStarMaxEmc() {
		return this.starMaxEmc;
	}
	
	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items)
    {
        if (this.isInCreativeTab(tab))
        {
        	ItemStack stack = new ItemStack(this);
        	stack.setTagCompound(new NBTTagCompound());
            items.add(stack);
            
            stack = new ItemStack(this);
            this.setEmc(stack, this.getStarMaxEmc());
            items.add(stack);
            
        }
    }
	
	@Override
	public boolean showDurabilityBar(ItemStack stack)
	{
		return stack.hasTagCompound();
	}
	
	@Override
	public double getDurabilityForDisplay(ItemStack stack)
	{
		double starEmc = this.getEmc(stack);
		
		if (starEmc == 0)
		{
			return 1.0D;
		}
		
		return 1.0D - starEmc / this.getStarMaxEmc();
	}
	
	/** @IItemEmc */
	/**********************************************************************/
	@Override
	public double addEmc(ItemStack stack, double toAdd) {
		double add = Math.min(getMaximumEmc(stack) - getStoredEmc(stack), toAdd);
		this.addEmcToStack(stack, add);
		return add;
	}

	@Override
	public double extractEmc(ItemStack stack, double toRemove) {
		double sub = Math.min(getStoredEmc(stack), toRemove);
		this.removeEmc(stack, sub);
		return sub;
	}

	@Override
	public double getStoredEmc(ItemStack stack) {
		return this.getEmc(stack);
	}

	@Override
	public double getMaximumEmc(ItemStack stack) {
		return this.getStarMaxEmc();
	}
	
	
	/** staticメソッド */
	/**********************************************************************/
	
	public double getEmc(ItemStack stack)
	{
		if (!stack.hasTagCompound()) {
        	stack.setTagCompound(new NBTTagCompound());
		}
		return stack.getTagCompound().getDouble("StoredEMC");
	}
	
	public void removeEmc(ItemStack stack, double amount)
	{
		double result = this.getEmc(stack) - amount;
		
		if (result < 0)
		{
			result = 0;
		}
		
		this.setEmc(stack, result);
	}
	
	public void setEmc(ItemStack stack, double amount)
	{
		if (!stack.hasTagCompound()) {
        	stack.setTagCompound(new NBTTagCompound());
		}
		stack.getTagCompound().setDouble("StoredEMC", amount);
	}
	
	public void addEmcToStack(ItemStack stack, double amount)
	{
		this.setEmc(stack, getEmc(stack) + amount);
	}
		
}
