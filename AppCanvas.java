import gameCanvasUtil.Resource.InputManager;
import gameCanvasUtil.GameCanvas;

import java.awt.*;
import java.awt.event.*;

/**
 * Canvas(描画を管理するクラス)
 *
 * 内部にスクリーンバッファを持っていて、ダブルバッファリングとかをするクラス。
 * あと、更新処理をgameCanvasにたらいまわしする
 */
public class AppCanvas extends Canvas implements MouseMotionListener,
        MouseListener, KeyListener
{

    // serialVersionUID
    private static final long serialVersionUID = 1L;
    // オフスクリーン用のImageオブジェクト
    private Image offImage = null;
    // オフスクリーンバッファ
    private Graphics offScreen  = null;
    // キー入力とかのクラス
    private InputManager inputManager = null;
    // GameCanvas
    private GameCanvas gameCanvas = null;

    /**
     * コンストラクタ イベントハンドラの登録とか・・・
     */
    public AppCanvas()
    {
        super();

        inputManager = InputManager.getInstance();
        gameCanvas = GameCanvas.getInstance();

        addKeyListener(this);
        addMouseListener(this);
        addMouseMotionListener(this);
        this.setSize(GameCanvas.WIDTH, GameCanvas.HEIGHT);
    }

    /**
     * 更新処理をすべき時に呼び出されます
     */
    public void updateMessage()
    {
        gameCanvas.updateMessage();
        inputManager.updateKeyData();
    }

    /**
     * 描画処理をすべき時に呼び出されます。
     */
    public void drawMessage()
    {
        gameCanvas.drawMessage();
        repaint();
    }

    /**
     * repaint呼び出したら　これが呼び出されるはずです・・・
     *
     * @param g 描画対象
     */
    public void update(Graphics g)
    {
        this.paint(g);
    }

    /**
     * 描画が必要になったら呼び出されます
     *
     * @param g 描画対象
     */
    public void paint(Graphics g)
    {
        if (offImage == null)
        {
            offImage = createImage(GameCanvas.WIDTH, GameCanvas.HEIGHT);
            offScreen = offImage.getGraphics();
            gameCanvas.setGraphics(offScreen, offImage);
            this.requestFocusInWindow();
        }
        g.drawImage(offImage, 0, 0, null);
    }

    /**
     * キーが押されたときに呼び出されます
     *
     * @param e キーイベント情報
     */
    public synchronized void keyPressed(KeyEvent e)
    {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_ESCAPE)
        { // ESCでリセット
            if (gameCanvas.showYesNoDialog("ゲームをリセットしますか？"))
            {
                gameCanvas.resetGame();
                gameCanvas.resetGameInstance(new Game());
            }
        }

        inputManager.keyPressed(e);
    }

    /**
     * キーが離されたときに呼び出されます
     *
     * @param e キーイベント情報
     */
    public synchronized void keyReleased(KeyEvent e)
    {
        inputManager.keyReleased(e);
    }

    /**
     * キーがタイプされたときに呼び出されます
     *
     * @param e キーイベント情報
     */
    public void keyTyped(KeyEvent e)
    {
    }

    /**
     * マウスが画面に入ったときに呼び出される？
     *
     * @param e マウスイベント
     */
    public void mouseEntered(MouseEvent e)
    {
        this.inputManager.mouseEntered(e);
    }

    /**
     * マウスが画面から出たときに呼び出される？
     *
     * @param e マウスイベント
     */
    public void mouseExited(MouseEvent e)
    {
        this.inputManager.mouseExited(e);
    }

    /**
     * マウスがクリックときに呼び出される？
     *
     * @param e マウスイベント
     */
    public void mouseClicked(MouseEvent e)
    {
        this.inputManager.mouseClicked(e);
    }

    /**
     * マウスのボタンが押されたとき呼び出される
     *
     * @param e マウスイベント
     */
    public void mousePressed(MouseEvent e)
    {
        this.inputManager.mousePressed(e);
    }

    /**
     * マウスのボタンが離されたとき呼び出される
     *
     * @param e マウスイベント
     */
    public void mouseReleased(MouseEvent e)
    {
        this.inputManager.mouseReleased(e);
    }

    /**
     * マウスドラッグされたとき呼び出される
     *
     * @param e マウスイベント
     */
    public void mouseDragged(MouseEvent e)
    {
        this.inputManager.mouseDragged(e);
    }

    /**
     * マウスが動いたとき呼び出される
     *
     * @param e マウスイベント
     */
    public void mouseMoved(MouseEvent e)
    {
        this.inputManager.mouseMoved(e);
    }

}
