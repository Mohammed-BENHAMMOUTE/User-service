package inpt.aseds.userservice.domain.repository;

import inpt.aseds.userservice.domain.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User , Long> {
//    User findByUsername(String username);
//    User findByEmail(String email);
//    User findByUsernameOrEmailOrPhoneNumber(String username, String email, String phoneNumber);
//
//    @Modifying
//    @Query("UPDATE User u SET u.isActive = :status WHERE u.id = :id")
//    void updateUserStatus(@Param("id") String id, @Param("status") boolean status);
}