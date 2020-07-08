package com.neu.prattle.model;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.Table;

/**
 * The type Group user.
 */
@Entity
@Table(name = "GROUPUSERS")
public class GroupUser {
  @EmbeddedId
  private GroupUserId id;

  @ManyToOne(cascade = CascadeType.PERSIST)
  @MapsId("groupid")
  private ChatGroup chatGroup;

  @ManyToOne(cascade = CascadeType.PERSIST)
  @MapsId("username")
  private User user;

  @Column(name = "IS_MODERATOR")
  private boolean moderator;

  /**
   * Instantiates a new Group user.
   */
  protected GroupUser() {
  }

  /**
   * Instantiates a new Group user.
   *
   * @param user      the user
   * @param chatGroup the chat group
   */
  public GroupUser(User user, ChatGroup chatGroup) {
    validate(user, chatGroup);
    this.user = user;
    this.chatGroup = chatGroup;
    this.moderator = false;
    this.id = new GroupUserId(user.getName(), chatGroup.getId());
  }

  /**
   * Instantiates a new Group user.
   *
   * @param user      the user
   * @param chatGroup the chat group
   * @param moderator the moderator
   */
  public GroupUser(User user, ChatGroup chatGroup, boolean moderator) {
    validate(user, chatGroup);
    this.user = user;
    this.chatGroup = chatGroup;
    this.moderator = moderator;
  }

  private void validate(User user, ChatGroup chatGroup) {
    if (user == null)
      throw new IllegalArgumentException("Reference user is null.");
    if (chatGroup == null)
      throw new IllegalArgumentException("Reference chatGroup is null.");

  }

  /**
   * Gets user.
   *
   * @return the user
   */
  public User getUser() {
    return user;
  }

  /**
   * Sets user.
   *
   * @param user the user
   */
  public void setUser(User user) {
    if (user == null)
      throw new IllegalArgumentException("Reference user is null.");
    this.user = user;
  }

  /**
   * Is moderator boolean.
   *
   * @return the boolean
   */
  public boolean isModerator() {
    return moderator;
  }

  /**
   * Sets moderator.
   *
   * @param moderator the moderator
   */
  public void setModerator(boolean moderator) {
    this.moderator = moderator;
  }

  /**
   * Gets chat group.
   *
   * @return the chat group
   */
  public ChatGroup getChatGroup() {
    return chatGroup;
  }

  /**
   * Sets chat group.
   *
   * @param chatGroup the chat group
   */
  public void setChatGroup(ChatGroup chatGroup) {
    if (chatGroup == null)
      throw new IllegalArgumentException("Reference chatGroup is null.");
    this.chatGroup = chatGroup;
  }
}
