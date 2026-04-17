import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;

// メニュー画面の状態を表すクラス
public class MenuState extends State {

    private ArrayList<Button> buttons;

    private Font font;

    public MenuState() {
        buttons = new ArrayList<Button>();

        font = Loader.loadFont("/resources/fonts/arcadeFont.ttf", 38);

        // PLAYボタン配置
        buttons.add(new Button(Assets.greyBtn,
                               Assets.blueBtn, 
                               Constants.WIDTH/2 - Assets.greyBtn.getWidth()/2, 
                               Constants.HEIGHT/2 - Assets.greyBtn.getHeight() * 2, 
                               Constants.PLAY, 
                               new Action() {
                                       @Override
                                       public void doAction() {
                                           State.changeState(new GameState());
                                       }
                               }));
        
        // QUITボタン配置                     
        buttons.add(new Button(Assets.greyBtn, 
                               Assets.blueBtn, 
                               Constants.WIDTH/2 - Assets.greyBtn.getWidth()/2, 
                               Constants.HEIGHT/2 + Assets.greyBtn.getHeight() * 2, 
                               Constants.QUIT, 
                               new Action() {
                                       @Override
                                       public void doAction() {
                                           System.exit(0);
                                       }
                               }));
        
        // HIGH SCORESボタン配置                       
        buttons.add(new Button(Assets.greyBtn,
                               Assets.blueBtn, 
                               Constants.WIDTH/2 - Assets.greyBtn.getWidth()/2, 
                               Constants.HEIGHT/2, 
                               Constants.HIGH_SCORES, 
                               new Action() {
                                       @Override
                                       public void doAction() {
                                           State.changeState(new ScoreState());
                                       }
                               }));                          
    }

    @Override
    public void update(float dt) {
        for(Button b: buttons) {
            b.update();
        }
    }

    @Override
    public void draw(Graphics g) {
        for(Button b: buttons) {
            b.draw(g);
        }

        Graphics2D g2d = (Graphics2D)g;

        // タイトルの描画
        Text.drawText(g2d, "BOSCOROIDS", new Vector2D(Constants.WIDTH/2, Constants.HEIGHT/2 - 100), 
        true, Color.RED, font);
    }
    
}
