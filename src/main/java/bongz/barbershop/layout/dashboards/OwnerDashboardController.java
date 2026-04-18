package bongz.barbershop.layout.dashboards;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import bongz.barbershop.App;
import bongz.barbershop.dto.common.ServiceResult;
import bongz.barbershop.dto.report.DailyBarberTotalDTO;
import bongz.barbershop.dto.report.DailyShopTotalDTO;
import bongz.barbershop.dto.report.OwnerDashboardDTO;
import bongz.barbershop.dto.report.PricingCategorySummaryDTO;
import bongz.barbershop.dto.transaction.TransactionViewDTO;
import bongz.barbershop.loader.AppLoader;
import bongz.barbershop.loader.ModalLoader;
import bongz.barbershop.model.BarberModel;
import bongz.barbershop.model.PricingCategoryModel;
import bongz.barbershop.model.ShopSettingsModel;
import bongz.barbershop.model.UserModel;
import bongz.barbershop.model.enums.UserRole;
import bongz.barbershop.service.barber.BarberService;
import bongz.barbershop.service.pricing.PricingCategoryService;
import bongz.barbershop.service.reporting.ReportService;
import bongz.barbershop.service.settings.ShopSettingsService;
import bongz.barbershop.service.user.UserService;
import bongz.barbershop.storage.AppDataPaths;
import bongz.barbershop.ui.AnimationSupport;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
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

public class OwnerDashboardController {

    private final BarberService barberService = new BarberService();
    private final PricingCategoryService pricingCategoryService = new PricingCategoryService();
    private final ShopSettingsService shopSettingsService = new ShopSettingsService();
    private final UserService userService = new UserService();
    private final ReportService reportService = new ReportService();
    private final Image defaultBarberImage = loadDefaultBarberImage();

    private final ToggleGroup mainNavigationGroup = new ToggleGroup();
    private final ToggleGroup operationsNavigationGroup = new ToggleGroup();

    private final ObservableList<TransactionViewDTO> overviewRecentTransactions = FXCollections.observableArrayList();
    private final ObservableList<TransactionViewDTO> operationsAllTransactions = FXCollections.observableArrayList();
    private final ObservableList<BarberModel> allBarbers = FXCollections.observableArrayList();
    private final ObservableList<PricingCategoryModel> allPricingCategories = FXCollections.observableArrayList();
    private final ObservableList<UserModel> allUsers = FXCollections.observableArrayList();
    private final ObservableList<DailyShopTotalDTO> earningsDailyShopTotals = FXCollections.observableArrayList();
    private final ObservableList<DailyBarberTotalDTO> earningsBarberTotals = FXCollections.observableArrayList();
    private final ObservableList<PricingCategorySummaryDTO> earningsPricingSummaries = FXCollections.observableArrayList();

    private App app;
    private UserModel currentUser;
    private ShopSettingsModel currentSettings;

    @FXML
    private ToggleButton overviewNavButton;
    @FXML
    private ToggleButton operationsNavButton;
    @FXML
    private ToggleButton earningsNavButton;
    @FXML
    private ToggleButton barbersNavButton;
    @FXML
    private ToggleButton pricingNavButton;
    @FXML
    private ToggleButton usersNavButton;
    @FXML
    private ToggleButton settingsNavButton;
    @FXML
    private Label pageTitleLabel;
    @FXML
    private Label signedInAsLabel;
    @FXML
    private DatePicker businessDatePicker;
    @FXML
    private VBox overviewPane;
    @FXML
    private VBox operationsPane;
    @FXML
    private VBox earningsPane;
    @FXML
    private VBox barbersPane;
    @FXML
    private VBox pricingPane;
    @FXML
    private VBox usersPane;
    @FXML
    private VBox settingsPane;
    @FXML
    private Label statusLabel;

    @FXML
    private Label overviewGreetingLabel;
    @FXML
    private Label overviewDateLabel;
    @FXML
    private Label overviewHaircutsLabel;
    @FXML
    private Label overviewGrossSalesLabel;
    @FXML
    private Label overviewBarberCommissionsLabel;
    @FXML
    private Label overviewShopEarningsLabel;
    @FXML
    private PieChart overviewRevenueSplitChart;
    @FXML
    private BarChart<String, Number> overviewPricingMixChart;
    @FXML
    private CategoryAxis overviewPricingMixCategoryAxis;
    @FXML
    private NumberAxis overviewPricingMixValueAxis;
    @FXML
    private TableView<TransactionViewDTO> overviewRecentTransactionsTable;
    @FXML
    private TableColumn<TransactionViewDTO, Integer> overviewTransactionIdColumn;
    @FXML
    private TableColumn<TransactionViewDTO, String> overviewTransactionBarberColumn;
    @FXML
    private TableColumn<TransactionViewDTO, String> overviewTransactionCategoryColumn;
    @FXML
    private TableColumn<TransactionViewDTO, Integer> overviewTransactionAmountColumn;
    @FXML
    private TableColumn<TransactionViewDTO, String> overviewTransactionStatusColumn;

    @FXML
    private ToggleButton operationsOverviewToggle;
    @FXML
    private ToggleButton operationsRecordToggle;
    @FXML
    private ToggleButton operationsTransactionsToggle;
    @FXML
    private VBox operationsOverviewPane;
    @FXML
    private HBox operationsRecordPane;
    @FXML
    private VBox operationsTransactionsPane;
    @FXML
    private Label operationsTotalCutsLabel;
    @FXML
    private Label operationsBarberCommissionListLabel;
    @FXML
    private Label operationsShopSalesLabel;
    @FXML
    private Label operationsGrossSalesLabel;
    @FXML
    private BarChart<String, Number> operationsBarberBreakdownChart;
    @FXML
    private CategoryAxis operationsBarberBreakdownCategoryAxis;
    @FXML
    private NumberAxis operationsBarberBreakdownValueAxis;
    @FXML
    private TableView<BarberModel> operationsActiveBarbersTable;
    @FXML
    private TableColumn<BarberModel, Integer> operationsActiveBarberIdColumn;
    @FXML
    private TableColumn<BarberModel, String> operationsActiveBarberNameColumn;
    @FXML
    private Label operationsSelectedBarberIdLabel;
    @FXML
    private ImageView operationsSelectedBarberImageView;
    @FXML
    private Label operationsSelectedBarberNameLabel;
    @FXML
    private Label operationsSelectedBarberHaircutCountLabel;
    @FXML
    private Label operationsSelectedBarberCommissionLabel;
    @FXML
    private Label operationsSelectedBarberShopShareLabel;
    @FXML
    private Button operationsNewTransactionButton;
    @FXML
    private TableView<TransactionViewDTO> operationsSelectedBarberTransactionsTable;
    @FXML
    private TableColumn<TransactionViewDTO, Integer> operationsSelectedTransactionIdColumn;
    @FXML
    private TableColumn<TransactionViewDTO, String> operationsSelectedPricingCategoryIdColumn;
    @FXML
    private TableColumn<TransactionViewDTO, String> operationsSelectedLoggedByUserIdColumn;
    @FXML
    private TableColumn<TransactionViewDTO, String> operationsSelectedBusinessDateColumn;
    @FXML
    private TableColumn<TransactionViewDTO, String> operationsSelectedRecordedAtColumn;
    @FXML
    private TableColumn<TransactionViewDTO, String> operationsSelectedPricingCategoryNameColumn;
    @FXML
    private TableColumn<TransactionViewDTO, Integer> operationsSelectedChargedAmountColumn;
    @FXML
    private TableColumn<TransactionViewDTO, Integer> operationsSelectedBarberCommissionPercentColumn;
    @FXML
    private TableColumn<TransactionViewDTO, String> operationsSelectedStatusColumn;
    @FXML
    private TableColumn<TransactionViewDTO, String> operationsSelectedVoidReasonColumn;
    @FXML
    private TableColumn<TransactionViewDTO, String> operationsSelectedNoteColumn;
    @FXML
    private TextField operationsSearchField;
    @FXML
    private ComboBox<String> operationsFilterComboBox;
    @FXML
    private TableView<TransactionViewDTO> operationsTransactionsTable;
    @FXML
    private TableColumn<TransactionViewDTO, Integer> operationsTransactionsIdColumn;
    @FXML
    private TableColumn<TransactionViewDTO, String> operationsTransactionsBarberIdColumn;
    @FXML
    private TableColumn<TransactionViewDTO, String> operationsTransactionsPricingCategoryIdColumn;
    @FXML
    private TableColumn<TransactionViewDTO, String> operationsTransactionsLoggedByUserIdColumn;
    @FXML
    private TableColumn<TransactionViewDTO, String> operationsTransactionsBusinessDateColumn;
    @FXML
    private TableColumn<TransactionViewDTO, String> operationsTransactionsRecordedAtColumn;
    @FXML
    private TableColumn<TransactionViewDTO, String> operationsTransactionsPricingCategoryNameColumn;
    @FXML
    private TableColumn<TransactionViewDTO, Integer> operationsTransactionsShopEarningAmountColumn;
    @FXML
    private TableColumn<TransactionViewDTO, String> operationsTransactionsStatusColumn;

    @FXML
    private DatePicker earningsFromDatePicker;
    @FXML
    private DatePicker earningsToDatePicker;
    @FXML
    private Label earningsRangeLabel;
    @FXML
    private Label earningsHaircutsLabel;
    @FXML
    private Label earningsGrossSalesLabel;
    @FXML
    private Label earningsBarberCommissionsLabel;
    @FXML
    private Label earningsShopEarningsLabel;
    @FXML
    private TableView<DailyShopTotalDTO> earningsShopTotalsTable;
    @FXML
    private TableColumn<DailyShopTotalDTO, String> earningsShopDateColumn;
    @FXML
    private TableColumn<DailyShopTotalDTO, Integer> earningsShopHaircutsColumn;
    @FXML
    private TableColumn<DailyShopTotalDTO, Integer> earningsShopGrossColumn;
    @FXML
    private TableColumn<DailyShopTotalDTO, Integer> earningsShopBarberColumn;
    @FXML
    private TableColumn<DailyShopTotalDTO, Integer> earningsShopNetColumn;
    @FXML
    private TableView<DailyBarberTotalDTO> earningsBarberTotalsTable;
    @FXML
    private TableColumn<DailyBarberTotalDTO, Integer> earningsBarberIdColumn;
    @FXML
    private TableColumn<DailyBarberTotalDTO, String> earningsBarberNameColumn;
    @FXML
    private TableColumn<DailyBarberTotalDTO, Integer> earningsBarberHaircutsColumn;
    @FXML
    private TableColumn<DailyBarberTotalDTO, Integer> earningsBarberGrossColumn;
    @FXML
    private TableColumn<DailyBarberTotalDTO, Integer> earningsBarberCommissionColumn;
    @FXML
    private TableColumn<DailyBarberTotalDTO, Integer> earningsBarberShopShareColumn;
    @FXML
    private TableView<PricingCategorySummaryDTO> earningsPricingSummaryTable;
    @FXML
    private TableColumn<PricingCategorySummaryDTO, Integer> earningsPricingIdColumn;
    @FXML
    private TableColumn<PricingCategorySummaryDTO, String> earningsPricingCodeColumn;
    @FXML
    private TableColumn<PricingCategorySummaryDTO, String> earningsPricingNameColumn;
    @FXML
    private TableColumn<PricingCategorySummaryDTO, Integer> earningsPricingHaircutsColumn;
    @FXML
    private TableColumn<PricingCategorySummaryDTO, Integer> earningsPricingGrossColumn;
    @FXML
    private TableColumn<PricingCategorySummaryDTO, Integer> earningsPricingCommissionColumn;
    @FXML
    private TableColumn<PricingCategorySummaryDTO, Integer> earningsPricingShopShareColumn;

    @FXML
    private TextField barberSearchField;
    @FXML
    private ComboBox<String> barberStatusFilterComboBox;
    @FXML
    private TableView<BarberModel> barbersTable;
    @FXML
    private TableColumn<BarberModel, Integer> barbersIdColumn;
    @FXML
    private TableColumn<BarberModel, String> barbersNameColumn;
    @FXML
    private TableColumn<BarberModel, Integer> barbersDisplayOrderColumn;
    @FXML
    private TableColumn<BarberModel, String> barbersStatusColumn;
    @FXML
    private ImageView barbersDetailImageView;
    @FXML
    private Label barbersDetailIdLabel;
    @FXML
    private Label barbersDetailNameLabel;
    @FXML
    private Label barbersDetailDisplayOrderLabel;
    @FXML
    private Label barbersDetailStatusLabel;
    @FXML
    private Label barbersDetailCreatedAtLabel;
    @FXML
    private Button editBarberButton;
    @FXML
    private Button toggleBarberStatusButton;

    @FXML
    private TextField pricingSearchField;
    @FXML
    private ComboBox<String> pricingStatusFilterComboBox;
    @FXML
    private TableView<PricingCategoryModel> pricingTable;
    @FXML
    private TableColumn<PricingCategoryModel, Integer> pricingIdColumn;
    @FXML
    private TableColumn<PricingCategoryModel, String> pricingCodeColumn;
    @FXML
    private TableColumn<PricingCategoryModel, String> pricingNameColumn;
    @FXML
    private TableColumn<PricingCategoryModel, Integer> pricingAmountColumn;
    @FXML
    private TableColumn<PricingCategoryModel, Integer> pricingCommissionPercentColumn;
    @FXML
    private TableColumn<PricingCategoryModel, String> pricingDefaultColumn;
    @FXML
    private TableColumn<PricingCategoryModel, String> pricingStatusColumn;
    @FXML
    private Label pricingDetailIdLabel;
    @FXML
    private Label pricingDetailCodeLabel;
    @FXML
    private Label pricingDetailNameLabel;
    @FXML
    private Label pricingDetailDescriptionLabel;
    @FXML
    private Label pricingDetailAmountLabel;
    @FXML
    private Label pricingDetailCommissionLabel;
    @FXML
    private Label pricingDetailDefaultLabel;
    @FXML
    private Label pricingDetailSortOrderLabel;
    @FXML
    private Label pricingDetailStatusLabel;
    @FXML
    private Button editPricingButton;
    @FXML
    private Button setDefaultPricingButton;
    @FXML
    private Button togglePricingStatusButton;

    @FXML
    private TextField userSearchField;
    @FXML
    private ComboBox<String> userStatusFilterComboBox;
    @FXML
    private TableView<UserModel> usersTable;
    @FXML
    private TableColumn<UserModel, Integer> usersIdColumn;
    @FXML
    private TableColumn<UserModel, String> usersUsernameColumn;
    @FXML
    private TableColumn<UserModel, String> usersRoleColumn;
    @FXML
    private TableColumn<UserModel, String> usersStatusColumn;
    @FXML
    private Label usersDetailIdLabel;
    @FXML
    private Label usersDetailUsernameLabel;
    @FXML
    private Label usersDetailRoleLabel;
    @FXML
    private Label usersDetailStatusLabel;
    @FXML
    private Label usersDetailCreatedAtLabel;
    @FXML
    private Button editUserButton;
    @FXML
    private Button toggleUserStatusButton;

    @FXML
    private Label settingsShopNameLabel;
    @FXML
    private Label settingsCurrencyCodeLabel;
    @FXML
    private Label settingsUpdatedAtLabel;

    @FXML
    private void initialize() {
        configureMainNavigation();
        configureOperationsNavigation();
        configureOverviewTable();
        configureOperationsTables();
        configureEarningsTables();
        configureBarbersPane();
        configurePricingPane();
        configureUsersPane();
        configureFilterControls();
        configureReadableTables();
        configureCharts();
        setVisiblePane(overviewPane);
        setVisibleOperationsPane(operationsOverviewPane);
    }

    public void load(App app, UserModel currentUser) {
        this.app = app;
        this.currentUser = currentUser != null ? currentUser : app.getCurrentUser();

        if (this.currentUser == null) {
            showStatus("No logged-in owner found. Please log in again.");
            return;
        }

        signedInAsLabel.setText("Signed in as " + this.currentUser.getUsername() + " (" + this.currentUser.getRole() + ")");

        if (businessDatePicker.getValue() == null) {
            businessDatePicker.setValue(LocalDate.now());
        }

        if (earningsFromDatePicker.getValue() == null) {
            earningsFromDatePicker.setValue(LocalDate.now().minusDays(6));
        }

        if (earningsToDatePicker.getValue() == null) {
            earningsToDatePicker.setValue(LocalDate.now());
        }

        overviewNavButton.setSelected(true);
        setPageTitle("Owner Overview");
        setVisiblePane(overviewPane);
        operationsOverviewToggle.setSelected(true);
        setVisibleOperationsPane(operationsOverviewPane);

        refreshAllData("Loaded owner dashboard for " + getBusinessDate() + ".");
    }

    @FXML
    private void handleShowOverview() {
        overviewNavButton.setSelected(true);
        setPageTitle("Owner Overview");
        setVisiblePane(overviewPane);
        loadOverviewData();
    }

    @FXML
    private void handleShowOperations() {
        operationsNavButton.setSelected(true);
        setPageTitle("Owner Operations");
        setVisiblePane(operationsPane);
        loadOperationsData();
    }

    @FXML
    private void handleShowEarnings() {
        earningsNavButton.setSelected(true);
        setPageTitle("Owner Earnings");
        setVisiblePane(earningsPane);
        loadEarningsData();
    }

    @FXML
    private void handleShowBarbers() {
        barbersNavButton.setSelected(true);
        setPageTitle("Owner Barbers");
        setVisiblePane(barbersPane);
        loadBarbersData();
    }

    @FXML
    private void handleShowPricing() {
        pricingNavButton.setSelected(true);
        setPageTitle("Owner Pricing");
        setVisiblePane(pricingPane);
        loadPricingData();
    }

    @FXML
    private void handleShowUsers() {
        usersNavButton.setSelected(true);
        setPageTitle("Owner Users");
        setVisiblePane(usersPane);
        loadUsersData();
    }

    @FXML
    private void handleShowSettings() {
        settingsNavButton.setSelected(true);
        setPageTitle("Owner Settings");
        setVisiblePane(settingsPane);
        loadSettingsData();
    }

    @FXML
    private void handleBusinessDateChanged() {
        if (currentUser == null) {
            return;
        }

        loadOverviewData();
        loadOperationsData();
        showStatus("Loaded owner data for " + getBusinessDate() + ".");
    }

    @FXML
    private void handleOperationsOverviewTab() {
        operationsOverviewToggle.setSelected(true);
        setVisibleOperationsPane(operationsOverviewPane);
        loadOperationsOverviewData();
    }

    @FXML
    private void handleOperationsRecordTab() {
        operationsRecordToggle.setSelected(true);
        setVisibleOperationsPane(operationsRecordPane);
        loadOperationsRecordData();
    }

    @FXML
    private void handleOperationsTransactionsTab() {
        operationsTransactionsToggle.setSelected(true);
        setVisibleOperationsPane(operationsTransactionsPane);
        loadOperationsTransactionsData();
    }

    @FXML
    private void handleApplyEarningsRange() {
        loadEarningsData();
        showStatus("Loaded earnings range " + getEarningsFromDate() + " to " + getEarningsToDate() + ".");
    }

    @FXML
    private void handleOpenOverviewNewTransaction() {
        handleShowOperations();
        operationsRecordToggle.setSelected(true);
        setVisibleOperationsPane(operationsRecordPane);

        if (operationsActiveBarbersTable.getItems().isEmpty()) {
            showStatus("Add an active barber first before recording transactions.");
            return;
        }

        if (operationsActiveBarbersTable.getSelectionModel().getSelectedItem() == null) {
            operationsActiveBarbersTable.getSelectionModel().selectFirst();
        }

        handleOpenOperationsNewTransaction();
    }

    @FXML
    private void handleOpenOverviewAddBarber() {
        handleShowBarbers();
        handleOpenCreateBarberModal();
    }

    @FXML
    private void handleOpenOverviewAddPricing() {
        handleShowPricing();
        handleOpenCreatePricingModal();
    }

    @FXML
    private void handleOpenOverviewAddManager() {
        handleShowUsers();
        handleOpenCreateUserModal();
    }

    @FXML
    private void handleOpenOverviewEditSettings() {
        handleShowSettings();
        handleOpenEditSettingsModal();
    }

    @FXML
    private void handleOpenOperationsNewTransaction() {
        BarberModel selectedBarber = operationsActiveBarbersTable.getSelectionModel().getSelectedItem();

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
                        refreshAllData(message);
                        handleShowOperations();
                        operationsRecordToggle.setSelected(true);
                        setVisibleOperationsPane(operationsRecordPane);
                        selectOperationsBarberById(selectedBarberId);
                    });
        } catch (IOException e) {
            showStatus("Failed to open new transaction modal.");
        }
    }

    @FXML
    private void handleOpenEarningsAllTransactions() {
        openTransactionsListModal(
                "Range Transactions",
                "Transactions from " + getEarningsFromDate() + " to " + getEarningsToDate(),
                () -> reportService.getTransactionViewsByDateRange(getEarningsFromDate(), getEarningsToDate()));
    }

    @FXML
    private void handleOpenCreateBarberModal() {
        try {
            ModalLoader.load_owner_barber_form_modal(app, null, this::refreshAllData);
        } catch (IOException e) {
            showStatus("Failed to open barber form.");
        }
    }

    @FXML
    private void handleOpenEditBarberModal() {
        BarberModel selectedBarber = barbersTable.getSelectionModel().getSelectedItem();
        if (selectedBarber == null) {
            showStatus("Select a barber first.");
            return;
        }

        try {
            ModalLoader.load_owner_barber_form_modal(app, selectedBarber, this::refreshAllData);
        } catch (IOException e) {
            showStatus("Failed to open barber form.");
        }
    }

    @FXML
    private void handleToggleBarberStatus() {
        BarberModel selectedBarber = barbersTable.getSelectionModel().getSelectedItem();
        if (selectedBarber == null) {
            showStatus("Select a barber first.");
            return;
        }

        int nextStatus = selectedBarber.getIsActive() == 1 ? 0 : 1;
        String actionLabel = nextStatus == 1 ? "Activate Barber" : "Deactivate Barber";

        openConfirmModal(
                actionLabel,
                selectedBarber.getName() + " will become " + (nextStatus == 1 ? "active" : "inactive") + ".",
                actionLabel,
                () -> barberService.setBarberActiveStatus(selectedBarber.getBarberId(), nextStatus));
    }

    @FXML
    private void handleOpenCreatePricingModal() {
        try {
            ModalLoader.load_owner_pricing_category_form_modal(app, null, this::refreshAllData);
        } catch (IOException e) {
            showStatus("Failed to open pricing form.");
        }
    }

    @FXML
    private void handleOpenEditPricingModal() {
        PricingCategoryModel selectedCategory = pricingTable.getSelectionModel().getSelectedItem();
        if (selectedCategory == null) {
            showStatus("Select a pricing category first.");
            return;
        }

        try {
            ModalLoader.load_owner_pricing_category_form_modal(app, selectedCategory, this::refreshAllData);
        } catch (IOException e) {
            showStatus("Failed to open pricing form.");
        }
    }

    @FXML
    private void handleSetDefaultPricing() {
        PricingCategoryModel selectedCategory = pricingTable.getSelectionModel().getSelectedItem();
        if (selectedCategory == null) {
            showStatus("Select a pricing category first.");
            return;
        }

        openConfirmModal(
                "Set Default Pricing",
                selectedCategory.getName() + " will become the default pricing category.",
                "Set Default",
                () -> pricingCategoryService.setDefaultCategory(selectedCategory.getPricingCategoryId()));
    }

    @FXML
    private void handleTogglePricingStatus() {
        PricingCategoryModel selectedCategory = pricingTable.getSelectionModel().getSelectedItem();
        if (selectedCategory == null) {
            showStatus("Select a pricing category first.");
            return;
        }

        int nextStatus = selectedCategory.getIsActive() == 1 ? 0 : 1;
        String actionLabel = nextStatus == 1 ? "Activate Pricing" : "Deactivate Pricing";

        openConfirmModal(
                actionLabel,
                selectedCategory.getName() + " will become " + (nextStatus == 1 ? "active" : "inactive") + ".",
                actionLabel,
                () -> pricingCategoryService.setCategoryActiveStatus(selectedCategory.getPricingCategoryId(), nextStatus));
    }

    @FXML
    private void handleOpenCreateUserModal() {
        try {
            ModalLoader.load_owner_user_form_modal(app, null, this::refreshAllData);
        } catch (IOException e) {
            showStatus("Failed to open user form.");
        }
    }

    @FXML
    private void handleOpenEditUserModal() {
        UserModel selectedUser = usersTable.getSelectionModel().getSelectedItem();
        if (selectedUser == null) {
            showStatus("Select a user first.");
            return;
        }

        if (UserRole.OWNER.name().equalsIgnoreCase(selectedUser.getRole())) {
            showStatus("Owner accounts are read-only in this screen.");
            return;
        }

        try {
            ModalLoader.load_owner_user_form_modal(app, selectedUser, this::refreshAllData);
        } catch (IOException e) {
            showStatus("Failed to open user form.");
        }
    }

    @FXML
    private void handleToggleUserStatus() {
        UserModel selectedUser = usersTable.getSelectionModel().getSelectedItem();
        if (selectedUser == null) {
            showStatus("Select a user first.");
            return;
        }

        if (UserRole.OWNER.name().equalsIgnoreCase(selectedUser.getRole())) {
            showStatus("Owner accounts cannot be deactivated here.");
            return;
        }

        int nextStatus = selectedUser.getIsActive() == 1 ? 0 : 1;
        String actionLabel = nextStatus == 1 ? "Activate User" : "Deactivate User";

        openConfirmModal(
                actionLabel,
                selectedUser.getUsername() + " will become " + (nextStatus == 1 ? "active" : "inactive") + ".",
                actionLabel,
                () -> userService.setUserActiveStatus(selectedUser.getId(), nextStatus));
    }

    @FXML
    private void handleOpenEditSettingsModal() {
        try {
            ModalLoader.load_owner_shop_settings_modal(app, currentSettings, this::refreshAllData);
        } catch (IOException e) {
            showStatus("Failed to open settings form.");
        }
    }

    @FXML
    private void handleLogout() {
        try {
            currentUser = null;
            app.clearCurrentUser();
            AppLoader.load_app_window(app, app.getMainStage());
        } catch (Exception e) {
            showStatus("Failed to log out.");
        }
    }

    private void refreshAllData(String message) {
        loadSettingsData();
        loadOverviewData();
        loadOperationsData();
        loadEarningsData();
        loadBarbersData();
        loadPricingData();
        loadUsersData();

        if (message != null && !message.isBlank()) {
            showStatus(message);
        }
    }

    private void loadOverviewData() {
        OwnerDashboardDTO dashboard = reportService.getOwnerDashboard(getBusinessDate());
        DailyShopTotalDTO shopTotals = dashboard.getShopTotals();
        List<PricingCategorySummaryDTO> pricingSummaries = reportService.getPricingCategorySummary(getBusinessDate(), getBusinessDate());

        overviewGreetingLabel.setText("Welcome back, " + currentUser.getUsername());
        overviewDateLabel.setText("Business Date: " + getBusinessDate());
        overviewHaircutsLabel.setText(String.valueOf(shopTotals.getHaircutCount()));
        overviewGrossSalesLabel.setText(formatCurrency(shopTotals.getGrossSales()));
        overviewBarberCommissionsLabel.setText(formatCurrency(shopTotals.getBarberCommissionTotal()));
        overviewShopEarningsLabel.setText(formatCurrency(shopTotals.getShopShareTotal()));
        populateOverviewRevenueSplitChart(shopTotals);
        populateOverviewPricingMixChart(pricingSummaries);

        List<TransactionViewDTO> recentTransactions = dashboard.getRecentTransactions();
        int startIndex = Math.max(0, recentTransactions.size() - 12);
        overviewRecentTransactions.setAll(recentTransactions.subList(startIndex, recentTransactions.size()));
        overviewRecentTransactionsTable.setItems(overviewRecentTransactions);
    }

    private void loadOperationsData() {
        loadOperationsOverviewData();
        loadOperationsRecordData();
        loadOperationsTransactionsData();
    }

    private void loadOperationsOverviewData() {
        DailyShopTotalDTO shopTotal = reportService.getDailyShopTotal(getBusinessDate());
        List<DailyBarberTotalDTO> barberTotals = reportService.getDailyBarberTotals(getBusinessDate());

        operationsTotalCutsLabel.setText("Total Cuts Today: " + shopTotal.getHaircutCount());
        operationsShopSalesLabel.setText("Shop Earnings Today: " + formatCurrency(shopTotal.getShopShareTotal()));
        operationsGrossSalesLabel.setText("Gross Sales Today: " + formatCurrency(shopTotal.getGrossSales()));
        operationsBarberCommissionListLabel.setText(buildBarberBreakdownSummary(barberTotals));
        populateOperationsBarberBreakdownChart(barberTotals);
    }

    private void loadOperationsRecordData() {
        int selectedBarberId = getSelectedOperationsBarberId();
        List<BarberModel> activeBarbers = barberService.getAllActiveBarbers();

        operationsActiveBarbersTable.setItems(FXCollections.observableArrayList(activeBarbers));

        if (selectedBarberId > 0) {
            selectOperationsBarberById(selectedBarberId);
        }

        if (operationsActiveBarbersTable.getSelectionModel().getSelectedItem() == null && !activeBarbers.isEmpty()) {
            operationsActiveBarbersTable.getSelectionModel().selectFirst();
        }

        BarberModel selectedBarber = operationsActiveBarbersTable.getSelectionModel().getSelectedItem();
        operationsNewTransactionButton.setDisable(selectedBarber == null);
        refreshOperationsSelectedBarberSection(selectedBarber);
    }

    private void loadOperationsTransactionsData() {
        operationsAllTransactions.setAll(reportService.getTransactionViewsByBusinessDate(getBusinessDate()));
        applyOperationsTransactionsFilter();
    }

    private void loadEarningsData() {
        String fromDate = getEarningsFromDate();
        String toDate = getEarningsToDate();

        if (fromDate.compareTo(toDate) > 0) {
            earningsRangeLabel.setText("Select a valid date range.");
            earningsHaircutsLabel.setText("0");
            earningsGrossSalesLabel.setText(formatCurrency(0));
            earningsBarberCommissionsLabel.setText(formatCurrency(0));
            earningsShopEarningsLabel.setText(formatCurrency(0));
            earningsDailyShopTotals.clear();
            earningsBarberTotals.clear();
            earningsPricingSummaries.clear();
            return;
        }

        List<DailyShopTotalDTO> dailyShopTotals = reportService.getDateRangeShopTotals(fromDate, toDate);
        List<DailyBarberTotalDTO> rawBarberTotals = reportService.getDateRangeBarberTotals(fromDate, toDate);
        List<PricingCategorySummaryDTO> pricingSummaries = reportService.getPricingCategorySummary(fromDate, toDate);

        earningsDailyShopTotals.setAll(dailyShopTotals);
        earningsBarberTotals.setAll(aggregateBarberTotals(rawBarberTotals));
        earningsPricingSummaries.setAll(pricingSummaries);

        int totalHaircuts = dailyShopTotals.stream().mapToInt(DailyShopTotalDTO::getHaircutCount).sum();
        int totalGross = dailyShopTotals.stream().mapToInt(DailyShopTotalDTO::getGrossSales).sum();
        int totalBarber = dailyShopTotals.stream().mapToInt(DailyShopTotalDTO::getBarberCommissionTotal).sum();
        int totalShop = dailyShopTotals.stream().mapToInt(DailyShopTotalDTO::getShopShareTotal).sum();

        earningsRangeLabel.setText("Range: " + fromDate + " to " + toDate);
        earningsHaircutsLabel.setText(String.valueOf(totalHaircuts));
        earningsGrossSalesLabel.setText(formatCurrency(totalGross));
        earningsBarberCommissionsLabel.setText(formatCurrency(totalBarber));
        earningsShopEarningsLabel.setText(formatCurrency(totalShop));
    }

    private void loadBarbersData() {
        int selectedBarberId = getSelectedBarberId();
        allBarbers.setAll(barberService.getAllBarbers());
        applyBarberFilter();

        if (selectedBarberId > 0) {
            selectBarberById(selectedBarberId);
        }

        if (barbersTable.getSelectionModel().getSelectedItem() == null && !barbersTable.getItems().isEmpty()) {
            barbersTable.getSelectionModel().selectFirst();
        }

        refreshBarberDetails(barbersTable.getSelectionModel().getSelectedItem());
    }

    private void loadPricingData() {
        int selectedCategoryId = getSelectedPricingCategoryId();
        allPricingCategories.setAll(pricingCategoryService.getAllCategories());
        applyPricingFilter();

        if (selectedCategoryId > 0) {
            selectPricingCategoryById(selectedCategoryId);
        }

        if (pricingTable.getSelectionModel().getSelectedItem() == null && !pricingTable.getItems().isEmpty()) {
            pricingTable.getSelectionModel().selectFirst();
        }

        refreshPricingDetails(pricingTable.getSelectionModel().getSelectedItem());
    }

    private void loadUsersData() {
        int selectedUserId = getSelectedUserId();
        allUsers.setAll(userService.getAllUsers());
        applyUserFilter();

        if (selectedUserId > 0) {
            selectUserById(selectedUserId);
        }

        if (usersTable.getSelectionModel().getSelectedItem() == null && !usersTable.getItems().isEmpty()) {
            usersTable.getSelectionModel().selectFirst();
        }

        refreshUserDetails(usersTable.getSelectionModel().getSelectedItem());
    }

    private void loadSettingsData() {
        currentSettings = shopSettingsService.getSettings();

        if (currentSettings == null) {
            settingsShopNameLabel.setText("Shop Name: (missing)");
            settingsCurrencyCodeLabel.setText("Currency: PHP");
            settingsUpdatedAtLabel.setText("Updated At: (missing)");
            return;
        }

        settingsShopNameLabel.setText("Shop Name: " + valueOrPlaceholder(currentSettings.getShopName()));
        settingsCurrencyCodeLabel.setText("Currency: " + valueOrPlaceholder(currentSettings.getCurrencyCode()));
        settingsUpdatedAtLabel.setText("Updated At: " + valueOrPlaceholder(currentSettings.getUpdatedAt()));
    }

    private void configureMainNavigation() {
        overviewNavButton.setToggleGroup(mainNavigationGroup);
        operationsNavButton.setToggleGroup(mainNavigationGroup);
        earningsNavButton.setToggleGroup(mainNavigationGroup);
        barbersNavButton.setToggleGroup(mainNavigationGroup);
        pricingNavButton.setToggleGroup(mainNavigationGroup);
        usersNavButton.setToggleGroup(mainNavigationGroup);
        settingsNavButton.setToggleGroup(mainNavigationGroup);

        mainNavigationGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null && oldValue != null) {
                oldValue.setSelected(true);
            }
        });
    }

    private void configureOperationsNavigation() {
        operationsOverviewToggle.setToggleGroup(operationsNavigationGroup);
        operationsRecordToggle.setToggleGroup(operationsNavigationGroup);
        operationsTransactionsToggle.setToggleGroup(operationsNavigationGroup);

        operationsNavigationGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null && oldValue != null) {
                oldValue.setSelected(true);
            }
        });
    }

    private void configureOverviewTable() {
        overviewTransactionIdColumn.setCellValueFactory(new PropertyValueFactory<>("transactionId"));
        overviewTransactionBarberColumn.setCellValueFactory(new PropertyValueFactory<>("barberName"));
        overviewTransactionCategoryColumn.setCellValueFactory(new PropertyValueFactory<>("pricingCategoryName"));
        overviewTransactionAmountColumn.setCellValueFactory(new PropertyValueFactory<>("chargedAmount"));
        overviewTransactionStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        configureCurrencyColumn(overviewTransactionAmountColumn);

        overviewRecentTransactionsTable.setRowFactory(table -> {
            TableRow<TransactionViewDTO> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (row.isEmpty() || event.getClickCount() != 1) {
                    return;
                }

                openTransactionDetailModal(row.getItem());
            });
            return row;
        });
    }

    private void configureOperationsTables() {
        operationsActiveBarberIdColumn.setCellValueFactory(new PropertyValueFactory<>("barberId"));
        operationsActiveBarberNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        operationsActiveBarbersTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            operationsNewTransactionButton.setDisable(newValue == null);
            refreshOperationsSelectedBarberSection(newValue);
        });

        operationsSelectedTransactionIdColumn.setCellValueFactory(new PropertyValueFactory<>("transactionId"));
        configureTextColumn(operationsSelectedPricingCategoryIdColumn,
                transaction -> valueOrPlaceholder(transaction.getPricingCategoryName()));
        configureTextColumn(operationsSelectedLoggedByUserIdColumn,
                transaction -> valueOrPlaceholder(transaction.getLoggedByUsername()));
        operationsSelectedBusinessDateColumn.setCellValueFactory(new PropertyValueFactory<>("businessDate"));
        operationsSelectedRecordedAtColumn.setCellValueFactory(new PropertyValueFactory<>("recordedAt"));
        operationsSelectedPricingCategoryNameColumn.setCellValueFactory(new PropertyValueFactory<>("pricingCategoryName"));
        operationsSelectedChargedAmountColumn.setCellValueFactory(new PropertyValueFactory<>("chargedAmount"));
        operationsSelectedBarberCommissionPercentColumn.setCellValueFactory(new PropertyValueFactory<>("barberCommissionPercent"));
        operationsSelectedStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        operationsSelectedVoidReasonColumn.setCellValueFactory(new PropertyValueFactory<>("voidReason"));
        operationsSelectedNoteColumn.setCellValueFactory(new PropertyValueFactory<>("note"));
        configureCurrencyColumn(operationsSelectedChargedAmountColumn);
        configurePercentColumn(operationsSelectedBarberCommissionPercentColumn);

        operationsSelectedTransactionIdColumn.setPrefWidth(55);
        operationsSelectedPricingCategoryIdColumn.setText("Category");
        operationsSelectedPricingCategoryIdColumn.setPrefWidth(125);
        operationsSelectedLoggedByUserIdColumn.setText("Logged By");
        operationsSelectedLoggedByUserIdColumn.setPrefWidth(95);
        operationsSelectedBusinessDateColumn.setVisible(false);
        operationsSelectedRecordedAtColumn.setText("Recorded");
        operationsSelectedRecordedAtColumn.setPrefWidth(105);
        operationsSelectedPricingCategoryNameColumn.setVisible(false);
        operationsSelectedChargedAmountColumn.setPrefWidth(80);
        operationsSelectedBarberCommissionPercentColumn.setVisible(false);
        operationsSelectedStatusColumn.setPrefWidth(70);
        operationsSelectedVoidReasonColumn.setVisible(false);
        operationsSelectedNoteColumn.setVisible(false);

        operationsSelectedBarberTransactionsTable.setRowFactory(table -> createTransactionRowFactory());

        operationsTransactionsIdColumn.setCellValueFactory(new PropertyValueFactory<>("transactionId"));
        configureTextColumn(operationsTransactionsBarberIdColumn,
                transaction -> valueOrPlaceholder(transaction.getBarberName()));
        configureTextColumn(operationsTransactionsPricingCategoryIdColumn,
                transaction -> valueOrPlaceholder(transaction.getPricingCategoryName()));
        configureTextColumn(operationsTransactionsLoggedByUserIdColumn,
                transaction -> valueOrPlaceholder(transaction.getLoggedByUsername()));
        operationsTransactionsBusinessDateColumn.setCellValueFactory(new PropertyValueFactory<>("businessDate"));
        operationsTransactionsRecordedAtColumn.setCellValueFactory(new PropertyValueFactory<>("recordedAt"));
        operationsTransactionsPricingCategoryNameColumn.setCellValueFactory(new PropertyValueFactory<>("pricingCategoryName"));
        operationsTransactionsShopEarningAmountColumn.setCellValueFactory(new PropertyValueFactory<>("shopEarningAmount"));
        operationsTransactionsStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        configureCurrencyColumn(operationsTransactionsShopEarningAmountColumn);

        operationsTransactionsIdColumn.setPrefWidth(55);
        operationsTransactionsBarberIdColumn.setText("Barber");
        operationsTransactionsBarberIdColumn.setPrefWidth(130);
        operationsTransactionsPricingCategoryIdColumn.setText("Category");
        operationsTransactionsPricingCategoryIdColumn.setPrefWidth(155);
        operationsTransactionsLoggedByUserIdColumn.setText("Logged By");
        operationsTransactionsLoggedByUserIdColumn.setPrefWidth(105);
        operationsTransactionsBusinessDateColumn.setText("Date");
        operationsTransactionsBusinessDateColumn.setPrefWidth(95);
        operationsTransactionsRecordedAtColumn.setVisible(false);
        operationsTransactionsPricingCategoryNameColumn.setVisible(false);
        operationsTransactionsShopEarningAmountColumn.setText("Shop");
        operationsTransactionsShopEarningAmountColumn.setPrefWidth(95);
        operationsTransactionsStatusColumn.setText("Status");
        operationsTransactionsStatusColumn.setPrefWidth(75);

        operationsTransactionsTable.setRowFactory(table -> createTransactionRowFactory());
    }

    private void configureEarningsTables() {
        earningsShopDateColumn.setCellValueFactory(new PropertyValueFactory<>("businessDate"));
        earningsShopHaircutsColumn.setCellValueFactory(new PropertyValueFactory<>("haircutCount"));
        earningsShopGrossColumn.setCellValueFactory(new PropertyValueFactory<>("grossSales"));
        earningsShopBarberColumn.setCellValueFactory(new PropertyValueFactory<>("barberCommissionTotal"));
        earningsShopNetColumn.setCellValueFactory(new PropertyValueFactory<>("shopShareTotal"));
        configureCurrencyColumn(earningsShopGrossColumn);
        configureCurrencyColumn(earningsShopBarberColumn);
        configureCurrencyColumn(earningsShopNetColumn);

        earningsShopTotalsTable.setItems(earningsDailyShopTotals);
        earningsShopTotalsTable.setRowFactory(table -> {
            TableRow<DailyShopTotalDTO> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (row.isEmpty() || event.getClickCount() != 1) {
                    return;
                }

                DailyShopTotalDTO total = row.getItem();
                openTransactionsListModal(
                        "Transactions on " + total.getBusinessDate(),
                        "All transactions recorded on " + total.getBusinessDate(),
                        () -> reportService.getTransactionViewsByBusinessDate(total.getBusinessDate()));
            });
            return row;
        });

        earningsBarberIdColumn.setCellValueFactory(new PropertyValueFactory<>("barberId"));
        earningsBarberNameColumn.setCellValueFactory(new PropertyValueFactory<>("barberName"));
        earningsBarberHaircutsColumn.setCellValueFactory(new PropertyValueFactory<>("haircutCount"));
        earningsBarberGrossColumn.setCellValueFactory(new PropertyValueFactory<>("grossSales"));
        earningsBarberCommissionColumn.setCellValueFactory(new PropertyValueFactory<>("barberCommissionTotal"));
        earningsBarberShopShareColumn.setCellValueFactory(new PropertyValueFactory<>("shopShareTotal"));
        configureCurrencyColumn(earningsBarberGrossColumn);
        configureCurrencyColumn(earningsBarberCommissionColumn);
        configureCurrencyColumn(earningsBarberShopShareColumn);
        earningsBarberIdColumn.setVisible(false);

        earningsBarberTotalsTable.setItems(earningsBarberTotals);
        earningsBarberTotalsTable.setRowFactory(table -> {
            TableRow<DailyBarberTotalDTO> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (row.isEmpty() || event.getClickCount() != 1) {
                    return;
                }

                DailyBarberTotalDTO total = row.getItem();
                openTransactionsListModal(
                        "Transactions for " + total.getBarberName(),
                        total.getBarberName() + " from " + getEarningsFromDate() + " to " + getEarningsToDate(),
                        () -> reportService.getTransactionViewsByBarberAndDateRange(
                                total.getBarberId(),
                                getEarningsFromDate(),
                                getEarningsToDate()));
            });
            return row;
        });

        earningsPricingIdColumn.setCellValueFactory(new PropertyValueFactory<>("pricingCategoryId"));
        earningsPricingCodeColumn.setCellValueFactory(new PropertyValueFactory<>("code"));
        earningsPricingNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        earningsPricingHaircutsColumn.setCellValueFactory(new PropertyValueFactory<>("haircutCount"));
        earningsPricingGrossColumn.setCellValueFactory(new PropertyValueFactory<>("grossSales"));
        earningsPricingCommissionColumn.setCellValueFactory(new PropertyValueFactory<>("barberCommissionTotal"));
        earningsPricingShopShareColumn.setCellValueFactory(new PropertyValueFactory<>("shopShareTotal"));
        configureCurrencyColumn(earningsPricingGrossColumn);
        configureCurrencyColumn(earningsPricingCommissionColumn);
        configureCurrencyColumn(earningsPricingShopShareColumn);
        earningsPricingIdColumn.setVisible(false);

        earningsPricingSummaryTable.setItems(earningsPricingSummaries);
        earningsPricingSummaryTable.setRowFactory(table -> {
            TableRow<PricingCategorySummaryDTO> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (row.isEmpty() || event.getClickCount() != 1) {
                    return;
                }

                PricingCategorySummaryDTO summary = row.getItem();
                openTransactionsListModal(
                        "Transactions for " + summary.getName(),
                        summary.getName() + " from " + getEarningsFromDate() + " to " + getEarningsToDate(),
                        () -> reportService.getTransactionViewsByPricingCategoryAndDateRange(
                                summary.getPricingCategoryId(),
                                getEarningsFromDate(),
                                getEarningsToDate()));
            });
            return row;
        });
    }

    private void configureBarbersPane() {
        barbersIdColumn.setCellValueFactory(new PropertyValueFactory<>("barberId"));
        barbersNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        barbersDisplayOrderColumn.setCellValueFactory(new PropertyValueFactory<>("displayOrder"));
        barbersStatusColumn.setCellValueFactory(cellData -> new SimpleStringProperty(statusText(cellData.getValue().getIsActive())));

        barbersTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            refreshBarberDetails(newValue);
        });
    }

    private void configurePricingPane() {
        pricingIdColumn.setCellValueFactory(new PropertyValueFactory<>("pricingCategoryId"));
        pricingCodeColumn.setCellValueFactory(new PropertyValueFactory<>("code"));
        pricingNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        pricingAmountColumn.setCellValueFactory(new PropertyValueFactory<>("chargedAmountPesos"));
        pricingCommissionPercentColumn.setCellValueFactory(new PropertyValueFactory<>("barberCommissionPercent"));
        pricingDefaultColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getIsDefault() == 1 ? "Yes" : "No"));
        pricingStatusColumn.setCellValueFactory(cellData -> new SimpleStringProperty(statusText(cellData.getValue().getIsActive())));
        configureCurrencyColumn(pricingAmountColumn);
        configurePercentColumn(pricingCommissionPercentColumn);

        pricingIdColumn.setVisible(false);
        pricingCodeColumn.setPrefWidth(80);
        pricingNameColumn.setPrefWidth(155);
        pricingAmountColumn.setPrefWidth(90);
        pricingCommissionPercentColumn.setVisible(false);
        pricingDefaultColumn.setVisible(false);
        pricingStatusColumn.setPrefWidth(90);

        pricingTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            refreshPricingDetails(newValue);
        });
    }

    private void configureUsersPane() {
        usersIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        usersUsernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        usersRoleColumn.setCellValueFactory(new PropertyValueFactory<>("role"));
        usersStatusColumn.setCellValueFactory(cellData -> new SimpleStringProperty(statusText(cellData.getValue().getIsActive())));

        usersTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            refreshUserDetails(newValue);
        });
    }

    private void configureFilterControls() {
        operationsFilterComboBox.setItems(FXCollections.observableArrayList("ALL", "POSTED", "VOID"));
        operationsFilterComboBox.getSelectionModel().selectFirst();
        operationsSearchField.textProperty().addListener((observable, oldValue, newValue) -> applyOperationsTransactionsFilter());
        operationsFilterComboBox.valueProperty().addListener((observable, oldValue, newValue) -> applyOperationsTransactionsFilter());

        barberStatusFilterComboBox.setItems(FXCollections.observableArrayList("ALL", "ACTIVE", "INACTIVE"));
        barberStatusFilterComboBox.getSelectionModel().selectFirst();
        barberSearchField.textProperty().addListener((observable, oldValue, newValue) -> applyBarberFilter());
        barberStatusFilterComboBox.valueProperty().addListener((observable, oldValue, newValue) -> applyBarberFilter());

        pricingStatusFilterComboBox.setItems(FXCollections.observableArrayList("ALL", "ACTIVE", "INACTIVE"));
        pricingStatusFilterComboBox.getSelectionModel().selectFirst();
        pricingSearchField.textProperty().addListener((observable, oldValue, newValue) -> applyPricingFilter());
        pricingStatusFilterComboBox.valueProperty().addListener((observable, oldValue, newValue) -> applyPricingFilter());

        userStatusFilterComboBox.setItems(FXCollections.observableArrayList("ALL", "ACTIVE", "INACTIVE"));
        userStatusFilterComboBox.getSelectionModel().selectFirst();
        userSearchField.textProperty().addListener((observable, oldValue, newValue) -> applyUserFilter());
        userStatusFilterComboBox.valueProperty().addListener((observable, oldValue, newValue) -> applyUserFilter());
    }

    private void configureReadableTables() {
        for (TableView<?> table : List.of(
                overviewRecentTransactionsTable,
                operationsActiveBarbersTable,
                operationsSelectedBarberTransactionsTable,
                operationsTransactionsTable,
                earningsShopTotalsTable,
                earningsBarberTotalsTable,
                earningsPricingSummaryTable,
                barbersTable,
                pricingTable,
                usersTable)) {
            configureReadableTable(table);
        }
    }

    private void configureReadableTable(TableView<?> table) {
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setFixedCellSize(28);
        table.setStyle("-fx-font-size: 13px;");
    }

    private void configureCharts() {
        overviewRevenueSplitChart.setLabelsVisible(true);
        overviewRevenueSplitChart.setLegendVisible(true);
        overviewRevenueSplitChart.setClockwise(true);

        overviewPricingMixChart.setAnimated(false);
        overviewPricingMixChart.setLegendVisible(false);
        overviewPricingMixChart.setHorizontalGridLinesVisible(false);
        overviewPricingMixChart.setVerticalGridLinesVisible(false);
        overviewPricingMixChart.setAlternativeColumnFillVisible(false);
        overviewPricingMixChart.setAlternativeRowFillVisible(false);
        overviewPricingMixCategoryAxis.setLabel("Pricing Category");
        overviewPricingMixValueAxis.setLabel("Gross Sales");
        overviewPricingMixValueAxis.setForceZeroInRange(true);

        operationsBarberBreakdownChart.setAnimated(false);
        operationsBarberBreakdownChart.setLegendVisible(true);
        operationsBarberBreakdownChart.setHorizontalGridLinesVisible(false);
        operationsBarberBreakdownChart.setVerticalGridLinesVisible(false);
        operationsBarberBreakdownChart.setAlternativeColumnFillVisible(false);
        operationsBarberBreakdownChart.setAlternativeRowFillVisible(false);
        operationsBarberBreakdownCategoryAxis.setLabel("Barber");
        operationsBarberBreakdownValueAxis.setLabel("Amount");
        operationsBarberBreakdownValueAxis.setForceZeroInRange(true);
    }

    private void refreshOperationsSelectedBarberSection(BarberModel selectedBarber) {
        if (selectedBarber == null) {
            operationsSelectedBarberIdLabel.setText("Selected Barber ID: -");
            setBarberPreviewImage(operationsSelectedBarberImageView, null);
            operationsSelectedBarberNameLabel.setText("Selected Barber Name: -");
            operationsSelectedBarberHaircutCountLabel.setText("Haircut Count Today: 0");
            operationsSelectedBarberCommissionLabel.setText("Commission Today: " + formatCurrency(0));
            operationsSelectedBarberShopShareLabel.setText("Shop Share Today: " + formatCurrency(0));
            operationsSelectedBarberTransactionsTable.setItems(FXCollections.observableArrayList());
            return;
        }

        String businessDate = getBusinessDate();
        DailyBarberTotalDTO card = reportService.getDailyBarberTotals(businessDate).stream()
                .filter(item -> item.getBarberId() == selectedBarber.getBarberId())
                .findFirst()
                .orElse(new DailyBarberTotalDTO(businessDate, selectedBarber.getBarberId(), selectedBarber.getName(), 0, 0, 0, 0));

        operationsSelectedBarberIdLabel.setText("Selected Barber ID: " + selectedBarber.getBarberId());
        setBarberPreviewImage(operationsSelectedBarberImageView, selectedBarber.getImagePath());
        operationsSelectedBarberNameLabel.setText("Selected Barber Name: " + selectedBarber.getName());
        operationsSelectedBarberHaircutCountLabel.setText("Haircut Count Today: " + card.getHaircutCount());
        operationsSelectedBarberCommissionLabel.setText("Commission Today: " + formatCurrency(card.getBarberCommissionTotal()));
        operationsSelectedBarberShopShareLabel.setText("Shop Share Today: " + formatCurrency(card.getShopShareTotal()));

        operationsSelectedBarberTransactionsTable.setItems(FXCollections.observableArrayList(
                reportService.getTransactionViewsByBarberAndBusinessDate(selectedBarber.getBarberId(), businessDate)));
    }

    private void refreshBarberDetails(BarberModel barber) {
        editBarberButton.setDisable(barber == null);
        toggleBarberStatusButton.setDisable(barber == null);

        if (barber == null) {
            setBarberPreviewImage(barbersDetailImageView, null);
            barbersDetailIdLabel.setText("ID: -");
            barbersDetailNameLabel.setText("Name: -");
            barbersDetailDisplayOrderLabel.setText("Display Order: -");
            barbersDetailStatusLabel.setText("Status: -");
            barbersDetailCreatedAtLabel.setText("Created At: -");
            toggleBarberStatusButton.setText("Toggle Status");
            return;
        }

        setBarberPreviewImage(barbersDetailImageView, barber.getImagePath());
        barbersDetailIdLabel.setText("ID: " + barber.getBarberId());
        barbersDetailNameLabel.setText("Name: " + barber.getName());
        barbersDetailDisplayOrderLabel.setText("Display Order: " + barber.getDisplayOrder());
        barbersDetailStatusLabel.setText("Status: " + statusText(barber.getIsActive()));
        barbersDetailCreatedAtLabel.setText("Created At: " + valueOrPlaceholder(barber.getCreatedAt()));
        toggleBarberStatusButton.setText(barber.getIsActive() == 1 ? "Deactivate" : "Activate");
    }

    private void refreshPricingDetails(PricingCategoryModel category) {
        editPricingButton.setDisable(category == null);
        setDefaultPricingButton.setDisable(category == null || category.getIsDefault() == 1);
        togglePricingStatusButton.setDisable(category == null);

        if (category == null) {
            pricingDetailIdLabel.setText("ID: -");
            pricingDetailCodeLabel.setText("Code: -");
            pricingDetailNameLabel.setText("Name: -");
            pricingDetailDescriptionLabel.setText("Description: -");
            pricingDetailAmountLabel.setText("Charged Amount: " + formatCurrency(0));
            pricingDetailCommissionLabel.setText("Commission Percent: -");
            pricingDetailDefaultLabel.setText("Default: -");
            pricingDetailSortOrderLabel.setText("Sort Order: -");
            pricingDetailStatusLabel.setText("Status: -");
            togglePricingStatusButton.setText("Toggle Status");
            return;
        }

        pricingDetailIdLabel.setText("ID: " + category.getPricingCategoryId());
        pricingDetailCodeLabel.setText("Code: " + category.getCode());
        pricingDetailNameLabel.setText("Name: " + category.getName());
        pricingDetailDescriptionLabel.setText("Description: " + valueOrPlaceholder(category.getDescription()));
        pricingDetailAmountLabel.setText("Charged Amount: " + formatCurrency(category.getChargedAmountPesos()));
        pricingDetailCommissionLabel.setText("Commission Percent: " + category.getBarberCommissionPercent() + "%");
        pricingDetailDefaultLabel.setText("Default: " + (category.getIsDefault() == 1 ? "Yes" : "No"));
        pricingDetailSortOrderLabel.setText("Sort Order: " + category.getSortOrder());
        pricingDetailStatusLabel.setText("Status: " + statusText(category.getIsActive()));
        togglePricingStatusButton.setText(category.getIsActive() == 1 ? "Deactivate" : "Activate");
    }

    private void refreshUserDetails(UserModel user) {
        boolean isOwner = user != null && UserRole.OWNER.name().equalsIgnoreCase(user.getRole());
        editUserButton.setDisable(user == null || isOwner);
        toggleUserStatusButton.setDisable(user == null || isOwner);

        if (user == null) {
            usersDetailIdLabel.setText("ID: -");
            usersDetailUsernameLabel.setText("Username: -");
            usersDetailRoleLabel.setText("Role: -");
            usersDetailStatusLabel.setText("Status: -");
            usersDetailCreatedAtLabel.setText("Created At: -");
            toggleUserStatusButton.setText("Toggle Status");
            return;
        }

        usersDetailIdLabel.setText("ID: " + user.getId());
        usersDetailUsernameLabel.setText("Username: " + user.getUsername());
        usersDetailRoleLabel.setText("Role: " + user.getRole());
        usersDetailStatusLabel.setText("Status: " + statusText(user.getIsActive()));
        usersDetailCreatedAtLabel.setText("Created At: " + valueOrPlaceholder(user.getCreatedAt()));
        toggleUserStatusButton.setText(user.getIsActive() == 1 ? "Deactivate" : "Activate");
    }

    private void applyOperationsTransactionsFilter() {
        String searchTerm = valueOrEmpty(operationsSearchField.getText()).toLowerCase(Locale.ROOT);
        String selectedStatus = operationsFilterComboBox.getValue();

        List<TransactionViewDTO> filteredTransactions = operationsAllTransactions.stream()
                .filter(transaction -> matchesStatus(transaction.getStatus(), selectedStatus))
                .filter(transaction -> matchesTransactionSearch(transaction, searchTerm))
                .collect(Collectors.toList());

        operationsTransactionsTable.setItems(FXCollections.observableArrayList(filteredTransactions));
    }

    private void applyBarberFilter() {
        String searchTerm = valueOrEmpty(barberSearchField.getText()).toLowerCase(Locale.ROOT);
        String statusFilter = barberStatusFilterComboBox.getValue();

        List<BarberModel> filteredBarbers = allBarbers.stream()
                .filter(barber -> matchesStatus(statusText(barber.getIsActive()), statusFilter))
                .filter(barber -> barber.getName().toLowerCase(Locale.ROOT).contains(searchTerm)
                        || valueOrEmpty(barber.getImagePath()).toLowerCase(Locale.ROOT).contains(searchTerm)
                        || String.valueOf(barber.getBarberId()).contains(searchTerm))
                .collect(Collectors.toList());

        barbersTable.setItems(FXCollections.observableArrayList(filteredBarbers));
    }

    private void applyPricingFilter() {
        String searchTerm = valueOrEmpty(pricingSearchField.getText()).toLowerCase(Locale.ROOT);
        String statusFilter = pricingStatusFilterComboBox.getValue();

        List<PricingCategoryModel> filteredCategories = allPricingCategories.stream()
                .filter(category -> matchesStatus(statusText(category.getIsActive()), statusFilter))
                .filter(category -> valueOrEmpty(category.getCode()).toLowerCase(Locale.ROOT).contains(searchTerm)
                        || valueOrEmpty(category.getName()).toLowerCase(Locale.ROOT).contains(searchTerm)
                        || valueOrEmpty(category.getDescription()).toLowerCase(Locale.ROOT).contains(searchTerm)
                        || String.valueOf(category.getPricingCategoryId()).contains(searchTerm))
                .collect(Collectors.toList());

        pricingTable.setItems(FXCollections.observableArrayList(filteredCategories));
    }

    private void applyUserFilter() {
        String searchTerm = valueOrEmpty(userSearchField.getText()).toLowerCase(Locale.ROOT);
        String statusFilter = userStatusFilterComboBox.getValue();

        List<UserModel> filteredUsers = allUsers.stream()
                .filter(user -> matchesStatus(statusText(user.getIsActive()), statusFilter))
                .filter(user -> valueOrEmpty(user.getUsername()).toLowerCase(Locale.ROOT).contains(searchTerm)
                        || valueOrEmpty(user.getRole()).toLowerCase(Locale.ROOT).contains(searchTerm)
                        || String.valueOf(user.getId()).contains(searchTerm))
                .collect(Collectors.toList());

        usersTable.setItems(FXCollections.observableArrayList(filteredUsers));
    }

    private void openTransactionDetailModal(TransactionViewDTO transaction) {
        if (transaction == null) {
            return;
        }

        try {
            ModalLoader.load_transaction_detail_modal(app, currentUser, transaction, this::refreshAllData);
        } catch (IOException e) {
            showStatus("Failed to open transaction detail modal.");
        }
    }

    private void openTransactionsListModal(String title, String summary, Supplier<List<TransactionViewDTO>> reloadSupplier) {
        try {
            ModalLoader.load_owner_transactions_list_modal(
                    app,
                    currentUser,
                    title,
                    summary,
                    reloadSupplier,
                    this::refreshAllData);
        } catch (IOException e) {
            showStatus("Failed to open transactions list modal.");
        }
    }

    private void openConfirmModal(String title, String summary, String confirmButtonText,
            Supplier<ServiceResult<?>> confirmAction) {
        try {
            ModalLoader.load_owner_confirm_action_modal(
                    app,
                    title,
                    summary,
                    confirmButtonText,
                    confirmAction,
                    this::refreshAllData);
        } catch (IOException e) {
            showStatus("Failed to open confirmation modal.");
        }
    }

    private TableRow<TransactionViewDTO> createTransactionRowFactory() {
        TableRow<TransactionViewDTO> row = new TableRow<>();
        row.setOnMouseClicked(event -> {
            if (row.isEmpty() || event.getClickCount() != 1) {
                return;
            }

            openTransactionDetailModal(row.getItem());
        });
        return row;
    }

    private List<DailyBarberTotalDTO> aggregateBarberTotals(List<DailyBarberTotalDTO> rawTotals) {
        Map<Integer, DailyBarberTotalDTO> aggregatedTotals = new LinkedHashMap<>();

        rawTotals.stream()
                .sorted(Comparator.comparing(DailyBarberTotalDTO::getBarberName)
                        .thenComparing(DailyBarberTotalDTO::getBarberId))
                .forEach(total -> aggregatedTotals.compute(total.getBarberId(), (barberId, existing) -> {
                    if (existing == null) {
                        return new DailyBarberTotalDTO(
                                getEarningsFromDate() + " to " + getEarningsToDate(),
                                total.getBarberId(),
                                total.getBarberName(),
                                total.getHaircutCount(),
                                total.getGrossSales(),
                                total.getBarberCommissionTotal(),
                                total.getShopShareTotal());
                    }

                    existing.setHaircutCount(existing.getHaircutCount() + total.getHaircutCount());
                    existing.setGrossSales(existing.getGrossSales() + total.getGrossSales());
                    existing.setBarberCommissionTotal(
                            existing.getBarberCommissionTotal() + total.getBarberCommissionTotal());
                    existing.setShopShareTotal(existing.getShopShareTotal() + total.getShopShareTotal());
                    return existing;
                }));

        return aggregatedTotals.values().stream().toList();
    }

    private int getSelectedOperationsBarberId() {
        BarberModel selectedBarber = operationsActiveBarbersTable.getSelectionModel().getSelectedItem();
        return selectedBarber == null ? -1 : selectedBarber.getBarberId();
    }

    private int getSelectedBarberId() {
        BarberModel selectedBarber = barbersTable.getSelectionModel().getSelectedItem();
        return selectedBarber == null ? -1 : selectedBarber.getBarberId();
    }

    private int getSelectedPricingCategoryId() {
        PricingCategoryModel selectedCategory = pricingTable.getSelectionModel().getSelectedItem();
        return selectedCategory == null ? -1 : selectedCategory.getPricingCategoryId();
    }

    private int getSelectedUserId() {
        UserModel selectedUser = usersTable.getSelectionModel().getSelectedItem();
        return selectedUser == null ? -1 : selectedUser.getId();
    }

    private void selectOperationsBarberById(int barberId) {
        if (barberId <= 0) {
            return;
        }

        for (BarberModel barber : operationsActiveBarbersTable.getItems()) {
            if (barber.getBarberId() == barberId) {
                operationsActiveBarbersTable.getSelectionModel().select(barber);
                operationsActiveBarbersTable.scrollTo(barber);
                return;
            }
        }
    }

    private void selectBarberById(int barberId) {
        if (barberId <= 0) {
            return;
        }

        for (BarberModel barber : barbersTable.getItems()) {
            if (barber.getBarberId() == barberId) {
                barbersTable.getSelectionModel().select(barber);
                barbersTable.scrollTo(barber);
                return;
            }
        }
    }

    private void selectPricingCategoryById(int pricingCategoryId) {
        if (pricingCategoryId <= 0) {
            return;
        }

        for (PricingCategoryModel category : pricingTable.getItems()) {
            if (category.getPricingCategoryId() == pricingCategoryId) {
                pricingTable.getSelectionModel().select(category);
                pricingTable.scrollTo(category);
                return;
            }
        }
    }

    private void selectUserById(int userId) {
        if (userId <= 0) {
            return;
        }

        for (UserModel user : usersTable.getItems()) {
            if (user.getId() == userId) {
                usersTable.getSelectionModel().select(user);
                usersTable.scrollTo(user);
                return;
            }
        }
    }

    private boolean matchesTransactionSearch(TransactionViewDTO transaction, String searchTerm) {
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

    private boolean matchesStatus(String statusValue, String selectedStatus) {
        if (selectedStatus == null || "ALL".equalsIgnoreCase(selectedStatus)) {
            return true;
        }

        return selectedStatus.equalsIgnoreCase(statusValue);
    }

    private void setVisiblePane(Node targetPane) {
        List<Node> panes = List.of(
                overviewPane,
                operationsPane,
                earningsPane,
                barbersPane,
                pricingPane,
                usersPane,
                settingsPane);
        Node currentPane = panes.stream()
                .filter(Node::isVisible)
                .findFirst()
                .orElse(null);

        panes.stream()
                .filter(pane -> pane != currentPane && pane != targetPane)
                .forEach(pane -> setPaneState(pane, false));

        AnimationSupport.switchVisiblePane(currentPane, targetPane);
    }

    private void setVisibleOperationsPane(Node targetPane) {
        List<Node> panes = List.of(
                operationsOverviewPane,
                operationsRecordPane,
                operationsTransactionsPane);
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

    private void setPageTitle(String title) {
        pageTitleLabel.setText(title);
    }

    private void setBarberPreviewImage(ImageView imageView, String storedImagePath) {
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

    private <T> void configurePercentColumn(TableColumn<T, Integer> column) {
        column.setCellFactory(tableColumn -> new TableCell<T, Integer>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item + "%");
            }
        });
    }

    private void populateOverviewRevenueSplitChart(DailyShopTotalDTO shopTotals) {
        ObservableList<PieChart.Data> chartData = FXCollections.observableArrayList();

        if (shopTotals.getBarberCommissionTotal() > 0) {
            chartData.add(new PieChart.Data("Barber " + formatCurrency(shopTotals.getBarberCommissionTotal()),
                    shopTotals.getBarberCommissionTotal()));
        }

        if (shopTotals.getShopShareTotal() > 0) {
            chartData.add(new PieChart.Data("Shop " + formatCurrency(shopTotals.getShopShareTotal()),
                    shopTotals.getShopShareTotal()));
        }

        overviewRevenueSplitChart.setData(chartData);
    }

    private void populateOverviewPricingMixChart(List<PricingCategorySummaryDTO> pricingSummaries) {
        XYChart.Series<String, Number> pricingMixSeries = new XYChart.Series<>();
        pricingMixSeries.setName("Gross Sales");

        pricingSummaries.stream()
                .filter(summary -> summary.getGrossSales() > 0)
                .forEach(summary -> pricingMixSeries.getData().add(
                        new XYChart.Data<>(summary.getName(), summary.getGrossSales())));

        overviewPricingMixChart.getData().setAll(pricingMixSeries);
    }

    private void populateOperationsBarberBreakdownChart(List<DailyBarberTotalDTO> barberTotals) {
        XYChart.Series<String, Number> barberSeries = new XYChart.Series<>();
        barberSeries.setName("Barber");

        XYChart.Series<String, Number> shopSeries = new XYChart.Series<>();
        shopSeries.setName("Shop");

        barberTotals.forEach(total -> {
            barberSeries.getData().add(new XYChart.Data<>(total.getBarberName(), total.getBarberCommissionTotal()));
            shopSeries.getData().add(new XYChart.Data<>(total.getBarberName(), total.getShopShareTotal()));
        });

        operationsBarberBreakdownChart.getData().setAll(barberSeries, shopSeries);
    }

    private String buildBarberBreakdownSummary(List<DailyBarberTotalDTO> barberTotals) {
        if (barberTotals.isEmpty()) {
            return "No posted haircuts for this date.";
        }

        return barberTotals.size() + " barber"
                + (barberTotals.size() == 1 ? "" : "s")
                + " posted haircuts on "
                + getBusinessDate()
                + ". The chart compares barber earnings against shop share per barber.";
    }

    private String getBusinessDate() {
        LocalDate selectedDate = businessDatePicker.getValue();
        if (selectedDate == null) {
            selectedDate = LocalDate.now();
            businessDatePicker.setValue(selectedDate);
        }

        return selectedDate.toString();
    }

    private String getEarningsFromDate() {
        LocalDate selectedDate = earningsFromDatePicker.getValue();
        if (selectedDate == null) {
            selectedDate = LocalDate.now().minusDays(6);
            earningsFromDatePicker.setValue(selectedDate);
        }

        return selectedDate.toString();
    }

    private String getEarningsToDate() {
        LocalDate selectedDate = earningsToDatePicker.getValue();
        if (selectedDate == null) {
            selectedDate = LocalDate.now();
            earningsToDatePicker.setValue(selectedDate);
        }

        return selectedDate.toString();
    }

    private String formatCurrency(int amountPesos) {
        String currencyCode = currentSettings == null || valueOrEmpty(currentSettings.getCurrencyCode()).isBlank()
                ? "PHP"
                : currentSettings.getCurrencyCode();
        return currencyCode + " " + amountPesos;
    }

    private String statusText(int isActive) {
        return isActive == 1 ? "ACTIVE" : "INACTIVE";
    }

    private String valueOrEmpty(String value) {
        return value == null ? "" : value;
    }

    private String valueOrPlaceholder(String value) {
        return value == null || value.isBlank() ? "(none)" : value;
    }

    private void showStatus(String message) {
        statusLabel.setText(message == null ? "" : message);
    }
}
