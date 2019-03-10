package com.gotofinal.darkrise.economy.commands;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

import com.gotofinal.darkrise.core.annotation.DarkRiseSubCommand;
import com.gotofinal.darkrise.core.commands.Command;
import com.gotofinal.darkrise.economy.DarkRiseEconomy;
import com.gotofinal.darkrise.economy.DarkRiseItem;
import com.gotofinal.darkrise.economy.cfg.VoucherManager;
import com.gotofinal.darkrise.spigot.core.command.Arguments;
import com.gotofinal.darkrise.spigot.core.command.CommandExecutor;
import com.gotofinal.messages.api.messages.Message.MessageData;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@DarkRiseSubCommand(value = EconItemsCommand.class, name = "voucher")
public class EconItemsVoucherCommand implements CommandExecutor
{
	private final DarkRiseEconomy  plugin;
	private final EconItemsCommand command;

	public EconItemsVoucherCommand(DarkRiseEconomy plugin, EconItemsCommand command)
	{
		this.plugin = plugin;
		this.command = command;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command<CommandSender> command, String label,
	                                  Matcher matchedPattern, Arguments args)
	{
		String[] raw = args.getRawArgs();
		if (raw.length == 2)
		{
			return Collections.emptyList();
		}
		if (raw.length == 0)
		{
			return this.plugin.getItems().getItems().stream().map(DarkRiseItem::getId).collect(Collectors.toList());
		}
		String str = raw[0].toLowerCase();
		return this.plugin.getItems()
				.getItems()
				.stream()
				.map(DarkRiseItem::getId)
				.filter(id -> id.toLowerCase().startsWith(str))
				.collect(Collectors.toList());
	}

	@Override
	public void runCommand(final CommandSender sender, final Command<CommandSender> command, final String label,
	                       final Matcher matchedPattern, final Arguments args)
	{
		if (! this.checkPermission(sender, "econ.items.voucher"))
		{
			return;
		}

		if (args.length() != 2 && args.length() != 3)
		{
			this.sendUsage(command.getUsage(), sender, command, args);
			return;
		}

		Player target = args.asPlayer(0);
		if (target == null)
		{
			this.sendMessage("notAPlayer", sender, new MessageData("name", args.asString(0)));
			return;
		}

		DarkRiseItem riseItem = this.plugin.getItems().getItemById(args.asString(1));
		if (riseItem == null)
		{
			this.sendMessage("economy.commands.noItem", sender, new MessageData("name", args.asString(1)));
			return;
		}

		int amount = 1;
		if (args.has(2))
		{
			Integer i = args.asInt(2);
			if (i == null)
			{
				this.sendMessage("notANumber", sender, new MessageData("text", args.asText(2)));
				return;
			}
			amount = i;
		}

		ItemStack item = VoucherManager.getInstance().addNextId(riseItem.getItem(amount));
		Player player = (Player) sender;
		HashMap<Integer, ItemStack> notAdded = player.getInventory().addItem(item);

		if (! notAdded.isEmpty())
		{
			notAdded.forEach((a, i) -> {
				i = i.clone();
				i.setAmount(a);
				player.getLocation().getWorld().dropItemNaturally(player.getLocation(), i);
			});
		}
	}
}