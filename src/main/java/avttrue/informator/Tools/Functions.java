package avttrue.informator.Tools;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.UUID;

import avttrue.informator.Informator;
import net.minecraft.block.Block;
import net.minecraft.block.BlockButtonStone;
import net.minecraft.block.BlockButtonWood;
import net.minecraft.block.BlockDaylightDetector;
import net.minecraft.block.BlockLever;
import net.minecraft.block.BlockPressurePlate;
import net.minecraft.block.BlockPressurePlateWeighted;
import net.minecraft.block.BlockRailDetector;
import net.minecraft.block.BlockRedstoneComparator;
import net.minecraft.block.BlockRedstoneRepeater;
import net.minecraft.block.BlockRedstoneWire;
import net.minecraft.block.BlockTripWireHook;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraftforge.common.UsernameCache;
public class Functions 
{
//	
// TODO возвращает скорость блок/секунду
//
	public static double CulcSpeed(double x1, double x2, 
									double y1, double y2, 
									double z1, double z2, long tick1, long tick2)
	{
		try 
		{
			if (tick1 == tick2) // если на паузе
				return 0;
			
			double deltax = x1 - x2;
			double deltay = y1 - y2;
			double deltaz = z1 - z2;
			
			if (deltax > 999 || deltay > 999 || deltaz > 999) // если случались телепорты
			{
				System.out.println("Teleport?");
				return 0;
			}
					
			return new BigDecimal(20 * Math.sqrt(Math.pow(deltax, 2) + // бо 1 сек = 20 тиков
												Math.pow(deltay, 2) + 
												Math.pow(deltaz, 2)) / (Math.abs(tick1 - tick2))).
												setScale(2, RoundingMode.UP).doubleValue();
		}
		catch (Exception e) 
		{
			System.out.println(e.getMessage());
			e.printStackTrace();
			return 0;
		}
	}
	
//	
// TODO возвращает заряд блока
//
	public static String GetTextPower(Minecraft mc, Block block, BlockPos blockPos)
	{
		try 
		{
			if(block != null && blockPos != null)
			{
				if (block == Blocks.REDSTONE_WIRE)
				{
					return "" + mc.theWorld.getBlockState(blockPos).getValue(BlockRedstoneWire.POWER);
				}
				else if (block == Blocks.DETECTOR_RAIL)
				{
					return "" + mc.theWorld.getBlockState(blockPos).getValue(BlockRailDetector.POWERED).compareTo(false) * 15;
				}
				else if (block == Blocks.LEVER)
				{
					return "" + mc.theWorld.getBlockState(blockPos).getValue(BlockLever.POWERED).compareTo(false) * 15;
				}
				else if (block == Blocks.TRIPWIRE_HOOK)
				{
					return "" + mc.theWorld.getBlockState(blockPos).getValue(BlockTripWireHook.POWERED).compareTo(false) * 15;
				}
				else if (block == Blocks.STONE_BUTTON)
				{
					return "" + mc.theWorld.getBlockState(blockPos).getValue(BlockButtonStone.POWERED).compareTo(false) * 15;
				}
				else if (block == Blocks.WOODEN_BUTTON)
				{
					return "" + mc.theWorld.getBlockState(blockPos).getValue(BlockButtonWood.POWERED).compareTo(false) * 15;
				}
				else if (block == Blocks.DAYLIGHT_DETECTOR ||
						block == Blocks.DAYLIGHT_DETECTOR_INVERTED)
				{
					return "" + mc.theWorld.getBlockState(blockPos).getValue(BlockDaylightDetector.POWER);
				}
				else if (block == Blocks.POWERED_COMPARATOR ||
						block == Blocks.UNPOWERED_COMPARATOR)
				{
					return "Mode:" + mc.theWorld.getBlockState(blockPos).getValue(BlockRedstoneComparator.MODE).toString();
				}
				else if (block == Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE || 
						block == Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE)
				{
					return "" + mc.theWorld.getBlockState(blockPos).getValue(BlockPressurePlateWeighted.POWER);
				}
				else if (block == Blocks.STONE_PRESSURE_PLATE ||
						block == Blocks.WOODEN_PRESSURE_PLATE)
				{
					return "" + mc.theWorld.getBlockState(blockPos).getValue(BlockPressurePlate.POWERED).compareTo(false) * 15;
				}
				else if(block == Blocks.UNPOWERED_REPEATER)
				{
					return "Delay:" + mc.theWorld.getBlockState(blockPos).getValue(BlockRedstoneRepeater.DELAY) + " (0)";
				}
				else if(block == Blocks.UNLIT_REDSTONE_TORCH)
				{
					return "" + 0;
				}
				else if(block == Blocks.REDSTONE_TORCH)
				{
					return "" + 15;
				}
				else if(block == Blocks.POWERED_REPEATER)
				{
					return "Delay:" + mc.theWorld.getBlockState(blockPos).getValue(BlockRedstoneRepeater.DELAY) + " (15)";
				}
				else
				{
					return "" + mc.theWorld.isBlockIndirectlyGettingPowered(blockPos);
				}
			}
			else
				return "0";
		}
		catch (Exception e) 
		{
			System.out.println(e.getMessage());
			e.printStackTrace();
			return "0";
		}
	}

//	
// TODO возвращает энчант предмета
//	
	public static ArrayList GetItemEnchants(ItemStack item) 
	{
		ArrayList<String> ReturnedList = new ArrayList<String>();
		try 
		{
			if (item == null) 
				return null;
			
			if (item.getTagCompound() == null) 
				return null;
					
			if (item.getTagCompound().getTag("ench") == null) 
				return null;
						
			NBTTagList enchants = (NBTTagList) item.getTagCompound().getTag("ench");
						
			for (int i = 0; i < enchants.tagCount(); i++) 
			{
				NBTTagCompound enchant = ((NBTTagList) enchants).getCompoundTagAt(i);
				ReturnedList.add(TxtRes.GetEnchantmentNameByEID(enchant.getInteger("id")) + " " + 
								TxtRes.ArabToLatinNumber(enchant.getInteger("lvl")));
			}
			
			return ReturnedList;
		}
		catch (Exception e) 
		{
			System.out.println(e.getMessage());
			e.printStackTrace();
			return null;
		}
	}

//	
// TODO возвращает точный ItemStack 
//
	public static ItemStack GetItemStack(World world, RayTraceResult rtr)
	{
		if (world == null) return null;
		if (rtr == null) return null;	
		
		BlockPos bp = rtr.getBlockPos();
		Block b = world.getBlockState(bp).getBlock();
		IBlockState ibs = world.getBlockState(bp);
		
		if (world.getWorldType() != WorldType.DEBUG_WORLD)
        {
            ibs = ibs.getActualState(world, bp);
        }
		
		return b.getItem(world, bp, ibs);
		
		
		// старый код
		// идея позаимствована из NEI - местами не очень понятно
		/*
		ArrayList<ItemStack> items = new ArrayList<ItemStack>();
		ItemStack common_is = null;
		ItemStack blocks_is = null;
		  
		ItemStack pick = b.getPickBlock(ibs, rtr, world, bp, Minecraft.getMinecraft().thePlayer);
        if (pick != null)
            items.add(pick);
		
        try 
        {
            items.addAll(b.getDrops(world, bp, ibs, 0));
        } 
        catch (Exception ignor) {return null;}
        
        try // ?
        {
        	if (b instanceof IShearable) 
        	{
        		IShearable shearable = (IShearable) b;
        		if (shearable.isShearable(new ItemStack(Items.shears), world, bp))
        			items.addAll(shearable.onSheared(new ItemStack(Items.shears), world, bp, 0));
        	}
        } 
        catch (Exception ignor) {return null;}
        
        if (items.size() == 0)
        	items.add(new ItemStack(b, 1, b.getMetaFromState(ibs)));
		
        // начинаем выбор правильного айтема по дамагу
		int BlockMinDamage = Integer.MAX_VALUE;
		int CommonMinDamage = Integer.MAX_VALUE;
		for(ItemStack istack : items) 
		{
			if(istack.getItem() != null)
            {
				// блоки - отдельно, всё остальное - отдельно
				if (Block.getBlockFromItem(istack.getItem()) != null && 
						istack.getItemDamage() < BlockMinDamage)
				{
					blocks_is = istack;
					BlockMinDamage = istack.getItemDamage();
				}
				else if(istack.getItemDamage() < CommonMinDamage)
				{	
					common_is = istack;
					CommonMinDamage = istack.getItemDamage();
				}
            }
        }
		if (blocks_is != null) return blocks_is; // приоритет за блоками
		else return common_is;
		*/
	}
	
//	
// TODO возвращает свойства куба 
//	
	public static <T extends Comparable<T>> String GetPropertyItemStack(IBlockState ibs)
	{
		String rezult = "";
		for (Entry < IProperty<?>, Comparable<? >> entry : ibs.getProperties().entrySet())
        {
            IProperty<T> iproperty = (IProperty)entry.getKey();
            T t = (T)entry.getValue();
            rezult += iproperty.getName() + ": " + iproperty.getName(t) + " ";
        }
		return rezult;
	}
	
//
// TODO сообщение в чат текущему игроку
//
	public static void SendMessageToUser(String text, Style style)
	{
		if (Minecraft.getMinecraft().ingameGUI.getChatGUI() == null)
			return;
		ITextComponent itc = new TextComponentString(text);
		if (style != null) 
			itc.setStyle(style);
		Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(itc);
	}
	
//	
// TODO возвращает имя по UUID
//
	public static String getUsernameByUUID(UUID uuid) 
	{
	    String username = null;
	    //uuid = UUID.fromString("20d6918d-e3e7-4a69-a83d-39d13a6285ec"); // для проверки
	    try
	    {
	    	if (uuid == null)
	    		return TxtRes.GetLocalText("avttrue.informator.37", "Nobody's"); 
	    	
	    	// ищем в нашем кэше
	    	username = Informator.ProfileCashListFromWeb.FindNameByUUID(uuid.toString());
	    	
	    	// ищем на сайте
	    	if (Informator.TargetMobBar_SeachOwnerInWeb && username == null)
	    		Informator.PWC.SetUUID(uuid.toString());
	    	
	    	// ищем в кэше клиента
	    	if (username == null)
	    		username = UsernameCache.getLastKnownUsername(uuid);
	    	
	    	// нигде не нашли
	    	if (username == null)
	    		return TxtRes.GetLocalText("avttrue.informator.17", "Unknown");
        
	    	return username;
	    }
	    catch (Exception e) 
		{
	    	System.out.println(e.getMessage());
			e.printStackTrace();
			return TxtRes.GetLocalText("avttrue.informator.17", "Unknown");
		}
	}

//	
// TODO количество стрел в инвентаре
//
	public static int GetArrowsCount(EntityPlayerSP player)
	{
		int ArrowCount = 0;
		for (ItemStack its : player.inventory.mainInventory) 
		{
			if (its != null && 
				(its.getItem() == Items.ARROW ||
				its.getItem() == Items.SPECTRAL_ARROW ||
				its.getItem() == Items.TIPPED_ARROW)) 
			{
				ArrowCount+= 1;//its.stackSize;
			}
		}
		return ArrowCount;
	}

// 
// TODO открываем УРЛ
// позаимствовано: minecraft\net\minecraft\client\gui\GuiScreen.java
	public static void openWebLink(String url, boolean AlsoInChat)
	{
		URI uri = null;
		String Space = "_";
		String address = url.replace(" ", Space);
		if(AlsoInChat)
		{
			ClickEvent openWebLinkEvent = new ClickEvent(ClickEvent.Action.OPEN_URL, address);
			Style clickableChatStyle = new Style().setClickEvent(openWebLinkEvent);
			SendMessageToUser("\u00A77\u00A7n" + address, clickableChatStyle);
		}
		System.out.println("Open the web-link: \"" + address + "\"");
		try
		{
			uri = URI.create(address);
		}
		catch (Exception e) 
		{
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
			
		try
		{
			Class<?> oclass = Class.forName("java.awt.Desktop");
			Object object = oclass.getMethod("getDesktop", new Class[0]).invoke((Object)null, new Object[0]);
			oclass.getMethod("browse", new Class[] {URI.class}).invoke(object, new Object[] {uri});
		}
		catch (Throwable throwable)
		{
			System.out.println(throwable.getMessage());
			System.out.println(throwable.getCause());
		}
	}	
}
