package com.etb.filemanager.manager.editor;

import android.app.Application;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.etb.filemanager.files.util.FileUtil;

import org.jetbrains.annotations.Contract;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class CodeEditorViewModel extends AndroidViewModel {
    public static final String TAG = CodeEditorViewModel.class.getSimpleName();
    public static final int XML_TYPE_NONE = 0;
    public static final int XML_TYPE_AXML = 1;
    public static final int XML_TYPE_ABX = 2;
    private static final Map<String, String> EXT_TO_LANGUAGE_MAP = new HashMap<String, String>() {{
        put("cmd", "sh");
        put("htm", "xml");
        put("html", "xml");
        put("kt", "kotlin");
        put("prop", "properties");
        put("tokens", "properties");
        put("xhtml", "xml");
    }};
    private final MutableLiveData<String> mContentLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> mSaveFileLiveData = new MutableLiveData<>();
    @Nullable
    private String mLanguage;
    @Nullable
    private File mSourceFile;
    private CodeEditorFragment.Options mOptions;
    public CodeEditorViewModel(@NonNull Application application) {
        super(application);
    }

    @Contract("!null -> !null")
    @Nullable
    private static String getLanguageFromExt(@Nullable String ext) {
        String lang = EXT_TO_LANGUAGE_MAP.get(ext);
        if (lang != null) return lang;
        return ext;
    }

    @Nullable
    private static String getFileExtension(@NonNull File file) {
        String name = file.getName();
        int lastDotIndex = name.lastIndexOf(".");
        if (lastDotIndex >= 0 && lastDotIndex < name.length() - 1) {
            return name.substring(lastDotIndex + 1);
        }
        return null;
    }

    public LiveData<String> getContentLiveData() {
        return mContentLiveData;
    }

    public LiveData<Boolean> getSaveFileLiveData() {
        return mSaveFileLiveData;
    }

    public void setOptions(@NonNull CodeEditorFragment.Options options) {
        mOptions = options;
        mSourceFile = options.uri != null ? new File(options.uri.getPath()) : null;
        String content = "";
        if (mSourceFile != null) {
            content = new FileUtil().readFileAsString(mSourceFile.getPath());
        }
        mContentLiveData.postValue(content);
        String extension = mSourceFile != null ? getFileExtension(mSourceFile) : null;
        mLanguage = getLanguageFromExt(extension);
    }

    @Nullable
    public File getSourceFile() {
        return mSourceFile;
    }

    public boolean saveFile(@NonNull String content) {
        FileUtil fileUtil = new FileUtil();
        return fileUtil.saveFile(mSourceFile.getPath(), content);
    }

    public boolean isReadOnly() {
        return mOptions == null || mOptions.readOnly;
    }

    public boolean canWrite() {
        return !isReadOnly() && mSourceFile != null && mSourceFile.canWrite();
    }

    public boolean isBackedByAFile() {
        return mSourceFile != null;
    }

    @NonNull
    public String getFilename() {
        if (mSourceFile == null) {
            return "untitled.txt";
        }
        return mSourceFile.getName();
    }

    @Nullable
    public String getLanguage() {
        return mLanguage;
    }

    @IntDef({XML_TYPE_NONE, XML_TYPE_AXML, XML_TYPE_ABX})
    @interface XmlType {
    }


}
