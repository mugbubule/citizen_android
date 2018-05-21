package jonas.emile.poll.model;

import java.util.UUID;

public class Choice {
    public UUID uuid;
    public String text;

    public Choice(UUID uuid, String text) {
        this.uuid = uuid;
        this.text = text;
    }
}
