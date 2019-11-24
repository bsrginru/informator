package avttrue.informator.events;

import java.awt.Color;

import com.mojang.blaze3d.platform.GlStateManager;

import avttrue.informator.Informator;
import avttrue.informator.config.ModSettings;
import avttrue.informator.data.CollectedBlockData;
import avttrue.informator.data.CollectedClockData;
import avttrue.informator.data.CollectedEnchantmentsData;
import avttrue.informator.data.CollectedEntityData;
import avttrue.informator.data.CollectedHeldItemsData;
import avttrue.informator.data.CollectedHeldItemsData.HeldItem;
import avttrue.informator.data.CollectedVelocityData;
import avttrue.informator.data.CollectedWeatherData;
import avttrue.informator.data.TimeOfDay;
import avttrue.informator.tools.Drawing;
import avttrue.informator.tools.TextTranslation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.LightType;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ForgeI18n;
import net.minecraftforge.fml.client.config.GuiUtils;

public class OnRenderGameOverlay //extends Gui
{
    private Minecraft mc = Minecraft.getInstance();

    private static final int PANEL_TRANSPARENT = 0;
    private static final int PANEL_STEEL = (int)0xFF707070; // стальной
    private static final int PANEL_STEEL_TRANSPARENT = (int)0xA0404040; // стальной, немного прозрачный (для нанесения поверх PANEL_GRAY_TRANSPARENT)
    private static final int PANEL_GRAY_TRANSPARENT = Color.lightGray.getRGB() + 0x60000000; // светло-серый, почти прозрачный
    private static final int PANEL_MAROON = (int)0xFFA5072C; // густой красный
    private static final int PANEL_GREEN = (int)0xFF1AB615; // травянисто-зелёный

    private static final int FONT_WHITE = 0xffffff;
    private static final int FONT_GRAY = 0xBFBFBE;
    private static final int FONT_GREEN = 0x00FF00;
    private static final int FONT_BLUE = 0x0040FF;
    private static final int FONT_AQUA = 0x0080FF;
    private static final int FONT_RED = 0xFF0000;
    
    private static int STRING_HEIGHT = 9;
    private static final int STRING_PREFIX_px = 2; // префикс (в пикселях) перед любой надписью на пенели
    private static final int STRING_POSTFIX_px = 2; // постфикс (в пикселях) после любой надписи на панелях
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
        // проверяем версию мода (однократно, с задержкой)
        if (!Informator.TOOLS.versionChecked) Informator.TOOLS.checkVersion();
        // выключили по горячей клавише
        if (!ModSettings.GENERAL.Global_ON.get()) return;
        // показывает в отрисовке хотбара
        if (event.getType() != ElementType.HOTBAR) return;
        // если в дебаг-режиме и показ выключен для этого режима
        if (mc.gameSettings.showDebugInfo && ModSettings.GENERAL.Global_HideInDebugMode.get()) return;

        try
        {
            STRING_HEIGHT = mc.fontRenderer.FONT_HEIGHT;
            mainWndScaledWidth = mc.mainWindow.getScaledWidth();
            mainWndScaledHeight = mc.mainWindow.getScaledHeight();

            // === ИНФОРМАЦИОННЫЕ ПАНЕЛИ НА ЭКРАНЕ ===
            // Clock bar
            if (ModSettings.TIME.TimeBar_Show.get()) drawClockBar();
            // Velocity Bar
            if (ModSettings.VELOCITY.VelocityBar_Show.get()) drawVelocityBar();
            // Held item bar
            if (ModSettings.HELD.HeldItemDetails_Show.get()) drawHeldItemBar();
            // Current Enchantments
            if (ModSettings.ENCHANTS.EnchantBar_Show.get()) drawEnchantBar();

            // == НАДПИСИ В НАПРАВЛЕНИИ ВЗГЛЯДА ===
            // Block bar
            if (ModSettings.BLOCK.BlockBar_Show.get())
            {
                Informator.block.refresh(ModSettings.BLOCK.BlockBar_ShowElectricity.get());
                if (Informator.block.data.valid) drawBlockBar();
            }
            // Target Mob
            if (ModSettings.TARGET.TargetMobBar_Show.get())
            {
                Informator.entity.refresh();
                if (Informator.entity.data.valid && (Informator.entity.data.entity != null)) drawTargetBar();
            }

            // Thesaurus
            //DrawThesaurusButton();

            /***drawDebugBar();/***/
        }
        catch (Exception e)
        {
            ModSettings.GENERAL.Global_ON.set(false);
            System.out.println(e.getMessage());
            e.printStackTrace();
            Informator.TOOLS.SendFatalErrorToUser(Informator.TRANSLATOR.field_fatal_error);
        }
    }

    private void drawHeldItemBar()
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
            final float alarm_level = (float)ModSettings.HELD.HeldItemDetails_DamageAlarm.get() / 100.0F;
            final float warning_level = (float)ModSettings.HELD.HeldItemDetails_DamageWarning.get() / 100.0F;
            final boolean critical = hitm.damageFactor < alarm_level;
            final boolean warning = (hitm.damageFactor < 0.5) ? (hitm.damageFactor < warning_level) : false;
            // отрисовка панели
            if (ModSettings.GENERAL.Global_ShowPanel.get()) 
            {
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
            // отрисовка текста (на панели цвет текста всегда соответствует цвету раритетности вещи)
            if (ModSettings.GENERAL.Global_ShowPanel.get()) 
            {
                mc.fontRenderer.drawStringWithShadow(
                        hitm.damageDesc,
                        xPos + Skin.MC_ICON_SIZE + STRING_PREFIX_px,
                        yPos + (Skin.MC_ICON_SIZE-STRING_HEIGHT)/2+1,
                        hitm.rarity.color.getColor());
            }
            // отрисовка текста (если панели отключены, то цвет текста учитывает предупреждения об износе брони и оружия)
            else
            {
                final int color_text = critical ? Color.red.getRGB() : (warning ? Color.yellow.getRGB() : hitm.rarity.color.getColor());
                mc.fontRenderer.drawStringWithShadow(
                        hitm.damageDesc,
                        xPos + Skin.MC_ICON_SIZE + STRING_PREFIX_px,
                        yPos + (Skin.MC_ICON_SIZE-STRING_HEIGHT)/2+1,
                        color_text);
            }
            // смещаемся к следующей панели
            yPos += Skin.MC_ICON_SIZE;
        }
    }

    private long startDrawMaxVelocity = 0;

    private void drawVelocityBar()
    {
        final CollectedVelocityData.Data velocity = Informator.velocity.data;
        if (!velocity.valid) return;

        final int VelocityBar_xPos = ModSettings.VELOCITY.VelocityBar_xOffset.get();
        final int VelocityBar_yPos = ModSettings.VELOCITY.VelocityBar_yOffset.get() +  Skin.MC_ICON_SIZE /*time*/ + Skin.ICON_WEATHER_PRETTY.size /*погода*/;

        // отрисовка панели
        if (ModSettings.GENERAL.Global_ShowPanel.get()) 
        {
            final int iVelocityLen = mc.fontRenderer.getStringWidth(velocity.sVelocity) + STRING_GROW_px;
            GuiUtils.drawGradientRect(0,
                    VelocityBar_xPos,
                    VelocityBar_yPos,
                    VelocityBar_xPos + Skin.MC_ICON_SIZE + iVelocityLen,
                    VelocityBar_yPos + Skin.MC_ICON_SIZE,
                    PANEL_STEEL,
                    PANEL_TRANSPARENT);
        }
        // отрисовка текста: максимальная скорость
        boolean showMaxVelocity = false;
        int color = FONT_RED;
        if (ModSettings.VELOCITY.VelocityBar_ShowMax.get() && velocity.isMotionless && velocity.knownVelocityPrevMax)
        {
            if (startDrawMaxVelocity == 0)
                startDrawMaxVelocity = Informator.realTimeTick;
            final long diff = Informator.realTimeTick - startDrawMaxVelocity;
            // дольше 5х секунд информацию о максимальной скорости не выводим
            final int GLOW_DURATION = 250; // 5сек
            final int FLICK_DURATION = 150; // 3сек
            showMaxVelocity = diff <= GLOW_DURATION;
            // добавляем эффект мерцания
            int glow;
            if (diff <= FLICK_DURATION)
            {
                // циклически 3сек: за 0.5сек цвет достигает значения с 0xff0000 до 0xff8080, и ещё 0.5сек возвращается к 0xff0000
                glow = (int)((float)diff * 5.1) % 0x100;
                if (glow >= 0x80) glow = 0x100 - glow;
            }
            
            else
            {
                // оставшиеся 2сек цвет спадает с 0xff0000 до 0xffffff
                glow = (int)((float)(diff-FLICK_DURATION) * 2.55) % 0x100;
            }
            color = 0xff0000 | glow << 8 | glow;
        }
        if (velocity.knownVelocityPrevMax == false)
            startDrawMaxVelocity = 0;
        if (showMaxVelocity)
        {
            final int xPos = VelocityBar_xPos + Skin.MC_ICON_SIZE + STRING_PREFIX_px;
            final int yPos = VelocityBar_yPos + (Skin.MC_ICON_SIZE-STRING_HEIGHT)/2;
            final int lenPrefix = mc.fontRenderer.getStringWidth(velocity.sVelocityPrefix);
            final int lenVelocityMax = mc.fontRenderer.getStringWidth(velocity.sVelocityPrevMax);
            mc.fontRenderer.drawStringWithShadow(
                    velocity.sVelocityPrefix,
                    xPos,
                    yPos,
                    FONT_WHITE);
            mc.fontRenderer.drawStringWithShadow(
                    velocity.sVelocityPrevMax,
                    xPos + lenPrefix,
                    yPos,
                    color);
            mc.fontRenderer.drawStringWithShadow(
                    velocity.sVelocityPostfix,
                    xPos + lenPrefix + lenVelocityMax,
                    yPos,
                    color);
        }
        // отрисовка текста: текущая скорость
        else
        {
            mc.fontRenderer.drawStringWithShadow(
                    velocity.sVelocity,
                    VelocityBar_xPos + Skin.MC_ICON_SIZE + STRING_PREFIX_px,
                    VelocityBar_yPos + (Skin.MC_ICON_SIZE-STRING_HEIGHT)/2,
                    FONT_WHITE);
        }
        // отрисовка иконки
        Drawing.DrawItemStack(mc.getItemRenderer(), new ItemStack(Items.COMPASS), VelocityBar_xPos, VelocityBar_yPos);
    }

    private void drawClockBar()
    {
        final CollectedClockData.Data clock = Informator.clock.data;
        final CollectedWeatherData.Data weather = Informator.weather.data;
        if (!clock.valid || !weather.valid) return;

        final int currentTimeStrLen = mc.fontRenderer.getStringWidth(clock.currentTime) + STRING_GROW_px;
        final boolean showBedIcon = ModSettings.TIME.TimeBarBed_Show.get() ? clock.restTimeHourOverhead : false;

        // учёт сдвига на размер иконки луны при прижатии книзу
        final boolean isWeatherBarPresent = ModSettings.TIME.TimeBarMoon_Show.get() || ModSettings.TIME.TimeBarWeather_Show.get();
        final int weatherBarHeight = isWeatherBarPresent ? Skin.ICON_WEATHER_PRETTY.size : 0;

        // расчёт размещения панели
        int time_xPos = 0;
        int time_yPos = 0;
        switch (ModSettings.TIME.TimeBar_alignMode.get())
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
        time_xPos += ModSettings.TIME.TimeBar_xOffset.get();
        time_yPos += ModSettings.TIME.TimeBar_yOffset.get();

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
            if (ModSettings.TIME.TimeBarMoon_Show.get())
            {
                moonPhaseLen = STRING_GROW_px + Math.max(
                        mc.fontRenderer.getStringWidth(weather.sMoonPhase),
                        mc.fontRenderer.getStringWidth(weather.sMoonPhaseFactor));
                // позиция и размеры
                switch (ModSettings.TIME.TimeBar_alignMode.get())
                {
                default:
                case 0: // topleft
                    moon_xPos = ModSettings.TIME.TimeBarWeather_Show.get() ? Skin.ICON_WEATHER_PRETTY.size : 0;
                    moon_yPos = 0;
                    break;
                case 1: // topright
                    moon_xPos = mainWndScaledWidth - moonPhaseLen - (1+Skin.ICON_MOON.size+1);
                    moon_yPos = 0;
                    break;
                case 2: // bottomleft
                    moon_xPos = ModSettings.TIME.TimeBarWeather_Show.get() ? Skin.ICON_WEATHER_PRETTY.size : 0;
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
            if (ModSettings.TIME.TimeBarWeather_Show.get())
            {
                // позиция и размеры
                switch (ModSettings.TIME.TimeBar_alignMode.get())
                {
                default:
                case 0: // topleft
                    weather_xPos = 0;
                    weather_yPos = 0;
                    break;
                case 1: // topright
                    weather_xPos = mainWndScaledWidth - moonPhaseLen - (ModSettings.TIME.TimeBarMoon_Show.get() ? 1 : 0) * (1+Skin.ICON_MOON.size+1) - Skin.ICON_WEATHER_PRETTY.size;
                    weather_yPos = 0;
                    break;
                case 2: // bottomleft
                    weather_xPos = 0;
                    break;
                case 3: // bottomright
                    weather_xPos = mainWndScaledWidth - moonPhaseLen - (ModSettings.TIME.TimeBarMoon_Show.get() ? 1 : 0) * (1+Skin.ICON_MOON.size+1) - Skin.ICON_WEATHER_PRETTY.size;
                    break;
                }
            }
        }

        // ВРЕМЯ: отрисовка панели
        if (ModSettings.GENERAL.Global_ShowPanel.get()) 
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
        if (ModSettings.TIME.TimeBarMoon_Show.get())
        {
            // ЛУНА: отрисовка панели
            if (ModSettings.GENERAL.Global_ShowPanel.get()) 
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
            if (ModSettings.TIME.TimeBarWeather_Show.get())
            {
                // ПОГОДА:отрисовка панели
                if (ModSettings.GENERAL.Global_ShowPanel.get()) 
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
        mc.getTextureManager().bindTexture(Informator.weather_textures);
        if (ModSettings.TIME.TimeBarMoon_Show.get())
        {
            // ЛУНА: отрисовка иконки луны
            DrawSkinIcon(
                    moon_xPos+1,
                    moon_yPos + Skin.MC_ICON_SIZE + (2*STRING_HEIGHT-Skin.ICON_MOON.size)/2,
                    Skin.ICON_MOON,
                    weather.moonPhase);
            // ПОГОДА: (отображается на отдельной панели только СОВМЕСТНО с луной)
            if (ModSettings.TIME.TimeBarWeather_Show.get())
            {
                // отрисовка иконки
                if (!ModSettings.TIME.TimeBarWeatherPretty_Show.get())
                {
                    DrawSkinIcon(
                            weather_xPos,
                            weather_yPos + Skin.MC_ICON_SIZE,
                            Skin.ICON_WEATHER_AVTTRUE,
                            weatherPhase);
                }
                else
                {
                    drawWeatherAndMoon(
                            weather_xPos,
                            weather_yPos + Skin.MC_ICON_SIZE,
                            false,
                            weatherPhasePretty,
                            weather.moonPhase);
                }
            }
        }
        // ВРЕМЯ: отрисовка иконки часов
        if (ModSettings.TIME.TimeBarMoon_Show.get() && ModSettings.TIME.TimeBarWeather_Show.get())
        {
            Drawing.DrawItemStack(mc.getItemRenderer(), new ItemStack(Items.CLOCK), time_xPos, time_yPos);
        }
        // ВРЕМЯ (иконка погоды на месте иконки часов):
        else
        {
            //не пользуемся этим методом??? иконка слишком маленькая, и новолуние неотличимо от дождя днём
            drawWeatherAndMoon(
                    time_xPos,
                    time_yPos,
                    true,
                    weatherPhasePretty,
                    weather.moonPhase);
            //DrawSkinIcon(
            //        time_xPos,
            //        time_yPos,
            //        Skin.ICON_WEATHER_TIME,
            //        weatherPhasePretty);
        }
        // КРОВАТЬ: отрисовка иконки кровати
        if (showBedIcon)
        {
            Drawing.DrawItemStack(mc.getItemRenderer(), new ItemStack(Items.BLUE_BED), time_xPos + Skin.MC_ICON_SIZE, time_yPos);
        }
    }

    private void drawEnchantBar()
    {
        final CollectedEnchantmentsData.Data enchantments = Informator.enchantments.data;
        if (!enchantments.valid) return;
        if (enchantments.held_enchanted.isEmpty()) return;

        int deltaY = ModSettings.ENCHANTS.EnchantBar_yOffset.get(); // смещение по высоте
        final int xPos = ModSettings.ENCHANTS.EnchantBar_xOffset.get() + mainWndScaledWidth; // правая граница панелей

        // перебираем список зачарованных предметов
        // (список отфильтрован флагами Informator.EnchantBar_ShowHands, Informator.EnchantBar_ShowBody)
        for (CollectedEnchantmentsData.HeldItem hitm : enchantments.held_enchanted)
        {
            // список наложенных чар всегда не пуст, т.ч. кол-во чарок != 0
            final int count = hitm.enchants.size();
            // ДВУХПРОХОДОВЫЙ АЛГОРИТМ: сначала считаем длину строк, потом отрисовываем
            // расчёт размера панели
            int textMaxLen = 0;
            for (final String text : hitm.enchants)
            {
                final int textLen = mc.fontRenderer.getStringWidth(text) + STRING_GROW_px;
                if (textLen > textMaxLen) textMaxLen = textLen;
            }
            // отрисовка панели
            final int panelHeight = (count == 1) ? Skin.MC_ICON_SIZE : (STRING_HEIGHT * count + 1);
            if (ModSettings.GENERAL.Global_ShowPanel.get()) 
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
            final Item item = Item.getItemById(hitm.id);
            Drawing.DrawItemStack(mc.getItemRenderer(), new ItemStack(item), xPos - Skin.MC_ICON_SIZE - textMaxLen, deltaY);
            // отрисовка текста
            final int prevDeltaY = deltaY;
            for (final String text : hitm.enchants)
            {
                mc.fontRenderer.drawStringWithShadow(
                        text,
                        xPos - textMaxLen + STRING_PREFIX_px,
                        deltaY + ((count == 1) ? ((Skin.MC_ICON_SIZE-STRING_HEIGHT)/2+1) : 0),
                        FONT_WHITE);
                deltaY += STRING_HEIGHT;
            }
            deltaY = prevDeltaY + panelHeight;
        }
    }

    final static ITextComponent [][] FACING_NAMES = {
        {   Informator.TRANSLATOR.field_below,
            Informator.TRANSLATOR.field_ontop,
            Informator.TRANSLATOR.field_behind,
            Informator.TRANSLATOR.field_infront,
            Informator.TRANSLATOR.field_onright,
            Informator.TRANSLATOR.field_onleft }, // SOUTH(0)
        {   Informator.TRANSLATOR.field_below,
            Informator.TRANSLATOR.field_ontop,
            Informator.TRANSLATOR.field_onright,
            Informator.TRANSLATOR.field_onleft,
            Informator.TRANSLATOR.field_infront,
            Informator.TRANSLATOR.field_behind }, // WEST(1)
        {   Informator.TRANSLATOR.field_below,
            Informator.TRANSLATOR.field_ontop,
            Informator.TRANSLATOR.field_infront,
            Informator.TRANSLATOR.field_behind,
            Informator.TRANSLATOR.field_onleft,
            Informator.TRANSLATOR.field_onright }, // NORTH(2)
        {   Informator.TRANSLATOR.field_below,
            Informator.TRANSLATOR.field_ontop,
            Informator.TRANSLATOR.field_onleft,
            Informator.TRANSLATOR.field_onright,
            Informator.TRANSLATOR.field_behind,
            Informator.TRANSLATOR.field_infront }  // EAST(3)
    };

    private void drawBlockBar()
    {
        // ранее уже была выполнена проверка : удалось определить блок, на который смотрим
        CollectedBlockData.Data details = Informator.block.data;

        // кэшируем значения перменных в этом методе
        final ClientWorld world = mc.world;
        final ClientPlayerEntity player = mc.player;
        final int dimentionTypeId = world.getDimension().getType().getId();

        // кешируем и индексируем надписи И др. переменные, которые будут использоваться для вывода инфомации
        final int LINE_MAX_COUNT = 8;
        String blockNameStr = "";
        String [] strLines = new String[LINE_MAX_COUNT];
        int strLinesUsed = 0; // по умолчанию всегда выводятся координаты блока
        boolean playerOffsetShown = false;

        // ===== КООРДИНАТЫ БЛОКА =====
        // раньше к Y тут прибавлялась единица, что неправильно, т.к. если поставить перед собой куб и смотреть на него, то
        // поскольку он будет на уровне наших ног, то и dY тоже должно быть = 0, а не стать +1 !!!
        //---
        // раньше смещение игрока относительно координат, нак оторые смотрим были абсолютные, что было неправильно, т.к.
        // если смотреть вправо, то X будет +1, если смотреть влево, то X тоже станет +1 (хотя правильнее показывать направление
        // уменьшения координат !!!)
        final BlockPos playerPos = new BlockPos(player.posX, player.posY, player.posZ);
        final int x = details.pos.getX();
        final int y = details.pos.getY();
        final int z = details.pos.getZ();
        strLines[strLinesUsed++] = String.format("%d %d %d", x, y, z);
//посчитать ещё и расстояние?!

        /**Informator.R4.add(String.format("player at %d %d %d", playerPos.getX(), playerPos.getY(), playerPos.getZ()));*/

        // ===== НАИМЕНОВАНИЕ БЛОКА =====
        // если мы смотрим не в воздух
        if (!details.isAir)
        {
            // проверяем исключительные ситуации и контрируируем валидность данных на ветках условий
            if (details.block != null)
            {
                // название блока (если у блока есть stack, то м.б. известно кол-во item-ов; иначе выводим название блока)
                if (details.stack != null)
                {
                    final int count = details.stack.getCount();
                    if (count != 1)
                        blockNameStr = String.format("%s (%d)", details.stack.getDisplayName().getFormattedText(), count);
                    else
                        blockNameStr = details.stack.getDisplayName().getFormattedText();
                }
                if (blockNameStr.isEmpty())
                {
                    blockNameStr = details.block.getNameTextComponent().getFormattedText();
                }
            }
        }

        // ===== СМЕЩЕНИЕ БЛОКА (от персонажа, либо координаты в аду) =====
        final ItemStack held_stack = player.getHeldItemMainhand();
        final Item held_item = (held_stack == null) ? Items.AIR : held_stack.getItem();
        if ((held_item == Items.OBSIDIAN) && (dimentionTypeId == 0 || dimentionTypeId == -1))
        {
            // если удерживаемый элемент является обсидианом, то считаем координаты нижнего/верхнего мира
            /** хрень какая-то получается... надо разбираться (взято из setPortal в Entity)
            final BlockPos portalPos = details.pos;
            BlockPattern.PatternHelper blockpattern$patternhelper = ((NetherPortalBlock)Blocks.NETHER_PORTAL).createPatternHelper(world, portalPos);
            double d0 = blockpattern$patternhelper.getForwards().getAxis() == Direction.Axis.X ? (double)blockpattern$patternhelper.getFrontTopLeft().getZ() : (double)blockpattern$patternhelper.getFrontTopLeft().getX();
            double d1 = Math.abs(MathHelper.pct((blockpattern$patternhelper.getForwards().getAxis() == Direction.Axis.X ? details.pos.getZ() : details.pos.getX()) - (double)(blockpattern$patternhelper.getForwards().rotateY().getAxisDirection() == Direction.AxisDirection.NEGATIVE ? 1 : 0), d0, d0 - (double)blockpattern$patternhelper.getWidth()));
            double d2 = MathHelper.pct(details.pos.getY() - 1.0D, (double)blockpattern$patternhelper.getFrontTopLeft().getY(), (double)(blockpattern$patternhelper.getFrontTopLeft().getY() - blockpattern$patternhelper.getHeight()));
            final Vec3d portalVec = new Vec3d(d1, d2, 0.0D);
            final Direction teleportDirection = blockpattern$patternhelper.getForwards();
strLines[strLinesUsed++] = String.format("d0=%.2f d0=%.2f d0=%.2f | %s", d0, d1, d2, teleportDirection.getName());
            */
            switch (dimentionTypeId)
            {
            case 0: // верхний мир
                strLines[strLinesUsed++] = String.format("[/8] %d %d %d", (int)(x/8), (int)(y/8), (int)(z/8));
                playerOffsetShown = true;
                break;
            case -1: // нижний мир (ад)
                strLines[strLinesUsed++] = String.format("[*8] %d ?? %d", x*8, z*8);
                playerOffsetShown = true;
                break;
            }
        }
        /**if (details.block != null)
            if (details.block == Blocks.OBSIDIAN || details.block == Blocks.NETHER_PORTAL)
            {
                // если блок является обсидианом (или порталом в ад), то считаем координаты нижнего/верхнего мира
                switch (dimentionTypeId)
                {
                case 0: // верхний мир
                    strLines[strLinesUsed++] = String.format("[/8] %d %d %d", (int)(x/8), (int)(y/8), (int)(z/8));
                    playerOffsetShown = true;
                    break;
                case -1: // нижний мир (ад)
                    strLines[strLinesUsed++] = String.format("[*8] %d ?? %d", x*8, z*8);
                    playerOffsetShown = true;
                    break;
                }
            }*/
        // смещение относительно координат персонажа (если это место ещё не занято)
        if (!playerOffsetShown && ModSettings.BLOCK.BlockBar_ShowPlayerOffset.get())
        {
            strLines[strLinesUsed++] = String.format("[±] %d %d %d", x - playerPos.getX(), y - playerPos.getY(), z - playerPos.getZ());
        }

        // ===== СВЕТИМОСТЬ (ОСВЕЩЁННОСТЬ) БЛОКА =====
        // код светимости, взят из call у DebugOverlayGui
        // поскольку система тут оперирует с дробными значениями, получаем именно координаты __поверхности__ блока
        final BlockPos blockpos = details.pos.up();
        // освещение (светимость) светом блоков, т.е. это свет поверности блока в полночь
        final int blockIllumination = this.mc.world.getLightFor(LightType.BLOCK, blockpos);
        // либо над поверхностью блока стоит прозрачный блок (факел, сундук, вода); либо там ничего нет, т.е. там воздух
        final boolean hasSkyLight = world.getDimension().hasSkyLight();
        if (hasSkyLight && (world.isAirBlock(blockpos) || world.canBlockSeeSky(blockpos)))
        {
            // освещение блока небом, т.е. это свет поверхности блока в полдень
            final int skyLuminosity = this.mc.world.getLightFor(LightType.SKY, blockpos);
            //---
            // код светимости неба взят из метода updatePower у DaylightDetectorBlock
            // мощность света на блоке от неба в текущий момент (утро, день, вечер, ночь... погода не влияет)
            int daylightPower = world.getLightFor(LightType.SKY, blockpos) - world.getSkylightSubtracted();
            float f = world.getCelestialAngleRadians(1.0F); // радиус небесного угла
            if (daylightPower > 0)
            {
                final float f1 = f < (float)Math.PI ? 0.0F : ((float)Math.PI * 2F);
                f = f + (f1 - f) * 0.2F;
                daylightPower = Math.round((float)daylightPower * MathHelper.cos(f));
            }
            daylightPower = MathHelper.clamp(daylightPower, 0, 15);
            strLines[strLinesUsed++] = String.format(
                    "%s %d/%d, %s %d", // Свет %d/%d, блок %d
                    TextTranslation.getInstance().field_illumination_of_block.getFormattedText(),
                    daylightPower,
                    skyLuminosity,
                    TextTranslation.getInstance().field_of_block.getFormattedText(),
                    blockIllumination);
        }
        else
        {
            if (hasSkyLight)
            {
                strLines[strLinesUsed++] = String.format(
                        "%s %d", // Освещение %d
                        TextTranslation.getInstance().field_block_lighting.getFormattedText(),
                        blockIllumination);
            }
            else
            {
                strLines[strLinesUsed++] = String.format(
                        "%s %d", // Светимость %d
                        TextTranslation.getInstance().field_luminosity_of_block.getFormattedText(),
                        blockIllumination);
            }
        }

        // ===== ЭЛЕКТРИФИКАЦИЯ (ЗАРЯД) БЛОКА, механизмы и схемы =====
        if (ModSettings.BLOCK.BlockBar_ShowElectricity.get())
        {
            if (details.power.wire)
            {
                strLines[strLinesUsed++] =
                    (details.power.strong_level == 15) ?
                    (TextTranslation.getInstance().field_provide_power_level.getFormattedText() +" 15") :
                    (TextTranslation.getInstance().field_wire_level.getFormattedText() +" " + details.power.strong_level);
            }
            else if (details.power.strong_level > 0)
            {
                strLines[strLinesUsed++] = TextTranslation.getInstance().field_power_level.getFormattedText() + " " + details.power.strong_level;
            }
            if (details.power.powered)
            {
                final String nm = FACING_NAMES[details.power.facing.getHorizontalIndex()][details.power.direction.getIndex()].getFormattedText();
                strLines[strLinesUsed++] = String.format(
                    (TextTranslation.getInstance().field_powered.getFormattedText() + " %s %d"),
                    details.power.strong ? nm.toUpperCase() : nm.toLowerCase(),
                    details.power.level
                );
            }
        }

        // ===== ВЫЧИСЛЕНИЕ ДЛИНН НАДПИСЕЙ =====
        final int blockNameStrLen = blockNameStr.isEmpty() ? 0 : (mc.fontRenderer.getStringWidth(blockNameStr) + STRING_GROW_px);
        int [] strLens = new int[LINE_MAX_COUNT];
        int strLensMax = 0;
        for (int i = 0; i < strLinesUsed; ++i)
        {
            strLens[i] = 0;
            if (strLines[i].isEmpty()) continue;
            strLens[i] = mc.fontRenderer.getStringWidth(strLines[i]);
            if (strLensMax < strLens[i]) strLensMax = strLens[i];
        }
        // расчёт размещения панели
        int InfoBlockBar_xPos;
        int InfoBlockBar_yPos;
        final int InfoBlockBar_PanelWidth = Skin.MC_ICON_SIZE + strLensMax + STRING_GROW_px;
        final int InfoBlockBar_PanelHeight = (strLinesUsed == 1) ? Skin.MC_ICON_SIZE : (strLinesUsed * STRING_HEIGHT);
        // расположение надписи с названием
        final int BlockName_xPos;
        final int BlockName_yPos;
        // позиционирование панели
        switch (ModSettings.BLOCK.BlockBar_alignMode.get())
        {
        default:
        case 0: // topleft
            InfoBlockBar_xPos = 0;
            InfoBlockBar_yPos = 0;
            BlockName_xPos = 0;
            BlockName_yPos = InfoBlockBar_PanelHeight;
            break;
        case 1: // topright
            InfoBlockBar_xPos = mainWndScaledWidth - InfoBlockBar_PanelWidth;
            InfoBlockBar_yPos = 0;
            BlockName_xPos = mainWndScaledWidth - blockNameStrLen;
            BlockName_yPos = InfoBlockBar_PanelHeight;
            break;
        case 2: // bottomleft
            InfoBlockBar_xPos = 0;
            InfoBlockBar_yPos = mainWndScaledHeight - InfoBlockBar_PanelHeight;
            BlockName_xPos = 0;
            BlockName_yPos = InfoBlockBar_yPos - STRING_HEIGHT;
            break;
        case 3: // bottomright
            InfoBlockBar_xPos = mainWndScaledWidth - InfoBlockBar_PanelWidth;
            InfoBlockBar_yPos = mainWndScaledHeight - InfoBlockBar_PanelHeight;
            BlockName_yPos = InfoBlockBar_yPos - STRING_HEIGHT;
            BlockName_xPos = mainWndScaledWidth - blockNameStrLen;
            break;
        }
        InfoBlockBar_xPos += ModSettings.BLOCK.BlockBar_xOffset.get();
        InfoBlockBar_yPos += ModSettings.BLOCK.BlockBar_yOffset.get();
        // отрисовка панели
        final boolean hasBlockName = ModSettings.BLOCK.BlockBar_ShowName.get() && (blockNameStrLen != 0);
        if (ModSettings.GENERAL.Global_ShowPanel.get()) 
        {
            GuiUtils.drawGradientRect(0,
                    InfoBlockBar_xPos,
                    InfoBlockBar_yPos,
                    InfoBlockBar_xPos + InfoBlockBar_PanelWidth,
                    InfoBlockBar_yPos + InfoBlockBar_PanelHeight,
                    PANEL_STEEL,
                    PANEL_TRANSPARENT);
            if (hasBlockName)
            {
                GuiUtils.drawGradientRect(0,
                        BlockName_xPos,
                        BlockName_yPos,
                        BlockName_xPos + blockNameStrLen,
                        BlockName_yPos + STRING_HEIGHT,
                        PANEL_STEEL,
                        PANEL_TRANSPARENT);
            }
        }
        // отрисовка текста : координаты, дистанция, освещённость, заряд
        final int strLinesYOffset = ((strLinesUsed == 1) ? ((Skin.MC_ICON_SIZE-STRING_HEIGHT)/2+1) : 1);
        for (int i = 0; i < strLinesUsed; ++i)
        {
            if (strLens[i] == 0) continue;
            mc.fontRenderer.drawStringWithShadow(
                    strLines[i],
                    InfoBlockBar_xPos + Skin.MC_ICON_SIZE + STRING_PREFIX_px,
                    InfoBlockBar_yPos + STRING_HEIGHT * i + strLinesYOffset,
                    FONT_WHITE);
        }
        // отрисовка наименования блока
        if (hasBlockName)
        {
            int color = net.minecraft.item.Rarity.COMMON.color.getColor();
            if (details.item != null)
                color = details.item.getRarity(details.stack).color.getColor();
            mc.fontRenderer.drawStringWithShadow(
                    blockNameStr,
                    BlockName_xPos + STRING_PREFIX_px, 
                    BlockName_yPos + 1,
                    color);
        }
        // отрисовка иконки (если stack==null, то смысла рисовать иконку ItemStack.EMPTY нет, т.к. она полностью прозначная)
        if (ModSettings.BLOCK.BlockBar_ShowIcons.get() && details.stack != null)
        {
            Drawing.DrawItemStack(mc.getItemRenderer(), details.stack, InfoBlockBar_xPos, InfoBlockBar_yPos);
        }
    }

    private void drawTargetBar()
    {
        // ранее уже была выполнена проверка : удалось определить сущность, на которую смотрим
        CollectedEntityData.Data details = Informator.entity.data;

        // кэшируем значения перменных в этом методе
        final ClientWorld world = mc.world;
        final ClientPlayerEntity player = mc.player;
        //final int dimentionTypeId = world.getDimension().getType().getId();
        final boolean portraitPresent = ModSettings.TARGET.TargetMobBar_ShowPortrait.get();

/*
        if (!view.ISee) return;
*/
        // позиция и размеры
        final int TargetMobBar_Len = ModSettings.TARGET.TargetMobBar_ScreenWidth.get() * mainWndScaledWidth / 100;
        final int nameLen = mc.fontRenderer.getStringWidth(details.name);
        final int portraitBorder = 2;
        final int portraitSize = portraitPresent ? (Skin.MC_ICON_SIZE + portraitBorder) : 0;

        // расчёт размещения панели
        int target_xPos;
        int target_yPos = 0;
        switch (ModSettings.TARGET.TargetMobBar_alignMode.get())
        {
        default:
        case 0: // topcenter
            target_xPos = (mainWndScaledWidth - TargetMobBar_Len) / 2;
            break;
        case 1: // topleft
            target_xPos = 0;
            break;
        case 2: // topright
            target_xPos = mainWndScaledWidth - TargetMobBar_Len;
            break;
        }
        target_xPos += ModSettings.TARGET.TargetMobBar_xOffset.get();
        target_yPos += ModSettings.TARGET.TargetMobBar_yOffset.get();

        // отрисовка панелей
        //игнорируется:if (ModSettings.GENERAL.Global_ShowPanel.get()) 
        {
            // основная панель
            GuiUtils.drawGradientRect(0,
                    target_xPos,
                    target_yPos,
                    target_xPos + TargetMobBar_Len,
                    target_yPos + 1 + STRING_HEIGHT + 1,
                    PANEL_GRAY_TRANSPARENT,
                    PANEL_GRAY_TRANSPARENT);
            // панель имени
            GuiUtils.drawGradientRect(0,
                    target_xPos + 1,
                    target_yPos + 1,
                    target_xPos + TargetMobBar_Len - 1,
                    target_yPos + 1 + STRING_HEIGHT,
                    PANEL_STEEL_TRANSPARENT,
                    PANEL_TRANSPARENT);
        }
        // имя текст
        mc.fontRenderer.drawStringWithShadow(
                details.name,
                target_xPos + 1 + (TargetMobBar_Len - nameLen) / 2,
                target_yPos + 1 + 1,
                FONT_WHITE);

        if (details.isLiving)
        {
            final String healthStr =
                    (details.armor == 0) ?
                    ForgeI18n.parseMessage(Informator.TRANSLATOR.field_health.getFormattedText(), (int)details.health, (int)details.healthMax) :
                    ForgeI18n.parseMessage(Informator.TRANSLATOR.field_health_and_armor.getFormattedText(), (int)details.health, (int)details.healthMax, details.armor);
            final int healthLen = mc.fontRenderer.getStringWidth(healthStr);
            int healthLineLen = TargetMobBar_Len;
            final int health_xPos = target_xPos + portraitSize;
            final int health_yPos = target_yPos + 1 + STRING_HEIGHT + 1;
            final int health_wPos = target_xPos + TargetMobBar_Len;
            final int health_hPos = health_yPos + 1 + STRING_HEIGHT + 1;
            // панели здоровья
            if (details.health <= 0 || details.healthMax <= 0.01F) // исключительные ситуации (на ноль делить тоже нельзя ;)
                healthLineLen = 1;
            else if (details.healthMax > details.health)
                healthLineLen = (int)(Math.round(((float)details.health/(float)details.healthMax) * healthLineLen));
            // здоровье панель зелёная
            GuiUtils.drawGradientRect(0,
                    health_xPos,
                    health_yPos,
                    target_xPos + healthLineLen,
                    health_hPos,
                    PANEL_GREEN,
                    PANEL_GREEN);
            // здоровье панель красная
            if (healthLineLen < TargetMobBar_Len)
            {
                GuiUtils.drawGradientRect(0,
                        target_xPos + healthLineLen,
                        health_yPos,
                        target_xPos + TargetMobBar_Len,
                        health_hPos,
                        PANEL_MAROON,
                        PANEL_MAROON);
            }
            // здоровье панель серая 
            GuiUtils.drawGradientRect(0,
                    health_xPos + 1,
                    health_yPos + 1,
                    health_wPos - 1,
                    health_hPos - 1,
                    PANEL_STEEL,//GRAY_TRANSPARENT,
                    PANEL_TRANSPARENT);
            // здоровье и броня текст
            mc.fontRenderer.drawStringWithShadow(
                    healthStr,
                    health_xPos + 1 + (TargetMobBar_Len - portraitSize - healthLen) / 2,
                    health_yPos + 1 + 1,
                    FONT_WHITE);
        }

        // рисуем портреты
        if (portraitPresent)
        {
            // портретная панель светлая
            GuiUtils.drawGradientRect(0,
                    target_xPos,
                    target_yPos + 1 + STRING_HEIGHT + 1,
                    target_xPos + portraitSize,
                    target_yPos + 1 + STRING_HEIGHT + 1 + portraitSize,
                    PANEL_GRAY_TRANSPARENT,
                    PANEL_GRAY_TRANSPARENT);
            // рисуем портрет
            if (details.isLiving &&
                !mc.skipRenderWorld &&
                (world != null) &&
                (player != null) &&
                Minecraft.isGuiEnabled() &&
                !mc.isGamePaused() &&
                (mc.getRenderViewEntity() != null))
            {
                float scale = (float)(portraitSize-portraitBorder) / Math.max(details.entity.getHeight(), details.entity.getWidth());
                try {
                    Drawing.drawEntityOnScreen(
                            target_xPos + 1 + (portraitSize-portraitBorder)/2/*отсчёт от центра?*/,
                            target_yPos + 1 + STRING_HEIGHT + 1 + portraitSize,
                            scale, 0, 0,
                            (LivingEntity)details.entity);
                } catch (Exception e) { } // почему-то рисовалка портретов падает при первом старте, если entity в прицеле, а мир загружается
            }
/*
            else
            {
                mc.renderEngine.bindTexture(new ResourceLocation("avttrue_informator:textures/icons.png"));
                drawTexturedModalRect(target_xPos + 1, target_yPos + 3 + STRING_HEIGHT, 
                                        16, 0, portraitSize-portraitBorder, portraitSize-portraitBorder);
            }
*/
        }

        // дистанция
        int shownLines = 0;
        if (ModSettings.TARGET.TargetMobBar_ShowDistance.get())
        {
            // дистанция, расчёт
            final String distStr = ForgeI18n.parseMessage(Informator.TRANSLATOR.field_distance.getFormattedText(), String.format("%3.1f", details.distance));
            final int distLen = mc.fontRenderer.getStringWidth(distStr) + STRING_GROW_px;
            final int dist_xPos = target_xPos + portraitSize;
            final int dist_yPos = target_yPos + (STRING_HEIGHT + 2) * 2;
            shownLines++;
            // дистанция панель
            GuiUtils.drawGradientRect(0,
                    dist_xPos,
                    dist_yPos,
                    dist_xPos + distLen,
                    dist_yPos + STRING_HEIGHT,
                    PANEL_STEEL,//GRAY_TRANSPARENT,
                    PANEL_TRANSPARENT);
            // дистанция текст
            mc.fontRenderer.drawStringWithShadow(
                    distStr,
                    dist_xPos + STRING_PREFIX_px,
                    dist_yPos + 1,
                    FONT_WHITE);
        }

        // дополнительные характеристики / коня, собачек и кошечек
        if (details.tamed || details.movementPresent)
        {
            // доп. характеристики для приручаемых животных и коней
            String ownerStr = "";
            String movementSpeedStr = "";
            String jumpHeightStr = "";
            int ownerLen = 0;
            int movementSpeedLen = 0;
            int jumpHeightLen = 0;
            if (details.tamed)
            {
                if (details.nameOwner == null || details.nameOwner.isEmpty())
                    ownerStr = ForgeI18n.parseMessage(Informator.TRANSLATOR.field_whose.getFormattedText(), "???");
                else
                    ownerStr = ForgeI18n.parseMessage(Informator.TRANSLATOR.field_whose.getFormattedText(), details.nameOwner);
                ownerLen = mc.fontRenderer.getStringWidth(ownerStr);
            }
            if (details.movementPresent)
            {
                if (details.movementSpeed > 0)
                {
                    movementSpeedStr = ForgeI18n.parseMessage(Informator.TRANSLATOR.field_movement_speed.getFormattedText(), String.format("%4.2f", details.movementSpeed));
                    movementSpeedLen = mc.fontRenderer.getStringWidth(movementSpeedStr);
                }
                if (details.jumpHeight > 0)
                {
                    jumpHeightStr = ForgeI18n.parseMessage(Informator.TRANSLATOR.field_jump_height.getFormattedText(), String.format("%4.2f", details.jumpHeight));
                    jumpHeightLen = mc.fontRenderer.getStringWidth(jumpHeightStr);
                }
            }

            // панель
            int details_xPos = target_xPos + portraitSize;
            final int detatils_yPos = target_yPos + (STRING_HEIGHT+2) * (2+shownLines) + 1; // +1 добавлен для того, чтобы потом не выравнивать каждую надпись
            GuiUtils.drawGradientRect(0,
                    details_xPos,
                    (detatils_yPos-1),
                    details_xPos + STRING_PREFIX_px + ownerLen + STRING_PREFIX_px + movementSpeedLen + jumpHeightLen + STRING_POSTFIX_px,
                    (detatils_yPos-1) + STRING_HEIGHT,
                    PANEL_STEEL,//GRAY_TRANSPARENT,
                    PANEL_TRANSPARENT);
            // текст
            details_xPos += STRING_PREFIX_px;
            if (details.tamed)
            {
                mc.fontRenderer.drawStringWithShadow(ownerStr, details_xPos, detatils_yPos, FONT_WHITE);
                details_xPos += (ownerLen + STRING_PREFIX_px);
            }
            if (details.movementPresent)
            {
                int movement_color = FONT_RED;
                int jump_color = FONT_RED;
                if (details.movementSpeed >= 13.0D)      movement_color = FONT_AQUA; 
                else if (details.movementSpeed >= 11.0D) movement_color = FONT_GREEN;
                else if (details.movementSpeed >= 8.0D)  movement_color = FONT_WHITE;
                if (details.jumpHeight >= 5.0D)          jump_color = FONT_AQUA;
                else if (details.jumpHeight >= 4.0D)     jump_color = FONT_GREEN;
                else if (details.jumpHeight >= 2.75D)    jump_color = FONT_WHITE; 
                mc.fontRenderer.drawStringWithShadow(movementSpeedStr, details_xPos, detatils_yPos, movement_color);
                details_xPos += (movementSpeedLen + STRING_PREFIX_px);
                mc.fontRenderer.drawStringWithShadow(jumpHeightStr, details_xPos, detatils_yPos, jump_color);
            }
            shownLines++;
        }
    }

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

    private void drawWeatherAndMoon(int x, int y, boolean for_time, int weather, int moon)
    {
        // используем стандартные иконки, так быстрее, нежели смешивать два слоя
        if (!ModSettings.TIME.TimeBarWeather_WithMoonPhases.get() || // ИЛИ режим выключен
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

    /***private void drawDebugBar()
    {
        // включаем отладку (скрытую), если поменялись тестовые регистры, то будет заменена надпись в тек.временем на их значения
        final boolean nums = Informator.R1 != null || Informator.R2 != null || Informator.R3 != null;
        final boolean strs = !Informator.R4.isEmpty();
        if (!nums && !strs) return;

        int panel_height = (nums ? 1 : 0) + (strs ? Informator.R4.size() : 0);
        if (panel_height > 16) panel_height = 16;
        int [] panel_widths = new int[panel_height];
        int panel_widths_idx = 0, panel_width = 0;

        String line0 = null;
        if (nums)
        {
            line0 = Informator.R1 + " | " + Informator.R2 + " | " + Informator.R3;
            //+ " | " + String.format("%1$5.2f", Informator.R0);
            panel_width = panel_widths[panel_widths_idx++] = mc.fontRenderer.getStringWidth(line0);
        }
        if (strs)
        {
            for (final String lineN : Informator.R4)
            {
                panel_widths[panel_widths_idx] = mc.fontRenderer.getStringWidth(lineN);
                panel_width = Math.max(panel_width, panel_widths[panel_widths_idx++]);
                if (panel_widths_idx == 16) break;
            }
        }
        panel_width += STRING_GROW_px;
        int y = mainWndScaledHeight - 24 - panel_height * STRING_HEIGHT;
        //мешается:final int x = (mainWndScaledWidth - panel_width)/2;
        //мешается:GuiUtils.drawGradientRect(0, x, y, x + panel_width, y + panel_height * STRING_HEIGHT, PANEL_STEEL, PANEL_TRANSPARENT);

        panel_widths_idx = 0;
        if (nums)
        {
            mc.fontRenderer.drawStringWithShadow(line0, (mainWndScaledWidth - panel_widths[panel_widths_idx++])/2 + STRING_PREFIX_px, y + 1, FONT_AQUA);
            y += STRING_HEIGHT;
        }
        if (strs)
        {
            for (final String lineN : Informator.R4)
            {
                mc.fontRenderer.drawStringWithShadow(lineN, (mainWndScaledWidth - panel_widths[panel_widths_idx++])/2 + STRING_PREFIX_px, y + 1, FONT_AQUA);
                y += STRING_HEIGHT;
                if (panel_widths_idx == 16) break;
            }
        }
    }/***/
}

