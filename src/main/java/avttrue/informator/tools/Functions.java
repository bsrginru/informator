package avttrue.informator.tools;

import java.util.UUID;

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.NewChatGui;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;

import net.minecraftforge.common.UsernameCache;

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
}
