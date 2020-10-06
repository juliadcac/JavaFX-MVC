package javafxmvc.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafxmvc.model.domain.Categoria;

import java.net.URL;
import java.util.ResourceBundle;

public class FXMLAnchorPaneCadastroCategoiasDialogController implements Initializable {

    @FXML
    private Button buttonConfirmar;

    @FXML
    private Button buttonCancelar;

    @FXML
    private TextField testFieldCategoriaNome;


    private Stage dialogStage;
    private boolean btConfirmarClicked = false;
    private Categoria categoria;

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

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @FXML
    public void handleButtonConfirmar(){

        if(validarEntradaDeDados()) {

            categoria.setDescricao(testFieldCategoriaNome.getText());

            btConfirmarClicked = true;
            dialogStage.close();
        }
    }

    @FXML
    public void handleButtonCancelar(){
        dialogStage.close();
    }


    private boolean validarEntradaDeDados() {
        String errorMessage = "";

        if(testFieldCategoriaNome.getText() == null || testFieldCategoriaNome.getText().length() == 0){
            errorMessage += "Nome inválido!\n";
        }

        if(errorMessage.length() == 0) {
            return true;
        } else {

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erro no cadastro");
            alert.setHeaderText("Campos inválidos, por favor corrija.");
            alert.setContentText(errorMessage);
            alert.show();
            return false;
        }
    }
}
