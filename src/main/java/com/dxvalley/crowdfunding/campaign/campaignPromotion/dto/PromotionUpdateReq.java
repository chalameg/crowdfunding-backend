package com.dxvalley.crowdfunding.campaign.campaignPromotion.dto;

import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = WordsValidator.class)
@interface Words {
    int min() default 1;

    String message() default "Invalid word count";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

@Data
public class PromotionUpdateReq {
    @NotBlank
    @Words(min = 4, message = "Description must have more than three words")
    private String description;
}

class WordsValidator implements ConstraintValidator<Words, String> {
    private int min;

    @Override
    public void initialize(Words constraintAnnotation) {
        min = constraintAnnotation.min();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; // Null values are handled by @NotBlank annotation
        }
        String[] words = value.split("\\s+");
        return words.length >= min;
    }
}
