import java.awt.image.BufferedImage;

// パワーアップの種類を定義する列挙型
public enum PowerUpTypes {
    SHIELD("SHIELD", Assets.shield), // 防御シールド
    LIFE("+1 LIFE", Assets.specialFlag), // 1UP
    SCORE_X2("SCORE x2", Assets.luckyFrag), // スコア2倍
    RAPID_FIRE("RAPID FIRE", Assets.rapidFire), // 連射
    SCORE_STACK("+1000 POINTS", Assets.pacman), // 高得点
    DOUBLE_GUN("DOUBLE GUN", Assets.doubleGun); // ダブルガン

    public String text;
    public BufferedImage texture;

    // コンストラクタ
    private PowerUpTypes(String text, BufferedImage texture) {
        this.text = text;
        this.texture = texture;
    }
}