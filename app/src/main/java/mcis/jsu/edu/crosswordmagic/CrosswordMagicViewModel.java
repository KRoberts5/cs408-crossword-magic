package mcis.jsu.edu.crosswordmagic;

import android.app.ActivityManager;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import static android.provider.BaseColumns._ID;

public class CrosswordMagicViewModel extends ViewModel {

    /* Application Context */

    private final MutableLiveData<Context> context = new MutableLiveData<Context>();

    /* Display Properties */

    private final MutableLiveData<Integer> windowOverheadDp = new MutableLiveData<Integer>();
    private final MutableLiveData<Integer> windowHeightDp = new MutableLiveData<Integer>();
    private final MutableLiveData<Integer> windowWidthDp = new MutableLiveData<Integer>();
    private final MutableLiveData<Integer> puzzleHeight = new MutableLiveData<Integer>();
    private final MutableLiveData<Integer> puzzleWidth = new MutableLiveData<Integer>();

    /* Puzzle Data */

    private final MutableLiveData<Integer> puzzleID = new MutableLiveData<Integer>();
    private final MutableLiveData<String> puzzleName = new MutableLiveData<>();
    private final MutableLiveData<HashMap<String, Word>> words = new MutableLiveData<>();
    private final MutableLiveData<String> aClues = new MutableLiveData<String>();
    private final MutableLiveData<String> dClues = new MutableLiveData<String>();

    private final MutableLiveData<Character[][]> letters = new MutableLiveData<Character[][]>();
    private final MutableLiveData<Integer[][]> numbers = new MutableLiveData<Integer[][]>();

    //Database

    private final MutableLiveData<PuzzleDatabase> puzzleDB = new MutableLiveData<>();
    private static String[] FROM = { _ID, PuzzleDatabase.BOX_NUM_FIELD, PuzzleDatabase.DIRECTION_FIELD };
    private static String WHERE = PuzzleDatabase.PUZZLE_ID_FIELD + " = ?";
    //private static String[] FROM_SAVED_QUERY = {""};
    private static String WHERE_SAVED_QUERY = PuzzleDatabase.PUZZLE_ID_FIELD + " = ? AND " + PuzzleDatabase.DIRECTION_FIELD + " = ? AND " + PuzzleDatabase.BOX_NUM_FIELD + " = ?";


    /* Setters / Getters */

    public void setContext(Context c) {
        context.setValue(c);
    }

    public void setWindowHeightDp(int height) {
        windowHeightDp.setValue(height);
    }

    public void setWindowWidthDp(int width) {
        windowWidthDp.setValue(width);
    }

    public void setPuzzleHeight(int height) {
        puzzleHeight.setValue(height);
    }

    public void setPuzzleWidth(int width) {
        puzzleWidth.setValue(width);
    }

    public void setWindowOverheadDp(int width) {
        windowOverheadDp.setValue(width);
    }

    public void setPuzzleID(int id) {
        if ( (puzzleID.getValue() == null) || (puzzleID.getValue() != id) ) {
            getPuzzleData(id);
            puzzleID.setValue(id);

            if(puzzleDB.getValue() == null)
                puzzleDB.setValue(new PuzzleDatabase(this.getContext()));

            this.loadSavedWords();
        }
    }

    public void setDatabase(PuzzleDatabase p){
        this.puzzleDB.setValue(p);

    }

    public Context getContext() {
        return context.getValue();
    }

    public int getWindowHeightDp() {
        return windowHeightDp.getValue();
    }

    public int getWindowWidthDp() {
        return windowWidthDp.getValue();
    }

    public int getPuzzleHeight() {
        return puzzleHeight.getValue();
    }

    public int getPuzzleWidth() {
        return puzzleWidth.getValue();
    }

    public int getWindowOverheadDp() {
        return windowOverheadDp.getValue();
    }

    public int getPuzzleID() {
        return puzzleID.getValue();
    }

    public String getAClues() {
        return aClues.getValue();
    }

    public String getDClues() {
        return dClues.getValue();
    }

    public Character[][] getLetters() {
        return letters.getValue();
    }

    public Integer[][] getNumbers() {
        return numbers.getValue();
    }

    public HashMap<String, Word> getWords() {
        return words.getValue();
    }

    /* Load Puzzle Data from Input File */

    private void getPuzzleData(int id) {

        BufferedReader br = new BufferedReader(new InputStreamReader(context.getValue().getResources().openRawResource(id)));
        String line;
        String[] fields;

        HashMap<String, Word> wordMap = new HashMap<>();
        StringBuilder aString = new StringBuilder();
        StringBuilder dString = new StringBuilder();

        try {

            // Read from the input file using the "br" input stream shown above.  Your program
            // should get the puzzle height/width from the header row in the first line of the
            // input file.  Replace the placeholder values shown below with the values from the
            // file.  Get the data from the remaining rows, splitting each tab-delimited line
            // into an array of strings, which you can use to initialize a Word object.  Add each
            // Word object to the "wordMap" hash map; for the key names, use the box number
            // followed by the direction (for example, "16D" for Box # 16, Down).




            while((line = br.readLine()) != null){
                line.trim();
                fields = line.split("\t");

                if(fields.length == Word.HEADER_FIELDS){
                    int boardHeight = Integer.valueOf(fields[0]);
                    int boardWidth = Integer.valueOf(fields[1]);
                    String name = fields[2];

                    puzzleHeight.setValue(boardHeight);
                    puzzleWidth.setValue(boardWidth);
                    puzzleName.setValue(name);

                }
                else{

                    Word word = new Word(fields);

                    int wordBoxNum = word.getBox();
                    String wordDirection = word.getDirection();
                    String wordName = wordBoxNum + wordDirection;

                    wordMap.put(wordName,word);

                    if(word.getDirection().equals(Word.ACROSS)){
                        aString.append(word.getBox() + ": " + word.getClue() + "\n");
                    }
                    else{
                        dString.append(word.getBox() + ": " + word.getClue() + "\n");
                    }
                }
            }

            br.close();

        } catch (Exception e) {}

        words.setValue(wordMap);
        aClues.setValue(aString.toString());
        dClues.setValue(dString.toString());

        Character[][] aLetters = new Character[puzzleHeight.getValue()][puzzleWidth.getValue()];
        Integer[][] aNumbers = new Integer[puzzleHeight.getValue()][puzzleWidth.getValue()];

        for (int i = 0; i < aLetters.length; ++i) {
            Arrays.fill(aLetters[i], '*');
        }

        for (int i = 0; i < aNumbers.length; ++i) {
            Arrays.fill(aNumbers[i], 0);
        }

        for (HashMap.Entry<String, Word> e : wordMap.entrySet()) {

            Word w = e.getValue();

            // INSERT YOUR CODE HERE

            int row = w.getRow();
            int col = w.getColumn();
            char[] letters = w.getWord().toCharArray();
            int boxNum = w.getBox();
            String direction = w.getDirection();

            aNumbers[row][col] = boxNum;

            for(int i = 0; i < letters.length; ++ i){

                if(direction.equals(Word.DOWN)){
                    //aLetters[row + i][col] = letters[i];
                    aLetters[row + i][col] = ' ';
                }
                else{
                    //aLetters[row][col + i] = letters[i];
                    aLetters[row][col + i] = ' ';
                }
            }
        }



        this.letters.setValue(aLetters);
        this.numbers.setValue(aNumbers);

    }

    private void loadSavedWords(){
        ArrayList<String> savedWords = this.getSavedWords(this.puzzleName.getValue());
        Character[][] letterTemp = letters.getValue();


        for(String s: savedWords){
            Word w = this.words.getValue().get(s);

            if(w != null) {

                String word = w.getWord();
                String direction = w.getDirection();
                int row = w.getRow();
                int col = w.getColumn();

                for (int i = 0; i < word.length(); ++i) {
                    char c = word.charAt(i);

                    if (direction.equals(Word.ACROSS))
                        letterTemp[row][col + i] = c;
                    else
                        letterTemp[row + i][col] = c;

                }
            }
        }

        letters.setValue(letterTemp);
    }

    public Word getWordById(String id){
        Word w = words.getValue().get(id);
        return w;
    }

    public void addWordToGrid(Word w){

        Character[][] letterTemp = letters.getValue();

        String word = w.getWord();
        String direction = w.getDirection();
        int row = w.getRow();
        int col = w.getColumn();

        for(int i = 0; i < word.length(); ++i){
            char c = word.charAt(i);

            if(direction.equals(Word.ACROSS))
                letterTemp[row][col + i] = c;
            else
                letterTemp[row + i][col] = c;

        }

        letters.setValue(letterTemp);

        if(!this.isAlreadySaved(w)) {
            try {
                this.saveWord(w);
            } catch (Exception e) {
                Toast toast = Toast.makeText(getContext(), "oops", Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }

    public boolean isPuzzleComplete(){
        Character[][] puzzle = letters.getValue();

        int numRows = puzzleHeight.getValue();
        int numCols = puzzleWidth.getValue();

        boolean complete = true;

        for(int i = 0; i < numRows; ++i){
            for(int j = 0; j < numCols; ++j){
                if(puzzle[i][j].equals(' '))
                    complete = false;
            }
        }

        return complete;
    }

    public boolean compareInputToWord(String id, String input){
        boolean match = false;

        Word w = words.getValue().get(id);
        input = input.toUpperCase();

        if(w != null){
            if(input.equals(w.getWord())){
                this.addWordToGrid(w);
                match = true;
            }
        }

        return match;
    }

    public void clearPuzzle(){
        Character[][] l = letters.getValue();

        for(int i = 0; i < this.puzzleHeight.getValue(); ++i){
            for(int j = 0; j < this.puzzleWidth.getValue(); ++j){
                if(!l[i][j].equals('*')){
                    l[i][j] = ' ';
                }
            }
        }

        this.letters.setValue(l);

    }

    public void autoCompleteGame(){
        HashMap<String,Word> w = words.getValue();

        for(HashMap.Entry<String, Word> e : w.entrySet()){
            this.addWordToGrid(e.getValue());
        }
    }

    private void saveWord( Word w){
        String direction = w.getDirection();
        int boxNum = w.getBox();

        SQLiteDatabase db = puzzleDB.getValue().getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(PuzzleDatabase.PUZZLE_ID_FIELD, puzzleID.getValue());
        values.put(PuzzleDatabase.BOX_NUM_FIELD,boxNum);
        values.put(PuzzleDatabase.DIRECTION_FIELD,direction);
        db.insertOrThrow(PuzzleDatabase.TABLE_NAME, null, values);

    }

    public ArrayList<String> getSavedWords(String puzzleName){
        ArrayList<String> wordData = new ArrayList<>();

        String[] puzzleIDArg = {String.valueOf(puzzleID.getValue())};

        SQLiteDatabase db = puzzleDB.getValue().getReadableDatabase();
        Cursor cursor = db.query(PuzzleDatabase.TABLE_NAME, FROM, WHERE, puzzleIDArg, null, null, null);

        while(cursor.moveToNext()){
            int boxNum = cursor.getInt(1);
            String direction = cursor.getString(2);
            direction = direction.toUpperCase();

            wordData.add(boxNum + direction);
        }


        return wordData;

    }

    private boolean isAlreadySaved(Word w){

        boolean saved = false;

        String direction = w.getDirection();
        int boxnum = w.getBox();

        String[] selectionArg = {String.valueOf(puzzleID.getValue()), direction, String.valueOf(boxnum)};

        SQLiteDatabase db = puzzleDB.getValue().getWritableDatabase();
        Cursor c = db.query(PuzzleDatabase.TABLE_NAME,null,WHERE_SAVED_QUERY,selectionArg,null,null,null);

       if(c.getCount() > 0)
           saved = true;

        return saved;
    }

    public void clearCurrentSaveData(){

        String[] whereArg = {String.valueOf(puzzleID.getValue())};

        SQLiteDatabase db = puzzleDB.getValue().getWritableDatabase();
        db.delete(PuzzleDatabase.TABLE_NAME,WHERE,whereArg);
    }

}