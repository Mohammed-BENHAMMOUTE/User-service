package inpt.aseds.userservice.domain.grpcImpl;

import com.google.protobuf.Empty;
import inpt.aseds.userservice.application.mappers.UserMappers;
import inpt.aseds.userservice.application.services.UserService;
import inpt.aseds.userservice.domain.exceptions.UserNotFoundException;
import inpt.aseds.userservice.domain.exceptions.UserOperationException;
import inpt.aseds.userservice.domain.exceptions.UserValidationException;
import inpt.aseds.userservice.infrastructure.grpc.*;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.grpc.server.service.GrpcService;

/**
 * gRPC service implementation for user operations.
 * Handles protocol buffer conversion and delegates business logic to UserService.
 */
@GrpcService
public class GrpcUserServiceImp extends UserServiceGrpc.UserServiceImplBase {
    
    private static final Logger log = LoggerFactory.getLogger(GrpcUserServiceImp.class);
    
    private final UserService userService;
    
    public GrpcUserServiceImp(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void getUser(GetUserRequest request, StreamObserver<User> responseObserver) {
        try {
            log.debug("Getting user with ID: {}", request.getId());
            
            inpt.aseds.userservice.domain.model.User user = userService.getUserById(request.getId());
            User response = UserMappers.toGrpcUser(user);
            
            responseObserver.onNext(response);
            responseObserver.onCompleted();
            
        } catch (UserNotFoundException e) {
            log.warn("User not found: {}", e.getMessage());
            responseObserver.onError(Status.NOT_FOUND
                    .withDescription(e.getMessage())
                    .asRuntimeException());
        } catch (UserValidationException e) {
            log.warn("Validation error: {}", e.getMessage());
            responseObserver.onError(Status.INVALID_ARGUMENT
                    .withDescription(e.getMessage())
                    .asRuntimeException());
        } catch (Exception e) {
            log.error("Unexpected error getting user with ID: {}", request.getId(), e);
            responseObserver.onError(Status.INTERNAL
                    .withDescription("Internal server error")
                    .asRuntimeException());
        }
    }

    @Override
    public void getUserByUsername(GetUserByUsernameRequest request, StreamObserver<User> responseObserver) {
        try {
            log.debug("Getting user with username: {}", request.getUsername());
            
            inpt.aseds.userservice.domain.model.User user = userService.getUserByUsername(request.getUsername());
            User response = UserMappers.toGrpcUser(user);
            
            responseObserver.onNext(response);
            responseObserver.onCompleted();
            
        } catch (UserNotFoundException e) {
            log.warn("User not found: {}", e.getMessage());
            responseObserver.onError(Status.NOT_FOUND
                    .withDescription(e.getMessage())
                    .asRuntimeException());
        } catch (UserValidationException e) {
            log.warn("Validation error: {}", e.getMessage());
            responseObserver.onError(Status.INVALID_ARGUMENT
                    .withDescription(e.getMessage())
                    .asRuntimeException());
        } catch (Exception e) {
            log.error("Unexpected error getting user with username: {}", request.getUsername(), e);
            responseObserver.onError(Status.INTERNAL
                    .withDescription("Internal server error")
                    .asRuntimeException());
        }
    }

    @Override
    public void updateUser(UpdateUserRequest request, StreamObserver<User> responseObserver) {
        try {
            log.debug("Updating user with ID: {}", request.getId());
            
            inpt.aseds.userservice.domain.model.User updates = createUserFromUpdateRequest(request);
            inpt.aseds.userservice.domain.model.User updatedUser = userService.updateUser(request.getId(), updates);
            User response = UserMappers.toGrpcUser(updatedUser);
            
            responseObserver.onNext(response);
            responseObserver.onCompleted();
            
        } catch (UserNotFoundException e) {
            log.warn("User not found for update: {}", e.getMessage());
            responseObserver.onError(Status.NOT_FOUND
                    .withDescription(e.getMessage())
                    .asRuntimeException());
        } catch (UserValidationException e) {
            log.warn("Validation error during update: {}", e.getMessage());
            responseObserver.onError(Status.INVALID_ARGUMENT
                    .withDescription(e.getMessage())
                    .asRuntimeException());
        } catch (UserOperationException e) {
            log.warn("Operation error during update: {}", e.getMessage());
            responseObserver.onError(Status.ALREADY_EXISTS
                    .withDescription(e.getMessage())
                    .asRuntimeException());
        } catch (Exception e) {
            log.error("Unexpected error updating user with ID: {}", request.getId(), e);
            responseObserver.onError(Status.INTERNAL
                    .withDescription("Internal server error")
                    .asRuntimeException());
        }
    }

    @Override
    public void deleteUser(DeleteUserRequest request, StreamObserver<Empty> responseObserver) {
        try {
            log.debug("Deleting user with ID: {}", request.getId());
            
            userService.deleteUser(request.getId());
            
            responseObserver.onNext(Empty.getDefaultInstance());
            responseObserver.onCompleted();
            
        } catch (UserNotFoundException e) {
            log.warn("User not found for deletion: {}", e.getMessage());
            responseObserver.onError(Status.NOT_FOUND
                    .withDescription(e.getMessage())
                    .asRuntimeException());
        } catch (UserValidationException e) {
            log.warn("Validation error during deletion: {}", e.getMessage());
            responseObserver.onError(Status.INVALID_ARGUMENT
                    .withDescription(e.getMessage())
                    .asRuntimeException());
        } catch (UserOperationException e) {
            log.warn("Operation error during deletion: {}", e.getMessage());
            responseObserver.onError(Status.FAILED_PRECONDITION
                    .withDescription(e.getMessage())
                    .asRuntimeException());
        } catch (Exception e) {
            log.error("Unexpected error deleting user with ID: {}", request.getId(), e);
            responseObserver.onError(Status.INTERNAL
                    .withDescription("Internal server error")
                    .asRuntimeException());
        }
    }

    @Override
    public void listUsers(ListUsersRequest request, StreamObserver<ListUsersResponse> responseObserver) {
        try {
            log.debug("Listing users - page: {}, size: {}", request.getPage(), request.getSize());
            
            PageRequest pageable = PageRequest.of(request.getPage(), request.getSize());
            Page<inpt.aseds.userservice.domain.model.User> userPage = userService.getAllUsers(pageable);
            
            ListUsersResponse response = UserMappers.toListUsersResponse(
                    userPage.getContent(),
                    userPage.getTotalPages(),
                    userPage.getTotalElements()
            );
            
            responseObserver.onNext(response);
            responseObserver.onCompleted();
            
        } catch (UserValidationException e) {
            log.warn("Validation error during list users: {}", e.getMessage());
            responseObserver.onError(Status.INVALID_ARGUMENT
                    .withDescription(e.getMessage())
                    .asRuntimeException());
        } catch (UserOperationException e) {
            log.warn("Operation error during list users: {}", e.getMessage());
            responseObserver.onError(Status.FAILED_PRECONDITION
                    .withDescription(e.getMessage())
                    .asRuntimeException());
        } catch (Exception e) {
            log.error("Unexpected error listing users", e);
            responseObserver.onError(Status.INTERNAL
                    .withDescription("Internal server error")
                    .asRuntimeException());
        }
    }

    @Override
    public void searchUsers(SearchUsersRequest request, StreamObserver<ListUsersResponse> responseObserver) {
        try {
            log.debug("Searching users with query: {}, page: {}, size: {}", 
                     request.getQuery(), request.getPage(), request.getSize());
            
            PageRequest pageable = PageRequest.of(request.getPage(), request.getSize());
            Page<inpt.aseds.userservice.domain.model.User> userPage = 
                    userService.searchUsers(request.getQuery(), pageable);
            
            ListUsersResponse response = UserMappers.toListUsersResponse(
                    userPage.getContent(),
                    userPage.getTotalPages(),
                    userPage.getTotalElements()
            );
            
            responseObserver.onNext(response);
            responseObserver.onCompleted();
            
        } catch (UserValidationException e) {
            log.warn("Validation error during search: {}", e.getMessage());
            responseObserver.onError(Status.INVALID_ARGUMENT
                    .withDescription(e.getMessage())
                    .asRuntimeException());
        } catch (UserOperationException e) {
            log.warn("Operation error during search: {}", e.getMessage());
            responseObserver.onError(Status.FAILED_PRECONDITION
                    .withDescription(e.getMessage())
                    .asRuntimeException());
        } catch (Exception e) {
            log.error("Unexpected error searching users with query: {}", request.getQuery(), e);
            responseObserver.onError(Status.INTERNAL
                    .withDescription("Internal server error")
                    .asRuntimeException());
        }
    }

    @Override
    public void checkUsernameExists(CheckUsernameRequest request, StreamObserver<CheckUsernameResponse> responseObserver) {
        try {
            log.debug("Checking username existence: {}", request.getUsername());
            
            boolean exists = userService.usernameExists(request.getUsername());
            CheckUsernameResponse response = CheckUsernameResponse.newBuilder()
                    .setExists(exists)
                    .build();
            
            responseObserver.onNext(response);
            responseObserver.onCompleted();
            
        } catch (UserValidationException e) {
            log.warn("Validation error during username check: {}", e.getMessage());
            responseObserver.onError(Status.INVALID_ARGUMENT
                    .withDescription(e.getMessage())
                    .asRuntimeException());
        } catch (UserOperationException e) {
            log.warn("Operation error during username check: {}", e.getMessage());
            responseObserver.onError(Status.FAILED_PRECONDITION
                    .withDescription(e.getMessage())
                    .asRuntimeException());
        } catch (Exception e) {
            log.error("Unexpected error checking username: {}", request.getUsername(), e);
            responseObserver.onError(Status.INTERNAL
                    .withDescription("Internal server error")
                    .asRuntimeException());
        }
    }

    @Override
    public void deactivateUser(deactivateUserRequest request, StreamObserver<Empty> responseObserver) {
        try {
            log.debug("Deactivating user with ID: {}", request.getId());
            
            userService.deactivateUser(request.getId());
            
            responseObserver.onNext(Empty.getDefaultInstance());
            responseObserver.onCompleted();
            
        } catch (UserNotFoundException e) {
            log.warn("User not found for deactivation: {}", e.getMessage());
            responseObserver.onError(Status.NOT_FOUND
                    .withDescription(e.getMessage())
                    .asRuntimeException());
        } catch (UserValidationException e) {
            log.warn("Validation error during deactivation: {}", e.getMessage());
            responseObserver.onError(Status.INVALID_ARGUMENT
                    .withDescription(e.getMessage())
                    .asRuntimeException());
        } catch (UserOperationException e) {
            log.warn("Operation error during deactivation: {}", e.getMessage());
            responseObserver.onError(Status.FAILED_PRECONDITION
                    .withDescription(e.getMessage())
                    .asRuntimeException());
        } catch (Exception e) {
            log.error("Unexpected error deactivating user with ID: {}", request.getId(), e);
            responseObserver.onError(Status.INTERNAL
                    .withDescription("Internal server error")
                    .asRuntimeException());
        }
    }
    
    /**
     * Creates a User domain object from gRPC UpdateUserRequest.
     * Safely extracts StringValue fields to prevent toString() issues.
     */
    private inpt.aseds.userservice.domain.model.User createUserFromUpdateRequest(UpdateUserRequest request) {
        inpt.aseds.userservice.domain.model.User user = new inpt.aseds.userservice.domain.model.User();
        
        if (request.hasUsername()) {
            user.setUsername(request.getUsername().getValue());
        }
        if (request.hasEmail()) {
            user.setEmail(request.getEmail().getValue());
        }
        if (request.hasBio()) {
            user.setBio(request.getBio().getValue());
        }
        if (request.hasAvatarUrl()) {
            user.setAvatarUrl(request.getAvatarUrl().getValue());
        }
        if (request.hasChannelId()) {
            user.setChannelId(request.getChannelId().getValue());
        }
        
        return user;
    }
}
