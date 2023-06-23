package com.etb.filemanager.files.file.properties;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.etb.filemanager.manager.adapter.FileModel;

import org.jetbrains.annotations.NotNull;

public class ViewStateAdapter extends FragmentStateAdapter {

    private final FileModel fileItem;
    private final Context context;

    public ViewStateAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle, FileModel fileModel, Context context) {
        super(fragmentManager, lifecycle);
        fileItem = fileModel;
        this.context = context;
    }

    @NonNull
    @NotNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) {
            return basicPropertiesFragment();
        }
        return new BasicPropertiesFragment();
    }

    @NonNull
    @NotNull
    public BasicPropertiesFragment basicPropertiesFragment() {
        basicPropertiesFragment().addListProperties(new FilePropertiesUtil().getBasicProperties(fileItem));

        return basicPropertiesFragment();
    }

    @Override
    public int getItemCount() {
        return 1;
    }
}
