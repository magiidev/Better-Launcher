package com.aikodev.netheritelauncher.ui.panels.pages;

import com.aikodev.netheritelauncher.Launcher;
import com.aikodev.netheritelauncher.Main;
import com.aikodev.netheritelauncher.ui.PanelManager;
import com.aikodev.netheritelauncher.ui.panel.Panel;
import com.aikodev.netheritelauncher.utils.LauncherUtils;
import fr.litarvan.openauth.AuthenticationException;
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
        loginPanel.setMaxHeight(580);
        loginPanel.setMinHeight(580);

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
        Label noAccount = new Label("Don't have an account ?");
        Label registerHere = new Label("Buy minecraft here!");

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

        Label connectLabel = new Label("Login");
        setCanTakeAllSize(connectLabel);
        setAlignement(connectLabel, VPos.TOP, HPos.CENTER);
        connectLabel.setStyle("-fx-text-fill: #ffffff; -fx-font-size: 24px;");
        connectLabel.setTranslateY(15);

        Separator connectSeparator = new Separator();
        setCanTakeAllSize(connectSeparator);
        setAlignement(connectSeparator, VPos.TOP, HPos.CENTER);
        connectSeparator.setTranslateY(60);
        connectSeparator.setMinWidth(325);
        connectSeparator.setMaxWidth(325);
        connectSeparator.setStyle("-fx-background-color: #4B5945; -fx-opacity: 0.5;");

        Label emaiLabel = new Label("E-mail");
        setCanTakeAllSize(emaiLabel);
        setAlignement(emaiLabel, VPos.TOP, HPos.LEFT);
        emaiLabel.setStyle("-fx-text-fill: #ffffff; -fx-font-size: 14px;");
        emaiLabel.setTranslateY(110);
        emaiLabel.setTranslateX(37.5);

        TextField emailField = new TextField();
        setCanTakeAllSize(emailField);
        setAlignement(emailField, VPos.TOP, HPos.LEFT);
        emailField.setStyle("-fx-background-color: #A59D84; -fx-font-size: 16px; -fx-text-fill: e5e5e5;");
        emailField.setMaxWidth(325);
        emailField.setMaxHeight(40);
        emailField.setTranslateY(140);
        emailField.setTranslateX(37.5);

        Separator emailSeparator = new Separator();
        setCanTakeAllSize(emailSeparator);
        setAlignement(emailSeparator, VPos.TOP, HPos.CENTER);
        emailSeparator.setTranslateY(181);
        emailSeparator.setMinWidth(325);
        emailSeparator.setMaxWidth(325);
        emailSeparator.setMaxHeight(1);
        emailSeparator.setTranslateX(5);
        emailSeparator.setStyle("-fx-opacity: 0.4; -fx-background-color: #4B5945;");

        Label passwordLabel = new Label("Password");
        setCanTakeAllSize(passwordLabel);
        setAlignement(passwordLabel, VPos.TOP, HPos.LEFT);
        passwordLabel.setStyle("-fx-text-fill: #ffffff; -fx-font-size: 14px;");
        passwordLabel.setTranslateY(200);
        passwordLabel.setTranslateX(37.5);

        PasswordField passwordField = new PasswordField();
        setCanTakeAllSize(passwordField);
        setAlignement(passwordField, VPos.TOP, HPos.LEFT);
        passwordField.setStyle("-fx-background-color: #A59D84; -fx-font-size: 16px; -fx-text-fill: e5e5e5;");
        passwordField.setMaxWidth(325);
        passwordField.setMaxHeight(40);
        passwordField.setTranslateY(230);
        passwordField.setTranslateX(37.5);

        Separator passwordSeparator = new Separator();
        setCanTakeAllSize(passwordSeparator);
        setAlignement(passwordSeparator, VPos.TOP, HPos.CENTER);
        passwordSeparator.setTranslateY(271);
        passwordSeparator.setMinWidth(325);
        passwordSeparator.setMaxWidth(325);
        passwordSeparator.setMaxHeight(1);
        passwordSeparator.setTranslateX(5);
        passwordSeparator.setStyle("-fx-opacity: 0.4; -fx-background-color: #4B5945;");

        Label forgotPasswordLabel = new Label("Forgot password?");
        setCanTakeAllSize(forgotPasswordLabel);
        setAlignement(forgotPasswordLabel, VPos.CENTER, HPos.LEFT);
        forgotPasswordLabel.setStyle("-fx-text-fill: #ffffff; -fx-font-size: 12px;");
        forgotPasswordLabel.setUnderline(true);
        forgotPasswordLabel.setTranslateX(37.5);
        forgotPasswordLabel.setTranslateY(35);
        forgotPasswordLabel.setOnMouseEntered(e-> this.layout.setCursor(Cursor.HAND));
        forgotPasswordLabel.setOnMouseExited(e-> this.layout.setCursor(Cursor.DEFAULT));
        forgotPasswordLabel.setOnMouseClicked(e->
        {
            LauncherUtils.openURL("https://support.microsoft.com/en-us/account-billing/reset-a-forgotten-microsoft-account-password-eff4f067-5042-c1a3-fe72-b04d60556c37");
        });

        Button connectionButton = new Button("Connect");
        setCanTakeAllSize(connectionButton);
        setAlignement(connectionButton, VPos.CENTER, HPos.LEFT);
        connectionButton.setTranslateX(37.5);
        connectionButton.setTranslateY(80);
        connectionButton.setMinWidth(325);
        connectionButton.setMinHeight(50);
        connectionButton.getStyleClass().add("login-btn");
        connectionButton.setOnMouseEntered(e-> this.layout.setCursor(Cursor.HAND));
        connectionButton.setOnMouseExited(e-> this.layout.setCursor(Cursor.DEFAULT));
        connectionButton.setOnMouseClicked(e->
        {
            authenticate(emailField, passwordField);
        });

        Separator chooseConnectSeparator = new Separator();
        setCanTakeAllSize(chooseConnectSeparator);
        setAlignement(chooseConnectSeparator, VPos.CENTER, HPos.CENTER);
        chooseConnectSeparator.setTranslateY(160);
        chooseConnectSeparator.setMinWidth(325);
        chooseConnectSeparator.setMaxWidth(325);
        chooseConnectSeparator.setStyle("-fx-opacity: 30%; -fx-background-color: #4B5945;");

        Button chooseConnection = new Button("LOGIN WITH");
        setCanTakeAllSize(chooseConnection);
        setAlignement(chooseConnection, VPos.CENTER, HPos.CENTER);
        chooseConnection.setTranslateY(160);
        chooseConnection.setStyle("-fx-background-color: #D7D3BF; -fx-font-size: 14px; -fx-text-fill: #fff;");

        Image logoMicrosoft = new Image(Main.class.getResource("/images/microsoft.png").toExternalForm());
        ImageView imageViewMicrosoft = new ImageView(logoMicrosoft);
        imageViewMicrosoft.setPreserveRatio(true);
        imageViewMicrosoft.setFitHeight(30d);

        Button microsoftButton = new Button();
        setCanTakeAllSize(microsoftButton);
        setAlignement(microsoftButton, VPos.CENTER, HPos.CENTER);
        microsoftButton.setTranslateY(210);
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

        CheckBox crackCheckBox = new CheckBox("Cracked");
        setCanTakeAllSize(crackCheckBox);
        setAlignement(crackCheckBox, VPos.TOP, HPos.LEFT);
        crackCheckBox.setMinWidth(70);
        crackCheckBox.setMinHeight(30);
        crackCheckBox.setTranslateY(70);
        crackCheckBox.setTranslateX(38);
        crackCheckBox.setStyle("-fx-background-color: #D7D3BF; -fx-border-radius: 0px; -fx-background-insets: 0px; -fx-font-size: 14px; -fx-text-fill: #fff;");
        crackCheckBox.selectedProperty().addListener((observable, oldValue, isSelected) -> {
            if (isSelected)
            {
                offlineAuth.set(isSelected);

                mainPanel.getChildren().removeAll(passwordField, passwordLabel, forgotPasswordLabel);
                emaiLabel.setText("Pseudo");
                emaiLabel.setTranslateY(150);
                emailField.setTranslateY(180);
                emailSeparator.setTranslateY(220);
            } else {
                if (!mainPanel.getChildren().contains(passwordField))
                {
                    mainPanel.getChildren().addAll(passwordLabel, passwordField, forgotPasswordLabel);
                    emaiLabel.setText("E-mail");
                    emaiLabel.setTranslateY(110);
                    emailField.setTranslateY(140);
                    emailSeparator.setTranslateY(181);
                }
            }
        });

        mainPanel.getChildren().addAll
        (connectLabel, connectSeparator, emaiLabel, emailField, passwordLabel, passwordField, forgotPasswordLabel,
                connectionButton, chooseConnectSeparator, chooseConnection, microsoftButton, crackCheckBox);
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
