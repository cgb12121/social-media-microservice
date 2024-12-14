package com.mine.socialmedia.common.dtos.response;

import java.util.Map;

public record AuthResponse(
        Map<String, String> tokens
) {

}