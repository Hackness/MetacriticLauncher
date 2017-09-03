package launcher.parse;

import launcher.data.Game;
import javafx.util.Pair;

/**
 * Created by Hack
 * Date: 02.09.2017 12:45
 *
 * The class is processing parse of page content
 */
public enum WebPage {
    Metacritic(
            new Pair<>("<span itemprop=\"ratingValue\">", "</span>"),
            new Pair<>("<div class=\"metascore_w user large game", "</div>"),
            new Pair<>("<meta property=\"og:title\" content=\"", "\">"),
            new Pair<>("<span class=\"data\" itemprop=\"datePublished\">", "</span>"),
            new Pair<>("<span class=\"data\" itemprop=\"genre\">", "</span>"),
            new Pair<>("<meta property=\"og:image\" content=\"", "\">")
    );

    private Pair<String, String> ratingTags;
    private Pair<String, String> nameTags;
    private Pair<String, String> releaseTags;
    private Pair<String, String> genreTags;
    private Pair<String, String> userScoreTags;
    private Pair<String, String> imageTags;

    WebPage(Pair<String, String> ratingTags, Pair<String, String> userScoreTags, Pair<String, String> nameTags,
            Pair<String, String> releaseTags, Pair<String, String> genreTags, Pair<String, String> imageTags) {
        this.ratingTags = ratingTags;
        this.nameTags = nameTags;
        this.releaseTags = releaseTags;
        this.genreTags = genreTags;
        this.userScoreTags = userScoreTags;
        this.imageTags = imageTags;
    }

    /**
     * Parse the presented item by it's url content
     * @param game - the item that would be consume new parsed info
     * @return - the item
     */
    public Game parse(Game game) {
        String content = Grabber.getInstance().getContent(game.getUrl());
        if (content.contains(ratingTags.getKey()))
            game.setRating(Double.parseDouble(parsePair(content, ratingTags)));
        else
            game.setRating(0);
        game.setName(parsePair(content, nameTags));
        game.setRelease(parsePair(content, releaseTags));
        game.setGenre(multiParsePair(content, genreTags));
        if (content.contains(userScoreTags.getKey())) {
            String usTemp = parsePair(content, userScoreTags);
            usTemp = usTemp.substring(usTemp.indexOf(">") + 1);
            try {
                game.setUserScore(Double.parseDouble(usTemp));
            } catch (NumberFormatException e) {
                game.setUserScore(0);
            }
        } else
            game.setUserScore(0);
        game.setImgUrl(parsePair(content, imageTags));
        return game;
    }

    /**
     * Returns a value between pair's sequencers
     * @param content - content of the page
     * @param pair - sequencers pair
     * @return - value between
     */
    private String parsePair(String content, Pair<String, String> pair) {
        int start = content.indexOf(pair.getKey()) + pair.getKey().length();
        return content.substring(start, content.indexOf(pair.getValue(), start));
    }

    /**
     * Returns values between pair's sequencers splitted by , found on a line
     * @param content - content of the page
     * @param pair - sequencers pair
     * @return - concatenated string
     */
    private String multiParsePair(String content, Pair<String, String> pair) {
        int start = 0;
        int lineStart;
        content = content.substring(lineStart = content.indexOf(pair.getKey()), content.indexOf("\n", lineStart));
        StringBuilder sb = new StringBuilder();
        while (true) {
            int end = content.indexOf(pair.getValue(), start);
            if (end == -1)
                return sb.toString();
            if (sb.length() != 0)
                sb.append(", ");
            sb.append(content.substring(content.indexOf(pair.getKey(), start) + pair.getKey().length(), end));
            start = end + pair.getValue().length();
        }
    }
}
