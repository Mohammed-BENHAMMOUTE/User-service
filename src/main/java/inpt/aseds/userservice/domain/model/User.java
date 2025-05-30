package inpt.aseds.userservice.domain.model;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.URL;
import java.util.Date;



@Entity
@Table(name = "user_table")
@Getter @Setter
public class User{
   @Id
    private Long id;

    @NotBlank(message = "Username cannot be blank")
    @Size(min = 3, max = 100, message = "Username must be between 3 and 100 characters")
    @Column(name = "username", nullable = false, length = 100)
    private String username;

    @Size(max = 500, message = "Bio must not exceed 500 characters")
    @Column(length = 500)
    private String bio;

    @URL(message = "Avatar URL must be a valid URL")
    @Column(name = "avatar_url")
    private String avatarUrl;

    @Pattern(regexp = "^[a-zA-Z0-9_-]{3,50}$", message = "Channel ID must be alphanumeric and between 3 and 50 characters")
    @Column(name = "channel_id")
    private String channelId;

    @Email(message = "Email must be valid")
    @NotBlank(message = "Email cannot be blank")
    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Column(name = "updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;

    @Column(name = "is_active")
    private boolean isActive = true;

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
