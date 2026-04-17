import java.awt.Graphics;

// ゲームの状態を管理する抽象クラス
public abstract class State {

    private static State currentState = null; // 現在の状態を保持する静的変数

    // 現在の状態を取得するための静的メソッド
    public static State getCurrentState() {
        return currentState;
    }

    // 状態を変更するための静的メソッド
    public static void changeState(State newState) {
        currentState = newState;
    }
    
    public abstract void update(float dt);
    public abstract void draw(Graphics g);
}
