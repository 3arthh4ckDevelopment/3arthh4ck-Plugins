package me.earth.autocat;

import me.earth.earthhack.api.module.data.DefaultData;

final class AutoCatModuleData extends DefaultData<AutoCatModule>
{
    public AutoCatModuleData(AutoCatModule module)
    {
        super(module);
        register(module.mode,
                "-Self: cats only for you!"
                        + "\n-Mgs: message random ppl about cats!"
                        + "\n-Everyone: cats for everyone in chat!");
        register(module.messageLength, "Choose what is the max length of the amazing cat fact ~~meow~~");
        register(module.delay, "The message delay between the cat facts");
        register(module.greenText, "Select if you want the text to be green or not (only works with mode: everyone)");
        register(module.randomSuffix, "Use a random suffix to bypass cat spamming restrictions!");
        register(module.prefix, "The fact prefix");
    }

    @Override
    public String getDescription()
    {
        return "Gets random facts about cats for you!";
    }

}

