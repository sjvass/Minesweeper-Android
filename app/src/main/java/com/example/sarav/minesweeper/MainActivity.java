package com.example.sarav.minesweeper;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    MineSweeperBoard board;
    TextView mineTV;
    TextView timeTV;
    int numMines;
    Timer timer;
    TableLayout table;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //table layout that is used to make the board
        table = findViewById(R.id.board);

        //declare timeTV equal to timeTextView
        timeTV = findViewById(R.id.timeTextView);

        //declare mineTV equal to minesTextView
        mineTV = findViewById(R.id.minesTextView);

    }

    //behavior when start game button is clicked
    public void startClick(View view) {

        //EditText views
        EditText heightET = findViewById(R.id.editTextHeight);
        EditText widthET = findViewById(R.id.editTextWidth);
        EditText minesET = findViewById(R.id.editTextMines);

        //make sure user inputs integers
        try {
            //store user input
            int boardWidth = Integer.parseInt(widthET.getText().toString());
            int boardHeight = Integer.parseInt(heightET.getText().toString());
            numMines = Integer.parseInt(minesET.getText().toString());

            //make sure there is enough room for all the mines
            if(numMines < (boardHeight * boardWidth)) {
                //make start views go away
                LinearLayout startViews = findViewById(R.id.LinearLayoutV);
                startViews.setVisibility(View.GONE);

                //make game views visable
                mineTV.setVisibility(View.VISIBLE);
                timeTV.setVisibility(View.VISIBLE);
                ImageButton resetBtn = findViewById(R.id.resetImageButton);
                resetBtn.setVisibility(View.VISIBLE);
                ImageView faceIV = findViewById(R.id.faceImageView);
                faceIV.setVisibility(View.VISIBLE);
                table.setVisibility(View.VISIBLE);


                // create game to use specifications
                createGame(boardWidth, boardHeight, numMines);
            }
            else {
                Toast tooManyMines = Toast.makeText(getApplicationContext(), "There is not enough room for that many mines."
                + "Please choose a number of mines between 0 and " + (boardHeight * boardWidth) + " or change your board dimensions.",
                        Toast.LENGTH_LONG);
                tooManyMines.setGravity(Gravity.CENTER, 0, 0);
                tooManyMines.show();
            }
        }
        catch (NumberFormatException nfe) {
            Toast invalidInput = Toast.makeText(getApplicationContext(), "Please enter a whole number in each input field.",
                    Toast.LENGTH_SHORT);
            invalidInput.setGravity(Gravity.CENTER, 0, 0);
            invalidInput.show();
        }
    }

    //generate and display a new Minesweeper game
    public void createGame(int width, int height, int mines) {
        //get screen width
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int screenWidth = metrics.widthPixels;

        //get table height
        ViewGroup.LayoutParams tableParams = table.getLayoutParams();
        int tHeight = tableParams.height;

        //create mew MineSweeperBoard object
        board = new MineSweeperBoard(width, height, mines);

        //nested for loop creates and displays board table
        for(int y = 0; y < board.height(); y++) {
            //create new table row
            TableRow row = new TableRow(this);
            //set row width to match parent
            TableRow.LayoutParams params = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                    TableRow.LayoutParams.MATCH_PARENT, 1);
            row.setLayoutParams(params);
            //center cells in row
            row.setGravity(Gravity.CENTER);

            //creates table cells
            for(int x = 0; x < board.width(); x++) {
                //create IamgeView with picture of covered cell
                ImageView cell = new ImageView(this);
                cell.setImageResource(R.drawable.minesweeper_unopened_square);

                //makes cell clickable
                cell.setClickable(true);

                //sets up click listener
                cell.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        coveredCellClicked(v);
                    }
                });

                //labels coordinates of cells
                cell.setTag(new Coordinates(x, y));

                //add cell to row
                row.addView(cell);

                //calculate how big cells would be to fill table width and height
                int cellWidth = (screenWidth - 20)/ board.width();
                int cellHeight = tHeight / board.height();

                //choose smaller demension of cells to be the size of the cell
                int cellSize = 0;
                if(cellWidth <= cellHeight) {
                    cellSize = cellWidth;
                }
                else {
                    cellSize = cellHeight;
                }

                //set cell's height and width
                ViewGroup.LayoutParams cellParams = cell.getLayoutParams();
                cellParams.height = cellSize;
                cellParams.width = cellSize;
                cell.setLayoutParams(cellParams);

            }
            //add row to table
            table.addView(row);
        }


        mineTV.setText(Integer.toString(mines));

        //set up game timer
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        int timeSecs = Integer.parseInt(timeTV.getText().toString());
                        timeSecs += 1;
                        timeTV.setText(Integer.toString(timeSecs));
                    }
                });

            }
        }, 0, 1000);
    }


    //behavior when covered cell is clicked
    public void coveredCellClicked(View cell) {


        //makes cell unclickable
        cell.setClickable(false);
    }

    //behavior when reset button is clicked
    public void resetClicked(View view) {

        //make game views disappear
        mineTV.setVisibility(View.GONE);
        timeTV.setVisibility(View.GONE);
        ImageButton resetBtn = findViewById(R.id.resetImageButton);
        resetBtn.setVisibility(View.GONE);
        ImageView faceIV = findViewById(R.id.faceImageView);
        faceIV.setVisibility(View.GONE);
        table.setVisibility(View.GONE);

        //cancel timer
        timer.cancel();

        //reset timeTV to 0
        timeTV.setText("0");

        //delete table rows
        cleanTable(table);

        //get EditText views
        EditText heightET = findViewById(R.id.editTextHeight);
        EditText widthET = findViewById(R.id.editTextWidth);
        EditText minesET = findViewById(R.id.editTextMines);

        //empty EditText views
        heightET.setText("");
        widthET.setText("");
        minesET.setText("");

        //Make starting views appear
        LinearLayout startViews = findViewById(R.id.LinearLayoutV);
        startViews.setVisibility(View.VISIBLE);

    }

    //deletes all elements in table
    private void cleanTable(TableLayout table) {

        int childCount = table.getChildCount();

        // Remove all rows
        if (childCount > 0) {
            table.removeViews(0, childCount);
        }
    }

}
