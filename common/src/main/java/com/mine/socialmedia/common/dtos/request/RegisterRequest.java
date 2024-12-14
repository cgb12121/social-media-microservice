package com.mine.socialmedia.common.dtos.request;

import jakarta.annotation.Nullable;

import java.util.UUID;

public record RegisterRequest(
        //These are for auth entity
        String email,
        String phoneNumber,
        String password,
        String reEnterPassword,
        //These are for user-entity
        String firstName,
        String lastName,
        String address,
        String country,

        @Nullable
        UUID authId
) {

}
