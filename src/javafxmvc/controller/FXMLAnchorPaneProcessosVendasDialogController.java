package javafxmvc.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafxmvc.model.dao.ClienteDAO;
import javafxmvc.model.dao.ProdutoDAO;
import javafxmvc.model.database.Database;
import javafxmvc.model.database.DatabaseFactory;
import javafxmvc.model.domain.Cliente;
import javafxmvc.model.domain.ItemDeVenda;
import javafxmvc.model.domain.Produto;
import javafxmvc.model.domain.Venda;

import java.net.URL;
import java.sql.Connection;
import java.util.List;
import java.util.ResourceBundle;

public class FXMLAnchorPaneProcessosVendasDialogController implements Initializable {

    @FXML
    private ComboBox comboBoxVendaClientes;

    @FXML
    private DatePicker datePickVendaData;

    @FXML
    private CheckBox checkBoxVendaPago;

    @FXML
    private TableView<ItemDeVenda> tableViewItensDeVenda;

    @FXML
    private TextField textFieldVendaValor;

    @FXML
    private ComboBox comboBoxVendaProduto;

    @FXML
    private TableColumn<ItemDeVenda, Produto> tableColumItemDeVendaProduto;

    @FXML
    private TableColumn<ItemDeVenda, Integer> tableColumItemDeVendaQtde;

    @FXML
    private TableColumn<ItemDeVenda, Double> tableColumItemDeVendaValor;

    @FXML
    private TextField textFieldVendaItemDeVendaQtde;

    @FXML
    private Button btAdicionar;

    @FXML
    private Button btConfirmar;

    @FXML
    private Button btCancelar;

    private List<Cliente> listClientes;
    private List<Produto> listProduto;
    private ObservableList<Cliente> observableListClientes;
    private ObservableList<Produto> observableListProdutos;
    private ObservableList<ItemDeVenda> observableListItensDeVenda;

    //Atributos para minipulação de Banco de Dados
    private final Database database = DatabaseFactory.getDatabase("mysql");
    private final Connection connection = database.conectar();
    private final ClienteDAO clienteDAO = new ClienteDAO();
    private final ProdutoDAO produtoDAO = new ProdutoDAO();
    private Stage dialogStage;
    private boolean btConfirmarClicked = false;
    private Venda venda;

    public Stage getDialogStage() {
        return dialogStage;
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public boolean isBtConfirmarClicked() {
        return btConfirmarClicked;
    }

    public void setBtConfirmarClicked(boolean btConfirmarClicked) {
        this.btConfirmarClicked = btConfirmarClicked;
    }

    public Venda getVenda() {
        return venda;
    }

    public void setVenda(Venda venda) {
        this.venda = venda;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        clienteDAO.setConnection(connection);
        produtoDAO.setConnection(connection);

        carregarComboBoxClientes();
        carregarComboBoxProdutos();

        tableColumItemDeVendaProduto.setCellValueFactory(new PropertyValueFactory<>("produto"));
        tableColumItemDeVendaQtde.setCellValueFactory(new PropertyValueFactory<>("quantidade"));
        tableColumItemDeVendaValor.setCellValueFactory(new PropertyValueFactory<>("valor"));

    }

    public void carregarComboBoxClientes() {
        listClientes = clienteDAO.listar();
        observableListClientes = FXCollections.observableArrayList(listClientes);
        comboBoxVendaClientes.setItems(observableListClientes);
    }

    public void carregarComboBoxProdutos() {
        listProduto = produtoDAO.listar();
        observableListProdutos = FXCollections.observableArrayList(listProduto);
        comboBoxVendaProduto.setItems(observableListProdutos);
    }

    @FXML
    public void handleButtonAdicionar() {
        Produto produto;
        ItemDeVenda itemDeVenda = new ItemDeVenda();
        if(comboBoxVendaProduto.getSelectionModel().getSelectedItem() != null){
            produto = (Produto) comboBoxVendaProduto.getSelectionModel().getSelectedItem();
            if(produto.getQuantidade() >= Integer.parseInt(textFieldVendaItemDeVendaQtde.getText())){
                itemDeVenda.setProduto((Produto) comboBoxVendaProduto.getSelectionModel().getSelectedItem());
                itemDeVenda.setQuantidade(Integer.parseInt(textFieldVendaItemDeVendaQtde.getText()));
                itemDeVenda.setValor(itemDeVenda.getProduto().getPreco() * itemDeVenda.getQuantidade());
                venda.getItensDeVenda().add(itemDeVenda);
                venda.setValor(venda.getValor() + itemDeVenda.getValor());
                observableListItensDeVenda = FXCollections.observableArrayList(venda.getItensDeVenda());
                tableViewItensDeVenda.setItems(observableListItensDeVenda);
                textFieldVendaValor.setText(String.format("%.2f", venda.getValor()));
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText("Problemas na escolha de produto!");
                alert.setContentText("Não existe a quantidade de produtos disponíveis no estoque");
                alert.show();
            }
        }
    }

    @FXML
    public void handleButtonConfirmar() {
        if(validarEntradaDeDados()){
            venda.setCliente((Cliente) comboBoxVendaClientes.getSelectionModel().getSelectedItem());
            venda.setPago(checkBoxVendaPago.isSelected());
            venda.setData(datePickVendaData.getValue());
            btConfirmarClicked = true;
            dialogStage.close();
        }
    }

    @FXML
    public void handleButtonCancelar(){
        getDialogStage().close();
    }

    //Validar entrada de dados para o cadastro
    public boolean validarEntradaDeDados(){
        String errorMessage = "";
        if (comboBoxVendaClientes.getSelectionModel().getSelectedItem() == null){
            errorMessage += "Cliente inválido\n";
        }
        if (datePickVendaData.getValue() == null){
            errorMessage += "Data Inválida!\n";
        }
        if (observableListItensDeVenda == null){
            errorMessage += "Itens de venda inválidos!\n";
        }
        if (errorMessage.length() == 0){
            return true;
        } else{
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erro no cadastro");
            alert.setHeaderText("Campos inválidos, por favor, corrija");
            alert.setContentText(errorMessage);
            alert.show();
            return false;
        }
    }



}
