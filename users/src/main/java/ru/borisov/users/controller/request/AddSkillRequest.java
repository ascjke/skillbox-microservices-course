package ru.borisov.users.controller.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import ru.borisov.users.model.SkillType;

@Data
@Builder
public class AddSkillRequest {

    @NotBlank
    private String title;

    @NotNull
    private SkillType skillType;
}
