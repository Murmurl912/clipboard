package com.example.clipboard.client.ui.controller;

import com.example.clipboard.client.repository.model.LoginResponseModel;
import com.example.clipboard.client.service.AccountService;
import com.example.clipboard.client.service.exception.LoginFailedException;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.InputMethodEvent;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.util.Callback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.net.ConnectException;
import java.util.function.Consumer;


@Component
public class LoginController {

    public Label hint;
    @FXML
    private JFXButton cancel;
    @FXML
    private JFXButton confirm;
    @FXML
    private TextField username;
    @FXML
    private PasswordField password;
    @FXML
    private JFXButton signup;
    private JFXDialog dialog;
    private Consumer<LoginResponseModel> callback;
    @Autowired
    private AccountService service;

    @Value("classpath:view/signup_dialog.fxml")
    private Resource signUpView;
    @Autowired
    private ApplicationContext context;

    public void initialize() {

    }

    public void setDialog(JFXDialog dialog, Consumer<LoginResponseModel> callback) {
        this.dialog = dialog;
        this.callback = callback;
    }


    public void close(MouseEvent event) {
        dialog.close();
    }


    public void signIn(MouseEvent event) {
        String name = username.getText();
        String pass = password.getText();
        if(StringUtils.isEmpty(name) || StringUtils.isEmpty(pass)) {
            hint.setVisible(true);
            hint.setManaged(true);
            hint.setText("Username or password is empty!");
            return;
        }

        service.signIn(name, pass)
                .doOnError(error -> {
                    if(error instanceof ConnectException) {
                        Platform.runLater(()->{
                            hint.setVisible(true);
                            hint.setManaged(true);
                            hint.setText("Cannot connect to server!");
                        });
                    } else if(error instanceof LoginFailedException) {
                        Platform.runLater(()->{
                            hint.setVisible(true);
                            hint.setManaged(true);
                            hint.setText("Login failed, check your password and username!");
                        });
                    } else {
                        Platform.runLater(()->{
                            hint.setVisible(true);
                            hint.setManaged(true);
                            hint.setText("Something went wrong, Try again later!");
                        });
                    }
                })
                .subscribe(loginResponseModel -> {
                    if(callback != null) {
                        callback.accept(loginResponseModel);
                    }
                    dialog.close();
                });

    }

    public void hide(KeyEvent keyEvent) {
        if(hint.isVisible()) {
            hint.setVisible(false);
            hint.setManaged(false);
        }
    }

    public void signup(MouseEvent event) {
        new Thread(()->{
            try {
                FXMLLoader loader = new FXMLLoader(signUpView.getURL());
                loader.setControllerFactory(context::getBean);
                Node root = loader.load();
                RegisterController controller = loader.getController();
                Platform.runLater(()->{
                    dialog.setContent((Region) root);
                });
                controller.setDialog(dialog, callback);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
