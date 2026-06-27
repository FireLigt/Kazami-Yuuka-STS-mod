package YuukaMod.util;

import com.megacrit.cardcrawl.cards.AbstractCard;

public class GeneralUtils {
    public static String arrToString(Object[] arr) {
        if (arr == null)
            return null;
        if (arr.length == 0)
            return "";

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < arr.length - 1; ++i) {
            sb.append(arr[i]).append(", ");
        }
        sb.append(arr[arr.length - 1]);
        return sb.toString();
    }

    public static String removePrefix(String ID) {
        return ID.substring(ID.indexOf(":") + 1);
    }

    public static void makeTemporary(AbstractCard card) {
        if (card == null) {
            return;
        }

        card.isEthereal = true;
        card.exhaust = true;
        appendKeywordLine(card, " Ethereal ");
        appendKeywordLine(card, " Exhaust ");
        card.initializeDescription();
    }

    private static void appendKeywordLine(AbstractCard card, String keyword) {
        String description = card.rawDescription == null ? "" : card.rawDescription;
        if (!description.toLowerCase().contains(keyword.toLowerCase())) {
            card.rawDescription = description + " NL " + keyword;
        }
    }
}
