package com.etb.filemanager.manager.files.editor;

import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.util.Log;
import androidx.appcompat.widget.AppCompatEditText;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class CodeEditor extends AppCompatEditText {
    private Context mContext;

    public CodeEditor(Context context, AttributeSet attrs){
        super(context);
        mContext = context;

    }

    public void setFileType(FileType fileType){
            if (fileType == FileType.JAVA){
                applyJavaSyntax(this);
            }
    }

    public void setCodebyStorage(String path){
        setText(readFileAsString(mContext, path));
        //applyJavaSyntax(this);
    }


    private void applyJavaSyntax(CodeEditor editor){
        SpannableString spannableString = new SpannableString(editor.getText());

        for (String keyword : editor.getJavaKeywords()){
            int startIndex = 0;
            while (startIndex != -1){
                startIndex = editor.getText().toString().indexOf(keyword, startIndex);
                if (startIndex != -1){
                    int endindex = startIndex + keyword.length();
                    spannableString.setSpan(new ForegroundColorSpan(Color.BLUE), startIndex, endindex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    startIndex = endindex;
                }
            }
        }



        editor.setText(spannableString);
    }

    public static String readFileAsString(Context context, String fileName) {
        StringBuilder stringBuilder = new StringBuilder();

        try {
            FileInputStream fis = context.openFileInput(fileName);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader bufferedReader = new BufferedReader(isr);

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }

            bufferedReader.close();
            isr.close();
            fis.close();
        } catch (IOException e) {
            Log.e("Erro ao ler arquivo", "Erro: " + e.getMessage());
            return "erro ao ler arquivo while public void";
        }

        return stringBuilder.toString();
    }


    private  List<String> getJavaKeywords() {
        List<String> keywords = new ArrayList<>();
        keywords.add("abstract");
        keywords.add("assert");
        keywords.add("boolean");
        keywords.add("break");
        keywords.add("byte");
        keywords.add("case");
        keywords.add("catch");
        keywords.add("char");
        keywords.add("class");
        keywords.add("const");
        keywords.add("continue");
        keywords.add("default");
        keywords.add("do");
        keywords.add("double");
        keywords.add("else");
        keywords.add("enum");
        keywords.add("extends");
        keywords.add("final");
        keywords.add("finally");
        keywords.add("float");
        keywords.add("for");
        keywords.add("if");
        keywords.add("implements");
        keywords.add("import");
        keywords.add("instanceof");
        keywords.add("int");
        keywords.add("interface");
        keywords.add("long");
        keywords.add("native");
        keywords.add("new");
        keywords.add("package");
        keywords.add("private");
        keywords.add("protected");
        keywords.add("public");
        keywords.add("return");
        keywords.add("short");
        keywords.add("static");
        keywords.add("strictfp");
        keywords.add("super");
        keywords.add("switch");
        keywords.add("synchronized");
        keywords.add("this");
        keywords.add("throw");
        keywords.add("throws");
        keywords.add("transient");
        keywords.add("try");
        keywords.add("void");
        keywords.add("volatile");
        keywords.add("while");


        return keywords;
    }




}


