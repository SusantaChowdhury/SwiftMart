package com.susanta.SwiftMart.entities;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordMatchesValidator implements ConstraintValidator<PasswordsMatched, UserForm> {

    @Override
    public boolean isValid(UserForm userForm, ConstraintValidatorContext context) {
        if (userForm.getPassword() == null || userForm.getConfirmPassword() == null) {
            return false;
        }
        return userForm.getPassword().equals(userForm.getConfirmPassword());
    }
}
