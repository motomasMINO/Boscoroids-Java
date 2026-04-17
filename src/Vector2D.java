// 2Dベクトル演算用のクラス
public class Vector2D {
    private double x, y;

    public Vector2D(double x, double y) { // コンストラクタ
        this.x = x;
        this.y = y;
    }

    public Vector2D(Vector2D v) { // コピーコンストラクタ
        this.x = v.x;
        this.y = v.y;
    }

    public Vector2D() { // デフォルトコンストラクタ
        x = 0;
        y = 0;
    }

    public Vector2D add(Vector2D v) { // ベクトルの加算
        return new Vector2D(x + v.getX(), y + v.getY());
    }

    public Vector2D subtract(Vector2D v) {
        return new Vector2D(x - v.getX(), y - v.getY());
    }

    public Vector2D scale(double value) { // ベクトルのスケーリング
        return new Vector2D(x*value, y*value);
    }

    public Vector2D limit(double value) { // ベクトルの大きさを制限する
        if(getMagnitude() > value) {
          return this.normalize().scale(value);
        }
        return this;        
    }

    public Vector2D normalize() { // ベクトルの正規化
        double magnitude = getMagnitude();
        return new Vector2D(x / magnitude, y / magnitude);
    }

    public double getMagnitude() { // ベクトルの大きさを計算
        return Math.sqrt(x*x + y*y);
    }

    public Vector2D setDirection(double angle) { // ベクトルの方向を設定する
        double magnitude = getMagnitude();
        return new Vector2D(Math.cos(angle) * magnitude, Math.sin(angle) * magnitude);
    }

    public double getAngle() { // ベクトルの角度を計算
        return Math.asin(y / getMagnitude());
    }

    public double getX() { // x成分を取得
        return x;
    }

    public void setX(double x) { // x成分を設定
        this.x = x;
    }

    public double getY() { // y成分を取得
        return y;
    }

    public void setY(double y) { // y成分を設定
        this.y = y;
    }

}
