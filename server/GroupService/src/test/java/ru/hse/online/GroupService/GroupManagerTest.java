package ru.hse.online.GroupService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.hse.online.GroupService.data.GroupManager;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

class GroupManagerTest {
    private GroupManager groupManager;

    @BeforeEach
    void setUp() {
        groupManager = new GroupManager();
    }

    @Test
    void addUserShouldCreateNewGroup() {
        Long groupId1 = groupManager.addUser("user1");
        Long groupId2 = groupManager.addUser("user2");

        assertNotNull(groupId1);
        assertNotNull(groupId2);
        assertEquals(0L, groupId1);
        assertEquals(1L, groupId2);
        assertEquals(1, groupManager.getGroup(0L).size());
    }

    @Test
    void removeUserShouldUpdateMaps() {
        groupManager.addUser("user1");
        groupManager.removeUser("user1");

        assertNull(groupManager.getGroupId("user1"));
        assertNull(groupManager.getGroup(0L));
    }

    // TODO
    @Test
    void joinUserGroupsShouldMergeGroups() {
        Long group1 = groupManager.addUser("user1");
        Long group2 = groupManager.addUser("user2");

        groupManager.joinUserGroups("user1", "user2");

        assertEquals(group2, groupManager.getGroupId("user1"));
        assertEquals(2, groupManager.getGroup(group2).size());
        assertNull(groupManager.getGroup(group1));
    }

    @Test
    void moveUserToNewGroupShouldCreateIsolatedGroup() {
        Long originalGroup = groupManager.addUser("user1");
        groupManager.moveUserToNewGroup("user1");

        Long newGroup = groupManager.getGroupId("user1");
        assertNotEquals(originalGroup, newGroup);
        assertEquals(1, groupManager.getGroup(newGroup).size());
        assertTrue(groupManager.getGroup(originalGroup).isEmpty());
    }

    @Test
    void getGroupShouldReturnCorrectMembers() {
        groupManager.addUser("user1");
        groupManager.addUser("user2");

        List<String> group0 = groupManager.getGroup(0L);
        List<String> group1 = groupManager.getGroup(1L);

        assertTrue(group0.contains("user1"));
        assertTrue(group1.contains("user2"));
    }
}
