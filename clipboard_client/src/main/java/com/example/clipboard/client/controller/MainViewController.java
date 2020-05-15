package com.example.clipboard.client.controller;

import com.example.clipboard.client.lifecycle.ApplicationInfo;
import com.example.clipboard.client.view.CardCell;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import net.rgielen.fxweaver.core.FxmlView;
import org.controlsfx.control.GridView;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;


@FxmlView
@Component
public class MainViewController {
    public final ApplicationContext context;
    @Value("classpath:view/signin_dialog.fxml")
    private Resource signInView;
    @Value("classpath:view/signout_dialog.fxml")
    private Resource signOutView;
    @Value("classpath:view/signup_dialog.fxml")
    private Resource signUpView;
    @Value("classpath:view/profile_dialog.fxml")
    private Resource profileView;
    @Value("classpath:view/password_dialog.fxml")
    private Resource passwordView;
    @Value("classpath:view/email_dialog.fxml")
    private Resource emailView;
    @Value("classpath:view/activate_dialog.fxml")
    private Resource activateView;

    public GridView<CardCell.CellModel> container;
    public StackPane root;
    public JFXButton clipboard;
    public JFXButton star;
    public JFXButton account;
    public JFXButton trash;
    public JFXButton signout;
    public JFXButton setting;

    public AtomicReference<MainViewState> stateAtomicReference = new AtomicReference<>(MainViewState.VIEW_STATE_DEFAULT);
    public AtomicBoolean transforming = new AtomicBoolean(false);

    public MainViewController(ApplicationContext context) {
        this.context = context;
    }

    @FXML
    private void initialize() {
        container.setCellFactory(view -> context.getBean(CardCell.class));

        ObservableList<CardCell.CellModel> list = FXCollections.observableArrayList();
        container.setItems(list);

        list.add(new CardCell.CellModel("abc"));
        list.add(new CardCell.CellModel("basdfafd"));
        list.add(new CardCell.CellModel("cadsfa"));
        list.add(new CardCell.CellModel("dheow adfadfkl "));
        list.add(new CardCell.CellModel("ea"));
        list.add(new CardCell.CellModel("g"));
        list.add(new CardCell.CellModel("hasdf"));
        list.add(new CardCell.CellModel("i"));
        list.add(new CardCell.CellModel("jasdfaaadf"));
        list.add(new CardCell.CellModel("k"));
        list.add(new CardCell.CellModel("l"));

        container.widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number old, Number now) {
                System.out.println("difference: " + (old.doubleValue() - now.doubleValue()));
                if(Math.abs(old.doubleValue() - now.doubleValue()) > 30) {
                    return;
                }
                double max = now.doubleValue() - 70;
                container.setCellWidth(max / 2);
            }
        });

        view();
    }

    private void view() {
        signout.setOnMouseClicked(e -> {
            ApplicationInfo info = context.getBean(ApplicationInfo.class);
            if(info.isLogin.get()) {
                navigate(MainViewState.VIEW_STATE_SIGN_OUT);
            } else {
                navigate(MainViewState.VIEW_STATE_SIGN_IN);
            }
        });

        account.setOnMouseClicked(e -> {
            navigate(MainViewState.VIEW_STATE_PROFILE);
        });
    }

    @NonNull
    private JFXDialog dialog(@NonNull Node node) {
        JFXDialog dialog = new JFXDialog();
        dialog.setDialogContainer(root);
        dialog.setCacheContainer(true);
        dialog.setTransitionType(JFXDialog.DialogTransition.NONE);

        dialog.getChildren().remove(dialog.getContent());
        dialog.setContent((Region)node);
        return dialog;
    }

    private Node load(@NonNull Resource resource) {
        try {
            FXMLLoader loader = new FXMLLoader(resource.getURL());
            return loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void post() {

    }
    private void navigate(MainViewState next) {
        if(transforming.get()) {
           return;
        }

        Platform.runLater(()->{
            long start = System.currentTimeMillis();
            transforming.set(true);
            switch (next) {
                case VIEW_STATE_SIGN_IN: {
                    signIn();
                } break;

                case VIEW_STATE_SIGN_OUT: {
                    signout();
                } break;

                case VIEW_STATE_PROFILE: {
                    profile();
                } break;

                case VIEW_STATE_SIGN_UP: {
                    signup();
                } break;

                case VIEW_STATE_ACTIVATE: {
                    activate();
                } break;

                case VIEW_STATE_EMAIL: {
                    email();
                } break;

                case VIEW_STATE_PASSWORD: {
                    password();
                } break;

                default: {

                }
            }
            transforming.set(false);
            long end = System.currentTimeMillis();
            System.out.println("Time: " + (end - start));
        });
    }
    
    private void signIn() {
        JFXDialog dialog = dialog(load(signInView));
        dialog.lookup("#confirm")
                .setOnMouseClicked(e -> dialog.close());
        dialog.lookup("#cancel")
                .setOnMouseClicked(e -> dialog.close());
        dialog.lookup("#passwordReset")
                .setOnMouseClicked(e -> {
                    dialog.close();
                    navigate(MainViewState.VIEW_STATE_PASSWORD);
                });
        dialog.lookup("#signup")
                .setOnMouseClicked(e -> {
                    dialog.close();
                    navigate(MainViewState.VIEW_STATE_SIGN_UP);
                });
        dialog.show();
    }

    private void signup() {
        JFXDialog dialog = dialog(load(signUpView));
        dialog.lookup("#confirm")
                .setOnMouseClicked(e -> dialog.close());
        dialog.lookup("#cancel")
                .setOnMouseClicked(e -> dialog.close());
        dialog.lookup("#password");
        dialog.lookup("#email");
        dialog.lookup("#passwordConfirm");
        dialog.lookup("#signIn")
                .setOnMouseClicked(e -> {
                    dialog.close();
                    navigate(MainViewState.VIEW_STATE_SIGN_IN);
                });
        dialog.show();;
    }

    private void signout() {
        JFXDialog dialog = dialog(load(signUpView));
        dialog.lookup("#confirm")
                .setOnMouseClicked(e -> dialog.close());
        dialog.lookup("#cancel")
                .setOnMouseClicked(e -> dialog.close());
        dialog.show();
    }

    private void profile() {
        JFXDialog dialog = dialog(load(profileView));
        dialog.lookup("#confirm")
                .setOnMouseClicked(e -> dialog.close());
        dialog.lookup("#cancel")
                .setOnMouseClicked(e -> dialog.close());

        dialog.lookup("#username");
        dialog.lookup("#avatar");

        dialog.show();;
    }

    private void password() {
        JFXDialog dialog = dialog(load(passwordView));
        dialog.lookup("#confirm")
                .setOnMouseClicked(e -> dialog.close());
        dialog.lookup("#cancel")
                .setOnMouseClicked(e -> dialog.close());

        dialog.show();;
    }
    
    private void email() {
        JFXDialog dialog = dialog(load(emailView));
        dialog.lookup("#confirm")
                .setOnMouseClicked(e -> dialog.close());
        dialog.lookup("#cancel")
                .setOnMouseClicked(e -> dialog.close());
        dialog.show();;
    }

    private void activate() {
        JFXDialog dialog = dialog(load(activateView));
        dialog.lookup("#confirm")
                .setOnMouseClicked(e -> dialog.close());
        dialog.lookup("#cancel")
                .setOnMouseClicked(e -> dialog.close());
        dialog.show();;
    }



    public static enum MainViewState {
        VIEW_STATE_DEFAULT,
        VIEW_STATE_SIGN_IN,
        VIEW_STATE_SIGN_UP,
        VIEW_STATE_SIGN_OUT,
        VIEW_STATE_PROFILE,
        VIEW_STATE_PASSWORD,
        VIEW_STATE_EMAIL,
        VIEW_STATE_ACTIVATE,
    }
}
