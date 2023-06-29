package com.example.toagigatron.tasks.wardens.wardensp2;

import com.example.Packets.MousePackets;
import com.example.Packets.MovementPackets;
import com.example.Packets.NPCPackets;
import com.example.Packets.ObjectPackets;
import com.example.Utility.Movement;
import com.example.Utility.NPCUtil;
import com.example.Utility.ObjectUtil;
import com.example.Utility.Reachable;
import com.example.Utility.Static;
import com.example.toagigatron.manager.GameTickManager;
import com.example.toagigatron.manager.ToaManager;
import com.example.toagigatron.model.constants.Stage;
import com.example.toagigatron.model.constants.ToaConstants;
import com.example.toagigatron.taskformat.StagedTask;
import com.example.toagigatron.taskformat.TaskDescriptor;
import java.util.ArrayList;
import java.util.Map;
import javax.inject.Inject;
import net.runelite.api.GameObject;
import net.runelite.api.NPC;
import net.runelite.api.coords.WorldPoint;

@TaskDescriptor(
	name = "Warden dodge special"
)
public class WardensP2DodgeSpecial extends StagedTask
{
	@Inject
	public WardensP2DodgeSpecial(ToaManager toaManager)
	{
		super(toaManager, Stage.WARDENS_P2);
	}

	/*WARDENS:
	 * 1. ALWAYS dodge prison priority number ONE
	 * 2. Dodge specials (skull, beam, windmill)
	 * 3.
	 *=
	 * */
	public boolean execute()
	{
		NPC core = NPCUtil.findNearest("Core");
		if (core != null || toaManager.wardens12.warden == null || toaManager.wardens12.warden.getId() == ToaConstants.WARDENS_P2_DOWNED)
		{
			return false;
		}
		WorldPoint safeTile = toaManager.wardens12.safeTile;
		WorldPoint dodgeTilte = toaManager.wardens12.dodgeTile;
		if (safeTile == null || dodgeTilte == null)
		{
			return false;
		}
		int beamTick = toaManager.wardens12.beamTick;
		int windmillTick = toaManager.wardens12.windMillTick;
		WorldPoint playerPoint = client.getLocalPlayer().getWorldLocation();
		GameObject yellowUFO = ObjectUtil.getNearestGameObject(ToaConstants.YELLOW_UFO);
		GameObject redUFO = ObjectUtil.getNearestGameObject(ToaConstants.RED_UFO);
		if (yellowUFO != null)
		{
			WorldPoint redTile = toaManager.wardens12.dodgeUFO();
			if (playerPoint.equals(redTile))
			{
				return false;
			}
			toaManager.print("Moving to safe tile from yellow ufo");
			Movement.walk(redTile);
			return true;
		}
		else if (redUFO != null)
		{
			WorldPoint redTile = toaManager.wardens12.defaultTile();
			if (playerPoint.equals(redTile))
			{
				return false;
			}
			toaManager.print("Moving to safe tile from red ufo");
			Movement.walk(redTile);
			return true;
		}
		else if (beamTick > 10)
		{
			if (beamTick > 38)
			{
				if (!playerPoint.equals(safeTile))
				{
					toaManager.print("Setting to safetile");
					Movement.walk(safeTile);
					return true;
				}
			}
			else if (beamTick >= 35)
			{
				if (!playerPoint.equals(dodgeTilte))
				{
					toaManager.print("Dodging beam to dodge tile");
					Movement.walk(dodgeTilte);
					return true;
				}
			}
			else if (!playerPoint.equals(safeTile))
			{
				toaManager.print("Dodging beam to safetile");
				Movement.walk(safeTile);
				return true;
			}

		}
		else if (windmillTick > 10)
		{
			if (windmillTick > 38)
			{
				if (!playerPoint.equals(safeTile))
				{
					toaManager.print("Setting to safetile");
					Movement.walk(safeTile);
					return true;
				}
			}
			else if (windmillTick >= 35)
			{
				if (!playerPoint.equals(dodgeTilte))
				{
					toaManager.print("Dodging windmill to dodge tile");
					Movement.walk(dodgeTilte);
					return true;
				}
			}
			else if (windmillTick >= 30)
			{
				if (!playerPoint.equals(safeTile))
				{
					toaManager.print("Dodging windmill to safe tile");
					Movement.walk(safeTile);
					return true;
				}
			}
			else if (windmillTick >= 18 && windmillTick <= 22)
			{
				if (!playerPoint.equals(dodgeTilte))
				{
					toaManager.print("Dodging windmill to dodge tile");
					Movement.walk(dodgeTilte);
					return true;
				}
			}
			else if (!playerPoint.equals(safeTile))
			{
				toaManager.print("Dodging windmill to safetile");
				Movement.walk(safeTile);
				return true;
			}
		}
		else if (!toaManager.wardens12.dangerTiles.isEmpty())
		{
			if (toaManager.wardens12.dangerTiles.containsKey(playerPoint))
			{
				WorldPoint nonDangerTile = toaManager.findClosestTile(getSafeTiles(), playerPoint);
				if(nonDangerTile == null){
					toaManager.print("Non danger tile is null in wardenp2 dodge special");
					return false;
				}
				toaManager.print("Dodging danger to " + toaManager.worldPointString(nonDangerTile));
				Movement.walk(nonDangerTile);
				return true;
			}
		}
		else if (!playerPoint.equals(safeTile))
		{
			toaManager.print("Moving back to safetile");
			Movement.walk(safeTile);
			return true;
		}
		return false;
	}

	public ArrayList<WorldPoint> getSafeTiles()
	{
		ArrayList<WorldPoint> returnList = new ArrayList<>(toaManager.wardens12.tilesInWardenRange);
		returnList.removeAll(toaManager.wardens12.prisonTiles);
		returnList.removeAll(listFromMap(toaManager.wardens12.dangerTiles));
		returnList.removeAll(toaManager.wardens12.wardenMeleeTiles());
		return returnList;
	}

	public ArrayList<WorldPoint> listFromMap(Map<WorldPoint, Integer> map)
	{
		ArrayList<WorldPoint> returnList = new ArrayList<>();
		for (Map.Entry<WorldPoint, Integer> entry : map.entrySet())
		{
			returnList.add(entry.getKey());
		}
		return returnList;
	}

}
