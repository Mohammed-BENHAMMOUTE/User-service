package inpt.aseds.userservice.domain.repository;

import inpt.aseds.userservice.domain.model.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User , Long> {
    Optional<User> findUserByUsername(@NotBlank(message = "Username cannot be blank") @Size(min = 3, max = 100, message = "Username must be between 3 and 100 characters") String username);

    Page<User> searchUsersByUsername(@NotBlank(message = "Username cannot be blank") @Size(min = 3, max = 100, message = "Username must be between 3 and 100 characters") String username, Pageable pageable);
//    User findByUsername(String username);
//    User findByEmail(String email);
//    User findByUsernameOrEmailOrPhoneNumber(String username, String email, String phoneNumber);
//
//    @Modifying
//    @Query("UPDATE User u SET u.isActive = :status WHERE u.id = :id")
//    void updateUserStatus(@Param("id") String id, @Param("status") boolean status);
}