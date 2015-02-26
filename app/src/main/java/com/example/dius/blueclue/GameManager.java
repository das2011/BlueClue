package com.example.dius.blueclue;

public class GameManager {

    private static GameManager instance;

    private GameManager() {
    }

    public static GameManager getInstance() {
        if (null == instance) {
            instance = new GameManager();
        }
        return instance;
    }

    private enum GameMode {TWITCH, CHALLENGE}

    private enum GamePhase {
        INPUT_QUESTION, SEND_QUESTION, WAIT_FOR_ANSWER, /*RECEIVE_ANSWER,*/
        WAIT_FOR_QUESTION, /*RECEIVE_QUESTION,*/ INPUT_ANSWER, SEND_ANSWER
    }

    private GameMode mode;
    private GamePhase currentPhase;
    private Question question;
    private Answer answer;
    private Score score;

    private void resetRound() {
        question = null;
        answer = null;
    }

    private void resetGame() {
        mode = GameMode.TWITCH;
        score = new Score();
        resetRound();
    }

    private boolean isCorrectAnswer() {
        return calculateResult() == answer.getValue();
    }

    private Integer calculateResult() {
        switch (question.getOperator()) {
            case PLUS:
                return question.getOperand1() + question.getOperand2();
            case TIMES:
                return question.getOperand1() * question.getOperand2();
        }
        return null;
    }

    public void startGameAsQuestioner() {
        resetGame();
        currentPhase = GamePhase.INPUT_QUESTION;
    }

    public void startGameAsAnswerer() {
        resetGame();
        currentPhase = GamePhase.WAIT_FOR_QUESTION;
    }


    public void sendQuestion(Question question) {
        this.question = question;
        currentPhase = GamePhase.SEND_QUESTION;

        // talk to comm service
    }

    public void questionSent() {
        currentPhase = GamePhase.WAIT_FOR_ANSWER;
    }

    public void answerReceived(Answer answer) {
        this.answer = answer;
        if (isCorrectAnswer()) {
            score.increaseTheirScoreBy(1);
        }

        if (isGameFinished()) {
            // we lost
            // transition to scoreboard activity
        }

        resetRound();
        currentPhase = GamePhase.WAIT_FOR_QUESTION;
    }

    public void questionReceived(Question question) {
        this.question = question;
        currentPhase = GamePhase.INPUT_ANSWER;
    }

    public void sendAnswer(Answer answer) {

        this.answer = answer;
        currentPhase = GamePhase.SEND_ANSWER;

        //talk to comm service.
    }

    public void answerSent() {
        if (isCorrectAnswer()) {
            score.increaseMyScoreBy(1);
        }

        if (isGameFinished()) {
            // we won
            // transition to scoreboard activity
        }

        resetRound();
        currentPhase = GamePhase.INPUT_QUESTION;

    }

    private boolean isGameFinished() {
        return score.getMyScore() > 10 || score.getTheirScore() > 10;
    }

    public int getMyScore() {
        return score.getMyScore();
    }

    public int getTheirScore() {
        return score.getTheirScore();
    }

}
