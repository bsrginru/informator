package avttrue.informator.tools;

import java.util.UUID;

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.NewChatGui;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;

import net.minecraftforge.common.UsernameCache;
import net.minecraftforge.fml.ForgeI18n;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.VersionChecker;
import net.minecraftforge.fml.VersionChecker.CheckResult;
import net.minecraftforge.forgespi.language.IModInfo;
import net.minecraftforge.versions.forge.ForgeVersion;

import avttrue.informator.Informator;

public class Functions
{
    // сообщение в чат текущему игроку
    public void SendMessageToUser(ITextComponent text)
    {
        final Minecraft mc = Minecraft.getInstance();
        if (mc == null || mc.ingameGUI == null) return;
        final NewChatGui chat = mc.ingameGUI.getChatGUI();
        if (chat == null) return;
        chat.printChatMessage(text);
    }

    // сообщение в чат текущему игроку
    public void SendMessageToUser(ITextComponent text, Style style)
    {
        final Minecraft mc = Minecraft.getInstance();
        if (mc == null || mc.ingameGUI == null) return;
        final NewChatGui chat = mc.ingameGUI.getChatGUI();
        if (chat == null) return;
        if (style != null)
            text.setStyle(style);
        chat.printChatMessage(text);
    }

    // сообщение в чат текущему игроку
    public void SendFatalErrorToUser(ITextComponent text)
    {
        final Minecraft mc = Minecraft.getInstance();
        if (mc == null || mc.ingameGUI == null) return;
        final NewChatGui chat = mc.ingameGUI.getChatGUI();
        if (chat == null) return;
        Style style = new Style();
        style.setColor(TextFormatting.DARK_RED);
        text.setStyle(style);
        chat.printChatMessage(text);
    }

    // синглтон утилит
    private static final Functions instance = new Functions();
    public static Functions getInstance()
    {
        return instance;
    }

    // TODO возвращает имя по UUID
    @Nullable
    public static String getUsernameByUUID(@Nullable final UUID uuid) 
    {
        //uuid = UUID.fromString("20d6918d-e3e7-4a69-a83d-39d13a6285ec"); // для проверки
        if (uuid == null) return null;

        String username = null;
/*
        // ищем в нашем кэше
        String username = Informator.ProfileCashListFromWeb.FindNameByUUID(uuid.toString());
        // ищем на сайте
        if (Informator.TargetMobBar_SeachOwnerInWeb && username == null)
            Informator.PWC.SetUUID(uuid.toString());
*/

        // ищем в кэше клиента
        if (username == null)
            username = UsernameCache.getLastKnownUsername(uuid);

        return username;
    }

    public boolean versionChecked = false;
    private long checkVersionFirstTick = 0;

    public void checkVersion()
    {
        // если игра ещё не активна
        if (!Minecraft.getInstance().isGameFocused() || Minecraft.getInstance().player == null)  return;
        // начинаем отсчёт в 3 секунды
        if (checkVersionFirstTick == 0) { checkVersionFirstTick = Informator.realTimeTick; return; }
        // проверяем, что разница более чем в 10*300=3000ms
        // простецким образом проверяем переход счётчика через 0
        if (Math.abs(Informator.realTimeTick - checkVersionFirstTick) < 300) return;
        versionChecked = true;
        // получаем информации о ерсии мода (здесь она не грузится... а УЖЕ готова, т.к. считывалась с серверов по время загрузки клиента)
        IModInfo mod = ModList.get().getModFileById(Informator.MODID).getMods().get(0);
        CheckResult res = VersionChecker.getResult(mod);
        if (res == null) return; // проблемы с получением информации по указанному MODID? сменился в .toml файле?
        switch (res.status)
        {
        default:
        case FAILED: // невозможно подключиться к указанному url
        case UP_TO_DATE: // текущая версия равна последней стабильной версии, или новее
            return;
        case PENDING: // запрошенный результат еще не завершен (повторяем проверку через 3 сек)
            versionChecked = false;
            checkVersionFirstTick = 0;
            return;
        case OUTDATED: // есть новая стабильная версия
        case BETA_OUTDATED: // существует новая нестабильная версия
        case BETA: // текущая версия равна или новее последней нестабильной версии
            //return;
            break;
        }
        final String ver = (res.target == null) ? "???" : res.target.toString(); // м.б. null
        ITextComponent comp = new StringTextComponent(ForgeI18n.parseMessage(Informator.TRANSLATOR.field_outdated.getFormattedText(),
                ver,
                mod.getDisplayName(),
                ForgeVersion.getVersion(),
                res.url));
        Style style = comp.getStyle();
        style.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, res.url));
        style.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Informator.TRANSLATOR.field_click_to_download));
        comp.setStyle(style);
        SendMessageToUser(comp);
    }
}
