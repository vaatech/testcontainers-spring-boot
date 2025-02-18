package com.github.vaatech.testcontainers.mailpit.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@JsonPropertyOrder({
        DeleteRequest.JSON_PROPERTY_IDS
})
public class DeleteRequest {

    public static final String JSON_PROPERTY_IDS = "IDs";

    @JsonProperty(JSON_PROPERTY_IDS)
    private List<String> ids = new ArrayList<>();

    public DeleteRequest addIdsItem(String idsItem) {
        if (this.ids == null) {
            this.ids = new ArrayList<>();
        }
        this.ids.add(idsItem);
        return this;
    }

}
