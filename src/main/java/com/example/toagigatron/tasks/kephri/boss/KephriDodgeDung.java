package com.example.toagigatron.tasks.kephri.boss;

import com.example.Packets.MousePackets;
import com.example.Packets.MovementPackets;
import com.example.Utility.Movement;
import com.example.Utility.Static;
import com.example.toagigatron.manager.ToaManager;
import com.example.toagigatron.model.constants.Stage;
import com.example.toagigatron.model.constants.ToaConstants;
import com.example.toagigatron.taskformat.StagedTask;
import com.example.toagigatron.taskformat.TaskDescriptor;
import javax.inject.Inject;
import net.runelite.api.Varbits;
import net.runelite.api.coords.WorldPoint;

@TaskDescriptor(
	name = "Kephri Dodge Dung",
	priority = 55,
	blocking = true
)
public class KephriDodgeDung extends StagedTask
{
	@Inject
	public KephriDodgeDung(ToaManager toaManager)
	{
		super(toaManager, Stage.KEPHRI_BOSS);
	}

	public boolean execute()
	{

		if (toaManager.kephri.kephriRows.isEmpty())
		{
			return false;
		}
		//if dungescape is true
		if ((toaManager.kephri.playerDungedLocation == null || toaManager.kephri.dungedPrepathTile == null)
			&& toaManager.kephri.dungEscape)
		{
			toaManager.print("Returning false cause dung escape and location is null");
			return false;
		}

		if(client.getLocalPlayer().getGraphic() == ToaConstants.DUNG_GRAPHIC_START && toaManager.kephri.optimalDungTile == null){
			toaManager.print("false because dung graphic and optimal dung tile is null");
			return false;
		}

		if(client.getLocalPlayer().getGraphic() == ToaConstants.DUNG_GRAPHIC_START
			&& client.getLocalPlayer().getWorldLocation().equals(toaManager.kephri.optimalDungTile)){
			toaManager.print("False because dung graphic and player is already on dung tile");
			return false;
		}
		WorldPoint playerPoint = client.getLocalPlayer().getWorldLocation();
		//Change this to use the walker to avoid bomb tiles
		//Potentially add an 'optimal melee tile' AND 'optimal dung tile', so you can melee without dropping bombs on the dung tile
		//Then move to dung tile to take the knockback
		if (client.getLocalPlayer().getGraphic() == ToaConstants.DUNG_GRAPHIC_START && !playerPoint.equals(toaManager.kephri.optimalDungTile))
		{
			toaManager.print("Moving back to the optimal tile to get dunged");
			Movement.walk(toaManager.kephri.optimalDungTile);
			return true;
		}
		//Have not yet moved after getting dunged
		if (toaManager.kephri.dungEscape && playerPoint.equals(toaManager.kephri.playerDungedLocation))
		{
			toaManager.print("Moving off dung row to tile -> " + toaManager.worldPointString(toaManager.kephri.dungedPrepathTile));
			Movement.walk(toaManager.kephri.dungedPrepathTile);
			return true;
		}
		//We have moved somewhere since getting dunged this means we r no longer dunged and can continue our battle
		//Maybe we need to change this to check that youve actually reached the dungedprepathtile for cases that dung paths weirdly and u might
		//Push urself into a locked area but i dont think its possible with accurate enough area and direction detection
		else if (toaManager.kephri.dungEscape && !playerPoint.equals(toaManager.kephri.playerDungedLocation))
		{
			toaManager.print("We have moved off dung, setting 'dungescape' to false and 'dungedprepathtile' to null");
			toaManager.kephri.dungEscape = false;
			toaManager.kephri.dungedPrepathTile = null;
			toaManager.kephri.playerDungedLocation = null;
			//this might be bad idk
			toaManager.kephri.preDungedTile = null;
			return true;
		}
		//toaManager.print("Returning false at bottom of dodge dung somehow nothing is right.");

		return false;
	}
}
