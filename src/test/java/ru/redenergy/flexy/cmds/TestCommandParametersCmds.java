package ru.redenergy.flexy.cmds;

import com.google.common.base.Optional;
import net.minecraft.command.ICommandSender;
import ru.redenergy.flexy.FlexyCommand;
import ru.redenergy.flexy.annotation.Command;
import ru.redenergy.flexy.annotation.Par;

public class TestCommandParametersCmds extends FlexyCommand {
    @Override
    public String getCommandName() {
        return "test";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/test";
    }

    @Command("simple")
    public void simple(@Par("--p") int val){
        System.out.print("Val:" + val);
    }

    @Command("optional")
    public void optonalPar(@Par("--p") Optional<Integer> val){
        System.out.print("Optional val:" + val.get());
    }
}
