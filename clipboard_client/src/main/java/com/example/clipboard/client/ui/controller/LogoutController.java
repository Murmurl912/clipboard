package com.example.clipboard.client.ui.controller;

import com.example.clipboard.client.service.AccountService;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import javafx.scene.input.MouseEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Component
public class LogoutController {

    public JFXButton cancel;
    public JFXButton confirm;
    private JFXDialog dialog;
    private Consumer<Void> callback;
    @Autowired
    private AccountService service;

    public void setDialog(JFXDialog dialog, Consumer<Void> callback) {
        this.dialog = dialog;
        this.callback = callback;
    }

    public void close(MouseEvent event) {
        dialog.close();
    }

    public void confirm(MouseEvent event) {
        service.signOut();
        callback.accept(null);
        dialog.close();
    }
}
