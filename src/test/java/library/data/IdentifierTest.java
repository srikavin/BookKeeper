package library.data;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class IdentifierTest {
    @Test
    void illegalStringTest(){
        assertThrows(IllegalArgumentException.class, () -> new Identifier("12345678901234560"));
    }
}