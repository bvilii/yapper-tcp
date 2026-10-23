package io.github.bvilii.yapper.server.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class ServerConfigTest {


    @DisplayName("Should throw when the port is not between 1 and 65535")
    @ParameterizedTest
    @ValueSource(ints = {0, -1, 65536, 100_000})
    void rejectsPortsOutOfRange(int port) {
        assertThatThrownBy(() -> new ServerConfig(port))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 9999, 65535})
    @DisplayName("Should accept ports that are in between 1 and 65535")
    void acceptValidPortNumbers(int port){
        assertThat(new ServerConfig(port).port()).isEqualTo(port);
    }

}
