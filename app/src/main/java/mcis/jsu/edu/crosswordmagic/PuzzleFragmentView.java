package mcis.jsu.edu.crosswordmagic;

import android.app.AlertDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v7.widget.GridLayout;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;

public class PuzzleFragmentView extends Fragment implements View.OnClickListener {

    View root;
    private CrosswordMagicViewModel model;
    private String userInput;
    private int boxNum = 0;

    public PuzzleFragmentView() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        model = ViewModelProviders.of(getActivity()).get(CrosswordMagicViewModel.class);
        userInput = "";

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.puzzle_fragment_view, container, false);
        return root;

    }

    @Override
    public void onStart() {

        super.onStart();
        initPuzzleView();

    }

    public void initPuzzleView() {

        /* Get View Data from Model */

        Character[][] letters = model.getLetters();
        Integer[][] numbers = model.getNumbers();

        /* Get View Properties from Model */

        int puzzleHeight = model.getPuzzleHeight();
        int puzzleWidth = model.getPuzzleWidth();
        int fragmentHeightDp = model.getWindowHeightDp();
        int fragmentWidthDp = model.getWindowWidthDp();
        int overhead = model.getWindowOverheadDp();

        /* Compute Square and Font Sizes from View Properties */

        int gridSize = Math.max(puzzleHeight, puzzleWidth);
        int squareSizeDp = ( Math.min(fragmentHeightDp - overhead, fragmentWidthDp) / gridSize );
        int letterSizeDp = (int)( squareSizeDp * 0.75 );
        int numberSizeDp = (int)( squareSizeDp * 0.2 );

        /* Acquire GridLayout Container References */

        GridLayout squaresContainer = root.findViewById(R.id.squaresContainer);
        GridLayout numbersContainer = root.findViewById(R.id.numbersContainer);

        /* Delete Any Existing Views */

        squaresContainer.removeAllViews();
        numbersContainer.removeAllViews();

        /* Set Container Sizes */

        squaresContainer.setAlignmentMode(GridLayout.ALIGN_BOUNDS);
        squaresContainer.setColumnCount(puzzleHeight);
        squaresContainer.setRowCount(puzzleWidth);

        numbersContainer.setAlignmentMode(GridLayout.ALIGN_BOUNDS);
        numbersContainer.setColumnCount(puzzleHeight);
        numbersContainer.setRowCount(puzzleWidth);

        /* Fill Containers with New TextViews */

        TextView newSquare, newNumber;

        for (int i = 0; i < (puzzleHeight * puzzleWidth); ++i) {

            /* Create New TextViews */

            newSquare = new TextView(getActivity());
            newNumber = new TextView(getActivity());

            /* Compute Row and Column; Add Tag */

            String r = String.format("%02d", (i / gridSize));
            String c = String.format("%02d", (i % gridSize));

            newSquare.setTag("Square" + r + c);
            newNumber.setTag("Square" + r + c);

            int row = Integer.parseInt(r);
            int col = Integer.parseInt(c);

            /* Set Foreground Text Color, Size, and Layout Parameters */

            newSquare.setTextColor(Color.BLACK);
            newNumber.setTextColor(Color.BLACK);

            LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

            newNumber.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
            newSquare.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

            newSquare.setTextSize(letterSizeDp);
            newNumber.setTextSize(numberSizeDp);

            params.height = squareSizeDp;
            params.width = squareSizeDp;

            newSquare.setIncludeFontPadding(false);
            newSquare.setLineSpacing(0, 0);
            newSquare.setPadding(0, (int)(-squareSizeDp * 0.05), 0, 0);
            newNumber.setPadding((int)(squareSizeDp * 0.1), 0, 0, 0);

            newSquare.setLayoutParams(params);
            newNumber.setLayoutParams(params);

            /* Does this square contain a box number?  If so, add it to number TextView */

            if (numbers[row][col] != 0) {
                newNumber.setText(Integer.toString(numbers[row][col]));
            }

            /* Does this square contain a block? */

            if (letters[row][col] == '*') {

                /* If so, fill it with solid black */
                newSquare.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.block));
            }
            else {
                /* If not, draw a thin border around it */
                newSquare.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.rectangle));
            }

            /* Set onClick() Listener */

            newSquare.setOnClickListener(this);

            /* Add to Container */

            numbersContainer.addView(newNumber, i);
            squaresContainer.addView(newSquare, i);

        }

        /* Update Grid Contents (if needed) */

        updatePuzzleView();

    }

    private void updatePuzzleView() {

        /* Update View from Model */

        Character[][] letters = model.getLetters();

        int puzzleHeight = model.getPuzzleHeight();
        int puzzleWidth = model.getPuzzleWidth();

        /* Iterate Views in Container */

        for (int i = 0; i < (puzzleHeight * puzzleWidth); ++i) {

            /* Compute Row/Column */

            int row = (i / puzzleHeight);
            int col = (i % puzzleHeight);

            /* Get View Reference; Add New Content */

            GridLayout squaresContainer = getActivity().findViewById(R.id.squaresContainer);
            TextView element = (TextView) squaresContainer.getChildAt(i);
            element.setText("" + letters[row][col]);

        }

    }

    @Override
    public void onClick(View v) {

        /* Sample OnClick() Implementation */

        /* Get Tag of Clicked View */

        String tag = v.getTag().toString();

        /* Compute Row/Column/Index from Tag */

        int row = Integer.parseInt(tag.substring(6, 8));
        int col = Integer.parseInt(tag.substring(8));
        int index = (row * model.getPuzzleHeight()) + col;

        /* Get Box Numbers from Model */

        Integer[][] numbers = model.getNumbers();

        /* Was a number clicked?  If so, display it in a Toast */

        if (numbers[row][col] != 0) {

            this.boxNum = numbers[row][col];

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(R.string.input_title);
            builder.setMessage(R.string.input_text);
            final EditText input = new EditText(getActivity());
            input.setInputType(InputType.TYPE_CLASS_TEXT);
            builder.setView(input);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface d, int i) {
                    userInput = input.getText().toString();
                    checkInput();
                }
            });
            builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface d, int i) {
                    userInput = "";
                    d.cancel();
                }
            });
            AlertDialog aboutDialog = builder.show();

            /* Update Grid Contents */
            updatePuzzleView();

        }
    }

    private void checkInput(){
        userInput.trim();

        if((!userInput.isEmpty()) && (boxNum != 0)){

            userInput = userInput.toUpperCase();
            Word aWord = model.getWordById(boxNum + Word.ACROSS);
            Word dWord = model.getWordById(boxNum + Word.DOWN);

            if(aWord != null){
                if(userInput.equals(aWord.getWord())){
                    model.addWordToGrid(aWord);

                    Toast toast=Toast.makeText(getContext(), "Congratulations! You correctly guessed " + boxNum + "A.", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
            if(dWord != null){
                if(userInput.equals(dWord.getWord())){
                    model.addWordToGrid(dWord);

                    Toast toast=Toast.makeText(getContext(), "Congratulations! You correctly guessed " + boxNum + "D.", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }

            updatePuzzleView();

            boolean puzzleComplete = model.isPuzzleComplete();

            if(puzzleComplete){
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(R.string.puzzle_complete_title);
                builder.setMessage(R.string.puzzle_complete_text);
                builder.setCancelable(false);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface d, int i) {}
                });
                AlertDialog aboutDialog = builder.show();

            }

        }
    }

}