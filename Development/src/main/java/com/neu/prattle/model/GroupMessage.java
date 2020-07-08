package com.neu.prattle.model;


import org.codehaus.jackson.annotate.JsonTypeName;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;


@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorValue("groupMsg")
@JsonTypeName("groupMsg")
public class GroupMessage extends AbstractMessage {

    @Column(name = "groupname")
    private String groupName;

    /**
     * Initiate a GroupMessage object
     */
    public GroupMessage() {
        // for jpa
    }


    /**
     * Get group name
     *
     * @return group name
     */
    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    /**
     * Message builder message builder.
     *
     * @return the message builder
     */
    public static GroupMessage.MessageBuilder groupMessageBuilder()   {
        return new MessageBuilder();
    }

    @Override
    public AbstractMessage copy() {
        return GroupMessage.groupMessageBuilder().setGroupName(this.getGroupName())
                .setReceived(this.isReceived())
                .setFrom(this.getFrom())
                .setTo(this.getTo())
                .setMessageContent(this.getContent())
                .build();
    }

    /***
     * A Builder helper class to create instances of {@link GroupMessage}
     */
    public static class MessageBuilder extends AbstractMessageBuilder<GroupMessage>    {
        /***
         * Invoking the build method will return this message object.
         */
        public MessageBuilder()    {
            message = new GroupMessage();
            message.setFrom("Not set");
            message.setReceived(false);
        }

        public MessageBuilder setGroupName(String groupName) {
            message.setGroupName(groupName);
            return this;
        }

        public  MessageBuilder buildFromMessage(IMessage userMessage) {
            message.setFrom(userMessage.getFrom());
            message.setTo(userMessage.getTo());
            message.setContent(userMessage.getContent());
            message.setReceived(userMessage.isReceived());
            return this;
        }
    }
}
