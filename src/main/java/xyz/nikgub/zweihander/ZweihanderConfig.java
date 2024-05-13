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
            COMMON_BUILDER.define("default_contract_credits", 60);

    static final ForgeConfigSpec COMMON = COMMON_BUILDER.build();

    public static int defaultContractCredits;

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event)
    {
        defaultContractCredits = DEFAULT_CONTRACT_CREDITS.get();
    }
}
