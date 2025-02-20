package com.magidev.betterlauncher.ui.panels.pages;

import com.magidev.betterlauncher.Launcher;
import com.magidev.betterlauncher.Main;
import com.magidev.betterlauncher.ui.PanelManager;
import com.magidev.betterlauncher.ui.panel.Panel;
import com.magidev.betterlauncher.ui.utils.lang.LanguageManager;
import com.magidev.betterlauncher.utils.LauncherUtils;
import fr.litarvan.openauth.microsoft.MicrosoftAuthResult;
import fr.litarvan.openauth.microsoft.MicrosoftAuthenticationException;
import fr.theshark34.openlauncherlib.minecraft.AuthInfos;
import fr.theshark34.openlauncherlib.util.Saver;
import fr.litarvan.openauth.microsoft.MicrosoftAuthenticator;
import javafx.application.Platform;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

public class Login extends Panel
{
    Saver saver = Launcher.getInstance().getSaver();
    AtomicBoolean offlineAuth = new AtomicBoolean(false);

    @Override
    public String getName()
    {
        return null;
    }

    @Override
    public String getStylesheetPath()
    {
        return "css/login.css";
    }

    @Override
    public void init(PanelManager panelManager)
    {
        super.init(panelManager);

        this.layout.getStyleClass().add("login-layout");

        // Panels
        GridPane loginPanel = new GridPane();
        GridPane mainPanel = new GridPane();
        GridPane bottomPanel = new GridPane();

        loginPanel.setMaxWidth(400);
        loginPanel.setMinWidth(400);
        loginPanel.setMaxHeight(295);
        loginPanel.setMinHeight(295);

        setCanTakeAllSize(loginPanel);
        setAlignement(loginPanel, VPos.CENTER, HPos.CENTER);

        GridPane bgImage = new GridPane();
        GridPane.setVgrow(bgImage, Priority.ALWAYS);
        GridPane.setHgrow(bgImage, Priority.ALWAYS);
        bgImage.getStyleClass().add("bg-image");
        this.layout.add(bgImage, 0, 0);

        // Constraints
        RowConstraints bottomConstraints = new RowConstraints();
        bottomConstraints.setValignment(VPos.BOTTOM);
        bottomConstraints.setMaxHeight(55);
        loginPanel.getRowConstraints().addAll(new RowConstraints(), bottomConstraints);
        loginPanel.add(mainPanel, 0, 0);
        loginPanel.add(bottomPanel, 0, 1);

        setCanTakeAllSize(mainPanel);
        setCanTakeAllSize(bottomPanel);

        mainPanel.getStyleClass().add("main-panel");
        bottomPanel.getStyleClass().add("bottom-panel");

        // Bottom Panel
        Label noAccount = new Label(LanguageManager.get("dontHaveAnAccount"));
        Label registerHere = new Label(LanguageManager.get("registerHere"));

        setCanTakeAllSize(noAccount);
        setAlignement(noAccount, VPos.TOP, HPos.CENTER);
        noAccount.getStyleClass().add("no-account");
        noAccount.setTranslateY(5);

        setCanTakeAllSize(registerHere);
        setAlignement(registerHere, VPos.BOTTOM, HPos.CENTER);
        registerHere.getStyleClass().add("register-here");
        registerHere.setUnderline(true);
        registerHere.setTranslateY(-10);
        registerHere.setOnMouseEntered(e -> this.layout.setCursor(Cursor.HAND));
        registerHere.setOnMouseExited(e -> this.layout.setCursor(Cursor.DEFAULT));
        registerHere.setOnMouseClicked(e ->
        {
            LauncherUtils.openURL("https://www.minecraft.net/fr-fr/");
        });

        bottomPanel.getChildren().addAll(noAccount, registerHere);
        this.layout.getChildren().add(loginPanel);

        TextField userField = new TextField();
        setCanTakeAllSize(userField);
        setAlignement(userField, VPos.TOP, HPos.LEFT);
        userField.setStyle("-fx-background-color: #C1BAA1; -fx-font-size: 16px; -fx-text-fill: white; -fx-prompt-text-fill: white;");
        userField.setPromptText(LanguageManager.get("userField"));
        userField.setMaxWidth(325);
        userField.setMaxHeight(40);
        userField.setTranslateY(70);
        userField.setTranslateX(37.5);

        Button crackedButton = new Button(LanguageManager.get("cracked"));
        setCanTakeAllSize(crackedButton);
        setAlignement(crackedButton, VPos.CENTER, HPos.LEFT);
        crackedButton.setTranslateX(37.5);
        crackedButton.setTranslateY(-80);
        crackedButton.setMinWidth(325);
        crackedButton.setMinHeight(35);
        crackedButton.getStyleClass().add("login-btn");
        crackedButton.setOnMouseEntered(e-> this.layout.setCursor(Cursor.HAND));
        crackedButton.setOnMouseExited(e-> this.layout.setCursor(Cursor.DEFAULT));
        crackedButton.setOnMouseClicked(e->
        {
            if(userField.getText().isEmpty())
            {
                new Alert(Alert.AlertType.ERROR, LanguageManager.get("userFieldCannotBeEmpty")).showAndWait();
                return;
            }

            offlineAuth.set(true);

            authenticate(userField, null);
        });

        Separator chooseConnectSeparator = new Separator();
        setCanTakeAllSize(chooseConnectSeparator);
        setAlignement(chooseConnectSeparator, VPos.CENTER, HPos.CENTER);
        chooseConnectSeparator.setTranslateY(10);
        chooseConnectSeparator.setMinWidth(325);
        chooseConnectSeparator.setMaxWidth(325);
        chooseConnectSeparator.setStyle("-fx-opacity: 30%; -fx-background-color: #4B5945;");

        Button chooseConnection = new Button(LanguageManager.get("loginWith"));
        setCanTakeAllSize(chooseConnection);
        setAlignement(chooseConnection, VPos.CENTER, HPos.CENTER);
        chooseConnection.setTranslateY(10);
        chooseConnection.setStyle("-fx-background-color: #D7D3BF; -fx-font-size: 14px; -fx-text-fill: #fff;");

        Image logoMicrosoft = new Image(Main.class.getResource("/images/microsoft.png").toExternalForm());
        ImageView imageViewMicrosoft = new ImageView(logoMicrosoft);
        imageViewMicrosoft.setPreserveRatio(true);
        imageViewMicrosoft.setFitHeight(30d);

        Button microsoftButton = new Button();
        setCanTakeAllSize(microsoftButton);
        setAlignement(microsoftButton, VPos.CENTER, HPos.CENTER);
        microsoftButton.setTranslateY(60);
        microsoftButton.setMaxWidth(250);
        microsoftButton.setMinHeight(40);
        microsoftButton.setStyle("-fx-background-color: #fff; -fx-border-radius: 0px; -fx-background-insets: 0px; -fx-font-size: 14px; -fx-text-fill: #fff;");
        microsoftButton.setGraphic(imageViewMicrosoft);
        microsoftButton.setOnMouseEntered(e-> this.layout.setCursor(Cursor.HAND));
        microsoftButton.setOnMouseExited(e-> this.layout.setCursor(Cursor.DEFAULT));
        microsoftButton.setOnMouseClicked(e->
        {
            authenticateMS();
        });

        mainPanel.getChildren().addAll
        (crackedButton, userField, chooseConnectSeparator, chooseConnection, microsoftButton);
    }

    public void authenticate(TextField emaiField, PasswordField passwordField)
    {
        if (!offlineAuth.get())
        {
            MicrosoftAuthenticator authenticator = new MicrosoftAuthenticator();

            try {
                MicrosoftAuthResult result = authenticator.loginWithCredentials(emaiField.getText(), passwordField.getText());

                saver.set("msAccessToken", result.getAccessToken());
                saver.set("msRefreshToken", result.getRefreshToken());
                saver.save();

                Launcher.getInstance().setAuthInfos(new AuthInfos(
                        result.getProfile().getName(),
                        result.getAccessToken(),
                        result.getProfile().getId(),
                        result.getXuid(),
                        result.getClientId()
                ));

                Launcher.getInstance().getLogger().info("Hello " + result.getProfile().getName());

                panelManager.showPanel(new App());
            } catch (MicrosoftAuthenticationException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("A error occurred during the connection.");
                alert.setContentText(e.getMessage());
                alert.show();
            }
        } else {
            AuthInfos infos = new AuthInfos(
                    emaiField.getText(),
                    UUID.randomUUID().toString(),
                    UUID.randomUUID().toString()
            );

            saver.set("offline-username", infos.getUsername());
            saver.save();
            Launcher.getInstance().setAuthInfos(infos);

            this.logger.info("Hello " + infos.getUsername());

            panelManager.showPanel(new App());
        }
    }

    public void authenticateMS()
    {
        MicrosoftAuthenticator authenticator = new MicrosoftAuthenticator();
        authenticator.loginWithAsyncWebview().whenComplete((response, error) -> {
            if (error != null)
            {
                Launcher.getInstance().getLogger().err(error.toString());
                Platform.runLater(()-> {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setContentText(error.getMessage());
                    alert.show();
                });

                return;
            }

            saver.set("msAccessToken", response.getAccessToken());
            saver.set("msRefreshToken", response.getRefreshToken());
            saver.save();
            Launcher.getInstance().setAuthInfos(new AuthInfos(
                    response.getProfile().getName(),
                    response.getAccessToken(),
                    response.getProfile().getId(),
                    response.getXuid(),
                    response.getClientId()
            ));

            Launcher.getInstance().getLogger().info("Hello " + response.getProfile().getName());

            Platform.runLater(() -> panelManager.showPanel(new App()));
        });
    }


}
