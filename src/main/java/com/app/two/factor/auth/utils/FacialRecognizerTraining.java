package com.app.two.factor.auth.utils;

import org.bytedeco.opencv.global.opencv_core;
import org.bytedeco.opencv.global.opencv_imgcodecs;
import org.bytedeco.opencv.global.opencv_imgproc;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.MatVector;
import org.bytedeco.opencv.opencv_face.FaceRecognizer;
import org.bytedeco.opencv.opencv_face.LBPHFaceRecognizer;
import org.springframework.scheduling.annotation.Async;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FacialRecognizerTraining {
    public int trainingModel(String userName) {
        String datasetPath = Paths.get("src/main/resources/assets/database/").toString();
        List<Mat> images = new ArrayList<>();
        List<Integer> labels = new ArrayList<>();

        int labelUser = -1;

        File dataset = new File(datasetPath);
        File[] personFolders = dataset.listFiles();

        if (personFolders != null) {
            int label = 0; // Cada pessoa recebe um ID único
            System.out.println(Arrays.toString(personFolders));
            for (File personFolder : personFolders) {
                if (personFolder.isDirectory()) {
                    File[] faceImages = personFolder.listFiles();

                    System.out.println("Pessoa: " + personFolder.getName());
                    System.out.println("Label: " + label);

                    if (personFolder.getName().equalsIgnoreCase(userName)) {
                        labelUser = label;
                    }

                    if (faceImages != null) {
                        for (File faceImage : faceImages) {
                            Mat img = opencv_imgcodecs.imread(faceImage.getAbsolutePath(), opencv_imgcodecs.IMREAD_GRAYSCALE);
                            opencv_imgproc.equalizeHist(img, img);
                            images.add(img);
                            labels.add(label);
                        }
                    }

                    label++; // Próxima pessoa
                }
            }
        }

        System.out.println("Imagens: " + images.size() + ", Labels: " + labels.size());

        // Converter listas para arrays do OpenCV
        MatVector imagesArray = new MatVector(images.size());
        Mat labelsArray = new Mat(images.size(), 1, opencv_core.CV_32SC1);

        for (int i = 0; i < images.size(); i++) {
            imagesArray.put(i, images.get(i));
            labelsArray.ptr(i).putInt(labels.get(i));
        }

        // Treinar o modelo
        FaceRecognizer recognizer = LBPHFaceRecognizer.create();
        recognizer.train(imagesArray, labelsArray);

        // Salvar o modelo treinado
        recognizer.save("src/main/resources/modelo.xml");
        System.out.println("Modelo treinado e salvo com sucesso!");

        return labelUser;
    }
}
