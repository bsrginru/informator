package avttrue.informator.Events;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import avttrue.informator.Informator;
import avttrue.informator.Tools.Drawing;
import avttrue.informator.Tools.Functions;
import avttrue.informator.Tools.TxtRes;
import avttrue.informator.Tools.View;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class OnRenderGameOverlay extends Gui 
{
	private Minecraft mc = Minecraft.getMinecraft();
	private ScaledResolution scaledResolution = null;
	
	private static final int PANEL_TRANSPARENT = 0;
	private static final int PANEL_STEEL = -9408400;
	private static final int PANEL_GRAY = Color.lightGray.getRGB();
	
	private static final int FONT_WHITE = 0xffffff;
	private static final int FONT_GRAY = 0xBFBFBE;
	private static final int FONT_GREEN = 0x00FF00;
	private static final int FONT_BLUE = 0x0040FF;
	private static final int FONT_AQUA = 0x0080FF;
	private static final int FONT_RED = 0xFF0000;
	
	private static final int ICON_SIZE = 16;
	private static int STRING_HEIGHT = 10;
	
	private static final int BUFF_ICON_SPACING = 20;
	private static final int BUFF_ICON_BASE_U_OFFSET = 0;
	private static final int BUFF_ICON_BASE_V_OFFSET = 198;
	private static final int BUFF_ICONS_PER_ROW = 8;
	private static final String mobsetts = null;
	
	private static int DAMAGE_ALARM = 10;
	
	// направление взгляда
	private View view = null;
	
	public OnRenderGameOverlay() 
	{
		super();
		STRING_HEIGHT = mc.fontRendererObj.FONT_HEIGHT;
	}

	@SubscribeEvent
	public void onRenderExperienceBar(RenderGameOverlayEvent event) 
	{
			if (event.getType() != ElementType.HOTBAR || // показывает в отрисовке хотбара
				!Informator.Gobal_ON) // выключили по горячей клавише
				return;
			
			// если в дебаг-режиме и показ выключен для этого режима
			if (mc.getMinecraft().gameSettings.showDebugInfo && 
					Informator.Global_HideInDebugMode) 
				return;
			
			// размеры экрана
			scaledResolution = new ScaledResolution(mc);
			
			// направление взгляда
			view = new View();
			
			// held item bar
			if(Informator.HeldItemDetails_Show) CreateHeldItemBar();

			// block bar
			if(Informator.InfoBlockBar_Show) CreateBlockBar();

			// Clock bar
			if (Informator.TimeBar_Show) CreateClockBar();
			
			// Speed Bar
			if(Informator.SpeedBar_Show) CreateSpeedBar();
			
			// Current Enchantments
			if(Informator.EnchantBar_Show) CreateEnchantBar();
			
			// Target Mob
			if(Informator.TargetMobBar_Show) CreateTargetMobBar();
			
			// Thesaurus
			DrawThesaurusButton();
			
			GlStateManager.enableRescaleNormal();	
			GlStateManager.enableBlend();
		}

	//---------------------------------------------------------------------------------------------------------------
	
///////////////////////////////////////////////////////////////////////////////////
// TODO Current Enchantments Bar
///////////////////////////////////////////////////////////////////////////////////	
	public void CreateEnchantBar()
	{
		try
		{
			int deltaY = 0; // смещение по высоте
			
			ArrayList<String> EnchantmentsList = null; // список зачарований на предмете
			List <ItemStack> istacks = new ArrayList<ItemStack>(); // список предметов
					
			if(Informator.EnchantBar_ShowHands) 
			{
				istacks.add(mc.thePlayer.getHeldItemMainhand()); //добавляем удерживаемый предмет в основной руке
				istacks.add(mc.thePlayer.getHeldItemOffhand()); //добавляем удерживаемый предмет во второй руке
			}
			
			if(Informator.EnchantBar_ShowBody)
			{
				for(int i=0; i < 4; i++) 
					istacks.add(mc.thePlayer.inventory.armorInventory.get(i)); // одетые предметы
			}
			
			for(Iterator<ItemStack> is = istacks.iterator(); is.hasNext();)
			{
				ItemStack istack = is.next();
				EnchantmentsList = Functions.GetItemEnchants(istack);
				if(EnchantmentsList != null)
				{
					int iterator = 0; // считаем строки
					for(Iterator<String> s = EnchantmentsList.iterator(); s.hasNext();) 
					{
						String text = " " + s.next() + " ";
						int sLen = mc.fontRendererObj.getStringWidth(text);
						int xPos = scaledResolution.getScaledWidth() - sLen;
						if(iterator == 0) xPos -= ICON_SIZE;
						
						// отрисовка панели
						if (Informator.Global_ShowPanel) 
						{
							if(iterator == 0)
								drawGradientRect(xPos, deltaY + Informator.EnchantBar_yPos, xPos + ICON_SIZE + sLen,
										deltaY + Informator.EnchantBar_yPos + ICON_SIZE, PANEL_STEEL, PANEL_TRANSPARENT);
							else
								drawGradientRect(xPos, deltaY + Informator.EnchantBar_yPos, xPos + sLen,
										deltaY + Informator.EnchantBar_yPos + STRING_HEIGHT, PANEL_STEEL, PANEL_TRANSPARENT);
						}   
					
						// отрисовка текста
						if(iterator == 0)
							mc.fontRendererObj.drawStringWithShadow(text, xPos + ICON_SIZE,
									deltaY + Informator.EnchantBar_yPos + STRING_HEIGHT / 3, FONT_WHITE);
						else
							mc.fontRendererObj.drawStringWithShadow(text, xPos,
									deltaY + Informator.EnchantBar_yPos, FONT_WHITE);

						// отрисовка иконки
						if(iterator == 0)
						{
							Drawing.DrawItemStack(mc.getRenderItem(), istack, xPos, deltaY + Informator.EnchantBar_yPos);
						}
					
						if(iterator == 0) deltaY += ICON_SIZE; 
						else deltaY += STRING_HEIGHT;
					
						iterator++;
					}
				}
				deltaY +=1;
			}
		}
		catch (Exception e) 
		{
			System.out.println(e.getMessage());
			e.printStackTrace();
			Informator.Gobal_ON = false;
			Functions.SendMessageToUser("\u00A7c" + TxtRes.GetLocalText("avttrue.informator.26", 
					"The mod Informator made a mistake and was off"), null);
		}
	}
	
///////////////////////////////////////////////////////////////////////////////////
// TODO HeldItem BAR
///////////////////////////////////////////////////////////////////////////////////
	
	public void CreateHeldItemBar()
	{
		try
		{
			DAMAGE_ALARM = Informator.HeldItemDetails_DamageAlarm;
			
			ItemStack heldMHIS = mc.thePlayer.getHeldItemMainhand();
			ItemStack heldOHIS = mc.thePlayer.getHeldItemOffhand();
			ItemStack headIS = mc.thePlayer.inventory.armorItemInSlot(3);
			ItemStack bodyIS = mc.thePlayer.inventory.armorItemInSlot(2);
			ItemStack legsIS = mc.thePlayer.inventory.armorItemInSlot(1);
			ItemStack footsIS = mc.thePlayer.inventory.armorItemInSlot(0);
			
			Item heldMHI = null;
			Item heldOHI = null;
			Item headI = null;
			Item bodyI = null;
			Item legsI = null;
			Item footsI = null;
			
			if (heldMHIS == null && heldOHIS == null && headIS == null &&
				bodyIS == null && legsIS == null && footsIS == null) 
				return;

			String heldMHItemInfo = "";
			String heldOHItemInfo = "";
			String headItemInfo = "";
			String bodyItemInfo = "";
			String legsItemInfo = "";
			String footsItemInfo = "";
			
			int heldItemMHInfo_Len = 0;
			int heldItemOHInfo_Len = 0;
			int headItemInfo_Len = 0;
			int bodyItemInfo_Len = 0;
			int legsItemInfo_Len = 0;
			int footsItemInfo_Len = 0;
			
			int color_panel = PANEL_STEEL;
			
			int ArrowCount = 0;
			
			// настройки позиционирования
			int xPos = Informator.HeldItemDetails_xPos;
			int yPos = Informator.HeldItemDetails_yPos;
			
			if(heldMHIS != null) // рука основная
			{
				heldMHI = heldMHIS.getItem();
				
				if (heldMHI.isDamageable())
				{
					heldMHItemInfo = (heldMHI.getMaxDamage(heldMHIS) - heldMHI.getDamage(heldMHIS) + 1) + "/"
						+ (heldMHI.getMaxDamage(heldMHIS) + 1) + " ";
					if (heldMHI == Items.BOW) 
					{
						// ищем стрелы
						ArrowCount = Functions.GetArrowsCount(mc.thePlayer);
						heldMHItemInfo += "("+ ArrowCount + ") ";
					}
					heldItemMHInfo_Len = mc.fontRendererObj.getStringWidth(heldMHItemInfo) + ICON_SIZE;
				}
			}
			
			if(heldOHIS != null) // рука вторая
			{
				heldOHI = heldOHIS.getItem();
				
				if (heldOHI.isDamageable())
				{
					heldOHItemInfo = (heldOHI.getMaxDamage(heldOHIS) - heldOHI.getDamage(heldOHIS) + 1) + "/"
						+ (heldOHI.getMaxDamage(heldOHIS) + 1) + " ";
					if (heldOHI == Items.BOW) 
					{
						// ищем стрелы
						if(ArrowCount == 0) ArrowCount = Functions.GetArrowsCount(mc.thePlayer);
						heldOHItemInfo += "("+ ArrowCount + ") ";
					}
					heldItemOHInfo_Len = mc.fontRendererObj.getStringWidth(heldOHItemInfo) + ICON_SIZE;
				}
			}
			
			if(headIS != null) // голова
			{
				headI = headIS.getItem(); 
				if (headI.isDamageable())
				{
				headItemInfo = (headI.getMaxDamage(headIS) - headI.getDamage(headIS) + 1) + "/"
						+ (headI.getMaxDamage(headIS) + 1) + " ";
				headItemInfo_Len = mc.fontRendererObj.getStringWidth(headItemInfo) + ICON_SIZE;
				}
			}
			if(bodyIS != null) // тело
			{
				bodyI = bodyIS.getItem();
				if (bodyI.isDamageable())
				{
					bodyItemInfo = (bodyI.getMaxDamage(bodyIS) - bodyI.getDamage(bodyIS) + 1) + "/"
						+ (bodyI.getMaxDamage(bodyIS) + 1) + " ";
					bodyItemInfo_Len = mc.fontRendererObj.getStringWidth(bodyItemInfo) + ICON_SIZE;
				}
			}
			if(legsIS != null) // ноги
			{
				legsI = legsIS.getItem();
				if (legsI.isDamageable())
				{
					legsItemInfo = (legsI.getMaxDamage(legsIS) - legsI.getDamage(legsIS) + 1) + "/"
						+ (legsI.getMaxDamage(legsIS) + 1) + " ";
					legsItemInfo_Len = mc.fontRendererObj.getStringWidth(legsItemInfo) + ICON_SIZE;
				}
			}
			if(footsIS != null) // ступни
			{
				footsI = footsIS.getItem();
				if (footsI.isDamageable()) 
				{
					footsItemInfo = (footsI.getMaxDamage(footsIS) - footsI.getDamage(footsIS) + 1) + "/"
						+ (footsI.getMaxDamage(footsIS) + 1) + " ";
					footsItemInfo_Len = mc.fontRendererObj.getStringWidth(footsItemInfo) + ICON_SIZE;
				}
			}	
			
			// обработка общих настроек позиционирования
			if (Informator.HeldItemDetails_alignMode.toLowerCase().contains("default")) 
			{
				xPos = 0;
				yPos = ICON_SIZE * 2 + 22;
			}
						
			// текст и иконки
			if (headI != null && headI.isDamageable()) // голова
			{
				// отрисовка панели
				if (Informator.Global_ShowPanel) 
				{
					color_panel = PANEL_STEEL;
					if ((float)(headI.getMaxDamage(headIS) - headI.getDamage(headIS)) / 
							(float)headI.getMaxDamage(headIS) * 100 < DAMAGE_ALARM)
						color_panel = Color.red.getRGB();
					drawGradientRect(xPos, yPos, xPos + headItemInfo_Len, yPos + ICON_SIZE, color_panel, PANEL_TRANSPARENT);
				}
				// отрисовка иконки
				Drawing.DrawItemStack(mc.getRenderItem(), headIS, xPos, yPos);
				
				// отрисовка текста
				mc.fontRendererObj.drawStringWithShadow(headItemInfo, xPos + ICON_SIZE, yPos + STRING_HEIGHT/3, FONT_WHITE);
				
				yPos += ICON_SIZE;
			}
						
			if (bodyI != null && bodyI.isDamageable()) // тело
			{
				// отрисовка панели
				if (Informator.Global_ShowPanel) 
				{
					color_panel = PANEL_STEEL;
					if ((float)(bodyI.getMaxDamage(bodyIS) - bodyI.getDamage(bodyIS)) / 
							(float)bodyI.getMaxDamage(bodyIS) * 100 < DAMAGE_ALARM)
						color_panel = Color.red.getRGB();
					drawGradientRect(xPos, yPos, xPos + bodyItemInfo_Len, yPos + ICON_SIZE, color_panel, PANEL_TRANSPARENT);
				}
				// отрисовка иконки
				Drawing.DrawItemStack(mc.getRenderItem(), bodyIS, xPos, yPos);
				// отрисовка текста
				mc.fontRendererObj.drawStringWithShadow(bodyItemInfo, xPos + ICON_SIZE, yPos + STRING_HEIGHT/3, FONT_WHITE);
				yPos += ICON_SIZE;
			}
			
			if (legsI != null && legsI.isDamageable()) // ноги
			{
				// отрисовка панели
				if (Informator.Global_ShowPanel) 
				{
					color_panel = PANEL_STEEL;
					if ((float)(legsI.getMaxDamage(legsIS) - legsI.getDamage(legsIS)) / 
							(float)legsI.getMaxDamage(legsIS) * 100 < DAMAGE_ALARM)
						color_panel = Color.red.getRGB();
					drawGradientRect(xPos, yPos, xPos + legsItemInfo_Len, yPos + ICON_SIZE, color_panel, PANEL_TRANSPARENT);
				}
				// отрисовка иконки
				Drawing.DrawItemStack(mc.getRenderItem(), legsIS, xPos, yPos);
				// отрисовка текста
				mc.fontRendererObj.drawStringWithShadow(legsItemInfo, xPos + ICON_SIZE, yPos + STRING_HEIGHT/3, FONT_WHITE);
				yPos += ICON_SIZE;
			}
			
			if (footsI != null && footsI.isDamageable()) // ступни
			{
				// отрисовка панели
				if (Informator.Global_ShowPanel) 
				{
					color_panel = PANEL_STEEL;
					if ((float)(footsI.getMaxDamage(footsIS) - footsI.getDamage(footsIS)) / 
							(float)footsI.getMaxDamage(footsIS) * 100 < DAMAGE_ALARM)
						color_panel = Color.red.getRGB();
					drawGradientRect(xPos, yPos, xPos + footsItemInfo_Len, yPos + ICON_SIZE, color_panel, PANEL_TRANSPARENT);
				}
				// отрисовка иконки
				Drawing.DrawItemStack(mc.getRenderItem(), footsIS, xPos, yPos);
				// отрисовка текста
				mc.fontRendererObj.drawStringWithShadow(footsItemInfo, xPos + ICON_SIZE, yPos + STRING_HEIGHT/3, FONT_WHITE);
				yPos += ICON_SIZE;
			}
			
			if (heldMHI != null && heldMHI.isDamageable()) // предмет в основной руке
			{
				// отрисовка панели
				if (Informator.Global_ShowPanel) 
				{
					color_panel = PANEL_STEEL;
					if ((float)(heldMHI.getMaxDamage(heldMHIS) - heldMHI.getDamage(heldMHIS)) / 
							(float)heldMHI.getMaxDamage(heldMHIS) * 100 < DAMAGE_ALARM)
						color_panel = Color.red.getRGB();
					drawGradientRect(xPos, yPos, xPos + heldItemMHInfo_Len, yPos + ICON_SIZE, color_panel, PANEL_TRANSPARENT);
				}
				// отрисовка иконки
				Drawing.DrawItemStack(mc.getRenderItem(), heldMHIS, xPos, yPos);
				// отрисовка текста
				mc.fontRendererObj.drawStringWithShadow(heldMHItemInfo, xPos + ICON_SIZE, yPos + STRING_HEIGHT/3, FONT_WHITE);
				yPos += ICON_SIZE;
			}
			
			if (heldOHI != null && heldOHI.isDamageable()) // предмет во второй руке
			{
				// отрисовка панели
				if (Informator.Global_ShowPanel) 
				{
					color_panel = PANEL_STEEL;
					if ((float)(heldOHI.getMaxDamage(heldOHIS) - heldOHI.getDamage(heldOHIS)) / 
							(float)heldOHI.getMaxDamage(heldOHIS) * 100 < DAMAGE_ALARM)
						color_panel = Color.red.getRGB();
					drawGradientRect(xPos, yPos, xPos + heldItemOHInfo_Len, yPos + ICON_SIZE, color_panel, PANEL_TRANSPARENT);
				}
				// отрисовка иконки
				Drawing.DrawItemStack(mc.getRenderItem(), heldOHIS, xPos, yPos);
				// отрисовка текста
				mc.fontRendererObj.drawStringWithShadow(heldOHItemInfo, xPos + ICON_SIZE, yPos + STRING_HEIGHT/3, FONT_WHITE);
				yPos += ICON_SIZE;
			}
		}

		catch (Exception e) 
		{
			System.out.println(e.getMessage());
			e.printStackTrace();
			Informator.Gobal_ON = false;
			Functions.SendMessageToUser("\u00A7c" + TxtRes.GetLocalText("avttrue.informator.26", 
					"The mod Informator made a mistake and was off"), null);
		}
	}
	
///////////////////////////////////////////////////////////////////////////////////
// TODO InfoBlock Bar
///////////////////////////////////////////////////////////////////////////////////
	
	public void CreateBlockBar()
	{
		try
		{
			// удалось определить блок, на который смотрим
			if (view.targetBlock == null) 
				return;
			
			// если мы смотрим не в воздух
			if (view.tBlock.isAir(view.tBlockState, mc.theWorld, view.tBlockPosition)) 
				return;
			
			ItemStack itst = Functions.GetItemStack(mc.theWorld, view.targetBlock);
			if (itst == null) return; // не можем нарисовать иконку (значит, вообщё чёрти что)
			
			int InfoBlockBar_xPos = Informator.InfoBlockBar_xPos;
			int InfoBlockBar_yPos = Informator.InfoBlockBar_yPos;
			int InfoBlockBar_PanelH = 4;
			
			// блок сверху того, на который смотрим
			BlockPos upBlockPos = new BlockPos(view.tBlockPosition.getX(), 
												view.tBlockPosition.getY() + 1, 
												view.tBlockPosition.getZ());
			Block upBlock = mc.theWorld.getBlockState(upBlockPos).getBlock();

			// имя блока
			String BlockName = " " + itst.getDisplayName();
			
			int BlockName_xPos = 0;
			int BlockName_yPos = 0;			
			int BlockName_strLen = mc.fontRendererObj.getStringWidth(BlockName);
			
			// координаты
			String BlockXYZ = " X=" + view.tBlockPosition.getX() + " Y=" + 
								view.tBlockPosition.getY() + " Z=" + 
								view.tBlockPosition.getZ() + " ";

			// расстояние
			String BlockXYZDelta = " dX=" + Math.abs((int) Math.floor(mc.thePlayer.posX) - view.tBlockPosition.getX()) +
					" dY=" + Math.abs((int) Math.floor(mc.thePlayer.posY) - view.tBlockPosition.getY() - 1) + // ибо Y игрока отображается относительно куба ног 
					" dZ=" + Math.abs((int) Math.floor(mc.thePlayer.posZ) - view.tBlockPosition.getZ()) + " ";

			// освещённость
			String BlockLight = "";
			if (upBlockPos.getY() > 254)  // выше 255 строить нельзя
			{
				BlockLight = " " + TxtRes.GetLocalText("avttrue.informator.2", "Light") +
						"=" + EnumSkyBlock.BLOCK.defaultLightValue + " / " + 
						EnumSkyBlock.SKY.defaultLightValue + " (" + 
						TxtRes.GetLocalText("avttrue.informator.14", "sky") + ") ";
			}
			else
			{
				Chunk c = mc.theWorld.getChunkFromBlockCoords(upBlockPos);
				BlockLight = " " + TxtRes.GetLocalText("avttrue.informator.2", "Light") +
						"=" + c.getLightFor(EnumSkyBlock.BLOCK, upBlockPos) + " / " + 
						c.getLightFor(EnumSkyBlock.SKY, upBlockPos) + " (" + 
						TxtRes.GetLocalText("avttrue.informator.14", "sky") + ") ";	
			}

			// заряд блока
			String BlockPower = " " + TxtRes.GetLocalText("avttrue.informator.3", "Power") +
					"=" + Functions.GetTextPower(mc, view.tBlock, view.tBlockPosition) + " ";
			
			int InfoBlockBar_strLen =ICON_SIZE + Math.max(mc.fontRendererObj.getStringWidth(BlockXYZ),
												(Math.max(mc.fontRendererObj.getStringWidth(BlockXYZDelta),
												(Math.max(mc.fontRendererObj.getStringWidth(BlockLight),
												(mc.fontRendererObj.getStringWidth(BlockPower)))))));
			
			// отрисовка панели
			if(Informator.InfoBlockBar_alignMode.toLowerCase().contains("bottomright"))
 			{
				InfoBlockBar_xPos = scaledResolution.getScaledWidth() - InfoBlockBar_strLen;
				InfoBlockBar_yPos = scaledResolution.getScaledHeight() - STRING_HEIGHT * InfoBlockBar_PanelH;
				BlockName_yPos = InfoBlockBar_yPos - STRING_HEIGHT;
				BlockName_xPos = scaledResolution.getScaledWidth() - BlockName_strLen;
 			}
			else if(Informator.InfoBlockBar_alignMode.toLowerCase().contains("topright"))
 			{
				InfoBlockBar_xPos = scaledResolution.getScaledWidth() - InfoBlockBar_strLen;
				InfoBlockBar_yPos = 0;
				BlockName_yPos = STRING_HEIGHT * InfoBlockBar_PanelH;
				BlockName_xPos = scaledResolution.getScaledWidth() - BlockName_strLen;
 			}
			else if(Informator.InfoBlockBar_alignMode.toLowerCase().contains("topleft"))
 			{
				InfoBlockBar_xPos = 0;
				InfoBlockBar_yPos = 0;
				BlockName_yPos = STRING_HEIGHT * InfoBlockBar_PanelH;
				BlockName_xPos = 0;
 			}
			else if(Informator.InfoBlockBar_alignMode.toLowerCase().contains("bottomleft"))
 			{
				InfoBlockBar_xPos = 0;
				InfoBlockBar_yPos = scaledResolution.getScaledHeight() - STRING_HEIGHT * InfoBlockBar_PanelH;
				BlockName_yPos = InfoBlockBar_yPos - STRING_HEIGHT;
				BlockName_xPos = 0;
 			}
			
			if (Informator.Global_ShowPanel) 
			{
				drawGradientRect(InfoBlockBar_xPos, InfoBlockBar_yPos,
						InfoBlockBar_xPos + InfoBlockBar_strLen, 
						InfoBlockBar_yPos + STRING_HEIGHT * InfoBlockBar_PanelH,
						PANEL_STEEL, PANEL_TRANSPARENT);
			}

			// отрисовка текста
			// координаты
			mc.fontRendererObj.drawStringWithShadow(BlockXYZ, InfoBlockBar_xPos + ICON_SIZE, 
					InfoBlockBar_yPos, FONT_WHITE);
			// дистанция
			mc.fontRendererObj.drawStringWithShadow(BlockXYZDelta, InfoBlockBar_xPos  + ICON_SIZE, 
					InfoBlockBar_yPos + STRING_HEIGHT, FONT_WHITE);
			// освещённость
			mc.fontRendererObj.drawStringWithShadow(BlockLight, InfoBlockBar_xPos + ICON_SIZE, 
					InfoBlockBar_yPos + STRING_HEIGHT * 2, FONT_WHITE);
			// заряд
			mc.fontRendererObj.drawStringWithShadow(BlockPower, InfoBlockBar_xPos + ICON_SIZE, 
					InfoBlockBar_yPos + STRING_HEIGHT * 3, FONT_WHITE);
			
			// отрисовка панели имени блока
			if(Informator.InfoBlockBar_ShowName)
			{
				if (Informator.Global_ShowPanel) 
				{
					drawGradientRect(BlockName_xPos, BlockName_yPos,
									BlockName_xPos + BlockName_strLen, 
									BlockName_yPos + STRING_HEIGHT,
									PANEL_STEEL, PANEL_TRANSPARENT);
				}
				// имя блока
				if(!BlockName.isEmpty())
					mc.fontRendererObj.drawStringWithShadow(BlockName, BlockName_xPos, 
											BlockName_yPos, FONT_WHITE);
			}
			// отрисовка иконки
			if(Informator.InfoBlockBar_ShowIcons)
			{
				Drawing.DrawItemStack(mc.getRenderItem(), itst, InfoBlockBar_xPos, InfoBlockBar_yPos);
				
			}
			else
			{
				mc.renderEngine.bindTexture(new ResourceLocation("avttrue_informator:textures/icons.png"));
				drawTexturedModalRect(InfoBlockBar_xPos, InfoBlockBar_yPos, 0, 16, 10, 10);
			}
			
		}
		catch (Exception e) 
		{
			System.out.println(e.getMessage());
			e.printStackTrace();
			Informator.Gobal_ON = false;
			Functions.SendMessageToUser("\u00A7c" + TxtRes.GetLocalText("avttrue.informator.26", 
					"The mod Informator made a mistake and was off"), null);
		}
	}
	
///////////////////////////////////////////////////////////////////////////////////
// TODO SPEED BAR
///////////////////////////////////////////////////////////////////////////////////
	
	public void CreateSpeedBar()
	{
		try
		{
			int SpeedBar_xPos = Informator.SpeedBar_xPos;
			int SpeedBar_yPos = Informator.SpeedBar_yPos;

			String sSpeed = " " + TxtRes.GetLocalText("avttrue.informator.1", "Speed") + ": " + 
								String.format("%1$5.2f %2$s ", Informator.Speed, 
											TxtRes.GetLocalText("avttrue.informator.15", "b/s")); 
			int iSpeedLen = mc.fontRendererObj.getStringWidth(sSpeed);

			// отрисовка панели
			if (Informator.Global_ShowPanel) 
			{
				drawGradientRect(SpeedBar_xPos, SpeedBar_yPos,
										SpeedBar_xPos + 16 + iSpeedLen,
										SpeedBar_yPos + 16, PANEL_STEEL, PANEL_TRANSPARENT);
			}

			// отрисовка текста
			mc.fontRendererObj.drawStringWithShadow(sSpeed, SpeedBar_xPos + ICON_SIZE,
													SpeedBar_yPos + STRING_HEIGHT / 3, FONT_WHITE);

			// отрисовка иконки
			Drawing.DrawItemStack(mc.getRenderItem(), new ItemStack(Items.COMPASS), SpeedBar_xPos, SpeedBar_yPos);
		}
		catch (Exception e) 
		{
			System.out.println(e.getMessage());
			e.printStackTrace();
			Informator.Gobal_ON = false;
			Functions.SendMessageToUser("\u00A7c" + TxtRes.GetLocalText("avttrue.informator.26", 
					"The mod Informator made a mistake and was off"), null);
		}
	}
	
///////////////////////////////////////////////////////////////////////////////////
// TODO Time Bar
///////////////////////////////////////////////////////////////////////////////////
	
	public void CreateClockBar()
	{
		try
		{
 			String currentTime = ""; // время реальное и игровое
 			
 			int currentTime_xPos = Informator.TimeBar_xPos;
 			int currentTime_yPos = Informator.TimeBar_yPos;
 			
 			// Реальное время
 			Date date = new Date();

 			// игровое время
 			if (Informator.worldTime > -1) // если время определено
 			{
 				 /* ибо:
 				 * http://minecraft.gamepedia.com/Time
 				 * 1 tick of gameplay = 3.6 Minecraft seconds
 				 * + 6 часов, т.к. 0-й тик = 6:00
 				 * 6*60*60 = 21600
 				 * 24*60*60 = 86400
 				 */
 				
 				long mcTimeSec = (long) ((Informator.worldTime * 3.6 + 21600) % 86400); // секунд натикало в текущем дне
 				long mcTimeMin = (mcTimeSec - mcTimeSec % 60) / 60;
 				long mcTimeHour = (mcTimeMin - mcTimeMin % 60) / 60;
 				mcTimeMin -= mcTimeHour * 60; // Нормализуем к часам

 				// для справки
 				currentTime = String.format(" %1$tT || %2$02d:%3$02d ", date.getTime(), mcTimeHour, mcTimeMin);
 			} 
 			else // если время не определено
 			{
 				currentTime = String.format(" %1$tT || ??:?? ", date.getTime());
 			}

 			int currentTimeStrLen = mc.fontRendererObj.getStringWidth(currentTime);
 			
 			// отрисовка панели
 			int dy = 0; // учёт сдвига на размер иконки луны при прижатии к низу
			if(Informator.TimeBarMoon_Show || Informator.TimeBarWeather_Show) dy = 20;
 			if(Informator.TimeBar_alignMode.toLowerCase().contains("topleft"))
 			{
 				currentTime_xPos = 0;
 				currentTime_yPos = 0;
 			}
 			else if(Informator.TimeBar_alignMode.toLowerCase().contains("topright"))
 			{
 				currentTime_xPos = scaledResolution.getScaledWidth() - currentTimeStrLen - ICON_SIZE;
 				currentTime_yPos = 0;
 			}
 			else if(Informator.TimeBar_alignMode.toLowerCase().contains("bottomleft"))
 			{
 				currentTime_xPos = 0;
 				currentTime_yPos = scaledResolution.getScaledHeight() - ICON_SIZE - dy;
 			}
 			else if(Informator.TimeBar_alignMode.toLowerCase().contains("bottomright"))
 			{
 				currentTime_xPos = scaledResolution.getScaledWidth() - currentTimeStrLen - ICON_SIZE;
 				currentTime_yPos = scaledResolution.getScaledHeight() - ICON_SIZE - dy;
 			}
 			
 			if (Informator.Global_ShowPanel) 
 			{
 				drawGradientRect(currentTime_xPos, currentTime_yPos, 
 						currentTime_xPos + ICON_SIZE + currentTimeStrLen,
 						currentTime_yPos + ICON_SIZE, PANEL_STEEL, PANEL_TRANSPARENT);
 			}

 			// отрисовка текста
 			mc.fontRendererObj.drawStringWithShadow(currentTime, currentTime_xPos + ICON_SIZE, 
 													currentTime_yPos + 2, FONT_WHITE);

 			// отрисовка иконки
 			Drawing.DrawItemStack(mc.getRenderItem(), new ItemStack(Items.CLOCK), currentTime_xPos, currentTime_yPos);
 			//
 			// фазы луны
 			//
			int moonPhaseFactorLen = 0; // длина текста фазы луны
 			if (Informator.TimeBarMoon_Show) 
 			{
 				int moonPhase = mc.theWorld.getMoonPhase();
 				float moonPhaseFactor = mc.theWorld.getCurrentMoonPhaseFactor();
 				String sMoonPhase = " " + TxtRes.GetLocalText("avttrue.informator.4", "Phase") + ": " + moonPhase + " ";
 				String sMoonPhaseFactor = "";
 				String sMoonPhase_FACTOR = TxtRes.GetLocalText("avttrue.informator.5", "Factor");

 				// расшифровка фактора фазы луны
 				if (moonPhaseFactor == 1.0) 
 					sMoonPhaseFactor = " " + sMoonPhase_FACTOR + ": " +
 											TxtRes.GetLocalText("avttrue.informator.6", "Full") + " ";
 				else if (moonPhaseFactor == 0.75) 
 					sMoonPhaseFactor = " " + sMoonPhase_FACTOR + ": " +
											TxtRes.GetLocalText("avttrue.informator.7", "Gibbous") + " ";
 				else if (moonPhaseFactor == 0.5) 
 					sMoonPhaseFactor = " " + sMoonPhase_FACTOR + ": " +
 											TxtRes.GetLocalText("avttrue.informator.8", "Quarter") + " ";
 				else if (moonPhaseFactor == 0.25) 
 					sMoonPhaseFactor = " " + sMoonPhase_FACTOR + ": " +
											TxtRes.GetLocalText("avttrue.informator.9", "Crescent") + " ";
 				else if (moonPhaseFactor == 0.0) 
 					sMoonPhaseFactor = " " + sMoonPhase_FACTOR + ": " +
 											TxtRes.GetLocalText("avttrue.informator.10", "New") + " ";
 				else
 					sMoonPhaseFactor = " " + sMoonPhase_FACTOR + ": " + moonPhaseFactor + " ";

 				moonPhaseFactorLen = mc.fontRendererObj.getStringWidth(sMoonPhaseFactor);

 				// отрисовка панели
 			    // позиция и размеры
 				int Weather_Len = 0; // для сдвига на размер иконки погоды
 					
 				if(Informator.TimeBar_alignMode.toLowerCase().contains("topleft"))
 	 			{
 	 				currentTime_xPos = 0 + (Informator.TimeBarWeather_Show ? 1 : 0) * 20;
 	 				currentTime_yPos = 0;
 	 			}
 				else if(Informator.TimeBar_alignMode.toLowerCase().contains("bottomleft"))
 	 			{
 	 				currentTime_xPos = 0 + (Informator.TimeBarWeather_Show ? 1 : 0) * 20;
 	 			}
 	 			else if(Informator.TimeBar_alignMode.toLowerCase().contains("topright"))
 	 			{
 	 				currentTime_xPos = scaledResolution.getScaledWidth() - moonPhaseFactorLen - 20;
 	 				currentTime_yPos = 0;
 	 			}
 	 			else if(Informator.TimeBar_alignMode.toLowerCase().contains("bottomright"))
 	 			{
 	 				currentTime_xPos = scaledResolution.getScaledWidth() - moonPhaseFactorLen - 20;
 	 			}
 				
 				if (Informator.Global_ShowPanel) 
 				{
 					drawGradientRect(currentTime_xPos, currentTime_yPos + ICON_SIZE,
 							currentTime_xPos + moonPhaseFactorLen + 20,
 							currentTime_yPos + STRING_HEIGHT + 26, PANEL_STEEL, PANEL_TRANSPARENT);
 				}

 				// отрисовка текста
 				mc.fontRendererObj.drawStringWithShadow(sMoonPhase, currentTime_xPos + 20,
 						currentTime_yPos + ICON_SIZE, FONT_WHITE);
 				mc.fontRendererObj.drawStringWithShadow(sMoonPhaseFactor, currentTime_xPos + 20,
 						currentTime_yPos + ICON_SIZE + STRING_HEIGHT + 1, FONT_WHITE);

 				// отрисовка иконки луны
 				mc.renderEngine.bindTexture(new ResourceLocation("avttrue_informator:textures/weather_icon.png"));
 				drawTexturedModalRect(currentTime_xPos, currentTime_yPos + ICON_SIZE,
 						20 * moonPhase, 21, 20, 20);

 			}
			
			//
			// погода
			//
			if (Informator.TimeBarWeather_Show) 
 			{
				float weatherReain = mc.theWorld.getRainStrength(1.0f);
				float weatherThander = mc.theWorld.getThunderStrength(0.1f);
				
				int weatherPhase = 0;  // солнечно
				if(weatherReain > 0 && weatherThander > 0) // дождь с грозой
					weatherPhase = 2;
				else if(weatherReain > 0)  // дождь
					weatherPhase = 1;
				
				if(Informator.TimeBar_alignMode.toLowerCase().contains("topleft"))
 	 			{
 	 				currentTime_xPos = 0;
 	 				currentTime_yPos = 0;
 	 			}
				else if(Informator.TimeBar_alignMode.toLowerCase().contains("bottomleft"))
 	 			{
 	 				currentTime_xPos = 0;
 	 			}
 	 			else if(Informator.TimeBar_alignMode.toLowerCase().contains("topright"))
 	 			{
 	 				currentTime_xPos = scaledResolution.getScaledWidth() - moonPhaseFactorLen - 
 	 						(Informator.TimeBarMoon_Show ? 1 : 0) * 20 - 20;
 	 				currentTime_yPos = 0;
 	 			}
 	 			else if(Informator.TimeBar_alignMode.toLowerCase().contains("bottomright"))
 	 			{
 	 				currentTime_xPos = scaledResolution.getScaledWidth() - moonPhaseFactorLen -
 	 						(Informator.TimeBarMoon_Show ? 1 : 0) * 20 - 20;
 	 			}
				// отрисовка панели
				if (Informator.Global_ShowPanel) 
 				{
 					drawGradientRect(currentTime_xPos, currentTime_yPos + ICON_SIZE,
 							currentTime_xPos + 20,
 							currentTime_yPos + STRING_HEIGHT + 26, PANEL_STEEL, PANEL_TRANSPARENT);
 				}
				
				// отрисовка иконки
 				mc.renderEngine.bindTexture(new ResourceLocation("avttrue_informator:textures/weather_icon.png"));
 				drawTexturedModalRect(currentTime_xPos, currentTime_yPos + ICON_SIZE,
 						20 * weatherPhase, 0, 20, 20);
 			}
		}
		catch (Exception e) 
		{
			System.out.println(e.getMessage());
			e.printStackTrace();
			Informator.Gobal_ON = false;
			Functions.SendMessageToUser("\u00A7c" + TxtRes.GetLocalText("avttrue.informator.26", 
					"The mod Informator made a mistake and was off"), null);
		}
	}
	
///////////////////////////////////////////////////////////////////////////////////
// TODO Target Mob Bar
///////////////////////////////////////////////////////////////////////////////////
	public void CreateTargetMobBar()
	{
		try
		{	
			if (!view.ISee)
				return;
		 
			// позиция и размеры
			int TargetMobBar_Len = Informator.TargetMobBar_WidthScreenPercentage * scaledResolution.getScaledWidth() / 100;
			
			// custom
			int TargetMobBar_x = Informator.TargetMobBar_xPos;
			int TargetMobBar_y = Informator.TargetMobBar_yPos;
			
			// по центру
			if (Informator.TargetMobBar_alignMode.toLowerCase().contains("center"))
			{
				TargetMobBar_x = (scaledResolution.getScaledWidth() - TargetMobBar_Len) / 2;
				TargetMobBar_y = 0;
			}
			// слева
			else if (Informator.TargetMobBar_alignMode.toLowerCase().contains("topleft"))
			{
				TargetMobBar_x = 0;
				TargetMobBar_y = 0;
			}
			// справа
			else if (Informator.TargetMobBar_alignMode.toLowerCase().contains("topright"))
			{
				TargetMobBar_x = scaledResolution.getScaledWidth() - TargetMobBar_Len;
				TargetMobBar_y = 0;
			}
						
			// имя
			String mobname = view.MobName;
			int mobnamexpos = TargetMobBar_x + 1 + (TargetMobBar_Len - 2 - mc.fontRendererObj.getStringWidth(mobname)) / 2;
			
			// дистанция
			String mobdist = " " + TxtRes.GetLocalText("avttrue.informator.16", "Distance") + ": " + view.DistToPlayer + " ";
			int mobdistlen = mc.fontRendererObj.getStringWidth(mobdist);
			
			// доп. характеристики для коней
			String mobowner = "";
			String mobspeed = "";
			String mobjamp = "";
			int mobaddsetts1len = 0;
			int mobaddsetts2len = 0;
			int mobaddsetts3len = 0;
			if (view.MobOwner != null)
			{
				mobowner = " " + TxtRes.GetLocalText("avttrue.informator.31", "Owner") + ": " + view.MobOwner + " ";
				mobaddsetts1len = mc.fontRendererObj.getStringWidth(mobowner);
			}
			if (view.MobMovementSpeed > 0)
			{
				mobspeed += " " + TxtRes.GetLocalText("avttrue.informator.32", "S.") + ": " + view.MobMovementSpeed;
				mobaddsetts2len = mc.fontRendererObj.getStringWidth(mobspeed);
			}
			if (view.MobJumpHeight > 0)
			{
				mobjamp += " " + TxtRes.GetLocalText("avttrue.informator.33", "J.") + ": " + view.MobJumpHeight;
				mobaddsetts3len = mc.fontRendererObj.getStringWidth(mobjamp);
			}
						
			// отрисовка панелей
			// основная панель
			drawRect(TargetMobBar_x, TargetMobBar_y, TargetMobBar_x + TargetMobBar_Len,
					TargetMobBar_y + mc.fontRendererObj.FONT_HEIGHT * 2 + 3, PANEL_GRAY);
			
			// имя панель
			drawGradientRect(TargetMobBar_x + 1, TargetMobBar_y + 1, TargetMobBar_x + TargetMobBar_Len - 1,
					TargetMobBar_y + mc.fontRendererObj.FONT_HEIGHT + 1, PANEL_STEEL, PANEL_TRANSPARENT);
			
			// имя текст
			mc.fontRendererObj.drawStringWithShadow(mobname, mobnamexpos, TargetMobBar_y + 1, FONT_WHITE);
			
			// здоровье панель красная
			if (view.MobMaxHealth > view.MobHealth) // типа экономим
			{
				Color cr = new Color(0xA5072C);
				drawRect(TargetMobBar_x + 18, TargetMobBar_y + 2 + mc.fontRendererObj.FONT_HEIGHT, 
						TargetMobBar_x + TargetMobBar_Len - 1,
						TargetMobBar_y + mc.fontRendererObj.FONT_HEIGHT * 2 + 2, cr.getRGB());
			}
			
			// панели здоровья
			String mobhealth = TxtRes.GetLocalText("avttrue.informator.19", "Health") + " " +
					view.MobHealth + " / " + view.MobMaxHealth + " | " +
					TxtRes.GetLocalText("avttrue.informator.20", "Armor") + " " +
					view.MobTotalArmor;
			int mobhealslinelen = TargetMobBar_Len - 18;
			int mobhealthXtxtpos = TargetMobBar_x + 18 + (mobhealslinelen - mc.fontRendererObj.getStringWidth(mobhealth)) / 2;
			if (view.MobHealth <= 0) // типа экономим
				mobhealslinelen = 1;
			else if (view.MobMaxHealth > view.MobHealth) // типа экономим
				mobhealslinelen = (int)(Math.round(((float)view.MobHealth/(float)view.MobMaxHealth) * mobhealslinelen));
			// здоровье панель зелёная
			Color cg = new Color(0x1AB615);
			drawRect(TargetMobBar_x + 18, TargetMobBar_y + 2 + mc.fontRendererObj.FONT_HEIGHT, 
					TargetMobBar_x + mobhealslinelen + 17,
					TargetMobBar_y + mc.fontRendererObj.FONT_HEIGHT * 2 + 2, cg.getRGB());
			
			// здоровье панель серая 
			drawGradientRect(TargetMobBar_x + 18, TargetMobBar_y + 2 + mc.fontRendererObj.FONT_HEIGHT, 
								TargetMobBar_x + TargetMobBar_Len,
								TargetMobBar_y + mc.fontRendererObj.FONT_HEIGHT * 2 + 2, PANEL_TRANSPARENT, PANEL_GRAY);
			
			// здоровье и броня текст
			mc.fontRendererObj.drawStringWithShadow(mobhealth, mobhealthXtxtpos, 
													TargetMobBar_y + 2 + mc.fontRendererObj.FONT_HEIGHT, FONT_WHITE);
			
			// портретная панель светлая
			drawRect(TargetMobBar_x, TargetMobBar_y + 2 + mc.fontRendererObj.FONT_HEIGHT, 
					TargetMobBar_x + 18, TargetMobBar_y + 20 + mc.fontRendererObj.FONT_HEIGHT, 
					PANEL_GRAY);
			
			// рисуем портреты
			if(view.elb != null && 
					Informator.TargetMobBar_DrawMobPortrait)
			{
				int scl = (int) (20 / Math.max(view.elb.height, view.elb.width));
				Drawing.drawEntityOnScreen(TargetMobBar_x + 9, 
										TargetMobBar_y + 18 + mc.fontRendererObj.FONT_HEIGHT, 
										scl, 0, 0, view.elb);
			}
			else
			{
				mc.renderEngine.bindTexture(new ResourceLocation("avttrue_informator:textures/icons.png"));
				drawTexturedModalRect(TargetMobBar_x + 1, TargetMobBar_y + 3 + mc.fontRendererObj.FONT_HEIGHT, 
										16, 0, ICON_SIZE, ICON_SIZE);
			}
			
			// дистанция панель
			int dist_y = TargetMobBar_y + mc.fontRendererObj.FONT_HEIGHT * 2 + 3;
			int dist_x =TargetMobBar_x + 18;
			drawGradientRect(dist_x, dist_y, dist_x + mobdistlen, dist_y + mc.fontRendererObj.FONT_HEIGHT, 
							PANEL_STEEL, PANEL_TRANSPARENT);
									
			// дистанция текст
			mc.fontRendererObj.drawStringWithShadow(mobdist, dist_x, dist_y, FONT_WHITE);			
			
			// дополнительные характеристики / коня, собачек и кошечек
			if(!mobowner.isEmpty() || !mobspeed.isEmpty() || !mobjamp.isEmpty())
			{
			// панель
				dist_y = TargetMobBar_y + mc.fontRendererObj.FONT_HEIGHT * 3 + 3;
				drawGradientRect(dist_x, dist_y, 
								dist_x + mobaddsetts1len + mobaddsetts2len + mobaddsetts3len + 3, 
								dist_y + mc.fontRendererObj.FONT_HEIGHT, 
								PANEL_STEEL, PANEL_TRANSPARENT);
			// текст
				mc.fontRendererObj.drawStringWithShadow(mobowner, dist_x, dist_y, FONT_WHITE);
				dist_x += mobaddsetts1len;
				if(view.MobMovementSpeed >= 13.0D)
					mc.fontRendererObj.drawStringWithShadow(mobspeed, dist_x, dist_y, FONT_AQUA); 
				else if(view.MobMovementSpeed >= 11.0D)
					mc.fontRendererObj.drawStringWithShadow(mobspeed, dist_x, dist_y, FONT_GREEN);
				else if(view.MobMovementSpeed >= 8.0D)
					mc.fontRendererObj.drawStringWithShadow(mobspeed, dist_x, dist_y, FONT_WHITE);
				else
					mc.fontRendererObj.drawStringWithShadow(mobspeed, dist_x, dist_y, FONT_RED);
				dist_x += mobaddsetts2len;
				if(view.MobJumpHeight >= 5.0D)
					mc.fontRendererObj.drawStringWithShadow(mobjamp, dist_x, dist_y, FONT_AQUA);
				else if(view.MobJumpHeight >= 4.0D)
					mc.fontRendererObj.drawStringWithShadow(mobjamp, dist_x, dist_y, FONT_GREEN);
				else if(view.MobJumpHeight >= 2.75D)
					mc.fontRendererObj.drawStringWithShadow(mobjamp, dist_x, dist_y, FONT_WHITE); 
				else
					mc.fontRendererObj.drawStringWithShadow(mobjamp, dist_x, dist_y, FONT_RED);
			}	
						
		}
		catch (Exception e) 
		{
			System.out.println(e.getMessage());
			e.printStackTrace();
			Informator.Gobal_ON = false;
			Functions.SendMessageToUser("\u00A7c" + TxtRes.GetLocalText("avttrue.informator.26", 
					"The mod Informator made a mistake and was off"), null);
		}
	}
	
////////////////////////////////////////////////////////
// TODO DrawThesaurusButton
////////////////////////////////////////////////////////
	public void DrawThesaurusButton()
	{
		if(Informator.ShowThesaurusButton && 
				mc.currentScreen instanceof GuiChat)
		{
			mc.renderEngine.bindTexture(new ResourceLocation("avttrue_informator:textures/icons.png"));
			drawTexturedModalRect(0, scaledResolution.getScaledHeight() - 35,
									0, 28, 20, 20);
		}
	}
}

