package ru.cytty.dao;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.With;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
@With

public class CreateAccountRequest {
@Setter
@Getter

private String firstname;
    private String lastname;
    private Integer totalprice;

    private Boolean depositpaid;
    private BookingdatesRequest bookingdates;
    private String additionalneeds;

}
