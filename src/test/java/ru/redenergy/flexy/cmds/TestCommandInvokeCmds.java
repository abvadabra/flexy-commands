package ru.redenergy.flexy.cmds;

import net.minecraft.command.ICommandSender;
import ru.redenergy.flexy.FlexyCommand;
import ru.redenergy.flexy.TestCommandSender;
import ru.redenergy.flexy.annotation.Arg;
import ru.redenergy.flexy.annotation.Command;
import ru.redenergy.flexy.annotation.Flag;

public class TestCommandInvokeCmds extends FlexyCommand {
    @Override
    public String getCommandName() {
        return "test";
    }

    @Override
    public String getCommandUsage(ICommandSender p_71518_1_) {
        return "/test <args>";
    }

    @Command(value = "disabled", disabled = true)
    public void disabledCommand(){}

    @Command("add {user} to {group}")
    public void testCommandParameterized(@Arg("group") String group, @Arg("user") String user, ICommandSender sender){
        System.out.print(sender.getName() + ":" + user + ":" + group);
    }

    @Command("empty")
    public void testEmptyCommand(){
        System.out.print("empty");
    }

    @Command("add message {*message}")
    public void addMessage(@Arg("message") String message){
        System.out.print(message);
    }

    @Command
    public void specialSender(TestCommandSender testCommandSender){
        System.out.print(testCommandSender.testUniqueMethid());
    }

    @Command("{bol} {number} {long}")
    public void typeInferenced(@Arg("bol") boolean bol, @Arg("number") int number, @Arg("long") long longArg){
        System.out.print(bol + ":" + number + ":" + longArg);
    }

    @Command("{action} {player} {something} {garbage} {bla}")
    public void commandFlagged(@Arg("action") String action, @Arg("player") String player, @Flag("-a") boolean big){
        if(big){
            System.out.print(action.toUpperCase() + player.toUpperCase());
        } else {
            System.out.print(action.toLowerCase() + player.toLowerCase());
        }
    }
}
