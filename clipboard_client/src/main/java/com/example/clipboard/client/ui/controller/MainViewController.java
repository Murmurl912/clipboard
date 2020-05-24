package com.example.clipboard.client.ui.controller;

import com.example.clipboard.client.repository.entity.Content;
import com.example.clipboard.client.repository.model.LoginResponseModel;
import com.example.clipboard.client.service.AppContext;
import com.example.clipboard.client.ui.model.ClipboardModel;
import com.example.clipboard.client.ui.view.CardCell;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXToolbar;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import de.jensd.fx.glyphs.materialicons.MaterialIcon;
import de.jensd.fx.glyphs.materialicons.MaterialIconView;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import net.rgielen.fxweaver.core.FxmlView;
import org.controlsfx.control.GridView;
import org.controlsfx.control.PopOver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Objects;
import java.util.function.Consumer;


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
    public JFXButton refresh;
    public JFXButton user;
    public JFXButton avatar;
    public JFXButton clear;
    @Value("classpath:view/signin_dialog.fxml")
    private Resource signInView;
    @Value("classpath:view/signout_dialog.fxml")
    private Resource signOutView;

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

                    if (cell.getBefore() == null) {
                        cell.setBefore(content);
                    } else {
                        Content c = cell.getBefore();
                        if (Objects.equals(c, content)) {
                            return;
                        }
                    }

                    if (Objects.equals(content.status, Content.ContentStatus.CONTENT_STATUS_CLOUD.STATUS)) {
                        ((MaterialIconView) ((JFXButton) cell.getHolder().get("cloud")).getGraphic())
                                .setIcon(MaterialIcon.CLOUD_DONE);
                    } else {
                        ((MaterialIconView) ((JFXButton) cell.getHolder().get("cloud")).getGraphic())
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
        toClipboard();
    }

    private void view() {
        refresh.setOnMouseClicked(e -> {
            refresh();
        });
        clear.setVisible(false);
        clear.setManaged(false);
        avatar.setOnMouseClicked(e -> {
            if (avatarPopOver == null) {
                new Thread(() -> {
                    Node node = load(avatarPopOverView);
                    avatarPopOver = new PopOver(node);
                    avatarPopOver.setDetachable(false);
                    avatarPopOver.setArrowLocation(PopOver.ArrowLocation.TOP_CENTER);
                    avatarPopOver.setTitle("Account");
                    AppContext appContext = context.getBean(AppContext.class);
                    JFXButton sign = (JFXButton) node.lookup("#sign");
                    JFXButton userAvatar = (JFXButton) node.lookup("#avatar");
                    userAvatar.setText(appContext.username);
                    if (StringUtils.isEmpty(appContext.account)) {
                        sign.setText("Sign In");
                        ((FontAwesomeIconView) sign.getGraphic())
                                .setIcon(FontAwesomeIcon.SIGN_IN);
                    } else {
                        sign.setText("Sign Out");
                        ((FontAwesomeIconView) sign.getGraphic())
                                .setIcon(FontAwesomeIcon.SIGN_OUT);
                    }
                    sign.setOnMouseClicked(event -> {
                        if (StringUtils.isEmpty(appContext.account)) {
                            new Thread(() -> {
                                signin(loginResponseModel -> {
                                    ((FontAwesomeIconView) sign.getGraphic())
                                            .setIcon(FontAwesomeIcon.SIGN_OUT);
                                    userAvatar.setText(appContext.username);
                                    sign.setText("Sign Out");
                                });
                            }).start();
                        } else {
                            new Thread(() -> {
                                signout(aVoid -> {
                                    ((FontAwesomeIconView) sign.getGraphic())
                                            .setIcon(FontAwesomeIcon.SIGN_IN);
                                    userAvatar.setText(appContext.username);
                                    sign.setText("Sign In");
                                    Platform.runLater(()->{
                                        container.getItems().clear();
                                    });
                                });
                            }).start();
                        }
                        avatarPopOver.hide();

                    });

                    Platform.runLater(() -> {
                        avatarPopOver.show(avatar);
                    });
                }).start();
                return;
            }

            if (avatarPopOver.isShowing()) {
                avatarPopOver.hide();
            } else {
                avatarPopOver.show(avatar);
            }
        });
    }

    private Node load(@NonNull Resource resource) {
        try {
            FXMLLoader loader = new FXMLLoader(resource.getURL());
            return loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void signin(Consumer<LoginResponseModel> callback) {
        try {
            FXMLLoader loader = new FXMLLoader(signInView.getURL());
            loader.setControllerFactory(context::getBean);
            Node root = loader.load();
            LoginController controller = loader.getController();
            JFXDialog dialog = new JFXDialog
                    (MainViewController.this.root, (Region) root, JFXDialog.DialogTransition.CENTER);
            controller.setDialog(dialog, callback);
            Platform.runLater(dialog::show);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    private void signout(Consumer<Void> callback) {
        try {
            FXMLLoader loader = new FXMLLoader(signOutView.getURL());
            loader.setControllerFactory(context::getBean);
            Node root = loader.load();
            LogoutController controller = loader.getController();
            JFXDialog dialog = new JFXDialog
                    (MainViewController.this.root, (Region) root, JFXDialog.DialogTransition.CENTER);
            controller.setDialog(dialog, callback);
            Platform.runLater(dialog::show);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    private void toClipboard() {
        ClipboardModel model = context.getBean(ClipboardModel.class);
        container.setItems(null);
        container.setItems(model.clipboard());
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
        if (content.status == Content.ContentStatus.CONTENT_STATUS_CLOUD.STATUS) {
            return;
        }
        ClipboardModel model = context.getBean(ClipboardModel.class);
        model.upload(content);
    }

    private void copy(int index, Content content) {
        content = container.getItems().get(index);
        Content finalContent = content;
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent clipboardContent = new ClipboardContent();
        clipboardContent.putString(finalContent.content);
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
        if (contentDetailsPopOver == null) {
            contentDetailsPopOver = new PopOver(load(contentDetailView));
            contentDetailsPopOver.setArrowLocation(PopOver.ArrowLocation.TOP_CENTER);
            contentDetailsPopOver.setDetachable(false);

            contentDetailsPopOver.show(root);
        } else if (contentDetailsPopOver.isShowing()) {
            contentDetailsPopOver.hide();
        } else {
            contentDetailsPopOver.show(root);
        }
    }

}
