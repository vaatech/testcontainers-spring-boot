package com.github.vaatech.testcontainers.mailpit.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@JsonPropertyOrder({
        MessageSummary.JSON_PROPERTY_ATTACHMENTS,
        MessageSummary.JSON_PROPERTY_BCC,
        MessageSummary.JSON_PROPERTY_CC,
        MessageSummary.JSON_PROPERTY_CREATED,
        MessageSummary.JSON_PROPERTY_FROM,
        MessageSummary.JSON_PROPERTY_ID,
        MessageSummary.JSON_PROPERTY_MESSAGE_ID,
        MessageSummary.JSON_PROPERTY_READ,
        MessageSummary.JSON_PROPERTY_SIZE,
        MessageSummary.JSON_PROPERTY_SUBJECT,
        MessageSummary.JSON_PROPERTY_TAGS,
        MessageSummary.JSON_PROPERTY_TO
})
public class MessageSummary {

    public static final String JSON_PROPERTY_ATTACHMENTS = "Attachments";
    public static final String JSON_PROPERTY_BCC = "Bcc";
    public static final String JSON_PROPERTY_CC = "Cc";
    public static final String JSON_PROPERTY_CREATED = "Created";
    public static final String JSON_PROPERTY_FROM = "From";
    public static final String JSON_PROPERTY_ID = "ID";
    public static final String JSON_PROPERTY_MESSAGE_ID = "MessageID";
    public static final String JSON_PROPERTY_READ = "Read";
    public static final String JSON_PROPERTY_REPLAY_TO = "ReplyTo";
    public static final String JSON_PROPERTY_SIZE = "Size";
    public static final String JSON_PROPERTY_SNIPPET = "Snippet";
    public static final String JSON_PROPERTY_SUBJECT = "Subject";
    public static final String JSON_PROPERTY_TAGS = "Tags";
    public static final String JSON_PROPERTY_TO = "To";

    @JsonProperty(JSON_PROPERTY_ATTACHMENTS)
    private Long attachments;

    @JsonProperty(JSON_PROPERTY_BCC)
    private List<Address> bcc = new ArrayList<>();

    @JsonProperty(JSON_PROPERTY_CC)
    private List<Address> cc = new ArrayList<>();

    @JsonProperty(JSON_PROPERTY_CREATED)
    private OffsetDateTime created;

    @JsonProperty(JSON_PROPERTY_FROM)
    private Address from;

    @JsonProperty(JSON_PROPERTY_ID)
    private String ID;

    @JsonProperty(JSON_PROPERTY_MESSAGE_ID)
    private String messageID;

    @JsonProperty(JSON_PROPERTY_READ)
    private Boolean read;

    @JsonProperty(JSON_PROPERTY_REPLAY_TO)
    private List<Address> replyTo;

    @JsonProperty(JSON_PROPERTY_SIZE)
    private Long size;

    @JsonProperty(JSON_PROPERTY_SNIPPET)
    private String snippet;

    @JsonProperty(JSON_PROPERTY_SUBJECT)
    private String subject;

    @JsonProperty(JSON_PROPERTY_TAGS)
    private List<String> tags = new ArrayList<>();

    @JsonProperty(JSON_PROPERTY_TO)
    private List<Address> to = new ArrayList<>();

    public MessageSummary addBccItem(Address bccItem) {
        if (this.bcc == null) {
            this.bcc = new ArrayList<>();
        }
        this.bcc.add(bccItem);
        return this;
    }

    public MessageSummary addCcItem(Address ccItem) {
        if (this.cc == null) {
            this.cc = new ArrayList<>();
        }
        this.cc.add(ccItem);
        return this;
    }

    public MessageSummary addTagsItem(String tagsItem) {
        if (this.tags == null) {
            this.tags = new ArrayList<>();
        }
        this.tags.add(tagsItem);
        return this;
    }

    public MessageSummary addToItem(Address toItem) {
        if (this.to == null) {
            this.to = new ArrayList<>();
        }
        this.to.add(toItem);
        return this;
    }
}

