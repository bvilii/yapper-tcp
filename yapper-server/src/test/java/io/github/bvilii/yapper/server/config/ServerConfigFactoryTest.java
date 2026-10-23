package io.github.bvilii.yapper.server.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ServerConfigFactoryTest {

    @Test
    @DisplayName("Should fall back to the default port when port key is missing")
    void load_missingPortKey_usesDefault() {
        Map<String, String> missingPortKey = Map.of();
        ServerConfig config = ServerConfigFactory.load(missingPortKey);

        assertThat(config.port()).isEqualTo(ServerConfigFactory.DEFAULT_PORT);
    }

    @Test
    @DisplayName("Should use the port when it is valid")
    void load_validPort_usesPortFromEnv() {
        Map<String, String> validPortMap = Map.of("SERVER_PORT", "8000");
        ServerConfig config = ServerConfigFactory.load(validPortMap);

        assertThat(config.port()).isEqualTo(8000);
    }

    @Test
    @DisplayName("Should throw when port is an invalid configuration")
    void load_nonNumericPort_throwsIllegalArgumentException() {
        Map<String, String> invalidPortMap = Map.of("SERVER_PORT", "abc");

        assertThatThrownBy(() -> ServerConfigFactory.load(invalidPortMap))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "    "})
    @DisplayName("Should fall back to the default port when port value is blank")
    void load_blankPortValue_usesDefault(String value) {
        Map<String, String> blankPortMap = Map.of("SERVER_PORT", value);
        ServerConfig config = ServerConfigFactory.load(blankPortMap);

        assertThat(config.port()).isEqualTo(ServerConfigFactory.DEFAULT_PORT);
    }


}
