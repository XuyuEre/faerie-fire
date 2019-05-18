package com.swordglowsblue.faeriefire;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.*;
import net.minecraft.client.render.*;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class FaerieFireParticle extends BillboardParticle {
    static final Identifier texture = new Identifier("faeriefire:textures/particle/faeriefire.png");

    public FaerieFireParticle(World world, double x, double y, double z, DyeColor color) {
        this(world, x, y, z, 0, 0, 0, color);
    }

    public FaerieFireParticle(World world, double x, double y, double z, double xvel, double yvel, double zvel, DyeColor color) {
        super(world, x, y, z, xvel, yvel, zvel);
        this.velocityX = this.velocityX * 0.009999999776482582D + xvel;
        this.velocityY = this.velocityY * 0.009999999776482582D + yvel;
        this.velocityZ = this.velocityZ * 0.009999999776482582D + zvel;
        this.x += (double) ((this.random.nextFloat() - this.random.nextFloat()) * 0.05F);
        this.y += (double) ((this.random.nextFloat() - this.random.nextFloat()) * 0.05F);
        this.z += (double) ((this.random.nextFloat() - this.random.nextFloat()) * 0.05F);
        this.maxAge = (int) (8.0D / (Math.random() * 0.8D + 0.2D)) + 10;
        this.setColor(color.getColorComponents()[0], color.getColorComponents()[1], color.getColorComponents()[2]);
    }

    public ParticleTextureSheet getType() { return ParticleTextureSheet.PARTICLE_SHEET_OPAQUE; }
    public void buildGeometry(BufferBuilder var1, Camera var2, float var3, float var4, float var5, float var6, float var7, float var8) {
        MinecraftClient.getInstance().getTextureManager().bindTexture(texture);
        super.buildGeometry(var1, var2, var3, var4, var5, var6, var7, var8);
    }

    public void move(double x, double y, double z) {
        this.setBoundingBox(this.getBoundingBox().offset(x, y, z));
        this.repositionFromBoundingBox();
    }

    public float getSize(float float_1) {
        float float_2 = ((float) this.age + float_1) / (float) this.maxAge;
        return this.scale * (1.0F - float_2 * float_2 * 0.5F);
    }

    public int getColorMultiplier(float float_1) {
        float float_2 = ((float) this.age + float_1) / (float) this.maxAge;
        float_2 = MathHelper.clamp(float_2, 0.0F, 1.0F);
        int int_1 = super.getColorMultiplier(float_1);
        int int_2 = int_1 & 255;
        int int_3 = int_1 >> 16 & 255;
        int_2 += (int) (float_2 * 15.0F * 16.0F);
        if (int_2 > 240) {
            int_2 = 240;
        }

        return int_2 | int_3 << 16;
    }

    public void tick() {
        this.prevPosX = this.x;
        this.prevPosY = this.y;
        this.prevPosZ = this.z;
        if (this.age++ >= this.maxAge) {
            this.markDead();
        } else {
            this.move(this.velocityX, this.velocityY, this.velocityZ);
            this.velocityX *= 0.9599999785423279D;
            this.velocityY *= 0.9599999785423279D;
            this.velocityZ *= 0.9599999785423279D;
            if (this.onGround) {
                this.velocityX *= 0.699999988079071D;
                this.velocityZ *= 0.699999988079071D;
            }
        }
    }

    protected float getMinU() { return 0; }
    protected float getMaxU() { return 1; }
    protected float getMinV() { return 0; }
    protected float getMaxV() { return 1; }
}
