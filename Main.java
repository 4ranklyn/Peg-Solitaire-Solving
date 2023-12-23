
import java.util.*;

class Move {
    public final Pair<Integer, Integer> from;
    public final Pair<Integer, Integer> to;

    public Move(Pair<Integer, Integer> from, Pair<Integer, Integer> to) {
        this.from = from;
        this.to = to;
    }

    public Pair<Integer, Integer> getPegToRemove() {
        return new Pair<>((from.getFirst() + to.getFirst()) / 2, (from.getSecond() + to.getSecond()) / 2);
    }

    @Override
    public String toString() {
        return "(" + from.getFirst() + "|" + from.getSecond() + ") => (" + to.getFirst() + "|" + to.getSecond() + ")";
    }
}

class Solitaire {
    private final int[][] board;
    private final int remainingPegs;
    private static final Map<Integer, Pair<Move, Boolean>> memory = new HashMap<>();

    static class CustomPair<A, B> {
        private final A first;
        private final B second;

        public CustomPair(A first, B second) {
            this.first = first;
            this.second = second;
        }

        public A getFirst() {
            return first;
        }

        public B getSecond() {
            return second;
        }
    }

    public Solitaire() {
        this.board = new int[][]{
                {-1, -1, 1, 1, 1, -1, -1},
                {-1, -1, 1, 1, 1, -1, -1},
                {1, 1, 1, 1, 1, 1, 1},
                {1, 1, 1, 0, 1, 1, 1},
                {1, 1, 1, 1, 1, 1, 1},
                {-1, -1, 1, 1, 1, -1, -1},
                {-1, -1, 1, 1, 1, -1, -1},
        };
        this.remainingPegs = 32;
    }

    public Solitaire(int[][] board, int remainingPegs) {
        this.board = Arrays.stream(board).map(int[]::clone).toArray(int[][]::new);
        this.remainingPegs = remainingPegs;
    }

    public Solitaire move(Move move) {
        int[][] newBoard = Arrays.stream(board).map(int[]::clone).toArray(int[][]::new);

        Pair<Integer, Integer> toRemove = move.getPegToRemove();
        newBoard[move.from.getFirst()][move.from.getSecond()] = 0;
        newBoard[toRemove.getFirst()][toRemove.getSecond()] = 0;
        newBoard[move.to.getFirst()][move.to.getSecond()] = 1;

        return new Solitaire(newBoard, remainingPegs - 1);
    }

    public boolean hasWon() {
        return this.remainingPegs == 1 && board[3][3] == 1;
    }

    private List<Move> getPossibleMoves() {
        List<Move> moves = new ArrayList<>();

        for (int rowIdx = 0; rowIdx < board.length; rowIdx++) {
            for (int cellIdx = 0; cellIdx < board[rowIdx].length; cellIdx++) {
                int cell = board[rowIdx][cellIdx];
                if (cell != 1) continue;

                Pair<Integer, Integer> from = new Pair<>(rowIdx, cellIdx);

                if (cellIdx <= 4 && board[rowIdx][cellIdx + 1] == 1 && board[rowIdx][cellIdx + 2] == 0)
                    moves.add(new Move(from, new Pair<>(rowIdx, cellIdx + 2)));

                if (cellIdx >= 2 && board[rowIdx][cellIdx - 1] == 1 && board[rowIdx][cellIdx - 2] == 0)
                    moves.add(new Move(from, new Pair<>(rowIdx, cellIdx - 2)));

                if (rowIdx <= 4 && board[rowIdx + 1][cellIdx] == 1 && board[rowIdx + 2][cellIdx] == 0)
                    moves.add(new Move(from, new Pair<>(rowIdx + 2, cellIdx)));

                if (rowIdx >= 2 && board[rowIdx - 1][cellIdx] == 1 && board[rowIdx - 2][cellIdx] == 0)
                    moves.add(new Move(from, new Pair<>(rowIdx - 2, cellIdx)));
            }
        }

        return moves;
    }

    public Pair<Move, Boolean> dfs() {
        if (hasWon()) return new Pair<>(null, true);

        int hash = Arrays.deepHashCode(board);
        if (memory.containsKey(hash))
            return memory.get(hash);

        List<Move> moves = getPossibleMoves();
        if (moves.isEmpty()) return new Pair<>(null, false);

        for (Move move : moves) {
            Solitaire newGame = move(move);
            boolean hasWon = newGame.dfs().getSecond();

            if (hasWon) {
                memory.put(hash, new Pair<>(move, true));
                return memory.get(hash);
            }
        }

        return new Pair<>(moves.get(0), false);
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder(" ");
        for (int i = 0; i < 15; i++) {
            str.append("-");
        }
        str.append("\n");

        for (int[] row : board) {
            str.append("| ");
            for (int item : row) {
                str.append(item == 0 ? "o " : (item == 1 ? "X " : "  "));
            }
            str.append("|\n");
        }

        str.append(" ");
        for (int i = 0; i < 15; i++) {
            str.append("-");
        }

        return str.toString();
    }
}

class Pair<A, B> {
    private final A first;
    private final B second;

    public Pair(A first, B second) {
        this.first = first;
        this.second = second;
    }

    public A getFirst() {
        return first;
    }

    public B getSecond() {
        return second;
    }
}

public class Main {
    public static void main(String[] args) {
        simulate();
    }

    private static void simulate() {
        Solitaire game = new Solitaire();
    
        System.out.println("Starting game...");
        System.out.println(game);
    
        int counter = 0;
        Scanner scanner = new Scanner(System.in);
    
        while (!game.hasWon()) {
            counter++;
    
            System.out.print("Press Enter to continue...");
            
            // Check if there is more input
            if (scanner.hasNextLine()) {
                // Read the line to consume the Enter key press
                scanner.nextLine();
    
                Pair<Move, Boolean> move;
                long ms;
    
                {
                    long startTime = System.currentTimeMillis();
                    move = game.dfs();
                    ms = System.currentTimeMillis() - startTime;
                }
    
                game = game.move(move.getFirst());
                System.out.println("Playing move #" + counter + " " + move.getFirst() + " in " + ms + " ms\n");
    
                System.out.println(game);
            } else {
                System.out.println("No more input. Exiting the game.");
                break;
            }
        }
    
        System.out.println("Game finished in " + counter + " moves!");
        scanner.close();
    }
}
