package inpt.aseds.userservice.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Table(name = "channels") @Getter @Setter
public class Channel {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "owner_id")
    private String ownerId; // References Keycloak ID

    @Column(name = "channel_name", unique = true)
    private String channelName;

    @Column(name = "description", length = 1000)
    private String description;

    @Column(name = "banner_url")
    private String bannerUrl;

    @Column(name = "logo_url")
    private String logoUrl;

    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Column(name = "is_live")
    private boolean isLive = false;

    @Column(name = "is_verified")
    private boolean isVerified = false;

    @PrePersist
    protected void onCreate() {
        createdAt = new Date();
    }
}
