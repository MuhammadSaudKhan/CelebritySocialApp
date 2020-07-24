package com.saud.celebrityapp.ui.videos;

import android.content.Context;
import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.saud.celebrityapp.Database.Repository;
import com.saud.celebrityapp.Model.FileModel;

import java.util.ArrayList;

public class VideosViewModel extends ViewModel {

    MutableLiveData<ArrayList<FileModel>> liveData;
    public void init(Context context){
        if(liveData!=null){
            return;
        }
        liveData= Repository.getInstance(context).getVideos();
    }
    public LiveData<ArrayList<FileModel>> getVideosData(){
        return liveData;
    }

}