package com.example.myfit.data.model.size;

import androidx.room.Entity;

import com.example.myfit.data.model.DefaultInfo;

import java.util.Objects;

@Entity
public class SizeEtc extends BaseSize {
    private String option1, option2, option3, option4, option5, option6;

    public SizeEtc(DefaultInfo defaultInfo, long parentId) {
        super(defaultInfo, parentId);
    }

    public String getOption1() {
        return option1;
    }

    public void setOption1(String option1) {
        this.option1 = option1;
    }

    public String getOption2() {
        return option2;
    }

    public void setOption2(String option2) {
        this.option2 = option2;
    }

    public String getOption3() {
        return option3;
    }

    public void setOption3(String option3) {
        this.option3 = option3;
    }

    public String getOption4() {
        return option4;
    }

    public void setOption4(String option4) {
        this.option4 = option4;
    }

    public String getOption5() {
        return option5;
    }

    public void setOption5(String option5) {
        this.option5 = option5;
    }

    public String getOption6() {
        return option6;
    }

    public void setOption6(String option6) {
        this.option6 = option6;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SizeEtc)) return false;
        SizeEtc sizeEtc = (SizeEtc) o;
        return getId() == sizeEtc.getId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}

