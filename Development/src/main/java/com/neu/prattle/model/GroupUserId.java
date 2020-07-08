package com.neu.prattle.model;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * The type Group user id.
 */
@Embeddable
public class GroupUserId implements Serializable {
  @Column(name = "USER_ID")
  private String username;
  @Column(name = "CHATGROUP_ID")
  private long groupid;

  /**
   * Instantiates a new Group user id.
   */
  public GroupUserId() {
  }

  /**
   * Instantiates a new Group user id.
   *
   * @param username the username
   * @param groupid  the groupid
   */
  public GroupUserId(String username, long groupid) {
    if (username == null)
      throw new IllegalArgumentException("Reference username is null");
    this.username = username;
    this.groupid = groupid;
  }

  /**
   * Gets username.
   *
   * @return the username
   */
  public String getUsername() {
    return username;
  }

  /**
   * Sets username.
   *
   * @param username the username
   */
  public void setUsername(String username) {
    if (username == null)
      throw new IllegalArgumentException("Reference username is null");
    this.username = username;
  }

  /**
   * Gets groupid.
   *
   * @return the groupid
   */
  public long getGroupid() {
    return groupid;
  }

  /**
   * Sets groupid.
   *
   * @param groupid the groupid
   */
  public void setGroupid(long groupid) {
    this.groupid = groupid;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null)
      return false;
    if (getClass() != o.getClass())
      return false;
    GroupUserId that = (GroupUserId) o;
    return groupid == that.groupid &&
            Objects.equals(username, that.username);
  }

  @Override
  public int hashCode() {
    return Objects.hash(username, groupid);
  }
}
