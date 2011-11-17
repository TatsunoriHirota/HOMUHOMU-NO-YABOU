import gameCanvasUtil.*;

import java.awt.*;
import java.awt.event.*;

import javax.swing.JFrame;

/**
 * アプリケーションのメイン部分(全てはここから始まる)
 *
 * スレッド、Event処理とかを一手に引き受けるクラス といいつつ、やることは基本他にたらいまわし。
 *
 * あと、ウインドウそのもののクラス
 */

public class GameApp extends JFrame implements WindowListener, Runnable
{
    // / serialVersionUID
    private static final long serialVersionUID = 1L;

    // / 時間管理を行うオブジェクト
    private FPSManager timer = null;

    // / 画面そのもののオブジェクト
    private AppCanvas canvas = null;

    // / スレッド
    private Thread th = null;

    // falseになったらスレッドループを抜ける
    private boolean thFlag = true;

    /**
     * ゲームモード
     *
     * @param args 引数
     */
    public static void main(String[] args)
    {
        new GameApp();
    }

    /**
     * コンストラクタ ウインドウの大きさ設定などを行ってます。
     */
    private GameApp()
    {
        super("FrameTest");
        timer = FPSManager.getInstance();
        timer.init(GameCanvas.CONFIG_FPS);
        GameCanvas.getInstance().init(this, new Game());

        // リサイズ不可に
        setResizable(false);
        // 閉じるボタンを押した際の処理
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // リスナーに自身を登録
        addWindowListener(this);

        // コンポーネント追加
        canvas = new AppCanvas();
        add(this.canvas, BorderLayout.CENTER);

        /* pack -> setPreferredSize の順に実行するとサイズ設定がうまくいく */
        // コンポーネントのサイズに合わせてサイズを変更する
        pack();

        // コンポーネント追加後に、内側のサイズ（ウインドウ枠をのぞいたサイズ）からウインドウサイズを指定
        setPreferredSize(new Dimension(GameCanvas.WIDTH, GameCanvas.HEIGHT));

        // ウインドウを表示
        setVisible(true);

        th = new Thread(this);
        th.start();
    }

    /**
     * Windowが出現したとき呼び出される
     *
     * @param evt ウインドウのイベント
     */
    public void windowOpened(WindowEvent evt)
    {
    }

    /**
     * Windowの×ボタンが押されたとき呼び出される
     *
     * @param evt ウインドウのイベント
     */
    public void windowClosing(WindowEvent evt)
    {
        GameCanvas.getInstance().finalize();
        thFlag = false;
    }

    /**
     * Windowが閉じるとき呼び出される
     *
     * @param evt ウインドウのイベント
     */
    public void windowClosed(WindowEvent evt)
    {
    }

    /**
     * Windowが最小化されたときに呼び出される
     *
     * @param evt ウインドウのイベント
     */
    public void windowIconified(WindowEvent evt)
    {
    }

    /**
     * Windowが最小化された状態から戻ったときに呼び出される
     *
     * @param evt ウインドウのイベント
     */
    public void windowDeiconified(WindowEvent evt)
    {
    }

    /**
     * Windowがアクティブになったとき呼び出される。
     *
     * @param evt ウインドウのイベント
     */
    public void windowActivated(WindowEvent evt)
    {
    }

    /**
     * Windowがアクティブじゃなくなったときに呼び出される。
     *
     * @param evt ウインドウのイベント
     */
    public void windowDeactivated(WindowEvent evt)
    {
    }

    /**
     * メインのスレッド
     */
    public void run()
    {
        while (thFlag)
        {
            this.canvas.updateMessage();
            this.canvas.drawMessage();
            timer.Wait();
        }
    }

}
