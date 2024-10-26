package de.tomalbrc.heatwave;

import de.tomalbrc.heatwave.io.Json;
import de.tomalbrc.heatwave.io.ParticleEffectFile;
import de.tomalbrc.heatwave.util.ParticleModels;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.lwjgl.openal.AL;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

public class Particles {
    public static final List<ParticleEffectFile> ALL = new ObjectArrayList<>();

    public static final ParticleEffectFile RAINBOW;
    public static final ParticleEffectFile SMOKE;
    public static final ParticleEffectFile BALL;

    static {
        try {
            RAINBOW = loadEffect("/particle/rainbow.particle.json");
            SMOKE = loadEffect("/particle/smoke.json");
            BALL = loadEffect("/particle/ball.json");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static ParticleEffectFile loadEffect(String path) throws IOException {
        InputStream stream = Heatwave.class.getResourceAsStream(path);
        if (stream != null) {
            ParticleEffectFile effectFile = Json.GSON.fromJson(new InputStreamReader(stream), ParticleEffectFile.class);
            ParticleModels.addFrom(effectFile);
            ALL.add(effectFile);
            return effectFile;
        }
        throw new RuntimeException(String.format("Could not load particle file %s", path));
    }

    public static void init() {}
}