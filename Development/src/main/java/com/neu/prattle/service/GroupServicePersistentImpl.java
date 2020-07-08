package com.neu.prattle.service;

import com.neu.prattle.database.DataRepository;
import com.neu.prattle.model.ChatGroup;
import com.neu.prattle.model.GroupUser;
import com.neu.prattle.model.User;


public class GroupServicePersistentImpl implements GroupService {

  private DataRepository repository;

  private GroupServicePersistentImpl() {
    this.repository = DataRepository.getDataRepository();
  }

  static GroupServicePersistentImpl groupService;

  static {
    groupService = new GroupServicePersistentImpl();
  }

  public static GroupService getInstance() {
    return groupService;
  }


  @Override
  public void createGroup(ChatGroup group) {
    validateGroup(group);
    repository.createGroup(group);
  }

  @Override
  public void deleteGroup(long groupid) {
    repository.deleteGroupById(groupid);
  }

  @Override
  public ChatGroup findGroup(long groupid) {
    return repository.findGroupById(groupid);
  }

  @Override
  public ChatGroup findGroupByName(String groupName) {
    return repository.findGroupByName(groupName);
  }

  @Override
  public void addGroupUser(ChatGroup group, User user) {
    validateGroup(group);
    validateUser(user);
    group.addUser(user);
    repository.updateGroup(group);
  }

  @Override
  public void deleteGroupUser(ChatGroup group, String username) {
    validateGroup(group);
    validateUsername(username);

    // Remove entity from the mapping table
    for (GroupUser gu : group.getGroupUsers()) {
      if (gu.getUser().getName().equals(username)) {
        repository.deleteGroupUser(gu);
      }
    }
    // Remove user from group object maintain consistency.
    group.removeUser(username);

    repository.updateGroup(group);
  }

  @Override
  public void deleteGroupUser(ChatGroup group, User user) {
    validateGroup(group);
    validateUser(user);

    // Remove entity from the mapping table
    for (GroupUser gu : group.getGroupUsers()) {
      if (gu.getUser().getName().equals(user.getName())) {
        repository.deleteGroupUser(gu);
      }
    }
    // Remove user from group object maintain consistency.
    group.removeUser(user.getName());
    repository.refresh(user);
    repository.updateGroup(group);

  }

  @Override
  public void addGroupModerator(ChatGroup group, User user) {
    validateGroup(group);
    validateUser(user);
    group.addModerator(user);
    repository.updateGroup(group);
  }

  @Override
  public void removeGroupModerator(ChatGroup group, String username) {
    validateGroup(group);
    validateUsername(username);
    group.removeModerator(username);

    ChatGroup grp = repository.findGroupByName(group.getGroupName());

    repository.updateGroupUser(username,grp.getId(),false);

    repository.updateGroup(group);
  }

  private void validateGroup(ChatGroup group) {
    if (group == null)
      throw new IllegalArgumentException("Reference group is null.");
  }

  private void validateUser(User user) {
    if (user == null)
      throw new IllegalArgumentException("Reference user is null.");
  }

  private void validateUsername(String username) {
    if (username == null)
      throw new IllegalArgumentException("Reference username is null.");
  }
}
