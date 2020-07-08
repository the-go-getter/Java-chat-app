package com.neu.prattle.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.Temporal;

import static javax.persistence.TemporalType.TIMESTAMP;

/**
 * The type Abstract message.
 */
@Entity
@Table(name = "MESSAGES")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="TYPE", discriminatorType= DiscriminatorType.STRING,length=20)
public abstract class AbstractMessage implements IMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private long id;
    /***
     * The name of the user who sent this message.
     */
    @Column(name = "sender")
    private String from;
    /***
     * The name of the user to whom the message is sent.
     */
    @Column(name = "receiver")
    private String to;
    /***
     * It represents the contents of the message.
     */
    @Column
    private String content;

    /**
     * True if message is received by receiver, false otherwise. Default value is false.
     */
    @Column
    private boolean isReceived;

    @Temporal(value = TIMESTAMP)
    private java.util.Date timestamp = new Date();

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isReceived() {
        return isReceived;
    }

    public void setReceived(boolean received) {
        this.isReceived = received;
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public void setId(long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "From: " + from
                + " To: " + to
                + " Content: " + content;
    }

    @Override
    public String getFrom() {
        return from;
    }

    @Override
    public void setFrom(String from) {
        this.from = from;
    }

    @Override
    public String getTo() {
        return to;
    }

    @Override
    public void setTo(String to) {
        this.to = to;
    }

    @Override
    public String getContent() {
        return content;
    }

    @Override
    public void setContent(String content) {
        this.content = content;
    }

}
