package su.nightexpress.excellentcrates.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.AbstractCommand;
import su.nexmedia.engine.api.command.CommandResult;
import su.nexmedia.engine.utils.CollectionsUtil;
import su.nightexpress.excellentcrates.ExcellentCrates;
import su.nightexpress.excellentcrates.Perms;
import su.nightexpress.excellentcrates.Placeholders;
import su.nightexpress.excellentcrates.config.Lang;
import su.nightexpress.excellentcrates.crate.impl.Crate;

import java.util.Arrays;
import java.util.List;

public class GiveCommand extends AbstractCommand<ExcellentCrates> {

    public GiveCommand(@NotNull ExcellentCrates plugin) {
        super(plugin, new String[]{"give"}, Perms.COMMAND_GIVE);
        this.setDescription(plugin.getMessage(Lang.COMMAND_GIVE_DESC));
        this.setUsage(plugin.getMessage(Lang.COMMAND_GIVE_USAGE));
        this.addFlag(CommandFlags.SILENT);
    }

    @Override
    @NotNull
    public List<String> getTab(@NotNull Player player, int arg, @NotNull String[] args) {
        if (arg == 1) {
            return CollectionsUtil.playerNames(player);
        }
        if (arg == 2) {
            return plugin.getCrateManager().getCrateIds(false);
        }
        if (arg == 3) {
            return Arrays.asList("1", "5", "10");
        }
        return super.getTab(player, arg, args);
    }

    @Override
    protected void onExecute(@NotNull CommandSender sender, @NotNull CommandResult result) {
        if (result.length() < 3) {
            this.printUsage(sender);
            return;
        }

        int amount = result.length() >= 4 ? result.getInt(3, 1) : 1;

        Crate crate = plugin.getCrateManager().getCrateById(result.getArg(2));
        if (crate == null) {
            plugin.getMessage(Lang.CRATE_ERROR_INVALID).send(sender);
            return;
        }

        Player player = plugin.getServer().getPlayer(result.getArg(1));
        if (player == null) {
            this.errorPlayer(sender);
            return;
        }

        plugin.getCrateManager().giveCrate(player, crate, amount);

        if (!result.hasFlag(CommandFlags.SILENT)) {
            plugin.getMessage(Lang.COMMAND_GIVE_NOTIFY)
                .replace(Placeholders.GENERIC_AMOUNT, amount)
                .replace(crate.replacePlaceholders())
                .send(player);
        }
        if (sender != player) {
            plugin.getMessage(Lang.COMMAND_GIVE_DONE)
                .replace(Placeholders.forPlayer(player))
                .replace(Placeholders.GENERIC_AMOUNT, amount)
                .replace(crate.replacePlaceholders())
                .send(sender);
        }
    }
}
