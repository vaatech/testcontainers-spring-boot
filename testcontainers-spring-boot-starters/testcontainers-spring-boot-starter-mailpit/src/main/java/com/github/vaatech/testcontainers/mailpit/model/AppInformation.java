package com.github.vaatech.testcontainers.mailpit.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

import java.util.Map;

@Data
@JsonPropertyOrder({
        AppInformation.JSON_PROPERTY_DATABASE,
        AppInformation.JSON_PROPERTY_DATABASE_SIZE,
        AppInformation.JSON_PROPERTY_LATEST_VERSION,
        AppInformation.JSON_PROPERTY_MESSAGES,
        AppInformation.JSON_PROPERTY_RUNTIME_STATS,
        AppInformation.JSON_PROPERTY_TAGS,
        AppInformation.JSON_PROPERTY_UNREAD,
        AppInformation.JSON_PROPERTY_VERSION
})
public class AppInformation {
    public static final String JSON_PROPERTY_DATABASE = "Database";
    public static final String JSON_PROPERTY_DATABASE_SIZE = "DatabaseSize";
    public static final String JSON_PROPERTY_LATEST_VERSION = "LatestVersion";
    public static final String JSON_PROPERTY_MESSAGES = "Messages";
    public static final String JSON_PROPERTY_RUNTIME_STATS = "RuntimeStats";
    public static final String JSON_PROPERTY_TAGS = "Tags";
    public static final String JSON_PROPERTY_UNREAD = "Unread";
    public static final String JSON_PROPERTY_VERSION = "Version";

    @JsonProperty(JSON_PROPERTY_DATABASE)
    private String database;

    @JsonProperty(JSON_PROPERTY_DATABASE_SIZE)
    private Long databaseSize;

    @JsonProperty(JSON_PROPERTY_LATEST_VERSION)
    private String latestVersion;

    @JsonProperty(JSON_PROPERTY_MESSAGES)
    private Long messages;

    @JsonProperty(JSON_PROPERTY_RUNTIME_STATS)
    private RuntimeStats runtimeStats;

    @JsonProperty(JSON_PROPERTY_TAGS)
    private Map<String, Integer> tags;

    @JsonProperty(JSON_PROPERTY_UNREAD)
    private Long unread;

    @JsonProperty(JSON_PROPERTY_VERSION)
    private String version;

    @Data
    @JsonPropertyOrder({
            RuntimeStats.JSON_PROPERTY_MEMORY,
            RuntimeStats.JSON_PROPERTY_MESSAGES_DELETED,
            RuntimeStats.JSON_PROPERTY_SMTP_ACCEPTED,
            RuntimeStats.JSON_PROPERTY_SMTP_ACCEPTED_SIZE,
            RuntimeStats.JSON_PROPERTY_SMTP_IGNORED,
            RuntimeStats.JSON_PROPERTY_SMTP_REJECTED,
            RuntimeStats.JSON_PROPERTY_UPTIME
    })
    public static class RuntimeStats {
        public static final String JSON_PROPERTY_MEMORY = "Memory";
        public static final String JSON_PROPERTY_MESSAGES_DELETED = "MessagesDeleted";
        public static final String JSON_PROPERTY_SMTP_ACCEPTED = "SMTPAccepted";
        public static final String JSON_PROPERTY_SMTP_ACCEPTED_SIZE = "SMTPAcceptedSize";
        public static final String JSON_PROPERTY_SMTP_IGNORED = "SMTPIgnored";
        public static final String JSON_PROPERTY_SMTP_REJECTED = "SMTPRejected";
        public static final String JSON_PROPERTY_UPTIME = "Uptime";

        @JsonProperty(JSON_PROPERTY_MEMORY)
        private Long memory;

        @JsonProperty(JSON_PROPERTY_MESSAGES_DELETED)
        private Long messagesDeleted;

        @JsonProperty(JSON_PROPERTY_SMTP_ACCEPTED)
        private Long smtpAccepted;

        @JsonProperty(JSON_PROPERTY_SMTP_ACCEPTED_SIZE)
        private Long smtpAcceptedSize;

        @JsonProperty(JSON_PROPERTY_SMTP_IGNORED)
        private Long smtpIgnored;

        @JsonProperty(JSON_PROPERTY_SMTP_REJECTED)
        private Long smtpRejected;

        @JsonProperty(JSON_PROPERTY_UPTIME)
        private Long uptime;
    }

}