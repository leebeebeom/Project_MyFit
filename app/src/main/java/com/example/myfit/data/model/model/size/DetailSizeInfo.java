package com.example.myfit.data.model.model.size;

import java.util.Objects;

public class DetailSizeInfo {
    private String firstInfo, secondInfo, thirdInfo, fourthInfo, fifthInfo, sixthInfo;

    public String getFirstInfo() {
        return firstInfo;
    }

    public void setFirstInfo(String firstInfo) {
        this.firstInfo = firstInfo;
    }

    public String getSecondInfo() {
        return secondInfo;
    }

    public void setSecondInfo(String secondInfo) {
        this.secondInfo = secondInfo;
    }

    public String getThirdInfo() {
        return thirdInfo;
    }

    public void setThirdInfo(String thirdInfo) {
        this.thirdInfo = thirdInfo;
    }

    public String getFourthInfo() {
        return fourthInfo;
    }

    public void setFourthInfo(String fourthInfo) {
        this.fourthInfo = fourthInfo;
    }

    public String getFifthInfo() {
        return fifthInfo;
    }

    public void setFifthInfo(String fifthInfo) {
        this.fifthInfo = fifthInfo;
    }

    public String getSixthInfo() {
        return sixthInfo;
    }

    public void setSixthInfo(String sixthInfo) {
        this.sixthInfo = sixthInfo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DetailSizeInfo)) return false;
        DetailSizeInfo that = (DetailSizeInfo) o;
        return Objects.equals(getFirstInfo(), that.getFirstInfo()) &&
                Objects.equals(getSecondInfo(), that.getSecondInfo()) &&
                Objects.equals(getThirdInfo(), that.getThirdInfo()) &&
                Objects.equals(getFourthInfo(), that.getFourthInfo()) &&
                Objects.equals(getFifthInfo(), that.getFifthInfo()) &&
                Objects.equals(getSixthInfo(), that.getSixthInfo());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getFirstInfo(), getSecondInfo(), getThirdInfo(), getFourthInfo(), getFifthInfo(), getSixthInfo());
    }
}
