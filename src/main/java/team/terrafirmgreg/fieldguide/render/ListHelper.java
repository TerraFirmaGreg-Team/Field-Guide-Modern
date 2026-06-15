package team.terrafirmgreg.fieldguide.render;

public class ListHelper {

    public static String generateListItemStart(int level) {
        
        level = Math.max(1, Math.min(level, 9));

        char bullet = (level % 2 == 0) ? '\u25E6' : '\u2022';

        if (level > 1) {
            return "<li style=\"margin-left: " + (level - 1) + "em;\">" + bullet + " ";
        } else {
            return "<li>" + bullet + " ";
        }
    }

    public static boolean isListFormat(String key) {
        return key.matches("li\\d?");
    }

    public static int extractListLevel(String key) {
        if (!isListFormat(key)) {
            return 1;
        }

        if (key.length() == 2) {
            return 1; 
        }

        char levelChar = key.charAt(2);
        if (Character.isDigit(levelChar)) {
            return Character.digit(levelChar, 10);
        }

        return 1;
    }

    public static char getBullet(int level) {
        return (level % 2 == 0) ? '\u25E6' : '\u2022';
    }
}