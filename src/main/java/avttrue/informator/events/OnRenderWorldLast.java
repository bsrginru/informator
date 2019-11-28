package avttrue.informator.events;

import java.util.stream.Stream;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.platform.GlStateManager;

import avttrue.informator.Informator;
import avttrue.informator.config.ModSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.RedstoneTorchBlock;
import net.minecraft.block.RedstoneWallTorchBlock;
import net.minecraft.block.StandingSignBlock;
import net.minecraft.block.TorchBlock;
import net.minecraft.block.WallSignBlock;
import net.minecraft.block.WallTorchBlock;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.LightType;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/*
 * Этот класс проверяет свойства поверхности вокруг игрока
 * и отрисовывает на ней метки
 * Сделано на базе
 * http://minecraft.curseforge.com/projects/light-level-overlay-reloaded 
 */
public class OnRenderWorldLast
{
    private Minecraft mc = Minecraft.getInstance();
    private boolean withChunkBorder;

    @SubscribeEvent
    public void onRenderWorldLast(RenderWorldLastEvent event)
    {
        if (!ModSettings.GENERAL.Global_IlluminationOnSurface.get()) return; // если отображение выключено
        try
        {
            final ClientWorld world = mc.world;
            final ClientPlayerEntity player = mc.player;
            final ISelectionContext ctx = ISelectionContext.forEntity(player);
            final ChunkPos chunk_of_player = player.world.getChunk(player.chunkCoordX, player.chunkCoordZ).getPos();
            final Direction direction = player.getHorizontalFacing();
            
/**
 * отладка
 *
Informator.R4.clear();
RayTraceResult rtr = mc.objectMouseOver;
if ((rtr != null) && (rtr.getType() == RayTraceResult.Type.BLOCK))
{
	BlockRayTraceResult target = ((BlockRayTraceResult)rtr);
	if (target.getType() != RayTraceResult.Type.MISS)
	{
		final BlockPos pos = target.getPos(), dpos = pos.down();
		final BlockState current = world.getBlockState(pos), down = world.getBlockState(dpos);
    	Informator.R4.add(String.format("up %dx%dx%d %s %s %s %s",
    		pos.getX(), pos.getY(), pos.getZ(),
    		current.isOpaqueCube(world, pos) ? "" : "прозрачный",
    		current.isSolid() ? "сплошной" : "",
    		current.isNormalCube(world, pos) ? "куб" : "",
    		current.getMaterial().isLiquid() ? "жидкость" : ""
    	));
    	Informator.R4.add(String.format("down %dx%dx%d %s %s %s %s",
    		dpos.getX(), dpos.getY(), dpos.getZ(),
    		down.isOpaqueCube(world, dpos) ? "" : "прозрачный",
    		down.isSolid() ? "сплошной" : "",
    		down.isNormalCube(world, dpos) ? "куб" : "",
        	down.getMaterial().isLiquid() ? "жидкость" : ""
    	));
	}
}/**/

            // определяем границы в которых будет прорисовываться поверхность, запоминаем и другие настройки
            final int depth = ModSettings.ILLUMINATION.Illumination_Depth.get();
            withChunkBorder = ModSettings.ILLUMINATION.Illumination_ShowChunkBorder.get();
            // получаем список блоков, в которых будет произведён рассчёт освещённости
            final BlockPos center = new BlockPos(player.posX, player.posY, player.posZ);
            Stream<BlockPos> surface = BlockPos.getAllInBox(
        		center.add(-depth, -6, -depth), // -1 соответствует одному блоку под ногами; берём -6 чтобы смотреть с возвышений под ноги
        		center.add(depth, 2, depth));   // на высоту головы
            IlluminationData illumination = new IlluminationData();

            GlStateManager.enableBlend();
            GlStateManager.enableTexture();
            mc.getTextureManager().bindTexture(Informator.light_textures);
            surface.forEach(pos -> {
            	if (getLight(world, ctx, pos, illumination) && (illumination.light >= 0))
            	{
	            	final ChunkPos chunk_by_pos = world.getChunkAt(pos).getPos();
	            	illumination.our_chunk = chunk_by_pos == chunk_of_player;
	        		drawLightOfSurface(world, direction, pos, illumination);
            	}
            });
            Tessellator.getInstance().getBuffer().setTranslation(0, 0, 0); // необязательно, но... чтобы другие плагины не косячили
        }
        catch(Exception e)
        {
            ModSettings.GENERAL.Global_IlluminationOnSurface.set(false);
            System.out.println(e.getMessage());
            e.printStackTrace();
            Informator.TOOLS.SendFatalErrorToUser(Informator.TRANSLATOR.field_fatal_error);
        }
    }
    
    public class IlluminationData
    {
    	int light;
    	double block_height;
    	boolean our_chunk;
    }
    
    private boolean getLight(ClientWorld world, ISelectionContext ctx, BlockPos pos, IlluminationData illumination)
    {
		illumination.block_height = -1;
		final BlockState state_current = world.getBlockState(pos);
		final Material material_current = state_current.getMaterial();
		if ((material_current == Material.CARPET) ||
			(material_current == Material.SNOW))
        {
        	// пофик что под ковром и под снегом - показываем освещённость этого прозрачного блока
        }
		else if ((material_current == Material.PORTAL) ||
				 (material_current == Material.BAMBOO) ||
				 material_current.isLiquid())
		{
			return false; // в воде (на воде) и в лаве (на лаве), в порталах не показываем
		}
		else if (state_current.isNormalCube(world, pos))
		{
			return false; // блок в виде непрозрачного куба (бэдрок, дёрн, не факел, не стеклянный блок) - не показываем
		}
		else
		{
			final Block block_current = state_current.getBlock();
            if (block_current == Blocks.BARRIER)
            {
            	return false; // граница мира - не показываем
            }
    		final BlockPos pos_down = pos.down();
        	final BlockState down_state = world.getBlockState(pos_down);
        	final Material material_down = down_state.getMaterial();
    		//final Block block_down = down_state.getBlock();
            if (material_down == Material.AIR) return false; // воздух (под чем-то прозрачным, под воздухом?) - не показываем
            if (material_down == Material.PORTAL) return false; // в порталах не показываем
            if (material_down == Material.BAMBOO) return false; // блок бамбука непроходим, нет нужды показывать освещение на нём
            if (material_down.isLiquid()) return false; // в воде (на воде) и в лаве (на лаве) не показываем
            if ((material_down == material_current) && (material_current == Material.GLASS)) return false; // в стекле и под стеклом - не показываем
            //if ((block_down instanceof DoorBlock) && (block_current instanceof DoorBlock)) return false; //  - не показываем
            if (!down_state.isNormalCube(world, pos_down)) return false; // под чем-то прозрачным (под воздухом?) что-то непрозрачное (и не кубическое) - не показываем
            //if (!down.isSolid()) return false;	
            
            if (material_current == Material.GLASS)
            {
            	// если под стеклом нет другого стекла, то поскольку стекло прозрачное, то информацию об освещённости
            	// не поднимаем на его верхнее ребро, а оставляем под основанием стеклянного блока
            	illumination.block_height = 0.0;
            }
            else if (block_current instanceof DoorBlock ||
            		 block_current instanceof StandingSignBlock ||
            		 block_current instanceof WallSignBlock ||
            		 block_current instanceof TorchBlock ||
            		 block_current instanceof WallTorchBlock ||
            		 block_current instanceof RedstoneTorchBlock ||
            		 block_current instanceof RedstoneWallTorchBlock)
            {
    			// отключаем расчёт высоты двери, табличек, иначе сведения появятся посередине двери
    			illumination.block_height = 0.0;
            }
		}
        //----------------------------------------------------------------
		if (illumination.block_height < 0)
		{
			// поправка на высоту блока (ковра, саженца,...)
	    	VoxelShape shape = world.getBlockState(pos).getShape(world, pos, ctx);
	    	illumination.block_height = shape.isEmpty() ? 0.0 : shape.getEnd(Direction.Axis.Y);
		}
		illumination.light = world.getLightFor(LightType.BLOCK, pos);
		return true;
    }
    
    private void drawLightOfSurface(ClientWorld world, Direction direction, BlockPos pos, IlluminationData illumination)
    {
    	Tessellator tess = Tessellator.getInstance();
    	BufferBuilder bb = tess.getBuffer();
    	ActiveRenderInfo ari = mc.gameRenderer.getActiveRenderInfo();

    	final double dx = ari.getProjectedView().x;
    	final double dy = ari.getProjectedView().y - illumination.block_height - 0.01d; // поправка на высоту блока (ковра, саженца,...)
    	final double dz = ari.getProjectedView().z;
    	final double chunk_offset = withChunkBorder ? (illumination.our_chunk ? 0.0 : 256.0d) : 0.0d;
        final double item_lt = (16.0d * illumination.light + chunk_offset) / 512.0d;
        final double item_rb = item_lt + 16.0d / 512.0d;

        bb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
        bb.setTranslation(pos.getX()-dx, pos.getY()-dy, pos.getZ()-dz);
        switch (direction.getHorizontalIndex())
        {
        case 0: // south
            bb.pos(0, 0, 0).tex(item_rb, 1).color(255, 255, 255, 255).endVertex();
            bb.pos(0, 0, 1).tex(item_rb, 0).color(255, 255, 255, 255).endVertex();
            bb.pos(1, 0, 1).tex(item_lt, 0).color(255, 255, 255, 255).endVertex();
            bb.pos(1, 0, 0).tex(item_lt, 1).color(255, 255, 255, 255).endVertex();
        	break;
        case 1: // west
            bb.pos(0, 0, 0).tex(item_rb, 0).color(255, 255, 255, 255).endVertex();
            bb.pos(0, 0, 1).tex(item_lt, 0).color(255, 255, 255, 255).endVertex();
            bb.pos(1, 0, 1).tex(item_lt, 1).color(255, 255, 255, 255).endVertex();
            bb.pos(1, 0, 0).tex(item_rb, 1).color(255, 255, 255, 255).endVertex();
        	break;
        case 2: // north
            bb.pos(0, 0, 0).tex(item_lt, 0).color(255, 255, 255, 255).endVertex();
            bb.pos(0, 0, 1).tex(item_lt, 1).color(255, 255, 255, 255).endVertex();
            bb.pos(1, 0, 1).tex(item_rb, 1).color(255, 255, 255, 255).endVertex();
            bb.pos(1, 0, 0).tex(item_rb, 0).color(255, 255, 255, 255).endVertex();
        	break;
        case 3: // east
            bb.pos(0, 0, 0).tex(item_lt, 1).color(255, 255, 255, 255).endVertex();
            bb.pos(0, 0, 1).tex(item_rb, 1).color(255, 255, 255, 255).endVertex();
            bb.pos(1, 0, 1).tex(item_rb, 0).color(255, 255, 255, 255).endVertex();
            bb.pos(1, 0, 0).tex(item_lt, 0).color(255, 255, 255, 255).endVertex();
        	break;
        }
        tess.draw();
    }
}
