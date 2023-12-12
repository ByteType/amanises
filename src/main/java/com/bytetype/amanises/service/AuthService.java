package com.bytetype.amanises.service;

import com.bytetype.amanises.exception.NameExistException;
import com.bytetype.amanises.exception.RoleNotFoundException;
import com.bytetype.amanises.exception.UserExistException;
import com.bytetype.amanises.exception.UserNotFoundException;
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
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    /**
     * Authenticates a user with given login credentials.
     * <p>
     * This method uses the {@link AuthenticationManager} to attempt to authenticate the user with
     * the provided username and password. Upon successful authentication, it sets the corresponding
     * {@link Authentication} object in the {@link SecurityContextHolder} to maintain the security context.
     * It then extracts user details and authorities to create a {@link UserInfoResponse} object.
     * </p>
     *
     * @param request the login request containing the user's credentials
     * @return a {@link UserInfoResponse} containing the authenticated user's information, including their ID,
     * username, email, and roles.
     * @throws AuthenticationException if authentication fails due to invalid credentials or other authentication-related issues.
     */
    @Transactional
    public UserInfoResponse authenticateUser(LoginRequest request) throws UserNotFoundException {
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        if (roles.contains(RoleType.ROLE_GUEST.toString()))
            throw new UserNotFoundException();

        return new UserInfoResponse(
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail(),
                userDetails.getPhone(),
                userDetails.getAddress(),
                roles
        );
    }

    /**
     * Registers a new user with the given sign-up request data.
     * <p>
     * This method checks if the username or address already exists in the database and throws corresponding exceptions
     * if the conditions are not met. It either upgrade the existing guest user or creates a new user entry if the
     * address is not found in the database. After setting the user roles based on the provided role names, it saves
     * the new user record in the repository.
     * </p>
     *
     * @param request the sign-up request containing the new user's information, such as username, password, email, and address.
     * @return a {@link UserInfoResponse} containing the saved user's ID, username, email, and roles.
     * @throws NameExistException if a user with the same username already exists in the system.
     * @throws UserExistException if a user with the same address already exists in the system with roles assigned.
     * @throws RoleNotFoundException if the role provided in the request does not exist.
     */
    @Transactional
    public UserInfoResponse registerUser(SignupRequest request) throws NameExistException, UserExistException, RoleNotFoundException {
        User user;

        if (userRepository.existsByAddress(request.getAddress())) {
            user = userRepository.findByAddress(request.getAddress()).orElseThrow();
            Role guest = roleRepository.findByName(RoleType.ROLE_GUEST).orElseThrow(RoleNotFoundException::new);
            if (user.getRoles().isEmpty() || user.getRoles().contains(guest)) {
                user.setUsername(request.getUsername());
                user.setEmail(request.getEmail());
                user.setPhone(request.getPhone());
                user.setPassword(passwordEncoder.encode(request.getPassword()));
                user.setAddress(request.getAddress());
            } else throw new UserExistException();
        } else {
            if (userRepository.existsByUsername(request.getUsername())) throw new NameExistException();

            user = User.createFrom(request, passwordEncoder.encode(request.getPassword()));
        }

        Set<String> strRoles = request.getRole();
        Set<Role> roles = new HashSet<>();

        if (strRoles == null) {
            roles.add(roleRepository.findByName(RoleType.ROLE_GUEST).orElseThrow(RoleNotFoundException::new));
        } else {
            for (String role : strRoles) {
                switch (role) {
                    case "user":
                        roles.add(roleRepository.findByName(RoleType.ROLE_USER).orElseThrow(RoleNotFoundException::new));
                        break;
                    case "driver":
                        roles.add(roleRepository.findByName(RoleType.ROLE_DRIVER).orElseThrow(RoleNotFoundException::new));
                        break;
                    default:
                        roles.add(roleRepository.findByName(RoleType.ROLE_GUEST).orElseThrow(RoleNotFoundException::new));
                }
            }
        }

        user.setRoles(roles);
        user = userRepository.save(user);

        return new UserInfoResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getPhone(),
                user.getAddress(),
                user.getRoles().stream()
                        .map(role -> role.getName().toString())
                        .collect(Collectors.toList())
        );
    }

    /**
     * Removes a user from the system by anonymizing their record.
     * <p>
     * This method attempts to find a user by their ID. If found, it anonymizes the user by setting their
     * password to null and assigning them a 'GUEST' role. It does not delete the user record from the database.
     * </p>
     *
     * @param id the unique identifier of the user to be removed.
     * @return true if the user exists and has been anonymized successfully, false if the user does not exist.
     * @throws RoleNotFoundException if the 'GUEST' role does not exist in the repository.
     */
    @Transactional
    public boolean removeUser(Long id) throws RoleNotFoundException {
        if (userRepository.existsById(id)) {
            User user = userRepository.findById(id).orElse(null);
            if (user == null) return false;

            HashSet<Role> roles = new HashSet<>();
            roles.add(roleRepository.findByName(RoleType.ROLE_GUEST).orElseThrow(RoleNotFoundException::new));

            user.setPassword(null);
            user.setRoles(roles);

            userRepository.save(user);

            return true;
        } else {
            return false;
        }
    }
}