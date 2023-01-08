package com.dreambx.jmapch.common.command;

import com.dreambx.jmapch.client.Constants;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

public class CmdBase extends CommandBase {
    // 必须覆写。这一部分将作为命令的名字而存在。
    // 换言之，该命令的第一部分将会是 /mycommand。
    /*
    @Override
    public String getCommandName() {
        return "mycommand";
    }
     */



    @Override
    public String getName()
    {
        return "jmapch";
    }

    //必须覆写
    @Override
    public String getUsage(ICommandSender sender) {
        // 实际上这里应该使用 I18n 来保证支持国际化。
        //return "/mycommand - my first command";
        return Constants.getString("jmapch.cmdbase.desc");
    }

    // 必须覆写
    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        // 命令的执行逻辑全部在这里发生。
        sender.sendMessage(new TextComponentString(Constants.getString("jmapch.cmdbase.desc")));
    }

    // 可以不覆写，但默认权限要求是 4（即游戏管理员），所以请按需覆写
    /*
    @Override
    public int getRequiredPermissionLevel() {
        return 0; // 0 代表任何人都能用
    }
    */
}
