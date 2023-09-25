/*
 * Copyright (c)  2023  Juan Nascimento
 * Part of FileManagerSphere - ViewStateAdapter.java
 * SPDX-License-Identifier: GPL-3.0-or-later
 * More details at: https://www.gnu.org/licenses/
 */

package com.etb.filemanager.files.provider.archive.common.properties;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.etb.filemanager.manager.adapter.FileModel;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class ViewStateAdapter extends FragmentStateAdapter {

    private final FileModel fileItem;

    public ViewStateAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle, FileModel fileModel) {
        super(fragmentManager, lifecycle);
        fileItem = fileModel;

    }

    @NonNull
    @NotNull
    @Override
    public Fragment createFragment(int position) {
        return Objects.requireNonNull(new PropertiesFragment().getFragmentPropriedades(position, fileItem));

    }




    @Override
    public int getItemCount() {
        return 2;
    }
}
