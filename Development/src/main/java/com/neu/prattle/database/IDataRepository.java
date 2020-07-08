package com.neu.prattle.database;

import com.neu.prattle.model.ChatGroup;
import com.neu.prattle.model.Follow;
import com.neu.prattle.model.GroupUser;
import com.neu.prattle.model.IMessage;
import com.neu.prattle.model.InviteMessage;
import com.neu.prattle.model.User;

import java.util.List;


/**
 * The interface Data repository.
 */
public interface IDataRepository {

  /**
   * Find user by username user.
   *
   * @param username the username
   * @return the user
   */
  User findUserByUsername(String username);

  /**
   * Find password for the user.
   * @param username the username for this user
   * @return the password for this user
   */
  String findPasswordbyUsername(String username);

  /**
   * Create user.
   *
   * @param user the user
   */
  void createUser(User user);

  /**
   * Update user.
   *
   * @param user the user
   */
  void updateUser(User user);

  /**
   * Delete user by username.
   *
   * @param username the username
   */
  void deleteUserByUsername(String username);

  /**
   * Create message.
   *
   * @param message the message
   */
  void createMessage(IMessage message);


  /**
   * Update message received flag. Only update message received status.
   *
   * @param message the message
   */
  void updateMessageReceivedFlag(IMessage message);

  /**
   * Find message by id message.
   *
   * @param id the id
   * @return the message
   */
  IMessage findMessageById(long id);

  /**
   * Find message by sender username.
   *
   * @param sender the username
   * @return the list
   */
  List<IMessage> findMessageBySenderUsername(String sender);

  /**
   * Find message by receiver username.
   *
   * @param receiver the receiver
   * @return the list
   */
  List<IMessage> findMessageByReceiverUsername(String receiver);

  /**
   * Find message by sender and receiver.
   *
   * @param sender   the sender
   * @param receiver the receiver
   * @return the list
   */
  List<IMessage> findMessageBySenderAndReceiver(String sender, String receiver);

  /**
   * Create group.
   *
   * @param chatGroup the chat group
   */
  void createGroup(ChatGroup chatGroup);

  /**
   * Find group by id chat group.
   *
   * @param id the id
   * @return the chat group
   */
  ChatGroup findGroupById(long id);

  /**
   * Find group by name chat group.
   *
   * @param groupName the group name
   * @return the chat group
   */
  ChatGroup findGroupByName(String groupName);

  /**
   * Update group.
   *
   * @param chatGroup the chat group
   */
  void updateGroup(ChatGroup chatGroup);

  /**
   * Delete group by id.
   *
   * @param id the id
   */
  void deleteGroupById(long id);

  /**
   * Create group user.
   *
   * @param groupUser the group user
   */
  void createGroupUser(GroupUser groupUser);

  /**
   * Delete group-user pair.
   *
   * @param groupUser the group-user pair
   */
  void deleteGroupUser(GroupUser groupUser);


  /**
   * Update group user.
   *
   * @param groupUser the group user
   */
  void updateGroupUser(GroupUser groupUser);


  /**
   * Update group user.
   *
   * @param username    the username
   * @param groupid     the groupid
   * @param isModerator the is moderator
   */
  void updateGroupUser(String username, long groupid, boolean isModerator);

  /**
   * Follow user
   *
   * @param follow the follow object to be created
   */
  void followUser(Follow follow);

  /**
   * Unfollow user.
   *
   * @param id id of Follow object to be removed.
   */
  void unfollowUser(long id);

  /**
   * Find following list.
   *
   * @param u the u
   * @return the list
   */
  List<Follow> findFollowing(String u);

  /**
   * Is following boolean.
   *
   * @param s  the s
   * @param s1 the s 1
   * @return the boolean
   */
  boolean isFollowing(String s, String s1);

  /**
   * Gets follow by id.
   *
   * @param from           the from
   * @param toBeUnfollowed the to be unfollowed
   * @return the follow by id
   */
  List<Follow> getFollowById(String from, String toBeUnfollowed);

  /**
   * Add privilege user.
   *
   * @param username the username
   */
  void addPrivilegeUser(String username);

  /**
   * Has privilege boolean.
   *
   * @param username the username
   * @return the boolean
   */
  boolean hasPrivilege(String username);

  /**
   * Delete privilege user.
   *
   * @param username the username
   */
  void deletePrivilegeUser(String username);

  /**
   * Find invitations by receiver list.
   *
   * @param receiver the receiver
   * @return the list
   */
  List<IMessage> findInvitationsByReceiver(String receiver);

  /**
   * Find invitations by sender list.
   *
   * @param sender the sender
   * @return the list
   */
  List<IMessage> findInvitationsBySender(String sender);

  /**
   * Find unacknowledged invitations by receiver list.
   *
   * @param receiver the receiver
   * @return the list
   */
  List<IMessage> findUnacknowledgedInvitationsByReceiver(String receiver);

  /**
   * Update invitation message. Note, this messege must be an managed object. This means it must be
   * obtained by find* methods.
   *
   * @param message the message
   */
  void updateInvitationMessageStatus(InviteMessage message);

  List<User> findUsersByName(String name);
}
