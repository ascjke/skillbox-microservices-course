package ru.borisov.users.controller.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.borisov.users.model.SkillType;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddSkillRequest {

    @NotBlank
    private String title;

    @NotNull
    private SkillType skillType;
}
