package com.example.idp.cloudentity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * The Response from a call to "/accept". Contains a URL to which we should redirect the user.
 */
@Getter
@Setter
@NoArgsConstructor
class AcceptResponse {
    @JsonProperty("redirect_to")
    private String redirectTo;
}
