package com.example.clipboard.client.controller;

import com.example.clipboard.client.controller.viewmodel.ClipboardViewModel;
import com.example.clipboard.client.entity.Content;
import com.example.clipboard.client.lifecycle.ApplicationInfo;
import com.example.clipboard.client.view.CardCell;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXToolbar;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import io.reactivex.rxjava3.disposables.Disposable;
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
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.DateFormat;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;


@FxmlView
@Component
public class MainViewController {
    public final ApplicationContext context;
    public JFXToolbar menu;
    public JFXButton menuToggle;
    public JFXButton layout;
    public TextField searchEntry;
    public JFXButton search;
    public GridView<Content> container;
    private final ListChangeListener<Content>
            clipboardListener = change -> {
        Platform.runLater(() -> {
            container.getItems().clear();
            container.getItems().addAll(change.getList());
        });
    };
    private final ListChangeListener<Content>
            starListener = change -> {
        Platform.runLater(() -> {
            container.getItems().clear();
            container.getItems().addAll(change.getList());
        });
    };
    private final ListChangeListener<Content>
            archiveListener = change -> {
        Platform.runLater(() -> {
            container.getItems().clear();
            container.getItems().addAll(change.getList());
        });
    };
    private final ListChangeListener<Content>
            recycleListener = change -> {
        Platform.runLater(() -> {
            container.getItems().clear();
            container.getItems().addAll(change.getList());
        });
    };
    public StackPane root;
    public JFXButton clipboard;
    public JFXButton star;
    public JFXButton account;
    public JFXButton trash;
    public JFXButton signout;
    public JFXButton setting;
    public JFXButton archive;
    public AtomicReference<MainViewState> stateAtomicReference = new AtomicReference<>(MainViewState.VIEW_STATE_DEFAULT);
    public AtomicBoolean transforming = new AtomicBoolean(false);
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
    private ClipboardViewModel clipboardViewModel;


    public MainViewController(ApplicationContext context) {
        this.context = context;
    }

    @FXML
    private void initialize() {

        container.setVerticalCellSpacing(10);
        container.setHorizontalCellSpacing(0);
        container.setCellFactory(factory -> new CardCell(
                cell -> {
                    try {
                        FXMLLoader loader = new FXMLLoader(cardCellView.getURL());
                        loader.setController(cell);
                        Node node = loader.load();
                        cell.setGraphic(node);
                        cell.getHolder().put("node", node);
                        cell.getHolder().put("title", node.lookup("#title"));
                        cell.getHolder().put("content", node.lookup("#content"));
                        Node star = node.lookup("#star");
                        cell.getHolder().put("star", star);
                        cell.getHolder().put("time", node.lookup("#time"));
                        Node delete = node.lookup("#trash");
                        cell.getHolder().put("delete", delete);
                        Node copy = node.lookup("#copy");
                        cell.getHolder().put("copy", copy);
                        Node container = node.lookup("#container");
                        Node restore = node.lookup("#restore");
                        cell.getHolder().put("restore", restore);
                        copy.setOnMouseClicked(e -> {
                            copy(cell.getIndex(), cell.getItem());
                        });

                        delete.setOnMouseClicked(e -> {
                            delete(cell.getIndex(), cell.getItem());
                        });

                        container.setOnMouseClicked(e -> {
                            if (e.getClickCount() == 2) {
                                details(cell.getIndex(), cell.getItem());
                            }
                        });

                        star.setOnMouseClicked(e -> {
                            star(cell.getIndex(), cell.getItem());
                        });

                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                },
                (cell, content, empty) -> {
                    if (empty || content == null) {
                        return;
                    }

                    Node star = cell.getHolder().get("star");
                    Node copy = cell.getHolder().get("copy");
                    Node restore = cell.getHolder().get("restore");
                    Node delete = cell.getHolder().get("delete");
                    Node label = cell.getHolder().get("content");
                    ((Label) label).setText(content.content);

                    if (content.star) {
                        ((FontAwesomeIconView) ((JFXButton) cell.getHolder().get("star")).getGraphic())
                                .setIcon(FontAwesomeIcon.STAR);
                    } else {
                        ((FontAwesomeIconView) ((JFXButton) cell.getHolder().get("star")).getGraphic())
                                .setIcon(FontAwesomeIcon.STAR_ALT);
                    }
                    ((Label) cell.getHolder().get("time"))
                            .setText(DateFormat.getDateTimeInstance().format(content.update));
                }));
        container.widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number old, Number now) {
                double max = now.doubleValue();
                container.setCellWidth(max);
            }
        });
        view();
        navigate(MainViewState.VIEW_STATE_DEFAULT);
    }

    private void view() {
        signout.setOnMouseClicked(e -> {
            ApplicationInfo info = context.getBean(ApplicationInfo.class);
            if (info.isLogin.get()) {
                navigate(MainViewState.VIEW_STATE_SIGN_OUT);
            } else {
                navigate(MainViewState.VIEW_STATE_SIGN_IN);
            }
        });

        account.setOnMouseClicked(e -> {
            navigate(MainViewState.VIEW_STATE_PROFILE);
        });


        menuToggle.setOnMouseClicked(e -> {
            toggleMenu();
        });

        star.setOnMouseClicked(e -> {
            navigate(MainViewState.VIEW_STATE_STAR);
        });

        clipboard.setOnMouseClicked(e -> {
            navigate(MainViewState.VIEW_STATE_DEFAULT);
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

                case VIEW_STATE_STAR: {
                    toStar();
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

    // todo refactor code
    private void toStar() {
        menuToggle.setText("Star");
        if (clipboardViewModel == null) {
            clipboardViewModel =
                    context.getBean(ClipboardViewModel.class);
        }
        clearListener();
        container.getItems().clear();
        clipboardViewModel.getStar().addListener(starListener);
        clipboardViewModel.refreshStar();
    }

    // todo refactor code
    private void toClipboard() {
        menuToggle.setText("Clipboard");
        if (clipboardViewModel == null) {
            clipboardViewModel =
                    context.getBean(ClipboardViewModel.class);
        }
        clearListener();
        container.getItems().clear();
        clipboardViewModel.getClipboard().addListener(clipboardListener);
        clipboardViewModel.refreshClipboard();
    }

    private void clearListener() {
        clipboardViewModel.getStar().removeListener(starListener);
        clipboardViewModel.getClipboard().removeListener(clipboardListener);
    }

    private void setting() {

    }


    private void copy(int index, Content content) {
        content = container.getItems().get(index);
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent clipboardContent = new ClipboardContent();
        clipboardContent.putString(content.content);
        clipboard.setContent(clipboardContent);
    }


    private void star(int index, Content content) {
        content = container.getItems().get(index);
        switch (Content.ContentState.get(content.state)) {
            case CONTENT_STATE_NORMAL:
                if (clipboardViewModel == null) {
                    clipboardViewModel =
                            context.getBean(ClipboardViewModel.class);
                }
                clipboardViewModel.star(content.id, !content.star)
                        .subscribe();
                break;
            case CONTENT_STATE_DELETE:
            default:
                throw new RuntimeException("Cannot star content: " + content);
        }
    }

    private void delete(int index, Content content) {
        content = container.getItems().get(index);
        switch (Content.ContentState.get(content.state)) {
            case CONTENT_STATE_NORMAL:
                if (clipboardViewModel == null) {
                    clipboardViewModel =
                            context.getBean(ClipboardViewModel.class);
                }
                clipboardViewModel.state(content.id,
                        Content.ContentState.CONTENT_STATE_DELETE)
                        .subscribe();
                break;
            case CONTENT_STATE_DELETE:
                throw new RuntimeException("Cannot recycle content: " + content);
        }
    }

    private void details(int index, Content content) {
        content = container.getItems().get(index);
        Content data = new Content();
        BeanUtils.copyProperties(content, data);
        JFXDialog dialog = dialog(load(contentDetailView));
        normalDetails(dialog, data);
    }

    private void normalDetails(JFXDialog dialog, Content data) {
        // load view
        TextArea textArea = (TextArea) dialog.lookup("#content");
        Node copy = dialog.lookup("#copy");
        Node cancel = dialog.lookup("#cancel");
        Node star = dialog.lookup("#star");
        Node ok = dialog.lookup("#ok");
        Node delete = dialog.lookup("#delete");
        Node device = dialog.lookup("#device");
        Label label = (Label) dialog.lookup("#time");
        Node edit = dialog.lookup("#edit");

        // initialize
        textArea.setText(data.content);
        textArea.setEditable(false);
        ok.setManaged(false);
        ok.setVisible(false);

        if (data.star) {
            ((FontAwesomeIconView) ((JFXButton) star).getGraphic()).setIcon(FontAwesomeIcon.STAR);
        } else {
            ((FontAwesomeIconView) ((JFXButton) star).getGraphic()).setIcon(FontAwesomeIcon.STAR_ALT);
        }

        // listener

        star.setOnMouseClicked(e -> {
            if (textArea.isEditable()) {
                return;
            }

            Disposable subscribe = clipboardViewModel.star(data.id, !data.star)
                    .subscribe(c -> {
                        BeanUtils.copyProperties(c, data);
                        if (data.star) {
                            ((FontAwesomeIconView) ((JFXButton) star).getGraphic()).setIcon(FontAwesomeIcon.STAR);
                        } else {
                            ((FontAwesomeIconView) ((JFXButton) star).getGraphic()).setIcon(FontAwesomeIcon.STAR_ALT);
                        }
                    });

        });

        edit.setOnMouseClicked(e -> {
            ok.setVisible(true);
            ok.setManaged(true);
            edit.setVisible(false);
            edit.setManaged(false);
            star.setVisible(false);
            star.setManaged(false);
            copy.setVisible(false);
            copy.setManaged(false);
            delete.setVisible(false);
            delete.setManaged(false);
            textArea.setEditable(true);
        });

        cancel.setOnMouseClicked(e -> {
            if (textArea.isEditable()) {
                textArea.setEditable(false);
                ok.setVisible(false);
                ok.setManaged(false);
                edit.setManaged(true);
                edit.setVisible(true);
                star.setVisible(true);
                star.setManaged(true);
                copy.setVisible(true);
                copy.setManaged(true);
                delete.setVisible(true);
                delete.setManaged(true);
                textArea.setText(data.content);
            } else {
                dialog.close();
            }
        });

        ok.setOnMouseClicked(e -> {
            if (textArea.isEditable()) {
                textArea.setEditable(false);
                ok.setVisible(false);
                ok.setManaged(false);
                edit.setManaged(true);
                edit.setVisible(true);
                star.setVisible(true);
                star.setManaged(true);
                copy.setVisible(true);
                copy.setManaged(true);
                delete.setVisible(true);
                delete.setManaged(true);

                Disposable subscribe = clipboardViewModel.text(data.id, textArea.getText())
                        .subscribe(c -> {
                            BeanUtils.copyProperties(c, data);
                        });
            }
        });

        copy.setOnMouseClicked(e -> {
            if (!textArea.isEditable()) {
                Clipboard clipboard = Clipboard.getSystemClipboard();
                ClipboardContent clipboardContent = new ClipboardContent();
                clipboardContent.putString(textArea.getText());
                clipboard.setContent(clipboardContent);
            }
            dialog.close();
        });

        delete.setOnMouseClicked(e -> {
            if (textArea.isEditable()) {
                return;
            }

            switch (Content.ContentState.get(data.state)) {
                case CONTENT_STATE_NORMAL:
                    Disposable subscribe = clipboardViewModel.state(data.id,
                            Content.ContentState.CONTENT_STATE_DELETE)
                            .subscribe(c -> {
                                BeanUtils.copyProperties(c, data);
                            });
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
        VIEW_STATE_STAR,
        VIEW_STATE_SETTING,
    }
}
