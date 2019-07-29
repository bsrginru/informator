package avttrue.informator.tools.usernames;

import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Nullable;

import org.apache.commons.io.IOUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/*
 * Этот класс извлекает данные игрока (имя) по UUID с сайта Можангов 
 * https://api.mojang.com/user/profiles/<uuid>/names
 * 
 * типичное содержимое из https://api.mojang.com/user/profiles/20d6918de3e74a69a83d39d13a6285ec/names :
 *
 * [{"name":"De_Tumba"},{"name":"Mainstay","changedToAt":1430123460000},{"name":"De_Tumba","changedToAt":1432843313000},
 * {"name":"John_Osterman","changedToAt":1435519476000},{"name":"De_Tumba","changedToAt":1440061635000},
 * {"name":"John_Osterman","changedToAt":1453119842000},{"name":"De_Tumba","changedToAt":1456093556000},
 * {"name":"AlexColmix","changedToAt":1483122262000},{"name":"De_Tumba","changedToAt":1485721007000}]
 */
public class UsernamesLoader extends Thread
{
    private String profilesURL = "https://api.mojang.com/user/profiles/%1$s/names";
    private volatile String uuid = null;
    private UsernamesCache cache = null;
    private int checkInterval = 500; // интервал опроса

    public UsernamesLoader(UsernamesCache cache)
    {
        this.cache = cache;
    }

    public synchronized void setUUID(String uuid)
    {
        this.uuid = uuid;
    }

    @Nullable
    private synchronized String getUUID()
    {
        if (this.uuid == null) return null;
        if (this.uuid.isEmpty()) return null;
        final String uuid = this.uuid;
        this.uuid = null;
        return uuid;
    }

    public void run()
    {
        while(true)
        {
            try
            {
                // копируем uuid в локальное хранилище, если по нему есть данные - работаем; и даём его снова менять снаружи
                final String uuid = getUUID();
                if (uuid != null)
                    ReadDataFromWeb(uuid);
                sleep(checkInterval);
            }
            catch (Exception e)
            {
                System.out.println(e.getMessage());
                e.printStackTrace();
            }
        }
    }

    // лезем на сайт и читаем данные профиля
    private void ReadDataFromWeb(final String uuid)
    {
        InputStream inputstream = null;
        try
        {
            final String url = String.format(profilesURL, uuid.replace("-", ""));
            System.out.println("\nRead gamer name from: " + url);
            inputstream = new URL(url).openStream();
            if (inputstream == null) return;
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }

        try
        {
            final String profile = IOUtils.toString(inputstream, StandardCharsets.UTF_8);
            cache.addProfile(uuid, GetNameFromProfile(profile));
            //выполняется автоматически:cache.writeToFile();
        }
        catch (Exception e)
        {
            // не смогли прочесть с сайта по УРЛу, скорее всего UUID фейковый
            //System.out.println(e.getMessage());
            //e.printStackTrace();
        }
        finally
        {
            IOUtils.closeQuietly(inputstream);
        }
    }

    // расшифровывание имени из json профиля
    @Nullable
    private String GetNameFromProfile(@Nullable final String profile)
    {
        if (profile == null || profile.isEmpty()) return null;
        String Name = null;
        try
        {
            final Gson gson = new Gson(); //https://github.com/google/gson
            final Type type = new TypeToken<ArrayList<ProfileWebData>>(){}.getType();
            final List<ProfileWebData> listPWD = gson.fromJson(profile, type);
            long changedToAt = -1;
            Iterator<ProfileWebData> iterator = listPWD.iterator();
            while (iterator.hasNext())
            {
                ProfileWebData pwd = iterator.next();
                if (changedToAt < pwd.changedToAt) // берём с самой свежей датой
                    Name = pwd.name;
            }
            System.out.println("\nGamer name: " + Name);
            return Name;
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    // используется для извлечения данных о профиле игрока с сайта можангов
    // пример https://api.mojang.com/user/profiles/20d6918de3e74a69a83d39d13a6285ec/names
    private class ProfileWebData 
    {
        public String name = null;
        public long changedToAt = 0;
    }
}

