package org.teachingkidsprogramming.recipes.completed.section08tdd;

import java.awt.EventQueue;
import java.util.Arrays;
import java.util.Random;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.teachingextensions.logo.AStarPlayer;
import org.teachingextensions.logo.Puzzle;
import org.teachingextensions.logo.PuzzleAnimation;
import org.teachingextensions.logo.PuzzleBoard;
import org.teachingextensions.logo.PuzzlePlayer;
import org.teachingextensions.logo.PuzzleState;
import org.teachingextensions.logo.PuzzleWindow;
import org.teachingextensions.windows.MessageBox;

public class SimplePuzzle implements Runnable
{
  public Puzzle      puzzle   = null;
  public PuzzleState solution = null;
  public int[]       cells    = {0, 1, 2, 3, 4, 5, 6, 7, 8};
  //
  public static void main(String[] args)
  {
    EventQueue.invokeLater(new SimplePuzzle());
  }

  public static int[] shuffled(int[] source)
  {
    int[] copy = Arrays.copyOf(source, source.length);
    Random rnd = new Random();
    for (int i = copy.length - 1; i > 0; i--)
    {
      int index = rnd.nextInt(i + 1);
      // Simple swap
      int a = copy[index];
      copy[index] = copy[i];
      copy[i] = a;
    }
    return copy;
  }
  //
  @Override
  public void run()
  {
    this.setLookAndFeel();
    {
      do
      {
        MessageBox.showMessage("Looking for puzzle solution...");
        try
        {
          int[] shuffled = shuffled(cells);
          puzzle = new Puzzle(shuffled);
          PuzzlePlayer player = new AStarPlayer(puzzle);
          PuzzleState solution = player.solve();
          PuzzleBoard board = new PuzzleBoard(puzzle, solution);
          PuzzleWindow pw = new PuzzleWindow(board);
          new Thread(new PuzzleAnimation(board)).start();
          pw.setVisible(true);
        }
        catch (Exception e)
        {
          MessageBox.showMessage("This puzzle is not solvable, click ok to try again");
        }
      }
      //until a solution to the puzzle is found
      while (!solution.isSolution());
    }
  }
  //
  private void setLookAndFeel()
  {
    try
    {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    }
    catch (ClassNotFoundException | InstantiationException | UnsupportedLookAndFeelException
        | IllegalAccessException ignored)
    {
    }
  }
}
