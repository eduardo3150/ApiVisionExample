package com.eduardo.visionexample;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.services.vision.v1.Vision;
import com.google.api.services.vision.v1.VisionRequestInitializer;
import com.google.api.services.vision.v1.model.AnnotateImageRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesResponse;
import com.google.api.services.vision.v1.model.FaceAnnotation;
import com.google.api.services.vision.v1.model.Feature;
import com.google.api.services.vision.v1.model.Image;
import com.google.api.services.vision.v1.model.TextAnnotation;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    Vision vision;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Vision.Builder visionBuilder = new Vision.Builder(
                new NetHttpTransport(),
                new AndroidJsonFactory(),
                null
        );

        visionBuilder.setVisionRequestInitializer(new VisionRequestInitializer("AIzaSyCSpx-47A0p8ch7Nc4FSbGl8rpUoxU3rzA"));

        vision = visionBuilder.build();

        setVisionRequestInitializer();
    }

    private void setVisionRequestInitializer() {


        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                InputStream inputStream = getResources().openRawResource(R.raw.tarjeta);
                try {
                    byte[] photoData = IOUtils.toByteArray(inputStream);
                    inputStream.close();

                    Image inputImage = new Image();
                    inputImage.encodeContent(photoData);
                    Feature desiredFeature = new Feature();
                    desiredFeature.setType("TEXT_DETECTION");

                    AnnotateImageRequest request = new AnnotateImageRequest();
                    request.setImage(inputImage);
                    request.setFeatures(Arrays.asList(desiredFeature));

                    BatchAnnotateImagesRequest batchRequest = new BatchAnnotateImagesRequest();

                    batchRequest.setRequests(Arrays.asList(request));

                    BatchAnnotateImagesResponse batchResponse = vision.images().annotate(batchRequest).execute();

                    final TextAnnotation textAnnotation = batchResponse.getResponses().get(0).getFullTextAnnotation();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, textAnnotation.getText(), Toast.LENGTH_SHORT).show();

                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });


    }
}
