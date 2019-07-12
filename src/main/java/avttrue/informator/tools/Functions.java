package avttrue.informator.tools;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.NewChatGui;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;

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
}
