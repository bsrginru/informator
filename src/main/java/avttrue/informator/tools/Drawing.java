package avttrue.informator.tools;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;

import avttrue.informator.Informator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;

public class Drawing
{
    // отрисовка модели сущности на экране
    public static void drawEntityOnScreen(int x, int y, float scale, float mouseX, float mouseY, LivingEntity entity_elb)
    {
        RenderSystem.enableColorMaterial();
        GL11.glPushMatrix();
        GL11.glTranslatef((float)x, (float)y, 50.0F);
        GL11.glScalef(-scale, scale, scale);
        GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
        float f2 = entity_elb.renderYawOffset;
        float f3 = entity_elb.rotationYaw;
        float f4 = entity_elb.rotationPitch;
        float f5 = entity_elb.prevRotationYawHead;
        float f6 = entity_elb.rotationYawHead;
        GL11.glRotatef(160.0F, 0.0F, 1.0F, 0.0F); // 135 первый параметр, для фронтального расположения
        RenderHelper.enableStandardItemLighting();
        GL11.glRotatef(-135.0F, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(-((float)Math.atan((double)(mouseY / 40.0F))) * 20.0F, 1.0F, 0.0F, 0.0F);
        entity_elb.renderYawOffset = (float)Math.atan((double)(mouseX / 40.0F)) * 20.0F;
        entity_elb.rotationYaw = (float)Math.atan((double)(mouseX / 40.0F)) * 40.0F;
        entity_elb.rotationPitch = -((float)Math.atan((double)(mouseY / 40.0F))) * 20.0F;
        entity_elb.rotationYawHead = entity_elb.rotationYaw;
        entity_elb.prevRotationYawHead = entity_elb.rotationYaw;
        GL11.glTranslatef(0.0F, 0.0F, 0.0F);
        EntityRendererManager rendermanager = Minecraft.getInstance().getRenderManager();
        rendermanager.setPlayerViewY(180.0F);
        rendermanager.setRenderShadow(false);
        rendermanager.renderEntity(entity_elb, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, false);
        rendermanager.setRenderShadow(true);
        entity_elb.renderYawOffset = f2;
        entity_elb.rotationYaw = f3;
        entity_elb.rotationPitch = f4;
        entity_elb.prevRotationYawHead = f5;
        entity_elb.rotationYawHead = f6;
        GL11.glPopMatrix();
        RenderSystem.disableRescaleNormal();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.activeTexture(GLX.GL_TEXTURE1);
        RenderSystem.disableTexture();
        GlStateManager.activeTexture(GLX.GL_TEXTURE0);

    }

    // отрисовка иконки куба на экране
    public static void DrawItemStack(ItemRenderer renderitem, ItemStack istack, int xPos, int yPos)
    {
        GlStateManager.blendFuncSeparate(770, 771, 1, 0);
        RenderHelper.enableGUIStandardItemLighting();
        renderitem.zLevel = 200.0F;
        renderitem.renderItemAndEffectIntoGUI(istack, xPos, yPos);
    }

    public static int getGlowAndFlickColor()
    {
        final int GLOW_DURATION = 350; // 7сек
        final int FLICK_DURATION = 150; // 3сек
        final int LIGHTING_DURATION = FLICK_DURATION + (GLOW_DURATION-FLICK_DURATION)/2; // 5сек
        final long diff = Informator.realTimeTick % GLOW_DURATION;
        // добавляем эффект мерцания
        int glow;
        if (diff <= FLICK_DURATION)
        {
            // циклически 3сек: за 0.5сек цвет достигает значения с 0xff0000 до 0xff8080, и ещё 0.5сек возвращается к 0xff0000
            glow = (int)((float)diff * 5.1) % 0x100;
            if (glow >= 0x80) glow = 0x100 - glow;
        }
        else if (diff <= LIGHTING_DURATION)
        {
            // ещё 2сек цвет поднимается с 0xff0000 до 0xffffff
            glow = (int)((float)(diff-FLICK_DURATION) * 2.55) % 0x100;
        }
        else
        {
            // оставшиеся 2сек цвет спадает с 0xffffff до 0xff0000
            glow = 0xff - (int)((float)(diff-LIGHTING_DURATION) * 2.55) % 0x100;
        }
        int color = 0xffff0000 | glow << 8 | glow;
        return color;
    }
}
