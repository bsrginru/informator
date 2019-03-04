package avttrue.informator.Tools;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;

public class Drawing 
{

	// TODO отрисовка модели сущности на экране
	public static void drawEntityOnScreen(int x, int y, int scale, 
											float mouseX, float mouseY, 
											EntityLivingBase entity_elb)
    {
        GlStateManager.enableColorMaterial();
        GlStateManager.pushMatrix();
        GlStateManager.translate((float)x, (float)y, 50.0F);
        GlStateManager.scale((float)(-scale), (float)scale, (float)scale);
        GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
        float f2 = entity_elb.renderYawOffset;
        float f3 = entity_elb.rotationYaw;
        float f4 = entity_elb.rotationPitch;
        float f5 = entity_elb.prevRotationYawHead;
        float f6 = entity_elb.rotationYawHead;
        GlStateManager.rotate(160.0F, 0.0F, 1.0F, 0.0F); // 135 первый параметр, для фронтального расположения
        RenderHelper.enableStandardItemLighting();
        GlStateManager.rotate(-135.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(-((float)Math.atan((double)(mouseY / 40.0F))) * 20.0F, 1.0F, 0.0F, 0.0F);
        entity_elb.renderYawOffset = (float)Math.atan((double)(mouseX / 40.0F)) * 20.0F;
        entity_elb.rotationYaw = (float)Math.atan((double)(mouseX / 40.0F)) * 40.0F;
        entity_elb.rotationPitch = -((float)Math.atan((double)(mouseY / 40.0F))) * 20.0F;
        entity_elb.rotationYawHead = entity_elb.rotationYaw;
        entity_elb.prevRotationYawHead = entity_elb.rotationYaw;
        GlStateManager.translate(0.0F, 0.0F, 0.0F);
        RenderManager rendermanager = Minecraft.getMinecraft().getRenderManager();
        rendermanager.setPlayerViewY(180.0F);
        rendermanager.setRenderShadow(false);
        rendermanager.doRenderEntity(entity_elb, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, false);
        rendermanager.setRenderShadow(true);
        entity_elb.renderYawOffset = f2;
        entity_elb.rotationYaw = f3;
        entity_elb.rotationPitch = f4;
        entity_elb.prevRotationYawHead = f5;
        entity_elb.rotationYawHead = f6;
        GlStateManager.popMatrix();
        GlStateManager.disableRescaleNormal();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GlStateManager.disableTexture2D();
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
    }
	
	// TODO отрисовка иконки куба на экране
	public static void DrawItemStack(RenderItem renderitem, ItemStack istack, int xPos, int yPos)
	{
		GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
		RenderHelper.enableGUIStandardItemLighting();
		renderitem.zLevel = 200.0F;
		renderitem.renderItemAndEffectIntoGUI(istack, xPos, yPos);		
	}
	
}
