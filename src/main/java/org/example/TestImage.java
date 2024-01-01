package org.example;

import java.io.File;

public class TestImage {
    String contents;
    File image;

    public TestImage(String contents, File image) {
        this.contents = contents;
        this.image = image;
    }

    public String getContents() {
        return contents;
    }

    public File getImage() {
        return image;
    }
}
