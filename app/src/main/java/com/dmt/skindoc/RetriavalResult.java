package com.dmt.skindoc;

import android.graphics.Bitmap;

public class RetriavalResult {

    private String diseaseInfo;
    private String diseaseName;
    private Bitmap diseaseImage;

    private String imageUrl;

    public RetriavalResult(String diseaseInfo, String diseaseName, Bitmap diseaseImage) {
        this.diseaseInfo = diseaseInfo;
        this.diseaseName = diseaseName;
        this.diseaseImage = diseaseImage;
    }

    public String getDiseaseInfo() {
        return diseaseInfo;
    }

    public String getDiseaseName() {
        return diseaseName;
    }

    public Bitmap getDiseaseImage() {
        return diseaseImage;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
