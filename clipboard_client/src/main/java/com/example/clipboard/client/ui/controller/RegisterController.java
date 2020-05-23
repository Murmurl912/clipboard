package com.example.clipboard.client.ui.controller;

import com.example.clipboard.client.repository.model.LoginResponseModel;
import com.example.clipboard.client.service.AccountService;
import com.example.clipboard.client.service.exception.EmailRegisteredException;
import com.example.clipboard.client.service.exception.UserNameRegisteredException;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.function.Consumer;

@Component
public class RegisterController {

    public TextField username;
    public TextField email;
    public PasswordField password;
    public PasswordField passwordConfirm;
    public JFXButton cancel;
    public JFXButton signIn;
    public JFXButton confirm;
    public JFXDialog dialog;
    public Label hint;
    private Consumer<LoginResponseModel> callback;
    @Value("classpath:view/signin_dialog.fxml")
    private Resource signInView;
    @Autowired
    private ApplicationContext context;
    @Autowired
    private AccountService service;

    public void initialize() {

    }

    public void setDialog(JFXDialog dialog, Consumer<LoginResponseModel> loginResponseModelConsumer) {
        this.dialog = dialog;
        this.callback = loginResponseModelConsumer;
    }

    public void close(MouseEvent event) {
        dialog.close();
    }

    public void confirm(MouseEvent event) {
        if(StringUtils.isEmpty(username.getText())) {
            hint.setVisible(true);
            hint.setManaged(true);
            hint.setText("Username cannot be empty!");
            return;
        }
        if(StringUtils.isEmpty(email.getText())) {
            hint.setVisible(true);
            hint.setManaged(true);
            hint.setText("Email cannot be empty!");
            return;
        }

        if(StringUtils.isEmpty(password.getText())) {
            hint.setVisible(true);
            hint.setManaged(true);
            hint.setText("Password cannot be empty!");
            return;
        }

        if(StringUtils.isEmpty(passwordConfirm.getText())) {
            hint.setVisible(true);
            hint.setManaged(true);
            hint.setText("Confrim your password!");
            return;
        }

        if(passwordConfirm.getText().equals(password.getText())) {
            service.register(username.getText(), password.getText(), email.getText())
                    .doOnError(error -> {
                        if(error instanceof EmailRegisteredException) {
                            hint.setVisible(true);
                            hint.setManaged(true);
                            hint.setText("Email is registered!");
                        } else if(error instanceof UserNameRegisteredException) {
                            hint.setVisible(true);
                            hint.setManaged(true);
                            hint.setText("Username is registered!");
                        } else {
                            hint.setVisible(true);
                            hint.setManaged(true);
                            hint.setText("Something goes wrong, try again later!");
                        }
                    })
                    .subscribe(model -> {
                        dialog.close();
                    });
        }
    }

    public void signin(MouseEvent event) {
        new Thread(()->{
            try {
                FXMLLoader loader = new FXMLLoader(signInView.getURL());
                loader.setControllerFactory(context::getBean);
                Node root = loader.load();
                Platform.runLater(()->{
                    dialog.setContent((Region) root);
                });
                LoginController controller = loader.getController();
                controller.setDialog(dialog, callback);
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }).start();
    }

    public void keyPressed(KeyEvent keyEvent) {
        if(hint.isVisible()) {
            hint.setVisible(false);
            hint.setManaged(false);
        }

    }
}
