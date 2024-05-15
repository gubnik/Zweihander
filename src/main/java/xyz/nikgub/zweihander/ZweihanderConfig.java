package xyz.nikgub.zweihander;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

@Mod.EventBusSubscriber(modid = Zweihander.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ZweihanderConfig
{
    private static final ForgeConfigSpec.Builder CLIENT_BUILDER = new ForgeConfigSpec.Builder();

    static final ForgeConfigSpec CLIENT = CLIENT_BUILDER.build();

    private static final ForgeConfigSpec.Builder COMMON_BUILDER = new ForgeConfigSpec.Builder();

    private static final ForgeConfigSpec.ConfigValue<Integer> DEFAULT_CONTRACT_CREDITS =
            COMMON_BUILDER.comment("Defines the amount of credits the director gets when activating Accursed Contract")
                    .define("default_contract_credits", 60);
    private static final ForgeConfigSpec.ConfigValue<Float> DEFAULT_MUSKET_DAMAGE =
            COMMON_BUILDER.comment("Defines the default damage of a single musket shot")
                    .define("default_musket_damage", 16f);
    private static final ForgeConfigSpec.ConfigValue<Integer> DEFAULT_MUSKET_RANGE =
            COMMON_BUILDER.comment("Defines the default number of iterations in a single musket shot, 1 iteration = 0.2 blocks")
                    .define("default_musket_range", 150);
    private static final ForgeConfigSpec.ConfigValue<Float> MUSKET_DAMAGE_CAP =
            COMMON_BUILDER.comment("Defines the maximum amount of damage a single musket shot can deal")
                    .define("musket_damage_cap", 50f);


    static final ForgeConfigSpec COMMON = COMMON_BUILDER.build();

    public static int defaultContractCredits;
    public static float defaultMusketDamage;
    public static int defaultMusketRange;
    public static float musketDamageCap;

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event)
    {
        defaultContractCredits = DEFAULT_CONTRACT_CREDITS.get();
        defaultMusketDamage = DEFAULT_MUSKET_DAMAGE.get();
        defaultMusketRange = DEFAULT_MUSKET_RANGE.get();
        musketDamageCap = MUSKET_DAMAGE_CAP.get();
    }
}
