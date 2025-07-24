package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.shareit.booking.dto.BookingState;

import java.util.Map;

@Controller
@RequestMapping(path = "/bookings")
@Validated
@RequiredArgsConstructor
public class BookingController {
	public static final String USER_ID_HEADER = "X-Sharer-User-Id";

	private final BookingClient bookingClient;

	@GetMapping
	public ResponseEntity<Object> getBookings(@RequestHeader(USER_ID_HEADER) long userId,
											  @RequestParam(name = "state", defaultValue = "all") String stateParam,
											  @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
											  @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
		BookingState state;
		try {
			state = BookingState.from(stateParam)
					.orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Unknown state: " + stateParam));
		}
		return bookingClient.getBookings(userId, state, from, size);
	}

	@PostMapping
	public ResponseEntity<Object> bookItem(@RequestHeader(USER_ID_HEADER) long userId,
										   @RequestBody @Valid BookingDto bookingDto) {
		return bookingClient.bookItem(userId, bookingDto);
	}

	@GetMapping("/{bookingId}")
	public ResponseEntity<Object> getBooking(@RequestHeader(USER_ID_HEADER) long userId,
											 @PathVariable Long bookingId) {
		return bookingClient.getBooking(userId, bookingId);
	}

	@PatchMapping("/{bookingId}")
	public ResponseEntity<Object> updateBookingStatus(@RequestHeader(USER_ID_HEADER) long userId,
													  @PathVariable Long bookingId,
													  @RequestParam Boolean approved) {
		return bookingClient.updateBookingStatus(userId, bookingId, approved);
	}

	@GetMapping("/owner")
	public ResponseEntity<Object> getOwnerBookings(@RequestHeader(USER_ID_HEADER) long userId,
												   @RequestParam(name = "state", defaultValue = "all") String stateParam,
												   @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
												   @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
		BookingState state;
		try {
			state = BookingState.from(stateParam)
					.orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Unknown state: " + stateParam));
		}
		return bookingClient.getOwnerBookings(userId, state, from, size);
	}
}
