package io.github.bvilii.yapper.server.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ServerConfigTest {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/yapper";
    private static final String DB_USER = "yapper";
    private static final String DB_PASSWORD = "secret";

    @DisplayName("Should throw when the port is not between 1 and 65535")
    @ParameterizedTest
    @ValueSource(ints = {0, -1, 65536, 100_000})
    void rejectsPortsOutOfRange(int port) {
        assertThatThrownBy(() -> new ServerConfig(port, DB_URL, DB_USER, DB_PASSWORD))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 9999, 65535})
    @DisplayName("Should accept ports that are in between 1 and 65535")
    void acceptValidPortNumbers(int port) {
        assertThat(new ServerConfig(port, DB_URL, DB_USER, DB_PASSWORD).port()).isEqualTo(port);
    }

    @Test
    @DisplayName("Should throw when a database field is null")
    void rejectsNullDbFields() {
        assertThatThrownBy(() -> new ServerConfig(9099, null, DB_USER, DB_PASSWORD))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> new ServerConfig(9099, DB_URL, null, DB_PASSWORD))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> new ServerConfig(9099, DB_URL, DB_USER, null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("Should hide the password in toString")
    void toString_doesNotContainPassword() {
        ServerConfig config = new ServerConfig(9099, DB_URL, DB_USER, DB_PASSWORD);

        assertThat(config.toString())
                .doesNotContain(DB_PASSWORD)
                .contains("***");
    }
}