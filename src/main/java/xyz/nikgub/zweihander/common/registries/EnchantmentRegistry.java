package xyz.nikgub.zweihander.common.registries;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import xyz.nikgub.zweihander.Zweihander;
import xyz.nikgub.zweihander.common.items.MusketItem;
import xyz.nikgub.zweihander.common.items.ZweihanderItem;

public class EnchantmentRegistry {
    public static final DeferredRegister<Enchantment> ENCHANTMENTS = DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, Zweihander.MOD_ID);

    public static final EnchantmentCategory ZWEIHANDER_CATEGORY = EnchantmentCategory.create("zweihander", (item -> item instanceof ZweihanderItem));
    public static final EnchantmentCategory MUSKET_CATEGORY = EnchantmentCategory.create("musket", (item -> item instanceof MusketItem));
    public static final EnchantmentCategory SHIELD_CATEGORY = EnchantmentCategory.create("shield", (item -> item instanceof ShieldItem));

    // Shield enchantments

    public static final RegistryObject<Enchantment> REPULSION = ENCHANTMENTS.register("repulsion",
            () -> new Enchantment(Enchantment.Rarity.COMMON, SHIELD_CATEGORY, new EquipmentSlot[]{}) {
                @Override
                public int getMaxLevel()
                { return 2; }
            });

    // Zweihander enchantments

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

    public static final RegistryObject<Enchantment> TROOPER = ENCHANTMENTS.register("trooper",
            () -> new Enchantment(Enchantment.Rarity.UNCOMMON, MUSKET_CATEGORY, new EquipmentSlot[]{}) {
                @Override
                public int getMaxLevel()
                { return 1; }
                @Override
                public boolean isTreasureOnly()
                { return true; }
            });

    public static class Utils
    {
        public static float tryCurseOfChaos (LivingEntity target)
        {
            if (target instanceof Player) {
                target.setSecondsOnFire(5);
                return 1.25F;
            }
            return 0.5F;
        }

        public static void tryRepulsion (LivingEntity target, LivingEntity source, ItemStack mainHandItem)
        {
            if (target.isBlocking() && target.getUseItem().getEnchantmentLevel(EnchantmentRegistry.REPULSION.get()) > 0)
            {
                final double mod = 1.2 + mainHandItem.getEnchantmentLevel(EnchantmentRegistry.REPULSION.get())/1.25f;
                source.knockback(mod, Math.sin(target.getYRot() * ((float)Math.PI / 180F)), -Math.cos(target.getYRot() * ((float)Math.PI / 180F)));
                Zweihander.Utils.coverInParticles(source, ParticleTypes.ENCHANTED_HIT, 0.1);
            }
        }
    }
}
