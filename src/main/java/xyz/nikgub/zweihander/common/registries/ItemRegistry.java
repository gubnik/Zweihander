package xyz.nikgub.zweihander.common.registries;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import xyz.nikgub.zweihander.Zweihander;
import xyz.nikgub.zweihander.common.entities.FlamingGuillotineEntity;
import xyz.nikgub.zweihander.common.items.*;

public class ItemRegistry {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Zweihander.MOD_ID);

    public static final RegistryObject<Item> UNBOUND_BLOOD = ITEMS.register("unbound_blood", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> BLESSED_SILVER_INGOT = ITEMS.register("blessed_silver_ingot", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> INCENSE = ITEMS.register("incense", () -> new Item(new Item.Properties()));

    public static final RegistryObject<AccursedContractItem> ACCURSED_CONTRACT = ITEMS.register("accursed_contract", () -> new AccursedContractItem(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<ZweihanderItem> ZWEIHANDER = ITEMS.register("zweihander", () -> new ZweihanderItem(new Item.Properties()));

    public static final RegistryObject<MusketItem> MUSKET = ITEMS.register("musket", () -> new MusketItem(new Item.Properties()));

    public static final RegistryObject<MusketAmmunitionItem> IRON_MUSKET_BALL = ITEMS.register("iron_musket_ball", () -> new MusketAmmunitionItem(new Item.Properties(), (source, entity) -> 1f));

    public static final RegistryObject<MusketAmmunitionItem> SILVER_MUSKET_BALL = ITEMS.register("silver_musket_ball", () -> new MusketAmmunitionItem(new Item.Properties(), (source, entity) -> {
        if (entity.getMobType() == MobType.UNDEAD) return 1.2f; else return 0.8f;
    }));

    public static final RegistryObject<MusketAmmunitionItem> INQUISITORIAL_MUSKET_BALL = ITEMS.register("inquisitorial_musket_ball", () -> new MusketAmmunitionItem(new Item.Properties(), (source, entity) ->
    {
        if (!(entity.level() instanceof ServerLevel level)) return 0.5f;
        FlamingGuillotineEntity guillotine = FlamingGuillotineEntity.createWithDamage(EntityTypeRegistry.FLAMING_GUILLOTINE.get(), level, (float) source.getAttributeValue(Attributes.ATTACK_DAMAGE) / 2);
        if (source instanceof Player player) guillotine.setPlayerUuid(player.getUUID());
        guillotine.setSize(entity.getBbWidth() / 0.6f);
        guillotine.moveTo(entity.position());
        guillotine.setYRot(source.getYRot());
        level.addFreshEntity(guillotine);
        return 0.5f;
    }));

    public static final RegistryObject<InfusionItem> FIERY_INFUSION = ITEMS.register("fiery_infusion", () -> new InfusionItem(new Item.Properties(), MobEffectRegistry.FIERY_INFUSION.get(),
            Ingredient.of(Items.BLAZE_POWDER)));
    public static final RegistryObject<InfusionItem> ICE_INFUSION = ITEMS.register("ice_infusion", () -> new InfusionItem(new Item.Properties(), MobEffectRegistry.ICE_INFUSION.get(),
            Ingredient.of(Items.BLUE_ICE)));
    public static final RegistryObject<InfusionItem> SILVER_INFUSION = ITEMS.register("silver_infusion", () -> new InfusionItem(new Item.Properties(), MobEffectRegistry.SILVER_INFUSION.get(),
            Ingredient.of(ItemRegistry.BLESSED_SILVER_INGOT.get())));
    public static final RegistryObject<InfusionItem> CREEPER_INFUSION = ITEMS.register("creeper_infusion", () -> new InfusionItem(new Item.Properties(), MobEffectRegistry.CREEPER_INFUSION.get(),
            Ingredient.of(Items.TNT)));
    public static final RegistryObject<InfusionItem> MIDAS_INFUSION = ITEMS.register("midas_infusion", () -> new InfusionItem(new Item.Properties(), MobEffectRegistry.MIDAS_INFUSION.get(),
            Ingredient.of(Items.EXPERIENCE_BOTTLE)));
    public static final RegistryObject<InfusionItem> OIL_INFUSION = ITEMS.register("oil_infusion", () -> new InfusionItem(new Item.Properties(), MobEffectRegistry.OIL_INFUSION.get(),
            Ingredient.of(Items.COAL_BLOCK)));
}
