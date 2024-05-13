package xyz.nikgub.zweihander.data;

import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.SoundDefinition;
import net.minecraftforge.common.data.SoundDefinitionsProvider;
import xyz.nikgub.zweihander.Zweihander;
import xyz.nikgub.zweihander.common.registries.SoundEventRegistry;

public class SoundsDefinitionsDatagen extends SoundDefinitionsProvider {

    public SoundsDefinitionsDatagen(PackOutput output, ExistingFileHelper helper) {
        super(output, Zweihander.MOD_ID, helper);
    }

    @Override
    public void registerSounds() {
        this.add(SoundEventRegistry.MUSKET_SHOT.getId(), SoundDefinition.definition().subtitle("zweihander.subtitle.musket_shot").replace(true).with(sound("zweihander:musket_shot")));
        this.add(SoundEventRegistry.MUSKET_LOAD.getId(), SoundDefinition.definition().subtitle("zweihander.subtitle.musket_load").replace(true).with(sound("zweihander:musket_load")));
    }
}
