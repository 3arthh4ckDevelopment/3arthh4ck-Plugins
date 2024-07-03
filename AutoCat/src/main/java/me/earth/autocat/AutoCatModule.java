package me.earth.autocat;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.api.setting.settings.StringSetting;
import me.earth.earthhack.impl.event.events.misc.UpdateEvent;
import me.earth.earthhack.impl.event.listeners.LambdaListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.math.StopWatch;
import me.earth.earthhack.impl.util.text.ChatIDs;
import me.earth.earthhack.impl.util.text.ChatUtil;
import me.earth.earthhack.impl.util.thread.SafeRunnable;
import net.minecraft.network.play.client.CPacketChatMessage;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Random;

public class AutoCatModule extends Module {

    private static final AutoCatModule INSTANCE = new AutoCatModule();

    protected final Setting<MessageMode> mode =
            register(new EnumSetting<>("Message-Mode", MessageMode.Self));
    protected final Setting<Integer> messageLength =
            register(new NumberSetting<>("Message-Length", 150, 20, 500));
    protected final Setting<Integer> delay =
            register(new NumberSetting<>("Delay", 60, 10, 500));
    protected final Setting<Boolean> greenText =
            register(new BooleanSetting("GreenText", false));
    protected final Setting<Boolean> randomSuffix =
            register(new BooleanSetting("RandomSuffix", false));
    protected final Setting<String > prefix =
            register(new StringSetting("Prefix", "CatFact: "));

    private final StopWatch stopWatch = new StopWatch();
    private final Random random = new Random();
    private String fact = null;

    public AutoCatModule() {
        super("AutoCat", Category.Client);
        this.setData(new AutoCatModuleData(this));

        this.listeners.add(new LambdaListener<>(UpdateEvent.class, e -> {
            if (mc.player == null || mc.world == null) return;
            if (stopWatch.passed(delay.getValue() * 1000)) {
                if (fact == null) {
                    getCatFact();
                    return;
                }

                String message = (greenText.getValue() && mode.getValue() == MessageMode.Everyone ? ">" : "")
                        + prefix.getValue()
                        + " "
                        + fact
                        + (randomSuffix.getValue() ? "[" + ChatUtil.generateRandomHexSuffix(2) + "]" : "");

                switch (mode.getValue()) {
                    case Everyone:
                        mc.player.connection.sendPacket(
                                new CPacketChatMessage(message));
                        break;
                    case Mgs:
                        int playerEntitySize = mc.world.playerEntities.size();
                        if (playerEntitySize == 0) break;
                        int randomPlayerIndex = random.nextInt(playerEntitySize);
                        mc.player.connection.sendPacket(
                                new CPacketChatMessage("/msg " + mc.world.playerEntities.get(randomPlayerIndex) + message));
                        break;
                    case Self:
                        Managers.CHAT.sendDeleteMessage(message, this.getName(), ChatIDs.MODULE);
                        break;
                }
                fact = null;
                stopWatch.reset();
            }
        }));
    }

    public static AutoCatModule getInstance() {
        return INSTANCE;
    }

    @Override
    protected void onEnable() {
        stopWatch.reset();
    }

    private void getCatFact() {
        SafeRunnable runnable = () ->
        {
            String url = "https://catfact.ninja/fact?max_length=" + messageLength.getValue();
            try {
                String jsonResponse = IOUtils.toString(new URL(url), StandardCharsets.UTF_8);
                JsonObject jsonObject = new JsonParser().parse(jsonResponse).getAsJsonObject();
                this.fact = jsonObject.get("fact").getAsString();
            } catch (IOException e) {
                e.printStackTrace();
            }
        };

        Managers.THREAD.submit(runnable);
    }

}
