package com.etb.filemanager.manager.editor;

// SPDX-License-Identifier: GPL-3.0-or-later


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.os.BundleCompat;
import androidx.core.os.ParcelCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.transition.Transition;
import androidx.transition.TransitionManager;

import com.etb.filemanager.R;
import com.etb.filemanager.activity.MainActivity;
import com.etb.filemanager.fragment.HomeFragment;
import com.etb.filemanager.fragment.RecentFragment;
import com.etb.filemanager.manager.util.MaterialDialogUtils;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.color.MaterialColors;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.elevation.SurfaceColors;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.transition.MaterialSharedAxis;

import io.github.rosemoe.sora.event.ContentChangeEvent;
import io.github.rosemoe.sora.event.PublishSearchResultEvent;
import io.github.rosemoe.sora.event.SelectionChangeEvent;
import io.github.rosemoe.sora.lang.EmptyLanguage;
import io.github.rosemoe.sora.lang.Language;
import io.github.rosemoe.sora.langs.textmate.TextMateColorScheme;
import io.github.rosemoe.sora.text.Cursor;
import io.github.rosemoe.sora.text.LineSeparator;
import io.github.rosemoe.sora.widget.CodeEditor;
import io.github.rosemoe.sora.widget.DirectAccessProps;
import io.github.rosemoe.sora.widget.SymbolInputView;
import io.github.rosemoe.sora.widget.schemes.EditorColorScheme;
import io.github.rosemoe.sora.widget.EditorSearcher.SearchOptions;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;

import java.nio.file.Paths;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.PatternSyntaxException;



public class CodeEditorFragment extends Fragment {
    public static final String ARG_OPTIONS = "options";



    public static class Options implements Parcelable {
        @Nullable
        public final Uri uri;
        @Nullable
        public final String title;
        @Nullable
        public final String subtitle;
        public final boolean readOnly;
        public final boolean javaSmaliToggle;
        public final boolean enableSharing;


        private Options(@Nullable Uri uri, @Nullable String title, @Nullable String subtitle, boolean readOnly,
                        boolean javaSmaliToggle, boolean enableSharing) {
            this.uri = uri;
            this.title = title;
            this.subtitle = subtitle;
            this.readOnly = readOnly;
            this.javaSmaliToggle = javaSmaliToggle;
            this.enableSharing = enableSharing;
        }

        protected Options(@NonNull Parcel in) {
            uri = ParcelCompat.readParcelable(in, Uri.class.getClassLoader(), Uri.class);
            title = in.readString();
            subtitle = in.readString();
            readOnly = in.readByte() != 0;
            javaSmaliToggle = in.readByte() != 0;
            enableSharing = in.readByte() != 0;
        }

        public static final Creator<Options> CREATOR = new Creator<Options>() {
            @Override
            public Options createFromParcel(Parcel in) {
                return new Options(in);
            }

            @Override
            public Options[] newArray(int size) {
                return new Options[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(@NonNull Parcel dest, int flags) {
            dest.writeParcelable(uri, flags);
            dest.writeString(title);
            dest.writeString(subtitle);
            dest.writeByte((byte) (readOnly ? 1 : 0));
            dest.writeByte((byte) (javaSmaliToggle ? 1 : 0));
            dest.writeByte((byte) (enableSharing ? 1 : 0));
        }

        public static class Builder {
            @Nullable
            private Uri uri;
            @Nullable
            private String title;
            @Nullable
            private String subtitle;
            private boolean readOnly = false;
            private boolean javaSmaliToggle = false;
            private boolean enableSharing = true;

            public Builder() {
            }

            public Builder(@NonNull Options options) {
                uri = options.uri;
                title = options.title;
                subtitle = options.subtitle;
                readOnly = options.readOnly;
                javaSmaliToggle = options.javaSmaliToggle;
                enableSharing = options.enableSharing;
            }

            public Builder setUri(@Nullable Uri uri) {
                this.uri = uri;
                return this;
            }

            public Builder setTitle(@Nullable String title) {
                this.title = title;
                return this;
            }

            public Builder setSubtitle(@Nullable String subtitle) {
                this.subtitle = subtitle;
                return this;
            }

            public Builder setReadOnly(boolean readOnly) {
                this.readOnly = readOnly;
                return this;
            }

            public Builder setJavaSmaliToggle(boolean javaSmaliToggle) {
                this.javaSmaliToggle = javaSmaliToggle;
                return this;
            }

            public Builder setEnableSharing(boolean enableSharing) {
                this.enableSharing = enableSharing;
                return this;
            }

            public Options build() {
                return new Options(uri, title, subtitle, readOnly, javaSmaliToggle, enableSharing);
            }
        }
    }

    private EditorColorScheme mColorScheme;
    private CodeEditor mEditor;
    private SymbolInputView mSymbolInputView;
    private TextView mPositionButton;
    private MaterialButton mLockButton;
    private LinearLayoutCompat mSearchWidget;
    private TextInputEditText mSearchView;
    private TextInputEditText mReplaceView;
    private TextInputLayout mReplaceViewContainer;
    private MaterialButton mReplaceButton;
    private MaterialButton mReplaceAllButton;
    private TextView mSearchResultCount;
    private Options mOptions;
    private SearchOptions mSearchOptions = new SearchOptions(false, false);
    private MenuItem mSaveMenu;
    private MenuItem mUndoMenu;
    private MenuItem mRedoMenu;
    private MenuItem mJavaSmaliToggleMenu;
    private MenuItem mShareMenu;
    private CodeEditorViewModel mViewModel;
    private boolean mTextModified = false;

    private MaterialToolbar topAppBar;

    private final ActivityResultLauncher<Intent> mSaveOpenedFile = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                try {
                    if (result.getResultCode() != Activity.RESULT_OK) {
                        return;
                    }
                    Intent data = result.getData();
                    if (data == null) return;
                    Uri uri = data.getData();
                    if (uri == null) return;
                    int takeFlags = data.getFlags() & (Intent.FLAG_GRANT_READ_URI_PERMISSION
                            | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    saveFile(mEditor.getText().toString());
                    if (takeFlags != 0) {
                        // Make this URI the current URI
                        mOptions = new Options.Builder(mOptions)
                                .setUri(uri)
                                .setSubtitle("teste")
                                .build();
                        mViewModel.setOptions(mOptions);
                    }
                } finally {
                    showProgressIndicator(false);
                    unlockEditor();
                }
            });
    private final OnBackPressedCallback mBackPressedCallback = new OnBackPressedCallback(true) {
        @Override
        public void handleOnBackPressed() {
            if (mSearchWidget != null && mSearchWidget.getVisibility() == View.VISIBLE) {
                hideSearchWidget();
                return;
            }
            if (mTextModified) {
                new MaterialAlertDialogBuilder(requireContext())
                        .setTitle(R.string.exit_confirmation)
                        .setMessage(R.string.file_modified_are_you_sure)
                        .setPositiveButton(R.string.no, null)
                        .setNegativeButton(R.string.yes, (dialog, which) -> {
                            setEnabled(false);
                            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                            boolean fragmentPopped = fragmentManager.popBackStackImmediate();
                            if (!fragmentPopped) {
                                RecentFragment recentFragment = new RecentFragment();
                                MainActivity activity = (MainActivity) requireActivity();
                                activity.startNewFragment(recentFragment);

                            }
                        })
                        .setNeutralButton(R.string.save_and_exit, (dialog, which) -> {
                            saveFile(mEditor.getText().toString());
                            setEnabled(false);
                            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                            boolean fragmentPopped = fragmentManager.popBackStackImmediate();
                            if (!fragmentPopped) {
                                RecentFragment recentFragment = new RecentFragment();
                                MainActivity activity = (MainActivity) requireActivity();
                                activity.startNewFragment(recentFragment);

                            }
                        })
                        .show();
                return;
            }
            setEnabled(false);


            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
            boolean fragmentPopped = fragmentManager.popBackStackImmediate();
            if (!fragmentPopped) {
                RecentFragment recentFragment = new RecentFragment();
                MainActivity activity = (MainActivity) requireActivity();
                activity.startNewFragment(recentFragment);

            }
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_code_editor, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        topAppBar = view.findViewById(R.id.topAppBar);

        initToolbar();
        mViewModel = new ViewModelProvider(this).get(CodeEditorViewModel.class);
        mOptions = Objects.requireNonNull(BundleCompat.getParcelable(requireArguments(), ARG_OPTIONS, Options.class));
        mViewModel.setOptions(mOptions);
        mColorScheme = EditorThemes.getColorScheme(requireContext());
        mEditor = view.findViewById(R.id.editor);
        mEditor.setColorScheme(mColorScheme);
        mEditor.setTypefaceText(Typeface.MONOSPACE);
        mEditor.setTextSize(14);
        mEditor.setLineSpacing(2f, 1.1f);
        mEditor.subscribeEvent(ContentChangeEvent.class, (event, unsubscribe) -> {
            if (!mTextModified && event.getAction() != ContentChangeEvent.ACTION_SET_NEW_TEXT) {
                mTextModified = true;
                getActionBar().ifPresent(actionBar -> actionBar.setSubtitle("* " + mOptions.subtitle));
            }
            mEditor.postDelayed(this::updateLiveButtons, 50);
        });
        mEditor.subscribeEvent(SelectionChangeEvent.class, (event, unsubscribe) -> updatePositionText());
        mEditor.subscribeEvent(PublishSearchResultEvent.class, (event, unsubscribe) -> {
            updatePositionText();
            updateSearchResult();
        });
        DirectAccessProps props = mEditor.getProps();
        props.useICULibToSelectWords = false;
        props.symbolPairAutoCompletion = false;
        props.deleteMultiSpaces = -1;
        props.deleteEmptyLineFast = false;
        mSymbolInputView = view.findViewById(R.id.symbol_input);
        mSymbolInputView.addSymbols(
                new String[]{"⇥", "{", "}", "(", ")", ",", ".", ";", "\"", "?", "+", "-", "*", "/"},
                new String[]{"\t", "{", "}", "(", ")", ",", ".", ";", "\"", "?", "+", "-", "*", "/"});
        mSymbolInputView.setTextColor(MaterialColors.getColor(mSymbolInputView, com.google.android.material.R.attr.colorOnSurface));
        mSymbolInputView.setBackgroundColor(SurfaceColors.SURFACE_2.getColor(requireContext()));
        mSymbolInputView.bindEditor(mEditor);
        if (mOptions.readOnly) {
            mSymbolInputView.setVisibility(View.GONE);
        }
        mSearchWidget = view.findViewById(R.id.search_container);
        mSearchView = view.findViewById(R.id.search_bar);
        mSearchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(s)) {
                    mEditor.getSearcher().stopSearch();
                } else {
                    try {
                        mEditor.getSearcher().search(s.toString(), mSearchOptions);
                    } catch (PatternSyntaxException ignore) {
                    }
                }
            }
        });
        TextInputLayout searchViewContainer = view.findViewById(R.id.search_bar_container);
        searchViewContainer.setEndIconOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(v.getContext(), v);
            Menu menu = popupMenu.getMenu();
            menu.add(R.string.search_option_match_case)
                    .setCheckable(true)
                    .setChecked(!mSearchOptions.ignoreCase)
                    .setOnMenuItemClickListener(item -> {
                        boolean ignoreCase = item.isChecked();
                        item.setChecked(ignoreCase);
                        mSearchOptions = new SearchOptions(mSearchOptions.type, ignoreCase);
                        search(mSearchView.getText());
                        return true;
                    });
            menu.add(R.string.search_option_regex)
                    .setCheckable(true)
                    .setChecked(mSearchOptions.type == SearchOptions.TYPE_REGULAR_EXPRESSION)
                    .setOnMenuItemClickListener(item -> {
                        boolean regex = !item.isChecked();
                        item.setChecked(regex);
                        int type = regex ? SearchOptions.TYPE_REGULAR_EXPRESSION : SearchOptions.TYPE_NORMAL;
                        mSearchOptions = new SearchOptions(type, mSearchOptions.ignoreCase);
                        search(mSearchView.getText());
                        return true;
                    });
            menu.add(R.string.search_option_whole_word)
                    .setCheckable(true)
                    .setChecked(mSearchOptions.type == SearchOptions.TYPE_WHOLE_WORD)
                    .setOnMenuItemClickListener(item -> {
                        boolean wholeWord = !item.isChecked();
                        item.setChecked(wholeWord);
                        int type = wholeWord ? SearchOptions.TYPE_WHOLE_WORD : SearchOptions.TYPE_NORMAL;
                        mSearchOptions = new SearchOptions(type, mSearchOptions.ignoreCase);
                        search(mSearchView.getText());
                        return true;
                    });
            popupMenu.show();
        });
        mSearchResultCount = view.findViewById(R.id.search_result_count);
        view.findViewById(R.id.previous_button).setOnClickListener(v -> {
            if (!mEditor.getSearcher().hasQuery()) {
                return;
            }
            mEditor.getSearcher().gotoPrevious();
        });
        view.findViewById(R.id.next_button).setOnClickListener(v -> {
            if (!mEditor.getSearcher().hasQuery()) {
                return;
            }
            mEditor.getSearcher().gotoNext();
        });
        mReplaceView = view.findViewById(R.id.replace_bar);
        mReplaceViewContainer = view.findViewById(R.id.replace_bar_container);
        mReplaceButton = view.findViewById(R.id.replace_button);
        mReplaceAllButton = view.findViewById(R.id.replace_all_button);
        mReplaceButton.setOnClickListener(v -> {
            if (!mEditor.getSearcher().hasQuery()) {
                return;
            }
            CharSequence query = mReplaceView.getText();
            if (!TextUtils.isEmpty(query)) {
                mEditor.getSearcher().replaceThis(query.toString());
            }
        });
        mReplaceAllButton.setOnClickListener(v -> {
            if (!mEditor.getSearcher().hasQuery()) {
                return;
            }
            CharSequence query = mReplaceView.getText();
            if (!TextUtils.isEmpty(query)) {
                mEditor.getSearcher().replaceAll(query.toString());
            }
        });
        // Setup status bar
        mLockButton = view.findViewById(R.id.lock);
        mLockButton.setOnClickListener(v -> {
            // Toggle lock
            if (mEditor.isEditable()) {
                lockEditor();
            } else {
                unlockEditor();
            }
        });
        TextView languageButton = view.findViewById(R.id.language);
        languageButton.setOnClickListener(v -> {
            // TODO: 13/9/22 Display all the supported languages
        });
        // TODO: 13/9/22 Enable setting custom tab size if possible (e.g. Makefile requires tab)
        TextView indentSizeButton = view.findViewById(R.id.tab_size);
        TextView lineSeparatorButton = view.findViewById(R.id.line_separator);
        lineSeparatorButton.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(requireContext(), v);
            Menu menu = popupMenu.getMenu();
            menu.add("test aq").setEnabled(false);
            if (!mEditor.getLineSeparator().equals(LineSeparator.CRLF)) {
                menu.add("CRLF - Windows (\\r\\n)").setOnMenuItemClickListener(menuItem -> {
                    mEditor.setLineSeparator(LineSeparator.CRLF);
                    // TODO: 18/9/22 Update line separator for existing texts
                    lineSeparatorButton.setText(mEditor.getLineSeparator().name());
                    return true;
                });
            }
            if (!mEditor.getLineSeparator().equals(LineSeparator.CR)) {
                menu.add("CR - Classic Mac OS (\\r)").setOnMenuItemClickListener(menuItem -> {
                    mEditor.setLineSeparator(LineSeparator.CR);
                    lineSeparatorButton.setText(mEditor.getLineSeparator().name());
                    return true;
                });
            }
            if (!mEditor.getLineSeparator().equals(LineSeparator.LF)) {
                menu.add("LF - Unix & Mac OS (\\n)").setOnMenuItemClickListener(menuItem -> {
                    mEditor.setLineSeparator(LineSeparator.LF);
                    lineSeparatorButton.setText(mEditor.getLineSeparator().name());
                    return true;
                });
            }
            popupMenu.show();
        });
        mPositionButton = view.findViewById(R.id.position);
        mPositionButton.setOnClickListener(v -> {
            // TODO: 13/9/22 Enable going to custom places
        });

        // Update live buttons at the start
        updateLiveButtons();
        updateStartupMenu();
      //  UiUtils.applyWindowInsetsAsPaddingNoTop(view.findViewById(R.id.editor_container));

        mViewModel.getContentLiveData().observe(getViewLifecycleOwner(), content -> {
            showProgressIndicator(false);
            if (content == null) {
              //  UIUtils.displayLongToast(R.string.failed);
                return;
            }
            mEditor.setEditorLanguage(getLanguage(mViewModel.getLanguage()));
            if (mViewModel.isReadOnly()) {
                mLockButton.setEnabled(false);
                mEditor.setEditable(false);
            } else {
                mLockButton.setEnabled(true);
            }
            languageButton.setText(mViewModel.getLanguage());
            languageButton.setEnabled(!mViewModel.isReadOnly());
            indentSizeButton.setEnabled(!mViewModel.isReadOnly());
            // TODO: 13/9/22 Use localization
            CharSequence tabSize = mEditor.getTabWidth() + " " + (mEditor.getEditorLanguage().useTab() ? "tabs" : "spaces");
            indentSizeButton.setText(tabSize);
            lineSeparatorButton.setEnabled(!mViewModel.isReadOnly());
            mEditor.setText(content);
            lineSeparatorButton.setText(mEditor.getLineSeparator().name());
            updatePositionText();
        });
        mViewModel.getSaveFileLiveData().observe(getViewLifecycleOwner(), successful -> {
            if (successful) {

                mTextModified = false;
                getActionBar().ifPresent(actionBar -> actionBar.setSubtitle(mOptions.subtitle));
            } else {

            }
        });
      /*  mViewModel.getJavaFileLiveData().observe(getViewLifecycleOwner(), uri -> {
            CodeEditorFragment.Options options = new CodeEditorFragment.Options.Builder()
                    .setUri(uri)
                    .setTitle(mOptions.title)
                    .setSubtitle(mOptions.subtitle)
                    .setEnableSharing(true)
                    .setJavaSmaliToggle(false)
                    .setReadOnly(true)
                    .build();
            CodeEditorFragment fragment = new CodeEditorFragment();
            Bundle args = new Bundle();
            args.putParcelable(CodeEditorFragment.ARG_OPTIONS, options);
            fragment.setArguments(args);
            FragmentActivity activity = requireActivity();
            activity.getSupportFragmentManager()
                    .beginTransaction()
                    .replace(((ViewGroup) requireView().getParent()).getId(), fragment)
                    .addToBackStack(null)
                    .commit();
        });
        mViewModel.loadFileContentIfAvailable();*/
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        // Handle back press
        requireActivity().getOnBackPressedDispatcher().addCallback(this, mBackPressedCallback);
    }

    @Override
    public void onResume() {
        super.onResume();
        getActionBar().ifPresent(actionBar -> {
            actionBar.setTitle(mOptions.title);
            actionBar.setSubtitle((mTextModified ? "* " : "") + mOptions.subtitle);
        });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_code_editor_actions, menu);
        mSaveMenu = menu.findItem(R.id.action_save);
        mUndoMenu = menu.findItem(R.id.action_undo);
        mRedoMenu = menu.findItem(R.id.action_redo);
        mJavaSmaliToggleMenu = menu.findItem(R.id.action_java_smali_toggle);
        mShareMenu = menu.findItem(R.id.action_share);
        updateStartupMenu();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_undo) {
            if (mEditor != null && mEditor.canUndo()) {
                mEditor.undo();
                return true;
            }
        } else if (id == R.id.action_redo) {
            if (mEditor != null && mEditor.canRedo()) {
                mEditor.redo();
                return true;
            }
        } else if (id == R.id.action_wrap) {
            if (mEditor != null) {
                mEditor.setWordwrap(!mEditor.isWordwrap());
                return true;
            }
        } else if (id == R.id.action_save) {
            if (!mViewModel.isBackedByAFile()) {
                launchIntentSaver();
            } else if (mViewModel.canWrite()) {
                saveFile(mEditor.getText().toString());
            } else {
                new MaterialAlertDialogBuilder(requireContext())
                        .setTitle("PEr")
                        .setMessage("SLa")
                        .setPositiveButton(R.string.yes, (dialog, which) -> launchIntentSaver())
                        .setNegativeButton(R.string.no, null)
                        .show();
            }
            return true;
        } else if (id == R.id.action_save_as) {
            launchIntentSaver();
            return true;
        } else if (id == R.id.action_share) {

            return true;
        } else if (id == R.id.action_java_smali_toggle) {
          //  mViewModel.generateJava(mEditor.getText().toString());
        } else if (id == R.id.action_search) {
            if (mSearchWidget != null) {
                if (mSearchWidget.getVisibility() == View.VISIBLE) {
                    hideSearchWidget();
                } else showSearchWidget();
            }
        }
        return false;
    }

    @NonNull
    private Optional<ActionBar> getActionBar() {
        FragmentActivity activity = requireActivity();
        if (activity instanceof AppCompatActivity) {
            return Optional.ofNullable(((AppCompatActivity) activity).getSupportActionBar());
        }
        throw new IllegalStateException();
    }

    private void showProgressIndicator(boolean show) {
     /*   LinearProgressIndicator progressIndicator = requireActivity().findViewById(R.id.progress_linear);
        if (progressIndicator != null) {
            if (show) {
                progressIndicator.show();
            } else {
                progressIndicator.hide();
            }
        }*/
    }

    private void updateLiveButtons() {
        boolean readOnly = mViewModel.isReadOnly();
        if (mSaveMenu != null) {
            mSaveMenu.setEnabled(mTextModified && !readOnly);
        }
        if (mUndoMenu != null) {
            mUndoMenu.setEnabled(mEditor != null && mEditor.canUndo() && !readOnly);
        }
        if (mRedoMenu != null) {
            mRedoMenu.setEnabled(mEditor != null && mEditor.canRedo() && !readOnly);
        }
        if (mReplaceViewContainer != null) {
            mReplaceViewContainer.setVisibility(readOnly ? View.GONE : View.VISIBLE);
        }
        if (mReplaceButton != null) {
            mReplaceButton.setVisibility(readOnly ? View.GONE : View.VISIBLE);
        }
        if (mReplaceAllButton != null) {
            mReplaceAllButton.setVisibility(readOnly ? View.GONE : View.VISIBLE);
        }
    }

    private void updateStartupMenu() {
        if (mViewModel == null) return;
        if (mJavaSmaliToggleMenu != null) {
         //   mJavaSmaliToggleMenu.setVisible(mViewModel.canGenerateJava());
         //   mJavaSmaliToggleMenu.setEnabled(mViewModel.canGenerateJava());
        }
        if (mShareMenu != null) {
            mShareMenu.setEnabled(mViewModel.isBackedByAFile());
        }
    }

    private void updatePositionText() {
        Cursor cursor = mEditor.getCursor();
        StringBuilder text = new StringBuilder()
                .append(1 + cursor.getLeftLine())
                .append(":")
                .append(cursor.getLeftColumn());
        if (cursor.isSelected()) {
            text.append(" (")
                    .append(cursor.getRight() - cursor.getLeft())
                    .append(" chars)");
        }
        mPositionButton.setText(text);
    }

    private void updateSearchResult() {
        int count = mEditor.getSearcher().hasQuery() ? mEditor.getSearcher().getMatchedPositionCount() : 0;
        mSearchResultCount.setText(getResources().getQuantityString(R.plurals.search_results, count, count));
    }



    private void saveFile(String content) {
        if (mViewModel == null) return;
        if (!mViewModel.saveFile(content)) {
            String fileName = mViewModel.getFilename();
            String textPositiveButton = requireContext().getString(R.string.dialog_ok);

            String saveFileError = getResources().getString(R.string.save_file_error);
            String errorMessage = getResources().getQuantityString(R.plurals.an_error_message_occurred, 1, 1, fileName);

            MaterialDialogUtils materialDialogUtils = new MaterialDialogUtils();
            materialDialogUtils.createDialogInfo(
                    saveFileError,
                    errorMessage,
                    textPositiveButton, "",
                    requireContext(),
                    false,
                    new Function1<MaterialDialogUtils.DialogInfoResult, Unit>() {
                        @Override
                        public Unit invoke(MaterialDialogUtils.DialogInfoResult dialogResult) {
                            if (dialogResult.getConfirmed()) {
                                // Lógica a ser executada se dialogResult for confirmado

                            }
                            return null;
                        }
                    }
            );


        }
    }


    @NonNull
    public Language getLanguage(@Nullable String language) {
        if (language == null || !(mColorScheme instanceof TextMateColorScheme)) {
            return new EmptyLanguage();
        }
        return Languages.getLanguage(requireContext(), language, ((TextMateColorScheme) mColorScheme).getThemeSource());
    }

    public void showSearchWidget() {
        if (mSearchWidget != null) {
            Transition sharedAxis = new MaterialSharedAxis(MaterialSharedAxis.Y, true);
            TransitionManager.beginDelayedTransition(mSearchWidget, sharedAxis);
            mSearchWidget.setVisibility(View.VISIBLE);
            mSearchView.requestFocus();
        }
    }

    public void hideSearchWidget() {
        if (mSearchWidget != null) {
            Transition sharedAxis = new MaterialSharedAxis(MaterialSharedAxis.Y, false);
            TransitionManager.beginDelayedTransition(mSearchWidget, sharedAxis);
            mSearchWidget.setVisibility(View.GONE);
            mEditor.getSearcher().stopSearch();
        }
    }

    private void search(@Nullable CharSequence s) {
        if (TextUtils.isEmpty(s)) {
            mEditor.getSearcher().stopSearch();
        } else {
            try {
                mEditor.getSearcher().search(s.toString(), mSearchOptions);
            } catch (PatternSyntaxException ignore) {
            }
        }
    }

    private void lockEditor() {
        if (mViewModel.isReadOnly()) {
            return;
        }
        if (mEditor.isEditable()) {
            mEditor.setEditable(false);
            mSymbolInputView.setVisibility(View.GONE);

        }
    }

    private void unlockEditor() {
        if (mViewModel.isReadOnly()) {
            return;
        }
        if (!mEditor.isEditable()) {
            mEditor.setEditable(true);
            mSymbolInputView.setVisibility(View.VISIBLE);

        }
    }

    private void launchIntentSaver() {
        showProgressIndicator(true);
        lockEditor();
        mSaveOpenedFile.launch(getSaveIntent());
    }

    private Intent getSaveIntent() {
        return new Intent(Intent.ACTION_CREATE_DOCUMENT)
                .setType("*/*")
                .putExtra(Intent.EXTRA_TITLE, mViewModel.getFilename());
    }

    private void initToolbar() {
        AppCompatActivity activity = (AppCompatActivity) requireActivity();
        activity.setSupportActionBar(topAppBar);
    }


}