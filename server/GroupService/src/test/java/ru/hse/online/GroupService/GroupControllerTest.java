package ru.hse.online.GroupService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import ru.hse.online.GroupService.controller.GroupController;
import ru.hse.online.GroupService.data.GroupManager;
import ru.hse.online.GroupService.data.Invite;
import ru.hse.online.GroupService.data.Location;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;

class GroupControllerTest {
    private SimpMessagingTemplate messagingTemplate;
    private GroupController groupController;

    @BeforeEach
    void setUp() {
        messagingTemplate = Mockito.mock(SimpMessagingTemplate.class);
        groupController = new GroupController(messagingTemplate);
    }

    @Test
    void registerNewUserShouldTrimUsername() {
        Long groupId = groupController.registerNewUser("\"testUser\"");
        assertNotNull(groupId);
    }

    @Test
    void inviteShouldSendMessageToRecipient() {
        Invite invite = new Invite("user1", "user2");
        groupController.invite(invite);

        ArgumentCaptor<String> destinationCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);

        Mockito.verify(messagingTemplate).convertAndSendToUser(
            eq("user2"),
            destinationCaptor.capture(),
            messageCaptor.capture()
        );

        assertEquals("/msg", destinationCaptor.getValue());
        assertTrue(messageCaptor.getValue().contains("user1"));
    }

    @Test
    void updateLocationShouldBroadcastToGroup() throws Exception {
        groupController.registerNewUser("\"user1\"");
        groupController.registerNewUser("\"user2\"");
        groupController.joinGroup(new Invite("user1", "user2"));

        String json = "{\"from\":\"user1\",\"location\":{\"lat\":55.7,\"lng\":37.6}}";

        groupController.updateLocation(json);

        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        Mockito.verify(messagingTemplate, Mockito.atLeastOnce())
               .convertAndSendToUser(eq("user2"), eq("/msg"), messageCaptor.capture());

        String expectedJson = new Location(55.7, 37.6).toJson();
        assertEquals(expectedJson, messageCaptor.getValue());
    }

    @Test
    void quitGroupShouldCreateNewGroup() {
        groupController.registerNewUser("\"user1\"");
        groupController.quitGroup("\"user1\"");

        Long newGroupId = groupController.getGroupManager().getGroupId("user1");
        assertEquals(1L, newGroupId);
    }

    private GroupManager getGroupManager() {
        return groupController.getGroupManager();
    }
}
