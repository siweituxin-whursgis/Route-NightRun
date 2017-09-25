package com.example.huyigong.route_nightrun.substances;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 说说对象模型
 */
public class ShuoshuoModel implements Parcelable {
    /**
     * 用户头像
     */
    String UserImage;
    /**
     * 用户姓名
     */
    String UserName;
    /**
     * 说说内容
     */
    String Content;
    /**
     * 说说图片
     */
    String ContentImage;

    public ShuoshuoModel() {
        super();
    }

    public ShuoshuoModel(Parcel in) {
        UserImage = in.readString();
        UserName = in.readString();
        Content = in.readString();
        ContentImage = in.readString();
    }

    public static final Creator<ShuoshuoModel> CREATOR = new Creator<ShuoshuoModel>() {
        @Override
        public ShuoshuoModel createFromParcel(Parcel in) {
            return new ShuoshuoModel(in);
        }

        @Override
        public ShuoshuoModel[] newArray(int size) {
            return new ShuoshuoModel[size];
        }
    };

    public String getUserImage() {
        return UserImage;
    }

    public void setUserImage(String userImage) {
        UserImage = userImage;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getContent() {
        return Content;
    }

    public void setContent(String content) {
        Content = content;
    }

    public String getContentImage() {
        return ContentImage;
    }

    public void setContentImage(String contentImage) {
        ContentImage = contentImage;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(UserImage);
        dest.writeString(UserName);
        dest.writeString(Content);
        dest.writeString(ContentImage);
    }
}
