package com._6.group4.smartcart.auth;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PasswordHasherTest {

    private final PasswordHasher passwordHasher = new PasswordHasher();

    @Test
    void hash_createsBCryptDigestThatMatchesRawPassword() {
        String rawPassword = "S3curePass!";

        String firstHash = passwordHasher.hash(rawPassword);
        String secondHash = passwordHasher.hash(rawPassword);

        assertTrue(firstHash.startsWith("$2"));
        assertTrue(passwordHasher.matches(rawPassword, firstHash));
        assertTrue(passwordHasher.matches(rawPassword, secondHash));
        assertNotEquals(firstHash, secondHash);
    }

    @Test
    void matches_returnsFalseWhenEitherValueIsNull() {
        assertFalse(passwordHasher.matches(null, "hash"));
        assertFalse(passwordHasher.matches("password", null));
    }

    @Test
    void hash_rejectsNullPasswords() {
        assertThrows(IllegalArgumentException.class, () -> passwordHasher.hash(null));
    }
}
