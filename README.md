## Features ##

This is a plugin designed for hardcore-style servers, where players are temporarily banned upon death. You can grant players revival credits, which allow them to join the server before their ban time is up.


## Commands ##

<table style="border:1px solid black">
<tr><td>/deathban</td>
<td>Lists all available commands for the plugin</td></tr>
<tr><td>/deathban enable</td>
<td>Enable the plugin server-wide (always enabled on server start).</td></tr>
<tr><td>/deathban disable</td>
<td>Disable the plugin server-wide.</td></tr>
<tr><td>/deathban ban &lt;player&gt; [time]</td>
<td>Manually ban a player. If no time is specified, they will be banned for their default time period.</td></tr>
<tr><td>/deathban unban &lt;player&gt;</td>
<td>Manually unban a player.</td></tr>
<tr><td>/deathban status &lt;player&gt;</td>
<td>Display the status of a player.</td></tr>
<tr><td>/credits [player]</td>
<td>Check the amount of revival credits you or another player possess.</td></tr>
<tr><td>/credits send &lt;player&gt; &lt;amount&gt;</td>
<td>Send another player some of your own revival credits.</td></tr>
<tr><td>/credits give &lt;player&gt; &lt;amount&gt;</td>
<td>Give a player a certain amount of revival credits.</td></tr>
<tr><td>/credits take &lt;player&gt; &lt;amount&gt;</td>
<td>Take a certain amount of revival credits from a player.</td></tr>
</table>


## Permissions ##

<table style="border:1px solid black">
<tr><td>deathban.*</td>
<td>Allow full access to all commands in the plugin.</td></tr>
<tr><td>deathban.display</td>
<td>Allow use of /deathban to show all plugin commands.</td></tr>
<tr><td>deathban.enable</td>
<td>Allow access to /deathban enable and /deathban disable commands.</td></tr>
<tr><td>deathban.ban</td>
<td>Allow access to manually ban a player via the /deathban ban command.</td></tr>
<tr><td>deathban.ban.exempt</td>
<td>Players with this permission can not be banned and will respawn as normal when killed.</td></tr>
<tr><td>deathban.unban</td>
<td>Allow access to manually unban a player via the /deathban unban command.</td></tr>
<tr><td>deathban.status</td>
<td>Allow access to check a player's ban status via the /deathban status command.</td></tr>
<tr><td>deathban.credits.check</td>
<td>Allow access to check your own revival credits via the /credits command.</td></tr>
<tr><td>deathban.credits.check.others</td>
<td>Allow access to check other players' revival credits via the /credits command.</td></tr>
<tr><td>deathban.credits.send</td>
<td>Allow access to send revival credits to other players via the /credits send command.</td></tr>
<tr><td>deathban.credits.give</td>
<td>Allow access to give revival credits to players via the /credits give command.</td></tr>
<tr><td>deathban.credits.take</td>
<td>Allow access to take revival credits from players via the /credits take command.</td></tr>
<tr><td>deathban.class.&lt;class name&gt;</td>
<td>Give player access to a death class as defined in the config.</td></tr>
</table>


## Configuration ##

<table style="border:1px solid black">
<tr><td>Enabled</td>
<td>Boolean value to determine whether the plugin is enabled or not.</td></tr>
<tr><td>Ban-Time</td>
<td>Default time a player should be banned in the format of #y#mo#w#d#h#m#s, replacing # with the number of years, months, days, hours, minutes, and seconds, respectively, to be banned. A value does not have to be specified for each time unit (i.e., 1h30m is still valid).</td></tr>
<tr><td>Death-Message</td>
<td>String value shown to a player when kicked from the server for dieing.</td></tr>
<tr><td>Early-Message</td>
<td>String value shown to a player when trying to log in to the server while still banned.</td></tr>
<tr><td>Tick-Delay</td>
<td>Integer value that specifies the delay (in ticks) until the player is kicked after dieing. I recommend not changing this unless you know exactly what you are doing.</td></tr>
<tr><td>Starting-Credits</td>
<td>Integer value that specifies the number of revival credits players are given when they join the server for the first time</td></tr>
<tr><td>Verbose</td>
<td>Boolean value that enables/disables logging to the console. (Used for debug)</td></tr>
<tr><td>Death-Classes</td>
<td>See the <a href="http://dev.bukkit.org/server-mods/hardcoredeathban/pages/default-config/">default config</a> for an example on how to set up the death classes.</td></tr>
</table>


## Variables ##

These variables can be used in the config messages, and will automatically be changed to their corresponding value when displayed to the player.

<table style="border:1px solid black">
<tr><td>%server%</td>
<td>Name of the server</td></tr>
<tr><td>%player%</td>
<td>Name of the player</td></tr>
<tr><td>%currenttime%</td>
<td>The current time of the server (ex. 11:21 PM EDT)</td></tr>
<tr><td>%currentdate%</td>
<td>The current date of the server (ex. 11/29/2012)</td></tr>
<tr><td>%unbantime%</td>
<td>The time the player will be unbanned</td></tr>
<tr><td>%unbandate%</td>
<td>The date the player will be unbanned</td></tr>
<tr><td>%bantimeleft%</td>
<td>The amount of time left on the player's ban (ex. 4 days 3 hours 45 minutes 1 second)</td></tr>
</table>


## Update Checking ##

This plugin periodically checks BukkitDev for a new version, and will notify the console and players with the "deathban.receivealerts" permission of a new version. If you would like to disable this feature simply set "Check-for-Updates" to false in plugins/HardcoreDeathBan/config.yml.


## Stats Collection ##

Anonymous statistics are collected automatically by this plugin and sent to MCStats. If you would like to view the data, you can do so [here](http://mcstats.org/plugin/HardcoreDeathBan). If you would like to disable this feature, set "opt-out" to true in plugins/PluginMetrics/config.yml.


## Support and Feature Requests ##

If you have found a bug with the plugin or would like to suggest a feature to be added, please create an issue on GitHub to make sure I see it. You can get to the issues page by clicking either the Tickets link at the top of the BukkitDev page or the Issues link on the GitHub repo. Click on New Issue and provide as much information as possible. The more information you provide, the better I can help you.


## Links ##

[BukkitDev Page](http://dev.bukkit.org/bukkit-plugins/hardcoredeathban/)

[GitHub Repository](http://github.com/mstiles92/HardcoreDeathBan)

[Twitter](http://twitter.com/mstiles92)


## Donations ##

Donations are by no means required, but would be _much_ appreciated. If you feel that my plugins have been of great use to you and would like to give me a little something in return, this is the way to do it.

[Donate](https://www.paypal.com/cgi-bin/webscr?return=http%3A%2F%2Fdev.bukkit.org%2Fbukkit-plugins%2Fhardcoredeathban%2F&cn=Add+special+instructions+to+the+addon+author%28s%29&business=mstiles92%40gmail.com&bn=PP-DonationsBF%3Abtn_donateCC_LG.gif%3ANonHosted&cancel_return=http%3A%2F%2Fdev.bukkit.org%2Fbukkit-plugins%2Fhardcoredeathban%2F&lc=US&item_name=HardcoreDeathBan+%28from+Bukkit.org%29&cmd=_donations&rm=1&no_shipping=1&currency_code=USD)


## Legal ##

Licensed under the Common Development and Distribution License Version 1.0 (CDDL-1.0). For license information, see the [LICENSE](https://github.com/mstiles92/HardcoreDeathBan/blob/master/LICENSE) file, or on the web at <http://opensource.org/licenses/CDDL-1.0>.