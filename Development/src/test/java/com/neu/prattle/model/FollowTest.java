package com.neu.prattle.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class FollowTest {
  Long id = 1234567890L;
  String follower;
  String followed;
  Follow testFollow = new Follow(follower, followed);

  @Test
  public void setFollowerTest() {
    testFollow.setFollower("follower");
    assertEquals("follower", testFollow.getFollower());
  }

  @Test
  public void setFollowedTest() {
    testFollow.setFollowed("followed");
    assertEquals("followed", testFollow.getFollowed());
  }

  @Test
  public void setIdTest(){
    testFollow.setId(9876543210L);
    assertEquals(9876543210L, testFollow.getId());
  }
}
