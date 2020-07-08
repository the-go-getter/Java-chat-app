package com.neu.prattle.websocket;

import com.neu.prattle.model.InviteMessage;
import com.neu.prattle.model.Message;
import com.neu.prattle.model.User;
import com.neu.prattle.service.MessageService;
import com.neu.prattle.service.MessageServicePersistentImpl;
import com.neu.prattle.service.UserService;
import com.neu.prattle.service.UserServicePersistentImpl;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

import javax.websocket.RemoteEndpoint;
import javax.websocket.Session;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

public class ChatEndpointInviteTest {

    private static ChatEndpoint ce1;
    private static ChatEndpoint ce2;
    private static ChatEndpoint ce3;

    private static Session session1;
    private static Session session2;
    private static Session session3;

    private static String alice = "aliceInviteMock";
    private static String bob = "bobInviteMock";
    private static String charlie = "charlieInviteMock";
    private static String groupName = "inviteGroup";
    private static String password = "password";
    private static String createGroup = "\\creategroup";
    private static String inviteUser = "\\inviteuser";

    private static UserService userService;

    private static RemoteEndpoint.Basic remote1;
    private static RemoteEndpoint.Basic remote2;
    private static RemoteEndpoint.Basic remote3;
    private static MessageService messageService = MessageServicePersistentImpl.getInstance();

    @BeforeClass
    public static void setUpAll() throws Exception {
        userService = UserServicePersistentImpl.getInstance();
        User user1 = new User(alice, password);
        User user2 = new User(bob, password);
        User user3 = new User(charlie, password);
        userService.addUser(user1);
        userService.addUser(user2);
        userService.addUser(user3);

        ce1 = new ChatEndpoint();
        ce2 = new ChatEndpoint();
        ce3 = new ChatEndpoint();

        remote1 = mock(RemoteEndpoint.Basic.class);
        remote2 = mock(RemoteEndpoint.Basic.class);
        remote3 = mock(RemoteEndpoint.Basic.class);


        session1 = mock(Session.class);
        session2 = mock(Session.class);
        session3 = mock(Session.class);

        Mockito.when(session1.getId()).thenReturn("x-sess-id-1");
        Mockito.when(session2.getId()).thenReturn("x-sess-id-2");
        Mockito.when(session3.getId()).thenReturn("x-sess-id-3");

        Mockito.when(session1.getBasicRemote()).thenReturn(remote1);
        Mockito.when(session2.getBasicRemote()).thenReturn(remote2);
        Mockito.when(session3.getBasicRemote()).thenReturn(remote3);

        ce1.onOpen(session1, alice, password);
        ce2.onOpen(session2, bob, password);
        ce3.onOpen(session3, charlie, password);

        //alice only create group once.
        aliceCreateGroup();
    }


    @Test
    public void inviteUserNotExistTest() {
        try {
            Message message = Message.messageBuilder()
                    .setFrom(alice)
                    .setTo("testee")
                    .setMessageContent(String.format("%s %s %s", inviteUser, groupName, "Notexist")).build();
            ce1.onMessage(session1, message);
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void inviteUserNotMod() {
        try {
            Message message = Message.messageBuilder()
                    .setFrom(alice)
                    .setTo("testee")
                    .setMessageContent(String.format("%s %s %s", inviteUser, groupName, bob)).build();
            ce2.onMessage(session2, message);
        } catch (Exception e) {
            fail();
        }
    }


    @Test
    public void inviteUserTest() {
        try {
            Message message = Message.messageBuilder()
                    .setFrom(alice)
                    .setTo("testee")
                    .setMessageContent(String.format("%s %s %s", inviteUser, groupName, bob)).build();
            ce1.onMessage(session1, message);
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void inviteAcceptResponseTest() {
        try {
            InviteMessage inviteMessage = InviteMessage.messageBuilder().setFrom(alice).setTo(bob)
                    .setMessageContent("Invite").build();
            messageService.addMessage(inviteMessage);
            inviteMessage.setAcknowledged(true);
            inviteMessage.setGroupName(groupName);
            inviteMessage.setAccepted(true);
            ce1.onMessage(session1, inviteMessage);
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void inviteDeclineResponseTest() {
        try {
            InviteMessage inviteMessage = InviteMessage.messageBuilder().setFrom(alice).setTo(bob)
                    .setMessageContent("Invite").build();
            messageService.addMessage(inviteMessage);
            inviteMessage.setAcknowledged(true);
            inviteMessage.setGroupName(groupName);
            inviteMessage.setAccepted(false);
            ce1.onMessage(session1, inviteMessage);
        } catch (Exception e) {
            fail();
        }
    }


    private static void aliceCreateGroup() {
        Message createMsg = Message.messageBuilder()
                .setFrom(alice)
                .setTo("testee")
                .setMessageContent(String.format("%s %s", createGroup, groupName)).build();
        ce1.onMessage(session1, createMsg);
    }
}
