package com.neu.prattle.service;

import com.neu.prattle.database.DataRepository;
import com.neu.prattle.exceptions.UserAlreadyPresentException;
import com.neu.prattle.exceptions.UserNotPresentException;
import com.neu.prattle.model.Follow;
import com.neu.prattle.model.User;

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.ws.rs.NotFoundException;

public class UserServicePersistentImpl implements UserService {
  private static final String NOT_EXIST_WITH_NAME_S = "User does not exist with name: %s";
  private DataRepository repository;
  /**
   * The logger.
   */
  private static final Logger logger = Logger.getLogger(UserServicePersistentImpl.class);



  private UserServicePersistentImpl() {
    repository = DataRepository.getDataRepository();
    User gov = repository.findUserByUsername("gov");
    if (gov != null) {
      repository.deleteUserByUsername("gov");
    } else {
      User user = new User("gov", "gov", "operator@gov.gov");
      repository.createUser(user);
      repository.addPrivilegeUser(user.getName());
    }
  }

  private static UserService accountService;

  static {
    accountService = new UserServicePersistentImpl();
  }

  /**
   * Call this method to return an instance of this service.
   *
   * @return this
   */
  public static UserService getInstance() {
    return accountService;
  }


  /***
   * Returns an optional object which might be empty or wraps an object
   * if the System contains a {@link User} object having the same name
   * as the parameter.
   *
   * @param name The name of the user
   * @return Optional object.
   */
  @Override
  public Optional<User> findUserByName(String name) {
    if (name == null)
      throw new IllegalArgumentException("findUserByName: Reference name is null.");
    User user = repository.findUserByUsername(name);
    if (user != null)
      return Optional.of(user);
    else
      return Optional.empty();
  }

  public boolean authenticateUser(String username, String password) {
    if (username == null)
      throw new IllegalArgumentException("authenticateUser: Reference name is null.");
    return repository.findPasswordbyUsername(username) == null ||
            password.equals(repository.findPasswordbyUsername(username));
  }

  /***
   * Tries to add a user in the system
   * @param user User object
   */
  @Override
  public void addUser(User user) {
    if (user == null)
      throw new IllegalArgumentException("addUser: Reference user is null.");
    User oldUser = repository.findUserByUsername(user.getName());

    if (oldUser != null)
      throw new UserAlreadyPresentException("User already present: " + user.getName());

    repository.createUser(user);

  }

  private void validateUser(boolean b, String s, String name) {
    if (b)
      throw new UserNotPresentException(String.format(s, name));
  }

  /***
   * Tries to update a user in the system
   * @param user User object
   *
   */
  @Override
  public void updateUser(User user) {
    User oldUser = repository.findUserByUsername(user.getName());
    validateUser(oldUser == null, NOT_EXIST_WITH_NAME_S, user.getName());

    repository.updateUser(user);

  }

  /***
   * Tries to delete a user in the system
   * @param username Username of user to be deleted
   *
   */
  @Override
  public void deleteUser(String username) {
    User oldUser = repository.findUserByUsername(username);
    validateUser(oldUser == null, NOT_EXIST_WITH_NAME_S, username);

    repository.deleteUserByUsername(username);
  }

  /***
   * Tries to follow a user in the system
   * @param follower Username of user that is following another user
   * @param followed Username of user to be followed
   *
   */
  @Override
  public void followUser(String follower, String followed) {
    User user = repository.findUserByUsername(followed);
    User userFollower = repository.findUserByUsername(follower);
    validateUser(user == null, NOT_EXIST_WITH_NAME_S, follower);

    validateUser(userFollower == null, NOT_EXIST_WITH_NAME_S, followed);

    Follow follow = new Follow(follower, followed);
    repository.followUser(follow);
  }

  @Override
  public List<Follow> getFollowing(String user) {
    return repository.findFollowing(user);
  }

  @Override
  public void unfollowUser(String from, String toBeUnfollowed) {
    User user = repository.findUserByUsername(from);
    User unfollowedUser = repository.findUserByUsername(toBeUnfollowed);

    validateUser(user == null, NOT_EXIST_WITH_NAME_S, from);

    validateUser(unfollowedUser == null, NOT_EXIST_WITH_NAME_S, toBeUnfollowed);

    List<Follow> toBeRemoved = repository.getFollowById(from, toBeUnfollowed);

    if (toBeRemoved.isEmpty()) {
      throw new NotFoundException("Entry not found in Follow table");
    }
    long idToBeRemoved = toBeRemoved.get(0).getId();
    repository.unfollowUser(idToBeRemoved);
  }

  @Override
  public boolean isFollowing(String s, String s1) {
    return repository.isFollowing(s, s1);
  }


  @Override
  public boolean hasPrivilege(String username) {
    return repository.hasPrivilege(username);
  }

  @Override
  public List<User> findUsersByName(String name) {
    if (name == null)
      throw new IllegalArgumentException("findUsersByName: Reference name is null.");
    logger.info("UserservicePersistenImpl findUsersByName");
    List<User> users = repository.findUsersByName(name);
    logger.info("UserservicePersistenImpl findUsersByName found users");
    if(users == null) {
      return new ArrayList<>();
    }
    return users;
  }
}
