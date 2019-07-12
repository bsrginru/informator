package avttrue.informator.data;

import java.util.Date;

import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;

public class CollectedClockData
{
    public Data data = new Data();

    public class Data
    {
        // признак того, что собранными данными можно пользоваться (иначе считаются невалидными)
        public boolean valid = false;
        // данные полученные из мира
        public long worldTime = -1;
        public long wakeUpTime = -1;
        // данные, вычисленные в результате анализа
        public long mcTimeSec; // секунд натикало в текущем дне
        public long mcTimeHour; // минут
        public long mcTimeMin; // часов
        public TimeOfDay timeOfDay = TimeOfDay.UNKNOWN;
        public String currentTime;
        public boolean restTimeHourOverhead;
    }

    public void collectDataDuringTick()
    {
        final Minecraft mc = Minecraft.getInstance();
        final ClientWorld world = mc.world;
        // если игра ещё не начата вдруг
        if (world == null)
        {
            data.valid = false;
            data.worldTime = -1;
            data.timeOfDay = TimeOfDay.UNKNOWN;
            return;
        }
        // вычисляем время
        PlayerEntity player = mc.player;
        if (player.dimension.getId() == 0) // 0 в обычном мире, -1 в аду, 1 в енде
        {
            data.worldTime = world.dimension.getWorldTime();
            if (player.isSleeping()) // поскольку в аду спать нельзя, то точка сохранения параметра только здесь
            {
                // Внимание! таймер сна на сервере запускается спустя 100 тиков
                data.wakeUpTime = data.worldTime;
                //final StatisticsManager stats = ((ClientPlayerEntity)player).getStats();
                //for(Stat<ResourceLocation> stat : Stats.CUSTOM) {
                //    if (stat.getValue() == Stats.TIME_SINCE_REST)
                //    {
                //        // эта статистика почему-то не обновляется... запрашивается с сервера что ли? (замирает на последнем отображённом значении)
                //        String s = stat.format(stats.getValue(stat));
                //        break;
                //    }
                //}
                //Stats.TIME_SINCE_REST.toString();
            }
        }
        else if (data.worldTime != -1) // если не в обычном мире и время уже определено
        {
            data.worldTime++;
        }
        refreshCalculatedData();
    }
    
    public void refreshCalculatedData()
    {
        // реальное время
        final Date date = new Date();
        // игровое время
        if (data.worldTime > -1) // если время определено
        {
            /* ибо:
             * http://minecraft.gamepedia.com/Time
             * 1 tick of gameplay = 3.6 Minecraft seconds
             * + 6 часов, т.к. 0-й тик = 6:00
             * 6*60*60 = 21600
             * 24*60*60 = 86400
             */
            data.mcTimeSec = (long) ((data.worldTime * 3.6 + 21600) % 86400); // секунд натикало в текущем дне
            data.mcTimeMin = (data.mcTimeSec - data.mcTimeSec % 60) / 60;
            data.mcTimeHour = (data.mcTimeMin - data.mcTimeMin % 60) / 60;
            data.mcTimeMin -= data.mcTimeHour * 60; // Нормализуем к часам

            // для справки
            data.currentTime = String.format("%1$tT | %2$02d:%3$02d", date.getTime(), data.mcTimeHour, data.mcTimeMin);

            // вычисление сведений - сейчас день или ночь?
            // чтобы не "дёргалась" погодная иконка при смене ночь->смена_фазы->день, что происходит меньше чем
            // за 1 секунду, и заметно, то начинаем выводить "дневную иконку" ещё до того, как moonPhase увеличится
            // проверку проводить на контрольных значениях (при включённом дожде):
            //  23850 - переключение на убывающую луну
            //  47850
            //  71850
            //  95850
            //  119850
            //  143850
            //  167850
            //  191850
            //  215850
            if (data.mcTimeHour == 5 && data.mcTimeMin == 59)
                data.timeOfDay = TimeOfDay.DAY; // в 5:59 считаем, что уже день
            else if ((data.mcTimeHour < 6 || data.mcTimeHour > 18))
                data.timeOfDay = TimeOfDay.NIGHT;
            else
                data.timeOfDay = TimeOfDay.DAY;
        } 
        else
        {
            // если время не определено
            data.currentTime = String.format("%1$tT | ??:??", date.getTime());
        }
        if (data.wakeUpTime > 0)
        {
            final long diff = data.worldTime - data.wakeUpTime;
            data.restTimeHourOverhead = diff >= 72000; // после 72000 тиков может активироваться спавнер Фантома
            //debug:final long restTimeSec = (long)(diff * 3.6); // секунд натикало с подъёма с кровати
            //debug:long restTimeMin = (restTimeSec - restTimeSec % 60) / 60;
            //debug:final long restTimeHour = (restTimeMin - restTimeMin % 60) / 60;
            //debug:restTimeMin -= restTimeHour * 60; // Нормализуем к часам
            //debug:data.currentTime += String.format(" | %1$02d:%2$02d", restTimeHour, restTimeMin);
        }
        data.valid = true;
    }
}
