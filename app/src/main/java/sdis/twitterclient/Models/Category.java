package sdis.twitterclient.Models;


import java.io.Serializable;
import java.util.ArrayList;

public class Category implements Serializable{
    private String name;
    private ArrayList<User> users;

    public Category(String name){
        this.name= name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<User> getUsers() {
        return users;
    }

    public void setUsers(ArrayList<User> users) {
        this.users = users;
    }

    public boolean existsUser(User user){
        for(User userFromList: this.users){
            if(userFromList.getId() == user.getId())
                return true;
        }
        return false;
    }
    public void addUser(User user){
        if(!existsUser(user))
            this.users.add(user);
    }
}
