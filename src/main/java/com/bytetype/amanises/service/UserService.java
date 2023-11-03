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

    public User getOrCreateUser(UserPayload request) throws RoleNotFoundException {
        if (request.getId() != null) {
            User user = userRepository.findById(request.getId()).orElse(null);
            if (user != null) return user;
        }
        if (request.getAddress() != null) {
            User user = userRepository.findByAddress(request.getAddress()).orElse(null);
            if (user != null) return user;

            HashSet<Role> roles = new HashSet<>();
            roles.add(roleRepository.findByName(RoleType.GUEST).orElseThrow(RoleNotFoundException::new));

            user = User.createFrom(request, roles);

            return userRepository.save(user);
        }

        return null;
    }
}
