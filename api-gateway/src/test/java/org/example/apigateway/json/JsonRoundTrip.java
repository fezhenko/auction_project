package org.example.apigateway.json;

import java.io.IOException;

import org.springframework.boot.test.json.JacksonTester;

import static org.assertj.core.api.Assertions.assertThat;

/** Round-trip assertion for the JSON shape tests in this package. */
final class JsonRoundTrip {

    private JsonRoundTrip() {}

    /**
     * Checks both directions with the application's Jackson mapper. The value is written as exactly {@code json}: the
     * same field names, values and nulls, with no extra fields, in any order. {@code json} is read back into an equal
     * value.
     */
    static <T> void assertRoundTrip(JacksonTester<T> tester, T value, String json) throws IOException {
        assertThat(tester.write(value)).isStrictlyEqualToJson(json);
        assertThat(tester.parseObject(json)).isEqualTo(value);
    }
}
