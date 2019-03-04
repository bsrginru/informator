package avttrue.informator.Tools.Surface;

import java.util.ArrayList;

import avttrue.informator.Informator;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRailBase;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.chunk.Chunk;

/*
 * Этот класс проверяет свойства поверхности вокруг игрока
 * и отрисовывает на ней метки
 * Сделано на базе
 * http://minecraft.curseforge.com/projects/light-level-overlay-reloaded 
 */
public class SurfaceChecker extends Thread
{
	public volatile ArrayList<SurfaceOverlay>[][] surfaceOverlays;
	private int CheckInterval = 200; // интервал обновления
	
	public void run() 
	{
		int radius = 0;
		while (true) 
		{
			int chunkRadius = updateChunkRadius();
			radius = radius % chunkRadius + 1;
			if (Informator.SurfaceCheckerIsActive) 
				updateLightLevel(radius, chunkRadius);
			try 
			{
				sleep(CheckInterval);
			} 
			catch (Exception e) 
			{
				System.out.println(e.getMessage());
				e.printStackTrace();
			}
		}
	}
	
	private int updateChunkRadius() 
	{
		int size = Informator.LightLevelIndicatorRadius;
		if (surfaceOverlays == null || surfaceOverlays.length != size * 2 + 1) 
		{
			surfaceOverlays = new ArrayList[size * 2 + 1][size * 2 + 1];
			for (int i = 0; i < surfaceOverlays.length; i++)
			for (int j = 0; j < surfaceOverlays[i].length; j++)
				surfaceOverlays[i][j] = new ArrayList();
		}
		return size;
	}
	
private void updateLightLevel(int radius, int chunkRadius) 
{
		Minecraft mc = Minecraft.getMinecraft();
		if (mc.thePlayer == null) return;
		
		WorldClient world = mc.theWorld;
		int playerPosY = (int)Math.floor(mc.thePlayer.posY);
		int playerChunkX = mc.thePlayer.chunkCoordX;
		int playerChunkZ = mc.thePlayer.chunkCoordZ; 
		int skyLightSub = world.calculateSkylightSubtracted(1.0f);
		boolean mychunk = false;
		boolean useSkyLight = false; // можно параметризировать
		
		for (int chunkX = playerChunkX - radius; chunkX <= playerChunkX + radius; chunkX++)
		for (int chunkZ = playerChunkZ - radius; chunkZ <= playerChunkZ + radius; chunkZ++) 
		{
			Chunk chunk = mc.theWorld.getChunkFromChunkCoords(chunkX, chunkZ);
			if (!chunk.isLoaded()) continue;
			ArrayList<SurfaceOverlay> buffer = new ArrayList<SurfaceOverlay>();
			for (int offsetX = 0; offsetX < 16; offsetX++)
			for (int offsetZ = 0; offsetZ < 16; offsetZ++) 
			{
				int posX = (chunkX << 4) + offsetX;
				int posZ = (chunkZ << 4) + offsetZ;
				int maxY = playerPosY + 4, minY = Math.max(playerPosY - 40, 0);
				IBlockState preBlockState = null, curBlockState = chunk.getBlockState(offsetX, maxY, offsetZ);
				Block preBlock = null, curBlock = curBlockState.getBlock();
				BlockPos prePos = null, curPos = new BlockPos(posX, maxY, posZ);
				for (int posY = maxY - 1; posY >= minY; posY--) 
				{
					preBlockState = curBlockState;
					curBlockState = chunk.getBlockState(offsetX, posY, offsetZ);
					preBlock = curBlock;
					curBlock = curBlockState.getBlock();
					prePos = curPos;
					curPos = new BlockPos(posX, posY, posZ);
					if (curBlock == Blocks.AIR ||
						curBlock == Blocks.BEDROCK ||
						curBlock == Blocks.BARRIER ||
						preBlockState.isBlockNormalCube() ||
						preBlockState.getMaterial().isLiquid() ||
						preBlockState.canProvidePower() ||
						curBlockState.isSideSolid(world, curPos, EnumFacing.UP) == false ||
						BlockRailBase.isRailBlock(preBlockState)) 
					{
						continue;
					}
					double offsetY = 0;
					if (preBlock == Blocks.SNOW_LAYER || preBlock == Blocks.CARPET) 
					{
						offsetY = preBlockState.getBoundingBox(world, prePos).maxY;
						if (offsetY >= 0.15) continue; // 
					}
					int lightLevel = chunk.getLightFor(EnumSkyBlock.BLOCK, prePos);
					if (useSkyLight) 
					{
						int llevel = chunk.getLightFor(EnumSkyBlock.SKY, prePos) - skyLightSub;
						lightLevel = Math.max(lightLevel, llevel);
					}
					
					if(Informator.LightLevelIndicatorShowChunkBorder) // показывать границы чанка
					{
						if(chunk.xPosition != mc.thePlayer.chunkCoordX || 
								chunk.zPosition != mc.thePlayer.chunkCoordZ)
							mychunk = false;
						else
							mychunk = true;
					}
					else
						mychunk = false;
						
					if(Informator.LightLevelIndicatorShowAllValues) // показываем все значения
						buffer.add(new SurfaceOverlay(posX, posY + offsetY + 1, posZ, lightLevel, mychunk));
					else if(lightLevel < 8) // показываем только опасные
						buffer.add(new SurfaceOverlay(posX, posY + offsetY + 1, posZ, lightLevel, mychunk));
				}
			}
			int len = chunkRadius * 2 + 1;
			int arrayX = (chunkX % len + len) % len;
			int arrayZ = (chunkZ % len + len) % len;
			surfaceOverlays[arrayX][arrayZ] = buffer;
		}
	}
}
