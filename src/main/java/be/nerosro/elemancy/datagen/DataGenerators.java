package be.nerosro.elemancy.datagen;

import net.neoforged.neoforge.data.event.GatherDataEvent;

public class DataGenerators {

    public static void gatherData(GatherDataEvent.Client event) {
        event.createProvider(ElemancyBlockLootProvider::new);
        event.createProvider(ElemancyRecipeProvider.Runner::new);
        event.createProvider(ElemancyBlockTagsProvider::new);
        event.createProvider(ElemancyItemTagsProvider::new);
        event.createProvider(ElemancyModelProvider::new);
    }
}
