package javafxmvc.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafxmvc.model.dao.CategoriaDAO;
import javafxmvc.model.database.Database;
import javafxmvc.model.database.DatabaseFactory;
import javafxmvc.model.domain.Categoria;
import javafxmvc.model.domain.Produto;

import java.net.URL;
import java.sql.Connection;
import java.util.List;
import java.util.ResourceBundle;

public class FXMLAnchorPaneCadastroProdutosDialogController implements Initializable {

    @FXML
    private ComboBox comboBoxProdutoCategoria;

    @FXML
    private TextField textFieldProdutoNome;

    @FXML
    private TextField textFieldProdutoPreco;

    @FXML
    private TextField textFieldProdutoQtde;

    @FXML
    private Button btConfirmar;

    @FXML
    private Button btCancelar;

    private List<Categoria> listCategoias;
    private ObservableList<Categoria> observableListCategoria;

    //Atributos para minipulação de Banco de Dados
    private final Database database = DatabaseFactory.getDatabase("mysql");
    private final Connection connection = database.conectar();
    private final CategoriaDAO categoriaDAO = new CategoriaDAO();

    private Stage dialogStage;
    private boolean btConfirmarClicked = false;
    private Produto produto;

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

    public Produto getProduto() {
        return produto;
    }

    public void setProduto(Produto produto) {
        this.produto = produto;

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        categoriaDAO.setConnection(connection);

        carregarComboBoxCategorias();

    }

    public void carregarComboBoxCategorias(){
        listCategoias = categoriaDAO.listar();
        observableListCategoria = FXCollections.observableArrayList(listCategoias);
        comboBoxProdutoCategoria.setItems(observableListCategoria);
    }



    @FXML
    public void handleButtonConfirmar() {
        if(validarEntradaDeDados()){
            produto.setNome(textFieldProdutoNome.getText() );
            produto.setPreco(Double.parseDouble(textFieldProdutoPreco.getText()));
            produto.setQuantidade(Integer.parseInt(textFieldProdutoQtde.getText()));
            produto.setCategoria((Categoria) comboBoxProdutoCategoria.getSelectionModel().getSelectedItem());
            btConfirmarClicked = true;
            dialogStage.close();
        }
    }

    @FXML
    public void handleButtonCancelar(){
        getDialogStage().close();
    }

    //Validar entrada de dados para o cadastro
    public boolean validarEntradaDeDados() {
        String errorMessage = "";
        if (comboBoxProdutoCategoria.getSelectionModel().getSelectedItem() == null) {
            errorMessage += "Categoria inválida\n";
        }
        if (textFieldProdutoPreco.getText() == null || textFieldProdutoPreco.getText().length() == 0) {
            errorMessage += "Preço Inválido!\n";
        }
        if (textFieldProdutoNome.getText() == null || textFieldProdutoNome.getText().length() == 0) {
            errorMessage += "Nome inválido!\n";
        }
        if (textFieldProdutoQtde.getText() == null || textFieldProdutoQtde.getText().length() == 0) {
            errorMessage += "Quantidade inválida!\n";
        }
        if (errorMessage.length() == 0) {
            return true;
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erro no cadastro");
            alert.setHeaderText("Campos inválidos, por favor, corrija");
            alert.setContentText(errorMessage);
            alert.show();
            return false;
        }
    }

}
