package easv.easv_tickets_bar.bll;

import easv.easv_tickets_bar.CustomExceptions.MyException;
import easv.easv_tickets_bar.be.Event;
import easv.easv_tickets_bar.be.Role;
import org.apache.commons.logging.Log;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class LogicTest {
    private static Logic logic;

    @BeforeAll
    static void setUpClass() {
        logic = new Logic();
    }

    @Test
    @DisplayName("test for equality of username and password")
    void createUser() {
        Throwable exception = assertThrows(MyException.class, () -> logic.createUser("user3,1415", "user3,1415", Role.EVENT_COORDINATOR));
        assertEquals("Password can't be equal to username", exception.getMessage());
    }

    @Test
    @DisplayName("isInvalidString method test")
    void isInvalidString() {
        assertTrue(logic.isInvalidString(""));
        assertTrue(logic.isInvalidString(null));
        assertFalse(logic.isInvalidString("string"));
    }

    @Test
    @DisplayName("validateEventData method test")
    void validateEventData() {
        assertThrows(MyException.class, () -> logic.validateEventData("EventName", "17:fr", "17:hr", null, null, "neverland 2", "museum", "String guidance", "additional info", "100"));
    }

    @Test
    @DisplayName("createTicket method test")
    void createTicket() {
        Throwable exception = assertThrows(MyException.class, () -> logic.createTicket(5, 100, "", "10", "desc"));
        assertEquals("Make sure all the fields are filled out", exception.getMessage());
    }

    @Test
    @DisplayName("isValidEmail method test")
    void isValidEmail() {
        assertTrue(logic.isValidEmail("right_email14@gmail.com"));
        assertFalse(logic.isValidEmail("wrong_email.com"));
    }






//     if (username.isEmpty()) {
//        throw new MyException("Please fill all fields");
//    }
//        if (username.contains(" ")) {
//        throw new MyException("Username shall not contain spaces");
//    }
//        if (username.length() < 8) {
//        throw new MyException("Username must have at least 8 characters");
//    }
//        if (username.equals(password)) {
//        throw new MyException("Password can't be equal to username");
//    }
//        return true;
}