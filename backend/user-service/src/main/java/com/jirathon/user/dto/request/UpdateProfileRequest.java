package com.jirathon.user.dto.request;

import jakarta.validation.constraints.Size;

import java.util.List;

public class UpdateProfileRequest {

    @Size(min = 2, max = 100, message = "Display name must be 2-100 characters")
    private String displayName;
    private String avatarUrl;
    @Size(max = 20, message = "Phone number too long")
    private String phone;
    @Size(max = 500, message = "Bio must be under 500 characters")
    private String bio;
    @Size(max = 20, message = "Maximum 20 skills")
    private List<String> skills;
    private String timezone;
    private String language;
    private String linkedinUrl;
    private String githubUrl;
    private String city;
    private String country;

    public String getDisplayName()     { return displayName; }
    public String getAvatarUrl()       { return avatarUrl; }
    public String getPhone()           { return phone; }
    public String getBio()             { return bio; }
    public List<String> getSkills()    { return skills; }
    public String getTimezone()        { return timezone; }
    public String getLanguage()        { return language; }
    public String getLinkedinUrl()     { return linkedinUrl; }
    public String getGithubUrl()       { return githubUrl; }
    public String getCity()            { return city; }
    public String getCountry()         { return country; }

    public void setDisplayName(String displayName)  { this.displayName = displayName; }
    public void setAvatarUrl(String avatarUrl)       { this.avatarUrl = avatarUrl; }
    public void setPhone(String phone)               { this.phone = phone; }
    public void setBio(String bio)                   { this.bio = bio; }
    public void setSkills(List<String> skills)       { this.skills = skills; }
    public void setTimezone(String timezone)         { this.timezone = timezone; }
    public void setLanguage(String language)         { this.language = language; }
    public void setLinkedinUrl(String linkedinUrl)   { this.linkedinUrl = linkedinUrl; }
    public void setGithubUrl(String githubUrl)       { this.githubUrl = githubUrl; }
    public void setCity(String city)                 { this.city = city; }
    public void setCountry(String country)           { this.country = country; }
}
