package avttrue.informator.data;

import java.util.ArrayList;

import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;


public class CollectedEnchantmentsData
{
    public Data data = new Data();

    public class HeldItem
    {
        public boolean known;
        public boolean isEnchanted;
        public int id;
        public ArrayList<String> enchants;
        public Rarity rarity;
        HeldItem()
        {
            storeChanged(null);
        }
        HeldItem(ItemStack stack)
        {
            storeChanged(stack);
        }
        public boolean isChanged(ItemStack stack)
        {
            if (stack == null)
            {
                if (!known) return false;
                return true;
            }
            else
            {
                if (!known) return true;
                // далее требуется проверка содержимого стека
                final Item item = stack.getItem();
                if (Item.getIdFromItem(item) != id) return true;
                if (stack.hasTag() != isEnchanted) return true; // быстрый способ проверки наличия списка чар, все прочие - это медленная работа со строками
                final ArrayList<String> enchants = getItemEnchants(stack);
                if ((enchants == null) ^ (this.enchants == null)) return true;
                if ((enchants != null) && !enchants.equals(this.enchants)) return true;
                return false;
            }
        }
        public void storeChanged(ItemStack stack)
        {
            known = stack != null;
            if (!known) return;
            final Item item = stack.getItem();
            enchants = getItemEnchants(stack);
            isEnchanted = (enchants != null) && !enchants.isEmpty();
            if (!isEnchanted) return;
            id = Item.getIdFromItem(item);
            rarity = item.getRarity(stack);
        }
    }

    public class Data
    {
        // признак того, что собранными данными можно пользоваться (иначе считаются невалидными)
        public boolean valid = false;
        // данные полученные из мира
        public static final int MAINHAND = 0; // рука основная
        public static final int OFFHAND = 1; // рука вторая
        public static final int HEAD = 2; // голова
        public static final int BODY = 3; // тело
        public static final int LEGS = 4; // штаны
        public static final int FOOTS = 5; // ботинки
        public static final int MAX_HELD_ITEMS = 6;
        public HeldItem [] held = {
                new HeldItem(),
                new HeldItem(),
                new HeldItem(),
                new HeldItem(),
                new HeldItem(),
                new HeldItem()
        };
        // данные, вычисленные в результате анализа
        private long lastUpdateRlTime;
        public ArrayList<HeldItem> held_enchanted = new ArrayList<HeldItem>();
    }

    public void collectDataDuringTick(long realTimeTick, boolean inHands, boolean onBody)
    {
        final Minecraft mc = Minecraft.getInstance();
        final ClientWorld world = mc.world;
        final PlayerEntity player = mc.player;
        // если игра ещё не начата вдруг
        if (world == null || player == null)
        {
            if (data.valid)
            {
                data.valid = false;
                for (int i = 0; i < Data.MAX_HELD_ITEMS; ++i) data.held[i].known = false;
                data.held_enchanted.clear();
            }
            return;
        }
        // прореживаем обновления данных: раз в 10*50=500ms, чаще не надо
        // простецким образом проверяем переходы счётчиков через 0
        if (Math.abs(realTimeTick - data.lastUpdateRlTime) < 10) return;
        data.lastUpdateRlTime = realTimeTick;

        ItemStack [] stacks = {
                inHands ? player.getHeldItemMainhand() : null, // mainhand
                inHands ? player.getHeldItemOffhand() : null, // offhand
                onBody ? player.inventory.armorItemInSlot(3) : null, // head
                onBody ? player.inventory.armorItemInSlot(2) : null, // body
                onBody ? player.inventory.armorItemInSlot(1) : null, // legs
                onBody ? player.inventory.armorItemInSlot(0) : null // foots
        };

        // проверяем весь список надетых вещей, ищем изменения в ранее сохранённой информации
        boolean anyEnchantmentChanged = false;
        for (int i = 0; i < Data.MAX_HELD_ITEMS; ++i)
        {
            HeldItem held = data.held[i];
            if (!held.isChanged(stacks[i])) continue;
            held.storeChanged(stacks[i]);
            anyEnchantmentChanged = true;
        }
        // раз изменился какой-либо элемент в списке, то надо пересобрать весь список (важен порядок)
        if (anyEnchantmentChanged)
        {
            data.held_enchanted.clear();
            for (int i = 0; i < CollectedHeldItemsData.Data.MAX_HELD_ITEMS; ++i)
            {
                HeldItem held = data.held[i];
                if (!data.held[i].known) continue;
                if (!data.held[i].isEnchanted) continue;
                data.held_enchanted.add(held);
            }
        }

        data.valid = true;
    }

    @SuppressWarnings("deprecation") // код взят из исходников майна, значит так и должно быть
    private static ArrayList<String> getItemEnchants(ItemStack stack)
    {
        ArrayList<String> list = new ArrayList<String>();
        try 
        {
            if (stack == null) return null;
            if (!stack.hasTag()) return null;
            if (!stack.isEnchanted()) return null;
            ListNBT enchants = stack.getEnchantmentTagList();
            if (enchants == null) return null;
            if (enchants.isEmpty()) return null;
            for (int i = 0; i < enchants.size(); i++) 
            {
                CompoundNBT enchant = enchants.getCompound(i);
                Registry.ENCHANTMENT.getValue(ResourceLocation.tryCreate(enchant.getString("id"))).ifPresent(
                        (itxtcmp) -> { list.add(itxtcmp.getDisplayName(enchant.getInt("lvl")).getString()); }
                );
            }
            return list;
        }
        catch (Exception e) 
        {
            System.out.println(e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}
