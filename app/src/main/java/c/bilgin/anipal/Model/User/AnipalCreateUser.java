package c.bilgin.anipal.Model.User;

import java.util.Date;

public interface AnipalCreateUser {
    AnipalUser createUser(String userUUID, String emailAddress,
                          String firstName, String lastName, String photoURL, Date birthday);
}
