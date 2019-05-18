package com.swordglowsblue.faeriefire;

import net.fabricmc.api.ModInitializer;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.DyeColor;
import net.minecraft.util.registry.Registry;

import java.util.HashMap;

public class FaerieFire implements ModInitializer {
    public static final HashMap<DyeColor, FaerieFireBlock> blockFaerieFire = new HashMap<>();
    public static final DefaultParticleType particleTypeFaerieFire = new DefaultParticleType(false) {};

    @Override public void onInitialize() {
        for(DyeColor color : DyeColor.values()) {
            FaerieFireBlock block = new FaerieFireBlock(color);
            Registry.register(Registry.BLOCK, "faeriefire:"+color.getName()+"_faeriefire", block);
            Registry.register(Registry.ITEM, "faeriefire:"+color.getName()+"_faeriefire",
                new BlockItem(block, new Item.Settings().itemGroup(ItemGroup.DECORATIONS)));
            blockFaerieFire.put(color, block);
        }

        Registry.register(Registry.PARTICLE_TYPE, "faeriefire:faeriefire", particleTypeFaerieFire);
    }
}
