// utils/DataManager.java
package utils;

import models.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DataManager {
    private static DataManager instance;
    private List<Room> rooms;
    private List<Guest> guests;
    private List<Booking> bookings;

    private DataManager() {
        initializeData();
    }

    public static DataManager getInstance() {
        if (instance == null) {
            instance = new DataManager();
        }
        return instance;
    }

    private void initializeData() {
        // Initialize Rooms
        rooms = new ArrayList<>();

        // Floor 1 - Standard Rooms
        rooms.add(createRoom(1, "101", "Standard", 1500.00, "Occupied", 1, 2, "Queen Bed"));
        rooms.add(createRoom(2, "102", "Standard", 1500.00, "Available", 1, 2, "Queen Bed"));
        rooms.add(createRoom(3, "103", "Standard", 1500.00, "Available", 1, 2, "Queen Bed"));
        rooms.add(createRoom(4, "104", "Standard", 1500.00, "Available", 1, 2, "Queen Bed"));
        rooms.add(createRoom(5, "105", "Standard", 1500.00, "Maintenance", 1, 2, "Queen Bed"));

        // Floor 2 - Deluxe Rooms
        rooms.add(createRoom(6, "201", "Deluxe", 2500.00, "Available", 2, 3, "King Bed"));
        rooms.add(createRoom(7, "202", "Deluxe", 2500.00, "Available", 2, 3, "King Bed"));
        rooms.add(createRoom(8, "203", "Deluxe", 2500.00, "Reserved", 2, 3, "King Bed"));
        rooms.add(createRoom(9, "204", "Deluxe", 2500.00, "Available", 2, 3, "King Bed"));

        // Floor 3 - Suite Rooms
        rooms.add(createRoom(10, "301", "Suite", 4500.00, "Available", 3, 4, "King Bed"));
        rooms.add(createRoom(11, "302", "Suite", 4500.00, "Available", 3, 4, "King Bed"));
        rooms.add(createRoom(12, "303", "Suite", 4500.00, "Available", 3, 4, "King Bed"));

        // Floor 4 - Presidential Suite
        rooms.add(createRoom(13, "401", "Presidential", 10000.00, "Available", 4, 6, "Emperor Bed"));
        rooms.add(createRoom(14, "402", "Presidential", 10000.00, "Available", 4, 6, "Emperor Bed"));

        // Initialize Guests
        guests = new ArrayList<>();
        guests.add(createGuest(1, "John", "Doe", "john.doe@example.com", "9876543210", "New York", "American", true));
        guests.add(createGuest(2, "Jane", "Smith", "jane.smith@example.com", "9876543211", "Los Angeles", "American",
                true));
        guests.add(createGuest(3, "Raj", "Patel", "raj.patel@example.com", "9876543212", "Mumbai", "Indian", true));
        guests.add(
                createGuest(4, "Maria", "Garcia", "maria.garcia@example.com", "9876543213", "Madrid", "Spanish", true));
        guests.add(createGuest(5, "Chen", "Wei", "chen.wei@example.com", "9876543214", "Shanghai", "Chinese", true));
        guests.add(createGuest(6, "Ritesh", "Raj", "rit@gmail.com", "8709624574", "India", "Indian", true));

        // Initialize Bookings
        bookings = new ArrayList<>();
        LocalDate today = LocalDate.now();

        // Active booking - Room 101 (Checked-in)
        Booking b1 = createBooking(1, "BK001", 1, 1, today, today.plusDays(3), 3, 2, "Checked-in", "Paid", 4500.00,
                4500.00);
        bookings.add(b1);

        // Upcoming booking - Room 203 (Confirmed)
        Booking b2 = createBooking(2, "BK002", 2, 8, today.plusDays(1), today.plusDays(4), 3, 2, "Confirmed", "Pending",
                7500.00, 0);
        bookings.add(b2);

        // Pending booking - Room 302 (Pending)
        Booking b3 = createBooking(3, "BK003", 3, 11, today.plusDays(2), today.plusDays(5), 3, 2, "Pending", "Pending",
                13500.00, 0);
        bookings.add(b3);

        // Completed booking - Room 202
        Booking b4 = createBooking(4, "BK004", 2, 7, today.minusDays(5), today.minusDays(2), 3, 2, "Checked-out",
                "Paid", 7500.00, 7500.00);
        bookings.add(b4);

        // Cancelled booking - Room 204
        Booking b5 = createBooking(5, "BK005", 4, 9, today.minusDays(3), today, 3, 2, "Cancelled", "Refunded", 7500.00,
                0);
        bookings.add(b5);

        // Another active booking - Room 303 (Checked-in)
        Booking b6 = createBooking(6, "BK006", 5, 12, today, today.plusDays(2), 2, 2, "Checked-in", "Partial", 9000.00,
                4500.00);
        bookings.add(b6);
    }

    private Room createRoom(int id, String number, String type, double price, String status, int floor, int capacity,
            String bedType) {
        Room room = new Room();
        room.setRoomId(id);
        room.setRoomNumber(number);
        room.setRoomType(type);
        room.setPricePerNight(price);
        room.setStatus(status);
        room.setFloor(floor);
        room.setCapacity(capacity);
        room.setBedType(bedType);
        room.setHasWifi(true);
        room.setHasTV(true);
        room.setHasAC(true);
        room.setHasAttachedBathroom(true);
        return room;
    }

    private Guest createGuest(int id, String firstName, String lastName, String email, String phone, String address,
            String nationality, boolean isActive) {
        Guest guest = new Guest();
        guest.setGuestId(id);
        guest.setFirstName(firstName);
        guest.setLastName(lastName);
        guest.setEmail(email);
        guest.setPhone(phone);
        guest.setAddress(address);
        guest.setNationality(nationality);
        return guest;
    }

    private Booking createBooking(int id, String number, int guestId, int roomId, LocalDate checkIn, LocalDate checkOut,
            int nights, int guests, String status, String paymentStatus, double total, double paid) {
        Booking booking = new Booking();
        booking.setBookingId(id);
        booking.setBookingNumber(number);
        booking.setGuestId(guestId);
        booking.setRoomId(roomId);
        booking.setCheckInDate(checkIn);
        booking.setCheckOutDate(checkOut);
        booking.setNumberOfNights(nights);
        booking.setNumberOfGuests(guests);
        booking.setStatus(status);
        booking.setPaymentStatus(paymentStatus);
        booking.setTotalAmount(total);
        booking.setPaidAmount(paid);
        booking.setDueAmount(total - paid);

        // Set references
        for (Room room : rooms) {
            if (room.getRoomId() == roomId) {
                booking.setRoom(room);
                break;
            }
        }
        for (Guest g : this.guests) {
            if (g.getGuestId() == guestId) {
                booking.setGuest(g);
                break;
            }
        }

        return booking;
    }

    // Getters
    public List<Room> getAllRooms() {
        return new ArrayList<>(rooms);
    }

    public List<Guest> getAllGuests() {
        return new ArrayList<>(guests);
    }

    public List<Booking> getAllBookings() {
        return new ArrayList<>(bookings);
    }

    public Room getRoomById(int id) {
        return rooms.stream().filter(r -> r.getRoomId() == id).findFirst().orElse(null);
    }

    public Room getRoomByNumber(String number) {
        return rooms.stream().filter(r -> r.getRoomNumber().equals(number)).findFirst().orElse(null);
    }

    public Guest getGuestById(int id) {
        return guests.stream().filter(g -> g.getGuestId() == id).findFirst().orElse(null);
    }

    public Booking getBookingById(int id) {
        return bookings.stream().filter(b -> b.getBookingId() == id).findFirst().orElse(null);
    }

    public Booking getBookingByNumber(String number) {
        return bookings.stream().filter(b -> b.getBookingNumber().equals(number)).findFirst().orElse(null);
    }

    public List<Booking> getBookingsByGuestId(int guestId) {
        return bookings.stream().filter(b -> b.getGuestId() == guestId).collect(Collectors.toList());
    }

    public List<Booking> getBookingsByStatus(String status) {
        return bookings.stream().filter(b -> b.getStatus().equals(status)).collect(Collectors.toList());
    }

    public List<Booking> getTodayCheckIns() {
        LocalDate today = LocalDate.now();
        return bookings.stream()
                .filter(b -> b.getCheckInDate().equals(today) &&
                        (b.getStatus().equals("Confirmed") || b.getStatus().equals("Pending")))
                .collect(Collectors.toList());
    }

    public List<Booking> getTodayCheckOuts() {
        LocalDate today = LocalDate.now();
        return bookings.stream()
                .filter(b -> b.getCheckOutDate().equals(today) && b.getStatus().equals("Checked-in"))
                .collect(Collectors.toList());
    }

    public void updateRoom(Room room) {
        for (int i = 0; i < rooms.size(); i++) {
            if (rooms.get(i).getRoomId() == room.getRoomId()) {
                rooms.set(i, room);
                break;
            }
        }
    }

    public void updateRoomStatus(int roomId, String status) {
        Room room = getRoomById(roomId);
        if (room != null) {
            room.setStatus(status);
            updateRoom(room);
        }
    }

    public void updateBooking(Booking booking) {
        for (int i = 0; i < bookings.size(); i++) {
            if (bookings.get(i).getBookingId() == booking.getBookingId()) {
                bookings.set(i, booking);
                break;
            }
        }
    }

    public void updateBookingStatus(int bookingId, String status) {
        Booking booking = getBookingById(bookingId);
        if (booking != null) {
            booking.setStatus(status);
            // Update room status based on booking status
            if (status.equals("Checked-in")) {
                updateRoomStatus(booking.getRoomId(), "Occupied");
            } else if (status.equals("Checked-out") || status.equals("Cancelled")) {
                updateRoomStatus(booking.getRoomId(), "Available");
            } else if (status.equals("Confirmed")) {
                updateRoomStatus(booking.getRoomId(), "Reserved");
            }
            updateBooking(booking);
        }
    }

    public void addPayment(int bookingId, double amount) {
        Booking booking = getBookingById(bookingId);
        if (booking != null) {
            booking.addPayment(amount);
            updateBooking(booking);
        }
    }

    public void addBooking(Booking booking) {
        booking.setBookingId(bookings.size() + 1);
        bookings.add(booking);
    }

    public void addGuest(Guest guest) {
        guest.setGuestId(guests.size() + 1);
        guests.add(guest);
    }

    public void updateGuest(Guest guest) {
        for (int i = 0; i < guests.size(); i++) {
            if (guests.get(i).getGuestId() == guest.getGuestId()) {
                guests.set(i, guest);
                break;
            }
        }
    }

    // Statistics
    public int[] getRoomStatistics() {
        int[] stats = new int[5];
        stats[0] = rooms.size(); // total
        stats[1] = (int) rooms.stream().filter(r -> "Available".equals(r.getStatus())).count(); // available
        stats[2] = (int) rooms.stream().filter(r -> "Occupied".equals(r.getStatus())).count(); // occupied
        stats[3] = (int) rooms.stream().filter(r -> "Maintenance".equals(r.getStatus())).count(); // maintenance
        stats[4] = (int) rooms.stream().filter(r -> "Reserved".equals(r.getStatus())).count(); // reserved
        return stats;
    }

    public double[] getRevenueStatistics() {
        double[] revenue = new double[4];
        revenue[0] = bookings.stream().filter(b -> "Checked-out".equals(b.getStatus()))
                .mapToDouble(Booking::getTotalAmount).sum();
        revenue[1] = bookings.stream().filter(b -> b.getCheckInDate().equals(LocalDate.now()))
                .mapToDouble(Booking::getTotalAmount).sum();
        revenue[2] = bookings.stream().filter(b -> b.getCheckInDate().getMonth() == LocalDate.now().getMonth())
                .mapToDouble(Booking::getTotalAmount).sum();
        revenue[3] = bookings.stream().filter(b -> b.getCheckInDate().getYear() == LocalDate.now().getYear())
                .mapToDouble(Booking::getTotalAmount).sum();
        return revenue;
    }

    public int[] getBookingStatistics() {
        int[] stats = new int[6];
        stats[0] = bookings.size(); // total
        stats[1] = (int) bookings.stream().filter(b -> "Pending".equals(b.getStatus())).count();
        stats[2] = (int) bookings.stream().filter(b -> "Confirmed".equals(b.getStatus())).count();
        stats[3] = (int) bookings.stream().filter(b -> "Checked-in".equals(b.getStatus())).count();
        stats[4] = (int) bookings.stream().filter(b -> "Checked-out".equals(b.getStatus())).count();
        stats[5] = (int) bookings.stream().filter(b -> "Cancelled".equals(b.getStatus())).count();
        return stats;
    }

    public int[] getGuestStatistics() {
        int[] stats = new int[3];
        stats[0] = guests.size(); // total
        stats[1] = (int) guests.stream().filter(g -> true).count(); // active
        stats[2] = 0; // vip
        return stats;
    }
}