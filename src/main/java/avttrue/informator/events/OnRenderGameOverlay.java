package avttrue.informator.events;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.platform.GlStateManager;

import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.config.GuiUtils;

import avttrue.informator.Informator;
import avttrue.informator.data.CollectedClockData;
import avttrue.informator.data.CollectedHeldItemsData;
import avttrue.informator.data.CollectedHeldItemsData.HeldItem;
import avttrue.informator.data.CollectedVelocityData;
import avttrue.informator.data.CollectedWeatherData;
import avttrue.informator.data.TimeOfDay;
import avttrue.informator.tools.Drawing;

public class OnRenderGameOverlay //extends Gui
{
    private Minecraft mc = Minecraft.getInstance();
    //private ScaledResolution scaledResolution = null;
    
    private static final int PANEL_TRANSPARENT = 0;
    private static final int PANEL_STEEL = -9408400;
    private static final int PANEL_GRAY = Color.lightGray.getRGB();
    
    private static final int FONT_WHITE = 0xffffff;
    private static final int FONT_GRAY = 0xBFBFBE;
    private static final int FONT_GREEN = 0x00FF00;
    private static final int FONT_BLUE = 0x0040FF;
    private static final int FONT_AQUA = 0x0080FF;
    private static final int FONT_RED = 0xFF0000;
    
    private static int STRING_HEIGHT = 9;
    private static final int STRING_PREFIX_px = 4; // префикс (в пикселях) перед любой надписью на пенели
    private static final int STRING_POSTFIX_px = 4; // постфикс (в пикселях) после любой надписи на панелях
    private static final int STRING_GROW_px = STRING_PREFIX_px + STRING_POSTFIX_px;
    
    private static final int BUFF_ICON_SPACING = 19; //??? 20;
    private static final int BUFF_ICON_BASE_U_OFFSET = 0;
    private static final int BUFF_ICON_BASE_V_OFFSET = 198;
    private static final int BUFF_ICONS_PER_ROW = 8;
    private static final String mobsetts = null;
    
    private int mainWndScaledWidth;
    private int mainWndScaledHeight;

    @SubscribeEvent
    public void onRenderInformatorBars(RenderGameOverlayEvent event) 
    {
        // выключили по горячей клавише
        if (!Informator.Global_ON) return;
        // показывает в отрисовке хотбара
        if (event.getType() != ElementType.HOTBAR) return;
        // если в дебаг-режиме и показ выключен для этого режима
        if (mc.gameSettings.showDebugInfo && Informator.Global_HideInDebugMode) return;

        STRING_HEIGHT = mc.fontRenderer.FONT_HEIGHT;
        mainWndScaledWidth = mc.mainWindow.getScaledWidth();
        mainWndScaledHeight = mc.mainWindow.getScaledHeight();

        // === ИНФОРМАЦИОННЫЕ ПАНЕЛИ НА ЭКРАНЕ ===
        // Clock bar
        if (Informator.TimeBar_Show) CreateClockBar();
        // Velocity Bar
        if(Informator.VelocityBar_Show) CreateVelocityBar();
        // Held item bar
        if (Informator.HeldItemDetails_Show) CreateHeldItemBar();
        // Current Enchantments
        if (Informator.EnchantBar_Show) CreateEnchantBar();

        // == НАДПИСИ В НАПРАВЛЕНИИ ВЗГЛЯДА ===
//view = new View();
        // Block bar
//if(Informator.InfoBlockBar_Show) CreateBlockBar();
        // Target Mob
//if (Informator.TargetMobBar_Show) CreateTargetMobBar();
        
        // Thesaurus
//DrawThesaurusButton();
    }

    private void CreateHeldItemBar()
    {
        try
        {
            final CollectedHeldItemsData.Data held_items = Informator.held_items.data;
            if (!held_items.valid) return;
            if (held_items.held_damageable.isEmpty()) return;

            // настройки позиционирования
            //int xPos = Informator.HeldItemDetails_xOffset;
            //int yPos = Informator.HeldItemDetails_yOffset;
            final int xPos = 0;
            int yPos = Skin.MC_ICON_SIZE /*time*/ + Skin.ICON_WEATHER_PRETTY.size /*погода*/ + Skin.MC_ICON_SIZE /*скорость*/;

            // текст и иконки
            for (HeldItem hitm : held_items.held_damageable)
            {
                // отрисовка панели
                if (Informator.Global_ShowPanel) 
                {
                    final boolean critical = hitm.damageFactor < Informator.HeldItemDetails_DamageAlarm;
                    final boolean warning = (hitm.damageFactor < 0.5) ? (hitm.damageFactor < (1.5*Informator.HeldItemDetails_DamageAlarm)) : false;
                    final int color_panel = critical ? Color.red.getRGB() : (warning ? Color.yellow.getRGB() : PANEL_STEEL);
                    final int desc_len = mc.fontRenderer.getStringWidth(hitm.damageDesc) + STRING_GROW_px + Skin.MC_ICON_SIZE;
                    GuiUtils.drawGradientRect(0,
                            xPos,
                            yPos,
                            xPos + desc_len,
                            yPos + Skin.MC_ICON_SIZE,
                            color_panel,
                            PANEL_TRANSPARENT);
                }
                // отрисовка иконки
                final Item item = Item.getItemById(hitm.id);
                Drawing.DrawItemStack(mc.getItemRenderer(), new ItemStack(item), xPos, yPos);
                // отрисовка текста
                mc.fontRenderer.drawStringWithShadow(
                        hitm.damageDesc,
                        xPos + Skin.MC_ICON_SIZE + STRING_PREFIX_px,
                        yPos + (Skin.MC_ICON_SIZE-STRING_HEIGHT)/2+1,
                        hitm.rarity.color.getColor());
                // смещаемся к следующей панели
                yPos += Skin.MC_ICON_SIZE;
            }
        }
        catch (Exception e) 
        {
            Informator.Global_ON = false;
            System.out.println(e.getMessage());
            e.printStackTrace();
            Informator.TOOLS.SendFatalErrorToUser(Informator.TRANSLATOR.field_fatal_error);
        }
    }

    private void CreateVelocityBar()
    {
        try
        {
            final CollectedVelocityData.Data velocity = Informator.velocity.data;
            if (!velocity.valid) return;

            final int VelocityBar_xPos = Informator.VelocityBar_xOffset;
            final int VelocityBar_yPos = Informator.VelocityBar_yOffset +  Skin.MC_ICON_SIZE /*time*/ + Skin.ICON_WEATHER_PRETTY.size /*погода*/;

            // отрисовка панели
            if (Informator.Global_ShowPanel) 
            {
                final int iVelocityLen = mc.fontRenderer.getStringWidth(velocity.sVelocity) + STRING_GROW_px;
                GuiUtils.drawGradientRect(0,
                        VelocityBar_xPos,
                        VelocityBar_yPos,
                        VelocityBar_xPos + 16 + iVelocityLen,
                        VelocityBar_yPos + Skin.MC_ICON_SIZE,
                        PANEL_STEEL,
                        PANEL_TRANSPARENT);
            }
            // отрисовка текста
            mc.fontRenderer.drawStringWithShadow(
                    velocity.sVelocity,
                    VelocityBar_xPos + Skin.MC_ICON_SIZE + STRING_PREFIX_px,
                    VelocityBar_yPos + (Skin.MC_ICON_SIZE-STRING_HEIGHT)/2,
                    FONT_WHITE);
            // отрисовка иконки
             Drawing.DrawItemStack(mc.getItemRenderer(), new ItemStack(Items.COMPASS), VelocityBar_xPos, VelocityBar_yPos);
        }
        catch (Exception e) 
        {
            Informator.Global_ON = false;
            System.out.println(e.getMessage());
            e.printStackTrace();
            Informator.TOOLS.SendFatalErrorToUser(Informator.TRANSLATOR.field_fatal_error);
        }
    }

    private void CreateClockBar()
    {
        try
        {
            final CollectedClockData.Data clock = Informator.clock.data;
            final CollectedWeatherData.Data weather = Informator.weather.data;
            if (!clock.valid || !weather.valid) return;

            final int currentTimeStrLen = mc.fontRenderer.getStringWidth(clock.currentTime) + STRING_GROW_px;
            final boolean showBedIcon = Informator.TimeBarBed_Show ? clock.restTimeHourOverhead : false;

            // учёт сдвига на размер иконки луны при прижатии книзу
            final boolean isWeatherBarPresent = Informator.TimeBarMoon_Show || Informator.TimeBarWeather_Show;
            final int weatherBarHeight = isWeatherBarPresent ? Skin.ICON_WEATHER_PRETTY.size : 0;

            // расчёт размещения панели
            int time_xPos = 0;
            int time_yPos = 0;
            switch (Informator.TimeBar_alignMode)
            {
            default:
            case 0: // topleft
                //подразумевается:time_xPos = 0;
                //подразумевается:time_yPos = 0;
                break;
            case 1: // topright
                time_xPos = mainWndScaledWidth - currentTimeStrLen + STRING_PREFIX_px - Skin.ICON_WEATHER_TIME.size;
                //подразумевается:time_yPos = 0;
                break;
            case 2: // bottomleft
                //подразумевается:time_xPos = 0;
                time_yPos = mainWndScaledHeight - Skin.ICON_WEATHER_TIME.size - weatherBarHeight;
                break;
            case 3: // bottomright
                time_xPos = mainWndScaledWidth - currentTimeStrLen + STRING_PREFIX_px - Skin.ICON_WEATHER_TIME.size;
                time_yPos = mainWndScaledHeight - Skin.ICON_WEATHER_TIME.size - weatherBarHeight;
                break;
            }
            time_xPos += Informator.TimeBar_xOffset;
            time_yPos += Informator.TimeBar_yOffset;

            int moon_xPos = time_xPos;
            int moon_yPos = time_yPos;
            int weather_xPos = moon_xPos;
            int weather_yPos = moon_yPos;
            int moonPhaseLen = 0; // длина текста фазы луны

            // 0 дождь, 1 солнечно, 2 дождь с грозой
            final int weatherPhase = weather.isThundering ? 2 : (weather.isRaining ? 0 : 1);
            int weatherPhasePretty = weatherPhase * 2; // день: 0 дождь, 2 солнечно, 4 дождь с грозой
            if (clock.timeOfDay == TimeOfDay.NIGHT) weatherPhasePretty++; // ночь: 1 дождь, 3 солнечно, 5 дождь с грозой

            if (isWeatherBarPresent)
            {
                //
                // фазы луны
                //
                //14000 — Полнолуние.
                //38000 — Убывающая луна.
                //62000 — Последняя четверть.
                //86000 — Старая луна.
                //110000 — Новолуние.
                //134000 — Молодая луна.
                //158000 — Первая четверть.
                //182000 — Прибывающая луна.
                if (Informator.TimeBarMoon_Show)
                {
                    moonPhaseLen = STRING_GROW_px + Math.max(
                            mc.fontRenderer.getStringWidth(weather.sMoonPhase),
                            mc.fontRenderer.getStringWidth(weather.sMoonPhaseFactor));
                    // позиция и размеры
                    switch (Informator.TimeBar_alignMode)
                    {
                    default:
                    case 0: // topleft
                        moon_xPos = Informator.TimeBarWeather_Show ? Skin.ICON_WEATHER_PRETTY.size : 0;
                        moon_yPos = 0;
                        break;
                    case 1: // topright
                        moon_xPos = mainWndScaledWidth - moonPhaseLen - (1+Skin.ICON_MOON.size+1);
                        moon_yPos = 0;
                        break;
                    case 2: // bottomleft
                        moon_xPos = Informator.TimeBarWeather_Show ? Skin.ICON_WEATHER_PRETTY.size : 0;
                        break;
                    case 3: // bottomright
                        moon_xPos = mainWndScaledWidth - moonPhaseLen - (1+Skin.ICON_MOON.size+1);
                        break;
                    }
                }

                //
                // погода
                //
                weather_xPos = moon_xPos;
                weather_yPos = moon_yPos;
                if (Informator.TimeBarWeather_Show)
                {
                    // позиция и размеры
                    switch (Informator.TimeBar_alignMode)
                    {
                    default:
                    case 0: // topleft
                        weather_xPos = 0;
                        weather_yPos = 0;
                        break;
                    case 1: // topright
                        weather_xPos = mainWndScaledWidth - moonPhaseLen - (Informator.TimeBarMoon_Show ? 1 : 0) * (1+Skin.ICON_MOON.size+1) - Skin.ICON_WEATHER_PRETTY.size;
                        weather_yPos = 0;
                        break;
                    case 2: // bottomleft
                        weather_xPos = 0;
                        break;
                    case 3: // bottomright
                        weather_xPos = mainWndScaledWidth - moonPhaseLen - (Informator.TimeBarMoon_Show ? 1 : 0) * (1+Skin.ICON_MOON.size+1) - Skin.ICON_WEATHER_PRETTY.size;
                        break;
                    }
                }
            }

            // ВРЕМЯ: отрисовка панели
            if (Informator.Global_ShowPanel) 
            {
                GuiUtils.drawGradientRect(0,
                        time_xPos,
                        time_yPos, 
                        time_xPos + Skin.MC_ICON_SIZE + currentTimeStrLen + (showBedIcon ? Skin.MC_ICON_SIZE : 0),
                        time_yPos + Skin.MC_ICON_SIZE,
                        PANEL_STEEL,
                        PANEL_TRANSPARENT);
            }
            // ВРЕМЯ: отрисовка текста
            mc.fontRenderer.drawStringWithShadow(
                    clock.currentTime,
                    time_xPos + Skin.MC_ICON_SIZE + (showBedIcon ? Skin.MC_ICON_SIZE : 0) + STRING_PREFIX_px,
                    time_yPos + (Skin.MC_ICON_SIZE-STRING_HEIGHT)/2+1,
                    FONT_WHITE);
            // ЛУНА и ПОГОДА: (погода отдельно не отображается, либо ВМЕСТЕ с луной, либо ВМЕСТО иконки времени) 
            if (Informator.TimeBarMoon_Show)
            {
                // ЛУНА: отрисовка панели
                if (Informator.Global_ShowPanel) 
                {
                    GuiUtils.drawGradientRect(0,
                            moon_xPos,
                            moon_yPos + Skin.MC_ICON_SIZE,
                            moon_xPos + moonPhaseLen + 1+Skin.ICON_MOON.size+1,
                            moon_yPos + Skin.MC_ICON_SIZE + 2*STRING_HEIGHT,
                            PANEL_STEEL,
                            PANEL_TRANSPARENT);
                }
                // ЛУНА: отрисовка текста
                mc.fontRenderer.drawStringWithShadow(
                        weather.sMoonPhase,
                        moon_xPos + 1+Skin.ICON_MOON.size+1 + STRING_PREFIX_px,
                        moon_yPos + Skin.MC_ICON_SIZE,
                        FONT_WHITE);
                mc.fontRenderer.drawStringWithShadow(
                        weather.sMoonPhaseFactor,
                        moon_xPos + 1+Skin.ICON_MOON.size+1 + STRING_PREFIX_px,
                        moon_yPos + Skin.MC_ICON_SIZE + STRING_HEIGHT,
                        FONT_WHITE);

                // ПОГОДА:
                if (Informator.TimeBarWeather_Show)
                {
                    // ПОГОДА:отрисовка панели
                    if (Informator.Global_ShowPanel) 
                    {
                        GuiUtils.drawGradientRect(0,
                                weather_xPos,
                                weather_yPos + Skin.MC_ICON_SIZE,
                                weather_xPos + Skin.ICON_WEATHER_PRETTY.size,
                                weather_yPos + Skin.MC_ICON_SIZE + 2*STRING_HEIGHT,
                                PANEL_STEEL,
                                PANEL_TRANSPARENT);
                    }
                }
            }

            // ВРЕМЯ, ЛУНА, ПОГОДА (иконки): предварительная загрузка ресурсов (иконок, которые потом будем быстро наносить на экран)
            mc.getTextureManager().bindTexture(new ResourceLocation("avttrue_informator:textures/wthr.png"));
            if (Informator.TimeBarMoon_Show)
            {
                // ЛУНА: отрисовка иконки луны
                DrawSkinIcon(
                        moon_xPos+1,
                        moon_yPos + Skin.MC_ICON_SIZE + (2*STRING_HEIGHT-Skin.ICON_MOON.size)/2,
                        Skin.ICON_MOON,
                        weather.moonPhase);
                // ПОГОДА: (отображается на отдельной панели только СОВМЕСТНО с луной)
                if (Informator.TimeBarWeather_Show)
                {
                    // отрисовка иконки
                    if (!Informator.TimeBarWeatherPretty_Show)
                    {
                        DrawSkinIcon(
                                weather_xPos,
                                weather_yPos + Skin.MC_ICON_SIZE,
                                Skin.ICON_WEATHER_AVTTRUE,
                                weatherPhase);
                    }
                    else
                    {
                        DrawWeatherAndMoon(
                                weather_xPos,
                                weather_yPos + Skin.MC_ICON_SIZE,
                                false,
                                weatherPhasePretty,
                                weather.moonPhase);
                    }
                }
            }
            // ВРЕМЯ: отрисовка иконки часов
            if (Informator.TimeBarMoon_Show && Informator.TimeBarWeather_Show)
            {
                Drawing.DrawItemStack(mc.getItemRenderer(), new ItemStack(Items.CLOCK), time_xPos, time_yPos);
            }
            // ВРЕМЯ (иконка погоды на месте иконки часов):
            else
            {
                //не пользуемся этим методом: иконка слишком маленькая, и новолуние неотличимо от дождя днём DrawWeatherAndMoon(
                //        time_xPos,
                //        time_yPos,
                //        true,
                //        weatherPhasePretty,
                //        moonPhase);
                DrawSkinIcon(
                        time_xPos,
                        time_yPos,
                        Skin.ICON_WEATHER_TIME,
                        weatherPhasePretty);
            }
            // КРОВАТЬ: отрисовка иконки кровати
            if (showBedIcon)
            {
                Drawing.DrawItemStack(mc.getItemRenderer(), new ItemStack(Items.BLUE_BED), time_xPos + Skin.MC_ICON_SIZE, time_yPos);
            }
        }
        catch (Exception e) 
        {
            Informator.Global_ON = false;
            System.out.println(e.getMessage());
            e.printStackTrace();
            Informator.TOOLS.SendFatalErrorToUser(Informator.TRANSLATOR.field_fatal_error);
        }
    }

    public void CreateEnchantBar()
    {
        try
        {
            ArrayList<String> EnchantmentsList = null; // список зачарований на предмете
            List<ItemStack> istacks = new ArrayList<ItemStack>(); // список предметов
                    
            if (Informator.EnchantBar_ShowHands) 
            {
                istacks.add(mc.player.getHeldItemMainhand()); //добавляем удерживаемый предмет в основной руке
                istacks.add(mc.player.getHeldItemOffhand()); //добавляем удерживаемый предмет во второй руке
            }
            
            if (Informator.EnchantBar_ShowBody)
            {
                for (int i = 0; i < 4; i++) 
                    istacks.add(mc.player.inventory.armorInventory.get(i)); // надетые предметы
            }
            
            int deltaY = Informator.EnchantBar_yOffset; // смещение по высоте
            final int xPos = Informator.EnchantBar_xOffset + mainWndScaledWidth; // правая граница панелей
            for (ItemStack istack : istacks)
            {
                EnchantmentsList = getItemEnchants(istack);
                if (EnchantmentsList == null) continue;
                
                final int enchantmentsCount = EnchantmentsList.size();
                if (enchantmentsCount == 0) continue;
                
                // расчёт размера панели
                int textMaxLen = 0;
                for (final String text : EnchantmentsList)
                {
                    final int textLen = mc.fontRenderer.getStringWidth(text) + STRING_GROW_px;
                    if (textLen > textMaxLen) textMaxLen = textLen;
                }

                // отрисовка панели
                final int panelHeight = (enchantmentsCount == 1) ? Skin.MC_ICON_SIZE : (STRING_HEIGHT * enchantmentsCount + 1);
                if (Informator.Global_ShowPanel) 
                {
                    GuiUtils.drawGradientRect(0,
                            xPos - Skin.MC_ICON_SIZE - textMaxLen,
                            deltaY,
                            xPos,
                            deltaY + panelHeight,
                            PANEL_STEEL,
                            PANEL_TRANSPARENT);
                }   
                // отрисовка иконки
                Drawing.DrawItemStack(mc.getItemRenderer(), istack, xPos - Skin.MC_ICON_SIZE - textMaxLen, deltaY);

                final int prevDeltaY = deltaY;
                for (final String text : EnchantmentsList)
                {
                    // отрисовка текста
                    mc.fontRenderer.drawStringWithShadow(
                            text,
                            xPos - textMaxLen + STRING_PREFIX_px,
                            deltaY + ((enchantmentsCount == 1) ? ((Skin.MC_ICON_SIZE-STRING_HEIGHT)/2+1) : 0),
                            FONT_WHITE);
                    deltaY += STRING_HEIGHT;
                }
                deltaY = prevDeltaY + panelHeight;
            }
        }
        catch (Exception e) 
        {
            Informator.Global_ON = false;
            System.out.println(e.getMessage());
            e.printStackTrace();
            Informator.TOOLS.SendFatalErrorToUser(Informator.TRANSLATOR.field_fatal_error);
        }
    }
    
    private static ArrayList<String> getItemEnchants(ItemStack stack) 
    {
        ArrayList<String> list = new ArrayList<String>();
        try 
        {
            if (stack == null) return null;
            if (!stack.isEnchanted()) return null;
            if (!stack.hasTag()) return null;
            ListNBT enchants = stack.getEnchantmentTagList();
            if (enchants == null) return null;
            if (enchants.isEmpty()) return null;
            for (int i = 0; i < enchants.size(); i++) 
            {
                CompoundNBT enchant = enchants.getCompound(i);
                Registry.ENCHANTMENT.getValue(ResourceLocation.tryCreate(enchant.getString("id"))).ifPresent(
                        (itxtcmp) -> { list.add(itxtcmp.getDisplayName(enchant.getInt("lvl")).getString()); }
                );
            }
            return list;
        }
        catch (Exception e) 
        {
            System.out.println(e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /*private void CreateBlockBar()
    {
        try
        {
            // удалось определить блок, на который смотрим
            if (view.targetBlock == null) return;
            
            // если мы смотрим не в воздух
            if (view.tBlock.isAir(view.tBlockState, mc.theWorld, view.tBlockPosition)) return;
            
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
                BlockLight = " " + TextTranslation.GetLocalText("avttrue.informator.2", "Light") +
                        "=" + EnumSkyBlock.BLOCK.defaultLightValue + " / " + 
                        EnumSkyBlock.SKY.defaultLightValue + " (" + 
                        TextTranslation.GetLocalText("avttrue.informator.14", "sky") + ") ";
            }
            else
            {
                Chunk c = mc.theWorld.getChunkFromBlockCoords(upBlockPos);
                BlockLight = " " + TextTranslation.GetLocalText("avttrue.informator.2", "Light") +
                        "=" + c.getLightFor(EnumSkyBlock.BLOCK, upBlockPos) + " / " + 
                        c.getLightFor(EnumSkyBlock.SKY, upBlockPos) + " (" + 
                        TextTranslation.GetLocalText("avttrue.informator.14", "sky") + ") ";    
            }

            // заряд блока
            String BlockPower = " " + TextTranslation.GetLocalText("avttrue.informator.3", "Power") +
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
            Informator.TOOLS.SendFatalErrorToUser(Informator.TRANSLATOR.field_fatal_error);
        }
    }*/

    /*public void CreateTargetMobBar()
    {
        try
        {    
            if (!view.ISee) return;
         
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
            String mobdist = " " + TextTranslation.GetLocalText("avttrue.informator.16", "Distance") + ": " + view.DistToPlayer + " ";
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
                mobowner = " " + TextTranslation.GetLocalText("avttrue.informator.31", "Owner") + ": " + view.MobOwner + " ";
                mobaddsetts1len = mc.fontRendererObj.getStringWidth(mobowner);
            }
            if (view.MobMovementSpeed > 0)
            {
                mobspeed += " " + TextTranslation.GetLocalText("avttrue.informator.32", "S.") + ": " + view.MobMovementSpeed;
                mobaddsetts2len = mc.fontRendererObj.getStringWidth(mobspeed);
            }
            if (view.MobJumpHeight > 0)
            {
                mobjamp += " " + TextTranslation.GetLocalText("avttrue.informator.33", "J.") + ": " + view.MobJumpHeight;
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
            String mobhealth = TextTranslation.GetLocalText("avttrue.informator.19", "Health") + " " +
                    view.MobHealth + " / " + view.MobMaxHealth + " | " +
                    TextTranslation.GetLocalText("avttrue.informator.20", "Armor") + " " +
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
            Informator.TOOLS.SendFatalErrorToUser(Informator.TRANSLATOR.field_fatal_error);
        }
    }*/


    private void DrawSkinIcon(int x, int y, Skin skin, int idx)
    {
        if (skin.blend)
        {
            GlStateManager.pushMatrix();
            GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(org.lwjgl.opengl.GL11.GL_SRC_ALPHA, org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA);
        }
        GuiUtils.drawTexturedModalRect(
                x,
                y,
                skin.x + (skin.horizontal ? (skin.size * idx) : 0),
                skin.y + (skin.horizontal ? 0 : (skin.size * idx)),
                skin.size,
                skin.size,
                0);
        if (skin.blend)
        {
            GlStateManager.disableBlend();
            GlStateManager.popMatrix();
        }
    }

    
    private void DrawWeatherAndMoon(int x, int y, boolean for_time, int weather, int moon)
    {
        // используем стандартные иконки, так быстрее, нежели смешивать два слоя
        if (!Informator.TimeBarWeather_WithMoonPhases || // ИЛИ режим выключен
            (moon == 0) || // ИЛИ полнолуние
            ((weather % 2) == 0)) // ИЛИ сейчас день и луну/месяц не видно
        {
            Skin weather_skin = for_time ? Skin.ICON_WEATHER_TIME : Skin.ICON_WEATHER_PRETTY;
            DrawSkinIcon(x, y, weather_skin, weather);
        }
        else
        {
            // получаем ссылки на шкурки (либо 16x16, либо 19x19)
            Skin moon_skin = for_time ? Skin.ICON_WEATHER_TIME_PHASES : Skin.ICON_MOON_PHASES;
            Skin cloud_skin = for_time ? Skin.ICON_WEATHER_TIME_CLOUD : Skin.ICON_WEATHER_PRETTY_CLOUD;
            // пересчитываем индекс фаз лун, т.к. при горизонтальном размещении они идут так : 4, 5, 6, 7, 0, 1, 2, 3
            moon = (moon + 4) % 8;
            // пересчитываем индекс иконки погоды, т.к. облака имеются только для weather=1 и weather=5
            weather = (weather == 1) ? 0 : ((weather == 5) ? 1 : -1);
            // рисуем одну иконку поверх другой
            GlStateManager.pushMatrix();
            GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(org.lwjgl.opengl.GL11.GL_SRC_ALPHA, org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA);
            GuiUtils.drawTexturedModalRect(
                    x,
                    y,
                    moon_skin.x + (moon_skin.horizontal ? (moon_skin.size * moon) : 0),
                    moon_skin.y + (moon_skin.horizontal ? 0 : (moon_skin.size * moon)),
                    moon_skin.size,
                    moon_skin.size,
                    0);
            if (weather != -1)
                GuiUtils.drawTexturedModalRect(
                        x,
                        y,
                        cloud_skin.x + (cloud_skin.horizontal ? (cloud_skin.size * weather) : 0),
                        cloud_skin.y + (cloud_skin.horizontal ? 0 : (cloud_skin.size * weather)),
                        cloud_skin.size,
                        cloud_skin.size,
                        0);
            GlStateManager.disableBlend();
            GlStateManager.popMatrix();
        }
    }


    private static class Skin
    {
        public static final int WEATHER_ICON_SIZE = 19;
        private static final int MC_ICON_SIZE = 16;
        public static final Skin ICON_WEATHER_PRETTY = new Skin(0, 0, WEATHER_ICON_SIZE, true, true); // 6шт  горизонтально (19x19)
        public static final Skin ICON_WEATHER_PRETTY_CLOUD = new Skin(114, 0, WEATHER_ICON_SIZE, true, true); // 2шт горизонтально (19x19)
        public static final Skin ICON_WEATHER_AVTTRUE = new Skin(0, 19, WEATHER_ICON_SIZE, true, false); // 3шт горизонтально (19x19)
        public static final Skin ICON_WEATHER_TIME = new Skin(57, 19, MC_ICON_SIZE, true, true); // 6шт горизонтально (16x16)
        public static final Skin ICON_WEATHER_TIME_CLOUD = new Skin(153, 19, MC_ICON_SIZE, true, true); // 2шт горизонтально (16x16)
        public static final Skin ICON_MOON = new Skin(0, 38, 14, true, false); // 8шт горизонтально (14x14)
        public static final Skin ICON_MOON_PHASES = new Skin(0, 52, WEATHER_ICON_SIZE, false, true); // 8шт вертикально (19x19)
        public static final Skin ICON_WEATHER_TIME_PHASES = new Skin(19, 52, MC_ICON_SIZE, false, true); // 8шт вертикально (16x16)
        public final int x;
        public final int y;
        public final int size;
        public final boolean horizontal;
        public final boolean blend; // признак того, что иконки требуют прозрачность при отрисовке (не пиксельные)
    
        public Skin(int x, int y, int size, boolean horizontal, boolean blend)
        {
            this.x = x;
            this.y = y;
            this.size = size;
            this.horizontal = horizontal;
            this.blend = blend;
        }
    }
}

