package com.neu.prattle.model;

import org.codehaus.jackson.annotate.JsonTypeName;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

/***
 * A Basic POJO for Message.
 *
 * @author CS5500 Fall 2019 Teaching staff
 * @version dated 2019-10-06
 */
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorValue("Msg")
@JsonTypeName("msg")
public class Message extends AbstractMessage {

    /**
     * Message builder message builder.
     *
     * @return the message builder
     */
    public static MessageBuilder messageBuilder()   {
        return new MessageBuilder();
    }

    @Override
    public AbstractMessage copy() {
        return Message.messageBuilder()
                .setReceived(this.isReceived())
                .setFrom(this.getFrom())
                .setTo(this.getTo())
                .setMessageContent(this.getContent())
                .build();
    }

    /***
     * A Builder helper class to create instances of {@link Message}
     */
    public static class MessageBuilder extends AbstractMessageBuilder<Message>    {

        /***
         * Invoking the build method will return this message object.
         */
        public MessageBuilder()    {
            message = new Message();
            message.setFrom("Not set");
            message.setReceived(false);
        }

    }
}
