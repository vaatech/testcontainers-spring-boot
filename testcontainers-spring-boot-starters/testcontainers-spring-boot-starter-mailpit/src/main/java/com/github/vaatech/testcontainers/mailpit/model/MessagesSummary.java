package com.github.vaatech.testcontainers.mailpit.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@JsonPropertyOrder({
        MessagesSummary.JSON_PROPERTY_MESSAGES,
        MessagesSummary.JSON_PROPERTY_MESSAGES_COUNT,
        MessagesSummary.JSON_PROPERTY_START,
        MessagesSummary.JSON_PROPERTY_TAGS,
        MessagesSummary.JSON_PROPERTY_TOTAL,
        MessagesSummary.JSON_PROPERTY_UNREAD
})
public class MessagesSummary {

    public static final String JSON_PROPERTY_MESSAGES = "messages";
    public static final String JSON_PROPERTY_MESSAGES_COUNT = "messages_count";
    public static final String JSON_PROPERTY_START = "start";
    public static final String JSON_PROPERTY_TAGS = "tags";
    public static final String JSON_PROPERTY_TOTAL = "total";
    public static final String JSON_PROPERTY_UNREAD = "unread";

    @JsonProperty(JSON_PROPERTY_MESSAGES)
    private List<MessageSummary> messages = new ArrayList<>();

    @JsonProperty(JSON_PROPERTY_MESSAGES_COUNT)
    private Long messagesCount;

    @JsonProperty(JSON_PROPERTY_START)
    private Long start;

    @JsonProperty(JSON_PROPERTY_TAGS)
    private List<String> tags = new ArrayList<>();

    @JsonProperty(JSON_PROPERTY_TOTAL)
    private Long total;

    @JsonProperty(JSON_PROPERTY_UNREAD)
    private Long unread;

    public MessagesSummary addMessagesItem(MessageSummary messagesItem) {
        if (this.messages == null) {
            this.messages = new ArrayList<>();
        }
        this.messages.add(messagesItem);
        return this;
    }

    public MessagesSummary addTagsItem(String tagsItem) {
        if (this.tags == null) {
            this.tags = new ArrayList<>();
        }
        this.tags.add(tagsItem);
        return this;
    }
}
