package com.swordglowsblue.faeriefire;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.block.ColoredBlock;
import net.minecraft.entity.EntityContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.IntegerProperty;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import java.util.Random;

@SuppressWarnings("deprecation")
public class FaerieFireBlock extends Block implements ColoredBlock {
    private static final BooleanProperty LOCKED = BooleanProperty.create("locked");
    private static final BooleanProperty VISIBLE = BooleanProperty.create("visible");
    private static final IntegerProperty LIGHT_LEVEL = IntegerProperty.create("light_level", 1, 15);

    public FaerieFireBlock(DyeColor color) {
        super(FabricBlockSettings.of(new Material(
            color.getMaterialColor(),
            false, // isLiquid
            false, // isSolid
            false, // blocksMovement
            false, // isOpaque
            true,  // requiresNoTool
            false, // flammable
            false, // replaceable
            PistonBehavior.NORMAL))
            .collidable(false)
            .hardness(0)
            .resistance(0)
            .sounds(BlockSoundGroup.GLASS)
            .build());
        setDefaultState(getStateFactory().getDefaultState()
            .with(LOCKED, false)
            .with(VISIBLE, true)
            .with(LIGHT_LEVEL, 15));
        this.color = color;
    }

    private DyeColor color;
    public DyeColor getColor() { return this.color; }

    public boolean activate(BlockState bs, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hitResult) {
        if(player.isSneaking()) {
            world.setBlockState(pos, bs.cycle(LOCKED));
            player.addChatMessage(new TranslatableComponent("faeriefire.notif.lock."+!bs.get(LOCKED)), true);
            return true;
        }
        if(bs.get(LOCKED)) return false;

        if(player.getStackInHand(hand).getItem() instanceof FlintAndSteelItem) {
            world.setBlockState(pos, bs.cycle(VISIBLE));
            if(!player.isCreative() && !world.isClient)
                player.getStackInHand(hand).applyDamage(1, player, (pe) -> pe.sendToolBreakStatus(hand));
            player.addChatMessage(new TranslatableComponent("faeriefire.notif.visible."+!bs.get(VISIBLE)), true);
        } else if(player.getStackInHand(hand).getItem() instanceof DyeItem) {
            DyeItem dye = (DyeItem)player.getStackInHand(hand).getItem();
            if(dye.getColor() == this.color) return false;

            world.setBlockState(pos, FaerieFire.blockFaerieFire.get(dye.getColor()).getDefaultState()
                .with(LOCKED, bs.get(LOCKED))
                .with(VISIBLE, bs.get(VISIBLE))
                .with(LIGHT_LEVEL, bs.get(LIGHT_LEVEL)));
            if(!player.isCreative()) player.getStackInHand(hand).subtractAmount(1);
            player.addChatMessage(new TranslatableComponent("faeriefire.notif.color",
                new TranslatableComponent("color.minecraft."+dye.getColor()).getText()), true);
        } else {
            world.setBlockState(pos, bs.cycle(LIGHT_LEVEL));
            player.addChatMessage(new TranslatableComponent("faeriefire.notif.light_level", bs.cycle(LIGHT_LEVEL).get(LIGHT_LEVEL)), true);
        }

        return true;
    }

    public BlockRenderType getRenderType(BlockState bs) { return BlockRenderType.INVISIBLE; }
    public VoxelShape getOutlineShape(BlockState bs, BlockView bv, BlockPos pos, EntityContext ec) {
        return VoxelShapes.cuboid(0.35d, 0.35d, 0.35d, 0.65d, 0.65d, 0.65d); }

    public int getLuminance(BlockState bs) { return bs.get(LIGHT_LEVEL); }
    protected void appendProperties(StateFactory.Builder sf) { sf.add(LOCKED, VISIBLE, LIGHT_LEVEL); }

    @Environment(EnvType.CLIENT)
    public void randomDisplayTick(BlockState bs, World world, BlockPos pos, Random random) {
        if(bs.get(VISIBLE)) {
            double x = pos.getX() + 0.5d;
            double y = pos.getY() + 0.5d;
            double z = pos.getZ() + 0.5d;
            MinecraftClient.getInstance().particleManager
                .addParticle(new FaerieFireParticle(world, x, y, z, this.color));
        }
    }
}
