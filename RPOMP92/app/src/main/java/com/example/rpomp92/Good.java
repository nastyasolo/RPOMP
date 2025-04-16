package com.example.rpomp92;

import android.os.Parcel;
import android.os.Parcelable;

public class Good implements Parcelable {
    private int id;
    private String name;
    private double price;
    private boolean isChecked;

    public Good(int id, String name, double price, boolean isChecked) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.isChecked = isChecked;
    }

    protected Good(Parcel in) {
        id = in.readInt();
        name = in.readString();
        price = in.readDouble();
        isChecked = in.readByte() != 0;
    }

    public static final Creator<Good> CREATOR = new Creator<Good>() {
        @Override
        public Good createFromParcel(Parcel in) {
            return new Good(in);
        }

        @Override
        public Good[] newArray(int size) {
            return new Good[size];
        }
    };

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeDouble(price);
        dest.writeByte((byte) (isChecked ? 1 : 0));
    }
}



