package com.example.nexatron.model.constants;

public enum Stage
{
	KC_AREA,
	CLAIMING_DEATH,
	BANK,
	NEX_SMOKE,
	NEX_SHADOW,
	NEX_BLOOD,
	NEX_ICE,
	MINION_SMOKE,
	MINION_SHADOW,
	MINION_BLOOD,
	MINION_ICE,
	NEX_ZAROS,
	NEX_DEAD,
	GRAND_EXCHANGE,
	NONE;

	Stage()
	{
	}

	public String toString()
	{
		String parent = super.toString();
		char var10000 = parent.charAt(0);
		return "" + var10000 + parent.substring(1).replaceAll("_", " ").trim().toLowerCase();
	}
}
