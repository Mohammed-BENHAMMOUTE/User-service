package inpt.aseds.userservice.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "user_preferences")
@Getter @Setter
public class userPreferences {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private String userId; // References Keycloak ID

    @Column(name = "dark_mode")
    private boolean darkMode = false;

    @Column(name = "language")
    private String language = "en";

    @Column(name = "autoplay_videos")
    private boolean autoplayVideos = true;

    @Enumerated(EnumType.STRING)
    @Column(name = "streaming_quality")
    private StreamingQuality streamingQuality = StreamingQuality.AUTO;

    @Column(name = "notification_email")
    private boolean notificationEmail = true;

    @Column(name = "notification_push")
    private boolean notificationPush = true;
}

enum StreamingQuality {
    LOW, MEDIUM, HIGH, AUTO
}