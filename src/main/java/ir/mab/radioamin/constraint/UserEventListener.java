package ir.mab.radioamin.constraint;

import ir.mab.radioamin.entity.User;
import org.springframework.data.rest.core.annotation.HandleBeforeCreate;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;

@RepositoryEventHandler
public class UserEventListener {
    @HandleBeforeCreate
    public void handleUserSave(User user) {
        System.out.println("HandleBeforeCreate " + user);
        // … you can now deal with Person in a type-safe way
    }
}
