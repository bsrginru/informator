package avttrue.informator;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import avttrue.informator.config.ModSettings;
import avttrue.informator.config.ModSettingsScreen;
import avttrue.informator.data.CollectedBlockData;
import avttrue.informator.data.CollectedClockData;
import avttrue.informator.data.CollectedEnchantmentsData;
import avttrue.informator.data.CollectedEntityData;
import avttrue.informator.data.CollectedHeldItemsData;
import avttrue.informator.data.CollectedVelocityData;
import avttrue.informator.data.CollectedWeatherData;
import avttrue.informator.events.OnClientTick;
import avttrue.informator.events.OnKeyInput;
import avttrue.informator.events.OnRenderGameOverlay;
import avttrue.informator.events.OnRenderTick;
import avttrue.informator.events.OnRenderWorldLast;
import avttrue.informator.tools.Functions;
import avttrue.informator.tools.TextTranslation;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.moddiscovery.ModInfo;
import net.minecraftforge.versions.forge.ForgeVersion;
import net.minecraftforge.versions.mcp.MCPVersion;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(Informator.MODID)
public class Informator
{
    // Идентификатор мода, фигурирует в зависимостях, в конфиг-файлах, ресурсах, путях и т.п.
    public static final String MODID = "avttrue_informator";
    // Directly reference a log4j logger.
    public static final Logger LOGGER = LogManager.getLogger();
    // Статический транслятор, который регистрирует "самопереводящиеся" текстовые ресурсы
    public static final TextTranslation TRANSLATOR = TextTranslation.getInstance();
    // Статические функции модуля, в которых есть всяко-разно для упрощения кода функциональных шклассов-обработчиков
    public static final Functions TOOLS = Functions.getInstance();

    // время, которое идёт со скоростью ClientTick (около 20 тиков в секунду) и не зависит от нахождения игрока в аду/краю
    public static volatile long realTimeTick = 0;
    // используется для контроля игрового времени
    public static CollectedClockData clock = new CollectedClockData();
    public static CollectedWeatherData weather = new CollectedWeatherData();
    // используется для контроля скорости перемещения игрока
    public static CollectedVelocityData velocity = new CollectedVelocityData();
    // используется для контроля удерживаемых и надетых предметов
    public static CollectedHeldItemsData held_items = new CollectedHeldItemsData();
    // используется для контроля заколдованных предметов
    public static CollectedEnchantmentsData enchantments = new CollectedEnchantmentsData();
    // используется для обзора информации о блоках
    public static CollectedBlockData block = new CollectedBlockData();
    // используется для обзора информации о сущностях
    public static CollectedEntityData entity = new CollectedEntityData();

    // предварительно загруженные иконки, используемые для рисования погоды, времени суток
    public static ResourceLocation weather_textures = new ResourceLocation("avttrue_informator:textures/wthr.png");
    // предварительно загруженные иконки, используемые для рисования освещённости
    public static ResourceLocation light_textures = new ResourceLocation("avttrue_informator:textures/illumination.png");

    // отладочные регистры, чтобы смотреть всякую отладочную ерунду в рантайме
/*** //public static float R0 = 0;
    public static Integer R1 = null, R2 = null, R3 = null;
    public static ArrayList<String> R4 = new ArrayList<String>();/***/
    
    // Хватит рыться в Интернете!
    // правильный (актуальный) пример (и последовательность) инициализации см. в net.minecraftforge.common.ForgeMod
    // в ModExample инициализация неправильная!!!
    public Informator()
    {
        LOGGER.info("Informator mod loading, version {}, for Forge {} MC {} with MCP {}",
                "???",
                ForgeVersion.getVersion(),
                MCPVersion.getMCVersion(),
                MCPVersion.getMCPVersion());
        
        DistExecutor.callWhenOn(Dist.CLIENT, () -> () -> {
        	LOGGER.info("Informator !!!!!!!!!!!!!!!!!!!!!!!!!!!!!! (0)");
            ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.CONFIGGUIFACTORY, () -> (minecraft, screen) -> new ModSettingsScreen(minecraft, screen, this));
            LOGGER.info("Informator !!!!!!!!!!!!!!!!!!!!!!!!!!!!!! (1)");
            ModInfo mi = ModList.get().getMods().stream().filter(min -> min.getModId().equals(Informator.MODID)).findAny().orElseThrow(() -> new IllegalStateException("We couldn't find ourselves in the mods list!"));
            List<ModInfo> mli = ModList.get().getMods();
            for(int i = 0, cnt = mli.size(); i < cnt; i++)
            {
                LOGGER.info("Informator !!!!!!!!!!!!!!!!!!!!!!!!!!!!!! (2)");
                if(mli.get(i) != mi) continue;
                LOGGER.info("Informator !!!!!!!!!!!!!!!!!!!!!!!!!!!!!! (3)");
                mli.set(i, new ModInfo(mi.getOwningFile(), mi.getModConfig()) {
                    @Override
                    public boolean hasConfigUI() {
                        LOGGER.info("Informator !!!!!!!!!!!!!!!!!!!!!!!!!!!!!! (4)");
                        return true;
                    }
                });
            }
            return null;
        });

        final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::preInit);
        modEventBus.addListener(this::doClientStuff);
        ModLoadingContext.get().registerConfig(net.minecraftforge.fml.config.ModConfig.Type.CLIENT, ModSettings.spec);
        // если поставить здесь ForgeConfig.class (как во многих примерах), то НЕ будут вызываться event-ы Config.class
        modEventBus.register(ModSettings.class);

        //см. https://www.minecraftforge.net/forum/topic/68011-config-not-working/?tab=comments#comment-328444
        MinecraftForge.EVENT_BUS.register(new OnKeyInput());
        MinecraftForge.EVENT_BUS.register(new OnClientTick());
        MinecraftForge.EVENT_BUS.register(new OnRenderTick());
        MinecraftForge.EVENT_BUS.register(new OnRenderGameOverlay());
        MinecraftForge.EVENT_BUS.register(new OnRenderWorldLast());
        MinecraftForge.EVENT_BUS.register(this);
    }

    // Преинициализация
    //---
    // Called before {@link FMLClientSetupEvent} or {@link FMLDedicatedServerSetupEvent} during mod startup.
    // Called after {@link net.minecraftforge.event.RegistryEvent.Register} events have been fired.
    // Most non-specific mod setup will be performed here. Note that this is a parallel dispatched event - you cannot
    // interact with game state in this event.
    // See net.minecraftforge.fml.DeferredWorkQueue to enqueue work to run on the main game thread after this event has
    // completed dispatch.
    private void preInit(final FMLCommonSetupEvent event)
    {
        LOGGER.info("PreInit");
    }

    // Нечто, что делается только на клиентской стороне: получение настроек игры, key bindings
    //---
    // Called before {@link InterModEnqueueEvent}
    // Called after {@link FMLCommonSetupEvent}
    // Called on {@link net.minecraftforge.api.distmarker.Dist#CLIENT} - the game client.
    // Do client only setup with this event, such as KeyBindings.
    // This is a parallel dispatch event.
    private void doClientStuff(final FMLClientSetupEvent event)
    {
        LOGGER.info("Game Settings {}", event.getMinecraftSupplier().get().gameSettings);
        KeyBindings.Initialization();
    }

    // следующее событие не срабатывает (в Forge пока ВООБЩЕ нет вызовов этого метода)
    // видимо оно как-то связано с конфигурационным GUI, который тоже отсутствует в Forge
    // так что может быть это нормально (временно)
    @SubscribeEvent
    public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event)
    {
        System.out.printf("Config Changed %s %s", event.getModID(), event.getConfigID());
        /**if (Informator.R1 == null) Informator.R1 = new Integer(1); else Informator.R1++;/**/
    }
}
