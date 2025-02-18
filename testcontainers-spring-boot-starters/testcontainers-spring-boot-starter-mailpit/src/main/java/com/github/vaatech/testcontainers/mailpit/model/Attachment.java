package com.github.vaatech.testcontainers.mailpit.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

@Data
@JsonPropertyOrder({
        Attachment.JSON_PROPERTY_CONTENT_ID,
        Attachment.JSON_PROPERTY_CONTENT_TYPE,
        Attachment.JSON_PROPERTY_FILE_NAME,
        Attachment.JSON_PROPERTY_PART_ID,
        Attachment.JSON_PROPERTY_SIZE
})
public class Attachment {
    public static final String JSON_PROPERTY_CONTENT_ID = "ContentID";
    public static final String JSON_PROPERTY_CONTENT_TYPE = "ContentType";
    public static final String JSON_PROPERTY_FILE_NAME = "FileName";
    public static final String JSON_PROPERTY_PART_ID = "PartID";
    public static final String JSON_PROPERTY_SIZE = "Size";

    @JsonProperty(JSON_PROPERTY_CONTENT_ID)
    private String contentID;

    @JsonProperty(JSON_PROPERTY_CONTENT_TYPE)
    private String contentType;

    @JsonProperty(JSON_PROPERTY_FILE_NAME)
    private String fileName;

    @JsonProperty(JSON_PROPERTY_PART_ID)
    private String partID;

    @JsonProperty(JSON_PROPERTY_SIZE)
    private Long size;
}

