package firis.yuzukotoalchemy.client.tesr;

import java.util.Date;

import org.lwjgl.opengl.GL11;

import firis.yuzukotoalchemy.common.tileentity.YKTileYuzuKotoCatalyst;
import firis.yuzukotoalchemy.common.tileentity.YKTileYuzuKotoCatalyst.CircleTier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.ResourceLocation;

/**
 * ゆづことカタリスト描画用
 * @author computer
 *
 */
public class YKTesrYuzuKotoCatalyst extends TileEntitySpecialRenderer<YKTileYuzuKotoCatalyst> {
	
	protected final ResourceLocation textures = new ResourceLocation("yuzukotoalchemy", "textures/image/magic_circle.png");
	
	/**
	 * Renderer
	 */
	@Override
	public void render(YKTileYuzuKotoCatalyst te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        GlStateManager.pushMatrix();
        GlStateManager.translate((float)x, (float)y, (float)z);
        doRender(te, x, y, z, partialTicks, destroyStage, alpha);
        GlStateManager.popMatrix();
	}
		
	/**
	 * 描画する
	 */
	protected void doRender(YKTileYuzuKotoCatalyst tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
		
		//練成陣が稼動してない場合は何もしない
		CircleTier circleTier = tile.getCircleTier();
		if (circleTier == CircleTier.NONE) return;
		
        //テクスチャバインド
        Minecraft.getMinecraft().getTextureManager().bindTexture(textures);
        
        //魔方陣の色を設定
        if (circleTier == CircleTier.TIER3) {
        	//紫
            GlStateManager.color(176F / 255F, 35F / 255F, 254F / 255F, 1.0F);
        } else if(circleTier == CircleTier.TIER2) {
        	//青
            GlStateManager.color(0F / 255F, 0F / 255F, 255F / 255F, 1.0F);
        } else {
        	//赤
        	GlStateManager.color(255F / 255F, 0F / 255F, 0F / 255F, 1.0F);
        } 
        
        //GL11初期化
		GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glCullFace(GL11.GL_BACK);
        GL11.glDisable(GL11.GL_CULL_FACE);
        //ライト設定（これをしないと描画したものが暗くなる）
        RenderHelper.disableStandardItemLighting();
        //描画のオブジェクトを透過設定
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        
        //スプライトからUVを取得
        float minU = 0;
        float maxU = 1;
        float minV = 0;
        float maxV = 1;
        
        //位置調整
        GlStateManager.translate(0.5, 1.01, 0.5);

        //サイズ設定
        int scale = circleTier.getRange() * 2 + 1;
        GlStateManager.scale(scale, 1, scale);
        
        //回転の計算
        Date date = new Date();
        long time = date.getTime();
        //1tick計算
        time = time / 50;
        float angle = (time % 360);
        GlStateManager.rotate(angle, 0F, 1F, 0F);
        
        
        //基準の高さ
        double vertY = 0.0D;
        
        //描画処理
        //板モデルを描画
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glTexCoord2d(minU, minV);
        GL11.glVertex3d(-0.5, vertY, -0.5);
        GL11.glTexCoord2d(maxU, minV);
        GL11.glVertex3d(0.5, vertY, -0.5);
        GL11.glTexCoord2d(maxU, maxV);
        GL11.glVertex3d(0.5, vertY, 0.5);
        GL11.glTexCoord2d(minU, maxV);
        GL11.glVertex3d(-0.5, vertY, 0.5);
        GL11.glEnd();
        
        //GL11終了処理
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glFlush();
        
	}	
}
