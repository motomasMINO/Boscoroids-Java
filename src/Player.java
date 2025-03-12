import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public class Player extends MovingObject {

    private Vector2D heading;
    private Vector2D acceleration;

    private boolean accelerating = false;
    private long fireRate;

    private boolean spawning, visible;

    private long spawnTime, flickerTime, shieldTime, doubleScoreTime, rapidFireTime, doubleGunTime;

    private Sound shoot, lose, ACC;

    private boolean shieldOn, doubleScoreOn, rapidFireOn, doubleGunOn;

    private Animation shieldEffect;

    private long fireSpeed;

    public Player(Vector2D position, Vector2D velocity, double maxVel, BufferedImage texture, GameState gameState) {
        super(position, velocity, maxVel, texture, gameState);
        heading = new Vector2D(0, 1);
        acceleration = new Vector2D();
        fireRate = 0;
        spawnTime = 0;
        flickerTime = 0;
        shieldTime = 0;
        rapidFireTime = 0;
        doubleGunTime = 0;
        shoot = new Sound(Assets.playerShoot);
        lose = new Sound(Assets.playerLose);
        ACC = new Sound(Assets.accelerationMusic);

        shieldEffect = new Animation(Assets.shieldEffect, 80, null);

        visible = true;
    }

    @Override
    public void update(float dt) {
        fireRate += dt;

        if(shieldOn)
          shieldTime += dt;

        if(doubleScoreOn)
          doubleScoreTime += dt;
          
        if(rapidFireOn) {
          fireSpeed = Constants.FIRERATE / 2;
          rapidFireTime += dt;
        }else {
          fireSpeed = Constants.FIRERATE;
        }
        
        if(doubleGunOn)
          doubleGunTime += dt;

        if(shieldTime > Constants.SHIELD_TIME) {
          shieldTime = 0;
          shieldOn = false;
        }
        
        if(doubleScoreTime > Constants.DOUBLE_SCORE_TIME) {
          doubleScoreOn = false;
          doubleScoreTime = 0;
        }

        if(rapidFireTime > Constants.RAPID_FIRE_TIME) {
          rapidFireOn = false;
          rapidFireTime = 0;
        }

        if(doubleGunTime > Constants.DOUBLE_GUN_TIME) {
          doubleGunOn = false;
          doubleGunTime = 0;
        }

        if(spawning) {
          flickerTime += dt;
          spawnTime += dt;

          if(flickerTime > Constants.FLICKER_TIME) {
            visible = !visible;
            flickerTime = 0;
          }

          if(spawnTime > Constants.SPAWNING_TIME) {
            spawning = false;
            visible = true;
          }
        }

        // レーザーの発射間隔
        if(KeyBoard.SHOOT && fireRate > fireSpeed && !spawning) {
          if(doubleGunOn) {
            Vector2D leftGun = getCenter();
            Vector2D rightGun = getCenter();

            Vector2D temp = new Vector2D(heading);
            temp.normalize();
            temp = temp.setDirection(angle - 1.3f);
            temp = temp.scale(width);
            rightGun = rightGun.add(temp);

            temp = temp.setDirection(angle - 1.9f);
            leftGun = leftGun.add(temp);

            Laser l = new Laser(leftGun, heading, Constants.LASER_VEL, angle, Assets.blueLaser, gameState);
            Laser r = new Laser(rightGun, heading, Constants.LASER_VEL, angle, Assets.blueLaser, gameState);

            gameState.getMovingObjects().add(0, l);
            gameState.getMovingObjects().add(0, r);
          }else {
            gameState.getMovingObjects().add(0, new Laser(getCenter().add(heading.scale(width)), heading, Constants.LASER_VEL, angle, Assets.blueLaser, gameState));
          }

          fireRate = 0;
          shoot.play();
        }

        if(shoot.getFramePosition() > 8500) {
          shoot.stop();
        }
        
        // 自機の移動
        if(KeyBoard.RIGHT)
          angle += Constants.DELTAANGLE;
        if(KeyBoard.LEFT)
          angle -= Constants.DELTAANGLE;

        if(KeyBoard.UP) {
          acceleration = heading.scale(Constants.ACC);
          accelerating = true;
          ACC.play();
        }else {
          if(velocity.getMagnitude() != 0)
            acceleration = (velocity.scale(-1).normalize()).scale(Constants.ACC / 2);
          accelerating = false;
          ACC.stop();
        }
        
        velocity = velocity.add(acceleration);

        velocity = velocity.limit(maxVel);

        heading = heading.setDirection(angle - Math.PI / 2);

        position = position.add(velocity);

        if(position.getX() > Constants.WIDTH)
          position.setX(0);
        if(position.getY() > Constants.HEIGHT)
          position.setY(0);

        if(position.getX() < 0)
          position.setX(Constants.WIDTH);
        if(position.getY() < 0)
          position.setY(Constants.HEIGHT);

        if(shieldOn)
          shieldEffect.update(dt);  

        collidesWith();
    }

    public void setShield() {
        if(shieldOn)
          shieldTime = 0;
        shieldOn = true;  
    }

    public void setDoubleScore() {
        if(doubleScoreOn)
          doubleScoreTime = 0;
        doubleScoreOn = true;  
    }

    public void setRapidFire() {
        if(rapidFireOn)
          rapidFireTime = 0;
        rapidFireOn = true;
    }

    public void setDoubleGun() {
        if(doubleGunOn)
          doubleGunTime = 0;
        doubleGunOn = true;  
    }

    @Override
    public void Destroy() {
        spawning = true;
        gameState.playExplosion(position);
        spawnTime = 0;
        lose.play();
        ACC.stop();
        if(!gameState.subtractLife(position)) {
          gameState.gameOver();
          super.Destroy();
        }
        resetValues();
    }

    private void resetValues() {
        angle = 0;
        velocity = new Vector2D();
        position = GameState.PLAYER_START_POSITION;
    }

    @Override
    public void draw(Graphics g) {
        if(!visible)
          return;

        Graphics2D g2d = (Graphics2D)g;

        AffineTransform at1 = AffineTransform.getTranslateInstance(position.getX() + width/2 + 25, position.getY() + height/2 + 10);

        AffineTransform at2 = AffineTransform.getTranslateInstance(position.getX() + 5, position.getY() + height/2 + 10); 

        at1.rotate(angle, -25, -10);
        at2.rotate(angle, width/2 -5, -10);

        if(accelerating) {
          g2d.drawImage(Assets.speed, at1, null);
          g2d.drawImage(Assets.speed, at2, null);
        }

        if(shieldOn) {
          BufferedImage currentFrame = shieldEffect.getCurrentFrame();
          AffineTransform at3 = AffineTransform.getTranslateInstance(position.getX() - currentFrame.getWidth()/2 + width/2, 
                                                                     position.getY() - currentFrame.getHeight()/2 + height/2);

          at3.rotate(angle, currentFrame.getWidth()/2, currentFrame.getHeight()/2);
          
          g2d.drawImage(shieldEffect.getCurrentFrame(), at3, null);
        }

        at = AffineTransform.getTranslateInstance(position.getX(), position.getY());

        at.rotate(angle, width / 2, height / 2);

        if(doubleGunOn)
          g2d.drawImage(Assets.doubleGunPlayer, at, null);
        else
          g2d.drawImage(texture, at, null);  
    }

    public boolean isSpawning() {
        return spawning;
    }

    public boolean isShieldOn() {
        return shieldOn;
    }

    public boolean isDoubleScoreOn() {
        return doubleScoreOn;
    }
}