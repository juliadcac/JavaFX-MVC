package javafxmvc.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafxmvc.model.dao.ClienteDAO;
import javafxmvc.model.database.Database;
import javafxmvc.model.database.DatabaseFactory;
import javafxmvc.model.domain.Cliente;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.util.List;
import java.util.ResourceBundle;

public class FXMLAnchorPaneCadastroClientesController implements Initializable {

    @FXML
    private TableView<Cliente> tableViewCliente;

    @FXML
    private TableColumn<Cliente,String> tableColumClienteNome;

    @FXML
    private TableColumn<Cliente, String> tableColumClienteCPF;

    @FXML
    private Label labelClienteCodigo;

    @FXML
    private Label labelClienteNome;

    @FXML
    private Label labelClienteCPF;

    @FXML
    private Label labelClienteTelefone;

    @FXML
    private Button btInserir;

    @FXML
    private Button btAlterar;

    @FXML
    private Button btRemover;

    private List<Cliente> listClientes;
    private ObservableList<Cliente> observableListCliente;

    //Atributos para manipulação de BD
    private final Database database = DatabaseFactory.getDatabase("mysql"); //forneçe um instancia do bd msql
    private final Connection connection = database.conectar();
    private final ClienteDAO clienteDAO = new ClienteDAO();


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        clienteDAO.setConnection(connection);
        carregarTableViewCleintes();

        //Listen acionando diante de quaisquer alterações na seleção de itens do TableView
        tableViewCliente.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> selecionarItemTableViewClientes(newValue));
    }

    public void carregarTableViewCleintes(){
        tableColumClienteNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        tableColumClienteCPF.setCellValueFactory(new PropertyValueFactory<>("cpf"));

        listClientes = clienteDAO.listar();

        observableListCliente = FXCollections.observableArrayList(listClientes);
        tableViewCliente.setItems(observableListCliente);
    }

    public void selecionarItemTableViewClientes(Cliente cliente){
        if(cliente != null) {
            labelClienteCodigo.setText(String.valueOf(cliente.getCdCliente()));
            labelClienteNome.setText(cliente.getNome());
            labelClienteCPF.setText(cliente.getCpf());
            labelClienteTelefone.setText(cliente.getTelefone());
        }else {
            labelClienteCodigo.setText("");
            labelClienteNome.setText("");
            labelClienteCPF.setText("");
            labelClienteTelefone.setText("");
        }
    }

    @FXML
    public void handleButtonInserir() throws IOException {
        Cliente cliente = new Cliente();
        boolean buttonConfirmarClicked = showFXMLAnhorPaneCadastroClientesDialog(cliente);
        if(buttonConfirmarClicked){
            clienteDAO.inserir(cliente);
            carregarTableViewCleintes();
        }
    }

    @FXML
    public void handleButtonAlterar() throws IOException{
        Cliente cliente = tableViewCliente.getSelectionModel().getSelectedItem();
        if(cliente != null){
            boolean buttonConfirmarClicked = showFXMLAnhorPaneCadastroClientesDialog(cliente);
            if(buttonConfirmarClicked) {
                clienteDAO.alterar(cliente);
                carregarTableViewCleintes();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Por favor, escolha um cliente na Tabela");
            alert.show();
        }
    }

    public void handleButtonRemover() throws IOException{
        Cliente cliente = tableViewCliente.getSelectionModel().getSelectedItem();
        if(cliente != null) {
            clienteDAO.remover(cliente);
            carregarTableViewCleintes();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Por favor, escolha um cliente na Tabela");
            alert.show();
        }
    }

    public boolean showFXMLAnhorPaneCadastroClientesDialog(Cliente cliente) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(FXMLAnchorPaneCadastroClientesDialogController.class.getResource("/javafxmvc/view/FXMLAnchorPaneCadastroClientesDialog.fxml"));
        AnchorPane page = (AnchorPane) loader.load();

        //Criando um estágio de diáologo (Stage Dialog)
        Stage dialogStage = new Stage();
        dialogStage.setTitle("Cadastro de Clientes");
        Scene scene= new Scene(page);
        dialogStage.setScene(scene);

        //Setando o cliente no Controller
        FXMLAnchorPaneCadastroClientesDialogController controller = loader.getController();
        controller.setDialogStage(dialogStage);
        controller.setCliente(cliente);

        //Mostra o  Dialog e espera até que o usuário feche
        dialogStage.showAndWait();

        return controller.isBtConfirmarClicked();
    }

}
