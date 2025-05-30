package inpt.aseds.userservice.domain.grpcImpl;
import com.google.protobuf.Empty;
import inpt.aseds.userservice.application.mappers.UserMappers;
import inpt.aseds.userservice.domain.repository.UserRepository;
import inpt.aseds.userservice.infrastructure.grpc.*;
import io.grpc.stub.StreamObserver;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.grpc.server.service.GrpcService;

@AllArgsConstructor
@GrpcService
public class GrpcUserServiceImp extends UserServiceGrpc.UserServiceImplBase {
    private final UserRepository userRepository;

    @Override
    public void getUser(GetUserRequest request, StreamObserver<User> responseObserver) {
//        super.getUser(request, responseObserver);
        inpt.aseds.userservice.domain.model.User user = userRepository.findById(request.getId())
                .orElse(null);
        if (user != null) {
           User response = UserMappers.toGrpcUser(user);
            if (response == null) {
                responseObserver.onError(new Exception("Failed to convert user to gRPC User"));
                return;
            }
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } else {
            responseObserver.onError(new Exception("User not found"));
        }

    }

    @Override
    public void getUserByUsername(GetUserByUsernameRequest request, StreamObserver<User> responseObserver) {
//        super.getUserByUsername(request, responseObserver);
        inpt.aseds.userservice.domain.model.User user = userRepository.findUserByUsername(request.getUsername()).orElse(null);
        if (user != null) {
            User response = UserMappers.toGrpcUser(user);
            if (response == null) {
                responseObserver.onError(new Exception("Failed to convert user to gRPC User"));
                return;
            }
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } else {
            responseObserver.onError(new Exception("User not found"));
        }
    }

    @Override
    public void updateUser(UpdateUserRequest request, StreamObserver<User> responseObserver) {
//        super.updateUser(request, responseObserver);
        inpt.aseds.userservice.domain.model.User user = userRepository.findById(request.getId())
                .orElse(null);
        if (user == null) {
            responseObserver.onError(new Exception("User not found"));
            return;
        }

        // Update user fields
        if (request.hasUsername()) {
            String username = request.getUsername().getValue();
            if (!username.isEmpty()) {
                user.setUsername(username);
            }
        }
        if (request.hasEmail()) {
            user.setEmail(request.getEmail().toString());
        }
        if (request.hasBio()) {
            user.setBio(request.getBio().toString());
        }
        if (request.hasAvatarUrl()) {
            user.setAvatarUrl(request.getAvatarUrl().toString());
        }
        if (request.hasChannelId()) {
            user.setChannelId(request.getChannelId().toString());
        }

        // Save updated user
        inpt.aseds.userservice.domain.model.User updatedUser = userRepository.save(user);
        User response = UserMappers.toGrpcUser(updatedUser);
        if (response == null) {
            responseObserver.onError(new Exception("Failed to convert updated user to gRPC User"));
            return;
        }

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void deleteUser(DeleteUserRequest request, StreamObserver<Empty> responseObserver) {
        //super.deleteUser(request, responseObserver);
        inpt.aseds.userservice.domain.model.User user = userRepository.findById(request.getId())
                .orElse(null);
        if (user == null) {
            responseObserver.onError(new Exception("User not found"));
        } else {
            userRepository.delete(user);
            responseObserver.onNext(Empty.getDefaultInstance());
            responseObserver.onCompleted();
        }
    }

   @Override
   public void listUsers(ListUsersRequest request, StreamObserver<ListUsersResponse> responseObserver) {
       int page = request.getPage();
       int size = request.getSize();

       if (page < 0 || size <= 0) {
           responseObserver.onError(new IllegalArgumentException("Invalid pagination parameters"));
           return;
       }

       try {
           org.springframework.data.domain.Pageable pageable =
               org.springframework.data.domain.PageRequest.of(page, size);

           org.springframework.data.domain.Page<inpt.aseds.userservice.domain.model.User> userPage =
               userRepository.findAll(pageable);

           ListUsersResponse response = UserMappers.toListUsersResponse(
               userPage.getContent(),
               userPage.getTotalPages(),
               userPage.getTotalElements()
           );

           responseObserver.onNext(response);
           responseObserver.onCompleted();
       } catch (Exception e) {
           responseObserver.onError(new Exception("Failed to list users: " + e.getMessage()));
       }
   }

   @Override
    public void searchUsers(SearchUsersRequest request, StreamObserver<ListUsersResponse> responseObserver) {
//        super.searchUsers(request, responseObserver);
        String query = request.getQuery();
        int page = request.getPage();
        int size = request.getSize();

        if (query.isEmpty()) {
            responseObserver.onError(new IllegalArgumentException("Search query cannot be empty"));
            return;
        }

        if (page < 0 || size <= 0) {
            responseObserver.onError(new IllegalArgumentException("Invalid pagination parameters"));
            return;
        }

        try {
            Pageable pageable = Pageable.ofSize(size).withPage(page);
            org.springframework.data.domain.Page<inpt.aseds.userservice.domain.model.User> userPage =
                    userRepository.searchUsersByUsername(query, pageable);

            ListUsersResponse response = UserMappers.toListUsersResponse(
                    userPage.getContent(),
                    userPage.getTotalPages(),
                    userPage.getTotalElements()
            );

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(new Exception("Failed to search users: " + e.getMessage()));
        }
    }

    @Override
    public void checkUsernameExists(CheckUsernameRequest request, StreamObserver<CheckUsernameResponse> responseObserver) {
//        super.checkUsernameExists(request, responseObserver);
        String username = request.getUsername();
        if (username.isEmpty()) {
            responseObserver.onError(new IllegalArgumentException("Username cannot be empty"));
            return;
        }
        boolean exists = userRepository.findUserByUsername(username).isPresent();
        CheckUsernameResponse response = CheckUsernameResponse.newBuilder()
                .setExists(exists)
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }


    @Override
    public void deactivateUser(deactivateUserRequest request, StreamObserver<Empty> responseObserver) {
        inpt.aseds.userservice.domain.model.User user = userRepository.findById(request.getId())
                .orElse(null);
        if (user == null) {
            responseObserver.onError(new Exception("User not found"));
            return;
        }

        user.setActive(false);
        userRepository.save(user);
        responseObserver.onNext(Empty.getDefaultInstance());
        responseObserver.onCompleted();
    }
}
