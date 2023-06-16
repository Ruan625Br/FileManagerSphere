package com.etb.filemanager.files.file.properties;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import org.jetbrains.annotations.NotNull;

public class ViewStateAdapter extends FragmentStateAdapter {


    public ViewStateAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle){
        super(fragmentManager, lifecycle);
    }
    @NonNull
    @NotNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) {
            return new BasicPropertiesFragment();
        }
        return new BasicPropertiesFragment();
    }

    @Override
    public int getItemCount() {
        return 1;
    }
}
