package avttrue.informator;

import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.versions.forge.ForgeVersion;
import net.minecraftforge.versions.mcp.MCPVersion;

import avttrue.informator.config.ModSettings;
import avttrue.informator.data.CollectedClockData;
import avttrue.informator.data.CollectedEnchantmentsData;
import avttrue.informator.data.CollectedHeldItemsData;
import avttrue.informator.data.CollectedVelocityData;
import avttrue.informator.data.CollectedWeatherData;
import avttrue.informator.events.OnClientTick;
import avttrue.informator.events.OnKeyInput;
import avttrue.informator.events.OnRenderGameOverlay;
import avttrue.informator.events.OnRenderTick;
import avttrue.informator.tools.Functions;
import avttrue.informator.tools.TextTranslation;

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

    // отладочные регистры, чтобы смотреть всякую отладочную ерунду в рантайме
    //public static float R0 = 0;
    public static Integer R1 = null, R2 = null, R3 = null;
    public static ArrayList<String> R4 = new ArrayList<String>();

    // Хватит руться в Интернете!
    // правильный (актуальный) пример (и последовательность) инициализации см. в net.minecraftforge.common.ForgeMod
    // в ModExample инициализация неправильная!!!
    public Informator()
    {
        LOGGER.info("Informator mod loading, version {}, for Forge {} MC {} with MCP {}",
                "???",
                ForgeVersion.getVersion(),
                MCPVersion.getMCVersion(),
                MCPVersion.getMCPVersion());
        final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::preInit);
        modEventBus.addListener(this::doClientStuff);
        ModLoadingContext.get().registerConfig(net.minecraftforge.fml.config.ModConfig.Type.CLIENT, ModSettings.spec);
        // если поставить здесь ForgeConfig.class (как во многих примерах), то НЕ будут вызываться event-ы Config.class
        modEventBus.register(ModSettings.class);
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

        MinecraftForge.EVENT_BUS.register(new OnKeyInput());
        MinecraftForge.EVENT_BUS.register(new OnClientTick());
        MinecraftForge.EVENT_BUS.register(new OnRenderTick());
        MinecraftForge.EVENT_BUS.register(new OnRenderGameOverlay());

        //нет событий у этого объекта, см. https://www.minecraftforge.net/forum/topic/68011-config-not-working/?tab=comments#comment-328444
        //MinecraftForge.EVENT_BUS.register(this);
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
}
