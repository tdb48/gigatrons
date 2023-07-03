package com.example.toagigatron.model.puzzlemodel;

import com.example.Utility.ObjectUtil;
import com.example.toagigatron.model.constants.ToaConstants;
import java.io.InputStream;
import java.util.ArrayList;
import net.runelite.api.GameObject;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;

public class AkkhaPuzzle
{

	public ArrayList<AkkhaPuzzleRoomTile> roomTiles;
	public AkkhaPuzzleSolution solution;
	public String roomMatrix = "";
	public String layoutName = "";
	WorldArea area;
	ArrayList<WorldPoint> worldPointList;
	ArrayList<AkkhaPuzzleRoomTile> blockingWalls;
	ArrayList<AkkhaPuzzleRoomTile> mineableWalls;
	ArrayList<AkkhaPuzzleRoomTile> fixedMirrors;
	ArrayList<AkkhaPuzzleRoomTile> moveableMirrors;

	public AkkhaPuzzle(WorldArea area,
					   ArrayList<AkkhaPuzzleRoomTile> blockingWalls,
					   ArrayList<AkkhaPuzzleRoomTile> mineableWalls,
					   ArrayList<AkkhaPuzzleRoomTile> fixedMirrors,
					   ArrayList<AkkhaPuzzleRoomTile> moveableMirrors)
	{
		this.area = area;
		this.worldPointList = (ArrayList<WorldPoint>) area.toWorldPointList();
		this.blockingWalls = blockingWalls;
		this.mineableWalls = mineableWalls;
		this.fixedMirrors = fixedMirrors;
		this.moveableMirrors = moveableMirrors;
		this.roomTiles = new ArrayList<>();
		roomMatrix = "";
		solution = null;
		constructRoomTiles();
	}

	public void updateLocation(WorldPoint worldPoint, GameObject obj, String type)
	{
		AkkhaPuzzleRoomTile updatedTile = new AkkhaPuzzleRoomTile(worldPoint, obj);
		AkkhaPuzzleRoomTile updatedTileNullObject = new AkkhaPuzzleRoomTile(worldPoint, null);
		switch (type)
		{
			case "blockingwall":
				blockingWalls.removeIf(x -> x.getWorldPoint().equals(worldPoint));
				blockingWalls.add(updatedTile);
				break;
			case "fixedmirror":
				fixedMirrors.removeIf(x -> x.getWorldPoint().equals(worldPoint));
				fixedMirrors.add(updatedTile);
				break;
			case "moveablemirror":
				moveableMirrors.removeIf(x -> x.getWorldPoint().equals(worldPoint));
				moveableMirrors.add(updatedTile);
				break;
			case "moveablemirrordespawned":
				moveableMirrors.removeIf(x -> x.getWorldPoint().equals(worldPoint));
				break;
			case "mineablewall":
				mineableWalls.removeIf(x -> x.getWorldPoint().equals(worldPoint));
				mineableWalls.add(updatedTile);
				break;
		}
		roomTiles.removeIf(tile -> tile.getWorldPoint().equals(worldPoint));
		if (type.equals("moveablemirrordespawned"))
		{
			roomTiles.add(updatedTileNullObject);
		}
		else
		{
			roomTiles.add(updatedTile);
		}

	}

	public void constructRoomTiles()
	{
		for (WorldPoint wp : worldPointList)
		{
			GameObject obj = null;

			for (AkkhaPuzzleRoomTile fixedMirror : fixedMirrors)
			{
				if (fixedMirror.getWorldPoint().equals(wp))
				{
					// System.out.println("mirror");
					obj = fixedMirror.getObject();
				}
			}
			for (AkkhaPuzzleRoomTile mineableWall : mineableWalls)
			{
				if (mineableWall.getWorldPoint().equals(wp))
				{
					// System.out.println("mineable wall");
					obj = mineableWall.getObject();
				}
			}
			for (AkkhaPuzzleRoomTile blockingWall : blockingWalls)
			{
				if (blockingWall.getWorldPoint().equals(wp))
				{
					// System.out.println("blocking wall");
					obj = blockingWall.getObject();
				}
			}
			for (AkkhaPuzzleRoomTile moveableMirror : moveableMirrors)
			{
				if (moveableMirror.getWorldPoint().equals(wp))
				{
					// System.out.println("moveable mirror");
					obj = moveableMirror.getObject();
				}
			}
			roomTiles.add(new AkkhaPuzzleRoomTile(wp, obj));
		}
	}

	private String intToCharRepresentation(GameObject obj)
	{
		if (obj == null)
		{
			return "  ";
		}
		int objectID = obj.getId();
		String str = "  ";
		if (objectID == 45456)
		{
			str = "FM";
		}
		else if (objectID == 45460 || objectID == 45458)
		{
			//str = "B ";
			str = "W ";
		}
		else if (objectID == 45464 || objectID == 45462)
		{
			str = "W ";
		}
		else if (objectID == 45455)
		{
			//str = "M ";
		}
		else if (objectID == 45466)
		{
			//str = "R ";
			str = "W ";
		}
		return str;
	}

	public void generateMatrix()
	{
		StringBuilder r1 = new StringBuilder();
		StringBuilder r2 = new StringBuilder();
		StringBuilder r3 = new StringBuilder();
		StringBuilder r4 = new StringBuilder();
		StringBuilder r5 = new StringBuilder();
		StringBuilder r6 = new StringBuilder();
		StringBuilder r7 = new StringBuilder();
		StringBuilder r8 = new StringBuilder();
		StringBuilder r9 = new StringBuilder();
		StringBuilder r10 = new StringBuilder();
		StringBuilder r11 = new StringBuilder();
		StringBuilder r12 = new StringBuilder();
		StringBuilder r13 = new StringBuilder();
		StringBuilder r14 = new StringBuilder();
		StringBuilder r15 = new StringBuilder();
		StringBuilder r16 = new StringBuilder();
		StringBuilder r17 = new StringBuilder();
		StringBuilder r18 = new StringBuilder();
		StringBuilder r19 = new StringBuilder();
		int rowMin = roomTiles.get(0).getWorldPoint().getY();
		int rowMax = roomTiles.get(roomTiles.size() - 1).getWorldPoint().getY();
		for (AkkhaPuzzleRoomTile tile : roomTiles)
		{
			String charRepresentation = intToCharRepresentation(tile.getObject());
			int y = tile.getWorldPoint().getY();
			if (y == rowMin)
			{
				r1.append("[ ").append(charRepresentation).append(" ]");
			}
			else if (y == rowMin + 1)
			{
				r2.append("[ ").append(charRepresentation).append(" ]");
			}
			else if (y == rowMin + 2)
			{
				r3.append("[ ").append(charRepresentation).append(" ]");
			}
			else if (y == rowMin + 3)
			{
				r4.append("[ ").append(charRepresentation).append(" ]");
			}
			else if (y == rowMin + 4)
			{
				r5.append("[ ").append(charRepresentation).append(" ]");
			}
			else if (y == rowMin + 5)
			{
				r6.append("[ ").append(charRepresentation).append(" ]");
			}
			else if (y == rowMin + 6)
			{
				r7.append("[ ").append(charRepresentation).append(" ]");
			}
			else if (y == rowMin + 7)
			{
				r8.append("[ ").append(charRepresentation).append(" ]");
			}
			else if (y == rowMin + 8)
			{
				r9.append("[ ").append(charRepresentation).append(" ]");
			}
			else if (y == rowMin + 9)
			{
				r10.append("[ ").append(charRepresentation).append(" ]");
			}
			else if (y == rowMin + 10)
			{
				r11.append("[ ").append(charRepresentation).append(" ]");
			}
			else if (y == rowMin + 11)
			{
				r12.append("[ ").append(charRepresentation).append(" ]");
			}
			else if (y == rowMin + 12)
			{
				r13.append("[ ").append(charRepresentation).append(" ]");
			}
			else if (y == rowMin + 13)
			{
				r14.append("[ ").append(charRepresentation).append(" ]");
			}
			else if (y == rowMin + 14)
			{
				r15.append("[ ").append(charRepresentation).append(" ]");
			}
			else if (y == rowMin + 15)
			{
				r16.append("[ ").append(charRepresentation).append(" ]");
			}
			else if (y == rowMin + 16)
			{
				r17.append("[ ").append(charRepresentation).append(" ]");
			}
			else if (y == rowMin + 17)
			{
				r18.append("[ ").append(charRepresentation).append(" ]");
			}
			else if (y == rowMax)
			{
				r19.append("[ ").append(charRepresentation).append(" ]");
			}
		}
		roomMatrix = roomMatrix + r19 + "\n" + r18 + "\n" + r17 + "\n" + r16 + "\n" + r15 + "\n"
			+ r14 + "\n" + r13 + "\n" + r12 + "\n" + r11 + "\n" + r10 + "\n" + r9 + "\n" + r8 + "\n" +
			r7 + "\n" + r6 + "\n" + r5 + "\n" + r4 + "\n" + r3 + "\n" + r2 + "\n" + r1;
	}

	public void setSolution(String name)
	{
		GameObject statue = ObjectUtil.getNearestGameObject(ToaConstants.AKKHA_SHIELD_STATUE);
		if (statue != null)
		{
			WorldPoint statueLoc = statue.getWorldLocation();
			ArrayList<WorldPoint> mineableWallLocations = new ArrayList<>();
			ArrayList<Mirror> mirrorLocations = new ArrayList<>();
			WorldPoint mineTile = null;
			WorldPoint wp = null;
			WorldPoint wp2 = null;
			Mirror m1 = null;
			Mirror m2 = null;
			Mirror m3 = null;

			switch (name)
			{
				case "N1.txt":
					wp = statueLoc.dx(1).dy(2);
					m1 = new Mirror(statueLoc.dx(1).dy(8), ToaConstants.AKKHA_PUZZLE_MIRROR_SW);
					m2 = new Mirror(statueLoc.dx(-8).dy(8), ToaConstants.AKKHA_PUZZLE_MIRROR_SE);
					m3 = new Mirror(statueLoc.dx(-8), ToaConstants.AKKHA_PUZZLE_MIRROR_NE);
					mineTile = statueLoc.dx(-4).dy(2);
					break;
				case "N2.txt":
					m1 = new Mirror(statueLoc.dy(7), ToaConstants.AKKHA_PUZZLE_MIRROR_SW);
					m2 = new Mirror(statueLoc.dx(-10).dy(7), ToaConstants.AKKHA_PUZZLE_MIRROR_SE);
					m3 = new Mirror(statueLoc.dx(-10), ToaConstants.AKKHA_PUZZLE_MIRROR_NE);
					mineTile = statueLoc.dx(-4).dy(2);
					break;
				case "N3.txt":
					wp = statueLoc.dx(-1).dy(2);
					m1 = new Mirror(statueLoc.dx(-1).dy(8), ToaConstants.AKKHA_PUZZLE_MIRROR_SW);
					m2 = new Mirror(statueLoc.dx(-8), ToaConstants.AKKHA_PUZZLE_MIRROR_NE);
					mineTile = statueLoc.dx(-4).dy(2);
					break;
				case "N4.txt":
					wp = statueLoc.dx(-1).dy(2);
					m1 = new Mirror(statueLoc.dx(-1).dy(3), ToaConstants.AKKHA_PUZZLE_MIRROR_SW);
					m2 = new Mirror(statueLoc.dx(-8).dy(3), ToaConstants.AKKHA_PUZZLE_MIRROR_SE);
					m3 = new Mirror(statueLoc.dx(-8), ToaConstants.AKKHA_PUZZLE_MIRROR_NE);
					mineTile = statueLoc.dx(-4).dy(2);
					break;
				case "N5.txt":
					m1 = new Mirror(statueLoc.dy(7), ToaConstants.AKKHA_PUZZLE_MIRROR_SW);
					m2 = new Mirror(statueLoc.dx(-11).dy(7), ToaConstants.AKKHA_PUZZLE_MIRROR_SE);
					m3 = new Mirror(statueLoc.dx(-11), ToaConstants.AKKHA_PUZZLE_MIRROR_NE);
					mineTile = statueLoc.dx(-4).dy(2);
					break;
				case "N6.txt":
					wp = statueLoc.dx(-1).dy(2);
					m1 = new Mirror(statueLoc.dx(-1).dy(3), ToaConstants.AKKHA_PUZZLE_MIRROR_SW);
					m2 = new Mirror(statueLoc.dx(-8), ToaConstants.AKKHA_PUZZLE_MIRROR_NE);
					mineTile = statueLoc.dx(-4).dy(2);
					break;
				case "N7.txt":
					m1 = new Mirror(statueLoc.dx(-10).dy(7), ToaConstants.AKKHA_PUZZLE_MIRROR_SE);
					mineTile = statueLoc.dx(-4).dy(2);
					break;
				case "S1.txt":
					wp = statueLoc.dy(-6);
					m1 = new Mirror(statueLoc.dx(-9).dy(-8), ToaConstants.AKKHA_PUZZLE_MIRROR_NE);
					m2 = new Mirror(statueLoc.dx(-9), ToaConstants.AKKHA_PUZZLE_MIRROR_SE);
					mineTile = statueLoc.dx(-3).dy(-2);
					break;
				case "S2.txt":
				case "S6.txt":
					wp = statueLoc.dy(-6);
					m1 = new Mirror(statueLoc.dy(-7), ToaConstants.AKKHA_PUZZLE_MIRROR_NW);
					m2 = new Mirror(statueLoc.dx(-9).dy(-7), ToaConstants.AKKHA_PUZZLE_MIRROR_NE);
					mineTile = statueLoc.dx(-3).dy(-2);
					break;
				case "S3.txt":
					wp = statueLoc.dy(-6);
					wp2 = statueLoc.dx(-8).dy(-4);
					m1 = new Mirror(statueLoc.dy(-7), ToaConstants.AKKHA_PUZZLE_MIRROR_NW);
					m2 = new Mirror(statueLoc.dx(-8), ToaConstants.AKKHA_PUZZLE_MIRROR_SE);
					mineTile = statueLoc.dx(-3).dy(-2);
					break;
				case "S4.txt":
					wp = statueLoc.dx(-10).dy(-3);
					m1 = new Mirror(statueLoc.dx(-10).dy(-8), ToaConstants.AKKHA_PUZZLE_MIRROR_NE);
					m2 = new Mirror(statueLoc.dx(4).dy(-8), ToaConstants.AKKHA_PUZZLE_MIRROR_NW);
					mineTile = statueLoc.dx(-3).dy(-2);
					break;
				case "S5.txt":
					wp = statueLoc.dy(-6);
					m1 = new Mirror(statueLoc.dy(-7), ToaConstants.AKKHA_PUZZLE_MIRROR_NW);
					m2 = new Mirror(statueLoc.dx(-9).dy(-7), ToaConstants.AKKHA_PUZZLE_MIRROR_NE);
					m3 = new Mirror(statueLoc.dx(-9), ToaConstants.AKKHA_PUZZLE_MIRROR_SE);
					mineTile = statueLoc.dx(-3).dy(-2);
					break;

			}
			if (wp != null)
			{
				mineableWallLocations.add(wp);
			}
			if (wp2 != null)
			{
				mineableWallLocations.add(wp2);
			}
			if (m1 != null)
			{
				mirrorLocations.add(m1);
			}
			if (m2 != null)
			{
				mirrorLocations.add(m2);
			}
			if (m3 != null)
			{
				mirrorLocations.add(m3);
			}
			solution = new AkkhaPuzzleSolution(mineableWallLocations, mirrorLocations, mineTile);
		}


	}

	private boolean charMatcher(ArrayList<Integer> inputStr, char[] matrix)
	{
		for (int i = 0; i < inputStr.size(); i++)
		{
			int charOne = inputStr.get(i);
			int charTwo = matrix[i];
			if (charOne != charTwo)
			{
//				System.out.println("istr size: " + inputStr.size() + "  mat size: " + matrix.length);
//							System.out.println("C1: " + charOne + " C2: " + charTwo);
//				System.out.println("Returning false,  char one ->  " + charOne + "  char two -> " + charTwo);
//		System.out.println("i: " + i);
				return false;
			}
		}
		return true;
	}

	public boolean matches()
	{
		ArrayList<String> solveNames = new ArrayList<>();
		solveNames.add("N1.txt");
		solveNames.add("N2.txt");
		solveNames.add("N3.txt");
		solveNames.add("N4.txt");
		solveNames.add("N5.txt");
		solveNames.add("N6.txt");
		solveNames.add("N7.txt");
		solveNames.add("S1.txt");
		solveNames.add("S2.txt");
		solveNames.add("S3.txt");
		solveNames.add("S4.txt");
		solveNames.add("S5.txt");
		solveNames.add("S6.txt");
		try
		{

			for (String s : solveNames)
			{
				byte[] array;
				InputStream input = this.getClass().getClassLoader().getResourceAsStream(s);
				if (input != null)
				{
					array = new byte[input.available()];
//					System.out.println("Bytes available -> " + input.available());
					input.read(array);

					String str = new String(array);
					str = str.trim();
//					System.out.println("Reading solve -> " + s);
//					System.out.println("STRING FROM TEXT FILE: ");
//					System.out.println(str);
//					System.out.println("STRING FROM MATRIX: ");
//					System.out.println(roomMatrix);
//					System.out.println("DOES IT MATCH? -> " + str.equals(roomMatrix));
					char[] strChar = str.toCharArray();
					ArrayList<Integer> strCharList = new ArrayList<>();
					for (char c : strChar)
					{
						strCharList.add((int) c);
					}
					strCharList.removeIf(x -> x == 13);

					char[] roomMatrixChar = roomMatrix.toCharArray();
//					System.out.println("size -> " + strCharList.size());
//					System.out.println("matrix size -> " + roomMatrixChar.length);
//					System.out.println("DOES IT MATCH? -> " + charMatcher(strCharList, roomMatrixChar));
//					if(s.equals("N4.txt")){
//						for(int i = 0; i < strCharList.size(); i++){
//							System.out.print((int) strCharList.get(i));
//							System.out.print(" ");
//						}
//						System.out.println("");
//						System.out.println("");
//						for(int i = 0; i < roomMatrixChar.length; i++){
//							System.out.print((int) roomMatrixChar[i]);
//							System.out.print(" ");
//						}
//					}

					if (charMatcher(strCharList, roomMatrixChar))
					{
//						System.out.println("FOUND SOLVE -> " + s);
						layoutName = s;
						setSolution(s);
						input.close();
						break;
					}
					input.close();
				}

//				URL resource = this.getClass().getClassLoader().getResource(s);
//				if (resource == null)
//				{
//					throw new IllegalArgumentException("file not found!");
//				}
//				else
//				{
//					File solve = new File(resource.getPath());
//					//System.out.println(solve.getAbsolutePath());
//					//System.out.println(solve.getCanonicalPath());
//					FileReader fr = new FileReader(solve);
//					BufferedReader br = new BufferedReader(fr);
//					String str = "";
//					System.out.println("Reading solve -> " + solve.getName());
//					while (true)
//					{
//						String tmp = br.readLine();
//						if (tmp == null)
//						{
//							break;
//						}
//						str = str + tmp + "\n";
//					}
//					System.out.println("STRING FROM TEXT FILE: ");
//					str = str.trim();
//					System.out.println(str);
//					System.out.println("STRING FROM MATRIX: ");
//					System.out.println(roomMatrix);
//					System.out.println("DOES IT MATCH? -> " + str.equals(roomMatrix));
//					if (str.equals(roomMatrix))
//					{
//						System.out.println("FOUND SOLVE -> " + solve.getName());
//						layoutName = solve.getName();
//						setSolution(solve.getName());
//						br.close();
//						fr.close();
//						break;
//					}
//					if (s.equals("S6.txt"))
//					{
//						br.close();
//						fr.close();
//					}
//
//				}
			}

//			URL resource = this.getClass().getClassLoader().getResource("akkhapuzzlelayouts");
//			if (resource == null)
//			{
//				throw new IllegalArgumentException("file not found!");
//			}
//			else
//			{
//				System.out.println("File name -> " + resource.getFile());
//				File solvesFolder = new File(resource.getFile());
//				System.out.println("Solves folder -> " + solvesFolder.getName());
//				File[] allSolves = solvesFolder.listFiles();
//				if (allSolves != null)
//				{
//					System.out.println("all solves is not null");
//					for (int i = 0; i < allSolves.length; i++)
//					{
//						File file = allSolves[i];
//						System.out.println(file.getName());
//						FileReader fr = new FileReader(file);
//						BufferedReader br = new BufferedReader(fr);
//						String str = "";
//						System.out.println("Reading solve -> " + file.getName());
//						while (true)
//						{
//							String tmp = br.readLine();
//							if (tmp == null)
//							{
//								break;
//							}
//							str = str + tmp + "\n";
//						}
//						System.out.println("STRING FROM TEXT FILE: ");
//						str = str.trim();
//						System.out.println(str);
//						System.out.println("STRING FROM MATRIX: ");
//						System.out.println(roomMatrix);
//						System.out.println("DOES IT MATCH? -> " + str.equals(roomMatrix));
//						if (str.equals(roomMatrix))
//						{
//							System.out.println("FOUND SOLVE -> " + file.getName());
//							layoutName = file.getName();
//							setSolution(file.getName());
//							br.close();
//							fr.close();
//							break;
//						}
//						if (i == allSolves.length - 1)
//						{
//							br.close();
//							fr.close();
//						}
//					}
//				} else {
//					System.out.println("All solves is null i guess");
//				}
//
//			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public void print()
	{
		for (AkkhaPuzzleRoomTile tile : roomTiles)
		{
			WorldPoint wp = tile.getWorldPoint();
			GameObject obj = tile.getObject();
			if (obj != null)
			{
				System.out.println("WP -> " + wp + " Object -> " + obj.getId());
			}
			else
			{
				System.out.println("WP -> " + wp + " Object -> " + "NULL");
			}

		}
	}


}
