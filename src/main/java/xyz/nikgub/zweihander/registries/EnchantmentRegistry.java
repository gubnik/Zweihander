package xyz.nikgub.zweihander.registries;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import xyz.nikgub.zweihander.Zweihander;
import xyz.nikgub.zweihander.items.ZweihanderItem;

public class EnchantmentRegistry {
    public static final DeferredRegister<Enchantment> ENCHANTMENTS = DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, Zweihander.MOD_ID);

    public static final EnchantmentCategory ZWEIHANDER_CATEGORY = EnchantmentCategory.create("zweihander", (item -> item instanceof ZweihanderItem));

    public static final RegistryObject<Enchantment> WEIGHT = ENCHANTMENTS.register("weight",
            () -> new Enchantment(Enchantment.Rarity.COMMON, ZWEIHANDER_CATEGORY, new EquipmentSlot[]{}) {
                @Override
                public int getMaxLevel()
                { return 5; }
            });

    public static final RegistryObject<Enchantment> POISE = ENCHANTMENTS.register("poise",
            () -> new Enchantment(Enchantment.Rarity.COMMON, ZWEIHANDER_CATEGORY, new EquipmentSlot[]{}) {
                @Override
                public int getMaxLevel()
                { return 5; }
            });

    public static final RegistryObject<Enchantment> GIANT = ENCHANTMENTS.register("giant",
            () -> new Enchantment(Enchantment.Rarity.UNCOMMON, ZWEIHANDER_CATEGORY, new EquipmentSlot[]{}) {
                @Override
                public int getMaxLevel()
                { return 3; }
            });

    public static final RegistryObject<Enchantment> CURSE_OF_CHAOS = ENCHANTMENTS.register("curse_of_chaos",
            () -> new Enchantment(Enchantment.Rarity.COMMON, ZWEIHANDER_CATEGORY, new EquipmentSlot[]{}) {
                @Override
                public int getMaxLevel()
                { return 1; }
                @Override
                public boolean isTreasureOnly()
                { return true; }
                @Override
                public boolean isCurse()
                { return true; }
            });
}
