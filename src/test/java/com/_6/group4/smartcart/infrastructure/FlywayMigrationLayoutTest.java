package com._6.group4.smartcart.infrastructure;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FlywayMigrationLayoutTest {

    private static final Pattern VERSIONED_SQL = Pattern.compile("^V(\\d+)__.+\\.sql$");

    @Test
    void migrations_areUniquelyVersionedAndSequential() throws IOException {
        List<String> names = migrationNames();
        List<Integer> versions = names.stream()
                .map(FlywayMigrationLayoutTest::versionFrom)
                .sorted()
                .toList();
        int highestVersion = versions.get(versions.size() - 1);

        assertEquals(versions.stream().distinct().toList(), versions);
        assertEquals(
                java.util.stream.IntStream.rangeClosed(1, highestVersion).boxed().toList(),
                versions
        );
    }

    @Test
    void migrations_includeAdminSeedPantryNormalizationAndPreferenceUpdates() throws IOException {
        List<String> names = migrationNames();

        assertTrue(names.contains("V5__seed_admin_user.sql"));
        assertTrue(names.contains("V6__fix_admin_password_hash.sql"));
        assertTrue(names.contains("V7__ensure_pantry_items_canonical_name.sql"));
        assertTrue(names.contains("V8__add_weekly_shopping_preferences.sql"));
    }

    private List<String> migrationNames() throws IOException {
        Path migrationDir = Path.of("src", "main", "resources", "db", "migration");
        assertTrue(Files.isDirectory(migrationDir));

        try (var paths = Files.list(migrationDir)) {
            return paths
                    .map(path -> path.getFileName().toString())
                    .filter(name -> VERSIONED_SQL.matcher(name).matches())
                    .sorted(Comparator.naturalOrder())
                    .toList();
        }
    }

    private static Integer versionFrom(String name) {
        Matcher matcher = VERSIONED_SQL.matcher(name);
        assertTrue(matcher.matches(), () -> "Unexpected migration filename: " + name);
        return Integer.parseInt(matcher.group(1));
    }
}
