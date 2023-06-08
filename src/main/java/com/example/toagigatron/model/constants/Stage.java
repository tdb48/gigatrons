package com.example.toagigatron.model.constants;

public enum Stage
{
	OUTSIDE,
	INSIDE,
	KEPHRI_BOSS,
	KEPHRI_PUZZLE,
	BABA_BOSS,
	BABA_PUZZLE,
	AKKHA_BOSS,
	AKKHA_PUZZLE,
	ZEBAK_BOSS,
	ZEBAK_PUZZLE,
	WARDENS_P1,
	WARDENS_P2,
	WARDENS_P3,
	OUTSIDE_TOA,
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
