package com.github.vaatech.testcontainers.mailpit.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@JsonPropertyOrder({
        Message.JSON_PROPERTY_ATTACHMENTS,
        Message.JSON_PROPERTY_BCC,
        Message.JSON_PROPERTY_CC,
        Message.JSON_PROPERTY_DATE,
        Message.JSON_PROPERTY_FROM,
        Message.JSON_PROPERTY_HTML,
        Message.JSON_PROPERTY_ID,
        Message.JSON_PROPERTY_INLINE,
        Message.JSON_PROPERTY_MESSAGE_ID,
        Message.JSON_PROPERTY_REPLY_TO,
        Message.JSON_PROPERTY_RETURN_PATH,
        Message.JSON_PROPERTY_SIZE,
        Message.JSON_PROPERTY_SUBJECT,
        Message.JSON_PROPERTY_TAGS,
        Message.JSON_PROPERTY_TEXT,
        Message.JSON_PROPERTY_TO
})
public class Message {

    public static final String JSON_PROPERTY_ATTACHMENTS = "Attachments";
    public static final String JSON_PROPERTY_BCC = "Bcc";
    public static final String JSON_PROPERTY_CC = "Cc";
    public static final String JSON_PROPERTY_DATE = "Date";
    public static final String JSON_PROPERTY_FROM = "From";
    public static final String JSON_PROPERTY_HTML = "HTML";
    public static final String JSON_PROPERTY_ID = "ID";
    public static final String JSON_PROPERTY_INLINE = "Inline";
    public static final String JSON_PROPERTY_MESSAGE_ID = "MessageID";
    public static final String JSON_PROPERTY_REPLY_TO = "ReplyTo";
    public static final String JSON_PROPERTY_RETURN_PATH = "ReturnPath";
    public static final String JSON_PROPERTY_SIZE = "Size";
    public static final String JSON_PROPERTY_SUBJECT = "Subject";
    public static final String JSON_PROPERTY_TAGS = "Tags";
    public static final String JSON_PROPERTY_TEXT = "Text";
    public static final String JSON_PROPERTY_TO = "To";


    @JsonProperty(JSON_PROPERTY_ATTACHMENTS)
    private List<Attachment> attachments = new ArrayList<>();

    @JsonProperty(JSON_PROPERTY_BCC)
    private List<Address> bcc = new ArrayList<>();

    @JsonProperty(JSON_PROPERTY_CC)
    private List<Address> cc = new ArrayList<>();

    @JsonProperty(JSON_PROPERTY_DATE)
    private OffsetDateTime date;

    @JsonProperty(JSON_PROPERTY_FROM)
    private Address from;

    @JsonProperty(JSON_PROPERTY_HTML)
    private String HTML;

    @JsonProperty(JSON_PROPERTY_ID)
    private String ID;

    @JsonProperty(JSON_PROPERTY_INLINE)
    private List<Attachment> inline = new ArrayList<>();

    @JsonProperty(JSON_PROPERTY_MESSAGE_ID)
    private String messageID;

    @JsonProperty(JSON_PROPERTY_REPLY_TO)
    private List<Address> replyTo = new ArrayList<>();

    @JsonProperty(JSON_PROPERTY_RETURN_PATH)
    private String returnPath;

    @JsonProperty(JSON_PROPERTY_SIZE)
    private Long size;

    @JsonProperty(JSON_PROPERTY_SUBJECT)
    private String subject;

    @JsonProperty(JSON_PROPERTY_TAGS)
    private List<String> tags = new ArrayList<>();

    @JsonProperty(JSON_PROPERTY_TEXT)
    private String text;

    @JsonProperty(JSON_PROPERTY_TO)
    private List<Address> to = new ArrayList<>();

    public Message addAttachmentsItem(Attachment attachmentsItem) {
        if (this.attachments == null) {
            this.attachments = new ArrayList<>();
        }
        this.attachments.add(attachmentsItem);
        return this;
    }

    public Message addBccItem(Address bccItem) {
        if (this.bcc == null) {
            this.bcc = new ArrayList<>();
        }
        this.bcc.add(bccItem);
        return this;
    }

    public Message addCcItem(Address ccItem) {
        if (this.cc == null) {
            this.cc = new ArrayList<>();
        }
        this.cc.add(ccItem);
        return this;
    }

    public Message addInlineItem(Attachment inlineItem) {
        if (this.inline == null) {
            this.inline = new ArrayList<>();
        }
        this.inline.add(inlineItem);
        return this;
    }

    public Message addReplyToItem(Address replyToItem) {
        if (this.replyTo == null) {
            this.replyTo = new ArrayList<>();
        }
        this.replyTo.add(replyToItem);
        return this;
    }

    public Message addTagsItem(String tagsItem) {
        if (this.tags == null) {
            this.tags = new ArrayList<>();
        }
        this.tags.add(tagsItem);
        return this;
    }

    public Message addToItem(Address toItem) {
        if (this.to == null) {
            this.to = new ArrayList<>();
        }
        this.to.add(toItem);
        return this;
    }
}