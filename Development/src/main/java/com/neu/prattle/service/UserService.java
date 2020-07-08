package com.neu.prattle.service;

import com.neu.prattle.model.Follow;
import com.neu.prattle.model.User;

import java.util.List;
import java.util.Optional;

/***
 * Acts as an interface between the data layer and the
 * servlet controller.
 *
 * The controller is responsible for interfacing with this instance
 * to perform all the CRUD operations on user accounts.
 *
 * @author CS5500 Fall 2019 Teaching staff
 * @version dated 2019-10-06
 *
 */
public interface UserService {
  /***
   * Returns an optional object which might be empty or wraps an object
   * if the System contains a {@link User} object having the same name
   * as the parameter.
   *
   * @param name The name of the user
   * @return Optional object.
   */
  Optional<User> findUserByName(String name);

  /**
   * Checks if the username and password are right or not.
   * @param username The username for this user.
   * @param password The password for this user.
   * @return true if the username and password are right, false otherwise.
   */
  boolean authenticateUser(String username, String password);

  /***
   * Tries to add a user in the system
   * @param user User object
   *
   */
  void addUser(User user);

  /***
   * Tries to add a user in the system
   * @param user User object
   *
   */
  void updateUser(User user);

  /***
   * Tries to delete a user in the system
   * @param username Username of user to be deleted
   *
   */
  void deleteUser(String username);

  /***
   * Tries to follow a user in the system
   * @param follower Username of user that is following another user
   * @param followed Username of user to be followed
   *
   */
  void followUser(String follower, String followed);

  List<Follow> getFollowing(String user);

  void unfollowUser(String from, String toBeUnfollowed);

  boolean isFollowing(String s, String s1);

  boolean hasPrivilege(String username);

  List<User> findUsersByName(String s);
}
