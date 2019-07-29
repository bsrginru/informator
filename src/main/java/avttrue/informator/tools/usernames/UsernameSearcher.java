package avttrue.informator.tools.usernames;

import java.util.UUID;

import javax.annotation.Nullable;

import net.minecraftforge.common.UsernameCache;

import avttrue.informator.config.ModSettings;

public class UsernameSearcher
{
    // возвращает имя по UUID
    @Nullable
    public String getUsernameByUUID(@Nullable final UUID uuid) 
    {
        //uuid = UUID.fromString("20d6918d-e3e7-4a69-a83d-39d13a6285ec"); // для проверки
        if (uuid == null) return null;
        // ищем в кэше клиента (кеш forge)
        String username = UsernameCache.getLastKnownUsername(uuid);
        // ищем в 'нашем' кэше (аналогичный по устройству кеш, но с настраиваемым временем хранения)
        final String uuidStr = uuid.toString();
        if (username == null)
        {
            // поиск в 'нашем' кеше
            username = cachedProfilesFromWeb.findNameByUUID(uuidStr);
            // ищем на сайте моджангов
            if (username == null && ModSettings.TARGET.TargetMobBar_SeachOwnerInWeb.get() && usernamesLoader != null)
            {
                usernamesLoader.setUUID(uuid.toString());
                // в течении N-миллисекунд будет выполнена загрузка данных о персонаже,
                // причём загруженные данные появятся в кеше cachedProfilesFromWeb
            }
        }
        else
        {
            // добавление данных в 'наш' кеш из кэша forge
            String cached_username = cachedProfilesFromWeb.findNameByUUID(uuidStr);
            if (cached_username == null)
                cachedProfilesFromWeb.addProfile(uuidStr, username);
        }
        return username;
    }

    // используется для кэширования результатов поиска игроков на сайте
    private volatile UsernamesCache cachedProfilesFromWeb = new UsernamesCache();
    // поток проверки имён на api.mojang.com (запустится при первом считывании данных из .toml-файла)
    public UsernamesLoader usernamesLoader = null;
    public Thread usernamesLoaderThread = null;

    // синглтон утилит получения данных по профилям пользователей
    private static final UsernameSearcher instance = new UsernameSearcher();
    public static UsernameSearcher getInstance()
    {
        // метод вызывается из события рендеринга пользовательского интерфейса,
        // а также из события загрузки настроек мода из .toml-файла
        return instance;
    }

    public void initialization(final String modConfigFileName)
    {
        // если инициализация уже выполнялась, то предотвращаем её повторное выполнение
        if (usernamesLoader != null) return;
        // загружаем кашированные ранее профили
        cachedProfilesFromWeb.initialization(modConfigFileName);
        // стартует поток проверки имён пользователей с сайта
        usernamesLoader = new UsernamesLoader(cachedProfilesFromWeb);
        usernamesLoaderThread = new Thread(usernamesLoader, "Profile Web Checker");
        usernamesLoaderThread.setPriority(Thread.MIN_PRIORITY);
        usernamesLoaderThread.start();
    }
}
