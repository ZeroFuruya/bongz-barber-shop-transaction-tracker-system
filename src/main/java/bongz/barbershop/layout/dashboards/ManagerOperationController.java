package bongz.barbershop.layout.dashboards;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
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
import bongz.barbershop.storage.AppDataPaths;
import bongz.barbershop.ui.AnimationSupport;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TableCell;
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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.util.function.Function;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class ManagerOperationController {

    private final BarberService barberService = new BarberService();
    private final ReportService reportService = new ReportService();
    private final Image defaultBarberImage = loadDefaultBarberImage();

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
    private ImageView selectedBarberImageView;
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
    private TableColumn<TransactionViewDTO, String> selectedPricingCategoryIdColumn;
    @FXML
    private TableColumn<TransactionViewDTO, String> selectedLoggedByUserIdColumn;
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
    private TableColumn<TransactionViewDTO, String> transactionsBarberIdColumn;
    @FXML
    private TableColumn<TransactionViewDTO, String> transactionsPricingCategoryIdColumn;
    @FXML
    private TableColumn<TransactionViewDTO, String> transactionsLoggedByUserIdColumn;
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
        configureReadableTables();
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

        greetingLabel.setText(currentUser.getUsername());
        totalCutsTodayLabel.setText(String.valueOf(shopTotal.getHaircutCount()));
        shopSalesTodayLabel.setText(formatCurrency(shopTotal.getShopShareTotal()));
        grossSalesTodayLabel.setText(formatCurrency(shopTotal.getGrossSales()));
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
            setBarberPreviewImage(selectedBarberImageView, null);
            selectedBarberNameLabel.setText("No barber selected");
            selectedBarberHaircutCountLabel.setText("0");
            selectedBarberCommissionLabel.setText(formatCurrency(0));
            selectedBarberShopShareLabel.setText(formatCurrency(0));
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

        setBarberPreviewImage(selectedBarberImageView, selectedBarber.getImagePath());
        selectedBarberNameLabel.setText(selectedBarber.getName());
        selectedBarberHaircutCountLabel.setText(String.valueOf(card.getHaircutCountToday()));
        selectedBarberCommissionLabel.setText(formatCurrency(card.getBarberCommissionToday()));
        selectedBarberShopShareLabel.setText(formatCurrency(card.getShopShareToday()));

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
        configureTextColumn(selectedPricingCategoryIdColumn,
                transaction -> valueOrPlaceholder(transaction.getPricingCategoryName()));
        configureTextColumn(selectedLoggedByUserIdColumn,
                transaction -> valueOrPlaceholder(transaction.getLoggedByUsername()));
        selectedBusinessDateColumn.setCellValueFactory(new PropertyValueFactory<>("businessDate"));
        selectedRecordedAtColumn.setCellValueFactory(new PropertyValueFactory<>("recordedAt"));
        selectedPricingCategoryNameColumn.setCellValueFactory(new PropertyValueFactory<>("pricingCategoryName"));
        selectedChargedAmountColumn.setCellValueFactory(new PropertyValueFactory<>("chargedAmount"));
        selectedBarberCommissionPercentColumn
                .setCellValueFactory(new PropertyValueFactory<>("barberCommissionPercent"));
        selectedStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        selectedVoidReasonColumn.setCellValueFactory(new PropertyValueFactory<>("voidReason"));
        selectedNoteColumn.setCellValueFactory(new PropertyValueFactory<>("note"));
        configureCurrencyColumn(selectedChargedAmountColumn);

        selectedTransactionIdColumn.setPrefWidth(55);
        selectedPricingCategoryIdColumn.setText("Category");
        selectedPricingCategoryIdColumn.setPrefWidth(125);
        selectedLoggedByUserIdColumn.setText("Logged By");
        selectedLoggedByUserIdColumn.setPrefWidth(95);
        selectedBusinessDateColumn.setVisible(false);
        selectedRecordedAtColumn.setText("Recorded");
        selectedRecordedAtColumn.setPrefWidth(105);
        selectedPricingCategoryNameColumn.setVisible(false);
        selectedChargedAmountColumn.setPrefWidth(80);
        selectedBarberCommissionPercentColumn.setVisible(false);
        selectedStatusColumn.setPrefWidth(70);
        selectedVoidReasonColumn.setVisible(false);
        selectedNoteColumn.setVisible(false);

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
        configureTextColumn(transactionsBarberIdColumn,
                transaction -> valueOrPlaceholder(transaction.getBarberName()));
        configureTextColumn(transactionsPricingCategoryIdColumn,
                transaction -> valueOrPlaceholder(transaction.getPricingCategoryName()));
        configureTextColumn(transactionsLoggedByUserIdColumn,
                transaction -> valueOrPlaceholder(transaction.getLoggedByUsername()));
        transactionsBusinessDateColumn.setCellValueFactory(new PropertyValueFactory<>("businessDate"));
        transactionsRecordedAtColumn.setCellValueFactory(new PropertyValueFactory<>("recordedAt"));
        transactionsPricingCategoryNameColumn.setCellValueFactory(new PropertyValueFactory<>("pricingCategoryName"));
        transactionsShopEarningAmountColumn.setCellValueFactory(new PropertyValueFactory<>("shopEarningAmount"));
        transactionsStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        configureCurrencyColumn(transactionsShopEarningAmountColumn);

        transactionsIdColumn.setPrefWidth(55);
        transactionsBarberIdColumn.setText("Barber");
        transactionsBarberIdColumn.setPrefWidth(130);
        transactionsPricingCategoryIdColumn.setText("Category");
        transactionsPricingCategoryIdColumn.setPrefWidth(155);
        transactionsLoggedByUserIdColumn.setText("Logged By");
        transactionsLoggedByUserIdColumn.setPrefWidth(105);
        transactionsBusinessDateColumn.setText("Date");
        transactionsBusinessDateColumn.setPrefWidth(95);
        transactionsRecordedAtColumn.setVisible(false);
        transactionsPricingCategoryNameColumn.setVisible(false);
        transactionsShopEarningAmountColumn.setText("Shop");
        transactionsShopEarningAmountColumn.setPrefWidth(95);
        transactionsStatusColumn.setText("Status");
        transactionsStatusColumn.setPrefWidth(75);

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

    private void configureReadableTables() {
        for (TableView<?> table : List.of(
                activeBarbersTable,
                selectedBarberTransactionsTable,
                transactionsTable)) {
            configureReadableTable(table);
        }
    }

    private void configureReadableTable(TableView<?> table) {
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setFixedCellSize(32);
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
        List<Node> panes = List.of(overviewPane, recordPane, transactionsPane);
        Node currentPane = panes.stream()
                .filter(Node::isVisible)
                .findFirst()
                .orElse(null);

        panes.stream()
                .filter(pane -> pane != currentPane && pane != targetPane)
                .forEach(pane -> setPaneState(pane, false));

        AnimationSupport.switchVisiblePane(currentPane, targetPane);
    }

    private void setPaneState(Node pane, boolean visible) {
        pane.setVisible(visible);
        pane.setManaged(visible);
    }

    private String buildBarberSummary(List<DailyBarberTotalDTO> barberTotals) {
        if (barberTotals.isEmpty()) {
            return "No posted cuts for this date.";
        }

        return barberTotals.stream()
                .map(total -> total.getBarberName()
                        + "\n"
                        + total.getHaircutCount()
                        + " cuts | Commission "
                        + formatCurrency(total.getBarberCommissionTotal())
                        + " | Shop "
                        + formatCurrency(total.getShopShareTotal()))
                .collect(Collectors.joining("\n\n"));
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

    private void setBarberPreviewImage(ImageView imageView, String storedImagePath) {
        if (imageView == null) {
            return;
        }

        imageView.setImage(loadBarberPreviewImage(storedImagePath));
    }

    private Image loadBarberPreviewImage(String storedImagePath) {
        if (storedImagePath == null || storedImagePath.isBlank()) {
            return defaultBarberImage;
        }

        try {
            Path resolvedImagePath = AppDataPaths.resolveAppDataPath(storedImagePath);
            if (Files.exists(resolvedImagePath)) {
                return new Image(resolvedImagePath.toUri().toString(), true);
            }
        } catch (IOException ignored) {
        }

        return defaultBarberImage;
    }

    private Image loadDefaultBarberImage() {
        var placeholderResource = App.class.getResource("assets/images/Usernameicon.png");
        return placeholderResource == null ? null : new Image(placeholderResource.toExternalForm(), true);
    }

    private void showStatus(String message) {
        statusLabel.setText(message);
    }

    private <T> void configureTextColumn(TableColumn<T, String> column, Function<T, String> valueProvider) {
        column.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue() == null ? "" : valueProvider.apply(cellData.getValue())));
    }

    private <T> void configureCurrencyColumn(TableColumn<T, Integer> column) {
        column.setCellFactory(tableColumn -> new TableCell<T, Integer>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : formatCurrency(item));
            }
        });
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
