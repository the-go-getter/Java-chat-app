package com.neu.prattle.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * The type Follow user.
 */
@Entity
@Table(name = "FOLLOW")
public class Follow {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(nullable = false)
  private long id;

  @Column(name = "follower")
  private String follower;

  @Column(name = "followed")
  private String followed;


  public Follow(String follower, String followed) {
    this.follower = follower;
    this.followed = followed;
  }

  protected Follow() {}

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getFollower() {
    return follower;
  }

  public void setFollower(String follower) {
    this.follower = follower;
  }

  public String getFollowed() {
    return followed;
  }

  public void setFollowed(String followed) {
    this.followed = followed;
  }

}
