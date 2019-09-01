package com.example.sarav.minesweeper;

// -------------------------------------------------------------------------
/**
 * Write a one-sentence summary of your class here. Follow it with additional
 * details about its purpose, what abstraction it represents, and how to use it.
 *
 * @author Sara Vass (sjv219)
 * @version 2016.02.25
 */

public class MineSweeperBoard
    extends MineSweeperBoardBase
{
    private MineSweeperCell[][] board;
    private int                 height;
    private int                 width;


    // ----------------------------------------------------------
    /**
     * Create a new MineSweeperBoard object.
     *
     * @param width
     *            width of board
     * @param height
     *            height of board
     * @param mines
     *            number of mines to be placed on board
     */
    public MineSweeperBoard(int width, int height, int mines)
    {
        this.height = height;
        this.width = width;
        board = new MineSweeperCell[width][height];

        // sets all cells to covered
        for (int i = 0; i < width; i++)
        {
            for (int j = 0; j < height; j++)
            {
                board[i][j] = MineSweeperCell.COVERED_CELL;
            }
        }

        // placing mines
        int minesPlaced = 0;
        while (minesPlaced < mines)
        {
            int x = (int)(Math.random() * width);
            int y = (int)(Math.random() * height);
            if (board[x][y] != MineSweeperCell.MINE)
            {
                board[x][y] = MineSweeperCell.MINE;
                minesPlaced = minesPlaced + 1;
            }
        }

    }


    @Override
    public int width()
    {
        return width;
    }


    @Override
    public int height()
    {
        return height;
    }


    @Override
    public MineSweeperCell getCell(int x, int y)
    {
        if (x < width && x >= 0 && y < height && y >= 0)
        {
            return board[x][y];
        }

        else
        {
            return MineSweeperCell.INVALID_CELL;
        }
    }


    @Override
    protected void setCell(int x, int y, MineSweeperCell value)
    {
        if (getCell(x, y) != MineSweeperCell.INVALID_CELL)
        {
            board[x][y] = value;
        }
    }


    @Override
    public void flagCell(int x, int y)
    {
        if (getCell(x, y) == MineSweeperCell.COVERED_CELL)
        {
            setCell(x, y, MineSweeperCell.FLAG);
        }
        else if (getCell(x, y) == MineSweeperCell.MINE)
        {
            setCell(x, y, MineSweeperCell.FLAGGED_MINE);
        }
        else if (getCell(x, y) == MineSweeperCell.FLAG)
        {
            setCell(x, y, MineSweeperCell.COVERED_CELL);
        }
        else if (getCell(x, y) == MineSweeperCell.FLAGGED_MINE)
        {
            setCell(x, y, MineSweeperCell.MINE);
        }
    }


    @Override
    public int numberOfAdjacentMines(int x, int y)
    {
        int count = 0;

        for (int i = x - 1; i <= x + 1; i++)
        {
            for (int j = y - 1; j <= y + 1; j++)
            {
                if (getCell(i, j) == MineSweeperCell.MINE
                    || getCell(i, j) == MineSweeperCell.FLAGGED_MINE
                    || getCell(i, j) == MineSweeperCell.UNCOVERED_MINE)
                {
                    count = count + 1;
                }
            }
        }

        return count;
    }


    @Override
    public void uncoverCell(int x, int y)
    {
        if (getCell(x, y) == MineSweeperCell.COVERED_CELL
            || getCell(x, y) == MineSweeperCell.MINE)
        {
            if (getCell(x, y) == MineSweeperCell.MINE)
            {
                setCell(x, y, MineSweeperCell.UNCOVERED_MINE);
            }

            else
            {
                setCell(
                    x,
                    y,
                    MineSweeperCell.adjacentTo(numberOfAdjacentMines(x, y)));
            }
        }
    }


    @Override
    public void revealBoard()
    {
        for (int i = 0; i < width; i++)
        {
            for (int j = 0; j < height; j++)
            {
                uncoverCell(i, j);

            }
        }
    }


    @Override
    public boolean isGameLost()
    {
        for (int i = 0; i < width; i++)
        {
            for (int j = 0; j < height; j++)
            {
                if (board[i][j] == MineSweeperCell.UNCOVERED_MINE)
                {
                    return true;
                }
            }
        }
        return false;
    }


    @Override
    public boolean isGameWon()
    {
        for (int i = 0; i < width; i++)
        {
            for (int j = 0; j < height; j++)
            {
                if (board[i][j] == MineSweeperCell.FLAG
                    || board[i][j] == MineSweeperCell.MINE
                    || board[i][j] == MineSweeperCell.COVERED_CELL
                    || board[i][j] == MineSweeperCell.UNCOVERED_MINE)
                {
                    return false;
                }

            }
        }

        return true;
    }
}
