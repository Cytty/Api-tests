package ru.cytty.dao;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.With;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
@With

public class CreateTokenRequest {

    private String username;
    private String password;

}