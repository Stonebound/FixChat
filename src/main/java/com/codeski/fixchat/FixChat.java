package com.codeski.fixchat;

public class FixChat {
	public enum Achievements {
		// @formatter:off
		ACQUIRE_IRON("Acquire Hardware"),
		BAKE_CAKE("The Lie"),
		BOOKCASE("Librarian"),
		BREED_COW("Repopulation"),
		BREW_POTION("Local Brewery"),
		BUILD_BETTER_PICKAXE("Getting an Upgrade"),
		BUILD_FURNACE("Hot Topic"),
		BUILD_HOE("Time to Farm!"),
		BUILD_PICKAXE("Time to Mine!"),
		BUILD_SWORD("Time to Strike!"),
		BUILD_WORKBENCH("Benchmarking"),
		COOK_FISH("Delicious Fish"),
		DIAMONDS_TO_YOU("Diamonds to you!"),
		ENCHANTMENTS("Enchanter"),
		END_PORTAL("The End?"),
		EXPLORE_ALL_BIOMES("Adventuring Time"),
		FLY_PIG("When Pigs Fly"),
		FULL_BEACON("Beaconator"),
		GET_BLAZE_ROD("Into Fire"),
		GET_DIAMONDS("DIAMONDS!"),
		GHAST_RETURN("Return to Sender"),
		KILL_COW("Cow Tipper"),
		KILL_ENEMY("Monster Hunter"),
		KILL_WITHER("The Beginning."),
		MAKE_BREAD("Bake Bread"),
		MINE_WOOD("Getting Wood"),
		NETHER_PORTAL("We Need to Go Deeper"),
		ON_A_RAIL("On A Rail"),
		OPEN_INVENTORY("Taking Inventory"),
		OVERKILL("Overkill"),
		OVERPOWERED("Overpowered"),
		SNIPE_SKELETON("Sniper Duel"),
		SPAWN_WITHER("The Beginning?"),
		THE_END("The End.");
		// @formatter:on
		private final String name;

		private Achievements(String name) {
			this.name = name;
		}

		@Override
		public String toString() {
			return name;
		}
	}

	public enum Strings {
		// @formatter:off
		AWAY(" is away from keyboard"),
		NO_WHISPER_REPLY("There's no whisper to reply to."),
		NON_PLAYER_REPLY("Non-players cannot respond because they cannot receive whispers."),
		NOT_AWAY(" is no longer away from keyboard");
		// @formatter:on
		private final String name;

		private Strings(String name) {
			this.name = name;
		}

		@Override
		public String toString() {
			return name;
		}
	}

	public static int _MPS = 1000;
	public static int _TPS = 20;
	public static int AWAY = 300 * _MPS;
	public static int INTERVAL = 30 * _TPS;
}
