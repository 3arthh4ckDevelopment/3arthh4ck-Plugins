package me.ai24.voicecontrol.module;

import edu.cmu.sphinx.api.Configuration;
import edu.cmu.sphinx.api.LiveSpeechRecognizer;
import me.ai24.voicecontrol.VoiceControl;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.thread.SafeRunnable;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

public class VoiceControlModule extends Module {

    private final AtomicBoolean enabled = new AtomicBoolean(false);
    private LiveSpeechRecognizer recognizer;

    public VoiceControlModule() {
        super("VoiceControl", Category.Client);
        try {
            recognizer = new LiveSpeechRecognizer(createConfiguration());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onEnable() {
        enabled.set(true);
        SafeRunnable listeningRunnable = () -> {
            recognizer.startRecognition(true);
            while (enabled.get()) {
                String hypothesis = recognizer.getResult().getHypothesis();

                try {
                    if (hypothesis.equals("<unk>")) continue;

                    String command = parseCommand(hypothesis);
                    if (command != null) {
                        VoiceControl.LOGGER.info("Command: " + command);
                        Managers.COMMANDS.applyCommandNoPrefix(command);
                    }
                } catch (Exception e) {
                    VoiceControl.LOGGER.error("Failed to apply hypothesis: " + hypothesis);
                }
            }
            recognizer.stopRecognition();
            VoiceControl.LOGGER.info("Voice control stopped");
        };
        Managers.THREAD.submit(listeningRunnable);
    }

    @Override
    public void onDisable() {
        enabled.set(false);
    }

    private String parseCommand(String data) {
        data = data.replaceFirst("phobos", "");

        String[] command = data.split("(?<=toggle|enable|disable)");

        if (command.length > 1) {
            String module = command[1].trim();
            module = switch (module) {
                case "crystal aura" -> "autocrystal";
                case "burrow" -> "blocklag";
                case "g u i", "click g u i" -> "clickgui";
                case "hud" -> "hudeditor";
                case "strafe" -> "speed";
                default -> module.replace(" ", "");
            };
            if (command[0].trim().equals("toggle")) {
                return command[0] + " " + module;
            } else {
                return module + " enabled " + (command[0].trim().equals("enable") ? "true" : "false");
            }
        }

        return null;
    }

    private Configuration createConfiguration() {
        Configuration configuration = new Configuration();

        // general configuration
        configuration.setAcousticModelPath("resource:/edu/cmu/sphinx/models/en-us/en-us");
        configuration.setDictionaryPath("resource:/edu/cmu/sphinx/models/en-us/cmudict-en-us.dict");

        // grammar
        configuration.setGrammarPath("resource:/assets/dialog");
        configuration.setUseGrammar(true);
        configuration.setGrammarName("dialog");

        return configuration;
    }
}
