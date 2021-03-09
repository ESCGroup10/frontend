package com.example.singhealthapp.auditor;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class QuestionBank {

    private Context mContext;
    private ArrayList<String> groupedChecklistQuestions;

    public QuestionBank(Context context) {
        this.mContext = context;
    }

    public ArrayList<String> getQuestions(String fileName) {
        groupedChecklistQuestions = new ArrayList<>();

        AssetManager assetManager = mContext.getAssets();

        try {
            InputStream inputStream = assetManager.open(fileName);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;

            while ((line = reader.readLine()) != null) {
                groupedChecklistQuestions.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return groupedChecklistQuestions;
    }

    String readLineIgnoreCR(BufferedReader reader) throws IOException {
        int c = reader.read();
        String line = "";
        while(c >= 0)
        {
            if((char) c == '>') {
                line += '\n';
                line += '-';
            }
            else if((char) c == '\n' || (char) c == '\r')
                return line;
            line += (char) c;

        }
        return line;
    }
}
