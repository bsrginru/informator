package avttrue.informator.Tools.Surface;

import java.util.ArrayList;

import org.lwjgl.opengl.GL11;

import avttrue.informator.Informator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;

public class OverlayRenderer 
{
	/*
	 * Сделано на базе
	 * http://minecraft.curseforge.com/projects/light-level-overlay-reloaded 
	 */
	private ResourceLocation texturec = null;
	private ResourceLocation textured = null;
	
	private double[] texureMinX;
	private double[] texureMaxX;
	
	public OverlayRenderer() 
	{
		texturec = new ResourceLocation("avttrue_informator:textures/lloverlaycircle.png");
		textured = new ResourceLocation("avttrue_informator:textures/lloverlaydigital.png");
		
		texureMinX = new double[32];
		texureMaxX = new double[32];
		for (int i = 0; i < 32; i++) 
		{
			texureMinX[i] = i / 32.0;
			texureMaxX[i] = (i + 1) / 32.0;
		}
	}
	
	public void RenderLight(double x, double y, double z, ArrayList<SurfaceOverlay>[][] surfaceOverlays) 
	{
		int mychunk = 0;
		TextureManager tm = Minecraft.getMinecraft().renderEngine;
		VertexBuffer vb = Tessellator.getInstance().getBuffer();
		
		if(Informator.LightLevelIndicatorShowDigital)
			tm.bindTexture(textured);
		else
			tm.bindTexture(texturec);
		
		GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
		GL11.glPushMatrix();
		GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_DST_COLOR, GL11.GL_ZERO);
		vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
		vb.setTranslation(-x, -y, -z);
		for (int i = 0; i < surfaceOverlays.length; i++)
		for (int j = 0; j < surfaceOverlays[i].length; j++) 
		{
			for (SurfaceOverlay u: surfaceOverlays[i][j]) 
			{
				if(u.myChunk)
					mychunk = 16;
				else
					mychunk = 0;
					
				vb.pos(u.x,     u.y, u.z    ).tex(texureMinX[u.lightLevel + mychunk], 0.0).color(255, 255, 255, 255).endVertex();
				vb.pos(u.x,     u.y, u.z + 1).tex(texureMinX[u.lightLevel + mychunk], 1.0).color(255, 255, 255, 255).endVertex();
				vb.pos(u.x + 1, u.y, u.z + 1).tex(texureMaxX[u.lightLevel + mychunk], 1.0).color(255, 255, 255, 255).endVertex();
				vb.pos(u.x + 1, u.y, u.z    ).tex(texureMaxX[u.lightLevel + mychunk], 0.0).color(255, 255, 255, 255).endVertex();
			}
		}
		Tessellator.getInstance().draw();
		vb.setTranslation(0, 0, 0);
		GL11.glPopMatrix();
		GL11.glPopAttrib();
	}
}
