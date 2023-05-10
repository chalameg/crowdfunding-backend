package com.dxvalley.crowdfunding.admin.user;

import com.dxvalley.crowdfunding.exception.ResourceNotFoundException;
import com.dxvalley.crowdfunding.user.UserRepository;
import com.dxvalley.crowdfunding.user.UserService;
import com.dxvalley.crowdfunding.user.dto.UserDTO;
import com.dxvalley.crowdfunding.user.dto.UserDTOMapper;
import com.dxvalley.crowdfunding.user.userRole.UserStatus;
import com.dxvalley.crowdfunding.user.userRole.Users;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminUserService{
    private final UserRepository userRepository;
    private final UserDTOMapper userDTOMapper;
    private final UserService userService;


    public List<UserDTO> getUsers() {
        try {
            List<Users> users = userRepository.findAll();
            if (users.isEmpty())
                throw new ResourceNotFoundException("Currently, There is no User");
            List<UserDTO> mappedUsers = users.stream().map(userDTOMapper).collect(Collectors.toList());

            return mappedUsers;
        } catch (DataAccessException ex) {
            log.error("Error retrieving Users: {}", ex.getMessage());
            throw new RuntimeException("Error retrieving Users", ex);
        }
    }

    public UserDTO getUserById(Long userId) {
        return new UserDTOMapper().apply(userService.utilGetUserByUserId(userId));
    }

    public UserDTO getUserByUsername(String username) {
        return new UserDTOMapper().apply(userService.utilGetUserByUsername(username));
    }

    public UserDTO activate_ban(Long userId) {
        Users user = userService.utilGetUserByUserId(userId);

        if (user.getUserStatus().equals(UserStatus.ACTIVE))
            user.setUserStatus(UserStatus.BANNED);
        else
            user.setUserStatus(UserStatus.ACTIVE);

        return new UserDTOMapper().apply(userRepository.save(user));
    }


    public void delete(String username) {
        try {
            Users user = userService.utilGetUserByUsername(username);
            userRepository.delete(user);

        } catch (DataAccessException ex) {
            log.error("Error Deleting User : {}", ex.getMessage());
            throw new RuntimeException("Error Deleting User", ex);
        }
    }

}
