package ru.hse.online.serviceTests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.hse.online.exceptions.EntityNotFoundException;
import ru.hse.online.model.User;
import ru.hse.online.model.UsernameToUserId;
import ru.hse.online.repository.UserRepository;
import ru.hse.online.repository.UsernameToUserIdRepository;
import ru.hse.online.service.core.UserCoreService;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UsernameToUserIdRepository usernameToUserIdRepository;

    @InjectMocks
    private UserCoreService userCoreService;

    private User testUser;
    private UUID testUserId;
    private String testUsername;

    @BeforeEach
    void setUp() {
        testUserId = UUID.randomUUID();
        testUsername = "andrey";
        testUser = User.builder()
                .userId(testUserId)
                .username(testUsername)
                .build();
    }

    @Test
    void getExistingUserByNameReturnsUser() {
        UsernameToUserId usernameToUserId = UsernameToUserId.builder()
                .username(testUsername)
                .userId(testUserId)
                .build();

        when(usernameToUserIdRepository.findById(testUsername)).thenReturn(Optional.of(usernameToUserId));
        when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));

        User result = userCoreService.getUserByName(testUsername);

        assertEquals(testUser, result);
        verify(usernameToUserIdRepository).findById(testUsername);
        verify(userRepository).findById(testUserId);
    }

    @Test
    void getNonExistingUserByNameThrowsEntityNotFoundException() {
        when(usernameToUserIdRepository.findById(testUsername)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userCoreService.getUserByName(testUsername));
        verify(usernameToUserIdRepository).findById(testUsername);
        verify(userRepository, never()).findById(any());
    }

    @Test
    void getExistingUserByIdReturnsUser() {
        when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));

        User result = userCoreService.getUserById(testUserId);

        assertEquals(testUser, result);
        verify(userRepository).findById(testUserId);
    }

    @Test
    void getNonExistingUserByIdThrowsEntityNotFoundException() {
        when(userRepository.findById(testUserId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userCoreService.getUserById(testUserId));
        verify(userRepository).findById(testUserId);
    }

    @Test
    void saveValidUserSavesUser() {
        userCoreService.saveUser(testUser);

        verify(userRepository).save(testUser);
        verify(usernameToUserIdRepository).save(any(UsernameToUserId.class));
    }
}
