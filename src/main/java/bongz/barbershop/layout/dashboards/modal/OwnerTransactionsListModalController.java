package bongz.barbershop.layout.dashboards.modal;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import bongz.barbershop.App;
import bongz.barbershop.dto.transaction.TransactionViewDTO;
import bongz.barbershop.loader.ModalLoader;
import bongz.barbershop.model.UserModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class OwnerTransactionsListModalController {

    private final ObservableList<TransactionViewDTO> allTransactions = FXCollections.observableArrayList();

    private App app;
    private UserModel currentUser;
    private Supplier<List<TransactionViewDTO>> reloadSupplier;
    private Consumer<String> onTransactionUpdated;

    @FXML
    private Label titleLabel;
    @FXML
    private Label summaryLabel;
    @FXML
    private TextField searchField;
    @FXML
    private ComboBox<String> filterComboBox;
    @FXML
    private TableView<TransactionViewDTO> transactionsTable;
    @FXML
    private TableColumn<TransactionViewDTO, Integer> transactionIdColumn;
    @FXML
    private TableColumn<TransactionViewDTO, String> businessDateColumn;
    @FXML
    private TableColumn<TransactionViewDTO, String> barberNameColumn;
    @FXML
    private TableColumn<TransactionViewDTO, String> pricingCategoryNameColumn;
    @FXML
    private TableColumn<TransactionViewDTO, Integer> chargedAmountColumn;
    @FXML
    private TableColumn<TransactionViewDTO, String> statusColumn;
    @FXML
    private Button refreshButton;
    @FXML
    private Label statusLabel;

    @FXML
    private void initialize() {
        filterComboBox.setItems(FXCollections.observableArrayList("ALL", "POSTED", "VOID"));
        filterComboBox.getSelectionModel().selectFirst();

        searchField.textProperty().addListener((observable, oldValue, newValue) -> applyFilter());
        filterComboBox.valueProperty().addListener((observable, oldValue, newValue) -> applyFilter());

        transactionIdColumn.setCellValueFactory(new PropertyValueFactory<>("transactionId"));
        businessDateColumn.setCellValueFactory(new PropertyValueFactory<>("businessDate"));
        barberNameColumn.setCellValueFactory(new PropertyValueFactory<>("barberName"));
        pricingCategoryNameColumn.setCellValueFactory(new PropertyValueFactory<>("pricingCategoryName"));
        chargedAmountColumn.setCellValueFactory(new PropertyValueFactory<>("chargedAmount"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        transactionsTable.setRowFactory(table -> {
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

    public void load(
            App app,
            UserModel currentUser,
            String title,
            String summary,
            Supplier<List<TransactionViewDTO>> reloadSupplier,
            Consumer<String> onTransactionUpdated) {
        this.app = app;
        this.currentUser = currentUser != null ? currentUser : app.getCurrentUser();
        this.reloadSupplier = reloadSupplier;
        this.onTransactionUpdated = onTransactionUpdated;

        titleLabel.setText(title == null || title.isBlank() ? "Transactions" : title);
        summaryLabel.setText(summary == null || summary.isBlank() ? "Click any row to inspect details." : summary);

        reloadTransactions();
    }

    @FXML
    private void handleRefresh() {
        reloadTransactions();
        showStatus("Transactions refreshed.");
    }

    @FXML
    private void handleClose() {
        ModalLoader.modal_close(app);
    }

    private void reloadTransactions() {
        List<TransactionViewDTO> transactions = reloadSupplier == null ? List.of() : reloadSupplier.get();
        allTransactions.setAll(transactions);
        applyFilter();
    }

    private void applyFilter() {
        String searchTerm = valueOrEmpty(searchField.getText()).toLowerCase(Locale.ROOT);
        String selectedStatus = filterComboBox.getValue();

        List<TransactionViewDTO> filteredTransactions = allTransactions.stream()
                .filter(transaction -> selectedStatus == null
                        || "ALL".equalsIgnoreCase(selectedStatus)
                        || selectedStatus.equalsIgnoreCase(transaction.getStatus()))
                .filter(transaction -> searchTerm.isBlank()
                        || valueOrEmpty(transaction.getBarberName()).toLowerCase(Locale.ROOT).contains(searchTerm)
                        || valueOrEmpty(transaction.getPricingCategoryName()).toLowerCase(Locale.ROOT).contains(searchTerm)
                        || valueOrEmpty(transaction.getBusinessDate()).toLowerCase(Locale.ROOT).contains(searchTerm)
                        || String.valueOf(transaction.getTransactionId()).contains(searchTerm))
                .collect(Collectors.toList());

        transactionsTable.setItems(FXCollections.observableArrayList(filteredTransactions));
    }

    private void openTransactionDetailModal(TransactionViewDTO transaction) {
        if (transaction == null) {
            return;
        }

        try {
            ModalLoader.load_transaction_detail_modal(app, currentUser, transaction, message -> {
                reloadTransactions();
                if (onTransactionUpdated != null) {
                    onTransactionUpdated.accept(message);
                }
                showStatus(message);
            });
        } catch (IOException e) {
            showStatus("Failed to open transaction detail modal.");
        }
    }

    private String valueOrEmpty(String value) {
        return value == null ? "" : value;
    }

    private void showStatus(String message) {
        statusLabel.setText(message == null ? "" : message);
    }
}
