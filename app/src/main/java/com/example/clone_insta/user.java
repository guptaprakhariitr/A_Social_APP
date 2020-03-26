package com.example.clone_insta;

public class user {
    private String email,first,last,country,profile_pic="";
   public user(String email,String first,String last,String country)
    {
        this.email=email;
        this.first=first;
        this.last=last;
        this.country=country;
    }

    public void setProfile_pic(String profile_pic) {
        this.profile_pic = profile_pic;
    }

    public String getEmail() {
        return email;
    }

    public String getFirst() {
        return first;
    }

    public String getLast() {
        return last;
    }

    public String getCountry() {
        return country;
    }

    public String getProfile_pic() {
        return profile_pic;
    }
}
