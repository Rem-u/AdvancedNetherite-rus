package com.autovw.advancednetherite.datagen;

import com.autovw.advancednetherite.Reference;
import com.autovw.advancednetherite.api.annotation.Internal;
import com.autovw.advancednetherite.datagen.providers.*;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.concurrent.CompletableFuture;

/**
 * Author: Autovw
 */
@Internal
@Mod.EventBusSubscriber(modid = Reference.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModDataGenerator {
    private ModDataGenerator() {
    }

    /**
     * A data generator which will generate json files such as item models, blockstates, recipes etc.
     *
     * @param event Event fired by Forge, which will start the data generator
     */
    @SuppressWarnings("unused")
    @SubscribeEvent
    public static void onGatherData(final GatherDataEvent event) {
        // TODO get data generator working again
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
        ExistingFileHelper helper = event.getExistingFileHelper();
        ModBlockTagsProvider blockTagsProvider = new ModBlockTagsProvider(packOutput, lookupProvider, Reference.MOD_ID, helper);

        // server
        generator.addProvider(event.includeServer(), blockTagsProvider);
        generator.addProvider(event.includeServer(), new ModItemTagsProvider(packOutput, lookupProvider, blockTagsProvider, Reference.MOD_ID, helper));
        generator.addProvider(event.includeServer(), new ModRecipeProvider(packOutput));
        //generator.addProvider(event.includeServer(), new ModLootTableProvider(packOutput)); // TODO fix loot table provider
        generator.addProvider(event.includeServer(), new ModAdvancementProvider(packOutput, lookupProvider, helper));
        generator.addProvider(event.includeServer(), new ModLootModifierProvider(generator, Reference.MOD_ID));

        // client
        generator.addProvider(event.includeClient(), new ModBlockStatesProvider(generator, Reference.MOD_ID, helper));
        generator.addProvider(event.includeClient(), new ModItemModelProvider(generator, Reference.MOD_ID, helper));
    }
}
