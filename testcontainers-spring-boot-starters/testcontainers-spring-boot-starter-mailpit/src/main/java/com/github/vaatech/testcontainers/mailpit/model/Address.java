package com.github.vaatech.testcontainers.mailpit.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

@Data
@JsonPropertyOrder({
        Address.JSON_PROPERTY_ADDRESS,
        Address.JSON_PROPERTY_NAME
})
public class Address {

    public static final String JSON_PROPERTY_ADDRESS = "Address";
    public static final String JSON_PROPERTY_NAME = "Name";

    @JsonProperty(JSON_PROPERTY_ADDRESS)
    private String address;
    @JsonProperty(JSON_PROPERTY_NAME)
    private String name;
}
