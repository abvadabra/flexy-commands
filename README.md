# Flexy Commands
Flexible API for commands in Forge.

## Example 
There is no need to parse user input by yourself, just define command specifics it will be parsed automatically.

```

@Command("give {player} {item}")
public void command(@Arg("player") String playerArg, @Arg("item") String itemArg){
    getPlayer(playerArg).inventory.addItem(Item.getItem(itemArg));
}
```

### More Information

* **[Getting started](https://github.com/FRedEnergy/flexy-commands/wiki/Getting-Started)**
   * [Downloading from Maven](https://github.com/FRedEnergy/flexy-commands/wiki/Getting-Started#downloading-from-maven)
   * [Using custom permissions providers](https://github.com/FRedEnergy/flexy-commands/wiki/Getting-Started#custom-permission-providers)
   * [Creating command class](https://github.com/FRedEnergy/flexy-commands/wiki/Getting-Started#creating-command-class)
   * [Declaring command](https://github.com/FRedEnergy/flexy-commands/wiki/Getting-Started#declaring-commands)
   * [Registering command](https://github.com/FRedEnergy/flexy-commands/wiki/Getting-Started#registering-command)
 * [More about @Command annotation](https://github.com/FRedEnergy/flexy-commands/wiki/Command-annotation)
 * [Template of the command](https://github.com/FRedEnergy/flexy-commands/wiki/Command-Template)
 * [Types of user input](https://github.com/FRedEnergy/flexy-commands/wiki/Types-of-User-Input)
   * [Basic arguments](https://github.com/FRedEnergy/flexy-commands/wiki/Types-of-User-Input#basic-arguments-or-variables)
   * [Flags](https://github.com/FRedEnergy/flexy-commands/wiki/Types-of-User-Input#flags)
   * [Optional parameters](https://github.com/FRedEnergy/flexy-commands/wiki/Types-of-User-Input#optional-named-parameters)
 * [Error handling](https://github.com/FRedEnergy/flexy-commands/wiki/Error-Handling)
 * [Automated command usage output](https://github.com/FRedEnergy/flexy-commands/wiki/Automated-command-usage-output)

