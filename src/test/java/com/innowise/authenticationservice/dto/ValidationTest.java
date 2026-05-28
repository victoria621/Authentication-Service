package com.innowise.authenticationservice.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.math.BigDecimal;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("DTO Validation Tests")
class ValidationTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Should validate valid AuthRequest")
    void shouldValidateValidAuthRequest() {
        AuthRequest request = new AuthRequest("john.doe", "password123");
        Set<ConstraintViolation<AuthRequest>> violations = validator.validate(request);
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("Should reject AuthRequest with blank login")
    void shouldRejectAuthRequestWithBlankLogin() {
        AuthRequest request = new AuthRequest("", "password123");
        Set<ConstraintViolation<AuthRequest>> violations = validator.validate(request);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getPropertyPath().toString()).isEqualTo("login");
    }

    @Test
    @DisplayName("Should reject AuthRequest with blank password")
    void shouldRejectAuthRequestWithBlankPassword() {
        AuthRequest request = new AuthRequest("john.doe", "");
        Set<ConstraintViolation<AuthRequest>> violations = validator.validate(request);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getPropertyPath().toString()).isEqualTo("password");
    }

    @Test
    @DisplayName("Should reject AuthRequest with null login")
    void shouldRejectAuthRequestWithNullLogin() {
        AuthRequest request = new AuthRequest(null, "password123");
        Set<ConstraintViolation<AuthRequest>> violations = validator.validate(request);
        assertThat(violations).hasSize(1);
    }


    @Test
    @DisplayName("Should validate valid UserRequest")
    void shouldValidateValidUserRequest() {
        UserRequest request = new UserRequest("john.doe", "password123");
        Set<ConstraintViolation<UserRequest>> violations = validator.validate(request);
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("Should reject UserRequest with blank login")
    void shouldRejectUserRequestWithBlankLogin() {
        UserRequest request = new UserRequest("", "password123");
        Set<ConstraintViolation<UserRequest>> violations = validator.validate(request);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isNotNull();
    }

    @Test
    @DisplayName("Should reject UserRequest with blank password")
    void shouldRejectUserRequestWithBlankPassword() {
        UserRequest request = new UserRequest("john.doe", "");
        Set<ConstraintViolation<UserRequest>> violations = validator.validate(request);
        assertThat(violations).hasSize(1);
    }


    @Test
    @DisplayName("Should validate valid ItemRequest")
    void shouldValidateValidItemRequest() {
        ItemRequest request = new ItemRequest("Laptop", "High-end laptop", new BigDecimal("999.99"));
        Set<ConstraintViolation<ItemRequest>> violations = validator.validate(request);
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("Should reject ItemRequest with null price")
    void shouldRejectItemRequestWithNullPrice() {
        ItemRequest request = new ItemRequest("Laptop", "Description", null);
        Set<ConstraintViolation<ItemRequest>> violations = validator.validate(request);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getPropertyPath().toString()).isEqualTo("price");
    }

    @Test
    @DisplayName("Should accept ItemRequest with null name and description")
    void shouldAcceptItemRequestWithNullNameAndDescription() {
        ItemRequest request = new ItemRequest(null, null, new BigDecimal("99.99"));
        Set<ConstraintViolation<ItemRequest>> violations = validator.validate(request);
        assertThat(violations).isEmpty(); // name and description are not @NotBlank
    }


    @Test
    @DisplayName("Should validate valid ItemResponse")
    void shouldValidateValidItemResponse() {
        ItemResponse response = new ItemResponse(1L, "Laptop", "Description", new BigDecimal("999.99"), null);
        Set<ConstraintViolation<ItemResponse>> violations = validator.validate(response);
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("Should reject ItemResponse with null price")
    void shouldRejectItemResponseWithNullPrice() {
        ItemResponse response = new ItemResponse(1L, "Laptop", "Description", null, null);
        Set<ConstraintViolation<ItemResponse>> violations = validator.validate(response);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getPropertyPath().toString()).isEqualTo("price");
    }

    @Test
    @DisplayName("Should validate valid UserResponse")
    void shouldValidateValidUserResponse() {
        UserResponse response = new UserResponse(1L, "john.doe", null, true);
        Set<ConstraintViolation<UserResponse>> violations = validator.validate(response);
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("Should reject UserResponse with null id")
    void shouldRejectUserResponseWithNullId() {
        UserResponse response = new UserResponse(null, "john.doe", null, true);
        Set<ConstraintViolation<UserResponse>> violations = validator.validate(response);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getPropertyPath().toString()).isEqualTo("id");
    }

    @Test
    @DisplayName("Should reject UserResponse with blank login")
    void shouldRejectUserResponseWithBlankLogin() {
        UserResponse response = new UserResponse(1L, "", null, true);
        Set<ConstraintViolation<UserResponse>> violations = validator.validate(response);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getPropertyPath().toString()).isEqualTo("login");
    }
}