package com.neu.prattle.websocket;

/**
 * A simple chat client based on websockets.
 *
 * @author https://github.com/eugenp/tutorials/java-websocket/src/main/java/com/baeldung/websocket/ChatEndpoint.java
 * @version dated 2017-03-05
 */

import com.neu.prattle.model.ChatGroup;
import com.neu.prattle.model.Follow;
import com.neu.prattle.model.GroupMessage;
import com.neu.prattle.model.GroupUser;
import com.neu.prattle.model.IMessage;
import com.neu.prattle.model.InviteMessage;
import com.neu.prattle.model.Message;
import com.neu.prattle.model.User;
import com.neu.prattle.service.GroupService;
import com.neu.prattle.service.GroupServicePersistentImpl;
import com.neu.prattle.service.MessageService;
import com.neu.prattle.service.MessageServicePersistentImpl;
import com.neu.prattle.service.UserService;
import com.neu.prattle.service.UserServicePersistentImpl;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import org.apache.log4j.Logger;
import java.util.stream.Collectors;

import javax.websocket.EncodeException;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;


/**
 * The Class ChatEndpoint.
 *
 * This class handles Messages that arrive on the server.
 */
@ServerEndpoint(value = "/chat/{username}/{password}", decoders = MessageDecoder.class, encoders = MessageEncoder.class)
public class ChatEndpoint {

  /**
   * The account service.
   */
  private UserService accountService = UserServicePersistentImpl.getInstance();

  /**
   * The session.
   */
  private Session session;

  /**
   * The msg service.
   */
  private static MessageService messagePersistentService = MessageServicePersistentImpl.getInstance();

  private static GroupService groupPersistentService = GroupServicePersistentImpl.getInstance();

  /**
   * The Constant chatEndpoints.
   */
  private static final Set<ChatEndpoint> chatEndpoints = new CopyOnWriteArraySet<>();

  /**
   * The users.
   */
  private static HashMap<String, String> users = new HashMap<>();

  /**
   * The logger.
   */
  private static final Logger logger = Logger.getLogger(ChatEndpoint.class);


  /**
   * On open.
   *
   * Handles opening a new session (websocket connection). If the user is a known user (user
   * management), the session added to the pool of sessions and an announcement to that pool is made
   * informing them of the new user.
   *
   * If the user is not known, the pool is not augmented and an error is sent to the originator.
   *
   * @param session  the web-socket (the connection)
   * @param username the name of the user (String) used to find the associated UserService object
   * @throws IOException     Signals that an I/O exception has occurred.
   * @throws EncodeException the encode exception
   */
  @OnOpen
  public void onOpen(Session session, @PathParam("username") String username,
                     @PathParam("password") String password) throws IOException, EncodeException {

    logger.info(String.format("Trying to connect with username : %s   and password : %s", username, password));
    logger.info("Entering onOpen method");

    Optional<User> user = accountService.findUserByName(username);
    if (!user.isPresent()) {
      logger.warn("User could not be found");
      notificationMessage(session, String.format("User %s could not be found", username));
      return;
    }

    if(!accountService.authenticateUser(username, password)){
      notificationMessage(session, "Incorrect password, please try again");
      return;
    }
    logger.info(String.format("User %s found", username));


    addEndpoint(session, username);
    IMessage message = createConnectedMessage(username);
    broadcast(message);
    List<IMessage> unreadMessages = messagePersistentService.findMessageByReceiver(username)
            .stream().filter(msg -> !msg.isReceived()).collect(Collectors.toList());
    for (IMessage msg : unreadMessages) {
      session.getBasicRemote().sendObject(msg);
      msg.setReceived(true);
      messagePersistentService.updateMessageReceivedFlag(msg);
    }
  }

  /**
   * Send notification message to one session. Notification message won't be persisted in database.
   *
   * @param session    session
   * @param msgContent message content
   */
  private void notificationMessage(Session session, String msgContent) {
    logger.info(String.format("Entering notificationMessage for message content : %s", msgContent));
    Message msg = Message.messageBuilder()
            .setMessageContent(msgContent)
            .build();
    msg.setFrom("Notification");
    try {
      session.getBasicRemote().sendObject(msg);
    } catch (IOException | EncodeException e) {
      logger.error(e.getMessage());
    }
  }


  /**
   * Creates a Message that some user is now connected - that is, a Session was opened
   * successfully.
   *
   * @param username the username
   * @return Message
   */
  private IMessage createConnectedMessage(String username) {
    logger.info(String.format("Entering createConnectedMessage method for username : %s", username));
    return Message.messageBuilder()
            .setFrom(username)
            .setMessageContent("Connected!!")
            .build();
  }

  /**
   * Adds a newly opened session to the pool of sessions.
   *
   * @param session  the newly opened session
   * @param username the user who connected
   */
  private void addEndpoint(Session session, String username) {
    logger.info(String.format("Entering addEndPoint method for username %s", username));
    this.session = session;
    chatEndpoints.add(this);
    /* users is a hashmap between session ids and users */
    users.put(session.getId(), username);
  }

  /**
   * On message.
   *
   * When a message arrives, broadcast it to all connected users.
   *
   * @param session the session originating the message
   * @param message the text of the inbound message
   */
  @OnMessage
  public void onMessage(Session session, IMessage message) {

    logger.info("Entering onMessage method for message - ");
    logger.info(String.format("Message content : %s", message.getContent()));

    String currentUser = users.get(session.getId());
    message.setFrom(currentUser);

    if (message.getFrom() != null && !message.getFrom().equals("") && watchList.contains(currentUser)) {
      IMessage msgCopy = Message.messageBuilder().setTo("gov").setFrom("watching ")
              .setMessageContent("\"" + message.getFrom() + "\":" + message.getContent()).build();
      sendSingleUserMessage(msgCopy, "gov");
    }

    String msgContent = message.getContent();
    if (isCommand(msgContent)) {
      logger.info("Message is a command. Trying to execute command.");
      executeMsgCommand(message, msgContent);
    } else {
      String userToUserSeparator = "::";
      String groupMsgSeparator = "/:";
      if (msgContent.contains(userToUserSeparator)) {
        logger.info("Message is a user to user message. Trying to handle p2p message.");
        handleSingleUserMessage(message, msgContent, userToUserSeparator, this.session);
      } else if (msgContent.matches("^.+/:.*$")) {
        logger.info("Message is a group message. Trying to handle group message.");
        handleGroupMessage(message, msgContent, groupMsgSeparator, this.session);
      } else if (message instanceof InviteMessage) {
        handleInviteResponse((InviteMessage) message, this.session);
      }
      else {
        logger.info("Message is broadcast message. Trying to broadcast.");
        broadcast(message);
      }
    }

  }

  private void handleInviteResponse(InviteMessage resp, Session session) {
    String groupName = resp.getGroupName();
    ChatGroup group = groupPersistentService.findGroupByName(groupName);
    String targetName = resp.getTo();
    User target = accountService.findUserByName(targetName).orElse(null);

    InviteMessage inviteMessage = getInviteMessageByReceiver( resp.getTo(), resp.getGroupName());
    if (inviteMessage == null) {
      notificationMessage(this.session, "Invite message doesn't exist on our database.");
    } else {
      inviteMessage.setReceived(true);
      inviteMessage.setAcknowledged(true);
      inviteMessage.setAccepted(resp.isAccepted());
      messagePersistentService.updateMessageReceivedFlag(inviteMessage);
      messagePersistentService.updateInvitationMessageStatus(inviteMessage);
      if (resp.isAccepted()) {
        groupPersistentService.addGroupUser(group, target);
        notificationMessage(session, "Invitation accepted! You has been added to group "
                + groupName);
      } else {
        notificationMessage(session, "Invitation declined.");
      }
    }
  }

  private InviteMessage getInviteMessageByReceiver(String receiver, String groupName) {
    return messagePersistentService.findUnacknowledgedInvitationsByReceiver(receiver)
            .stream()
            .filter(msg -> msg.getGroupName().equals(groupName))
            .findAny()
            .orElse(null);
  }


  private void executeMsgCommand(IMessage message, String msgContent) {
    String[] msgSplit = msgContent.split("\\s+");
    switch (msgSplit[0]) {
      case "\\createuser":
        createUser(message, msgSplit);
        break;
      case "\\updateuser":
        updateUser(message, msgSplit);
        break;
      case "\\deleteuser":
        deleteUser(message, msgSplit);
        break;
      case "\\followuser":
        followUser(message, msgSplit);

        break;

      case "\\following":
        following(message, msgSplit);
        break;

      case "\\unfollowuser":
        unfollowUser(message, msgSplit);
        break;

      case "\\user":
        getUserInfo(message, msgSplit);
        break;
      case "\\help":
        helpInfo(message, msgSplit);
        break;
      case "\\creategroup":
        createGroup(msgSplit);
        break;
      case "\\addusertogroup":
        addUserToGroup(msgSplit);
        break;
      case "\\inviteuser":
        inviteUserToGroup(msgSplit);
        break;
      case "\\removeuserfromgroup":
        removeUserFromGroup(msgSplit);
        break;
      case "\\addmodtogroup":
        addModToGroup(msgSplit);
        break;
      case "\\deletegroup":
        deleteGroup(msgSplit);
        break;
      case "\\leavegroup":
        leaveGroup(msgSplit);
        break;
      case "\\gov":
        logger.info("Government command.");
        executeGovCommand(msgSplit);
        break;
      case "\\search":
        searchUser(message, msgSplit);
        break;
      default:
        break;
    }
  }

  private void deleteUser(IMessage message, String[] msgSplit) {
    logger.info("Trying to delete user.");
    if (msgSplit.length == 2 && accountService.findUserByName(msgSplit[1]).isPresent()) {
      accountService.deleteUser(msgSplit[1]);
      message.setContent("Delete: User " + msgSplit[1] + " has been deleted!");
      broadcast(message);
    }
  }

  private void updateUser(IMessage message, String[] msgSplit) {
    logger.info("Trying to update user.");
    if (msgSplit.length == 3 && accountService.findUserByName(msgSplit[1]).isPresent()) {
      accountService.updateUser(new User(msgSplit[1], msgSplit[2]));
      message.setContent("Update: User " + msgSplit[1] + " has been updated!");
      broadcast(message);
    }
  }

  private void createUser(IMessage message, String[] msgSplit) {
    logger.info("Trying to create user.");
    if (msgSplit.length == 4) {
      accountService.addUser(new User(msgSplit[1], msgSplit[2], msgSplit[3]));
    } else if (msgSplit.length == 3) {
      accountService.addUser(new User(msgSplit[1], msgSplit[2]));
    } else if (msgSplit.length == 2) {
      accountService.addUser(new User(msgSplit[1]));
    }else{
      logger.info("Trying to create user failed. Invalid command length.");
      return;
    }
    message.setContent("Create: User " + msgSplit[1] + " has been created!");
    broadcast(message);
  }

  private void createGroup(String[] msgSplit) {
    logger.info("Trying to create group.");
    if (msgSplit.length == 2) {
      String groupName = msgSplit[1];
      User creator = accountService.findUserByName(users.get(this.session.getId())).orElse(null);
      ChatGroup group = new ChatGroup(msgSplit[1]);
      group.addModerator(creator);
      groupPersistentService.createGroup(group);
      notificationMessage(this.session, "Group " + groupName + " has been created");
    }
  }

  private void helpInfo(IMessage message, String[] msgSplit) {
    logger.info("Getting help commands.");
    if (msgSplit.length == 1) {
      String res = "\n";
      res = res + "Create a new user : " +
              "\\createuser <username>(required) <password>(optional) <email>(email)\n"
              + "Update a user's password : " +
              "\\updateuser <username>(required) <password>(required)\n"
              + "Delete existing user : " +
              "\\deleteuser <username>(required)\n"
              + "Follow a user : " +
              "\\followuser <username>(required)\n"
              + "Unfollow a user : " +
              "\\unfollowuser <username>(required)\n"
              + "Check all users being followed : " +
              "\\following\n"
              + "View details of a user (must be following said user to be able to view) : " +
              "\\user <username>(required)\n";
      message.setContent(res);
      broadcast(message);
    }
  }

  private void getUserInfo(IMessage message, String[] msgSplit) {
    logger.info("Trying to get user details.");
    if (msgSplit.length == 2) {
      String res = "\n";
      User u = new User("Temp");
      if (accountService.isFollowing(users.get(session.getId()), msgSplit[1])) {
        Optional<User> optionalUser = accountService.findUserByName(msgSplit[1]);
        if (optionalUser.isPresent()) {
          u = optionalUser.get();
        }
        res = res + "Username : " + u.getName() + "\n";
        res = res + "E-mail : " + u.getEmail() + "\n";
      } else {
        logger.info("User not in follow-list or doesn't exist.");
        res = res + "User not in follow-list or doesn't exist.";
      }
      message.setContent(res);
      broadcast(message);
    }
  }

  private void unfollowUser(IMessage message, String[] msgSplit) {
    logger.info("Trying to unfollow user.");
    if (msgSplit.length == 2 && accountService.findUserByName(msgSplit[1]).isPresent()) {
      accountService.unfollowUser(message.getFrom(), msgSplit[1]);
      message.setContent("Unfollowing " + msgSplit[1] + "!");
      broadcast(message);
    } else {
      logger.error("Error occurred! Cannot follow. Please check unfollow-user syntax.");
      message.setContent("Error occurred! Can't unfollow.");
      broadcast(message);
    }
  }

  private void following(IMessage message, String[] msgSplit) {
    logger.info("Trying to get list of following users.");
    if (msgSplit.length == 1) {
      StringBuilder res = new StringBuilder();
      res.append("Following : \n");
      List<Follow> following = accountService.getFollowing(users.get(session.getId()));
      for (Follow f : following) {
        res.append(f.getFollowed() + "\n");
      }
      message.setContent(res.toString());
      broadcast(message);
    }
  }

  private void followUser(IMessage message, String[] msgSplit) {
    logger.info("Trying to follow user.");
    if (msgSplit.length == 2) {
      if (accountService.findUserByName(msgSplit[1]).isPresent()) {
        accountService.followUser(message.getFrom(), msgSplit[1]);
        message.setContent("Now following " + msgSplit[1] + "!");
        broadcast(message);
      }
    } else {
      logger.error("Error occurred! Cannot follow. Please check follow-user syntax.");
      message.setContent("Error occurred! Cannot follow.");
      broadcast(message);
    }
  }

  private void searchUser(IMessage message, String[] msgSplit) {
    logger.info("Search command.");
    if (msgSplit.length == 2) {
      StringBuilder res = new StringBuilder("\n");
      List<User> userList = accountService.findUsersByName(msgSplit[1]);
      for(User u : userList) {
        res.append(u.getName() + "\n");
      }
      if(userList.isEmpty()) {
        res.append("No users found");
      }
      message.setContent(res.toString());
      broadcast(message);
    }
  }

  private void leaveGroup(String[] msgSplit) {
    logger.info("Trying to leave group.");
    if (msgSplit.length == 2) {
      String groupName = msgSplit[1];
      ChatGroup group = groupPersistentService.findGroupByName(groupName);
      String initiatorName = users.get(this.session.getId());
      if (group == null || !isInGroup(group, initiatorName)) {
        logger.info("Not in group.");
        notificationMessage(this.session, "you're not in group " + groupName);
      } else {
        logger.info("User has left the group");
        groupPersistentService.deleteGroupUser(group, initiatorName);
        notificationMessage(this.session, initiatorName +
                " has left the " + groupName);
      }
    }
  }

  private void deleteGroup(String[] msgSplit) {
    logger.info("Trying to delete group.");
    if (msgSplit.length == 2) {
      try {
        long groupId = Long.parseLong(msgSplit[1]);
        ChatGroup group = groupPersistentService.findGroup(groupId);

        if (isModeratorInGroup(group, users.get(this.session.getId()))) {
          groupPersistentService.deleteGroup(groupId);
          logger.info("group has been deleted");
          notificationMessage(this.session, "Group ID:" + groupId +
                  " has been deleted");
        } else {
          logger.info("only moderator can delete group");
          notificationMessage(this.session, "only moderator can delete group");
        }
      } catch (NumberFormatException e) {
        logger.info("Group ID can only be numbers");
        notificationMessage(this.session, "Group ID can only be numbers");
      }
    }
  }

  private void addModToGroup(String[] msgSplit) {
    logger.info("Trying to add moderator to group.");
    if (msgSplit.length == 3) {
      String groupName = msgSplit[1];
      ChatGroup group = groupPersistentService.findGroupByName(groupName);
      String initiatorName = users.get(this.session.getId());
      String targetName = msgSplit[2];
      User target = accountService.findUserByName(targetName).orElse(null);
      if (target == null) {
        logger.info("addmodtogroup: target user doesn't exist");
        notificationMessage(this.session, "addmodtogroup: target user doesn't exist");
      } else if (!isModeratorInGroup(group, initiatorName)) {
        logger.info("only moderator can add moderator to group");
        notificationMessage(this.session, "only moderator can add moderator to group");
      } else {
        logger.info("moderator added.");
        groupPersistentService.addGroupModerator(group, target);
        notificationMessage(this.session, targetName +
                " has been added as moderator in " + groupName);
      }
    }
  }

  private void addUserToGroup(String[] msgSplit) {
    logger.info("Trying to add user to group.");
    if (msgSplit.length == 3) {
      String groupName = msgSplit[1];
      ChatGroup group = groupPersistentService.findGroupByName(groupName);
      String initiatorName = users.get(this.session.getId());
      String targetName = msgSplit[2];
      User target = accountService.findUserByName(targetName).orElse(null);

      if (target == null) {
        logger.info("addusertogroup: target user doesn't exist.");
        notificationMessage(this.session, "addusertogroup: target user doesn't exist");
      } else if (!isModeratorInGroup(group, initiatorName)) {
        logger.info("only moderator can add user to group.");
        notificationMessage(this.session, "only moderator can add user to group");
      } else {
        logger.info("Added user to group.");
        groupPersistentService.addGroupUser(group, target);
        notificationMessage(this.session, targetName +
                " has been added to group " + groupName);
      }
    }
  }

  private void removeUserFromGroup(String[] msgSplit) {
    logger.info("Trying to remove user from group.");
    if (msgSplit.length == 3) {
      String groupName = msgSplit[1];
      ChatGroup group = groupPersistentService.findGroupByName(groupName);
      String initiatorName = users.get(this.session.getId());
      String targetName = msgSplit[2];
      User target = accountService.findUserByName(targetName).orElse(null);
      if (target == null) {
        logger.info("removeuserfromgroup: target user doesn't exist");
        notificationMessage(this.session, "removeuserfromgroup: target user doesn't exist");
      } else if (!isModeratorInGroup(group, initiatorName)) {
        logger.info("only moderator can remove user from group");
        notificationMessage(this.session, "only moderator can remove user from group");
      } else {
        logger.info("user has been removed from group.");
        groupPersistentService.deleteGroupUser(group, target);
        notificationMessage(this.session, targetName +
                " has been removed from group " + groupName);
      }
    }
  }

  private void inviteUserToGroup(String[] msgSplit) {
    if (msgSplit.length == 3) {
      String groupName = msgSplit[1];
      ChatGroup group = groupPersistentService.findGroupByName(groupName);
      String initiatorName = users.get(this.session.getId());
      String targetName = msgSplit[2];
      User target = accountService.findUserByName(targetName).orElse(null);
      if (getInviteMessageByReceiver(targetName, groupName) != null){
        notificationMessage(this.session, "Invite already sent.");
      } else if (target == null) {
        notificationMessage(this.session, "inviteuser: target user doesn't exist");
      } else if (!isModeratorInGroup(group, initiatorName)) {
        notificationMessage(this.session, "only moderator can add user to group");
      } else {
        InviteMessage invite = InviteMessage.messageBuilder()
                .setGroupName(groupName)
                .setFrom(initiatorName)
                .setTo(targetName)
                .setMessageContent("invite message")
                .build();
        sendSingleUserMessage(invite, targetName);
        notificationMessage(this.session, "Invite sent.");
      }
    }
  }



  private void executeGovCommand(String[] msgSplit) {
    String currentUser = users.get(this.session.getId());
    logger.info(String.format("Entering executeGovCommand method. User : %s", currentUser));
    if (accountService.hasPrivilege(currentUser)) {
      governmentFunction(msgSplit);
    } else {
      logger.info("No such command.");
      notificationMessage(this.session, "No such command.");
    }
  }

  private boolean isModeratorInGroup(ChatGroup group, String username) {
    logger.info(String.format("Check if user %s is moderator in group", username));
    for (GroupUser mod : group.getModerators()) {
      if (username.equals(mod.getUser().getName())) {
        return true;
      }
    }
    return false;
  }

  private void handleSingleUserMessage(IMessage message, String msgContent, String separator, Session session) {

    logger.info(String.format("Entering handleSingleUserMessage method for content : %s", msgContent));
    String receiver = msgContent.split(separator)[0];
    String msg = msgContent.split(separator)[1];
    message.setContent(msg);

    if (!isUserNameExist(receiver)) {
      logger.warn(String.format("User %s is not valid user name, message won't be sent."
              , receiver));
      notificationMessage(session,
              String.format("User %s is not valid user name, message won't be sent."
                      , receiver));
      return;
    }
    if (sendSingleUserMessage(message, receiver)) return;

    logger.info("Message sent.");
    notificationMessage(session, "message sent.");
  }

  private boolean sendSingleUserMessage(IMessage message, String receiver) {
    message.setTimestamp(new Date());
    logger.info(String.format("Entering sendSingleUserMessage method for receiver : %s", receiver));
    // Send message when receiver is online.
    for (Map.Entry<String, String> entry : users.entrySet()) {
      if (entry.getValue().equals(receiver)) {
        sendToSingleUser(message, entry.getKey());
        return true;
      }
    }

    logger.info("Now trying to persist message when receiver is offline.");
    // Persist message when receiver is offline.
    message.setTo(receiver);
    persistMessage(message);
    return false;
  }

  /**
   * Method will set message content and group name, then pass to
   *
   * @param message           original message with separator
   * @param groupMsgSeparator separator used in group message
   */
  private void handleGroupMessage(IMessage message, String msgContent, String groupMsgSeparator,
                                  Session session) {
    logger.info(String.format("Entering handleGroupMessage method for message : %s", msgContent));
    String[] msgSplit = msgContent.split(groupMsgSeparator);
    if (msgSplit.length == 2) {
      String groupName = msgSplit[0];
      String msg = msgSplit[1];
      String userName = users.get(session.getId());
      ChatGroup group = groupPersistentService.findGroupByName(groupName);
      GroupMessage groupMessage = GroupMessage.groupMessageBuilder().buildFromMessage(message).build();
      if (isInGroup(group, userName)) {
        logger.info(String.format("Group name : %s", groupName));
        logger.info(String.format("Message from : %s", message.getFrom()));
        logger.info(String.format("Message content : %s", msg));
        groupMessage.setFrom(String.format("%s from Group %s", message.getFrom(), groupName));
        groupMessage.setContent(msg);
        groupMessage.setGroupName(groupName);
        groupMessage.setTimestamp(new Date());
        sendGroupMessage(group, groupMessage);
      } else {
        logger.info(String.format("User %s is not part of group %s", userName, groupName));
        notificationMessage(session, String.format("User %s is not part of group %s", userName, groupName));
      }

    }
  }


  private boolean isInGroup(ChatGroup group, String userName) {
    logger.info(String.format("Check if user %s is in group.", userName));
    return group.getGroupUsers()
            .stream()
            .anyMatch(groupUser -> groupUser.getUser().getName().equals(userName));
  }

  private boolean isUserNameExist(String userName) {
    logger.info(String.format("Check if user %s exists.", userName));
    Optional<User> user = accountService.findUserByName(userName);
    return user.isPresent();
  }


  private boolean isCommand(String msg) {
    return msg.charAt(0) == '\\';
  }

  /**
   * On close.
   *
   * Closes the session by removing it from the pool of sessions and broadcasting the news to
   * everyone else.
   *
   * @param session the session
   */
  @OnClose
  public void onClose(Session session) {
    logger.info("Entering onClose method.");
    chatEndpoints.remove(this);
    Message message = new Message();
    message.setFrom(users.get(session.getId()));
    message.setContent("Disconnected!");
    message.setTimestamp(new Date());
    logger.info("Disconnecting session.");
    broadcast(message);
  }

  /**
   * On error.
   *
   * Handles situations when an error occurs.  Not implemented.
   *
   * @param session   the session with the problem
   * @param throwable the action to be taken.
   */
  @OnError
  public void onError(Session session, Throwable throwable) {
    // Do error handling here
  }

  /**
   * Broadcast.
   *
   * Send a Message to each session in the pool of sessions. The Message sending action is
   * synchronized.  That is, if another Message tries to be sent at the same time to the same
   * endpoint, it is blocked until this Message finishes being sent..
   *
   * @param message message
   */
  private static void broadcast(IMessage message) {
    boolean hasSender = message.getFrom() != null && !message.getFrom().equals("");
    message.setTimestamp(new Date());

    chatEndpoints.forEach(endpoint -> {
      synchronized (endpoint) {
        try {
          if (hasSender) {
            String userName = users.get(endpoint.session.getId());
            message.setTo(userName);
            message.setReceived(true);
            persistMessage(message);
          }
          endpoint.session.getBasicRemote()
                  .sendObject(message);
        } catch (IOException | EncodeException e) {
          /* note: in production, who exactly is looking at the console.  This exception's
           *       output should be moved to a logger.
           */
          logger.error(e.getMessage());
        }
      }
    });
  }

  private static void persistMessage(IMessage message) {
    logger.info("Trying to persist message");
    messagePersistentService.addMessage(message.copy());
  }

  /**
   * Group Message.
   *
   * @param message message
   */
  private static void sendGroupMessage(ChatGroup group, GroupMessage message) {

    logger.info("Entering sendGroupMessage method.");

    List<GroupUser> groupUsers = group.getGroupUsers();
    Set<String> groupUserNames = groupUsers.stream()
            .map(groupUser -> groupUser.getUser().getName())
            .collect(Collectors.toSet());


    for (ChatEndpoint endpoint : chatEndpoints) {
      String userName = users.get(endpoint.session.getId());
      if (groupUserNames.contains(userName)) {
        groupUserNames.remove(userName);
        synchronized (endpoint) {
          try {
            endpoint.session.getBasicRemote()
                    .sendObject(message);
            message.setReceived(true);
            message.setTo(userName);
            persistMessage(message);
          } catch (IOException | EncodeException e) {
            logger.error(e.getMessage());
          }
        }
      }
    }
    for (String name : groupUserNames) {
      message.setTo(name);
      message.setReceived(false);
      persistMessage(message);
    }
  }

  /**
   * Send to single user.
   *
   * Sending a message to a single user.
   *
   * @param message message to be sent
   * @param key     key to define a user
   */
  private static void sendToSingleUser(IMessage message, String key) {
    logger.info(String.format("Trying to execute point to point message for key : %s", key));
    chatEndpoints.forEach(endpoint -> {
      synchronized (endpoint) {
        try {
          if (endpoint.session.getId().equals(key)) {
            endpoint.session.getBasicRemote()
                    .sendObject(message);
            // Persist message for record.
            message.setReceived(true);
            message.setTo(users.get(key));
            persistMessage(message);
          }
        } catch (IOException | EncodeException e) {
          /* note: in production, who exactly is looking at the console.  This exception's
           *       output should be moved to a logger.
           */
          logger.error(e.getMessage());
        }
      }
    });
  }

  private void governmentFunction(String[] msgSplit) {
    logger.info(String.format("Entering governmentFunction method for command : %s", msgSplit[1]));
    switch (msgSplit[1]) {
      case "history":
        govHistory(msgSplit);
        break;
      case "watch":
        govWatch(msgSplit);
        break;
      case "unwatch":
        govUnwatch(msgSplit);
        break;
      case "ls":
        govLs(msgSplit);
        break;
      default:
        logger.warn("Invalid command for government function.");
        notificationMessage(this.session, "Invalid command");
    }

  }

  private void govWatch(String[] msgSplit) {
    if (msgSplit.length == 3) {
      logger.info(String.format("Trying to watch user activities for user : %s", msgSplit[2]));
      String username = msgSplit[2];

      if (accountService.findUserByName(username).isPresent()) {
        watchList.add(username);
        notificationMessage(this.session, "Start to watch user activities: " + "\"" + username + "\"");
      } else {
        logger.info("User doesn't exist.");
        notificationMessage(this.session, "User doesn't exist: " + "\"" + username + "\"");
      }
    } else {
      logger.info("Trying to watch user activities for user : user missing.");
      notificationMessage(this.session, "\\gov watch <username>");
    }
  }

  private void govHistory(String[] msgSplit) {
    if (msgSplit.length == 3) {
      String username = msgSplit[2];
      logger.info(String.format("Finding history for user %s" , username));
      notificationMessage(this.session, "Find history of user " + username);
      if (accountService.findUserByName(username).isPresent()) {
        List<IMessage> messages = messagePersistentService.findMessageBySender(username);
        StringBuilder sb = new StringBuilder();
        for (IMessage msg : messages) {
          sb.append(msg.toString()).append("\n");
        }
        notificationMessage(this.session, sb.toString());

      } else {
        logger.info("User doesn't exist");
        notificationMessage(this.session, "User doesn't exist: " + "\"" + username + "\"");
      }
    } else {
      notificationMessage(this.session, "\\gov history <username>");
    }
  }

  private void govUnwatch(String[] msgSplit) {
    if (msgSplit.length == 3) {
      logger.info(String.format("Unwatch command for user : %s.", msgSplit[2]));
      String username = msgSplit[2];

      if (watchList.contains(username)) {
        watchList.remove(username);
        logger.info("Stop watching user activities.");
        notificationMessage(this.session, "Stop to watch user activities: " + "\"" + username + "\"");
      } else {
        logger.info("User not being watched.");
        notificationMessage(this.session, "User is not being watched." + "\"" + username + "\"");
      }
    } else {
      logger.info("Unwatch command for user : user missing.");
      notificationMessage(this.session, "\\gov unwatch <username>");
    }
  }

  private void govLs(String[] msgSplit) {
    logger.info("Command to list of users of interest.");
    if (msgSplit.length == 2) {

      StringBuilder sb = new StringBuilder();
      sb.append("List of users of interest:\n");

      for (String user : watchList) {
        sb.append(user).append("\n");
      }
      notificationMessage(this.session, sb.toString());
    } else {
      notificationMessage(this.session, "\\gov watch <username>");
    }
  }

  private static Set<String> watchList = new HashSet<>();
}

