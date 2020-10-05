package javafxmvc.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafxmvc.model.domain.Cliente;

import java.net.URL;
import java.util.ResourceBundle;

public class FXMLAnchorPaneCadastroClientesDialogController implements Initializable {

    @FXML
    private Label labelClienteNome;

    @FXML
    private Label labelClienteCPF;

    @FXML
    private Label labelClienteTelefone;

    @FXML
    private TextField tfClienteNome;

    @FXML
    private TextField tfClienteCPF;

    @FXML
    private TextField tfClienteTelefone;

    @FXML
    private Button btConfirmar;

    @FXML
    private Button btCancelar;

    private Stage dialogStage;
    private boolean btConfirmarClicked = false;
    private Cliente cliente;

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

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
        this.tfClienteNome.setText(cliente.getNome());
        this.tfClienteCPF.setText(cliente.getCpf());
        this.tfClienteTelefone.setText(cliente.getTelefone());
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @FXML
    public void handleButtonConfirmar(){

        if(validarEntradaDeDados()) {

            cliente.setNome(tfClienteNome.getText());
            cliente.setCpf(tfClienteCPF.getText());
            cliente.setTelefone(tfClienteTelefone.getText());

            btConfirmarClicked = true;
            dialogStage.close();
        }
    }

    @FXML
    public void handleButtonCancelar(){
        dialogStage.close();
    }

    //Validar a entrada de dados para o cadastro
    private boolean validarEntradaDeDados() {
        String errorMessage = "";

        if(tfClienteNome.getText() == null || tfClienteNome.getText().length() == 0){
            errorMessage += "Nome inválido!\n";
        }
        if (tfClienteCPF.getText() == null || tfClienteCPF.getText().length() == 0){
            errorMessage += "CPF inválido!\n";
        }
        if (tfClienteTelefone.getText() == null || tfClienteTelefone.getText().length() == 0){
            errorMessage += "Telefone inválido!\n";
        }

        if(errorMessage.length() == 0) {
            return true;
        } else {
            //Mostrando a mensagem de erro
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erro no cadastro");
            alert.setHeaderText("Campos inválidos, por favorm corrija.");
            alert.setContentText(errorMessage);
            alert.show();
            return false;
        }
    }
}
