package com.neu.prattle.model;

import org.codehaus.jackson.annotate.JsonTypeName;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorValue("inviteMsg")
@JsonTypeName("inviteMsg")
public class InviteMessage extends AbstractMessage {
    @Column(name = "invitegroup")
    private String groupName = "";

    @Column(name = "accepted")
    private boolean accepted;

    @Column(name = "acknowledged")
    private boolean acknowledged;

    public InviteMessage() {
        // for jpa
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public boolean isAccepted() {
        return accepted;
    }

    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }

    public boolean isAcknowledged() {
        return acknowledged;
    }

    public void setAcknowledged(boolean acknowledged) {
        this.acknowledged = acknowledged;
    }

    /**
     * Message builder message builder.
     *
     * @return the message builder
     */
    public static InviteMessage.MessageBuilder messageBuilder()   {
        return new InviteMessage.MessageBuilder();
    }

    @Override
    public IMessage copy() {
        return InviteMessage.messageBuilder()
                .setAccepted(this.isAccepted())
                .setGroupName(this.getGroupName())
                .setAcknowledged(this.isAcknowledged())
                .setFrom(this.getFrom())
                .setTo(this.getTo())
                .setReceived(this.isReceived())
                .setMessageContent(this.getContent())
                .build();
    }

    @Override
    public String toString() {
        return "Invite group: " + this.getGroupName() +
                " From: " + this.getFrom()
                + " To: " + this.getTo()
                + " Content: " + this.getContent();
    }

    /***
     * A Builder helper class to create instances of {@link GroupMessage}
     */
    public static class MessageBuilder extends AbstractMessageBuilder<InviteMessage>    {
        /***
         * Invoking the build method will return this message object.
         */
        public MessageBuilder()    {
            message = new InviteMessage();
            message.setFrom("Not set");
            message.setReceived(false);
        }

        public InviteMessage.MessageBuilder setAccepted(boolean accepted) {
            message.setAccepted(accepted);
            return this;
        }

        public InviteMessage.MessageBuilder setGroupName(String groupName) {
            message.setGroupName(groupName);
            return this;
        }


        public InviteMessage.MessageBuilder setAcknowledged(boolean acknowledged) {
            message.setAcknowledged(acknowledged);
            return this;
        }
    }
}
