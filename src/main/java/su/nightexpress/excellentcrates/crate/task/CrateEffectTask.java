package su.nightexpress.excellentcrates.crate.task;

import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.particle.SimpleParticle;
import su.nexmedia.engine.api.server.AbstractTask;
import su.nexmedia.engine.utils.LocationUtil;
import su.nightexpress.excellentcrates.ExcellentCrates;
import su.nightexpress.excellentcrates.crate.effect.CrateEffect;
import su.nightexpress.excellentcrates.crate.effect.CrateEffectModel;
import su.nightexpress.excellentcrates.crate.impl.Crate;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

public class CrateEffectTask extends AbstractTask<ExcellentCrates> {

    public CrateEffectTask(@NotNull ExcellentCrates plugin) {
        super(plugin, 1L, true);
    }

    @Override
    public void action() {
        Collection<Crate> crates = this.plugin.getCrateManager().getCrates();

        crates.forEach(crate -> {
            if (crate.getBlockEffectModel() == CrateEffectModel.NONE) return;

            SimpleParticle particle = crate.getBlockEffectParticle();
            CrateEffect effect = crate.getBlockEffectModel().getEffect();

            new HashSet<>(crate.getBlockLocations()).forEach(location -> {
                World world = location.getWorld();
                int chunkX = location.getBlockX() >> 4;
                int chunkZ = location.getBlockZ() >> 4;
                if (world == null || !world.isChunkLoaded(chunkX, chunkZ)) return;

                Location center = LocationUtil.getCenter(location.clone());
                effect.step(center, particle);
            });
        });

        Arrays.asList(CrateEffectModel.values()).forEach(model -> model.getEffect().addStep());
    }
}
