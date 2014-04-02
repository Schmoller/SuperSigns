package au.com.addstar.signmaker.commands;

import java.util.EnumSet;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import au.com.addstar.signmaker.Justification;
import au.com.addstar.signmaker.SignMakerPlugin;
import au.com.addstar.signmaker.TextSign;
import au.com.addstar.signmaker.TextWriter;

public class CreateCommand implements ICommand
{

	@Override
	public String getName()
	{
		return "create";
	}

	@Override
	public String[] getAliases()
	{
		return null;
	}

	@Override
	public String getPermission()
	{
		return "signmaker.create";
	}

	@Override
	public String getUsageString( String label, CommandSender sender )
	{
		return label + " <name> <justification> [<font> <material> <text>]";
	}

	@Override
	public String getDescription()
	{
		return "Creates a new text sign at your location with the specified justification. You can also optionally set a value";
	}

	@Override
	public EnumSet<CommandSenderType> getAllowedSenders()
	{
		return EnumSet.of(CommandSenderType.Player);
	}

	@Override
	public boolean onCommand( CommandSender sender, String parent, String label, String[] args ) throws BadArgumentException
	{
		if(args.length != 2 && args.length < 5)
			return false;
		
		SignMakerPlugin plugin = SignMakerPlugin.instance;
		
		if(plugin.getSign(args[0]) != null)
			throw new BadArgumentException(0, "A sign by that name already exists");
		
		Justification justification = null;
		
		for(Justification just : Justification.values())
		{
			if(args[1].equalsIgnoreCase(just.name()))
			{
				justification = just;
				break;
			}
		}
		
		if(justification == null)
			throw new BadArgumentException(1, "Unknown value for justification. Valid values are: Left, Center, and Right");
		
		TextSign sign = new TextSign(((Player)sender).getLocation(), TextWriter.rotateRight(TextWriter.lookToFace(((Player)sender).getLocation().getYaw())), "");
		sign.setJustification(justification);
		
		if(args.length >= 5)
		{
			String font = args[2];
			if(TextWriter.getFont(font) == null)
				throw new BadArgumentException(2, "Unknown font");
			
			Material material = Material.valueOf(args[3].toUpperCase());
			if(material == null)
				throw new BadArgumentException(3, "Unknown material " + args[3]);
			
			if(!material.isBlock() || material.hasGravity() || !material.isSolid())
				throw new BadArgumentException(3, "Material cannot be an item, a block that falls under gravity, or not a full block");
			
			String text = "";
			for(int i = 4; i < args.length; ++i)
			{
				if(!text.isEmpty())
					text += " ";
				text += args[i];
			}
			
			sign.setFont(font);
			sign.setMaterial(material);
			sign.setText(text);
			
			sign.redraw();
		}
		
		plugin.addSign(args[0], sign);
		sender.sendMessage(ChatColor.GREEN + "Sign created");
		return true;
	}

	@Override
	public List<String> onTabComplete( CommandSender sender, String parent, String label, String[] args )
	{
		return null;
	}

}