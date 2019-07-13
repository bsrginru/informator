package avttrue.informator.data;

import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;

import avttrue.informator.Informator;


public class CollectedWeatherData
{
    public Data data = new Data();

    public class Data
    {
        // признак того, что собранными данными можно пользоваться (иначе считаются невалидными)
        public boolean valid = false;
        // данные полученные из мира
        public int moonPhase; // фаза луны (0 - полнолуние, 1 - убывающая луна, 4 - новолуние, ...)
        public boolean isThundering;
        public boolean isRaining;
        // данные, вычисленные в результате анализа
        private long lastUpdateRlTime;
        public String sMoonPhase;
        public String sMoonPhaseFactor;
    }

    public void collectDataDuringTick(long realTimeTick)
    {
        final Minecraft mc = Minecraft.getInstance();
        final ClientWorld world = mc.world;
        // если игра ещё не начата вдруг
        if (world == null)
        {
            data.valid = false;
            return;
        }

        data.moonPhase = world.getMoonPhase();
        data.isThundering = mc.world.isThundering();
        data.isRaining = mc.world.isRaining();

        // прореживаем обновления данных (раз в где-то 8*50=400ms), чаще не надо;
        // простецким образом проверяем переход счётчика через 0
        if (Math.abs(realTimeTick - data.lastUpdateRlTime) < 8) return;
        data.lastUpdateRlTime = realTimeTick;

        refreshCalculatedData();
    }
    
    public void refreshCalculatedData()
    {
        data.sMoonPhase = Informator.TRANSLATOR.field_moon_phase.getFormattedText() + ": ";
        data.sMoonPhaseFactor = Informator.TRANSLATOR.field_moon_phases[data.moonPhase].getFormattedText();
        data.valid = true;
    }
}
