package com.example.clipboard.client.ui.controller;

import com.example.clipboard.client.repository.entity.Content;
import com.example.clipboard.client.ui.model.ClipboardModel;
import com.example.clipboard.client.ui.view.CardCell;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXToolbar;
import de.jensd.fx.glyphs.materialicons.MaterialIcon;
import de.jensd.fx.glyphs.materialicons.MaterialIconView;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import net.rgielen.fxweaver.core.FxmlView;
import org.controlsfx.control.GridView;
import org.controlsfx.control.PopOver;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;


@FxmlView
@Component
public class MainViewController {
    public final ApplicationContext context;
    public JFXToolbar menu;
    public JFXButton layout;
    public TextField searchEntry;
    public JFXButton search;
    public GridView<Content> container;
    public StackPane root;
    public JFXButton clipboard;
    public AtomicReference<MainViewState> stateAtomicReference = new AtomicReference<>(MainViewState.VIEW_STATE_DEFAULT);
    public AtomicBoolean transforming = new AtomicBoolean(false);
    public JFXButton refresh;
    public JFXButton user;
    public JFXButton avatar;
    public JFXButton clear;
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
    @Value("classpath:view/card_cell.fxml")
    private Resource cardCellView;
    @Value("classpath:view/content_details.fxml")
    private Resource contentDetailView;
    @Value(("classpath:view/avatar_popover.fxml"))
    private Resource avatarPopOverView;

    private PopOver avatarPopOver;
    private PopOver contentDetailsPopOver;

    public MainViewController(ApplicationContext context) {
        this.context = context;
    }

    @FXML
    private void initialize() {

        container.setVerticalCellSpacing(20);
        container.setHorizontalCellSpacing(10);
        container.setCellFactory(factory -> new CardCell(
                cell -> {
                    try {
                        FXMLLoader loader = new FXMLLoader(cardCellView.getURL());
                        loader.setController(cell);
                        Node node = loader.load();
                        cell.setGraphic(node);
                        cell.getHolder().put("node", node);
                        cell.getHolder().put("content", node.lookup("#content"));
                        cell.getHolder().put("time", node.lookup("#time"));
                        cell.getHolder().put("date", node.lookup("#date"));
                        Node cloud = node.lookup("#cloud");
                        cell.getHolder().put("cloud", cloud);
                        Node delete = node.lookup("#trash");
                        cell.getHolder().put("delete", delete);
                        Node copy = node.lookup("#copy");
                        cell.getHolder().put("copy", copy);
                        Node container = node.lookup("#container");

                        cloud.setOnMouseClicked(e -> {
                            cloud(cell.getIndex(), cell.getItem());
                        });

                        copy.setOnMouseClicked(e -> {
                            copy(cell.getIndex(), cell.getItem());
                        });

                        delete.setOnMouseClicked(e -> {
                            delete(cell.getIndex(), cell.getItem());
                        });


                        container.setOnMouseClicked(e -> {
                            if (e.getClickCount() == 2) {
                                details(container, cell.getIndex(), cell.getItem());
                            }
                        });

                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                },
                (cell, content, empty) -> {
                    if (empty || content == null) {
                        return;
                    }

                    if(cell.getBefore() == null) {
                        cell.setBefore(content);
                    } else {
                        Content c = cell.getBefore();
                        if(Objects.equals(c, content)) {
                            return;
                        }
                    }

                    if(content.status == Content.ContentStatus.CONTENT_STATUS_CLOUD.STATUS) {
                        ((MaterialIconView)((JFXButton)cell.getHolder().get("cloud")).getGraphic())
                                .setIcon(MaterialIcon.CLOUD_DONE);
                    } else {
                        ((MaterialIconView)((JFXButton)cell.getHolder().get("cloud")).getGraphic())
                                .setIcon(MaterialIcon.CLOUD_OFF);
                    }

                    Node label = cell.getHolder().get("content");
                    ((Label) label).setText(content.content);

                    ((Label) cell.getHolder().get("time"))
                            .setText(DateFormat.getTimeInstance().format(content.update));
                    ((Label) cell.getHolder().get("date"))
                            .setText(DateFormat.getDateInstance().format(content.update));
                    cell.setBefore(content);
                }));
        container.widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number old, Number now) {
                double max = now.doubleValue();
                container.setCellWidth(max - 20);
            }
        });
        view();
        navigate(MainViewState.VIEW_STATE_DEFAULT);
    }

    private void view() {
        refresh.setOnMouseClicked(e -> {
            refresh();
        });
        avatar.setOnMouseClicked(e -> {
            if(avatarPopOver == null) {
                Node node = load(avatarPopOverView);
                avatarPopOver = new PopOver(node);
                avatarPopOver.setDetachable(false);
                avatarPopOver.setArrowLocation(PopOver.ArrowLocation.TOP_CENTER);
                avatarPopOver.setTitle("Account");
                node.lookup("#sign")
                        .setOnMouseClicked(event -> {
                            signIn();
                        });
                avatarPopOver.show(avatar);
                return;
            }

            if(avatarPopOver.isShowing()) {
                avatarPopOver.hide();
            } else {
                avatarPopOver.show(avatar);
            }
        });
    }

    @NonNull
    private JFXDialog dialog(@NonNull Node node) {
        JFXDialog dialog = new JFXDialog();
        dialog.setDialogContainer(root);
        dialog.setCacheContainer(true);
        dialog.setTransitionType(JFXDialog.DialogTransition.NONE);

        dialog.getChildren().remove(dialog.getContent());
        dialog.setContent((Region) node);
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

    private void navigate(MainViewState next) {
        if (transforming.get()) {
            return;
        }

        Platform.runLater(() -> {
            long start = System.currentTimeMillis();
            transforming.set(true);
            switch (next) {
                case VIEW_STATE_SIGN_IN: {
                    signIn();
                }
                break;

                case VIEW_STATE_SIGN_OUT: {
                    signout();
                }
                break;

                case VIEW_STATE_PROFILE: {
                    profile();
                }
                break;

                case VIEW_STATE_SIGN_UP: {
                    signup();
                }
                break;

                case VIEW_STATE_ACTIVATE: {
                    activate();
                }
                break;

                case VIEW_STATE_EMAIL: {
                    email();
                }
                break;

                case VIEW_STATE_PASSWORD: {
                    password();
                }
                break;


                default: {
                    toClipboard();
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
        dialog.show();
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

        dialog.show();
    }

    private void password() {
        JFXDialog dialog = dialog(load(passwordView));
        dialog.lookup("#confirm")
                .setOnMouseClicked(e -> dialog.close());
        dialog.lookup("#cancel")
                .setOnMouseClicked(e -> dialog.close());

        dialog.show();
    }

    private void email() {
        JFXDialog dialog = dialog(load(emailView));
        dialog.lookup("#confirm")
                .setOnMouseClicked(e -> dialog.close());
        dialog.lookup("#cancel")
                .setOnMouseClicked(e -> dialog.close());
        dialog.show();
    }

    private void activate() {
        JFXDialog dialog = dialog(load(activateView));
        dialog.lookup("#confirm")
                .setOnMouseClicked(e -> dialog.close());
        dialog.lookup("#cancel")
                .setOnMouseClicked(e -> dialog.close());
        dialog.show();
    }

    private void toClipboard() {
        ClipboardModel model = context.getBean(ClipboardModel.class);
        container.setItems(null);
        container.setItems(model.clipboard());
        model.clipboard().addListener((ListChangeListener<? super Content>) change -> {
            System.out.println(Thread.currentThread());
            System.out.println(change);
        });
    }

    private void refresh() {
        ClipboardModel model = context.getBean(ClipboardModel.class);
        model.clipboard().clear();
        model.refresh();
    }

    private void search(String text) {

    }

    private void cancel() {

    }

    private void cloud(int index, Content content) {
        content = container.getItems().get(index);
        if(content.status == Content.ContentStatus.CONTENT_STATUS_CLOUD.STATUS) {
            return;
        }
        ClipboardModel model = context.getBean(ClipboardModel.class);
        model.upload(content);
    }

    private void copy(int index, Content content) {
        content = container.getItems().get(index);
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent clipboardContent = new ClipboardContent();
        clipboardContent.putString(content.content);
        clipboard.setContent(clipboardContent);
    }

    private void delete(int index, Content content) {
        content = container.getItems().get(index);
        switch (Content.ContentState.get(content.state)) {
            case CONTENT_STATE_NORMAL:
                ClipboardModel model = context.getBean(ClipboardModel.class);
                model.state(content.id, Content.ContentState.CONTENT_STATE_DELETE);
                break;
            case CONTENT_STATE_DELETE:
                throw new RuntimeException("Cannot recycle content: " + content);
        }
    }

    private void details(Node root, int index, Content content) {
        content = container.getItems().get(index);
        if(contentDetailsPopOver == null) {
            contentDetailsPopOver = new PopOver(load(contentDetailView));
            contentDetailsPopOver.setArrowLocation(PopOver.ArrowLocation.TOP_CENTER);
            contentDetailsPopOver.setDetachable(false);

            contentDetailsPopOver.show(root);
        } else if(contentDetailsPopOver.isShowing()){
            contentDetailsPopOver.hide();
        } else {
            contentDetailsPopOver.show(root);
        }

//        Content data = new Content();
//        BeanUtils.copyProperties(content, data);
//        JFXDialog dialog = dialog(load(contentDetailView));
//        normalDetails(dialog, data);
    }

    private void normalDetails(JFXDialog dialog, Content data) {
        // load view
        Label label = (Label) dialog.lookup("#content");
        Node copy = dialog.lookup("#copy");
        Node cancel = dialog.lookup("#cancel");
        Node close = dialog.lookup("#close");
        Node delete = dialog.lookup("#delete");

        label.setText(data.content);

        close.setOnMouseClicked(e -> {
            dialog.close();
        });

        copy.setOnMouseClicked(e -> {
            Clipboard clipboard = Clipboard.getSystemClipboard();
            ClipboardContent clipboardContent = new ClipboardContent();
            clipboardContent.putString(label.getText());
            clipboard.setContent(clipboardContent);
            dialog.close();
        });

        delete.setOnMouseClicked(e -> {
            switch (Content.ContentState.get(data.state)) {
                case CONTENT_STATE_NORMAL:
                    ClipboardModel model = context.getBean(ClipboardModel.class);
                    model.state(data.id, Content.ContentState.CONTENT_STATE_DELETE);
                    break;
                case CONTENT_STATE_DELETE:
            }
            dialog.close();
        });

        dialog.show();
    }

    private void toggleMenu() {
        menu.setManaged(!menu.isManaged());
        menu.setVisible(!menu.isVisible());
    }

    public enum MainViewState {
        VIEW_STATE_DEFAULT,
        VIEW_STATE_SIGN_IN,
        VIEW_STATE_SIGN_UP,
        VIEW_STATE_SIGN_OUT,
        VIEW_STATE_PROFILE,
        VIEW_STATE_PASSWORD,
        VIEW_STATE_EMAIL,
        VIEW_STATE_ACTIVATE,
        VIEW_STATE_SETTING,
    }
}
