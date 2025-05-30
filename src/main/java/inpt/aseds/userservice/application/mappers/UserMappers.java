package inpt.aseds.userservice.application.mappers;


import com.google.protobuf.Timestamp;
import inpt.aseds.userservice.domain.model.User;
import com.google.protobuf.StringValue;
import com.google.protobuf.BoolValue;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class UserMappers {

    /**
     * Convert a domain User to gRPC User
     */
    public static inpt.aseds.userservice.infrastructure.grpc.User toGrpcUser(User domainUser) {
        if (domainUser == null) {
            return null;
        }

        inpt.aseds.userservice.infrastructure.grpc.User.Builder builder = inpt.aseds.userservice.infrastructure.grpc.User.newBuilder()
                .setId(domainUser.getId())
                .setUsername(domainUser.getUsername())
                .setIsActive(domainUser.isActive())
                .setEmail(domainUser.getEmail());

        if (domainUser.getBio() != null) {
            builder.setBio(domainUser.getBio());
        }

        if (domainUser.getAvatarUrl() != null) {
            builder.setAvatarUrl(domainUser.getAvatarUrl());
        }

        if (domainUser.getChannelId() != null) {
            builder.setChannelId(domainUser.getChannelId());
        }

        if (domainUser.getCreatedAt() != null) {
            builder.setCreatedAt(dateToTimestamp(domainUser.getCreatedAt()));
        }

        if (domainUser.getUpdatedAt() != null) {
            builder.setUpdatedAt(dateToTimestamp(domainUser.getUpdatedAt()));
        }

        return builder.build();
    }

    /**
     * Convert a gRPC User to domain User
     */
    public static User toDomainUser(inpt.aseds.userservice.infrastructure.grpc.User grpcUser) {
        if (grpcUser == null) {
            return null;
        }

        User user = new User();
        user.setId(grpcUser.getId());
        user.setUsername(grpcUser.getUsername());
        user.setBio(grpcUser.getBio());
        user.setAvatarUrl(grpcUser.getAvatarUrl());
        user.setChannelId(grpcUser.getChannelId());
        user.setEmail(grpcUser.getEmail());
        user.setActive(grpcUser.getIsActive());

        if (grpcUser.hasCreatedAt()) {
            user.setCreatedAt(timestampToDate(grpcUser.getCreatedAt()));
        }

        if (grpcUser.hasUpdatedAt()) {
            user.setUpdatedAt(timestampToDate(grpcUser.getUpdatedAt()));
        }

        return user;
    }

    /**
     * Apply updates from UpdateUserRequest to domain User
     */
    public static void applyUpdates(User user, inpt.aseds.userservice.infrastructure.grpc.UpdateUserRequest request) {
        if (request.hasUsername()) {
            user.setUsername(request.getUsername().getValue());
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

        if (request.hasIsActive()) {
            user.setActive(request.getIsActive().getValue());
        }
    }

    /**
     * Convert a list of domain Users to gRPC ListUsersResponse
     */
    public static inpt.aseds.userservice.infrastructure.grpc.ListUsersResponse toListUsersResponse(
            List<User> users, int totalPages, long totalElements) {

        List<inpt.aseds.userservice.infrastructure.grpc.User> grpcUsers = users.stream()
                .map(UserMappers::toGrpcUser)
                .collect(Collectors.toList());

        return inpt.aseds.userservice.infrastructure.grpc.ListUsersResponse.newBuilder()
                .addAllUsers(grpcUsers)
                .setTotalPages(totalPages)
                .setTotalElements(totalElements)
                .build();
    }

    /**
     * Convert Java Date to Protobuf Timestamp
     */
    private static Timestamp dateToTimestamp(Date date) {
        long seconds = date.getTime() / 1000;
        int nanos = (int) ((date.getTime() % 1000) * 1000000);
        return Timestamp.newBuilder().setSeconds(seconds).setNanos(nanos).build();
    }

    /**
     * Convert Protobuf Timestamp to Java Date
     */
    private static Date timestampToDate(Timestamp timestamp) {
        return Date.from(Instant.ofEpochSecond(timestamp.getSeconds(), timestamp.getNanos()));
    }
}