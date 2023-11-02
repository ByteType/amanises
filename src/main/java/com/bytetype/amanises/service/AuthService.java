package com.bytetype.amanises.service;

import com.bytetype.amanises.exception.NameExistException;
import com.bytetype.amanises.model.Role;
import com.bytetype.amanises.model.RoleType;
import com.bytetype.amanises.model.User;
import com.bytetype.amanises.payload.request.LoginRequest;
import com.bytetype.amanises.payload.request.SignupRequest;
import com.bytetype.amanises.payload.response.UserInfoResponse;
import com.bytetype.amanises.repository.RoleRepository;
import com.bytetype.amanises.repository.UserRepository;
import com.bytetype.amanises.security.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthService {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public UserInfoResponse authenticateUser(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return new UserInfoResponse(userDetails.getId(), userDetails.getUsername(), userDetails.getEmail(), roles);
    }

    /**
     * Register a user. If address exist, create a user upon the user.
     * @param signUpRequest The register request
     */
    public void registerUser(SignupRequest signUpRequest) throws NameExistException {
        if (userRepository.existsByUsername(signUpRequest.getUsername()))
            throw new NameExistException();

        User user;

        if (userRepository.existsByAddress(signUpRequest.getAddress())) {
            user = userRepository.findByAddress(signUpRequest.getAddress()).orElseThrow();
            user.setUsername(signUpRequest.getUsername());
            user.setEmail(signUpRequest.getEmail());
            user.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));
            user.setAddress(signUpRequest.getAddress());
        } else {
            user = User.createFrom(signUpRequest, passwordEncoder.encode(signUpRequest.getPassword()));
        }

        Set<String> strRoles = signUpRequest.getRole();
        Set<Role> roles = new HashSet<>();

        if (strRoles == null) {
            Role userRole = roleRepository.findByName(RoleType.GUEST)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                switch (role) {
                    case "user":
                        Role userRole = roleRepository.findByName(RoleType.USER)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(userRole);
                        break;
                    case "driver":
                        Role driverRole = roleRepository.findByName(RoleType.DRIVER)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(driverRole);
                        break;
                    default:
                        Role guestRole = roleRepository.findByName(RoleType.GUEST)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(guestRole);
                }
            });
        }

        user.setRoles(roles);
        userRepository.save(user);
    }

    public boolean removeUser(Long id) {
        if (userRepository.existsById(id)) {
            User user = userRepository.findById(id).orElse(null);
            if (user == null) return false;

            HashSet<Role> roles = new HashSet<>();
            roles.add(roleRepository.findByName(RoleType.GUEST)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found.")));

            user.setPassword(null);
            user.setRoles(roles);

            return true;
        } else {
            return false;
        }
    }
}