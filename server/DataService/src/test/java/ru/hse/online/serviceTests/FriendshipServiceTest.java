package ru.hse.online.serviceTests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.hse.online.model.User;
import ru.hse.online.repository.UserRepository;
import ru.hse.online.service.core.FriendshipCoreService;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FriendshipServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private FriendshipCoreService friendshipCoreService;

    private UUID userId;
    private UUID friendId1;
    private UUID friendId2;
    private User user;
    private User friend1;
    private User friend2;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        friendId1 = UUID.randomUUID();
        friendId2 = UUID.randomUUID();

        friend1 = User.builder().userId(friendId1).username("friend1").build();
        friend2 = User.builder().userId(friendId2).username("friend2").build();

        user = User.builder()
                .userId(userId)
                .username("andrey")
                .friends(Arrays.asList(friendId1, friendId2))
                .build();
    }

    @Test
    void getFriendsListReturnsListOfFriends() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.findById(friendId1)).thenReturn(Optional.of(friend1));
        when(userRepository.findById(friendId2)).thenReturn(Optional.of(friend2));

        List<User> expectedFriends = Arrays.asList(friend1, friend2);
        List<User> actualFriends = friendshipCoreService.getFriendsList(userId);

        assertEquals(expectedFriends.size(), actualFriends.size());
        assertEquals(expectedFriends.get(0), actualFriends.get(0));
        assertEquals(expectedFriends.get(1), actualFriends.get(1));

        verify(userRepository).findById(userId);
        verify(userRepository).findById(friendId1);
        verify(userRepository).findById(friendId2);
    }

    @Test
    void getFriendsListReturnsEmptyList() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        List<User> actualFriends = friendshipCoreService.getFriendsList(userId);

        assertEquals(0, actualFriends.size());
        verify(userRepository).findById(userId);
    }
}