package bongz.barbershop.layout.dashboards;

import java.io.IOException;
import bongz.barbershop.App;
import bongz.barbershop.dto.report.BarberDashboardCardDTO;
import bongz.barbershop.dto.report.DailyBarberTotalDTO;
import bongz.barbershop.dto.report.DailyShopTotalDTO;
import bongz.barbershop.dto.transaction.TransactionViewDTO;
import bongz.barbershop.loader.AppLoader;
import bongz.barbershop.loader.ModalLoader;
import bongz.barbershop.model.BarberModel;
import bongz.barbershop.model.UserModel;
import bongz.barbershop.service.barber.BarberService;
import bongz.barbershop.service.reporting.ReportService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class ManagerOperationController {

    private final BarberService barberService = new BarberService();
    private final ReportService reportService = new ReportService();

    private final ToggleGroup navigationToggleGroup = new ToggleGroup();
    private final ObservableList<TransactionViewDTO> allTransactions = FXCollections.observableArrayList();

    private App app;
    private UserModel currentUser;

    @FXML
    private ToggleButton overviewToggle;
    @FXML
    private ToggleButton recordToggle;
    @FXML
    private ToggleButton transactionsToggle;
    @FXML
    private DatePicker businessDatePicker;

    @FXML
    private HBox overviewPane;
    @FXML
    private HBox recordPane;
    @FXML
    private VBox transactionsPane;

    @FXML
    private Label greetingLabel;
    @FXML
    private Label totalCutsTodayLabel;
    @FXML
    private Label barberCommissionListLabel;
    @FXML
    private Label shopSalesTodayLabel;
    @FXML
    private Label grossSalesTodayLabel;

    @FXML
    private TableView<BarberModel> activeBarbersTable;
    @FXML
    private TableColumn<BarberModel, Integer> activeBarberIdColumn;
    @FXML
    private TableColumn<BarberModel, String> activeBarberNameColumn;
    @FXML
    private Label selectedBarberIdLabel;
    @FXML
    private Label selectedBarberImagePathLabel;
    @FXML
    private Label selectedBarberNameLabel;
    @FXML
    private Label selectedBarberHaircutCountLabel;
    @FXML
    private Label selectedBarberCommissionLabel;
    @FXML
    private Label selectedBarberShopShareLabel;
    @FXML
    private Button newTransactionButton;
    @FXML
    private TableView<TransactionViewDTO> selectedBarberTransactionsTable;
    @FXML
    private TableColumn<TransactionViewDTO, Integer> selectedTransactionIdColumn;
    @FXML
    private TableColumn<TransactionViewDTO, Integer> selectedPricingCategoryIdColumn;
    @FXML
    private TableColumn<TransactionViewDTO, Integer> selectedLoggedByUserIdColumn;
    @FXML
    private TableColumn<TransactionViewDTO, String> selectedBusinessDateColumn;
    @FXML
    private TableColumn<TransactionViewDTO, String> selectedRecordedAtColumn;
    @FXML
    private TableColumn<TransactionViewDTO, String> selectedPricingCategoryNameColumn;
    @FXML
    private TableColumn<TransactionViewDTO, Integer> selectedChargedAmountColumn;
    @FXML
    private TableColumn<TransactionViewDTO, Integer> selectedBarberCommissionPercentColumn;
    @FXML
    private TableColumn<TransactionViewDTO, String> selectedStatusColumn;
    @FXML
    private TableColumn<TransactionViewDTO, String> selectedVoidReasonColumn;
    @FXML
    private TableColumn<TransactionViewDTO, String> selectedNoteColumn;

    @FXML
    private TextField searchField;
    @FXML
    private ComboBox<String> filterComboBox;
    @FXML
    private TableView<TransactionViewDTO> transactionsTable;
    @FXML
    private TableColumn<TransactionViewDTO, Integer> transactionsIdColumn;
    @FXML
    private TableColumn<TransactionViewDTO, Integer> transactionsBarberIdColumn;
    @FXML
    private TableColumn<TransactionViewDTO, Integer> transactionsPricingCategoryIdColumn;
    @FXML
    private TableColumn<TransactionViewDTO, Integer> transactionsLoggedByUserIdColumn;
    @FXML
    private TableColumn<TransactionViewDTO, String> transactionsBusinessDateColumn;
    @FXML
    private TableColumn<TransactionViewDTO, String> transactionsRecordedAtColumn;
    @FXML
    private TableColumn<TransactionViewDTO, String> transactionsPricingCategoryNameColumn;
    @FXML
    private TableColumn<TransactionViewDTO, Integer> transactionsShopEarningAmountColumn;
    @FXML
    private TableColumn<TransactionViewDTO, String> transactionsStatusColumn;

    @FXML
    private Label statusLabel;

    @FXML
    private void initialize() {
        configureNavigation();
        configureActiveBarbersTable();
        configureSelectedBarberTransactionsTable();
        configureTransactionsTable();
        configureTransactionsFilterControls();
        setVisiblePane(overviewPane);
    }

    public void load(App app, UserModel currentUser) {
        this.app = app;
        this.currentUser = currentUser != null ? currentUser : app.getCurrentUser();

        if (this.currentUser == null) {
            showStatus("No logged-in user found. Please log in again.");
            return;
        }

        if (businessDatePicker.getValue() == null) {
            businessDatePicker.setValue(LocalDate.now());
        }

        overviewToggle.setSelected(true);
        setVisiblePane(overviewPane);
        loadManagerScreenData();
    }

    @FXML
    private void handleOverviewTab() {
        overviewToggle.setSelected(true);
        setVisiblePane(overviewPane);
        loadOverviewData();
    }

    @FXML
    private void handleRecordTab() {
        recordToggle.setSelected(true);
        setVisiblePane(recordPane);
        loadRecordData();
    }

    @FXML
    private void handleTransactionsTab() {
        transactionsToggle.setSelected(true);
        setVisiblePane(transactionsPane);
        loadTransactionsData();
    }

    @FXML
    private void handleBusinessDateChanged() {
        if (currentUser == null) {
            return;
        }

        loadManagerScreenData();
    }

    @FXML
    private void handleOpenNewTransactionModal() {
        BarberModel selectedBarber = activeBarbersTable.getSelectionModel().getSelectedItem();

        if (selectedBarber == null) {
            showStatus("Select a barber first.");
            return;
        }

        try {
            int selectedBarberId = selectedBarber.getBarberId();
            ModalLoader.load_new_transaction_modal(
                    app,
                    currentUser,
                    selectedBarber,
                    getBusinessDate(),
                    message -> {
                        loadManagerScreenData();
                        selectBarberById(selectedBarberId);
                        recordToggle.setSelected(true);
                        setVisiblePane(recordPane);
                        showStatus(message);
                    });
        } catch (IOException e) {
            showStatus("Failed to open new transaction modal.");
        }
    }

    @FXML
    private void handleLogout() {
        try {
            currentUser = null;
            app.clearCurrentUser();
            AppLoader.load_app_window(app, app.getMainStage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadManagerScreenData() {
        loadOverviewData();
        loadRecordData();
        loadTransactionsData();
        showStatus("Loaded manager data for " + getBusinessDate() + ".");
    }

    private void loadOverviewData() {
        String businessDate = getBusinessDate();
        DailyShopTotalDTO shopTotal = reportService.getDailyShopTotal(businessDate);
        List<DailyBarberTotalDTO> barberTotals = reportService.getDailyBarberTotals(businessDate);

        greetingLabel.setText("Hello, " + currentUser.getUsername() + " (" + currentUser.getRole() + ")");
        totalCutsTodayLabel.setText("Total Cuts Today: " + shopTotal.getHaircutCount());
        shopSalesTodayLabel.setText("Shop Earnings Today: " + formatCurrency(shopTotal.getShopShareTotal()));
        grossSalesTodayLabel.setText("Gross Sales Today: " + formatCurrency(shopTotal.getGrossSales()));
        barberCommissionListLabel.setText(buildBarberSummary(barberTotals));
    }

    private void loadRecordData() {
        List<BarberModel> activeBarbers = barberService.getAllActiveBarbers();
        activeBarbersTable.setItems(FXCollections.observableArrayList(activeBarbers));

        BarberModel selectedBarber = activeBarbersTable.getSelectionModel().getSelectedItem();
        if (selectedBarber == null && !activeBarbers.isEmpty()) {
            activeBarbersTable.getSelectionModel().selectFirst();
            selectedBarber = activeBarbersTable.getSelectionModel().getSelectedItem();
        }

        newTransactionButton.setDisable(selectedBarber == null);
        refreshSelectedBarberSection(selectedBarber);
    }

    private void loadTransactionsData() {
        String businessDate = getBusinessDate();
        allTransactions.setAll(reportService.getTransactionViewsByBusinessDate(businessDate));
        applyTransactionsFilter();
    }

    private void refreshSelectedBarberSection(BarberModel selectedBarber) {
        if (selectedBarber == null) {
            selectedBarberIdLabel.setText("Selected Barber ID: -");
            selectedBarberImagePathLabel.setText("Image Path: -");
            selectedBarberNameLabel.setText("Selected Barber Name: -");
            selectedBarberHaircutCountLabel.setText("Haircut Count Today: 0");
            selectedBarberCommissionLabel.setText("Commission Today: " + formatCurrency(0));
            selectedBarberShopShareLabel.setText("Shop Share Today: " + formatCurrency(0));
            selectedBarberTransactionsTable.setItems(FXCollections.observableArrayList());
            return;
        }

        String businessDate = getBusinessDate();
        BarberDashboardCardDTO card = reportService.getBarberDashboardCards(businessDate).stream()
                .filter(item -> item.getBarberId() == selectedBarber.getBarberId())
                .findFirst()
                .orElse(new BarberDashboardCardDTO(
                        selectedBarber.getBarberId(),
                        selectedBarber.getName(),
                        selectedBarber.getImagePath(),
                        0,
                        0,
                        0));

        selectedBarberIdLabel.setText("Selected Barber ID: " + selectedBarber.getBarberId());
        selectedBarberImagePathLabel.setText("Image Path: " + valueOrPlaceholder(selectedBarber.getImagePath()));
        selectedBarberNameLabel.setText("Selected Barber Name: " + selectedBarber.getName());
        selectedBarberHaircutCountLabel.setText("Haircut Count Today: " + card.getHaircutCountToday());
        selectedBarberCommissionLabel
                .setText("Commission Today: " + formatCurrency(card.getBarberCommissionToday()));
        selectedBarberShopShareLabel.setText("Shop Share Today: " + formatCurrency(card.getShopShareToday()));

        selectedBarberTransactionsTable.setItems(FXCollections.observableArrayList(
                reportService.getTransactionViewsByBarberAndBusinessDate(selectedBarber.getBarberId(), businessDate)));
    }

    private void configureNavigation() {
        overviewToggle.setToggleGroup(navigationToggleGroup);
        recordToggle.setToggleGroup(navigationToggleGroup);
        transactionsToggle.setToggleGroup(navigationToggleGroup);

        navigationToggleGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null && oldValue != null) {
                oldValue.setSelected(true);
            }
        });
    }

    private void configureActiveBarbersTable() {
        activeBarberIdColumn.setCellValueFactory(new PropertyValueFactory<>("barberId"));
        activeBarberNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        activeBarbersTable.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {
                    newTransactionButton.setDisable(newValue == null);
                    refreshSelectedBarberSection(newValue);
                });
    }

    private void configureSelectedBarberTransactionsTable() {
        selectedTransactionIdColumn.setCellValueFactory(new PropertyValueFactory<>("transactionId"));
        selectedPricingCategoryIdColumn.setCellValueFactory(new PropertyValueFactory<>("pricingCategoryId"));
        selectedLoggedByUserIdColumn.setCellValueFactory(new PropertyValueFactory<>("loggedByUserId"));
        selectedBusinessDateColumn.setCellValueFactory(new PropertyValueFactory<>("businessDate"));
        selectedRecordedAtColumn.setCellValueFactory(new PropertyValueFactory<>("recordedAt"));
        selectedPricingCategoryNameColumn.setCellValueFactory(new PropertyValueFactory<>("pricingCategoryName"));
        selectedChargedAmountColumn.setCellValueFactory(new PropertyValueFactory<>("chargedAmount"));
        selectedBarberCommissionPercentColumn
                .setCellValueFactory(new PropertyValueFactory<>("barberCommissionPercent"));
        selectedStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        selectedVoidReasonColumn.setCellValueFactory(new PropertyValueFactory<>("voidReason"));
        selectedNoteColumn.setCellValueFactory(new PropertyValueFactory<>("note"));

        selectedBarberTransactionsTable.setRowFactory(table -> {
            TableRow<TransactionViewDTO> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (row.isEmpty() || event.getClickCount() != 1) {
                    return;
                }

                int selectedBarberId = activeBarbersTable.getSelectionModel().getSelectedItem() == null
                        ? -1
                        : activeBarbersTable.getSelectionModel().getSelectedItem().getBarberId();

                openTransactionDetailModal(row.getItem(), message -> {
                    loadManagerScreenData();
                    selectBarberById(selectedBarberId);
                    recordToggle.setSelected(true);
                    setVisiblePane(recordPane);
                    showStatus(message);
                });
            });
            return row;
        });
    }

    private void configureTransactionsTable() {
        transactionsIdColumn.setCellValueFactory(new PropertyValueFactory<>("transactionId"));
        transactionsBarberIdColumn.setCellValueFactory(new PropertyValueFactory<>("barberId"));
        transactionsPricingCategoryIdColumn.setCellValueFactory(new PropertyValueFactory<>("pricingCategoryId"));
        transactionsLoggedByUserIdColumn.setCellValueFactory(new PropertyValueFactory<>("loggedByUserId"));
        transactionsBusinessDateColumn.setCellValueFactory(new PropertyValueFactory<>("businessDate"));
        transactionsRecordedAtColumn.setCellValueFactory(new PropertyValueFactory<>("recordedAt"));
        transactionsPricingCategoryNameColumn.setCellValueFactory(new PropertyValueFactory<>("pricingCategoryName"));
        transactionsShopEarningAmountColumn.setCellValueFactory(new PropertyValueFactory<>("shopEarningAmount"));
        transactionsStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        transactionsTable.setRowFactory(table -> {
            TableRow<TransactionViewDTO> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (row.isEmpty() || event.getClickCount() != 1) {
                    return;
                }

                openTransactionDetailModal(row.getItem(), message -> {
                    loadManagerScreenData();
                    transactionsToggle.setSelected(true);
                    setVisiblePane(transactionsPane);
                    showStatus(message);
                });
            });
            return row;
        });
    }

    private void configureTransactionsFilterControls() {
        filterComboBox.setItems(FXCollections.observableArrayList("ALL", "POSTED", "VOID"));
        filterComboBox.getSelectionModel().selectFirst();

        searchField.textProperty().addListener((observable, oldValue, newValue) -> applyTransactionsFilter());
        filterComboBox.valueProperty().addListener((observable, oldValue, newValue) -> applyTransactionsFilter());
    }

    private void applyTransactionsFilter() {
        String searchTerm = valueOrEmpty(searchField.getText()).toLowerCase(Locale.ROOT);
        String selectedStatus = filterComboBox.getValue();

        List<TransactionViewDTO> filteredTransactions = allTransactions.stream()
                .filter(transaction -> matchesStatus(transaction, selectedStatus))
                .filter(transaction -> matchesSearch(transaction, searchTerm))
                .collect(Collectors.toList());

        transactionsTable.setItems(FXCollections.observableArrayList(filteredTransactions));
    }

    private boolean matchesStatus(TransactionViewDTO transaction, String selectedStatus) {
        if (selectedStatus == null || "ALL".equalsIgnoreCase(selectedStatus)) {
            return true;
        }

        return selectedStatus.equalsIgnoreCase(transaction.getStatus());
    }

    private boolean matchesSearch(TransactionViewDTO transaction, String searchTerm) {
        if (searchTerm.isBlank()) {
            return true;
        }

        return valueOrEmpty(transaction.getBarberName()).toLowerCase(Locale.ROOT).contains(searchTerm)
                || valueOrEmpty(transaction.getPricingCategoryName()).toLowerCase(Locale.ROOT).contains(searchTerm)
                || valueOrEmpty(transaction.getLoggedByUsername()).toLowerCase(Locale.ROOT).contains(searchTerm)
                || valueOrEmpty(transaction.getStatus()).toLowerCase(Locale.ROOT).contains(searchTerm)
                || valueOrEmpty(transaction.getBusinessDate()).toLowerCase(Locale.ROOT).contains(searchTerm)
                || String.valueOf(transaction.getTransactionId()).contains(searchTerm);
    }

    private void setVisiblePane(Node targetPane) {
        setPaneState(overviewPane, targetPane == overviewPane);
        setPaneState(recordPane, targetPane == recordPane);
        setPaneState(transactionsPane, targetPane == transactionsPane);
    }

    private void setPaneState(Node pane, boolean visible) {
        pane.setVisible(visible);
        pane.setManaged(visible);
    }

    private String buildBarberSummary(List<DailyBarberTotalDTO> barberTotals) {
        if (barberTotals.isEmpty()) {
            return "No posted haircuts for this date.";
        }

        return barberTotals.stream()
                .map(total -> total.getBarberName()
                        + ": "
                        + total.getHaircutCount()
                        + " cuts, barber "
                        + formatCurrency(total.getBarberCommissionTotal())
                        + ", shop "
                        + formatCurrency(total.getShopShareTotal()))
                .collect(Collectors.joining("\n"));
    }

    private String getBusinessDate() {
        LocalDate selectedDate = businessDatePicker.getValue();
        if (selectedDate == null) {
            selectedDate = LocalDate.now();
            businessDatePicker.setValue(selectedDate);
        }

        return selectedDate.toString();
    }

    private String formatCurrency(int amountPesos) {
        return "PHP " + amountPesos;
    }

    private String valueOrEmpty(String value) {
        return value == null ? "" : value;
    }

    private String valueOrPlaceholder(String value) {
        return value == null || value.isBlank() ? "(none)" : value;
    }

    private void showStatus(String message) {
        statusLabel.setText(message);
    }

    private void openTransactionDetailModal(TransactionViewDTO transaction,
            java.util.function.Consumer<String> onUpdate) {
        if (transaction == null) {
            return;
        }

        try {
            ModalLoader.load_transaction_detail_modal(app, currentUser, transaction, onUpdate);
        } catch (IOException e) {
            showStatus("Failed to open transaction detail modal.");
        }
    }

    private void selectBarberById(int barberId) {
        if (barberId <= 0) {
            return;
        }

        for (BarberModel barber : activeBarbersTable.getItems()) {
            if (barber.getBarberId() == barberId) {
                activeBarbersTable.getSelectionModel().select(barber);
                activeBarbersTable.scrollTo(barber);
                return;
            }
        }
    }
}
