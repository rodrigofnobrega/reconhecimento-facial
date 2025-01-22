package com.app.two.factor.auth.utils;

import org.bytedeco.opencv.opencv_face.FaceRecognizer;
import org.bytedeco.opencv.opencv_face.LBPHFaceRecognizer;
import org.bytedeco.opencv.global.opencv_imgcodecs;
import org.bytedeco.opencv.global.opencv_imgproc;
import org.bytedeco.opencv.opencv_core.*;
import org.bytedeco.opencv.opencv_objdetect.CascadeClassifier;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Paths;

public class FacialRecognizer {
    // Caminhos para os arquivos necessários
    private String modelPath = Paths.get("src/main/resources/modelo.xml").toString();
    private String cascadePath = Paths.get("src/main/resources/haarcascade_frontalface_alt2.xml").toString();

    public int recognizeFace(MultipartFile multipartFile) throws IOException {
        // Carregar o modelo treinado
        FaceRecognizer recognizer = LBPHFaceRecognizer.create();
        recognizer.read(modelPath);

        // Carregar o Haar Cascade para detecção de rostos
        CascadeClassifier faceDetector = new CascadeClassifier(cascadePath);

        // Converter MultipartFile para File
        File file = new File("temp_image.jpg");

        try (OutputStream os = new FileOutputStream(file)) {
            os.write(multipartFile.getBytes());
        }

        if (!file.exists()) {
            System.out.println("Arquivo não foi criado.");
            throw new RuntimeException("Erro ao abrir o arquivo");
        }

        System.out.println("Arquivo criado com sucesso em: " + file.getAbsolutePath());

        // Carregar a imagem de entrada
        Mat inputImage = opencv_imgcodecs.imread(file.getAbsolutePath(), opencv_imgcodecs.IMREAD_GRAYSCALE);
        if (inputImage.empty()) {
            System.out.println("Erro ao carregar a imagem de entrada.");
            throw new RuntimeException("Erro ao carregar a imagem de entrada");
        }

        // Equalizar o histograma da imagem
        opencv_imgproc.equalizeHist(inputImage, inputImage);

        // Detectar rostos na imagem
        RectVector detectedFaces = new RectVector();
        faceDetector.detectMultiScale(inputImage, detectedFaces);

        // Processar cada rosto detectado
        for (int i = 0; i < detectedFaces.size(); i++) {
            // Cortar a região do rosto detectado
            Rect faceRegion = detectedFaces.get(i);
            Mat face = new Mat(inputImage, faceRegion);

            // Reconhecer o rosto
            int label = recognizer.predict_label(face);
            System.out.println("Rosto reconhecido com o label: " + label);
            return label;
        }



        throw new RuntimeException("Nenhum rosto foi encontrado.");
    }
}
