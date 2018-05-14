import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Map;

/**
 * Collect all the Scoreboard Client classes in this file. Note that this file
 * MUST BE called ScoreboardClient.java
 */
public class ScoreboardClient implements Runnable {
    private String gameChoice;
    private BufferedReader input;
    private PrintWriter output;
    private ScoreboardClient master;
    //Interact with the questions from the challenge Response
    private ArrayList<ChallengeResponseGame> games;
    private ChallengeResponseGame currentGame = null;
    private String quid;

    ScoreboardClient(BufferedReader input, PrintWriter output, ArrayList<ChallengeResponseGame> games) {
        this.input = input;
        this.output = output;
        this.games = games;

    }

    void registerCallback(ScoreboardClient c) {
        this.master = c;
    }

    public void run() {
        //Creates a server socket, bound to the specified port.
        //should connect to the localhost on port 4001
        this.master.output.println("User Name Selected");
        //Username Declaration
        String userName = null;
        try {
            userName = this.master.input.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //Add to array of people
        this.master.output.println("User Name Selected");
        boolean done = false;
        while (!done) {
            String userInput = null;
            try {
                userInput = this.master.input.readLine().toLowerCase();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (userInput != null) {
                switch (userInput) {
                    //Or this may have to be done with an IF Else statements which might
                    //be a cleaner solution
                    //I moved most of this code to Client since it is interactive while
                    //Server is not
                    case "help":
                        this.master.output.println("Commands");
                        this.master.output.println("Expected Inputs: Help, Game, Scoreboard, questions,quit");
                        break;
                    case "game":

                        //Choosing the which game to play
                        try {
                            gameChoice = this.master.input.readLine().toLowerCase();
                            if (!gameChoice.equals("crypto") && !gameChoice.equals("networking")) {
                                this.master.output.println("Unexpected Input Try Again!");
                                break;
                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        //the currentGame is created with the games array at that array

                        for (ChallengeResponseGame game : this.games) {
                            if (game.gameId.equals(gameChoice)) {
                                currentGame = game;
                                currentGame.addPlayer(userName);
                            }
                        }
                        //The available Questions
                        //Choosing the which question to play
                        int qChoice = 2;
                        try {
                            qChoice = Integer.parseInt(this.master.input.readLine());
                            if (qChoice != 0 && qChoice != 1) {
                                this.master.output.println("Unexpected Input Try Again!");
                                break;
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        //The question is choosen
                        String question;
                        if (currentGame != null) {
                            question = currentGame.getQuestions().get(qChoice).getQuestion();
                            quid = currentGame.getQuestions().get(qChoice).getId();
                            this.master.output.println(question);
                        }
                        //UserAnswer for the question
                        String userAnswer = null;
                        try {
                            userAnswer = this.master.input.readLine();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        //checking the answer for the question of the current game by this.userName
                        if (currentGame != null) {
                            if (userAnswer != null) {
                                currentGame.answer(userName, quid, userAnswer.toUpperCase());
                            }
                        }
                        break;
                    case "scoreboard":
                        Map<String, Integer> board;
                        for (ChallengeResponseGame game : this.games) {
                            this.master.output.println("retrieving scores for:" + game.gameId);
                            board = game.getScores();
                            if (board != null) {
                                for (Map.Entry<String, Integer> entry : board.entrySet()) {
                                    this.master.output.println("Username: " + entry.getKey() + " Score: " + entry.getValue());
                                }
                            }
                        }

                        break;

                    case "questions":
                        for (ChallengeResponseGame game : this.games) {
                            for (int x = 0; x < 2; x++) {
                                this.master.output.println("From: " + game.getId());
                                this.master.output.println("Input needed: " + x + " Question: " + game.getQuestions().get(x).getQuestion());
                            }
                        }
                        break;
                    case "quit":
                        done = true;


                        break;
                    default:
                        this.master.output.println("Unexpected Input Try Again!");
                }
            }
        }
        try {
            this.master.input.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.master.output.close();
    }
}
