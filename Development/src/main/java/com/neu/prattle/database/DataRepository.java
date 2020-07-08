package com.neu.prattle.database;

import com.neu.prattle.exceptions.GroupAlreadyPresentException;
import com.neu.prattle.model.AbstractMessage;
import com.neu.prattle.model.ChatGroup;
import com.neu.prattle.model.Follow;
import com.neu.prattle.model.GroupUser;
import com.neu.prattle.model.GroupUserId;
import com.neu.prattle.model.IMessage;
import com.neu.prattle.model.InviteMessage;
import com.neu.prattle.model.Message;
import com.neu.prattle.model.Privilege;
import com.neu.prattle.model.User;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 * The type Data repository.
 */
public class DataRepository implements IDataRepository {

  private static final String PERSISTENCE_UNIT_NAME = "slackdata";
  private EntityManager entityManager;

  private static DataRepository dataRepository = null;
  private String follower = "follower";
  private String uName = "username";

  /**
   * Instantiates a new Data repository.
   */
  private DataRepository() {
    EntityManagerFactory factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
    this.entityManager = factory.createEntityManager();
  }

  public static DataRepository getDataRepository() {
    if (dataRepository == null)
      dataRepository = new DataRepository();
    return dataRepository;
  }


  @Override
  public User findUserByUsername(String username) {
    validate(username == null, "findUserByUsername: Reference username is null.");
    User user = this.entityManager.find(User.class, username);
    if (user == null)
      return null;
    this.entityManager.refresh(user);
    return user;
  }

  @Override
  public String findPasswordbyUsername(String username) {
    if (username == null) {
      throw new IllegalArgumentException("Reference username is null.");
    }
    User user = this.entityManager.find(User.class, username);
    if (user == null)
      return null;
    this.entityManager.refresh(user);
    return user.getPassword();
  }

  @Override
  public void createUser(User user) {
    validate(user == null, "createUser: Reference user is null.");
    entityManager.getTransaction().begin();
    entityManager.persist(user);
    entityManager.getTransaction().commit();
  }

  @Override
  public void updateUser(User user) {
    entityManager.getTransaction().begin();
    User oldUser = entityManager.find(User.class, user.getName());
    oldUser.setPassword(user.getPassword());
    entityManager.getTransaction().commit();
  }

  @Override
  public void deleteUserByUsername(String username) {
    entityManager.getTransaction().begin();
    User user = entityManager.find(User.class, username);
    if (user != null)
      entityManager.remove(user);
    entityManager.getTransaction().commit();
  }

  @Override
  public void createMessage(IMessage message) {
    validate(message == null, "createMessage: Reference message is null.");
    entityManager.getTransaction().begin();
    entityManager.persist(message);
    entityManager.getTransaction().commit();

  }

  @Override
  public IMessage findMessageById(long id) {
    return this.entityManager.find(Message.class, id);
  }

  @Override
  public void updateMessageReceivedFlag(IMessage message) {
    validate(message == null, "updateMessageReceivedFlag: Reference message is null.");
    entityManager.getTransaction().begin();
    IMessage oldMsg = entityManager.find(AbstractMessage.class, message.getId());
    oldMsg.setReceived(message.isReceived());
    entityManager.getTransaction().commit();
  }

  @Override
  @SuppressWarnings("unchecked")
  public List<IMessage> findMessageBySenderUsername(String sender) {
    validate(sender == null, "findMessageBySenderUsername: Reference sender is null.");
    return entityManager.createQuery("SELECT m FROM Message m where m.from = :sender")
            .setParameter("sender", sender).getResultList();
  }

  @Override
  @SuppressWarnings("unchecked")
  public List<IMessage> findMessageByReceiverUsername(String receiver) {
    validate(receiver == null, "findMessageByReceiverUsername: Reference receiver is null.");

    return entityManager.createQuery("SELECT m FROM AbstractMessage m where m.to = :receiver")
            .setParameter("receiver", receiver).getResultList();
  }

  @Override
  @SuppressWarnings("unchecked")
  public List<IMessage> findMessageBySenderAndReceiver(String sender, String receiver) {
    validate(sender == null, "findMessageBySenderAndReceiver: Reference sender is null.");
    validate(receiver == null, "findMessageBySenderAndReceiver: Reference receiver is null.");
    return entityManager.createQuery("SELECT m FROM Message m where m.from = :sender AND m.to = :receiver")
            .setParameter("sender", sender).setParameter("receiver", receiver).getResultList();
  }


  @Override
  public void createGroup(ChatGroup chatGroup) {
    validate(chatGroup == null, "findMessageBySenderAndReceiver: Reference chatGroup is null.");

    if (this.findGroupByName(chatGroup.getGroupName()) != null)
      throw new GroupAlreadyPresentException("Group with name " + chatGroup.getGroupName() + " already exists!");

    entityManager.getTransaction().begin();
    entityManager.persist(chatGroup);
    entityManager.getTransaction().commit();
    entityManager.refresh(chatGroup);
  }

  @Override
  public ChatGroup findGroupById(long id) {
    ChatGroup chatGroup = this.entityManager.find(ChatGroup.class, id);
    if (chatGroup == null)
      return null;
    entityManager.refresh(chatGroup);
    return chatGroup;
  }

  @Override
  public void updateGroup(ChatGroup chatGroup) {
    validate(chatGroup == null, "updateGroup: Reference chatGroup is null.");
    entityManager.getTransaction().begin();
    ChatGroup old = entityManager.find(ChatGroup.class, chatGroup.getId());
    if (old == null) {
      entityManager.getTransaction().commit();
      throw new IllegalStateException("Can not update non-existing group.");
    } else {
      old.setGroupName(chatGroup.getGroupName());
      old.setGroupUsers(chatGroup.getGroupUsers());
      entityManager.merge(old);
      entityManager.getTransaction().commit();
    }

    entityManager.refresh(chatGroup);
  }

  @Override
  public void deleteGroupById(long id) {
    entityManager.getTransaction().begin();
    ChatGroup group = entityManager.find(ChatGroup.class, id);
    if (group != null)
      entityManager.remove(group);
    entityManager.getTransaction().commit();
  }

  @Override
  public void deleteGroupUser(GroupUser groupUser) {
    validate(groupUser == null, "deleteGroupUser: Reference groupUser is null.");

    GroupUserId id = new GroupUserId(groupUser.getUser().getName(), groupUser.getChatGroup().getId());
    entityManager.getTransaction().begin();
    GroupUser old = entityManager.find(GroupUser.class, id);
    entityManager.remove(old);
    entityManager.getTransaction().commit();
  }

  private void validate(boolean b, String s) {
    if (b)
      throw new IllegalArgumentException(s);
  }

  @Override
  public void updateGroupUser(GroupUser groupUser) {
    validate(groupUser == null, "updateGroupUser: Reference groupUser is null.");

    GroupUserId id = new GroupUserId(groupUser.getUser().getName(), groupUser.getChatGroup().getId());
    entityManager.getTransaction().begin();
    GroupUser old = entityManager.find(GroupUser.class, id);
    old.setModerator(groupUser.isModerator());
    entityManager.merge(old);
    entityManager.getTransaction().commit();

  }

  @Override
  public void updateGroupUser(String username, long groupid, boolean isModerator) {

    GroupUserId id = new GroupUserId(username, groupid);
    entityManager.getTransaction().begin();
    GroupUser old = entityManager.find(GroupUser.class, id);
    old.setModerator(isModerator);
    entityManager.merge(old);
    entityManager.getTransaction().commit();

  }

  public void refresh(Object entity) {
    entityManager.refresh(entity);
  }

  @Override
  public ChatGroup findGroupByName(String name) {
    validate(name == null, "findGroupByName: Reference name is null.");

    List<ChatGroup> res = entityManager
            .createQuery("SELECT g from ChatGroup g where g.groupName =:name")
            .setParameter("name", name)
            .getResultList();

    if (res.size() == 1) {
      return res.get(0);
    } else {
      return null;
    }
  }

  /**
   * Follow user
   *
   * @param follow the follow object to be created
   */
  @Override
  public void followUser(Follow follow) {
    validate(follow == null, "followUser: Reference groupUser is null.");
    entityManager.getTransaction().begin();
    entityManager.persist(follow);
    entityManager.getTransaction().commit();
  }

  @Override
  public void unfollowUser(long id) {
    entityManager.getTransaction().begin();
    Follow f = entityManager.find(Follow.class, id);
    if (f != null)
      entityManager.remove(f);
    entityManager.getTransaction().commit();
  }

  @Override
  @SuppressWarnings("unchecked")
  public List<Follow> findFollowing(String u) {
    if (u == null) {
      return new ArrayList<>();
    }
    return entityManager.createQuery("SELECT f FROM Follow f where f.follower = :follower")
            .setParameter(follower, u).getResultList();
  }

  @Override
  public boolean isFollowing(String s, String s1) {
    return !entityManager.createQuery("SELECT f FROM Follow f where f.follower = :follower " +
            "AND f.followed = :followed")
            .setParameter(follower, s).setParameter("followed", s1)
            .getResultList().isEmpty();
  }

  @Override
  @SuppressWarnings("unchecked")
  public List<Follow> getFollowById(String from, String toBeUnfollowed) {
    return entityManager.createQuery("SELECT f FROM Follow f where f.follower = :follower " +
            "AND f.followed = :followed")
            .setParameter(follower, from).setParameter("followed", toBeUnfollowed)
            .getResultList();
  }

  @Override
  public void createGroupUser(GroupUser groupUser) {
    validate(groupUser == null, "createGroupUser: Reference groupUser is null.");
    entityManager.getTransaction().begin();
    entityManager.persist(groupUser);
    entityManager.getTransaction().commit();
  }


  @Override
  public void addPrivilegeUser(String username) {
    validate(username == null, "addPrivilegeUser failed: username is null.");
    entityManager.getTransaction().begin();
    Privilege old = entityManager.find(Privilege.class, username);
    if (old != null) {
      entityManager.getTransaction().commit();
      throw new IllegalStateException("addPrivilegeUser failed: user exists.");
    }
    entityManager.persist(new Privilege(username));
    entityManager.getTransaction().commit();
  }

  @Override
  public boolean hasPrivilege(String username) {
    validate(username == null, "hasPrivilege failed: username is null.");
    boolean ret = false;
    Privilege privilege = this.entityManager.find(Privilege.class, username);
    if (privilege != null) {
      ret = true;
      entityManager.refresh(privilege);
    }
    return ret;
  }

  @Override
  public void deletePrivilegeUser(String username) {
    validate(username == null, "delete PrivilegeUser failed: username is null.");

    entityManager.getTransaction().begin();
    Privilege privilege = this.entityManager.find(Privilege.class, username);
    if (privilege != null) {
      entityManager.remove(privilege);
    }
    entityManager.getTransaction().commit();
  }

  @Override
  public List<IMessage> findInvitationsByReceiver(String receiver) {
    validate(receiver == null, "findInvitationsByReceiver: Reference receiver is null.");
    return entityManager.createQuery("SELECT m FROM InviteMessage m where m.to = :username")
            .setParameter(uName, receiver).getResultList();
  }

  @Override
  public List<IMessage> findInvitationsBySender(String sender) {
    validate(sender == null, "findInvitationsBySender: Reference sender is null.");
    return entityManager.createQuery("SELECT m FROM InviteMessage m where m.from = :username")
            .setParameter(uName, sender).getResultList();
  }

  @Override
  public List<IMessage> findUnacknowledgedInvitationsByReceiver(String receiver) {
    validate(receiver == null, "findUnacknowledgedInvitationsByReceiver: Reference receiver is null.");
    return entityManager.createQuery("SELECT m FROM InviteMessage m where m.to = :username and m.acknowledged = false")
            .setParameter(uName, receiver).getResultList();
  }

  @Override
  public void updateInvitationMessageStatus(InviteMessage message) {
    validate(message == null, "updateInvitationMessage: Reference message is null.");
    InviteMessage old = entityManager.find(InviteMessage.class, message.getId());
    if(old!=null){
      entityManager.getTransaction().begin();
      old.setAcknowledged(message.isAcknowledged());
      old.setAccepted(message.isAccepted());
      entityManager.getTransaction().commit();
    }else{
      throw new IllegalStateException("updateInvitationMessage: can not update non-existing message.");
    }
  }

  @Override
  public List<User> findUsersByName(String name) {
    validate(name == null, "findUserByUsername: Reference name is null.");
    return this.entityManager
            .createQuery("SELECT u FROM User u WHERE u.name LIKE CONCAT('%',:query,'%')")
            .setParameter("query", name).getResultList();

  }
}
