package firis.yuzukotoalchemy.common;

import firis.yuzukotoalchemy.YuzuKotoAlchemy;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Config.Type;

@Config(modid = YuzuKotoAlchemy.MODID, type = Type.INSTANCE, name = YuzuKotoAlchemy.MODID)
public class YKConfig {

	/**
	 * 錬金バッグ拡張・チェストへの一括搬入機能
	 */
	@Comment({"Alchemical Bag Move Chest"})
	public static boolean ALCHEMICAL_BAG_MOVE_CHEST = true;
	
	/**
	 * 錬金バッグ拡張・自動回収機能
	 */
	@Comment({"Alchemical Bag EntityItem Auto Collect"})
	public static boolean ALCHEMICAL_BAG_AUTO_COLLECT = true;
	
	
}