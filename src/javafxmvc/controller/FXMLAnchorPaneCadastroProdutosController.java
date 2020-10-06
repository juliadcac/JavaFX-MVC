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
import javafxmvc.model.dao.ProdutoDAO;
import javafxmvc.model.database.Database;
import javafxmvc.model.database.DatabaseFactory;
import javafxmvc.model.domain.Produto;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.util.List;
import java.util.ResourceBundle;

public class FXMLAnchorPaneCadastroProdutosController implements Initializable {

    @FXML
    private TableView<Produto> tableViewProduto;

    @FXML
    private TableColumn<Produto, String> tableColumnProdutoNome;

    @FXML
    private Label labelProdutoCodigo;

    @FXML
    private Label labelProdutoNome;

    @FXML
    private Label labelProdutoPreco;

    @FXML
    private Label labelProdutoQtde;

    @FXML
    private Label labelProdutoCategoria;

    @FXML
    private Button btInserir;

    @FXML
    private Button btAlterar;

    @FXML
    private Button btRemover;

    private List<Produto> listProdutos;
    private ObservableList<Produto> observableListProduto;

    //Atributos para manipulação de BD
    private final Database database = DatabaseFactory.getDatabase("mysql");
    private final Connection connection = database.conectar();
    private final ProdutoDAO produtoDAO = new ProdutoDAO();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        produtoDAO.setConnection(connection);
        carregarTableViewProdutos();

        //Listen acionando diante de quaisquer alterações na seleção de itens do TableView
        tableViewProduto.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> selecionarItemTableViewProdutos(newValue));
    }

    public void carregarTableViewProdutos(){
        tableColumnProdutoNome.setCellValueFactory(new PropertyValueFactory<>("nome"));


        listProdutos = produtoDAO.listar();

        observableListProduto = FXCollections.observableArrayList(listProdutos);
        tableViewProduto.setItems(observableListProduto);
    }

    public void selecionarItemTableViewProdutos(Produto produto){
        if(produto != null) {
            labelProdutoCodigo.setText(String.valueOf(produto.getCdProduto()));
            labelProdutoNome.setText(produto.getNome());
            labelProdutoPreco.setText(String.valueOf(produto.getPreco()));
            labelProdutoQtde.setText(String.valueOf(produto.getQuantidade()));
            labelProdutoCategoria.setText(produto.getCategoria().getDescricao());

        }else {
            labelProdutoCodigo.setText("");
            labelProdutoNome.setText("");
            labelProdutoPreco.setText("");
            labelProdutoQtde.setText("");
            labelProdutoCategoria.setText("");
        }
    }

    @FXML
    public void handleButtonInserir() throws IOException {
        Produto produto = new Produto();
        boolean buttonConfirmarClicked = showFXMLAnhorPaneCadastroProdutosDialog(produto);
        if(buttonConfirmarClicked){
            produtoDAO.inserir(produto);
            carregarTableViewProdutos();
        }
    }

    @FXML
    public void handleButtonAlterar() throws IOException{
        Produto produto = tableViewProduto.getSelectionModel().getSelectedItem();
        if(produto != null){
            boolean buttonConfirmarClicked = showFXMLAnhorPaneCadastroProdutosDialog(produto);
            if(buttonConfirmarClicked) {
                produtoDAO.alterar(produto);
                carregarTableViewProdutos();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Por favor, escolha um produto na Tabela");
            alert.show();
        }
    }

    public void handleButtonRemover() throws IOException{
        Produto produto = tableViewProduto.getSelectionModel().getSelectedItem();
        if(produto != null) {
            produtoDAO.remover(produto);
            carregarTableViewProdutos();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Por favor, escolha um produto na Tabela");
            alert.show();
        }
    }

    public boolean showFXMLAnhorPaneCadastroProdutosDialog(Produto produto) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(FXMLAnchorPaneCadastroProdutosDialogController.class.getResource("/javafxmvc/view/FXMLAnchorPaneCadastroProdutosDialog.fxml"));
        AnchorPane page = (AnchorPane) loader.load();

        //Criando um estágio de diáologo (Stage Dialog)
        Stage dialogStage = new Stage();
        dialogStage.setTitle("Cadastro de Produtos");
        Scene scene= new Scene(page);
        dialogStage.setScene(scene);

        //Setando o cliente no Controller
        FXMLAnchorPaneCadastroProdutosDialogController controller = loader.getController();
        controller.setDialogStage(dialogStage);
        controller.setProduto(produto);

        //Mostra o  Dialog e espera até que o usuário feche
        dialogStage.showAndWait();

        return controller.isBtConfirmarClicked();
    }
}
