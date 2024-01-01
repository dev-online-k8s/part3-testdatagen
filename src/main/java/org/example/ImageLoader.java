package org.example;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class ImageLoader {

    List<File> images;

    public ImageLoader() {
        loadImages();
    }

    public void loadImages() {
        File directory = new File("image");
        images = new ArrayList<>(Arrays.stream(Objects.requireNonNull(directory.listFiles())).toList());
        Collections.shuffle(images);
    }

    public int countImage() {
        return images.size();
    }

    public TestImage pickImage() {
        if (images.isEmpty()) {
            loadImages();
        }

        File image = images.remove(0);
        String fileName = image.getName();
        int dotIndex = fileName.lastIndexOf('.');
        String contents =  (dotIndex == -1) ? fileName : fileName.substring(0, dotIndex);
        return new TestImage(contents, image);
    }

}
