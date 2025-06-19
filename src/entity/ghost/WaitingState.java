package entity.ghost;


public class WaitingState implements BlinkyState {

    private static final int SCORE_TO_TRIGGER_CHASE = 100;

    @Override
    public void update(Blinky blinky) {
        // אם הניקוד הושג, שנה מצב והתחל לצאת
        if (blinky.gp.scoreM.getScore() >= SCORE_TO_TRIGGER_CHASE) {
            blinky.setState(new ExitingHouseState());
            return;
        }

        // לוגיקה לתנועה אקראית בתוך בית הרוחות
        blinky.determineRandomDirectionInHouse();
        blinky.executeMovement(Blinky.MovementRule.IN_HOUSE);
    }
}