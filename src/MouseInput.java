import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

// マウス入力を処理するクラス
public class MouseInput extends MouseAdapter {

    public static int X, Y;
    public static boolean MLB; // 左クリックが押されているかどうかを示すフラグ

    @Override
    public void mousePressed(MouseEvent e) {
        if(e.getButton() == MouseEvent.BUTTON1) {
            MLB = true;
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if(e.getButton() == MouseEvent.BUTTON1) {
            MLB = false;
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        X = e.getX();
        Y = e.getY();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        X = e.getX();
        Y = e.getY();
    }
    
}
