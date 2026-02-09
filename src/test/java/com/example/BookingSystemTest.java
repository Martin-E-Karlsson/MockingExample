package com.example;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import org.junit.jupiter.params.provider.NullSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingSystemTest {
    @Mock
    TimeProvider timeProvider;
    @Mock
    RoomRepository roomRepository;
    @Mock
    NotificationService notificationService;
    @Mock
    Room room;

    @InjectMocks
    BookingSystem bookingSystem;

    // Static variables uses across the tests
    private static final LocalDateTime NOW = LocalDateTime.now();
    private static final LocalDateTime FUTURE_START = NOW.plusDays(3);
    private static final LocalDateTime FUTURE_END = NOW.plusDays(6);
    private static final LocalDateTime BEFORE_NOW = NOW.minusDays(1);
    private static final LocalDateTime BEFORE_FUTURE_START = FUTURE_START.minusDays(1);
    private static final String ROOM_ID = "room-123";

    /**
     * Translates static variables used in parameterized test from strings to their LocalDateTime counterparts using a
     * switch statement.
     * @param timeString
     * @return A LocalDateTime object if the string matches otherwise null
     */
    private LocalDateTime parseTime(String timeString) {
        if (timeString == null || "null".equals(timeString)) {
            return null;
        }
        return switch (timeString) {
            case "FUTURE_START" -> FUTURE_START;
            case "FUTURE_END" -> FUTURE_END;
            case "PAST_TIME" -> BEFORE_NOW;
            case "BEFORE_START" -> BEFORE_FUTURE_START;
            default -> null;
        };
    }

    /**
     * The test attempts to call bookRoom with LocalDateValues expected to create a successful booking.
     * After the booking methods from the service classes are called upon to verify it was successful.
     * @throws NotificationException
     */
    @Test
    void shouldBookRoomSuccessfully() throws NotificationException {
        when(timeProvider.getCurrentTime()).thenReturn(NOW);
        when(roomRepository.findById(ROOM_ID)).thenReturn(Optional.of(room));
        when(room.isAvailable(FUTURE_START, FUTURE_END)).thenReturn(true);

        boolean result = bookingSystem.bookRoom(ROOM_ID, FUTURE_START, FUTURE_END);

        assertThat(result).isTrue();
        verify(room).addBooking(any(Booking.class));
        verify(roomRepository).save(room);
        verify(notificationService).sendBookingConfirmation(any(Booking.class));
    }

    /**
     * Mockito is instructed to show the room as unavailable upon booking creation.
     * Then verifies that no booking has been created.
     * @throws NotificationException
     */
    @Test
    void shouldReturnFalseWhenRoomIsNotAvailable() throws NotificationException {
        when(timeProvider.getCurrentTime()).thenReturn(NOW);
        when(roomRepository.findById(ROOM_ID)).thenReturn(Optional.of(room));
        when(room.isAvailable(FUTURE_START, FUTURE_END)).thenReturn(false);

        boolean result = bookingSystem.bookRoom(ROOM_ID, FUTURE_START, FUTURE_END);

        assertThat(result).isFalse();
        verify(room, never()).addBooking(any(Booking.class));
        verify(roomRepository, never()).save(room);
        verify(notificationService, never()).sendBookingConfirmation(any(Booking.class));
    }

    /**
     * Runs the bookRoom method with null as the roomId then asserts that an exception is thrown.
     * @param roomId
     */
    @ParameterizedTest
    @NullSource
    void shouldThrowExceptionWhenRoomIdIsInvalid(String roomId) {
        assertThatThrownBy(() -> bookingSystem.bookRoom(roomId, FUTURE_START, FUTURE_END))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Bokning kräver giltiga start- och sluttider samt rum-id");
    }

    /**
     * Parameterized test which checks if the expected error messages are returned when invalid time frames are given.
     * @param startTimeStr
     * @param endTimeStr
     * @param expectedMessage
     */
    @ParameterizedTest
    @CsvSource({
        "PAST_TIME, FUTURE_END, Kan inte boka tid i dåtid",
        "FUTURE_START, BEFORE_START, Sluttid måste vara efter starttid"
    })
    void shouldThrowExceptionForInvalidTimes(String startTimeStr, String endTimeStr, String expectedMessage) {
        when(timeProvider.getCurrentTime()).thenReturn(NOW);

        LocalDateTime startTime = parseTime(startTimeStr);
        LocalDateTime endTime = parseTime(endTimeStr);

        assertThatThrownBy(() -> bookingSystem.bookRoom(ROOM_ID, startTime, endTime))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(expectedMessage);
    }

    @Test
    void shouldThrowExceptionWhenRoomDoesNotExist() {
        when(timeProvider.getCurrentTime()).thenReturn(NOW);
        when(roomRepository.findById(ROOM_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookingSystem.bookRoom(ROOM_ID, FUTURE_START, FUTURE_END))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Rummet existerar inte");
    }

    @Test
    void shouldThrowExceptionWhenStartTimeIsNull() {
        assertThatThrownBy(() -> bookingSystem.bookRoom(ROOM_ID, null, FUTURE_END))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Bokning kräver giltiga start- och sluttider samt rum-id");
    }

    @Test
    void shouldThrowExceptionWhenEndTimeIsNull() {
        assertThatThrownBy(() -> bookingSystem.bookRoom(ROOM_ID, FUTURE_START, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Bokning kräver giltiga start- och sluttider samt rum-id");
    }

    @Test
    void shouldContinueBookingWhenNotificationFails() throws NotificationException {
        when(timeProvider.getCurrentTime()).thenReturn(NOW);
        when(roomRepository.findById(ROOM_ID)).thenReturn(Optional.of(room));
        when(room.isAvailable(FUTURE_START, FUTURE_END)).thenReturn(true);
        doThrow(new NotificationException("Notification failed"))
                .when(notificationService).sendBookingConfirmation(any(Booking.class));

        boolean result = bookingSystem.bookRoom(ROOM_ID, FUTURE_START, FUTURE_END);

        assertThat(result).isTrue();
        verify(room).addBooking(any(Booking.class));
        verify(roomRepository).save(room);
    }

    @Test
    void shouldGetAvailableRooms() {
        Room availableRoom = mock(Room.class);
        Room bookedRoom = mock(Room.class);

        when(roomRepository.findAll()).thenReturn(List.of(availableRoom, bookedRoom));
        when(availableRoom.isAvailable(FUTURE_START, FUTURE_END)).thenReturn(true);
        when(bookedRoom.isAvailable(FUTURE_START, FUTURE_END)).thenReturn(false);

        List<Room> result = bookingSystem.getAvailableRooms(FUTURE_START, FUTURE_END);

        assertThat(result).hasSize(1).containsExactly(availableRoom);
    }

    @Test
    void shouldThrowExceptionWhenStartTimeIsNullInGetAvailableRooms() {
        assertThatThrownBy(() -> bookingSystem.getAvailableRooms(null, FUTURE_END))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Måste ange både start- och sluttid");
    }

    @Test
    void shouldThrowExceptionWhenEndTimeIsNullInGetAvailableRooms() {
        assertThatThrownBy(() -> bookingSystem.getAvailableRooms(FUTURE_START, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Måste ange både start- och sluttid");
    }

    @ParameterizedTest
    @CsvSource({
        "FUTURE_START, BEFORE_START, Sluttid måste vara efter starttid"
    })
    void shouldThrowExceptionForInvalidTimesInGetAvailableRooms(String startTimeStr, String endTimeStr, String expectedMessage) {
        LocalDateTime startTime = parseTime(startTimeStr);
        LocalDateTime endTime = parseTime(endTimeStr);

        assertThatThrownBy(() -> bookingSystem.getAvailableRooms(startTime, endTime))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(expectedMessage);
    }

    @Test
    void shouldCancelBookingSuccessfully() throws NotificationException {
        Booking booking = new Booking("booking-123", ROOM_ID, FUTURE_START, FUTURE_END);
        when(timeProvider.getCurrentTime()).thenReturn(NOW);
        when(roomRepository.findAll()).thenReturn(List.of(room));
        when(room.hasBooking("booking-123")).thenReturn(true);
        when(room.getBooking("booking-123")).thenReturn(booking);

        boolean result = bookingSystem.cancelBooking("booking-123");

        assertThat(result).isTrue();
        verify(room).removeBooking("booking-123");
        verify(roomRepository).save(room);
        verify(notificationService).sendCancellationConfirmation(booking);
    }

    @Test
    void shouldReturnFalseWhenBookingDoesNotExist() {
        when(roomRepository.findAll()).thenReturn(List.of(room));
        when(room.hasBooking("booking-123")).thenReturn(false);

        boolean result = bookingSystem.cancelBooking("booking-123");

        assertThat(result).isFalse();
        verify(room, never()).removeBooking(any());
        verify(roomRepository, never()).save(room);
    }

    @Test
    void shouldThrowExceptionWhenCancellingPastBooking() {
        Booking pastBooking = new Booking("booking-123", ROOM_ID, NOW.minusHours(2), NOW.minusHours(1));
        when(timeProvider.getCurrentTime()).thenReturn(NOW);
        when(roomRepository.findAll()).thenReturn(List.of(room));
        when(room.hasBooking("booking-123")).thenReturn(true);
        when(room.getBooking("booking-123")).thenReturn(pastBooking);

        assertThatThrownBy(() -> bookingSystem.cancelBooking("booking-123"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Kan inte avboka påbörjad eller avslutad bokning");
    }

    @ParameterizedTest
    @NullSource
    void shouldThrowExceptionWhenBookingIdIsInvalid(String bookingId) {
        assertThatThrownBy(() -> bookingSystem.cancelBooking(bookingId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Boknings-id kan inte vara null");
    }

    @Test
    void shouldContinueCancellationWhenNotificationFails() throws NotificationException {
        Booking booking = new Booking("booking-123", ROOM_ID, FUTURE_START, FUTURE_END);
        when(timeProvider.getCurrentTime()).thenReturn(NOW);
        when(roomRepository.findAll()).thenReturn(List.of(room));
        when(room.hasBooking("booking-123")).thenReturn(true);
        when(room.getBooking("booking-123")).thenReturn(booking);
        doThrow(new NotificationException("Notification failed"))
                .when(notificationService).sendCancellationConfirmation(booking);

        boolean result = bookingSystem.cancelBooking("booking-123");

        assertThat(result).isTrue();
        verify(room).removeBooking("booking-123");
        verify(roomRepository).save(room);
    }
}