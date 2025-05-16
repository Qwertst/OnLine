package ru.hse.online.GroupService.data;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class GroupManager {
    private final Map<String, Long> userToGroupId = new ConcurrentHashMap<>();
    private final Map<Long, List<String>> groupIdToUsers = new ConcurrentHashMap<>();
    private volatile long groupId = 0;

    private void logGroups() {
        System.out.println(userToGroupId);
        System.out.println(groupIdToUsers);
    }
    
    public Long addUser(String user) {
        logGroups();
        userToGroupId.put(user, groupId);
        System.out.println("Registered " + user);

        List<String> group = groupIdToUsers.get(groupId);
        if (group == null) {
            group = new ArrayList<>();
        }

        group.add(user);
        groupIdToUsers.put(groupId, group);
        logGroups();
        return groupId++;
    }

    public void removeUser(String user) {
        logGroups();
        long id = userToGroupId.get(user);

        groupIdToUsers.get(id).remove(user);
        if (groupIdToUsers.get(id).isEmpty()) {
            groupIdToUsers.remove(id);
        }

        userToGroupId.remove(user);
        logGroups();
    }

    public void joinUserGroups(String user1, String user2) {
        logGroups();
        System.out.println("Joining " + user1 + " and " + user2);

        System.out.println(userToGroupId);
        System.out.println(groupIdToUsers);

        long id1 = userToGroupId.get(user1);
        groupIdToUsers.get(id1).remove(user1);

        long id2 = userToGroupId.get(user2);
        userToGroupId.put(user1, id2);

        groupIdToUsers.get(id2).add(user1);

        logGroups();
    }

    public void moveUserToNewGroup(String user) {
        logGroups();
        long id = userToGroupId.get(user);
        groupIdToUsers.get(id).remove(user);

        userToGroupId.put(user, groupId);

        List<String> group = new ArrayList<>();
        group.add(user);
        groupIdToUsers.put(groupId, group);
        ++groupId;

        logGroups();
    }

    public Long getGroupId(String user) {
        return userToGroupId.get(user);
    }

    public List<String> getGroup(Long id) {
        return groupIdToUsers.get(id);
    }
}
