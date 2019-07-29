package avttrue.informator.tools;

import java.util.UUID;

import javax.annotation.Nullable;

import net.minecraftforge.common.UsernameCache;

public class UsernameSearcher
{
    public static void initialization(final String modConfigFileName)
    {
        cachedProfilesFromWeb.initialization(modConfigFileName);
    }

    // возвращает имя по UUID
    @Nullable
    public static String getUsernameByUUID(@Nullable final UUID uuid) 
    {
        //uuid = UUID.fromString("20d6918d-e3e7-4a69-a83d-39d13a6285ec"); // для проверки
        if (uuid == null) return null;

        // ищем в кэше клиента (кеш forge)
        String username = UsernameCache.getLastKnownUsername(uuid);

        // ищем в нашем кэше (аналогичный по устройству кеш, но с ограниченным временем хранения)
        final String uuidStr = uuid.toString();
        if (username == null)
            username = cachedProfilesFromWeb.findNameByUUID(uuidStr);
        else
        {
            String cached_username = cachedProfilesFromWeb.findNameByUUID(uuidStr);
            if (cached_username == null)
                cachedProfilesFromWeb.addProfile(uuidStr, username);
        }
/*
        // ищем на сайте
        if (Informator.TargetMobBar_SeachOwnerInWeb && username == null)
            Informator.PWC.SetUUID(uuid.toString());
*/

        return username;
    }

    // используется для кэширования результатов поиска игроков на сайте
    private volatile static UsernamesCache cachedProfilesFromWeb = new UsernamesCache();
}
