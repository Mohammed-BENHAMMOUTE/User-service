package inpt.aseds.userservice.model;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "user_profile")
@Getter @Setter
public class UserProfile {
    @Id
    private String id;

    @Column(length = 500)
    private String bio;

    @Column(name = "avatar_url")
    private String avatarUrl;

    @Column(name = "channel_id")
    private String channelId;

    @Column(name = "display_name")
    private String displayName;


    @ElementCollection
    @CollectionTable(name  = "user_social_links" , joinColumns = @JoinColumn(name = "user_id"))
    @MapKeyColumn(name = "platform")
    @Column(name = "link")
    private Map<String, String> socialLinks = new HashMap<>();

    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Column(name = "updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = new Date();
        updatedAt = new Date();
    }
    @PreUpdate
    protected void onUpdate() {
        updatedAt = new Date();
    }
}
