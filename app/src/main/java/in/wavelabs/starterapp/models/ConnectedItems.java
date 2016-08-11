package in.wavelabs.starterapp.models;

import java.util.List;

/**
 * Created by vivekkiran on 7/14/16.
 */

public class ConnectedItems {
    public List<SocialConnects> getSocialConnects() {
        return socialConnects;
    }

    public void setSocialConnects(List<SocialConnects> socialConnects) {
        this.socialConnects = socialConnects;
    }

    List<SocialConnects> socialConnects;
}
