package com.autovw.advancednetherite.common.loot;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.common.loot.LootModifier;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Random;

/**
 * Author: Autovw
 * <br/>
 * A loot modifier for adding additional drops to crop blocks.
 * See {@link com.autovw.advancednetherite.datagen.providers.ModLootModifierProvider} for example implementation.
 */
public class CropDropsLootModifier extends LootModifier {
    private final Item bonusDropItem;
    private final float bonusDropChance;
    private final int minDropAmount, maxDropAmount;

    /**
     * Constructs a LootModifier.
     *
     * @param conditionsIn the ILootConditions that need to be matched before the loot is modified.
     * @param bonusDropItem additional drop item
     * @param bonusDropChance chance of the additional item being dropped
     * @param minDropAmount the minimum amount of items to be dropped
     * @param maxDropAmount the maximum amount of items to be dropped
     */
    public CropDropsLootModifier(LootItemCondition[] conditionsIn, Item bonusDropItem, float bonusDropChance, int minDropAmount, int maxDropAmount) {
        super(conditionsIn);
        this.bonusDropItem = bonusDropItem;
        this.bonusDropChance = bonusDropChance;
        this.minDropAmount = minDropAmount;
        this.maxDropAmount = maxDropAmount;
    }

    @NotNull
    @Override
    protected List<ItemStack> doApply(List<ItemStack> generatedLoot, LootContext context) {
        ItemStack tool = context.getParamOrNull(LootContextParams.TOOL);
        BlockState blockState = context.getParamOrNull(LootContextParams.BLOCK_STATE);
        if (tool != null && blockState != null) {
            if (bonusDropChance > 0.0 && bonusDropItem != null) {
                Block block = blockState.getBlock();
                if (block instanceof CropBlock cropBlock && cropBlock.isMaxAge(blockState)) {
                    Random random = context.getRandom();
                    if (maxDropAmount >= minDropAmount && random.nextFloat() <= bonusDropChance) {
                        generatedLoot.add(new ItemStack(bonusDropItem, random.ints(minDropAmount, maxDropAmount + 1).iterator().nextInt()));
                    }
                }
            }
        }
        return generatedLoot;
    }

    public static class Serializer extends GlobalLootModifierSerializer<CropDropsLootModifier> {

        @Override
        public CropDropsLootModifier read(ResourceLocation location, JsonObject object, LootItemCondition[] ailootcondition) {
            JsonObject bonusDropObject = GsonHelper.getAsJsonObject(object, "bonus_drop");
            Item bonusDropItem = ForgeRegistries.ITEMS.getValue(new ResourceLocation(GsonHelper.getAsString(bonusDropObject, "item")));
            float bonusDropChance = GsonHelper.getAsFloat(bonusDropObject, "chance");
            int minDropAmount = GsonHelper.getAsInt(bonusDropObject, "min");
            int maxDropAmount = GsonHelper.getAsInt(bonusDropObject, "max");
            return new CropDropsLootModifier(ailootcondition, bonusDropItem, bonusDropChance, minDropAmount, maxDropAmount);
        }

        @Override
        public JsonObject write(CropDropsLootModifier instance) {
            JsonObject object = makeConditions(instance.conditions);
            JsonObject bonusDropObject = new JsonObject();
            object.add("bonus_drop", bonusDropObject);
            bonusDropObject.addProperty("item", ForgeRegistries.ITEMS.getKey(instance.bonusDropItem).toString());
            bonusDropObject.addProperty("chance", instance.bonusDropChance);
            bonusDropObject.addProperty("min", instance.minDropAmount);
            bonusDropObject.addProperty("max", instance.maxDropAmount);
            return object;
        }
    }
}