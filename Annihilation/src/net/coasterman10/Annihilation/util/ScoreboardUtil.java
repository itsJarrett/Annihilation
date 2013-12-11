package net.coasterman10.Annihilation.util;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

public class ScoreboardUtil {
    private static final HashMap<String, Scoreboard> scoreboards = new HashMap<String, Scoreboard>();

    public static void registerBoard(String name) {
	ScoreboardManager manager = Bukkit.getScoreboardManager();
	Scoreboard scoreboard = manager.getNewScoreboard();
	scoreboards.put(name, scoreboard);
    }

    public static void setBoard(Player player, String board) {
	if (scoreboards.containsKey(board)) {
	    player.setScoreboard(scoreboards.get(board));
	}
    }

    public static void setTitle(String board, String title) {
	if (scoreboards.containsKey(board)) {
	    Scoreboard scoreboard = scoreboards.get(board);
	    Objective o = scoreboard.getObjective(DisplaySlot.SIDEBAR);
	    if (o == null) {
		o = scoreboard.registerNewObjective("obj", "dummy");
		o.setDisplaySlot(DisplaySlot.SIDEBAR);
	    }
	    o.setDisplayName(title);
	}
    }

    public static void setScore(String board, String name, int score) {
	if (scoreboards.containsKey(board)) {
	    Scoreboard scoreboard = scoreboards.get(board);
	    Objective o = scoreboard.getObjective(DisplaySlot.SIDEBAR);
	    if (o == null) {
		o = scoreboard.registerNewObjective("obj", "dummy");
		o.setDisplaySlot(DisplaySlot.SIDEBAR);
	    }
	    Score s = o.getScore(Bukkit.getOfflinePlayer(name));
	    s.setScore(score);
	}
    }

    public static void clear(String board) {
	if (scoreboards.containsKey(board)) {
	    Scoreboard scoreboard = scoreboards.get(board);
	    Objective o = scoreboard.getObjective(DisplaySlot.SIDEBAR);
	    if (o != null) {
		o.unregister();
	    }
	    scoreboard.clearSlot(DisplaySlot.SIDEBAR);
	}
    }
}
