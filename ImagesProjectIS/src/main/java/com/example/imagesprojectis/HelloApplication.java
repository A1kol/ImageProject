package com.example.imagesprojectis;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.effect.BoxBlur;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        HBox root = new HBox(20);  // Главное горизонтальное окно
        VBox leftPane = new VBox(10); // Левая панель с кнопками
        VBox rightPane = new VBox(10); // Правая панель с результатом

        leftPane.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cccccc; -fx-border-width: 1px;");
        rightPane.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cccccc; -fx-border-width: 1px;");

        // Картинки
        ImageView original = new ImageView();
        ImageView result = new ImageView();
        original.setFitWidth(300);
        original.setFitHeight(500);
        result.setFitWidth(300);
        result.setFitHeight(500);
        result.setVisible(false); // Изначально скрыта картинка с эффектом

        // Кнопки
        Button img = new Button("Choose image");
        Button processing = new Button("Process");
        Button save = new Button("Save");
        ComboBox<String> choice = new ComboBox<>();
        choice.getItems().addAll("Grayscale", "Inverse", "Blur");

        // Стиль кнопок
        img.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        processing.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
        save.setStyle("-fx-background-color: #FF5722; -fx-text-fill: white;");

        // Логика выбора картинки
        img.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.bmp", "*.gif"));
            File file = fileChooser.showOpenDialog(stage);

            if (file != null) {
                Image image = new Image(file.toURI().toString());
                original.setImage(image);
                result.setVisible(false); // Скрываем результат, если выбираем новое изображение
            }
        });

        // Логика обработки изображения
        processing.setOnAction(e -> {
            String selectedEffect = choice.getValue();
            if (selectedEffect != null && original.getImage() != null) {
                Image image = original.getImage();
                Image processedImage = applyEffect(image, selectedEffect);
                result.setImage(processedImage);
                result.setVisible(true); // Показываем результат после применения эффекта
            }
        });

        // Логика сохранения изображения
        save.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
            File file = fileChooser.showSaveDialog(stage);

            if (file != null) {
                try {
                    BufferedImage bufferedImage = convertFXImageToBufferedImage(result.getImage());
                    ImageIO.write(bufferedImage, "png", file); // Сохраняем результат как PNG
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

        // Расположение элементов на экране
        leftPane.getChildren().addAll(img, choice, processing, save, original);
        rightPane.getChildren().add(result);
        root.getChildren().addAll(leftPane, rightPane);

        Scene scene = new Scene(root, 800, 600);
        stage.setTitle("Image Processing");
        stage.setScene(scene);
        stage.show();
    }

    // Метод для преобразования JavaFX изображения в BufferedImage
    private BufferedImage convertFXImageToBufferedImage(Image fxImage) {
        int width = (int) fxImage.getWidth();
        int height = (int) fxImage.getHeight();
        WritableImage writableImage = new WritableImage(width, height);
        PixelReader pixelReader = fxImage.getPixelReader();
        PixelWriter pixelWriter = writableImage.getPixelWriter();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color color = pixelReader.getColor(x, y);
                pixelWriter.setColor(x, y, color);
            }
        }

        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color color = writableImage.getPixelReader().getColor(x, y);
                bufferedImage.setRGB(x, y, color.hashCode());
            }
        }
        return bufferedImage;
    }

    // Метод для применения эффекта к изображению
    public static Image applyEffect(Image image, String effect) {
        int width = (int) image.getWidth();
        int height = (int) image.getHeight();

        PixelReader reader = image.getPixelReader();
        WritableImage newImage = new WritableImage(width, height);
        PixelWriter pixelWriter = newImage.getPixelWriter();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int argb = reader.getArgb(x, y);
                int red = (argb >> 16) & 0xFF;
                int green = (argb >> 8) & 0xFF;
                int blue = argb & 0xFF;

                if (effect.equals("Grayscale")) {
                    int gray = (int) (0.299 * red + 0.587 * green + 0.114 * blue);
                    red = green = blue = gray;
                } else if (effect.equals("Inverse")) {
                    red = 255 - red;
                    green = 255 - green;
                    blue = 255 - blue;
                }

                Color color = Color.rgb(red, green, blue);
                pixelWriter.setColor(x, y, color);
            }
        }

        // Применяем эффект размытия
        if (effect.equals("Blur")) {
            BoxBlur blur = new BoxBlur(10, 10, 3);
            ImageView imageView = new ImageView(newImage);
            imageView.setEffect(blur);
            return imageView.snapshot(null, null); // Создаем новое изображение с эффектом
        }

        return newImage;
    }

    public static void main(String[] args) {
        launch();
    }
}




