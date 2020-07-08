package com.neu.prattle.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

/**
 * The type Chat group.
 */
@Entity
@Table(name = "CHATGROUPS")
public class ChatGroup {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(nullable = false)
  private long id;

  private String groupName;

  @OneToMany(mappedBy = "chatGroup", cascade = CascadeType.ALL)
  @OrderBy("moderator DESC, id.username ASC")
  private List<GroupUser> groupUsers = new ArrayList<>();


  /**
   * Instantiates a new Chat group.
   */
  protected ChatGroup() {
  }

  /**
   * Instantiates a new Chat group.
   *
   * @param groupName the group name
   */
  public ChatGroup(String groupName) {

    if (groupName == null)
      throw new IllegalArgumentException("Reference groupName is null.");
    this.groupName = groupName;
  }

  /**
   * Gets id.
   *
   * @return the id
   */
  public long getId() {
    return id;
  }

  /**
   * Sets id.
   *
   * @param id the id
   */
  public void setId(long id) {
    this.id = id;
  }

  /**
   * Gets group name.
   *
   * @return the group name
   */
  public String getGroupName() {
    return groupName;
  }

  /**
   * Sets group name.
   *
   * @param groupName the group name
   */
  public void setGroupName(String groupName) {
    if (groupName == null)
      throw new IllegalArgumentException("Reference groupName is null.");
    this.groupName = groupName;
  }

  /**
   * Gets group users.
   *
   * @return the group users
   */
  public List<GroupUser> getGroupUsers() {
    return groupUsers;
  }

  /**
   * Sets group users.
   *
   * @param groupUsers the group users
   */
  public void setGroupUsers(List<GroupUser> groupUsers) {
    if (groupUsers == null)
      throw new IllegalArgumentException("Reference groupUsers is null.");
    this.groupUsers = groupUsers;
  }

  /**
   * Add user.
   *
   * @param user the user
   */
  public void addUser(User user) {
    if (user == null)
      throw new IllegalArgumentException("Reference user is null.");
    GroupUser gu = new GroupUser(user, this);
    /*
      If we uncomment the following line, it will create duplicate key problem in testAddAndDeleteUser.
      "user.getGroups().add(gu);"
     */
    this.groupUsers.add(gu);

  }

  /**
   * Add moderator.
   *
   * @param user the user
   */
  public void addModerator(User user) {
    if (user == null)
      throw new IllegalArgumentException("Reference user is null.");
    GroupUser gu = new GroupUser(user, this, true);
    /*
      If we uncomment the following line, it will create duplicate key problem in testAddAndDeleteUser.
      "user.getGroups().add(gu);"
     */

    this.groupUsers.add(gu);

  }

  /**
   * Remove moderator.
   *
   * @param username the username
   */
  public void removeModerator(String username) {
    validateUsername(username);

    for (int i = 0; i < groupUsers.size(); i++) {
      User user = groupUsers.get(i).getUser();
      if (user.getName().equals(username) && groupUsers.get(i).isModerator()) {

        for (int j = 0; j < user.getGroups().size(); j++) {
          if (user.getGroups().get(j).getChatGroup().getId() == this.id) {
            user.getGroups().get(j).setModerator(false);
          }
        }
        break;
      }
    }
  }

  private void validateUsername(String username) {
    if (username == null)
      throw new IllegalArgumentException("Reference username is null.");
  }

  public void removeUser(String username) {
    validateUsername(username);

    for (int i = 0; i < groupUsers.size(); i++) {
      User user = groupUsers.get(i).getUser();
      if (user.getName().equals(username) && !groupUsers.get(i).isModerator()) {
        groupUsers.remove(i);
        break;
      }
    }

  }

  /**
   * Get moderators.
   *
   * @return the moderators
   */
  public Collection<GroupUser> getModerators() {
    List<GroupUser> moderators = new ArrayList<>();
    for (GroupUser u : groupUsers) {
      if (u.isModerator()) {
        moderators.add(u);
      }
    }
    return moderators;
  }

}
