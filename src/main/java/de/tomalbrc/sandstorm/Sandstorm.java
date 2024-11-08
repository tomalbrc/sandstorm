package de.tomalbrc.sandstorm;

import com.mojang.logging.LogUtils;
import de.tomalbrc.sandstorm.command.SandstormCommand;
import de.tomalbrc.sandstorm.component.ParticleComponents;
import de.tomalbrc.sandstorm.polymer.ParticleEffectHolder;
import de.tomalbrc.sandstorm.util.ParticleModels;
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import gg.moonflower.molangcompiler.api.MolangCompiler;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

public class Sandstorm implements ModInitializer {
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final String MOD_ID = "sandstorm";
    public static final MolangCompiler MOLANG = MolangCompiler.create(MolangCompiler.DEFAULT_FLAGS, Sandstorm.class.getClassLoader());
    public static final float TIME_SCALE = 1.f / 20.f; // 20 tps based

    public static final Path CONFIG_DIR = FabricLoader.getInstance().getConfigDir().resolve("sandstorm");

    public static List<ParticleEffectHolder> HOLDER = new ObjectArrayList<>();

    @Override
    public void onInitialize() {
        createDirectoryStructure();

        PolymerResourcePackUtils.markAsRequired();

        ParticleComponents.init();
        Particles.init();

        loadFromConfig();

        CommandRegistrationCallback.EVENT.register((dispatcher, context, selection) -> {
            SandstormCommand.register(dispatcher);
        });

        PolymerResourcePackUtils.RESOURCE_PACK_CREATION_EVENT.register(ParticleModels::addToResourcePack);
    }

    public static void loadFromConfig() {
        Path particleDir = CONFIG_DIR.resolve("particle");
        try (Stream<Path> paths = Files.walk(particleDir)) {
            paths.filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".json"))
                    .forEach(path -> {
                        try (InputStream inputStream = Files.newInputStream(path)) {
                            Particles.loadEffect(inputStream);
                        } catch (IOException e) {
                            Sandstorm.LOGGER.error("Could not load {}!", path);
                        }
                    });
        } catch (IOException e) {
            Sandstorm.LOGGER.error("Could not access 'particle' folder!");
        };
    }

    public static void createDirectoryStructure() {
        try {
            if (!Files.exists(CONFIG_DIR)) {
                Files.createDirectories(CONFIG_DIR);

                Path texturesDir = CONFIG_DIR.resolve("textures/particle");
                Files.createDirectories(texturesDir);

                Path particlesDir = CONFIG_DIR.resolve("particle");
                Files.createDirectories(particlesDir);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
