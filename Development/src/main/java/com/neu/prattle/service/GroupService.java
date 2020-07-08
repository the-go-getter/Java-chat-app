package com.neu.prattle.service;

import com.neu.prattle.model.ChatGroup;
import com.neu.prattle.model.User;


/**
 * The interface Group service.
 */
public interface GroupService {
  /**
   * Create group. The user who send the create group command will be the original moderator.
   *
   * @param group the group
   */
  void createGroup(ChatGroup group);

  /**
   * Delete group.
   *
   * @param groupid the groupid
   */
  void deleteGroup(long groupid);

  /**
   * Find group chat group.
   *
   * @param groupid the groupid
   * @return the chat group
   */
  ChatGroup findGroup(long groupid);

  /**
   * Find group by name.
   */
  ChatGroup findGroupByName(String groupName);

  /**
   * Add group user.
   *
   * @param group the group
   * @param user  the user
   */
  void addGroupUser(ChatGroup group, User user);

  /**
   * Delete group user.
   *
   * @param group    the group
   * @param username the user name
   */
  void deleteGroupUser(ChatGroup group, String username);

  /**
   * Delete group user.
   *
   * @param group the group
   * @param user  the user
   */
  void deleteGroupUser(ChatGroup group, User user);

  /**
   * Set group moderator. The moderator must be a user in the same group.
   *
   * @param group the group
   * @param user  the user
   */
  void addGroupModerator(ChatGroup group, User user);

  /**
   * Remove group moderator. The moderator will be a user in the same grouper.
   *
   * @param group    the group
   * @param username the moderator name
   */
  void removeGroupModerator(ChatGroup group, String username);

}
