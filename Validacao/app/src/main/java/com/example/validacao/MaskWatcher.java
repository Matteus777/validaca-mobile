package com.example.validacao;

import android.text.Editable;
import android.text.TextWatcher;

public class MaskWatcher implements TextWatcher {
    private boolean isRunning = false;
    private boolean isDeleting = false;
    private  String mask;
    private boolean isDocument = false;

    public MaskWatcher(String mask) {
        this.mask = mask;
    }
    public MaskWatcher(String mask,boolean isDocument)
    {
        this.isDocument=isDocument;
        this.mask = mask;
    }

    public static MaskWatcher buildDocument() {

        return new MaskWatcher("###.###.###-##",true);
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
        isDeleting = count > after;
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable editable){
        if(isDocument){
            mask = "###.###.###-##";
            if(editable.length()>14) {
                mask = "##.###.###/####-##";
            }
        }
        if (isRunning || isDeleting) {
            return;
        }
        isRunning = true;

        int editableLength = editable.length();
        if (editableLength < mask.length()) {
            if (mask.charAt(editableLength) != '#') {
                editable.append(mask.charAt(editableLength));
            } else if (mask.charAt(editableLength-1) != '#') {
                editable.insert(editableLength-1, mask, editableLength-1, editableLength);
            }
        }

        isRunning = false;
    }
}