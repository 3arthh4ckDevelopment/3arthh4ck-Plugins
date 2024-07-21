package me.earth.lawnmower;

import me.earth.earthhack.api.module.data.DefaultData;

public class LawnmowerData extends DefaultData<LawnmowerModule> {

    public LawnmowerData(LawnmowerModule module) {
        super(module);
        register(module.tallGrass, "Break tall grass and tall ferns");
        register(module.shortGrass, "Break short grass");
        register(module.flowers, "Break flowers");

        register(module.range, "The range in which to break blocks");
        register(module.ticksDelay, "The amount of blocks to break per tick");
        register(module.rotate, "Rotate towards the block");
        register(module.render, "Render the blocks");
    }

    @Override
    public String getDescription()
    {
        return "Mows the lawn";
    }
}
