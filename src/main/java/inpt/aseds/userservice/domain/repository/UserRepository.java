package inpt.aseds.userservice.domain.repository;

import inpt.aseds.userservice.domain.model.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findUserByUsername(
        @NotBlank(message = "Username cannot be blank") 
        @Size(min = 3, max = 100, message = "Username must be between 3 and 100 characters") 
        String username
    );

    @Query("SELECT u FROM User u WHERE u.username LIKE %:query% AND u.isActive = true")
    Page<User> searchUsersByUsername(
        @Param("query") String query, 
        Pageable pageable
    );
    
    @Query("SELECT u FROM User u WHERE u.isActive = true")
    Page<User> findAllActiveUsers(Pageable pageable);
    
    Optional<User> findByEmail(String email);
    
    List<User> findByIdIn(List<Long> ids);
    
    boolean existsByUsername(String username);
    
    boolean existsByEmail(String email);
}