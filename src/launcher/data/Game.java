package launcher.data;

import lombok.*;

import java.io.Serializable;

/**
 * Created by Hack
 * Date: 02.09.2017 15:20
 *
 * Serializable POJO that contains data of an item
 */
@Data
@ToString
public class Game implements Serializable {
    private String url;
    private String name;
    private double rating;
    private double userScore;
    private String release;
    private String genre;
    private String type;
    private String imgUrl;

    public Game(String url) {
        this.url = url;
    }

    public int getId() {
        return DataManager.getInstance().getData().indexOf(this);
    }
}
