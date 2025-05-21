package ru.hse.online.GroupService.data;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class GroupManager {
    private final Map<String, Long> userToGroupId = new ConcurrentHashMap<>();
    private final Map<Long, List<String>> groupIdToUsers = new ConcurrentHashMap<>();
    private final AtomicLong groupId = new AtomicLong(0);

    private void logGroups() {
        System.out.println(userToGroupId);
        System.out.println(groupIdToUsers);
    }
    
    public Long addUser(String user) {
//        logGroups();
        userToGroupId.put(user, groupId.get());

        List<String> group = groupIdToUsers.get(groupId.get());
        if (group == null) {
            group = new ArrayList<>();
        }

        group.add(user);
        groupIdToUsers.put(groupId.get(), group);
//        logGroups();
        return groupId.getAndIncrement();
    }

    public void removeUser(String user) {
//        logGroups();
        Long id = userToGroupId.get(user);

        groupIdToUsers.get(id).remove(user);
        if (groupIdToUsers.get(id).isEmpty()) {
            groupIdToUsers.remove(id);
        }

        userToGroupId.remove(user);
//        logGroups();
    }

    public void joinUserGroups(String user1, String user2) {
//        logGroups();
        removeUser(user1);
        Long id2 = userToGroupId.get(user2);
        userToGroupId.put(user1, id2);
        groupIdToUsers.get(id2).add(user1);
//        logGroups();
    }

    public void moveUserToNewGroup(String user) {
//        logGroups();
        removeUser(user);
        addUser(user);
//        logGroups();
    }

    public Long getGroupId(String user) {
        return userToGroupId.get(user);
    }

    public List<String> getGroup(Long id) {
        return groupIdToUsers.get(id);
    }
}
