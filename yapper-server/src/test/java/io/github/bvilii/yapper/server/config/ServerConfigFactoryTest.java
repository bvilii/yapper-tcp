package io.github.bvilii.yapper.server.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ServerConfigFactoryTest {

    private static Map<String, String> validEnv() {
        Map<String, String> env = new HashMap<>();
        env.put("DB_URL", "jdbc:mysql://localhost:3306/yapper");
        env.put("DB_USER", "yapper");
        env.put("DB_PASSWORD", "secret");
        return env;
    }

    @Test
    @DisplayName("Should fall back to the default port when port key is missing")
    void load_missingPortKey_usesDefault() {
        ServerConfig config = ServerConfigFactory.load(validEnv());

        assertThat(config.port()).isEqualTo(ServerConfigFactory.DEFAULT_PORT);
    }

    @Test
    @DisplayName("Should use the port when it is valid")
    void load_validPort_usesPortFromEnv() {
        Map<String, String> env = validEnv();
        env.put("SERVER_PORT", "8000");

        ServerConfig config = ServerConfigFactory.load(env);

        assertThat(config.port()).isEqualTo(8000);
    }

    @Test
    @DisplayName("Should throw when port is an invalid configuration")
    void load_nonNumericPort_throwsIllegalArgumentException() {
        Map<String, String> env = validEnv();
        env.put("SERVER_PORT", "abc");

        assertThatThrownBy(() -> ServerConfigFactory.load(env))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "    "})
    @DisplayName("Should fall back to the default port when port value is blank")
    void load_blankPortValue_usesDefault(String value) {
        Map<String, String> env = validEnv();
        env.put("SERVER_PORT", value);

        ServerConfig config = ServerConfigFactory.load(env);

        assertThat(config.port()).isEqualTo(ServerConfigFactory.DEFAULT_PORT);
    }

    @Test
    @DisplayName("Should read the database settings from env")
    void load_validEnv_readsDbSettings() {
        ServerConfig config = ServerConfigFactory.load(validEnv());

        assertThat(config.dbUrl()).isEqualTo("jdbc:mysql://localhost:3306/yapper");
        assertThat(config.dbUser()).isEqualTo("yapper");
        assertThat(config.dbPassword()).isEqualTo("secret");
    }

    @ParameterizedTest
    @ValueSource(strings = {"DB_URL", "DB_USER", "DB_PASSWORD"})
    @DisplayName("Should throw when a required database key is missing")
    void load_missingDbKey_throwsIllegalStateException(String key) {
        Map<String, String> env = validEnv();
        env.remove(key);

        assertThatThrownBy(() -> ServerConfigFactory.load(env))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining(key);
    }

    @ParameterizedTest
    @ValueSource(strings = {"DB_URL", "DB_USER", "DB_PASSWORD"})
    @DisplayName("Should throw when a required database value is blank")
    void load_blankDbValue_throwsIllegalStateException(String key) {
        Map<String, String> env = validEnv();
        env.put(key, "   ");

        assertThatThrownBy(() -> ServerConfigFactory.load(env))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining(key);
    }
}