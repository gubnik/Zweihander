package xyz.nikgub.zweihander.mob_effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

public class InfusionMobEffect extends MobEffect {

    public final static String INFUSION_TAG = "___Infusion_Item___";
    public final static String RED_TAG = "___Infusion_Red___";
    public final static String BLUE_TAG = "___Infusion_Blue___";
    public final static String GREEN_TAG = "___Infusion_Green___";
    public final static String ALPHA_TAG = "___Infusion_Alpha___";

    private final InfusionMobEffect.Colors itemColors;
    private final InfusionMobEffect.InfusionEffect effect;

    public InfusionMobEffect(MobEffectCategory effectCategory, int color, Colors itemColors, InfusionEffect effect) {
        super(effectCategory, color);
        this.itemColors = itemColors;
        this.effect = effect;
    }

    public InfusionEffect getInfusionEffect ()
    {
        return effect;
    }

    public Colors getItemColors() {
        return itemColors;
    }

    public record Colors (float r, float g, float b, float a)
    {};

    public interface InfusionEffect
    {
        void effect (LivingHurtEvent event);
    }
}
