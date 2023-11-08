package com.bytetype.amanises.service;

import com.bytetype.amanises.exception.RoleNotFoundException;
import com.bytetype.amanises.exception.UserNotFoundException;
import com.bytetype.amanises.model.Role;
import com.bytetype.amanises.model.RoleType;
import com.bytetype.amanises.model.User;
import com.bytetype.amanises.payload.common.ParcelPayload;
import com.bytetype.amanises.payload.common.UserPayload;
import com.bytetype.amanises.payload.response.UserDetailResponse;
import com.bytetype.amanises.repository.ParcelRepository;
import com.bytetype.amanises.repository.RoleRepository;
import com.bytetype.amanises.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.stream.Collectors;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private ParcelRepository parcelRepository;

    public UserDetailResponse getUserById(Long id) throws UserNotFoundException {
        User user = userRepository.findById(id).orElseThrow(UserNotFoundException::new);

        return new UserDetailResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getPhone(),
                user.getAddress(),
                user.getRoles(),
                parcelRepository.findBySenderIdOrRecipientId(user.getId()).stream()
                        .map(ParcelPayload::createFrom)
                        .collect(Collectors.toList())
        );
    }

    /**
     * Retrieves an existing user by ID or address or creates a new one with a GUEST role.
     * <p>
     * The method first attempts to locate an existing user by ID if provided. If no such user exists,
     * it then tries to find a user by address. If the user is still not found, a new user record is created
     * with a GUEST role and the other details provided in the request.
     *
     * @param request the user payload containing user identification or creation information
     * @return the existing or newly created user, or null if the request does not contain an ID or address
     * @throws RoleNotFoundException if the GUEST role is not present in the role repository
     */
    public User getOrCreateUser(UserPayload request) throws RoleNotFoundException {
        if (request.getId() != null) {
            User user = userRepository.findById(request.getId()).orElse(null);
            if (user != null) return user;
        }
        if (request.getUsername() != null) {
            User user = userRepository.findById(request.getId()).orElse(null);
            if (user != null) return user;
        }
        if (request.getAddress() != null) {
            User user = userRepository.findByAddress(request.getAddress()).orElse(null);
            if (user != null) return user;

            HashSet<Role> roles = new HashSet<>();
            roles.add(roleRepository.findByName(RoleType.ROLE_GUEST).orElseThrow(RoleNotFoundException::new));

            user = User.createFrom(request, roles);

            return userRepository.save(user);
        }

        return null;
    }
}
