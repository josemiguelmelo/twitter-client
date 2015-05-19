package sdis.twitterclient.Models;


import sdis.twitterclient.Models.User;

public class Tweet {

    private String created_at;
    private long id;
    private String text;

    private User publisher;
    private User userMentions;


    private String publisherUsername;

    public Tweet(String text){
        this.text = text;
        this.created_at = null;
        this.id = 0;
        this.publisher = null;
        this.userMentions = null;
    }

    public Tweet(String publisherUsername, long id, String created_at, String text){
        this.created_at = created_at;
        this.id = id;
        this.text = text;
        this.publisher = publisher;
        this.userMentions = null;
        this.publisherUsername = publisherUsername;
    }


    public String getPublisherUsername() {
        return publisherUsername;
    }

    public void setPublisherUsername(String publisherUsername) {
        this.publisherUsername = publisherUsername;
    }

    public User getPublisher() {
        return publisher;
    }

    public void setPublisher(User publisher) {
        this.publisher = publisher;
    }

    public String getText(){ return this.text; }
    public void setText(String text){ this.text = text; }

    public User getUserMentions() {
        return userMentions;
    }

    public void setUserMentions(User userMentions) {
        this.userMentions = userMentions;
    }


    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

}
