package ru.borisov.users.controller.response;

import lombok.Builder;

@Builder
public record ApiResponse(boolean success, String message) {
}
