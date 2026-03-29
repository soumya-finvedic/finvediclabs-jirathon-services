package com.jirathon.org.dto;

import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.URL;

public class UpdateOrganizationRequest {

    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    @URL(message = "Logo URL must be a valid URL")
    private String logoUrl;

    @URL(message = "Website must be a valid URL")
    private String website;

    private Boolean autoApproveMembers;
    private Boolean allowPublicJoin;
    private Integer maxMembers;

    public String getName()               { return name; }
    public String getDescription()        { return description; }
    public String getLogoUrl()            { return logoUrl; }
    public String getWebsite()            { return website; }
    public Boolean getAutoApproveMembers(){ return autoApproveMembers; }
    public Boolean getAllowPublicJoin()   { return allowPublicJoin; }
    public Integer getMaxMembers()        { return maxMembers; }

    public void setName(String name)                        { this.name = name; }
    public void setDescription(String description)          { this.description = description; }
    public void setLogoUrl(String logoUrl)                  { this.logoUrl = logoUrl; }
    public void setWebsite(String website)                  { this.website = website; }
    public void setAutoApproveMembers(Boolean v)            { this.autoApproveMembers = v; }
    public void setAllowPublicJoin(Boolean allowPublicJoin) { this.allowPublicJoin = allowPublicJoin; }
    public void setMaxMembers(Integer maxMembers)           { this.maxMembers = maxMembers; }
}
