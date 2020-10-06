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
import javafxmvc.model.dao.CategoriaDAO;
import javafxmvc.model.database.Database;
import javafxmvc.model.database.DatabaseFactory;
import javafxmvc.model.domain.Categoria;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.util.List;
import java.util.ResourceBundle;

public class FXMLAnchorPaneCadastroCategoriasController implements Initializable {

    @FXML
    private TableView<Categoria> tableViewCategoria;

    @FXML
    private TableColumn<Categoria, String> tableColumnCategoria;

    @FXML
    private Label labelCategoriaCodigo;

    @FXML
    private Label labelCategoriaNome;

    @FXML
    private Button buttonInserir;

    @FXML
    private Button buttonAlterar;

    @FXML
    private Button buttonRemover;
    private List<Categoria> listCategorias;
    private ObservableList<Categoria> observableListCategorias;

    //Atributos para manipulação de BD
    private final Database database = DatabaseFactory.getDatabase("mysql");
    private final Connection connection = database.conectar();
    private final CategoriaDAO categoriaDAO = new CategoriaDAO();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        categoriaDAO.setConnection(connection);
        carregarTableViewCategorias();

        //Listen acionando diante de quaisquer alterações na seleção de itens do TableView
        tableViewCategoria.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> selecionarItemTableViewCategorias(newValue));
    }

    public void carregarTableViewCategorias(){
        tableColumnCategoria.setCellValueFactory(new PropertyValueFactory<>("descricao"));


        listCategorias = categoriaDAO.listar();

        observableListCategorias = FXCollections.observableArrayList(listCategorias);
        tableViewCategoria.setItems(observableListCategorias);
    }

    public void selecionarItemTableViewCategorias(Categoria categoria){
        if(categoria != null) {
            labelCategoriaCodigo.setText(String.valueOf(categoria.getCdCategoria()));
            labelCategoriaNome.setText(categoria.getDescricao());

        }else {
            labelCategoriaCodigo.setText("");
            labelCategoriaNome.setText("");
        }
    }

    @FXML
    public void handleButtonInserir() throws IOException {
        Categoria categoria = new Categoria();
        boolean buttonConfirmarClicked = showFXMLAnhorPaneCadastroCategoriasDialog(categoria);
        if(buttonConfirmarClicked){
            categoriaDAO.inserir(categoria);
            carregarTableViewCategorias();
        }
    }

    @FXML
    public void handleButtonAlterar() throws IOException{
        Categoria categoria = tableViewCategoria.getSelectionModel().getSelectedItem();
        if(categoria != null){
            boolean buttonConfirmarClicked = showFXMLAnhorPaneCadastroCategoriasDialog(categoria);
            if(buttonConfirmarClicked) {
                categoriaDAO.alterar(categoria);
                carregarTableViewCategorias();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Por favor, escolha uma categoria na Tabela");
            alert.show();
        }
    }

    public void handleButtonRemover() throws IOException{
        Categoria categoria = tableViewCategoria.getSelectionModel().getSelectedItem();
        if(categoria != null) {
            categoriaDAO.remover(categoria);
            carregarTableViewCategorias();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Por favor, escolha uma categoria na Tabela");
            alert.show();
        }
    }

    public boolean showFXMLAnhorPaneCadastroCategoriasDialog(Categoria categoria) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(FXMLAnchorPaneCadastroCategoiasDialogController.class.getResource("/javafxmvc/view/FXMLAnchorPaneCadastroCategoriasDialog.fxml"));
        AnchorPane page = (AnchorPane) loader.load();


        Stage dialogStage = new Stage();
        dialogStage.setTitle("Cadastro de Categorias");
        Scene scene= new Scene(page);
        dialogStage.setScene(scene);


        FXMLAnchorPaneCadastroCategoiasDialogController controller = loader.getController();
        controller.setDialogStage(dialogStage);
        controller.setCategoria(categoria);

        //Mostra o  Dialog e espera até que o usuário feche
        dialogStage.showAndWait();

        return controller.isBtConfirmarClicked();
    }


}
