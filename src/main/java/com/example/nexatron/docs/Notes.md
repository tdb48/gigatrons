#### Notes

- Figure out how boss switches targets on every phase
- After waiting for a switch, the person not aggro'd by nex goes out of nex range
- Keep track of current and next nex specials, for speccing on blood nex start
- Work out some logic for praying during the blood phases
- For ice minion, same as shadow/smoke, have one person melee the minion, DD when nex is close to the minion
- (very maybe) Do we kill reavers if the blood minion is available?
- Zaros, step under 5t, track nex attack tick so we can preswitch, track nex attack tick and our own attack tick

#### Socket
- Master-slave system, hard CA account is the master, look at varbit
- Socket packet includes the following:

NEX:
- (optional) Both: resign logic
- Master: send WorldPoint to slave

Bank/KC/Misc:
- Both: Boolean needsToKc 
- Both: Name of account
- Both: Hard CA diary
- Master: World variable
- Master: Breaking information
- Both: Stop plugin
- Master: Teleport out
- Both: Bank ready to prepot