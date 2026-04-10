// ui/BookingPanel.java - Complete fixed version
package ui;

import models.Booking;
import models.Room;
import models.Guest;
import utils.CustomTheme;
import utils.DataManager;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class BookingPanel extends JPanel {

    private JTable bookingTable;
    private DefaultTableModel tableModel;
    private DataManager dataManager;
    private CustomTheme.ModernTextField searchField;
    private JComboBox<String> statusFilterCombo;
    private JComboBox<String> dateFilterCombo;
    private JLabel statusLabel;
    private JLabel statsLabel;
    private JPanel detailPanel;
    private List<Booking> currentBookings;
    private Runnable onDataChangedCallback;

    public BookingPanel() {
        dataManager = DataManager.getInstance();
        initComponents();
        loadBookings();
    }
    
    public void setOnDataChangedCallback(Runnable callback) {
        this.onDataChangedCallback = callback;
    }
    
    private void notifyDataChanged() {
        if (onDataChangedCallback != null) {
            onDataChangedCallback.run();
        }
    }

    private void initComponents() {
        setLayout(new BorderLayout(15, 15));
        setBackground(CustomTheme.BACKGROUND_COLOR);
        
        add(createHeaderPanel(), BorderLayout.NORTH);
        
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setLeftComponent(createTablePanel());
        splitPane.setRightComponent(createDetailPanel());
        splitPane.setDividerLocation(900);
        splitPane.setDividerSize(5);
        splitPane.setBorder(null);
        
        add(splitPane, BorderLayout.CENTER);
        add(createBottomPanel(), BorderLayout.SOUTH);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(CustomTheme.PANEL_HEADER_COLOR);
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 2, 0, CustomTheme.PRIMARY_COLOR),
            BorderFactory.createEmptyBorder(20, 20, 15, 20)
        ));
        
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setOpaque(false);
        
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        titlePanel.setOpaque(false);
        
        JLabel titleIcon = new JLabel("📅");
        titleIcon.setFont(CustomTheme.getEmojiFont().deriveFont(32f));
        titlePanel.add(titleIcon);
        titlePanel.add(Box.createRigidArea(new Dimension(10, 0)));
        
        JLabel titleLabel = CustomTheme.createHeaderLabel("Booking Management");
        titlePanel.add(titleLabel);
        
        leftPanel.add(titlePanel);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        
        JLabel subtitleLabel = CustomTheme.createLabel("Manage reservations, check-ins, check-outs and track bookings");
        subtitleLabel.setFont(CustomTheme.SMALL_FONT);
        subtitleLabel.setForeground(CustomTheme.GRAY_COLOR);
        subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        leftPanel.add(subtitleLabel);
        
        headerPanel.add(leftPanel, BorderLayout.WEST);
        
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightPanel.setOpaque(false);
        
        dateFilterCombo = new JComboBox<>(new String[]{"All Bookings", "Today", "Tomorrow", "This Week", "This Month"});
        dateFilterCombo.setFont(CustomTheme.NORMAL_FONT);
        dateFilterCombo.setBackground(Color.WHITE);
        dateFilterCombo.setForeground(CustomTheme.TEXT_COLOR);
        dateFilterCombo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        dateFilterCombo.setPreferredSize(new Dimension(130, 38));
        dateFilterCombo.addActionListener(e -> filterByDate());
        rightPanel.add(dateFilterCombo);
        
        searchField = new CustomTheme.ModernTextField(15);
        searchField.setPlaceholder("Search bookings...");
        searchField.setPreferredSize(new Dimension(200, 38));
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                searchBookings();
            }
        });
        rightPanel.add(searchField);
        
        String[] statuses = {"All Statuses", "Pending", "Confirmed", "Checked-in", "Checked-out", "Cancelled"};
        statusFilterCombo = new JComboBox<>(statuses);
        statusFilterCombo.setFont(CustomTheme.NORMAL_FONT);
        statusFilterCombo.setBackground(Color.WHITE);
        statusFilterCombo.setForeground(CustomTheme.TEXT_COLOR);
        statusFilterCombo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        statusFilterCombo.setPreferredSize(new Dimension(130, 38));
        statusFilterCombo.addActionListener(e -> filterByStatus());
        rightPanel.add(statusFilterCombo);
        
        JButton newBookingButton = new JButton("➕ New Booking");
        newBookingButton.setFont(CustomTheme.BUTTON_FONT);
        newBookingButton.setBackground(CustomTheme.SUCCESS_COLOR);
        newBookingButton.setForeground(Color.WHITE);
        newBookingButton.setFocusPainted(false);
        newBookingButton.setBorderPainted(false);
        newBookingButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        newBookingButton.setPreferredSize(new Dimension(130, 38));
        newBookingButton.addActionListener(e -> showNewBookingDialog());
        rightPanel.add(newBookingButton);
        
        JButton refreshButton = new JButton("🔄 Refresh");
        refreshButton.setFont(CustomTheme.BUTTON_FONT);
        refreshButton.setBackground(CustomTheme.PRIMARY_COLOR);
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setFocusPainted(false);
        refreshButton.setBorderPainted(false);
        refreshButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        refreshButton.setPreferredSize(new Dimension(110, 38));
        refreshButton.addActionListener(e -> {
            loadBookings();
            notifyDataChanged();
        });
        rightPanel.add(refreshButton);
        
        headerPanel.add(rightPanel, BorderLayout.EAST);
        
        return headerPanel;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(CustomTheme.BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 20, 10, 10));
        
        String[] columns = {"ID", "Booking #", "Guest", "Room", "Check-in", "Check-out", "Nights", "Total", "Status", "Payment"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        bookingTable = new JTable(tableModel);
        CustomTheme.styleTable(bookingTable);
        
        TableColumnModel colModel = bookingTable.getColumnModel();
        colModel.getColumn(0).setMaxWidth(60);
        colModel.getColumn(1).setPreferredWidth(120);
        colModel.getColumn(2).setPreferredWidth(160);
        colModel.getColumn(3).setPreferredWidth(80);
        colModel.getColumn(4).setPreferredWidth(100);
        colModel.getColumn(5).setPreferredWidth(100);
        colModel.getColumn(6).setMaxWidth(70);
        colModel.getColumn(7).setPreferredWidth(120);
        colModel.getColumn(8).setPreferredWidth(120);
        colModel.getColumn(9).setPreferredWidth(100);
        
        bookingTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                showBookingDetails();
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(bookingTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(CustomTheme.LIGHT_COLOR));
        CustomTheme.styleScrollPane(scrollPane);
        
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }

    private JPanel createDetailPanel() {
        detailPanel = new JPanel(new BorderLayout());
        detailPanel.setBackground(CustomTheme.CARD_BACKGROUND_COLOR);
        detailPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 1, 0, 0, CustomTheme.LIGHT_COLOR),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        JLabel initialLabel = CustomTheme.createHeaderLabel("Select a booking to view details");
        initialLabel.setHorizontalAlignment(SwingConstants.CENTER);
        initialLabel.setForeground(CustomTheme.GRAY_COLOR);
        detailPanel.add(initialLabel, BorderLayout.CENTER);
        
        return detailPanel;
    }

    private JPanel createBottomPanel() {
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(CustomTheme.BACKGROUND_COLOR);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));
        
        CustomTheme.ShadowPanel statsPanel = new CustomTheme.ShadowPanel(10, 2);
        statsPanel.setBackground(Color.WHITE);
        statsPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 12));
        
        statsLabel = CustomTheme.createLabel("");
        statsPanel.add(statsLabel);
        
        bottomPanel.add(statsPanel, BorderLayout.WEST);
        
        statusLabel = CustomTheme.createLabel("Ready");
        statusLabel.setFont(CustomTheme.SMALL_FONT);
        statusLabel.setForeground(CustomTheme.GRAY_COLOR);
        bottomPanel.add(statusLabel, BorderLayout.EAST);
        
        return bottomPanel;
    }

    private void loadBookings() {
        currentBookings = dataManager.getAllBookings();
        updateTableData(currentBookings);
        updateStatistics(currentBookings);
        statusLabel.setText("Loaded " + currentBookings.size() + " bookings");
    }

    private void updateTableData(List<Booking> bookings) {
        tableModel.setRowCount(0);
        for (Booking booking : bookings) {
            String guestName = booking.getGuest() != null ? 
                booking.getGuest().getFirstName() + " " + booking.getGuest().getLastName() : "N/A";
            String roomNumber = booking.getRoom() != null ? booking.getRoom().getRoomNumber() : "N/A";
            
            tableModel.addRow(new Object[]{
                booking.getBookingId(),
                booking.getBookingNumber(),
                guestName,
                roomNumber,
                booking.getCheckInDate() != null ? booking.getCheckInDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "N/A",
                booking.getCheckOutDate() != null ? booking.getCheckOutDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "N/A",
                booking.getNumberOfNights(),
                String.format("₹%,.2f", booking.getTotalAmount()),
                booking.getStatus(),
                booking.getPaymentStatus()
            });
        }
    }

    private void updateStatistics(List<Booking> bookings) {
        long total = bookings.size();
        long confirmed = bookings.stream().filter(b -> "Confirmed".equals(b.getStatus())).count();
        long checkedIn = bookings.stream().filter(b -> "Checked-in".equals(b.getStatus())).count();
        long checkedOut = bookings.stream().filter(b -> "Checked-out".equals(b.getStatus())).count();
        long cancelled = bookings.stream().filter(b -> "Cancelled".equals(b.getStatus())).count();
        double totalRevenue = bookings.stream()
            .filter(b -> "Checked-out".equals(b.getStatus()))
            .mapToDouble(Booking::getTotalAmount).sum();
        
        String stats = String.format(
            "📊 Booking Statistics: Total: %d | ✅ Confirmed: %d | 🏨 Checked-in: %d | 🚪 Checked-out: %d | ❌ Cancelled: %d | 💰 Revenue: ₹%,.2f",
            total, confirmed, checkedIn, checkedOut, cancelled, totalRevenue);
        statsLabel.setText(stats);
        statsLabel.setForeground(CustomTheme.TEXT_COLOR);
    }

    private void filterByStatus() {
        String status = (String) statusFilterCombo.getSelectedItem();
        if (status == null || status.equals("All Statuses")) {
            loadBookings();
        } else {
            List<Booking> filtered = currentBookings.stream()
                .filter(b -> b.getStatus().equals(status))
                .collect(Collectors.toList());
            updateTableData(filtered);
            statusLabel.setText("Showing " + filtered.size() + " " + status + " bookings");
        }
    }

    private void filterByDate() {
        String filter = (String) dateFilterCombo.getSelectedItem();
        LocalDate today = LocalDate.now();
        
        List<Booking> filtered = new ArrayList<>();
        for (Booking booking : currentBookings) {
            LocalDate checkIn = booking.getCheckInDate();
            if (checkIn == null) continue;
            
            switch(filter) {
                case "Today":
                    if (checkIn.equals(today)) filtered.add(booking);
                    break;
                case "Tomorrow":
                    if (checkIn.equals(today.plusDays(1))) filtered.add(booking);
                    break;
                case "This Week":
                    if (!checkIn.isBefore(today) && !checkIn.isAfter(today.plusDays(7))) filtered.add(booking);
                    break;
                case "This Month":
                    if (checkIn.getMonth() == today.getMonth() && checkIn.getYear() == today.getYear()) filtered.add(booking);
                    break;
                default:
                    filtered = currentBookings;
                    break;
            }
        }
        updateTableData(filtered);
        statusLabel.setText("Showing " + filtered.size() + " bookings for " + filter);
    }

    private void searchBookings() {
        String searchTerm = searchField.getText().trim().toLowerCase();
        if (searchTerm.isEmpty()) {
            loadBookings();
        } else {
            List<Booking> filtered = currentBookings.stream()
                .filter(b -> {
                    String guestName = b.getGuest() != null ? 
                        (b.getGuest().getFirstName() + " " + b.getGuest().getLastName()).toLowerCase() : "";
                    return b.getBookingNumber().toLowerCase().contains(searchTerm) || guestName.contains(searchTerm);
                })
                .collect(Collectors.toList());
            updateTableData(filtered);
            statusLabel.setText("Found " + filtered.size() + " bookings matching '" + searchTerm + "'");
        }
    }

    private void showBookingDetails() {
        int selectedRow = bookingTable.getSelectedRow();
        if (selectedRow == -1) return;
        
        int bookingId = (int) tableModel.getValueAt(selectedRow, 0);
        Booking booking = dataManager.getBookingById(bookingId);
        
        if (booking != null) {
            updateDetailPanel(booking);
        }
    }

    private void updateDetailPanel(Booking booking) {
        detailPanel.removeAll();
        
        JPanel detailsCard = new JPanel();
        detailsCard.setLayout(new BoxLayout(detailsCard, BoxLayout.Y_AXIS));
        detailsCard.setBackground(Color.WHITE);
        detailsCard.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel titleLabel = new JLabel("Booking #" + booking.getBookingNumber());
        titleLabel.setFont(CustomTheme.HEADER_FONT);
        titleLabel.setForeground(CustomTheme.PRIMARY_COLOR);
        detailsCard.add(titleLabel);
        detailsCard.add(Box.createRigidArea(new Dimension(0, 15)));
        
        JPanel guestPanel = createInfoPanel("👤 Guest Information", new String[][]{
            {"Name", booking.getGuest() != null ? booking.getGuest().getFirstName() + " " + booking.getGuest().getLastName() : "N/A"},
            {"Phone", booking.getGuest() != null ? booking.getGuest().getPhone() : "N/A"},
            {"Email", booking.getGuest() != null ? booking.getGuest().getEmail() : "N/A"}
        });
        detailsCard.add(guestPanel);
        detailsCard.add(Box.createRigidArea(new Dimension(0, 15)));
        
        JPanel roomPanel = createInfoPanel("🏠 Room Information", new String[][]{
            {"Room Number", booking.getRoom() != null ? booking.getRoom().getRoomNumber() : "N/A"},
            {"Room Type", booking.getRoom() != null ? booking.getRoom().getRoomType() : "N/A"},
            {"Price/Night", String.format("₹%,.2f", booking.getRoomPricePerNight())}
        });
        detailsCard.add(roomPanel);
        detailsCard.add(Box.createRigidArea(new Dimension(0, 15)));
        
        JPanel bookingPanel = createInfoPanel("📅 Booking Details", new String[][]{
            {"Check-in", booking.getCheckInDate() != null ? booking.getCheckInDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "N/A"},
            {"Check-out", booking.getCheckOutDate() != null ? booking.getCheckOutDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "N/A"},
            {"Nights", String.valueOf(booking.getNumberOfNights())},
            {"Guests", String.valueOf(booking.getNumberOfGuests())},
            {"Status", booking.getStatus()}
        });
        detailsCard.add(bookingPanel);
        detailsCard.add(Box.createRigidArea(new Dimension(0, 15)));
        
        JPanel paymentPanel = createInfoPanel("💰 Payment Details", new String[][]{
            {"Total Amount", String.format("₹%,.2f", booking.getTotalAmount())},
            {"Paid Amount", String.format("₹%,.2f", booking.getPaidAmount())},
            {"Due Amount", String.format("₹%,.2f", booking.getDueAmount())},
            {"Payment Status", booking.getPaymentStatus()}
        });
        detailsCard.add(paymentPanel);
        detailsCard.add(Box.createRigidArea(new Dimension(0, 15)));
        
        // Action Buttons Panel - Check-in, Check-out, Cancel
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        actionPanel.setBackground(Color.WHITE);
        actionPanel.setBorder(BorderFactory.createTitledBorder("Actions"));
        
        // Check-in button (only for Confirmed bookings)
        if ("Confirmed".equals(booking.getStatus())) {
            JButton checkInBtn = new JButton("🏨 Check-in");
            checkInBtn.setFont(CustomTheme.BUTTON_FONT);
            checkInBtn.setBackground(CustomTheme.SUCCESS_COLOR);
            checkInBtn.setForeground(Color.WHITE);
            checkInBtn.setFocusPainted(false);
            checkInBtn.setBorderPainted(false);
            checkInBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            checkInBtn.setPreferredSize(new Dimension(130, 40));
            checkInBtn.addActionListener(e -> {
                int confirm = JOptionPane.showConfirmDialog(this, 
                    "Confirm check-in for " + booking.getBookingNumber() + "?", 
                    "Check-in", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    booking.setStatus("Checked-in");
                    booking.setCheckInTime(java.time.LocalDateTime.now());
                    dataManager.updateBooking(booking);
                    // Update room status
                    Room room = booking.getRoom();
                    if (room != null) {
                        room.setStatus("Occupied");
                        dataManager.updateRoom(room);
                    }
                    loadBookings();
                    updateDetailPanel(booking);
                    notifyDataChanged();
                    JOptionPane.showMessageDialog(this, "Check-in successful!");
                }
            });
            actionPanel.add(checkInBtn);
        }
        
        // Check-out button (only for Checked-in bookings)
        if ("Checked-in".equals(booking.getStatus())) {
            JButton checkOutBtn = new JButton("🚪 Check-out");
            checkOutBtn.setFont(CustomTheme.BUTTON_FONT);
            checkOutBtn.setBackground(CustomTheme.WARNING_COLOR);
            checkOutBtn.setForeground(Color.WHITE);
            checkOutBtn.setFocusPainted(false);
            checkOutBtn.setBorderPainted(false);
            checkOutBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            checkOutBtn.setPreferredSize(new Dimension(130, 40));
            checkOutBtn.addActionListener(e -> {
                int confirm = JOptionPane.showConfirmDialog(this, 
                    "Total Amount: ₹" + String.format("%.2f", booking.getTotalAmount()) +
                    "\nPaid: ₹" + String.format("%.2f", booking.getPaidAmount()) +
                    "\nDue: ₹" + String.format("%.2f", booking.getDueAmount()) +
                    "\n\nConfirm check-out?", 
                    "Check-out", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    booking.setStatus("Checked-out");
                    booking.setCheckOutTime(java.time.LocalDateTime.now());
                    dataManager.updateBooking(booking);
                    // Update room status
                    Room room = booking.getRoom();
                    if (room != null) {
                        room.setStatus("Available");
                        dataManager.updateRoom(room);
                    }
                    loadBookings();
                    updateDetailPanel(booking);
                    notifyDataChanged();
                    JOptionPane.showMessageDialog(this, "Check-out successful!");
                }
            });
            actionPanel.add(checkOutBtn);
        }
        
        // Cancel button (for Pending or Confirmed bookings)
        if ("Pending".equals(booking.getStatus()) || "Confirmed".equals(booking.getStatus())) {
            JButton cancelBtn = new JButton("❌ Cancel Booking");
            cancelBtn.setFont(CustomTheme.BUTTON_FONT);
            cancelBtn.setBackground(CustomTheme.DANGER_COLOR);
            cancelBtn.setForeground(Color.WHITE);
            cancelBtn.setFocusPainted(false);
            cancelBtn.setBorderPainted(false);
            cancelBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            cancelBtn.setPreferredSize(new Dimension(150, 40));
            cancelBtn.addActionListener(e -> {
                int confirm = JOptionPane.showConfirmDialog(this, 
                    "Are you sure you want to cancel booking " + booking.getBookingNumber() + "?\nThis action cannot be undone.", 
                    "Cancel Booking", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (confirm == JOptionPane.YES_OPTION) {
                    booking.setStatus("Cancelled");
                    dataManager.updateBooking(booking);
                    // Update room status back to available
                    Room room = booking.getRoom();
                    if (room != null) {
                        room.setStatus("Available");
                        dataManager.updateRoom(room);
                    }
                    loadBookings();
                    updateDetailPanel(booking);
                    notifyDataChanged();
                    JOptionPane.showMessageDialog(this, "Booking cancelled successfully!");
                }
            });
            actionPanel.add(cancelBtn);
        }
        
        // Add Payment button (if due amount > 0)
        if (booking.getDueAmount() > 0) {
            JButton paymentBtn = new JButton("💰 Add Payment");
            paymentBtn.setFont(CustomTheme.BUTTON_FONT);
            paymentBtn.setBackground(CustomTheme.PRIMARY_COLOR);
            paymentBtn.setForeground(Color.WHITE);
            paymentBtn.setFocusPainted(false);
            paymentBtn.setBorderPainted(false);
            paymentBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            paymentBtn.setPreferredSize(new Dimension(140, 40));
            paymentBtn.addActionListener(e -> {
                String amount = JOptionPane.showInputDialog(this, "Enter payment amount:", booking.getDueAmount());
                if (amount != null) {
                    try {
                        double amt = Double.parseDouble(amount);
                        if (amt <= 0 || amt > booking.getDueAmount()) {
                            JOptionPane.showMessageDialog(this, "Invalid amount!");
                            return;
                        }
                        booking.addPayment(amt);
                        dataManager.updateBooking(booking);
                        loadBookings();
                        updateDetailPanel(booking);
                        notifyDataChanged();
                        JOptionPane.showMessageDialog(this, "Payment of ₹" + String.format("%.2f", amt) + " added!");
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(this, "Invalid amount!");
                    }
                }
            });
            actionPanel.add(paymentBtn);
        }
        
        detailsCard.add(actionPanel);
        
        JScrollPane scrollPane = new JScrollPane(detailsCard);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(Color.WHITE);
        
        detailPanel.add(scrollPane, BorderLayout.CENTER);
        detailPanel.revalidate();
        detailPanel.repaint();
    }

    private JPanel createInfoPanel(String title, String[][] data) {
        CustomTheme.ShadowPanel panel = new CustomTheme.ShadowPanel(10, 2);
        panel.setBackground(Color.WHITE);
        panel.setLayout(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        JLabel titleLabel = CustomTheme.createHeaderLabel(title);
        titleLabel.setForeground(CustomTheme.PRIMARY_COLOR);
        panel.add(titleLabel, BorderLayout.NORTH);
        
        JPanel gridPanel = new JPanel(new GridLayout(data.length, 2, 10, 8));
        gridPanel.setBackground(Color.WHITE);
        gridPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
        
        for (String[] row : data) {
            JLabel keyLabel = CustomTheme.createLabel(row[0] + ":");
            keyLabel.setForeground(CustomTheme.GRAY_COLOR);
            JLabel valueLabel = CustomTheme.createLabel(row[1]);
            valueLabel.setForeground(CustomTheme.TEXT_COLOR);
            valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
            gridPanel.add(keyLabel);
            gridPanel.add(valueLabel);
        }
        
        panel.add(gridPanel, BorderLayout.CENTER);
        return panel;
    }

    private void showNewBookingDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Create New Booking", true);
        dialog.setSize(600, 650);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());
        dialog.getContentPane().setBackground(Color.WHITE);
        
        JPanel formPanel = createBookingForm();
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 20));
        buttonPanel.setBackground(Color.WHITE);
        
        JButton createButton = new JButton("Create Booking");
        createButton.setFont(CustomTheme.BUTTON_FONT);
        createButton.setBackground(CustomTheme.SUCCESS_COLOR);
        createButton.setForeground(Color.WHITE);
        createButton.setFocusPainted(false);
        createButton.setBorderPainted(false);
        createButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        createButton.setPreferredSize(new Dimension(140, 42));
        createButton.addActionListener(e -> {
            if (createBooking(formPanel, dialog)) {
                dialog.dispose();
                loadBookings();
                notifyDataChanged();
                JOptionPane.showMessageDialog(this, "Booking created successfully!");
            }
        });
        
        JButton cancelButton = new JButton("Cancel");
        cancelButton.setFont(CustomTheme.BUTTON_FONT);
        cancelButton.setBackground(CustomTheme.DANGER_COLOR);
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setFocusPainted(false);
        cancelButton.setBorderPainted(false);
        cancelButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        cancelButton.setPreferredSize(new Dimension(140, 42));
        cancelButton.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(createButton);
        buttonPanel.add(cancelButton);
        
        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.setVisible(true);
    }
    
    private JPanel createBookingForm() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 12, 12, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Guest Selection
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel guestLabel = new JLabel("Select Guest:*");
        guestLabel.setFont(CustomTheme.NORMAL_FONT);
        guestLabel.setForeground(CustomTheme.TEXT_COLOR);
        panel.add(guestLabel, gbc);
        gbc.gridx = 1;
        
        JComboBox<GuestComboItem> guestCombo = new JComboBox<>();
        guestCombo.setFont(CustomTheme.NORMAL_FONT);
        guestCombo.setBackground(Color.WHITE);
        guestCombo.setForeground(CustomTheme.TEXT_COLOR);
        guestCombo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));
        guestCombo.setPreferredSize(new Dimension(300, 42));
        
        for (Guest guest : dataManager.getAllGuests()) {
            guestCombo.addItem(new GuestComboItem(guest));
        }
        
        guestCombo.setEditable(true);
        JTextField guestEditor = (JTextField) guestCombo.getEditor().getEditorComponent();
        guestEditor.setFont(CustomTheme.NORMAL_FONT);
        guestEditor.setForeground(CustomTheme.TEXT_COLOR);
        
        guestEditor.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String text = guestEditor.getText().toLowerCase();
                guestCombo.removeAllItems();
                for (Guest guest : dataManager.getAllGuests()) {
                    String display = guest.getFirstName() + " " + guest.getLastName() + " (" + guest.getPhone() + ")";
                    if (display.toLowerCase().contains(text) || text.isEmpty()) {
                        guestCombo.addItem(new GuestComboItem(guest));
                    }
                }
                guestCombo.showPopup();
            }
        });
        
        panel.add(guestCombo, gbc);
        
        // Room Selection
        gbc.gridx = 0; gbc.gridy = 1;
        JLabel roomLabel = new JLabel("Select Room:*");
        roomLabel.setFont(CustomTheme.NORMAL_FONT);
        roomLabel.setForeground(CustomTheme.TEXT_COLOR);
        panel.add(roomLabel, gbc);
        gbc.gridx = 1;
        
        JComboBox<RoomComboItem> roomCombo = new JComboBox<>();
        roomCombo.setFont(CustomTheme.NORMAL_FONT);
        roomCombo.setBackground(Color.WHITE);
        roomCombo.setForeground(CustomTheme.TEXT_COLOR);
        roomCombo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));
        roomCombo.setPreferredSize(new Dimension(300, 42));
        
        for (Room room : dataManager.getAllRooms()) {
            if ("Available".equals(room.getStatus())) {
                roomCombo.addItem(new RoomComboItem(room));
            }
        }
        panel.add(roomCombo, gbc);
        
        // Check-in Date
        gbc.gridx = 0; gbc.gridy = 2;
        JLabel checkInLabel = new JLabel("Check-in Date:*");
        checkInLabel.setFont(CustomTheme.NORMAL_FONT);
        checkInLabel.setForeground(CustomTheme.TEXT_COLOR);
        panel.add(checkInLabel, gbc);
        gbc.gridx = 1;
        
        JTextField checkInField = new JTextField(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        checkInField.setFont(CustomTheme.NORMAL_FONT);
        checkInField.setForeground(CustomTheme.TEXT_COLOR);
        checkInField.setBackground(Color.WHITE);
        checkInField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));
        checkInField.setPreferredSize(new Dimension(300, 42));
        panel.add(checkInField, gbc);
        
        // Check-out Date
        gbc.gridx = 0; gbc.gridy = 3;
        JLabel checkOutLabel = new JLabel("Check-out Date:*");
        checkOutLabel.setFont(CustomTheme.NORMAL_FONT);
        checkOutLabel.setForeground(CustomTheme.TEXT_COLOR);
        panel.add(checkOutLabel, gbc);
        gbc.gridx = 1;
        
        JTextField checkOutField = new JTextField(LocalDate.now().plusDays(3).format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        checkOutField.setFont(CustomTheme.NORMAL_FONT);
        checkOutField.setForeground(CustomTheme.TEXT_COLOR);
        checkOutField.setBackground(Color.WHITE);
        checkOutField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));
        checkOutField.setPreferredSize(new Dimension(300, 42));
        panel.add(checkOutField, gbc);
        
        // Number of Guests
        gbc.gridx = 0; gbc.gridy = 4;
        JLabel guestsLabel = new JLabel("Number of Guests:*");
        guestsLabel.setFont(CustomTheme.NORMAL_FONT);
        guestsLabel.setForeground(CustomTheme.TEXT_COLOR);
        panel.add(guestsLabel, gbc);
        gbc.gridx = 1;
        
        JSpinner guestsSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 10, 1));
        guestsSpinner.setFont(CustomTheme.NORMAL_FONT);
        guestsSpinner.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(5, 12, 5, 12)
        ));
        guestsSpinner.setPreferredSize(new Dimension(300, 42));
        panel.add(guestsSpinner, gbc);
        
        panel.putClientProperty("guestCombo", guestCombo);
        panel.putClientProperty("roomCombo", roomCombo);
        panel.putClientProperty("checkIn", checkInField);
        panel.putClientProperty("checkOut", checkOutField);
        panel.putClientProperty("guests", guestsSpinner);
        
        return panel;
    }
    
    private boolean createBooking(JPanel formPanel, JDialog dialog) {
        try {
            @SuppressWarnings("unchecked")
            JComboBox<GuestComboItem> guestCombo = (JComboBox<GuestComboItem>) formPanel.getClientProperty("guestCombo");
            @SuppressWarnings("unchecked")
            JComboBox<RoomComboItem> roomCombo = (JComboBox<RoomComboItem>) formPanel.getClientProperty("roomCombo");
            JTextField checkInField = (JTextField) formPanel.getClientProperty("checkIn");
            JTextField checkOutField = (JTextField) formPanel.getClientProperty("checkOut");
            JSpinner guestsSpinner = (JSpinner) formPanel.getClientProperty("guests");
            
            GuestComboItem guestItem = (GuestComboItem) guestCombo.getSelectedItem();
            if (guestItem == null) {
                JOptionPane.showMessageDialog(dialog, "Please select a valid guest!", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            Guest selectedGuest = guestItem.getGuest();
            
            RoomComboItem roomItem = (RoomComboItem) roomCombo.getSelectedItem();
            if (roomItem == null) {
                JOptionPane.showMessageDialog(dialog, "Please select a room!", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            Room selectedRoom = roomItem.getRoom();
            
            LocalDate checkIn, checkOut;
            try {
                checkIn = LocalDate.parse(checkInField.getText().trim(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                checkOut = LocalDate.parse(checkOutField.getText().trim(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                if (checkOut.isBefore(checkIn) || checkOut.equals(checkIn)) {
                    JOptionPane.showMessageDialog(dialog, "Check-out date must be after check-in date!", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return false;
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Please enter valid dates in dd/MM/yyyy format!", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            
            int nights = (int) java.time.temporal.ChronoUnit.DAYS.between(checkIn, checkOut);
            int numberOfGuests = (Integer) guestsSpinner.getValue();
            double totalAmount = selectedRoom.getPricePerNight() * nights;
            
            Booking newBooking = new Booking();
            newBooking.setBookingId(currentBookings.size() + 1);
            newBooking.setBookingNumber("BK" + String.format("%03d", currentBookings.size() + 1));
            newBooking.setGuest(selectedGuest);
            newBooking.setGuestId(selectedGuest.getGuestId());
            newBooking.setRoom(selectedRoom);
            newBooking.setRoomId(selectedRoom.getRoomId());
            newBooking.setCheckInDate(checkIn);
            newBooking.setCheckOutDate(checkOut);
            newBooking.setNumberOfNights(nights);
            newBooking.setNumberOfGuests(numberOfGuests);
            newBooking.setRoomPricePerNight(selectedRoom.getPricePerNight());
            newBooking.setStatus("Pending");
            newBooking.setPaymentStatus("Pending");
            newBooking.setTotalAmount(totalAmount);
            newBooking.setPaidAmount(0);
            newBooking.setDueAmount(totalAmount);
            newBooking.setSubtotal(totalAmount);
            newBooking.setTaxAmount(0);
            newBooking.setDiscountAmount(0);
            
            dataManager.addBooking(newBooking);
            
            selectedRoom.setStatus("Reserved");
            dataManager.updateRoom(selectedRoom);
            
            return true;
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(dialog, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return false;
        }
    }
    
    // Helper classes for combo boxes
    class GuestComboItem {
        private Guest guest;
        
        public GuestComboItem(Guest guest) {
            this.guest = guest;
        }
        
        public Guest getGuest() { 
            return guest; 
        }
        
        @Override
        public String toString() {
            return guest.getFirstName() + " " + guest.getLastName() + " (" + guest.getPhone() + ")";
        }
    }
    
    class RoomComboItem {
        private Room room;
        
        public RoomComboItem(Room room) {
            this.room = room;
        }
        
        public Room getRoom() { 
            return room; 
        }
        
        @Override
        public String toString() {
            return room.getRoomNumber() + " - " + room.getRoomType() + " (₹" + String.format("%.2f", room.getPricePerNight()) + "/night)";
        }
    }
}