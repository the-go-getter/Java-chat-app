package com.neu.prattle.controller;

import com.neu.prattle.model.User;

import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;

public class UserControllerTest {
  private UserController uc;
  private User user;
  private User user1;
  private User user2;

  @Before
  public void setup() {
    user = new User("uc", "uc");
    user1 = new User("uc1", "uc1");
    user2 = new User("uc1", "uc1");
    uc = new UserController();
  }

  @Test
  public void testCreateUser() {
    Response r = uc.createUserAccount(user);
    assertEquals(200, r.getStatus());
  }

  @Test
  public void testUserAlreadyPresent() {
    uc.createUserAccount(user1);
    Response r = uc.createUserAccount(user2);
    assertEquals(409, r.getStatus());
  }

}